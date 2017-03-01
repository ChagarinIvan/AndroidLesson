package by.chagarin.androidlesson.fragments;

import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
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

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Callable;

import by.chagarin.androidlesson.DataLoader;
import by.chagarin.androidlesson.KindOfCategories;
import by.chagarin.androidlesson.R;
import by.chagarin.androidlesson.objects.Category;
import by.chagarin.androidlesson.objects.Proceed;
import by.chagarin.androidlesson.objects.Transaction;
import by.chagarin.androidlesson.objects.Transfer;
import by.chagarin.androidlesson.objects.User;
import by.chagarin.androidlesson.viewholders.CategoryViewHolder;

import static by.chagarin.androidlesson.DataLoader.CATEGORIES;

@EFragment(R.layout.fragment_categores)
public class CategoresFragment extends Fragment {

    @ViewById(R.id.categories_list_view)
    RecyclerView mRecycler;

    @ViewById(R.id.fab)
    FloatingActionButton fab;

    @Bean
    DataLoader loader;

    @ViewById
    SwipeRefreshLayout swipeLayout;

    private FirebaseRecyclerAdapter<Category, CategoryViewHolder> mAdapter;
    private boolean isCheck;

    @AfterViews
    void afterView() {
        // [END create_database_reference]
        mRecycler.setHasFixedSize(true);

        // Set up Layout Manager, reverse layout
        LinearLayoutManager mManager = new LinearLayoutManager(getActivity());
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        mRecycler.setLayoutManager(mManager);
        // Set up FirebaseRecyclerAdapter with the Query
        final Query postsQuery = loader.getQuery(CATEGORIES);
        mAdapter = new FirebaseRecyclerAdapter<Category, CategoryViewHolder>(Category.class, R.layout.category_list_item,
                CategoryViewHolder.class, postsQuery) {
            @Override
            protected void populateViewHolder(final CategoryViewHolder viewHolder, final Category model, final int position) {
                // Bind Post to ViewHolder, setting OnClickListener for the star button
                viewHolder.bindToCategory(model);
                //вешаем слушателя долгого нажатия для изменения категории
                viewHolder.cardView.setOnLongClickListener(new View.OnLongClickListener() {

                    @Override
                    public boolean onLongClick(View v) {
                        new MaterialDialog.Builder(getActivity())
                                .title(R.string.dialog_title)
                                .content(R.string.category_change_warning)
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull final MaterialDialog dialog, @NonNull DialogAction which) {
                                        new MaterialDialog.Builder(getActivity())
                                                .title(R.string.category_change)
                                                .content(R.string.category_change_content)
                                                .inputType(InputType.TYPE_CLASS_TEXT)
                                                .inputRange(2, 20)
                                                .input(model.name, model.name, false, new MaterialDialog.InputCallback() {
                                                    @Override
                                                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {

                                                    }
                                                })
                                                .checkBoxPrompt(getString(R.string.copilka), !model.isShow.equals("yes"), new CompoundButton.OnCheckedChangeListener() {
                                                    @Override
                                                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                                        isCheck = isChecked;
                                                    }
                                                })
                                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                                    @Override
                                                    public void onClick(@NonNull final MaterialDialog dialoge, @NonNull DialogAction which) {
                                                        final String userId = DataLoader.getUid();
                                                        loader.mDatabase.child(DataLoader.USERS).child(userId).addListenerForSingleValueEvent(
                                                                new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                                        // Get user value
                                                                        User user = dataSnapshot.getValue(User.class);

                                                                        // [START_EXCLUDE]
                                                                        if (user == null) {
                                                                            // User is null, error out
                                                                            Toast.makeText(getActivity(),
                                                                                    "Error: could not fetch user.",
                                                                                    Toast.LENGTH_SHORT).show();
                                                                        } else {
                                                                            // Write new postString name, String kind, String author, boolean isShowIt, String key
                                                                            String isShow;
                                                                            if (isCheck) {
                                                                                isShow = "not";
                                                                            } else {
                                                                                isShow = "yes";
                                                                            }
                                                                            //noinspection ConstantConditions
                                                                            Category category = new Category(dialoge.getInputEditText().getText().toString(),
                                                                                    model.kind,
                                                                                    userId,
                                                                                    isShow,
                                                                                    model.key);
                                                                            loader.writeNewCategory(category);
                                                                        }
                                                                    }

                                                                    @Override
                                                                    public void onCancelled(DatabaseError databaseError) {
                                                                        // [START_EXCLUDE]
                                                                    }
                                                                });
                                                    }
                                                })
                                                .onNegative(new MaterialDialog.SingleButtonCallback() {
                                                    @Override
                                                    public void onClick(@NonNull MaterialDialog dialoge, @NonNull DialogAction which) {
                                                        dialoge.dismiss();
                                                    }
                                                })
                                                .positiveText(R.string.save)
                                                .negativeText(R.string.dont_save)
                                                .show();

                                    }
                                })
                                .onNegative(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        dialog.dismiss();
                                    }
                                })
                                .positiveText(R.string.agree)
                                .negativeText(R.string.disagree)
                                .show();

                        return true;
                    }
                });

            }
        };
        //реакция на свайп
        final ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            //запрос на удаление при полном свайпе
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
                                                final int position = viewHolder.getAdapterPosition();
                                                loader.mDatabase.addListenerForSingleValueEvent(new DataLoader.AllDataLoaderListener(new Callable() {
                                                    @Override
                                                    public Object call() throws Exception {
                                                        List<String> allUsedCategoresKeys = getAllCategoryKeys(DataLoader.transactionList, DataLoader.proceedList, DataLoader.transferList);
                                                        if (checkCategory(allUsedCategoresKeys, DataLoader.categoryList.get(position).key)) {
                                                            DatabaseReference ref = mAdapter.getRef(position);
                                                            ref.removeValue();
                                                        } else {
                                                            Toast.makeText(getActivity(), getString(R.string.warning_sql), Toast.LENGTH_LONG).show();
                                                            mRecycler.setAdapter(mAdapter);
                                                        }
                                                        return null;
                                                    }
                                                }));
                                            }
                                        })
                                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                                            @Override
                                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                dialog.dismiss();
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

    /**
     * метод проверяет есть ли данная категория в списке
     */
    private boolean checkCategory(List<String> allUsedCategoresKeys, String position) {
        for (String line : allUsedCategoresKeys) {
            if (line.equals(position)) {
                return false;
            }
        }
        return true;
    }

    /**
     * метод создает лист со списком ключей всех использованных категорий
     */
    private List<String> getAllCategoryKeys(List<Transaction> transactionList, List<Proceed> proceedList, List<Transfer> transferList) {
        List<String> result = new ArrayList<>();
        for (Transaction tr : transactionList) {
            result.add(tr.categoryPlaceKey);
            result.add(tr.categoryTransactionKey);
        }
        for (Proceed pr : proceedList) {
            result.add(pr.categoryPlaceKey);
            result.add(pr.categoryProceedesKey);
        }
        for (Transfer tr : transferList) {
            result.add(tr.categoryPlaceFromKey);
            result.add(tr.categoryPlaceToKey);
        }
        HashSet<String> resultSet = new HashSet<>(result);
        return new ArrayList<>(resultSet);
    }

    //по нажатию на ФАБ запускается диалог добавления новой категории
    @Click
    void fabClicked() {
        alertDialog();
    }

    /**
     * инициируем всплывающий диалог
     */
    private void alertDialog() {
        new MaterialDialog.Builder(getActivity())
                .title(R.string.add_categores)
                .positiveText(R.string.ok_button)
                .content(R.string.chose_content)
                .items((CharSequence[]) KindOfCategories.getKinds())
                .itemsCallbackSingleChoice(0, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                        return true;
                    }
                })
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        final int kind = dialog.getSelectedIndex();
                        MaterialDialog.Builder newBuilder = new MaterialDialog.Builder(getActivity())
                                .title(R.string.add_categores)
                                .positiveText(R.string.save)
                                .content(R.string.input_content)
                                .inputType(InputType.TYPE_CLASS_TEXT |
                                        InputType.TYPE_TEXT_VARIATION_PERSON_NAME |
                                        InputType.TYPE_TEXT_FLAG_CAP_WORDS)
                                .inputRange(2, 20)
                                .input(R.string.hint_category_exemple, 0, false, new MaterialDialog.InputCallback() {
                                    @Override
                                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {

                                    }
                                })
                                .negativeText(R.string.dont_save)
                                .onNegative(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        dialog.dismiss();
                                    }
                                })
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull final MaterialDialog dialog, @NonNull DialogAction which) {

                                        final String userId = DataLoader.getUid();
                                        loader.mDatabase.child(DataLoader.USERS).child(userId).addListenerForSingleValueEvent(
                                                new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        // Get user value
                                                        User user = dataSnapshot.getValue(User.class);

                                                        // [START_EXCLUDE]
                                                        if (user == null) {
                                                            // User is null, error out
                                                            Toast.makeText(getActivity(),
                                                                    "Error: could not fetch user.",
                                                                    Toast.LENGTH_SHORT).show();
                                                        } else {
                                                            // Write new postString name, String kind, String author, boolean isShowIt, String key
                                                            String key = loader.mDatabase.child(CATEGORIES).push().getKey();
                                                            String isShow;
                                                            if (isCheck) {
                                                                isShow = "not";
                                                            } else {
                                                                isShow = "yes";
                                                            }
                                                            //noinspection ConstantConditions
                                                            Category category = new Category(dialog.getInputEditText().getText().toString(),
                                                                    KindOfCategories.getKinds()[kind],
                                                                    userId,
                                                                    isShow,
                                                                    key);
                                                            loader.writeNewCategory(category);
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {
                                                        // [START_EXCLUDE]
                                                    }
                                                });
                                        // [END single_value_read]
                                    }
                                });
                        if (TextUtils.equals(KindOfCategories.getKinds()[kind], KindOfCategories.getPlace())) {
                            newBuilder.checkBoxPrompt(getString(R.string.copilka), false, new CompoundButton.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                    isCheck = isChecked;
                                }
                            });
                        }
                        newBuilder.build().show();
                    }
                })
                .negativeText(R.string.cancel_button)
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                }).show();
    }
}
