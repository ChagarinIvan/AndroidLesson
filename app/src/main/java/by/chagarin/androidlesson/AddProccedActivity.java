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

import static by.chagarin.androidlesson.Transaction.df;

@EActivity(R.layout.activity_add_proceed)
public class AddProccedActivity extends ActionBarActivity implements DatePickerDialog.OnDateSetListener {

    @ViewById
    Toolbar toolbar;

    @ViewById
    Button addButton;

    @ViewById
    EditText title, sum, comment;

    @ViewById
    TextView dateText;

    @ViewById
    Spinner spinnerProceed;

    @ViewById
    Spinner spinnerPlace;

    @TextRes(R.string.add_transaction)
    CharSequence name;

    private Proceed proceed;
    private List<Category> listCategoriesPlace;
    private List<Category> listCategoriesProceed;
    private Date date;
    private DatePickerDialog dpd;

    @AfterViews
    void ready() {
        Calendar now = Calendar.getInstance();
        dpd = DatePickerDialog.newInstance(
                AddProccedActivity.this,
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
        proceed = (Proceed) getIntent().getParcelableExtra(
                Proceed.class.getCanonicalName());
    }

    @AfterViews
    void afterCreate() {
        loadCategoryData();
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        setTitle(name);
        sum.setHint(proceed.getPrice());
        title.setHint(proceed.getTitle());
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
                listCategoriesPlace = KindOfCategories.sortData(data, KindOfCategories.getPlace());
                listCategoriesProceed = KindOfCategories.sortData(data, KindOfCategories.getProceed());
                ArrayAdapter<String> adapterPlace = new ArrayAdapter<String>(getApplication(), R.layout.spinner_item, getStringArray(listCategoriesPlace));
                ArrayAdapter<String> adapterProceed = new ArrayAdapter<String>(getApplication(), R.layout.spinner_item, getStringArray(listCategoriesProceed));
                spinnerPlace.setAdapter(adapterPlace);
                spinnerProceed.setAdapter(adapterProceed);
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
            Category categoryPlace = listCategoriesPlace.get(spinnerPlace.getSelectedItemPosition());
            Category categoryProceed = listCategoriesProceed.get(spinnerProceed.getSelectedItemPosition());
            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(price)) {
                Toast.makeText(this, getString(R.string.warning_null), Toast.LENGTH_LONG).show();
                addButton.setEnabled(false);
            } else {
                if (date == null) {
                    dpd.show(getFragmentManager(), "Datepickerdialog");
                } else {
                    Float.parseFloat(price);
                    new Proceed(name, price, date, description, categoryPlace, categoryProceed).save();
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
