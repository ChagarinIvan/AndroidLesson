package by.chagarin.androidlesson;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mikepenz.crossfader.Crossfader;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.MiniDrawer;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialize.util.UIUtils;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import by.chagarin.androidlesson.fragments.CategoresFragment_;
import by.chagarin.androidlesson.fragments.Chat_;
import by.chagarin.androidlesson.fragments.ProceedFragment_;
import by.chagarin.androidlesson.fragments.StatisticsFragment_;
import by.chagarin.androidlesson.fragments.TransactionsFragment_;

public class MainActivity extends AppCompatActivity {

    private FirebaseUser mFirebaseUser;
    private MainActivity activity;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //add the values which need to be saved from the drawer to the bundle
        if (result != null) {
            outState = result.saveInstanceState(outState);
            //add the values which need to be saved from the accountHeader to the bundle
            outState = headerResult.saveInstanceState(outState);
            //add the values which need to be saved from the crossFader to the bundle
            outState = crossFader.saveInstanceState(outState);
            super.onSaveInstanceState(outState);
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
    }

    //
    public void setFragment(int position, int title, Fragment fragment) {
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
                    setFragment(position, R.string.transactions, TransactionsFragment_.builder().build());
                    return true;
                case 2:
                    result.setSelection(2);
                    setFragment(position, R.string.add, ProceedFragment_.builder().build());
                    return true;
                case 3:
                    result.setSelection(3);
                    setFragment(position, R.string.categores, CategoresFragment_.builder().build());
                    return true;
                case 4:
                    result.setSelection(4);
                    setFragment(position, R.string.statistics, StatisticsFragment_.builder().build());
                    return true;
                case 5:
                    result.setSelection(4);
                    setFragment(position, R.string.chat, Chat_.builder().build());
                    return true;
            }
            return false;
        }
    }

    private static final int PROFILE_SETTING = 1;

    //save our header or result
    private AccountHeader headerResult = null;
    private Drawer result = null;
    private MiniDrawer miniResult = null;
    private Crossfader crossFader;


    private Bundle savedInstanceState;
    private Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            IProfile profile = new ProfileDrawerItem().withName(mFirebaseUser.getDisplayName()).withIcon(bitmap);

            //Remove line to test RTL support
            // getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

            //example how to implement a persistentDrawer as shown in the google material design guidelines
            //https://material-design.storage.googleapis.com/publish/material_v_4/material_ext_publish/0Bx4BSt6jniD7YVdKQlF3TEo2S3M/patterns_navdrawer_behavior_persistent2.png
            //https://www.google.com/design/spec/patterns/navigation-drawer.html#navigation-drawer-behavior

            // Handle Toolbar
            final Toolbar toolbar = (Toolbar) activity.findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            //getSupportActionBar().setTitle(R.string.drawer_item_persistent_compact_header);

            // Create the AccountHeader
            headerResult = new AccountHeaderBuilder()
                    .withActivity(activity)
                    .withCompactStyle(true)
                    .withTranslucentStatusBar(true)
                    .withSelectionListEnabledForSingleProfile(false)
                    .withProfileImagesClickable(false)
                    .withHeaderBackground(R.drawable.drawer_header)
                    .withHeightPx(UIUtils.getActionBarHeight(activity))
                    .withAccountHeader(R.layout.material_drawer_compact_persistent_header)
                    .withTextColor(Color.BLACK)
                    .addProfiles(profile)
                    .build();

            //Create the drawer
            result = new DrawerBuilder()
                    .withActivity(activity)
                    .withTranslucentStatusBar(true)
                    .withAccountHeader(headerResult) //set the AccountHeader we created earlier for the header
                    .addDrawerItems(
                            new PrimaryDrawerItem().withName(R.string.transactions).withIcon(FontAwesome.Icon.faw_shopping_cart),
                            new PrimaryDrawerItem().withName(R.string.add).withIcon(FontAwesome.Icon.faw_download),
                            new PrimaryDrawerItem().withName(R.string.categores).withIcon(FontAwesome.Icon.faw_tags),
                            new PrimaryDrawerItem().withName(R.string.statistics).withIcon(FontAwesome.Icon.faw_area_chart),
                            new PrimaryDrawerItem().withName(R.string.chat).withIcon(FontAwesome.Icon.faw_chain_broken)
                    )
                    .withGenerateMiniDrawer(true)
                    .withOnDrawerItemClickListener(new DrawerItemClickListener())
                    .withOnDrawerListener(new Drawer.OnDrawerListener() {
                        @Override
                        public void onDrawerOpened(View drawerView) {
                            InputMethodManager inputMethodManager = (InputMethodManager) MainActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                            //noinspection ConstantConditions
                            inputMethodManager.hideSoftInputFromWindow(MainActivity.this.getCurrentFocus().getWindowToken(), 0);
                        }

                        @Override
                        public void onDrawerClosed(View drawerView) {
                        }

                        @Override
                        public void onDrawerSlide(View drawerView, float slideOffset) {

                        }
                    })
                    .buildView();

            // create the MiniDrawer and define the drawer and header to be used (it will automatically use the items from them)
            miniResult = result.getMiniDrawer().withIncludeSecondaryDrawerItems(true);

            //set the back arrow in the toolbar
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(false);

            //get the widths in px for the first and second panel
            int firstWidth = (int) com.mikepenz.crossfader.util.UIUtils.convertDpToPixel(300, activity);
            int secondWidth = (int) com.mikepenz.crossfader.util.UIUtils.convertDpToPixel(72, activity);

            //create and build our crossfader (see the MiniDrawer is also builded in here, as the build method returns the view to be used in the crossfader)
            crossFader = new Crossfader()
                    .withContent(findViewById(R.id.crossfade_content))
                    .withFirst(result.getSlider(), firstWidth)
                    .withSecond(miniResult.build(activity), secondWidth)
                    .build();

            //define the crossfader to be used with the miniDrawer. This is required to be able to automatically toggle open / close
            miniResult.withCrossFader(new CrossfadeWrapper(crossFader));

            //define and create the arrow ;)
            ImageView toggle = (ImageView) headerResult.getView().findViewById(R.id.material_drawer_account_header_toggle);
            //for RTL you would have to define the other arrow
            toggle.setImageDrawable(new IconicsDrawable(activity, GoogleMaterial.Icon.gmd_chevron_left).sizeDp(16).color(Color.BLACK));
            toggle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    crossFader.crossFade();
                }
            });
            setFragment(0, R.string.transactions, TransactionsFragment_.builder().build());
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
        }
    };

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
        activity = this;
        this.savedInstanceState = savedInstanceState;
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
}