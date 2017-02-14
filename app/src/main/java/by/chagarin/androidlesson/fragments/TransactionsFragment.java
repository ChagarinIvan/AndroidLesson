package by.chagarin.androidlesson.fragments;

import android.app.Fragment;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.Query;
import com.melnykov.fab.FloatingActionButton;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.OptionsMenuItem;
import org.androidannotations.annotations.ViewById;

import java.util.Calendar;
import java.util.List;

import by.chagarin.androidlesson.AddTransactionActivity_;
import by.chagarin.androidlesson.DataLoader;
import by.chagarin.androidlesson.R;
import by.chagarin.androidlesson.objects.Base;
import by.chagarin.androidlesson.objects.Category;
import by.chagarin.androidlesson.objects.Transaction;
import by.chagarin.androidlesson.viewholders.TransactionViewHolder;

import static by.chagarin.androidlesson.DataLoader.TRANSACTIONS;

@EFragment(R.layout.fragment_transactions)
@OptionsMenu(R.menu.menu_transactions)
//создаём файл меню для фрагмента
//android:showAsAction="ifRoom" значит, что элемент если помещается, то будет в тулбаре, иначе поместиться в "три точки"
public class TransactionsFragment extends Fragment implements DatePickerDialog.OnDateSetListener {

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
        // [END create_database_reference]
        mRecycler.setHasFixedSize(true);

