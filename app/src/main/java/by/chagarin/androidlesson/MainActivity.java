package by.chagarin.androidlesson;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;

import com.github.androidprogresslayout.ProgressLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.interfaces.OnCheckedChangeListener;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SwitchDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.octicons_typeface_library.Octicons;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import by.chagarin.androidlesson.fragments.CategoresFragment_;
import by.chagarin.androidlesson.fragments.Chat_;
import by.chagarin.androidlesson.fragments.ProceedFragment_;
import by.chagarin.androidlesson.fragments.StatisticsFragment_;
import by.chagarin.androidlesson.fragments.TransactionsFragment_;
import by.chagarin.androidlesson.fragments.TransferFragment_;
import by.chagarin.androidlesson.objects.User;

import static by.chagarin.androidlesson.DataLoader.USERS;

public class MainActivity extends AppCompatActivity {

    private FirebaseUser mFirebaseUser;
    private Bundle savedInstance;
    private MainActivity context;
    public Fragment actualFragment;
    public Fragment parentFragment;
    public ProgressLayout progressLayout;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //add the values which need to be saved from the drawer to the bundle
        if (result != null) {
            outState = result.saveInstanceState(outState);
            //add the values which need to be saved from the accountHeader to the bundle
            outState = headerResult.saveInstanceState(outState);
            //add the values which need to be saved from the crossFader to the bundle
            super.onSaveInstanceState(outState);
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
    }

    //
    public void setFragment(Fragment fragment) {
        parentFragment = actualFragment;
        result.closeDrawer();
        getFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
    }

    public void showContent() {
        progressLayout.showContent();
    }

    //
    private class DrawerItemClickListener implements Drawer.OnDrawerItemClickListener {
        @Override
        public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
            switch (position) {
                case 1:
                    result.setSelection(1);
                    setFragment(TransactionsFragment_.builder().build());
                    return true;
                case 2:
                    result.setSelection(2);
                    setFragment(ProceedFragment_.builder().build());
                    return true;
                case 3:
                    result.setSelection(3);
                    setFragment(CategoresFragment_.builder().build());
                    return true;
                case 5:
                    result.setSelection(5);
                    setFragment(StatisticsFragment_.builder().build());
                    return true;
                case 6:
                    result.setSelection(6);
                    setFragment(Chat_.builder().build());
                    return true;
                case 4:
                    result.setSelection(4);
                    setFragment(TransferFragment_.builder().build());
                    return true;
            }
            return false;
        }
    }

    //save our header or result
    private AccountHeader headerResult = null;
    private Drawer result = null;
    private User user;
    private DataLoader loader;

    private IProfile profile;

    private Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            profile = new ProfileDrawerItem().withName(user.name).withIcon(bitmap);
            DataLoader.isShow = user.isShow;
            startMainActivity();
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
        }
    };

    private void startMainActivity() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //set the back arrow in the toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Create a few sample profile
        // NOTE you have to define the loader logic too. See the CustomApplication for more details

        // Create the AccountHeader
        headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.drawer_header)
                .addProfiles(profile)
                .withSavedInstance(savedInstance)
                .withSelectionListEnabledForSingleProfile(false)
                .withProfileImagesClickable(false)
                .build();

        //Create the drawer
        result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(headerResult) //set the AccountHeader we created earlier for the header
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.transactions).withIcon(FontAwesome.Icon.faw_shopping_cart),
                        new PrimaryDrawerItem().withName(R.string.add).withIcon(FontAwesome.Icon.faw_download),
                        new PrimaryDrawerItem().withName(R.string.categores).withIcon(FontAwesome.Icon.faw_tags),
                        new PrimaryDrawerItem().withName(R.string.transfer).withIcon(FontAwesome.Icon.faw_adn),
                        new PrimaryDrawerItem().withName(R.string.statistics).withIcon(FontAwesome.Icon.faw_area_chart),
                        new PrimaryDrawerItem().withName(R.string.chat).withIcon(FontAwesome.Icon.faw_chain_broken),
                        new DividerDrawerItem(),
                        new SwitchDrawerItem().withName(R.string.swich).withIcon(Octicons.Icon.oct_tools).withChecked(user.isShow).withOnCheckedChangeListener(new SwitchListener())
                )// add the items we want to use with our Drawer
                .withOnDrawerItemClickListener(new DrawerItemClickListener())
                .withSavedInstance(savedInstance)
                .build();
        actualFragment = TransactionsFragment_.builder().build();
        setFragment(actualFragment);
        progressLayout.showContent();
    }

    private void someMethod(String uri) {
        Picasso.with(this).load(uri).into(target);
    }

    @Override
    public void onDestroy() {  // could be in onPause or onStop
        Picasso.with(this).cancelRequest(target);
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.savedInstance = savedInstanceState;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_persistent_drawer);
        progressLayout = (ProgressLayout) findViewById(R.id.progress_layout);
        ColorRandom colorRandom = ColorRandom_.getInstance_(this);
        context = this;
        FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if (mFirebaseUser == null) {
            // Not signed in, launch the Sign In activity
            startActivity(new Intent(this, SignInActivity_.class));
            finish();
            return;
        }
        loader = DataLoader_.getInstance_(this);
        loader.mDatabase.child(USERS).child(mFirebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
                someMethod(user.photoURL);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //handle the click on the back arrow click
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        //handle the back press :D close the drawer first and if the drawer is closed close the activity
        if (result != null && result.isDrawerOpen()) {
            result.closeDrawer();
        } else {
            if (actualFragment != parentFragment) {
                actualFragment = parentFragment;
                setFragment(parentFragment);
            } else {
                super.onBackPressed();
            }
        }
    }

    private class SwitchListener implements OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(IDrawerItem drawerItem, CompoundButton buttonView, final boolean isChecked) {
            final String userId = DataLoader.getUid();
            loader.mDatabase.child(USERS).child(userId).addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // Get user value
                            user = dataSnapshot.getValue(User.class);
                            user.isShow = isChecked;
                            user.userKey = userId;
                            loader.writeNewUser(user);
                            DataLoader.isShow = isChecked;
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // [START_EXCLUDE]
                        }
                    });
        }
    }
}