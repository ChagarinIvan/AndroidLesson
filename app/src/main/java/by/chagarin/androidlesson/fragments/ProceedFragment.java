package by.chagarin.androidlesson.fragments;


import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.melnykov.fab.FloatingActionButton;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.OptionsMenuItem;
import org.androidannotations.annotations.ViewById;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Callable;

import by.chagarin.androidlesson.ColorRandom;
import by.chagarin.androidlesson.DataLoader;
import by.chagarin.androidlesson.KindOfCategories;
import by.chagarin.androidlesson.MainActivity;
import by.chagarin.androidlesson.R;
import by.chagarin.androidlesson.objects.Category;
import by.chagarin.androidlesson.objects.Proceed;
import by.chagarin.androidlesson.objects.User;
import by.chagarin.androidlesson.viewholders.ProceedViewHolder;

import static by.chagarin.androidlesson.DataLoader.ACTIONS;
import static by.chagarin.androidlesson.DataLoader.CATEGORIES;
import static by.chagarin.androidlesson.DataLoader.PLACES;
import static by.chagarin.androidlesson.DataLoader.PROCEEDS;
import static by.chagarin.androidlesson.DataLoader.USERS;
import static by.chagarin.androidlesson.DataLoader.df;
import static by.chagarin.androidlesson.KindOfCategories.getStringArray;

@EFragment(R.layout.fragment_proceeds)
@OptionsMenu(R.menu.menu_transactions)
public class ProceedFragment extends Fragment {

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

    private FirebaseRecyclerAdapter<Proceed, ProceedViewHolder> mAdapter;
    private List<Category> listCategoriesProceedes;
    private List<Category> listCategoriesPlaces;
    private Proceed proceed;
    private TextView dateText;
    private ArrayAdapter<String> adapterProceedes;
    private ArrayAdapter<String> adapterPlaces;
    private Spinner spinnerProceed;
    private Spinner spinnerPlace;
    private ValueEventListener valueEventListener;

