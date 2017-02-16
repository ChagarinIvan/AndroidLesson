package by.chagarin.androidlesson;

import android.net.ParseException;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import by.chagarin.androidlesson.objects.Category;
import by.chagarin.androidlesson.objects.Proceed;
import by.chagarin.androidlesson.objects.User;

import static by.chagarin.androidlesson.DataLoader.PROCEEDS;
import static by.chagarin.androidlesson.DataLoader.df;
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

    private String date;
    private DatePickerDialog dpd;
    private List<Category> listCategoriesProceedes;
    private List<Category> listCategoriesPlaces;
    private Proceed createProceed;

    @AfterViews
    void ready() {
        loader.mDatabase.child(DataLoader.CATEGORIES).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Is better to use a List, because you don't know the size
                // of the iterator returned by dataSnapshot.getChildren() to
                // initialize the array
                final List<Category> categoryNames = new ArrayList<>();

                for (DataSnapshot areaSnapshot : dataSnapshot.getChildren()) {
                    Category categoryName = areaSnapshot.getValue(Category.class);
                    categoryName.key = areaSnapshot.getKey();
                    categoryNames.add(categoryName);
                }
                listCategoriesProceedes = KindOfCategories.sortData(categoryNames, KindOfCategories.getProceed());
                listCategoriesPlaces = KindOfCategories.sortData(categoryNames, KindOfCategories.getPlace());
                ArrayAdapter<String> adapterTransactions = new ArrayAdapter<>(getApplication(), R.layout.spinner_item, getStringArray(listCategoriesProceedes));
                ArrayAdapter<String> adapterPlaces = new ArrayAdapter<>(getApplication(), R.layout.spinner_item, getStringArray(listCategoriesPlaces));
                spinnerProceed.setAdapter(adapterTransactions);
                spinnerPlace.setAdapter(adapterPlaces);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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

        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        setTitle(name);
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
            final Category categoryProceed = listCategoriesProceedes.get(spinnerProceed.getSelectedItemPosition());
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
                    loader.mDatabase.child("users").child(userId).addListenerForSingleValueEvent(
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
                                        String key = loader.mDatabase.child(PROCEEDS).push().getKey();
                                        createProceed = new Proceed(name, price, date, description, categoryProceed.key, categoryPlace.key, userId, key);
                                        loader.writeNewProceed(createProceed);
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

    @AfterTextChange({R.id.title, R.id.sum})
    void afterTextChangedOnSomeTextViews() {
        addButton.setEnabled(true);
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, monthOfYear, dayOfMonth);
        this.date = df.format(calendar.getTime());
        dateText.setText(date);
    }
}
