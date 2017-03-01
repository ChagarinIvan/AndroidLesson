package by.chagarin.androidlesson;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.mikepenz.crossfadedrawerlayout.view.CrossfadeDrawerLayout;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.MiniDrawer;
import com.mikepenz.materialdrawer.interfaces.ICrossfader;
import com.mikepenz.materialdrawer.interfaces.OnCheckedChangeListener;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.MiniDrawerItem;
import com.mikepenz.materialdrawer.model.MiniProfileDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SwitchDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.util.DrawerUIUtils;
import com.mikepenz.materialize.util.UIUtils;
import com.mikepenz.octicons_typeface_library.Octicons;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import by.chagarin.androidlesson.fragments.CategoresFragment_;
import by.chagarin.androidlesson.fragments.Chat_;
import by.chagarin.androidlesson.fragments.ProceedFragment_;
import by.chagarin.androidlesson.fragments.StatisticsFragment_;
import by.chagarin.androidlesson.fragments.TransactionsFragment_;
import by.chagarin.androidlesson.objects.User;

public class MainActivity extends AppCompatActivity {

    private FirebaseUser mFirebaseUser;
    private MainActivity activity;
    private Bundle savedInstance;
    private CrossfadeDrawerLayout crossfadeDrawerLayout;

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
    public void setFragment(int title, Fragment fragment) {
        result.closeDrawer();
        setTitle(getString(title));
        getFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
    }

    //
    private class DrawerItemClickListener implements Drawer.OnDrawerItemClickListener {
        @Override
        public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
            switch (position) {
                case 1:
                    result.setSelection(1);
                    setFragment(R.string.transactions, TransactionsFragment_.builder().build());
                    return true;
                case 2:
                    result.setSelection(2);
                    setFragment(R.string.add, ProceedFragment_.builder().build());
                    return true;
                case 3:
                    result.setSelection(3);
                    setFragment(R.string.categores, CategoresFragment_.builder().build());
                    return true;
                case 4:
                    result.setSelection(4);
                    setFragment(R.string.statistics, StatisticsFragment_.builder().build());
                    return true;
                case 5:
                    result.setSelection(4);
                    setFragment(R.string.chat, Chat_.builder().build());
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
            profile = new ProfileDrawerItem().withName(mFirebaseUser.getDisplayName()).withIcon(bitmap);
            final String userId = DataLoader.getUid();
            loader.mDatabase.child(DataLoader.USERS).child(userId).addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            //удаляем элемент
                            user = dataSnapshot.getValue(User.class);
                            DataLoader.isShow = user.isShow;
                            startMainActivity();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
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
                .withDividerBelowHeader(true)
                .withCompactStyle(true)
                .build();

        //Create the drawer
        result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withHasStableIds(true)
                .withDrawerLayout(R.layout.crossfade_drawer)
                .withDrawerWidthDp(72)
                .withGenerateMiniDrawer(true)
                .withAccountHeader(headerResult) //set the AccountHeader we created earlier for the header
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.transactions).withIcon(FontAwesome.Icon.faw_shopping_cart),
                        new PrimaryDrawerItem().withName(R.string.add).withIcon(FontAwesome.Icon.faw_download),
                        new PrimaryDrawerItem().withName(R.string.categores).withIcon(FontAwesome.Icon.faw_tags),
                        new PrimaryDrawerItem().withName(R.string.statistics).withIcon(FontAwesome.Icon.faw_area_chart),
                        new PrimaryDrawerItem().withName(R.string.chat).withIcon(FontAwesome.Icon.faw_chain_broken),
                        new DividerDrawerItem(),
                        new SwitchDrawerItem().withName(R.string.swich).withIcon(Octicons.Icon.oct_tools).withChecked(user.isShow).withOnCheckedChangeListener(new SwitchListener())
                )// add the items we want to use with our Drawer
                .withOnDrawerItemClickListener(new DrawerItemClickListener())
                .withSavedInstance(savedInstance)
                .withShowDrawerOnFirstLaunch(true)
                .build();


