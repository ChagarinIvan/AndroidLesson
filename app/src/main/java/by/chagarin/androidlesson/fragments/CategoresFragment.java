package by.chagarin.androidlesson.fragments;

import android.app.Dialog;
import android.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.melnykov.fab.FloatingActionButton;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.HashMap;
import java.util.Map;

import by.chagarin.androidlesson.KindOfCategories;
import by.chagarin.androidlesson.R;
import by.chagarin.androidlesson.objects.Category;
import by.chagarin.androidlesson.objects.User;
import by.chagarin.androidlesson.viewholders.CategoryViewHolder;

import static by.chagarin.androidlesson.DataLoader.CATEGORIES;

@EFragment(R.layout.fragment_categores)
public class CategoresFragment extends Fragment {
    private static final String REQUIRED = "Required";

    @ViewById(R.id.categories_list_view)
    RecyclerView mRecycler;

    @ViewById(R.id.fab)
    FloatingActionButton fab;

    @ViewById
    SwipeRefreshLayout swipeLayout;

    private Spinner spinner;
    private DatabaseReference mDatabase;
    private LinearLayoutManager mManager;
    private FirebaseRecyclerAdapter<Category, CategoryViewHolder> mAdapter;
    private Button ok;

    @AfterViews
    void afterView() {
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // [END create_database_reference]
        mRecycler.setHasFixedSize(true);

        // Set up Layout Manager, reverse layout
        mManager = new LinearLayoutManager(getActivity());
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        mRecycler.setLayoutManager(mManager);
        // Set up FirebaseRecyclerAdapter with the Query
        Query postsQuery = getQuery(mDatabase);
        mAdapter = new FirebaseRecyclerAdapter<Category, CategoryViewHolder>(Category.class, R.layout.category_list_item,
                CategoryViewHolder.class, postsQuery) {
            @Override
            protected void populateViewHolder(final CategoryViewHolder viewHolder, final Category model, final int position) {
                final DatabaseReference postRef = getRef(position);

                // Set click listener for the whole post view
                final String postKey = postRef.getKey();

                // Determine if the current user has liked this post and set UI accordingly

                // Bind Post to ViewHolder, setting OnClickListener for the star button
                viewHolder.bindToCategory(model);
            }
        };
        mRecycler.setAdapter(mAdapter);
    }

    //по нажатию на ФАБ запускается диалог добавления новой категории
    @Click
    void fabClicked() {
        alertDialog();
    }

    public Query getQuery(DatabaseReference databaseReference) {
        // [START recent_posts_query]
        // Last 100 posts, these are automatically the 100 most recent
        // due to sorting by push() keys
        // [END recent_posts_query]

        return databaseReference.child(CATEGORIES)
                .limitToFirst(100);
    }

    public String getUid() {
        //noinspection ConstantConditions
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    /**
     * инициируем всплывающий диалог
     */
    private void alertDialog() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.dialog_window);
        TextView textView = (TextView) dialog.findViewById(R.id.title);
        spinner = (Spinner) dialog.findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this.getActivity(), R.layout.spinner_item, KindOfCategories.getKinds());
        spinner.setAdapter(adapter);
        final EditText editText = (EditText) dialog.findViewById(R.id.edit_text);
        ok = (Button) dialog.findViewById(R.id.ok_button);
        Button cancelButton = (Button) dialog.findViewById(R.id.cancel_button);

        textView.setText(R.string.categores);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Editable text = editText.getText();
                final String kind = (String) spinner.getSelectedItem();

                // Title is required
                if (TextUtils.isEmpty(text)) {
                    editText.setError(REQUIRED);
                    return;
                }
                // Disable button so there are no multi-posts
                setEditingEnabled(false);
                Toast.makeText(getActivity(), "Posting...", Toast.LENGTH_SHORT).show();

                // [START single_value_read]
                final String userId = getUid();
                mDatabase.child("users").child(userId).addListenerForSingleValueEvent(
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
                                    // Write new post
                                    writeNewCategory(userId, user.username, text.toString(), kind);
                                }

                                // Finish this Activity, back to the stream
                                setEditingEnabled(true);
                                dialog.dismiss();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                // [START_EXCLUDE]
                                setEditingEnabled(true);
                                // [END_EXCLUDE]
                            }
                        });
                // [END single_value_read]
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        //собственно настравиваем вид и пакезываем диалог
        //noinspection ConstantConditions
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
    }

    private void writeNewCategory(String userId, String username, String title, String kind) {
        // Create new post at /user-category/$userid/category and at
        // /category/categoryID simultaneously
        String key = mDatabase.child(CATEGORIES).push().getKey();
        Category category = new Category(title, kind, userId, username);
        Map<String, Object> postValues = category.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/" + CATEGORIES + "/" + key, postValues);
        childUpdates.put("/user-" + CATEGORIES + "/" + userId + "/" + key, postValues);

        mDatabase.updateChildren(childUpdates);
    }

    private void setEditingEnabled(boolean enabled) {
        if (enabled) {
            ok.setVisibility(View.VISIBLE);
        } else {
            ok.setVisibility(View.GONE);
        }
    }


}
