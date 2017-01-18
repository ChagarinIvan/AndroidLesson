package by.chagarin.androidlesson;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.TextRes;

@EActivity(R.layout.activity_add_transaction)
public class AddTransactionActivity extends ActionBarActivity {

    @ViewById(R.id.toolbar)
    Toolbar toolbar;

    @TextRes(R.string.add_transaction)
    CharSequence title;

    @AfterViews
    void afterCreate() {
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @OptionsItem(R.id.home)
    void back() {
        onBackPressed();
        finish();
    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
    }
}