        //get the CrossfadeDrawerLayout which will be used as alternative DrawerLayout for the Drawer
        //the CrossfadeDrawerLayout library can be found here: https://github.com/mikepenz/CrossfadeDrawerLayout
        crossfadeDrawerLayout = (CrossfadeDrawerLayout) result.getDrawerLayout();

        //define maxDrawerWidth
        crossfadeDrawerLayout.setMaxWidthPx(DrawerUIUtils.getOptimalDrawerWidth(this));
        //add second view (which is the miniDrawer)
        final MiniDrawer miniResult = result.getMiniDrawer();
        //build the view for the MiniDrawer
        View view = miniResult.build(this);
        //set the background of the MiniDrawer as this would be transparent
        view.setBackgroundColor(UIUtils.getThemeColorFromAttrOrRes(this, com.mikepenz.materialdrawer.R.attr.material_drawer_background, com.mikepenz.materialdrawer.R.color.material_drawer_background));
        //we do not have the MiniDrawer view during CrossfadeDrawerLayout creation so we will add it here
        crossfadeDrawerLayout.getSmallView().addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        //define the crossfader to be used with the miniDrawer. This is required to be able to automatically toggle open / close
        miniResult.withCrossFader(new ICrossfader() {
            @Override
            public void crossfade() {
                boolean isFaded = isCrossfaded();
                crossfadeDrawerLayout.crossfade(400);

                //only close the drawer if we were already faded and want to close it now
                if (isFaded) {
                    result.getDrawerLayout().closeDrawer(GravityCompat.START);
                }
            }

            @Override
            public boolean isCrossfaded() {
                return crossfadeDrawerLayout.isCrossfaded();
            }
        });


        /**
         * NOTE THIS IS A HIGHLY CUSTOM ANIMATION. USE CAREFULLY.
         * this animate the height of the profile to the height of the AccountHeader and
         * animates the height of the drawerItems to the normal drawerItems so the difference between Mini and normal Drawer is eliminated
         **/

        final double headerHeight = DrawerUIUtils.getOptimalDrawerWidth(this) * 9d / 16d;
        final double originalProfileHeight = UIUtils.convertDpToPixel(72, this);
        final double headerDifference = headerHeight - originalProfileHeight;
        final double originalItemHeight = UIUtils.convertDpToPixel(64, this);
        final double normalItemHeight = UIUtils.convertDpToPixel(48, this);
        final double itemDifference = originalItemHeight - normalItemHeight;
        crossfadeDrawerLayout.withCrossfadeListener(new CrossfadeDrawerLayout.CrossfadeListener() {
            @Override
            public void onCrossfade(View containerView, float currentSlidePercentage, int slideOffset) {
                for (int i = 0; i < miniResult.getAdapter().getItemCount(); i++) {
                    IDrawerItem drawerItem = miniResult.getAdapter().getItem(i);
                    if (drawerItem instanceof MiniProfileDrawerItem) {
                        MiniProfileDrawerItem mpdi = (MiniProfileDrawerItem) drawerItem;
                        mpdi.withCustomHeightPx((int) (originalProfileHeight + (headerDifference * currentSlidePercentage / 100)));
                    } else if (drawerItem instanceof MiniDrawerItem) {
                        MiniDrawerItem mdi = (MiniDrawerItem) drawerItem;
                        mdi.withCustomHeightPx((int) (originalItemHeight - (itemDifference * currentSlidePercentage / 100)));
                    }
                }
                miniResult.getAdapter().notifyDataSetChanged();
            }
        });
        setFragment(R.string.transactions, TransactionsFragment_.builder().build());
    }

    private void someMethod(Uri uri) {
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
        activity = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_persistent_drawer);

        FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if (mFirebaseUser == null) {
            // Not signed in, launch the Sign In activity
            startActivity(new Intent(this, SignInActivity_.class));
            finish();
            return;
        }
        loader = DataLoader_.getInstance_(this);
        someMethod(mFirebaseUser.getPhotoUrl());
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
            super.onBackPressed();
        }
    }

    private class SwitchListener implements OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(IDrawerItem drawerItem, CompoundButton buttonView, final boolean isChecked) {
            final String userId = DataLoader.getUid();
            loader.mDatabase.child(DataLoader.USERS).child(userId).addListenerForSingleValueEvent(
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