        // Set up Layout Manager, reverse layout
        LinearLayoutManager mManager = new LinearLayoutManager(getActivity());
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        mRecycler.setLayoutManager(mManager);
        // Set up FirebaseRecyclerAdapter with the Query
        final Query postsQuery = loader.getQuery(TRANSACTIONS);
        //адаптер fireBase
        mAdapter = new FirebaseRecyclerAdapter<Transaction, TransactionViewHolder>(Transaction.class, R.layout.list_item,
                TransactionViewHolder.class, postsQuery) {
            @Override
            protected void populateViewHolder(final TransactionViewHolder viewHolder, final Transaction model, final int position) {
                viewHolder.bindToTransaction(model);
                //слушатель долгого нажатия на карточку транзаеции
//                viewHolder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
//                    @Override
//                    public boolean onLongClick(View v) {
//                        //transaction = loader.getTransactions().get(position);
//                        new MaterialDialog.Builder(getActivity())
//                                .title(R.string.dialog_title)
//                                .onPositive(new MaterialDialog.SingleButtonCallback() {
//                                    public Spinner spinnerPlace;
//                                    public Spinner spinnerTransaction;
//                                    public TextView dateText;
//
//                                    @Override
//                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//                                        //показываем лист одиночного выбора полей транзакции для изменения
//                                        MaterialDialog newDialog = new MaterialDialog.Builder(getActivity())
//                                                .title(R.string.change_transacton_title)
//                                                .customView(R.layout.dialog_change_transaction, true)
//                                                .positiveText(R.string.save)
//                                                .onPositive(new MaterialDialog.SingleButtonCallback() {
//                                                    @Override
//                                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//                                                        final EditText title = (EditText) dialog.findViewById(R.id.title);
//                                                        final EditText price = (EditText) dialog.findViewById(R.id.price);
//                                                        final EditText comment = (EditText) dialog.findViewById(R.id.comment);
//                                                        final TextView date = (TextView) dialog.findViewById(R.id.date_text);
//                                                        final Category categoryTransaction = listCategoriesTransactions.get(spinnerTransaction.getSelectedItemPosition());
//                                                        final Category categoryPlace = listCategoriesPlaces.get(spinnerPlace.getSelectedItemPosition());
//                                                        //записываем
//                                                        final String userId = loader.getUid();
//                                                        loader.mDatabase.child("users").child(userId).addListenerForSingleValueEvent(
//                                                                new ValueEventListener() {
//                                                                    @Override
//                                                                    public void onDataChange(DataSnapshot dataSnapshot) {
//                                                                        //удаляем элемент
//                                                                        int position = viewHolder.getAdapterPosition();
//                                                                        DatabaseReference ref = mAdapter.getRef(position);
//                                                                        ref.removeValue();
//                                                                        //добавляем новый жлемент
//                                                                        // Get user value
//                                                                        User user = dataSnapshot.getValue(User.class);
//
//                                                                        // [START_EXCLUDE]
//                                                                        if (user == null) {
//                                                                            // User is null, error out
//                                                                            Toast.makeText(getActivity(), "Error: could not fetch user.", Toast.LENGTH_SHORT).show();
//                                                                        } else {
//                                                                            // Write new post
////                                                                            loader.writeNewTransaction(new Transaction(title.getText().toString(),
////                                                                                    price.getText().toString(),
////                                                                                    date.getText().toString(),
////                                                                                    comment.getText().toString(),
////                                                                                    categoryTransaction,
////                                                                                    categoryPlace,
////                                                                                    userId,
////                                                                                    user.getEmail()));
//                                                                        }
//                                                                    }
//
//                                                                    @Override
//                                                                    public void onCancelled(DatabaseError databaseError) {
//                                                                    }
//                                                                });
//                                                    }
//                                                }).build();
//                                        //устанавливаем в поля значения из редактируемой транзакции
//                                        EditText title = (EditText) newDialog.getCustomView().findViewById(R.id.title);
//                                        //title.setText(transaction.getTitle());
//                                        EditText price = (EditText) newDialog.getCustomView().findViewById(R.id.price);
//                                        //price.setText(transaction.getPrice());
//                                        EditText comment = (EditText) newDialog.getCustomView().findViewById(R.id.comment);
//                                        //comment.setText(transaction.getComment());
//                                        dateText = (TextView) newDialog.getCustomView().findViewById(R.id.date_text);
//                                        //dateText.setText(transaction.getDate());
//                                        //слушатель вызова изменения даты
//                                        dateText.setOnClickListener(new View.OnClickListener() {
//                                            @Override
//                                            public void onClick(View v) {
//                                                Calendar now = Calendar.getInstance();
////                                                try {
////                                                    now.setTime(loader.df.parse(transaction.getDate()));
////                                                } catch (ParseException e) {
////
////                                                }
//                                                DatePickerDialog dpd = DatePickerDialog.newInstance(
//                                                        new DatePickerDialog.OnDateSetListener() {
//                                                            @Override
//                                                            public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
//                                                                Calendar calendar = Calendar.getInstance();
//                                                                calendar.set(year, monthOfYear, dayOfMonth);
//                                                                String date = loader.df.format(calendar.getTime());
//                                                                dateText.setText(date);
//                                                            }
//                                                        },
//                                                        now.get(Calendar.YEAR),
//                                                        now.get(Calendar.MONTH),
//                                                        now.get(Calendar.DAY_OF_MONTH)
//                                                );
//                                                dpd.show(getFragmentManager(), "Datepickerdialog");
//                                            }
//                                        });
//                                        //List<Category> data = loader.getCategores();
//                                        //отделяем только необходимые категории
//                                        //listCategoriesTransactions = KindOfCategories.sortData(data, KindOfCategories.getTransaction());
//                                        //listCategoriesPlaces = KindOfCategories.sortData(data, KindOfCategories.getPlace());
//                                        //создаём для каждого спинера свой адаптер и устанавливаем их
//                                        ArrayAdapter<String> adapterTransactions = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, getStringArray(listCategoriesTransactions));
//                                        ArrayAdapter<String> adapterPlaces = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, getStringArray(listCategoriesPlaces));
//                                        spinnerTransaction = (Spinner) newDialog.getCustomView().findViewById(R.id.spinner_transaction);
//                                        spinnerPlace = (Spinner) newDialog.getCustomView().findViewById(R.id.spinner_place);
//                                        spinnerTransaction.setAdapter(adapterTransactions);
//                                        //spinnerTransaction.setSelection(KindOfCategories.getPosition(listCategoriesTransactions, transaction.getCategoryTransaction()));
//                                        spinnerPlace.setAdapter(adapterPlaces);
//                                        //spinnerPlace.setSelection(KindOfCategories.getPosition(listCategoriesPlaces, transaction.getCategoryPlace()));
//                                        newDialog.show();
//                                    }
//                                })
//                                .positiveText(R.string.agree)
//                                .onNegative(new MaterialDialog.SingleButtonCallback() {
//                                    @Override
//                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//                                        dialog.dismiss();
//                                    }
//                                })
//                                .negativeText(R.string.disagree)
//                                .show();
//                        return true;
//                    }
//                });
            }
        };
        //слушатель нажатий на остаток кэша
        //cash.setTitle(loader.getCashCount());
//        cash.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                getFragmentManager().beginTransaction().replace(R.id.content_frame, CashStatisticsFragment_.builder().build()).commit();
//                return true;
//            }
//        });
        //свайп слушатель
//        final ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
//            @Override
//            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
//                return false;
//            }
//
//            @Override
//            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
//                postsQuery.addListenerForSingleValueEvent(
//                        new ValueEventListener() {
//                            @Override
//                            public void onDataChange(DataSnapshot dataSnapshot) {
//                                new MaterialDialog.Builder(getActivity())
//                                        .title(R.string.delet_dialog)
//                                        .positiveText(R.string.ok_button)
//                                        .negativeText(R.string.cancel_button)
//                                        .onPositive(new MaterialDialog.SingleButtonCallback() {
//                                            @Override
//                                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//                                                int position = viewHolder.getAdapterPosition();
//                                                DatabaseReference ref = mAdapter.getRef(position);
//                                                ref.removeValue();
//                                            }
//                                        })
//                                        .onNegative(new MaterialDialog.SingleButtonCallback() {
//                                            @Override
//                                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//                                                dialog.dismiss();
//                                            }
//                                        }).show();
//                            }
//
//                            @Override
//                            public void onCancelled(DatabaseError databaseError) {
//
//                            }
//                        });
//            }
//        };
//        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
//        itemTouchHelper.attachToRecyclerView(mRecycler);
        mRecycler.setAdapter(mAdapter);
        fab.attachToRecyclerView(mRecycler);

    }

    @Click
    void fabClicked() {
        Intent intent = new Intent(getActivity(), AddTransactionActivity_.class);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.from_midle, R.anim.in_midle);
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, monthOfYear, dayOfMonth);
        String date = loader.df.format(calendar.getTime());
        //transaction.setDate(date);
    }
}
