package by.chagarin.androidlesson.fragments;


import android.app.Fragment;
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

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import by.chagarin.androidlesson.AddProccedActivity_;
import by.chagarin.androidlesson.DataLoader;
import by.chagarin.androidlesson.KindOfCategories;
import by.chagarin.androidlesson.R;
import by.chagarin.androidlesson.objects.Base;
import by.chagarin.androidlesson.objects.BaseListeners;
import by.chagarin.androidlesson.objects.Category;
import by.chagarin.androidlesson.objects.Proceed;
import by.chagarin.androidlesson.objects.Transaction;
import by.chagarin.androidlesson.objects.User;
import by.chagarin.androidlesson.viewholders.ProceedViewHolder;

import static by.chagarin.androidlesson.DataLoader.PROCEEDS;
import static by.chagarin.androidlesson.KindOfCategories.getStringArray;

@EFragment(R.layout.fragment_proceeds)
@OptionsMenu(R.menu.menu_transactions)
public class ProceedFragment extends Fragment implements BaseListeners {
    private Proceed proceed;


    @OptionsMenuItem
    MenuItem menuSearch;

    @OptionsMenuItem
    MenuItem cash;

    @ViewById(R.id.proceeds_list)
    RecyclerView mRecycler;

    @ViewById(R.id.fab)
    FloatingActionButton fab;

    @Bean
    DataLoader loader;

    @ViewById
    SwipeRefreshLayout swipeLayout;

