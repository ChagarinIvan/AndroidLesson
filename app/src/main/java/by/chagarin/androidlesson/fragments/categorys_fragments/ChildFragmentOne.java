package by.chagarin.androidlesson.fragments.categorys_fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Callable;

import by.chagarin.androidlesson.ColorRandom;
import by.chagarin.androidlesson.ColorRandom_;
import by.chagarin.androidlesson.DataLoader;
import by.chagarin.androidlesson.DataLoader_;
import by.chagarin.androidlesson.R;
import by.chagarin.androidlesson.objects.Category;
import by.chagarin.androidlesson.objects.Proceed;
import by.chagarin.androidlesson.objects.Transaction;
import by.chagarin.androidlesson.objects.Transfer;
import by.chagarin.androidlesson.objects.User;
import by.chagarin.androidlesson.viewholders.CategoryViewHolder;

public class ChildFragmentOne extends Fragment {
    private DataLoader loader;
    private RecyclerView mRecycler;
    private FirebaseRecyclerAdapter<Category, CategoryViewHolder> mAdapter;
    private boolean isCheck;

    public ChildFragmentOne() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category_child, container, false);
        ColorRandom colorRandom = ColorRandom_.getInstance_(getActivity());
        view.setBackgroundColor(colorRandom.getRandomColor());
        loader = DataLoader_.getInstance_(getActivity());
        mRecycler = (RecyclerView) view.findViewById(R.id.categories_list_view);
        // [END create_database_reference]
        mRecycler.setHasFixedSize(true);
        // Set up Layout Manager, reverse layout
        LinearLayoutManager mManager = new LinearLayoutManager(getActivity());
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        mRecycler.setLayoutManager(mManager);
        // Set up FirebaseRecyclerAdapter with the Query
        final Query postsQuery = loader.getQuery(getQuery());
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
                                                                            loader.writeNewCategory(category, getQuery());
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
                                                        if (checkCategory(allUsedCategoresKeys, getCategoryList().get(position).key)) {
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
        // Inflate the layout for this fragment
        return view;
    }

    public List<Category> getCategoryList() {
        return null;
    }

    public String getQuery() {
        return DataLoader.CATEGORIES;
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
}
