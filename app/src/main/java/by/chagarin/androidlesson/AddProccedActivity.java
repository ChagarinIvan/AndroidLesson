package by.chagarin.androidlesson;

import android.content.Context;
import android.net.ParseException;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import by.chagarin.androidlesson.objects.Category;
import by.chagarin.androidlesson.objects.Proceed;
import by.chagarin.androidlesson.objects.User;

import static by.chagarin.androidlesson.DataLoader.PROCEEDS;
import static by.chagarin.androidlesson.KindOfCategories.getStringArray;

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
    private String date;
    private DatePickerDialog dpd;
    private DatabaseReference mDatabase;
    private List<Category> listCategoriesTransactions;
    private List<Category> listCategoriesPlaces;
    private Proceed createProceed;

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
        proceed = getIntent().getParcelableExtra(
                Proceed.class.getCanonicalName());
    }

    @AfterViews
    void afterCreate() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(addButton.getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        //получаем список категорий из лодера
        List<Category> data = loader.getCategores();
        //отделяем только необходимые категории
        listCategoriesTransactions = KindOfCategories.sortData(data, KindOfCategories.getProceed());
        listCategoriesPlaces = KindOfCategories.sortData(data, KindOfCategories.getPlace());
        //создаём для каждого спинера свой адаптер и устанавливаем их
        ArrayAdapter<String> adapterTransactions = new ArrayAdapter<>(getApplication(), R.layout.spinner_item, getStringArray(listCategoriesTransactions));
        ArrayAdapter<String> adapterPlaces = new ArrayAdapter<>(getApplication(), R.layout.spinner_item, getStringArray(listCategoriesPlaces));
        spinnerProceed.setAdapter(adapterTransactions);
        spinnerPlace.setAdapter(adapterPlaces);
        //
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
        try {
            final String name = title.getText().toString();
            final String price = sum.getText().toString();
            final String description = comment.getText().toString();
            final Category categoryTransaction = listCategoriesTransactions.get(spinnerProceed.getSelectedItemPosition());
            final Category categoryPlace = listCategoriesPlaces.get(spinnerPlace.getSelectedItemPosition());
            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(price)) {
                Toast.makeText(this, getString(R.string.warning_null), Toast.LENGTH_LONG).show();
                addButton.setEnabled(false);
            } else {
                if (date == null) {
                    dpd.show(getFragmentManager(), "Datepickerdialog");
                } else {
                    float v = Float.parseFloat(price);
                    final String userId = loader.getUid();
                    mDatabase.child("users").child(userId).addListenerForSingleValueEvent(
                            new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    // Get user value
                                    User user = dataSnapshot.getValue(User.class);

                                    // [START_EXCLUDE]
                                    if (user == null) {
                                        // User is null, error out
                                        Toast.makeText(getParent(), "Error: could not fetch user.", Toast.LENGTH_SHORT).show();
                                    } else {
                                        // Write new post
                                        createProceed = new Proceed(name, price, date, description, categoryTransaction, categoryPlace, userId, user.username);
                                        writeNewProceed(createProceed);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                }
                            });
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

    private void writeNewProceed(Proceed createProceed) {
        String key = mDatabase.child(PROCEEDS).push().getKey();
        Map<String, Object> postValues = createProceed.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/" + PROCEEDS + "/" + key, postValues);
        mDatabase.updateChildren(childUpdates);
    }



    @AfterTextChange({R.id.title, R.id.sum})
    void afterTextChangedOnSomeTextViews() {
        addButton.setEnabled(true);
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, monthOfYear, dayOfMonth);
        this.date = Proceed.df.format(calendar.getTime());
        dateText.setText(date);
    }
}
