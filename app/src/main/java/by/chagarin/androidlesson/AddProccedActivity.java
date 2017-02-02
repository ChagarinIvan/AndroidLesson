package by.chagarin.androidlesson;

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
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.TextRes;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import by.chagarin.androidlesson.objects.Category;
import by.chagarin.androidlesson.objects.Proceed;

import static by.chagarin.androidlesson.objects.Transaction.df;

@EActivity(R.layout.activity_add_proceed)
public class AddProccedActivity extends ActionBarActivity implements DatePickerDialog.OnDateSetListener {

    @ViewById
    Toolbar toolbar;

    @ViewById
    Button addButton;

    @Bean
    DataLoader loader;

    @ViewById
    EditText title, sum, comment;

    @ViewById
    TextView dateText;

    @ViewById
    Spinner spinnerProceed;

    @ViewById
    Spinner spinnerPlace;

    @TextRes(R.string.add_preceed)
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
        List<Category> data = loader.getCategores();
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        //сртируем листы категорий, создаем адаптеры и присваиваеи их
        listCategoriesPlace = KindOfCategories.sortData(data, KindOfCategories.getPlace());
        listCategoriesProceed = KindOfCategories.sortData(data, KindOfCategories.getProceed());
        ArrayAdapter<String> adapterPlace = new ArrayAdapter<String>(getApplication(), R.layout.spinner_item, KindOfCategories.getStringArray(listCategoriesPlace));
        ArrayAdapter<String> adapterProceed = new ArrayAdapter<String>(getApplication(), R.layout.spinner_item, KindOfCategories.getStringArray(listCategoriesProceed));
        spinnerPlace.setAdapter(adapterPlace);
        spinnerProceed.setAdapter(adapterProceed);

        setTitle(name);
        sum.setHint(proceed.getPrice());
        title.setHint(proceed.getTitle());
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