    @Bean
    Base base;
    private FirebaseRecyclerAdapter<Proceed, ProceedViewHolder> mAdapter;
    private List<Category> listCategoriesProceedes;
    private List<Category> listCategoriesPlaces;

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        // [END create_database_reference]
        mRecycler.setHasFixedSize(true);
        // Set up Layout Manager, reverse layout
        LinearLayoutManager mManager = new LinearLayoutManager(getActivity());
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        mRecycler.setLayoutManager(mManager);
        // Set up FirebaseRecyclerAdapter with the Query
        final Query postsQuery = loader.getQuery(mDatabase, PROCEEDS);
        //адаптер БД
        mAdapter = new FirebaseRecyclerAdapter<Proceed, ProceedViewHolder>(Proceed.class, R.layout.list_item,
                ProceedViewHolder.class, postsQuery) {
            @Override
            protected void populateViewHolder(final ProceedViewHolder viewHolder, final Proceed model, final int position) {
                viewHolder.bindToProceed(model);
                viewHolder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        proceed = loader.getProceedes().get(position);
                        new MaterialDialog.Builder(getActivity())
                                .title(R.string.dialog_title)
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    public Spinner spinnerPlace;
                                    public Spinner spinnerProceed;
                                    public TextView dateText;

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
                                                        final EditText title = (EditText) dialog.findViewById(R.id.title);
                                                        final EditText price = (EditText) dialog.findViewById(R.id.price);
                                                        final EditText comment = (EditText) dialog.findViewById(R.id.comment);
                                                        final TextView date = (TextView) dialog.findViewById(R.id.date_text);
                                                        final Category categoryProceed = listCategoriesProceedes.get(spinnerProceed.getSelectedItemPosition());
                                                        final Category categoryPlace = listCategoriesPlaces.get(spinnerPlace.getSelectedItemPosition());
                                                        //записываем
                                                        final String userId = loader.getUid();
                                                        loader.getmDatabase().child("users").child(userId).addListenerForSingleValueEvent(
                                                                new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                                        //удаляем элемент
                                                                        int position = viewHolder.getAdapterPosition();
                                                                        DatabaseReference ref = mAdapter.getRef(position);
                                                                        ref.removeValue();
                                                                        //добавляем новый жлемент
                                                                        // Get user value
                                                                        User user = dataSnapshot.getValue(User.class);

                                                                        // [START_EXCLUDE]
                                                                        if (user == null) {
                                                                            // User is null, error out
                                                                            Toast.makeText(getActivity(), "Error: could not fetch user.", Toast.LENGTH_SHORT).show();
                                                                        } else {
                                                                            // Write new post
                                                                            proceed.setUid(userId);
                                                                            proceed.setAuthor(user.getEmail());
                                                                            loader.writeNewProceed(new Proceed(title.getText().toString(),
                                                                                    price.getText().toString(),
                                                                                    date.getText().toString(),
                                                                                    comment.getText().toString(),
                                                                                    categoryProceed,
                                                                                    categoryPlace,
                                                                                    userId,
                                                                                    user.getEmail()));
                                                                        }
                                                                    }

                                                                    @Override
                                                                    public void onCancelled(DatabaseError databaseError) {
                                                                    }
                                                                });
                                                    }
                                                }).build();
                                        //устанавливаем в поля значения из редактируемой транзакции
                                        EditText title = (EditText) newDialog.getCustomView().findViewById(R.id.title);
                                        title.setText(proceed.getTitle());
                                        TextView category_first = (TextView) newDialog.getCustomView().findViewById(R.id.category_first);
                                        category_first.setText(R.string.category_proceed);
                                        TextView category_second = (TextView) newDialog.getCustomView().findViewById(R.id.category_second);
                                        category_second.setText(R.string.category_place);
                                        EditText price = (EditText) newDialog.getCustomView().findViewById(R.id.price);
                                        price.setText(proceed.getPrice());
                                        EditText comment = (EditText) newDialog.getCustomView().findViewById(R.id.comment);
                                        comment.setText(proceed.getComment());
                                        dateText = (TextView) newDialog.getCustomView().findViewById(R.id.date_text);
                                        dateText.setText(proceed.getDate());
                                        //слушатель вызова изменения даты
                                        dateText.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Calendar now = Calendar.getInstance();
                                                try {
                                                    now.setTime(Transaction.df.parse(proceed.getDate()));
                                                } catch (ParseException e) {

                                                }
                                                DatePickerDialog dpd = DatePickerDialog.newInstance(
                                                        new DatePickerDialog.OnDateSetListener() {
                                                            @Override
                                                            public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                                                                Calendar calendar = Calendar.getInstance();
                                                                calendar.set(year, monthOfYear, dayOfMonth);
                                                                String date = Transaction.df.format(calendar.getTime());
                                                                dateText.setText(date);
                                                            }
                                                        },
                                                        now.get(Calendar.YEAR),
                                                        now.get(Calendar.MONTH),
                                                        now.get(Calendar.DAY_OF_MONTH)
                                                );
                                                dpd.show(getFragmentManager(), "Datepickerdialog");
                                            }
                                        });
                                        List<Category> data = loader.getCategores();
                                        //отделяем только необходимые категории
                                        listCategoriesProceedes = KindOfCategories.sortData(data, KindOfCategories.getProceed());
                                        listCategoriesPlaces = KindOfCategories.sortData(data, KindOfCategories.getPlace());
                                        //создаём для каждого спинера свой адаптер и устанавливаем их
                                        ArrayAdapter<String> adapterProceedes = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, getStringArray(listCategoriesProceedes));
                                        ArrayAdapter<String> adapterPlaces = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, getStringArray(listCategoriesPlaces));
                                        spinnerProceed = (Spinner) newDialog.getCustomView().findViewById(R.id.spinner_transaction);
                                        spinnerPlace = (Spinner) newDialog.getCustomView().findViewById(R.id.spinner_place);
                                        spinnerProceed.setAdapter(adapterProceedes);
                                        spinnerProceed.setSelection(KindOfCategories.getPosition(listCategoriesProceedes, proceed.getCategoryProceedes()));
                                        spinnerPlace.setAdapter(adapterPlaces);
                                        spinnerPlace.setSelection(KindOfCategories.getPosition(listCategoriesPlaces, proceed.getCategoryPlace()));
                                        newDialog.show();
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
            }
        };
        //слушатель кэша
        cash.setTitle(loader.getCashCount());
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
                                new MaterialDialog.Builder(getActivity())
                                        .title(R.string.delet_dialog)
                                        .positiveText(R.string.ok_button)
                                        .negativeText(R.string.cancel_button)
                                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                                            @Override
                                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                int position = viewHolder.getAdapterPosition();
                                                DatabaseReference ref = mAdapter.getRef(position);
                                                ref.removeValue();
                                            }
                                        })
                                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                                            @Override
                                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                dialog.dismiss();
                                                mRecycler.setAdapter(mAdapter);
                                            }
                                        }).show();
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

    @AfterViews
    void ready() {
        base.addListener(this);
    }

    @Click
    void fabClicked() {
        List<Proceed> proceeds = loader.getProceedes();
        Proceed lastProcced;
        if (proceeds.size() != 0) {
            lastProcced = proceeds.get(0);
        } else {
            lastProcced = new Proceed(
                    "ЗАРПЛАТА",
                    "1239",
                    Proceed.df.format(new Date()),
                    "наконецто дали денежку!!!",
                    new Category(getString(R.string.hint_category_exemple), KindOfCategories.getProceed(), "", true),
                    new Category(getString(R.string.hint_category_exemple), KindOfCategories.getPlace(), "", true),
                    "",
                    "");
        }
        Intent intent = new Intent(getActivity(), AddProccedActivity_.class);
        intent.putExtra(Proceed.class.getCanonicalName(), lastProcced);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.from_midle, R.anim.in_midle);
    }

    @Override
    public void doEvent() {
        cash.setTitle(loader.getCashCount());
    }

    @Override
    public void removeElement() {
        cash.setTitle(loader.getCashCount());
    }
}

