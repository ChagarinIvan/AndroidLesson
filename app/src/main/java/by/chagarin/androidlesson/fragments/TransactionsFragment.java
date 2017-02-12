package by.chagarin.androidlesson.fragments;

import android.app.Fragment;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.melnykov.fab.FloatingActionButton;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.OptionsMenuItem;
import org.androidannotations.annotations.ViewById;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import by.chagarin.androidlesson.AddTransactionActivity_;
import by.chagarin.androidlesson.DataLoader;
import by.chagarin.androidlesson.KindOfCategories;
import by.chagarin.androidlesson.R;
import by.chagarin.androidlesson.objects.Base;
import by.chagarin.androidlesson.objects.BaseListeners;
import by.chagarin.androidlesson.objects.Category;
import by.chagarin.androidlesson.objects.Transaction;
import by.chagarin.androidlesson.viewholders.TransactionViewHolder;

import static by.chagarin.androidlesson.DataLoader.TRANSACTIONS;
import static by.chagarin.androidlesson.KindOfCategories.getStringArray;

@EFragment(R.layout.fragment_transactions)
@OptionsMenu(R.menu.menu_transactions)
//создаём файл меню для фрагмента
//android:showAsAction="ifRoom" значит, что элемент если помещается, то будет в тулбаре, иначе поместиться в "три точки"
public class TransactionsFragment extends Fragment implements BaseListeners, DatePickerDialog.OnDateSetListener {

    @OptionsMenuItem(R.id.cash)
    MenuItem cash;

    @OptionsMenuItem
    MenuItem menuSearch;

    @ViewById(R.id.transactions_list)
    RecyclerView mRecycler;

    @ViewById(R.id.fab)
    FloatingActionButton fab;

    @ViewById
    SwipeRefreshLayout swipeLayout;

    @Bean
    DataLoader loader;

    @Bean
    Base base;

    private FirebaseRecyclerAdapter<Transaction, TransactionViewHolder> mAdapter;
    private Transaction transaction;
    private List<Category> listCategoriesTransactions;
    private List<Category> listCategoriesPlaces;

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        final SearchView searchView = (SearchView) menuSearch.getActionView();
        //делаем хинт из хмл
        final SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        // [END create_database_reference]
        mRecycler.setHasFixedSize(true);

        // Set up Layout Manager, reverse layout
        LinearLayoutManager mManager = new LinearLayoutManager(getActivity());
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        mRecycler.setLayoutManager(mManager);
        // Set up FirebaseRecyclerAdapter with the Query
        final Query postsQuery = loader.getQuery(mDatabase, TRANSACTIONS);
        //адаптер fireBase
        mAdapter = new FirebaseRecyclerAdapter<Transaction, TransactionViewHolder>(Transaction.class, R.layout.list_item,
                TransactionViewHolder.class, postsQuery) {
            @Override
            protected void populateViewHolder(final TransactionViewHolder viewHolder, final Transaction model, final int position) {
                viewHolder.bindToTransaction(model);
                //слушатель долгого нажатия на карточку транзаеции
                viewHolder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        transaction = loader.getTransactions().get(position);
                        new MaterialDialog.Builder(getActivity())
                                .title(R.string.dialog_title)
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        //показываем лист одиночного выбора полей транзакции для изменения
                                        MaterialDialog newDialog = new MaterialDialog.Builder(getActivity())
                                                .title(R.string.change_transacton_title)
                                                .customView(R.layout.dialog_change_transaction, true)
                                                .positiveText(R.string.save)
                                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                                    @Override
                                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                        EditText title = (EditText) dialog.findViewById(R.id.title);
                                                        transaction.setTitle(title.getText().toString());
                                                        showToast(transaction.getTitle());
                                                    }
                                                }).build();
                                        EditText title = (EditText) newDialog.getCustomView().findViewById(R.id.title);
                                        title.setText(transaction.getTitle());
                                        EditText price = (EditText) newDialog.getCustomView().findViewById(R.id.price);
                                        price.setText(transaction.getPrice());
                                        EditText comment = (EditText) newDialog.getCustomView().findViewById(R.id.comment);
                                        comment.setText(transaction.getComment());
                                        TextView date = (TextView) newDialog.getCustomView().findViewById(R.id.date_text);
                                        date.setText(transaction.getDate());
                                        List<Category> data = loader.getCategores();
                                        //отделяем только необходимые категории
                                        listCategoriesTransactions = KindOfCategories.sortData(data, KindOfCategories.getTransaction());
                                        listCategoriesPlaces = KindOfCategories.sortData(data, KindOfCategories.getPlace());
                                        //создаём для каждого спинера свой адаптер и устанавливаем их
                                        ArrayAdapter<String> adapterTransactions = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, getStringArray(listCategoriesTransactions));
                                        ArrayAdapter<String> adapterPlaces = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, getStringArray(listCategoriesPlaces));
                                        Spinner spinnerTransaction = (Spinner) newDialog.getCustomView().findViewById(R.id.spinner_transaction);
                                        Spinner spinnerPlace = (Spinner) newDialog.getCustomView().findViewById(R.id.spinner_place);
                                        spinnerTransaction.setAdapter(adapterTransactions);
                                        spinnerPlace.setAdapter(adapterPlaces);
                                        newDialog.show();


