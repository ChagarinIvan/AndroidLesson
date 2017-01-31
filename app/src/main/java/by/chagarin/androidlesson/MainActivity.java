package by.chagarin.androidlesson;

import android.app.Activity;
import android.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;

import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Receiver;
import org.androidannotations.annotations.ViewById;

import java.util.List;

import by.chagarin.androidlesson.auth.SessionManager;
import by.chagarin.androidlesson.fragments.CategoresFragment_;
import by.chagarin.androidlesson.fragments.ProceedFragment_;
import by.chagarin.androidlesson.fragments.StatisticsFragment_;
import by.chagarin.androidlesson.fragments.TransactionsFragment_;
import by.chagarin.androidlesson.objects.Proceed;
import by.chagarin.androidlesson.objects.Transaction;

@EActivity(R.layout.activity_main)
public class MainActivity extends ActionBarActivity {

    @ViewById
    Toolbar toolbar;

    private Drawer drawer;

    @Bean
    DataLoader loader;

    @Bean
    SessionManager sessionManager;
    private List<Proceed> listProceedes;
    private List<Transaction> listTransactions;
    private float cashCount;
    private LinearLayout linear;

    //регистрируем ресивер для приёма сообщений от Локал Бродкаст манагера из сессион манагера
    @Receiver(actions = {SessionManager.SESSION_OPEN_BROADCAST})
    void onSessionOpen() {

    }

    //проверяем аккаунт
    @Override
    protected void onResume() {
        super.onResume();
        sessionManager.login();
    }

    @AfterViews
    void afterCreate() {
//        linear = (LinearLayout) toolbar.findViewById(R.id.cash_layout);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        String login = "Chagarin_Ivan";
        String token = "213asdas32d1as5f4f4g3sg4f6s4ggd";
        sessionManager.createAccount(login, token);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        drawer = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withActionBarDrawerToggle(true)
                .withActionBarDrawerToggleAnimated(true)
                .withHeader(R.layout.drawer_header)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.transactions).withIcon(FontAwesome.Icon.faw_shopping_cart),
                        new PrimaryDrawerItem().withName(R.string.add).withIcon(FontAwesome.Icon.faw_download),
                        new PrimaryDrawerItem().withName(R.string.categores).withIcon(FontAwesome.Icon.faw_tags),
                        new PrimaryDrawerItem().withName(R.string.statistics).withIcon(FontAwesome.Icon.faw_area_chart)
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
        setFragment(0, R.string.transactions, TransactionsFragment_.builder().build());
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen()) {
            drawer.closeDrawer();
        } else {
            setFragment(0, R.string.transactions, TransactionsFragment_.builder().build());
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
    }

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
                    drawer.setSelection(1);
                    setFragment(position, R.string.add_transaction, TransactionsFragment_.builder().build());
                    return true;
                case 2:
                    drawer.setSelection(2);
                    setFragment(position, R.string.add, ProceedFragment_.builder().build());
                    return true;
                case 3:
                    drawer.setSelection(3);
                    setFragment(position, R.string.categores, CategoresFragment_.builder().build());
                    return true;
                case 4:
                    drawer.setSelection(4);
                    setFragment(position, R.string.statistics, StatisticsFragment_.builder().build());
                    return true;
            }
            return false;
        }
    }
}