    @Bean
    ColorRandom colorRandom;

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        final MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.actualFragment = this;
        mainActivity.setTitle(R.string.add);
        this.getView().setBackgroundColor(colorRandom.getRandomColor());
        super.onPrepareOptionsMenu(menu);
        // [END create_database_reference]
        mRecycler.setHasFixedSize(true);
        // Set up Layout Manager, reverse layout
        LinearLayoutManager mManager = new LinearLayoutManager(getActivity());
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        mRecycler.setLayoutManager(mManager);
        // Set up FirebaseRecyclerAdapter with the Query
        final Query postsQuery = loader.getQuery(ACTIONS + PROCEEDS);
        //адаптер БД
        mAdapter = new FirebaseRecyclerAdapter<Proceed, ProceedViewHolder>(Proceed.class, R.layout.list_item,
                ProceedViewHolder.class, postsQuery) {
            @Override
            protected void populateViewHolder(final ProceedViewHolder viewHolder, final Proceed model, final int position) {
                viewHolder.bindToProceed(model);
                viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loader.mDatabase.child(CATEGORIES).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String proceedName = dataSnapshot.child(PROCEEDS).child(model.categoryProceedesKey).child("name").getValue(String.class);
                                String placeName = dataSnapshot.child(PLACES).child(model.categoryPlaceKey).child("name").getValue(String.class);
                                new MaterialDialog.Builder(getActivity())
                                        .items(getListForViewInfoDialog(model, proceedName, placeName))
                                        .autoDismiss(true)
                                        .canceledOnTouchOutside(true)
                                        .show();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                });
                viewHolder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        loadCategories(new Callable() {
                            @Override
                            public Object call() throws Exception {
                                //запускаем диалог с вопросом об изменении
                                new MaterialDialog.Builder(getActivity())
                                        .title(R.string.dialog_title)
                                        .positiveText(R.string.agree)
                                        .negativeText(R.string.disagree)
                                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                                            @Override
                                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                dialog.dismiss();
                                            }
                                        })
                                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                                            @Override
                                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                //показываем лист одиночного выбора полей транзакции для изменения
                                                getMaterialDialog(model, new MaterialDialog.SingleButtonCallback() {
                                                    @Override
                                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                        final EditText title = (EditText) dialog.findViewById(R.id.title);
                                                        final EditText price = (EditText) dialog.findViewById(R.id.price);
                                                        final EditText comment = (EditText) dialog.findViewById(R.id.comment);
                                                        final Category categoryProceed = listCategoriesProceedes.get(spinnerProceed.getSelectedItemPosition());
                                                        final Category categoryPlace = listCategoriesPlaces.get(spinnerPlace.getSelectedItemPosition());
                                                        //записываем
                                                        final String userId = DataLoader.getUid();
                                                        loader.mDatabase.child(USERS).child(userId).addListenerForSingleValueEvent(
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
                                                                            proceed = new Proceed(title.getText().toString(),
                                                                                    price.getText().toString(),
                                                                                    dateText.getText().toString(),
                                                                                    comment.getText().toString(),
                                                                                    categoryProceed.key, categoryPlace.key, userId, model.key);
                                                                            loader.writeNewProceed(proceed);
                                                                        }
                                                                    }

                                                                    @Override
                                                                    public void onCancelled(DatabaseError databaseError) {
                                                                    }
                                                                });
                                                    }
                                                }, R.string.change_transacton_title);
                                            }
                                        })
                                        .show();
                                return null;
                            }
                        });
                        return true;
                    }
                });
            }
        };

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                loader.calcAndSetCash(cash, true);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        //слушатель кэша
        loader.mDatabase.addValueEventListener(valueEventListener);
        final Fragment fragment = this;
        cash.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                mainActivity.parentFragment = fragment;
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

    private List<String> getListForViewInfoDialog(Proceed model, String proceedName, String placeName) {
        ArrayList<String> list = new ArrayList<>();
        list.add(model.title);
        list.add(model.price);
        list.add(model.comment);
        list.add(model.date);
        list.add(proceedName);
        list.add(placeName);
        return list;
    }

    @Override
    public void onDestroy() {
        loader.mDatabase.removeEventListener(valueEventListener);
        super.onDestroy();
    }

    private void createDataPickerDialogWithNowDate(String date) {
        Calendar now = Calendar.getInstance();
        if (!TextUtils.isEmpty(date)) {
            try {
                now.setTime(df.parse(date));
            } catch (ParseException ignored) {
            }
        }
        DatePickerDialog dpd = DatePickerDialog.newInstance(new DatePicker(),
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dpd.show(getFragmentManager(), "Datepickerdialog");
    }

    private void getMaterialDialog(Proceed model, final MaterialDialog.SingleButtonCallback singleButtonCallback, int dialogTitle) {
        MaterialDialog newDialog = new MaterialDialog.Builder(getActivity())
                .title(dialogTitle)
                .customView(R.layout.dialog_change_transaction, true)
                .positiveText(R.string.save)
                .negativeText(R.string.dont_save)
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .onPositive(singleButtonCallback)
                .build();
        //устанавливаем в поля значения из редактируемой транзакции
        //noinspection ConstantConditions
        TextView textView = (TextView) newDialog.getCustomView().findViewById(R.id.category_first);
        textView.setText(R.string.category_proceed);
        TextView textView2 = (TextView) newDialog.getCustomView().findViewById(R.id.category_second);
        textView2.setText(R.string.category_place);
        EditText title = (EditText) newDialog.getCustomView().findViewById(R.id.title);
        EditText price = (EditText) newDialog.getCustomView().findViewById(R.id.price);
        EditText comment = (EditText) newDialog.getCustomView().findViewById(R.id.comment);
        dateText = (TextView) newDialog.getCustomView().findViewById(R.id.date_text);
        String text = "";
        spinnerProceed = (Spinner) newDialog.getCustomView().findViewById(R.id.spinner_transaction);
        spinnerPlace = (Spinner) newDialog.getCustomView().findViewById(R.id.spinner_place);
        spinnerProceed.setAdapter(adapterProceedes);
        spinnerPlace.setAdapter(adapterPlaces);
        if (model != null) {
            title.setText(model.title);
            price.setText(model.price);
            comment.setText(model.comment);
            dateText.setText(model.date);
            text = model.date;
            spinnerProceed.setSelection(KindOfCategories.getPosition(listCategoriesProceedes, model.categoryProceedesKey));
            spinnerPlace.setSelection(KindOfCategories.getPosition(listCategoriesPlaces, model.categoryPlaceKey));
        } else {
            createDataPickerDialogWithNowDate("");
        }
        final String finalText = text;
        dateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDataPickerDialogWithNowDate(finalText);
            }
        });
        newDialog.show();
    }

    private void loadCategories(final Callable func) {
        listCategoriesProceedes = DataLoader.proceedesCategoryList;
        listCategoriesPlaces = DataLoader.placesCategoryList;
        adapterProceedes = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, getStringArray(listCategoriesProceedes));
        adapterPlaces = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, getStringArray(listCategoriesPlaces));
        try {
            func.call();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Click
    void fabClicked() {
        loadCategories(new Callable() {
            @Override
            public Object call() throws Exception {
                getMaterialDialog(
                        null,
                        new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull final MaterialDialog dialog, @NonNull DialogAction which) {
                                final EditText title = (EditText) dialog.findViewById(R.id.title);
                                final EditText price = (EditText) dialog.findViewById(R.id.price);
                                final EditText comment = (EditText) dialog.findViewById(R.id.comment);
                                final Category categoryProceed = listCategoriesProceedes.get(spinnerProceed.getSelectedItemPosition());
                                final Category categoryPlace = listCategoriesPlaces.get(spinnerPlace.getSelectedItemPosition());
                                //записываем
                                final String userId = DataLoader.getUid();
                                loader.mDatabase.child(USERS).child(userId).addListenerForSingleValueEvent(
                                        new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                //добавляем новый жлемент
                                                // Get user value
                                                User user = dataSnapshot.getValue(User.class);

                                                // [START_EXCLUDE]
                                                if (user == null) {
                                                    // User is null, error out
                                                    Toast.makeText(getActivity(), "Error: could not fetch user.", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    // Write new post
                                                    String key = loader.mDatabase.child(ACTIONS + PROCEEDS).push().getKey();
                                                    String priceValue = price.getText().toString();
                                                    String dateValue = dateText.getText().toString();
                                                    String commentValue = comment.getText().toString();
                                                    String titleValue = title.getText().toString();
                                                    if (TextUtils.isEmpty(titleValue) || TextUtils.isEmpty(priceValue) || TextUtils.equals(dateValue, getString(R.string.date))) {
                                                        Toast.makeText(getActivity(), getString(R.string.warning_null), Toast.LENGTH_SHORT).show();
                                                        dialog.show();
                                                    } else {
                                                        if (categoryProceed == null || categoryPlace == null) {
                                                            Toast.makeText(getActivity(), getString(R.string.warning_no_categories), Toast.LENGTH_SHORT).show();
                                                        } else {
                                                            proceed = new Proceed(titleValue,
                                                                    priceValue,
                                                                    dateValue,
                                                                    commentValue,
                                                                    categoryProceed.key, categoryPlace.key, userId, key);
                                                            loader.writeNewProceed(proceed);
                                                        }
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {
                                            }
                                        });
                            }
                        },
                        R.string.add_preceed);
                return null;
            }
        });
    }

    class DatePicker implements DatePickerDialog.OnDateSetListener {
        @Override
        public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, monthOfYear, dayOfMonth);
            String date = DataLoader.df.format(calendar.getTime());
            dateText.setText(date);
        }
    }
}