//                                                    .title(R.string.change_transacton_title)
//                                                    .items(transaction.toArray())
//                                                    .itemsCallback(new MaterialDialog.ListCallback() {
//                                                        public MaterialDialog listDialog;
//
//                                                        public void rebuildAndShow(){
//                                                            MaterialDialog.Builder builder = listDialog.getBuilder();
//                                                            builder.items(transaction.toArray());
//                                                            builder.build().show();
//                                                        }
//
//                                                        @Override
//                                                        public void onSelection(final MaterialDialog dialog, View itemView, int position, CharSequence text) {
//                                                            listDialog = dialog;
//                                                            List<Category> categores = loader.getCategores();
//                                                            switch (position){
//                                                                case 0:
//                                                                    new MaterialDialog.Builder(getActivity())
//                                                                            .title(R.string.string_change)
//                                                                            .content(R.string.title)
//                                                                            .inputType(InputType.TYPE_CLASS_TEXT)
//                                                                            .inputRange(2, 100)
//                                                                            .positiveText(R.string.save)
//                                                                            .input(transaction.getTitle(), transaction.getTitle(), false, new MaterialDialog.InputCallback() {
//                                                                                @Override
//                                                                                public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
//                                                                                    transaction.setTitle(input.toString());
//                                                                                    rebuildAndShow();
//                                                                                }
//                                                                            })
//                                                                            .show();
//                                                                    break;
//                                                                case 1:
//                                                                    new MaterialDialog.Builder(getActivity())
//                                                                            .title(R.string.string_change)
//                                                                            .content(R.string.add_activity_sum_name)
//                                                                            .inputType(InputType.TYPE_NUMBER_FLAG_DECIMAL)
//                                                                            .positiveText(R.string.save)
//                                                                            .input(transaction.getPrice(), transaction.getPrice(), false, new MaterialDialog.InputCallback() {
//                                                                                @Override
//                                                                                public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
//                                                                                    transaction.setPrice(input.toString());
//                                                                                    rebuildAndShow();
//                                                                                }
//                                                                            })
//                                                                            .show();
//                                                                    break;
//                                                                case 2:
//                                                                    new MaterialDialog.Builder(getActivity())
//                                                                            .title(R.string.string_change)
//                                                                            .content(R.string.comment)
//                                                                            .inputType(InputType.TYPE_CLASS_TEXT)
//                                                                            .positiveText(R.string.save)
//                                                                            .input(transaction.getComment(), transaction.getComment(), false, new MaterialDialog.InputCallback() {
//                                                                                @Override
//                                                                                public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
//                                                                                    transaction.setComment(input.toString());
//                                                                                    rebuildAndShow();
//                                                                                }
//                                                                            })
//                                                                            .show();
//                                                                    break;
//                                                                case 3:
//                                                                    Calendar cal = Calendar.getInstance();
//                                                                    try {
//                                                                        cal.setTime(Transaction.df.parse(transaction.getDate()));
//                                                                    } catch (ParseException e) {
//                                                                        e.printStackTrace();
//                                                                    }
//                                                                    DatePickerDialog dpd = DatePickerDialog.newInstance(
//                                                                            TransactionsFragment.this,
//                                                                            cal.get(Calendar.YEAR),
//                                                                            cal.get(Calendar.MONTH),
//                                                                            cal.get(Calendar.DAY_OF_MONTH)
//                                                                    );
//                                                                    dpd.show(getFragmentManager(), "Datepickerdialog");
//                                                                    break;
//                                                                case 4:
//                                                                    String[] transactionsCategories = KindOfCategories.getArray(categores, KindOfCategories.getTransaction());
//                                                                    new MaterialDialog.Builder(getActivity())
//                                                                            .title(R.string.transaction_category)
//                                                                            .items(transactionsCategories)
//                                                                            .itemsCallbackSingleChoice(2, new MaterialDialog.ListCallbackSingleChoice() {
//                                                                                @Override
//                                                                                public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
//                                                                                    rebuildAndShow();
//                                                                                    return true;
//                                                                                }
//                                                                            })
//                                                                            .positiveText(R.string.save)
//                                                                            .show();
//                                                                case 5:
//                                                                    String[] placesCategories = KindOfCategories.getArray(categores, KindOfCategories.getTransaction());
//                                                                    new MaterialDialog.Builder(getActivity())
//                                                                            .title(R.string.transaction_category)
//                                                                            .items(placesCategories)
//                                                                            .itemsCallbackSingleChoice(1, new MaterialDialog.ListCallbackSingleChoice() {
//                                                                                @Override
//                                                                                public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
//                                                                                    rebuildAndShow();
//                                                                                    return true;
//                                                                                }
//                                                                            })
//                                                                            .positiveText(R.string.save)
//                                                                            .show();
//                                                            }
//                                                        }
//                                                    })
//                                                    .positiveText(R.string.save)
//                                                    .onPositive(new MaterialDialog.SingleButtonCallback() {
//                                                        @Override
//                                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//                                                            showToast("сохраняем");
//                                                        }
//                                                    })
                                    }
                                })
                                .positiveText(R.string.agree)
                                .onNegative(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        dialog.dismiss();
                                    }
                                })
                                .negativeText(R.string.disagree)
                                .show();
                        return true;
                    }
                });
                cash.setTitle(loader.getCashCount());

            }
        };
        //слушатель нажатий на остаток кэша
        cash.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                getFragmentManager().beginTransaction().replace(R.id.content_frame, CashStatisticsFragment_.builder().build()).commit();
                return true;
            }
        });
        //свайп слушатель
        final ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
                postsQuery.addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                int position = viewHolder.getAdapterPosition();
                                DatabaseReference ref = mAdapter.getRef(position);
                                ref.removeValue();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(mRecycler);
        mRecycler.setAdapter(mAdapter);
        fab.attachToRecyclerView(mRecycler);

    }

    private void showToast(String s) {
        Toast.makeText(getActivity(), s, Toast.LENGTH_LONG).show();
    }

    @AfterViews
    void ready() {
        base.addListener(this);
    }

    @Click
    void fabClicked() {
        List<Transaction> transactions = loader.getTransactions();
        Transaction lastTransaction;
        if (transactions.size() != 0) {
            lastTransaction = transactions.get(0);
        } else {
            lastTransaction = new Transaction(
                    "ВОЙНА и МИР",
                    "300.0",
                    Transaction.df.format(new Date()),
                    "коментарий к трате",
                    new Category(getString(R.string.hint_category_exemple), KindOfCategories.getPlace(), "", ""),
                    new Category(getString(R.string.hint_category_exemple), KindOfCategories.getProceed(), "", ""),
                    "",
                    "");
        }
        Intent intent = new Intent(getActivity(), AddTransactionActivity_.class);
        intent.putExtra(Transaction.class.getCanonicalName(), lastTransaction);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.from_midle, R.anim.in_midle);
    }

    @Override
    public void doEvent() {

    }

    @Override
    public void removeElement() {
        cash.setTitle(loader.getCashCount());
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, monthOfYear, dayOfMonth);
        String date = Transaction.df.format(calendar.getTime());
        transaction.setDate(date);
    }
}
