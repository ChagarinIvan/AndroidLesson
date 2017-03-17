package by.chagarin.androidlesson;


import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import by.chagarin.androidlesson.objects.Family;
import by.chagarin.androidlesson.objects.User;

@EActivity(R.layout.activity_sign)
public class SignInActivity extends ActionBarActivity implements GoogleApiClient.OnConnectionFailedListener {
    private FirebaseAuth mFirebaseAuth;
    private static final int RC_SIGN_IN = 9001;
    private GoogleApiClient mGoogleApiClient;
    private Dialog question_dialog;
    private FirebaseUser user;
    private User person;

    @ViewById
    RadioGroup signRadioGroup;

    @Bean
    DataLoader loader;

    @ViewById
    SignInButton signInButton;

    @AfterViews
    void afterCreate() {
        loader.mDatabase.child("family").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Toast.makeText(getApplicationContext(), "такая семья уже есть", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mFirebaseAuth = FirebaseAuth.getInstance();
    }

    @Click
    void signInButtonClicked() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed
                Toast.makeText(this, "Google Sign In failed.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(SignInActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            //user = task.getResult().getUser();
                            //writeNewUser(user);
                            if (signRadioGroup.getCheckedRadioButtonId() == R.id.sign_radio_button_1) {
                                start();
                            } else {
                                startAlertDialog();
                            }
                        }
                    }
                });
    }

    private void start() {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }

    private void startAlertDialog() {
        question_dialog = new Dialog(this);
        question_dialog.setContentView(R.layout.dialog_transfer_question);
        TextView title = (TextView) question_dialog.findViewById(R.id.title);
        TextView question = (TextView) question_dialog.findViewById(R.id.question);
        title.setText(getText(R.string.family_budget));
        question.setText(getText(R.string.family_question));

        Button okButton = (Button) question_dialog.findViewById(R.id.transfer_to);
        okButton.setText(getText(R.string.family_create_new));
        Button cancelButton = (Button) question_dialog.findViewById(R.id.transfer_from);
        cancelButton.setText(getText(R.string.family_add));
        okButton.setOnClickListener(new ButtonClickListener(true, this));
        cancelButton.setOnClickListener(new ButtonClickListener(false, this));
        //собственно настравиваем вид и пакезываем диалог
        //noinspection ConstantConditions
        question_dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        question_dialog.show();
    }

    private void writeNewUser(FirebaseUser user) {
        person = new User(user.getDisplayName(), user.getEmail(), user.getUid(), true);
        loader.writeNewUser(person);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }


    private class ButtonClickListener implements View.OnClickListener {
        private final Activity activity;
        private boolean flag;

        public ButtonClickListener(boolean b, Activity activity) {
            this.activity = activity;
            flag = b;
        }

        @Override
        public void onClick(View v) {
            final Dialog dialog = new Dialog(activity);
            if (flag) {
                dialog.setContentView(R.layout.dialog_create_family);
                final EditText name = (EditText) dialog.findViewById(R.id.edit_text_name);
                final EditText password = (EditText) dialog.findViewById(R.id.edit_text_password);
                Button okButton = (Button) dialog.findViewById(R.id.ok_button);
                Button cancelButton = (Button) dialog.findViewById(R.id.cancel_button);
                okButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String familyName = String.valueOf(name.getText());
                        String familyPassword = String.valueOf(password.getText());
                        Family family = new Family(familyName, familyPassword, person);
                        loader.mDatabase.child("familes").child(family.getName()).setValue(family);
                        dialog.dismiss();
                        question_dialog.dismiss();
                        start();

                    }
                });
                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        question_dialog.dismiss();
                    }
                });
            }
            //noinspection ConstantConditions
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.show();
        }
    }
}
