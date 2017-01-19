package by.chagarin.androidlesson;

import android.net.ParseException;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.androidannotations.annotations.AfterTextChange;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.TextRes;

@EActivity(R.layout.activity_add_transaction)
public class AddTransactionActivity extends ActionBarActivity {

    @ViewById
    Toolbar toolbar;

    @ViewById
    Button addButton;

    @ViewById
    EditText title, sum;

    @TextRes(R.string.add_transaction)
    CharSequence name;

    private Transaction transction;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        transction = (Transaction) getIntent().getParcelableExtra(
                Transaction.class.getCanonicalName());
    }

    @AfterViews
    void afterCreate() {
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        setTitle(name);
        sum.setHint(transction.getPrice());
        title.setHint(transction.getTitle());
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @OptionsItem(R.id.home)
    void back() {
        onBackPressed();
    }

    @Click
    void addButton() {
        String title = this.title.getText().toString();
        String price = sum.getText().toString();
        if (title.equals("") || price.equals("")) {
            Toast.makeText(this, getString(R.string.warning_null), Toast.LENGTH_LONG).show();
            addButton.setEnabled(false);
        } else {
            try {
                Integer.parseInt(price);
                new Transaction(title, price).save();
                finish();
            } catch (ParseException e) {
                Toast.makeText(this, getString(R.string.warning_no_integer), Toast.LENGTH_LONG).show();
                addButton.setEnabled(false);
            }
        }
    }

    @AfterTextChange({R.id.title, R.id.sum})
    void afterTextChangedOnSomeTextViews() {
        addButton.setEnabled(true);
    }


}
