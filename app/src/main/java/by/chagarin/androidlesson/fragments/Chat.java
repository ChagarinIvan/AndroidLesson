package by.chagarin.androidlesson.fragments;


import android.app.Dialog;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import by.chagarin.androidlesson.R;
import by.chagarin.androidlesson.objects.Post;
import by.chagarin.androidlesson.objects.User;
import by.chagarin.androidlesson.viewholders.PostViewHolder;

@EFragment(R.layout.fragment_chat)
public class Chat extends Fragment {
    private static final String REQUIRED = "Required";
    private EditText text;
    private EditText nameText;
    private Button ok;

    @ViewById
    FloatingActionButton fab;
    private DatabaseReference mDatabase;

    @ViewById(R.id.categories_list_view)
    RecyclerView mRecycler;

    @AfterViews
    void ready() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // [END create_database_reference]
        mRecycler.setHasFixedSize(true);
        // Set up Layout Manager, reverse layout
        LinearLayoutManager mManager = new LinearLayoutManager(getActivity());
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        mRecycler.setLayoutManager(mManager);
        // Set up FirebaseRecyclerAdapter with the Query
        Query postsQuery = getQuery(mDatabase);
        FirebaseRecyclerAdapter<Post, PostViewHolder> mAdapter = new FirebaseRecyclerAdapter<Post, PostViewHolder>(Post.class, R.layout.item_post,
                PostViewHolder.class, postsQuery) {
            @Override
            protected void populateViewHolder(final PostViewHolder viewHolder, final Post model, final int position) {

                // Bind Post to ViewHolder, setting OnClickListener for the star button
                viewHolder.bindToPost(model);
            }
        };
        mRecycler.setAdapter(mAdapter);
    }

    public Query getQuery(DatabaseReference databaseReference) {
        // [START recent_posts_query]
        // Last 100 posts, these are automatically the 100 most recent
        // due to sorting by push() keys
        // [END recent_posts_query]

        return databaseReference.child("posts")
                .limitToFirst(100);
    }

    @Click
    void fabClicked() {
        alertDialog();
    }

    private void setEditingEnabled(boolean enabled) {
        nameText.setEnabled(enabled);
        text.setEnabled(enabled);
        if (enabled) {
            ok.setVisibility(View.VISIBLE);
        } else {
            ok.setVisibility(View.GONE);
        }
    }

    public String getUid() {
        //noinspection ConstantConditions
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    private void alertDialog() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.dialog_create_family);
        TextView title = (TextView) dialog.findViewById(R.id.title);
        title.setText("добавим сообщение");
        TextView name = (TextView) dialog.findViewById(R.id.name);
        name.setText("заголовок");
        nameText = (EditText) dialog.findViewById(R.id.edit_text_name);
        TextView passwordText = (TextView) dialog.findViewById(R.id.password_text);
        passwordText.setText("текст");
        text = (EditText) dialog.findViewById(R.id.edit_text_password);
        text.setInputType(InputType.TYPE_CLASS_TEXT);
        ok = (Button) dialog.findViewById(R.id.ok_button);
        Button cancel = (Button) dialog.findViewById(R.id.cancel_button);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String title = nameText.getText().toString();
                final String body = text.getText().toString();

                // Title is required
                if (TextUtils.isEmpty(title)) {
                    nameText.setError(REQUIRED);
                    return;
                }

                // Body is required
                if (TextUtils.isEmpty(body)) {
                    text.setError(REQUIRED);
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
                                    writeNewPost(userId, user.userKey, title, body);
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
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        //noinspection ConstantConditions
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
    }

    private void writeNewPost(String userId, String username, String title, String body) {
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously
        String key = mDatabase.child("posts").push().getKey();
        Post post = new Post(userId, username, title, body);
        Map<String, Object> postValues = post.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/posts/" + key, postValues);
        childUpdates.put("/user-posts/" + userId + "/" + key, postValues);

        mDatabase.updateChildren(childUpdates);
    }
}
