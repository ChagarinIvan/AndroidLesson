package by.chagarin.androidlesson;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.icons.MaterialDrawerFont;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;


public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    //private DrawerLayout drawerLayout;
    private Drawer drawer;
    //private ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);

//        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
//        left_drawer = (ListView) findViewById(R.id.left_drawer);
//
        String[] navigationData = new String[]{
                getString(R.string.transactions),
                getString(R.string.categores),
                getString(R.string.statistics)};
//        ArrayAdapter<String> navigationDrawerAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, navigationData);
//        left_drawer.setAdapter(navigationDrawerAdapter);
//        left_drawer.setOnItemClickListener(new DrawerItemClickListener());

        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        drawer = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withActionBarDrawerToggle(true)
                .withActionBarDrawerToggleAnimated(true)
                .withHeader(R.layout.drawer_header)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.transactions).withIcon(MaterialDrawerFont.Icon.mdf_arrow_drop_down),
                        new PrimaryDrawerItem().withName(R.string.categores).withIcon(MaterialDrawerFont.Icon.mdf_arrow_drop_down),
                        new PrimaryDrawerItem().withName(R.string.statistics).withIcon(MaterialDrawerFont.Icon.mdf_arrow_drop_down)
                )
                .withOnDrawerItemClickListener(new DrawerItemClickListener())
                .withOnDrawerListener(new Drawer.OnDrawerListener() {
                    @Override
                    public void onDrawerOpened(View drawerView) {
                        InputMethodManager inputMethodManager = (InputMethodManager) MainActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(MainActivity.this.getCurrentFocus().getWindowToken(), 0);
                    }

                    @Override
                    public void onDrawerClosed(View drawerView) {
                    }

                    @Override
                    public void onDrawerSlide(View drawerView, float slideOffset) {

                    }
                })
                .build();
//        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.app_name, R.string.app_name);
//        drawerLayout.setDrawerListener(drawerToggle);
        setFragment(0, R.string.transactions, new TransactionsFragment());
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen()) {
            drawer.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
    }

//    @Override
//    protected void onPostCreate(Bundle savedInstanceState) {
//        super.onPostCreate(savedInstanceState);
//        drawerToggle.syncState();
//    }

    public void setFragment(int position, int title, Fragment fragment) {
        drawer.closeDrawer();
        setTitle(getString(title));
        getFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
    }

    private class DrawerItemClickListener implements Drawer.OnDrawerItemClickListener {
        @Override
        public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
            switch (position) {
                case 1:
                    setFragment(position, R.string.transactions, new TransactionsFragment());
                    return true;
                case 2:
                    setFragment(position, R.string.categores, new CategoresFragment());
                    return true;
                case 3:
                    setFragment(position, R.string.statistics, new StatisticsFragment());
                    return true;
            }
            return false;
        }
    }
}