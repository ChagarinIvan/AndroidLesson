package by.chagarin.androidlesson;

import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Loader;
import android.net.ParseException;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.androidannotations.annotations.AfterTextChange;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.TextRes;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import by.chagarin.androidlesson.objects.Category;
import by.chagarin.androidlesson.objects.Transaction;

import static by.chagarin.androidlesson.objects.Transaction.df;

@EActivity(R.layout.activity_add_transaction)
public class AddTransactionActivity extends ActionBarActivity implements DatePickerDialog.OnDateSetListener {

    @ViewById
    Toolbar toolbar;

    @ViewById
    Button addButton;

    @ViewById
    EditText title, sum, comment;

    @ViewById
    TextView dateText;

    @ViewById
    Spinner spinnerTransaction;

    @ViewById
    Spinner spinnerPlace;

    @TextRes(R.string.add_transaction)
    CharSequence name;

    private Transaction transction;
    private List<Category> listCategoriesTransactions;
    private List<Category> listCategoriesPlaces;
    private Date date;
    private DatePickerDialog dpd;

    @AfterViews
    void ready() {
        Calendar now = Calendar.getInstance();
        dpd = DatePickerDialog.newInstance(
                AddTransactionActivity.this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dpd.show(getFragmentManager(), "Datepickerdialog");
        dateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
        });
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        transction = (Transaction) getIntent().getParcelableExtra(
                Transaction.class.getCanonicalName());
    }

    @AfterViews
    void afterCreate() {
        loadCategoryData();
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        setTitle(name);
        sum.setHint(transction.getPrice());
        title.setHint(transction.getTitle());
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private List<String> getStringArray(List<Category> listCategories) {
        List<String> list = new ArrayList<String>();
        for (Category cat : listCategories) {
            list.add(cat.getName());
        }
        return list;
    }


    private void loadCategoryData() {
        getLoaderManager().restartLoader(0, null, new LoaderManager.LoaderCallbacks<List<Category>>() {
            /**
             * прозодит в бекграуде
             */
            @Override
            public Loader<List<Category>> onCreateLoader(int id, Bundle args) {
                final AsyncTaskLoader<List<Category>> loader = new AsyncTaskLoader<List<Category>>(getApplicationContext()) {
                    @Override
                    public List<Category> loadInBackground() {
                        return Category.getDataList();
                    }
                };
                //важно
                loader.forceLoad();
                return loader;
            }

            /**
             * в основном потоке после загрузки
             */
            @Override
            public void onLoadFinished(Loader<List<Category>> loader, List<Category> data) {
                listCategoriesTransactions = KindOfCategories.sortData(data, KindOfCategories.getTransaction());
                listCategoriesPlaces = KindOfCategories.sortData(data, KindOfCategories.getPlace());
                ArrayAdapter<String> adapterTransactions = new ArrayAdapter<String>(getApplication(), R.layout.spinner_item, getStringArray(listCategoriesTransactions));
                ArrayAdapter<String> adapterPlaces = new ArrayAdapter<String>(getApplication(), R.layout.spinner_item, getStringArray(listCategoriesPlaces));
                spinnerTransaction.setAdapter(adapterTransactions);
                spinnerPlace.setAdapter(adapterPlaces);
            }

            @Override
            public void onLoaderReset(Loader<List<Category>> loader) {

            }
        });
    }

    @OptionsItem(R.id.home)
    void back() {
        onBackPressed();
    }

    @Click
    void addButton() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        try {
            String name = title.getText().toString();
            String price = sum.getText().toString();
            String description = comment.getText().toString();
            Category categoryTransaction = listCategoriesTransactions.get(spinnerTransaction.getSelectedItemPosition());
            Category categoryPlace = listCategoriesPlaces.get(spinnerPlace.getSelectedItemPosition());
            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(price)) {
                Toast.makeText(this, getString(R.string.warning_null), Toast.LENGTH_LONG).show();
                addButton.setEnabled(false);
            } else {
                if (date == null) {
                    dpd.show(getFragmentManager(), "Datepickerdialog");
                } else {
                    Float.parseFloat(price);
                    new Transaction(name, price, date, description, categoryTransaction, categoryPlace).save();
                    finish();
                }
            }
        } catch (ParseException e) {
            Toast.makeText(this, getString(R.string.warning_no_integer), Toast.LENGTH_LONG).show();
            addButton.setEnabled(false);
        } catch (Exception e) {
            Toast.makeText(this, getString(R.string.warning_no_categories), Toast.LENGTH_LONG).show();
            addButton.setEnabled(false);
        }
    }

    @AfterTextChange({R.id.title, R.id.sum})
    void afterTextChangedOnSomeTextViews() {
        addButton.setEnabled(true);
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, monthOfYear, dayOfMonth);
        this.date = calendar.getTime();
        dateText.setText(df.format(date));
    }
}
