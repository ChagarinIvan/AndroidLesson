package by.chagarin.androidlesson;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;


public class MainActivity extends ActionBarActivity {

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ListView left_drawer;
    private ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        left_drawer = (ListView) findViewById(R.id.left_drawer);

//        String[] navigationData = getResources().getStringArray(R.array.screen_aray);
        String[] navigationData = new String[]{
                getString(R.string.transactions),
                getString(R.string.categores),
                getString(R.string.statistics)};
        ArrayAdapter<String> navigationDrawerAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, navigationData);
        left_drawer.setAdapter(navigationDrawerAdapter);
        left_drawer.setOnItemClickListener(new DrawerItemClickListener());


        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.app_name, R.string.app_name);
        drawerLayout.setDrawerListener(drawerToggle);
        setFragment(0, R.string.transactions, new TransactionsFragment());
    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    public void setFragment(int position, int title, Fragment fragment) {
        left_drawer.setItemChecked(position, true);
        drawerLayout.closeDrawer(left_drawer);
        setTitle(getString(title));
        getFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            switch (position) {
                case 0:
                    setFragment(position, R.string.transactions, new TransactionsFragment());
                    break;
                case 1:
                    setFragment(position, R.string.categores, new CategoresFragment());
                    break;
                case 2:
                    setFragment(position, R.string.statistics, new StatisticsFragment());
                    break;
            }
        }
    }
}