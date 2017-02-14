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

import com.google.firebase.database.ChildEventListener;
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
import by.chagarin.androidlesson.objects.Transaction;
import by.chagarin.androidlesson.objects.User;

import static by.chagarin.androidlesson.DataLoader.TRANSACTIONS;

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
    private Transaction createTransction;
    private List<Category> listCategoriesTransactions;
    private List<Category> listCategoriesPlaces;
    private List<Category> data;
    private String date;
    private DatePickerDialog dpd;

    @Bean
    DataLoader loader;

    @AfterViews
    void ready() {
        data = new ArrayList<>();
        loader.mDatabase.child(DataLoader.CATEGORIES).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String key = dataSnapshot.getKey();
                Category category = dataSnapshot.getValue(Category.class);
                category.key = key;
                data.add(category);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        //отделяем только необходимые категории
        listCategoriesTransactions = KindOfCategories.sortData(data, KindOfCategories.getTransaction());
        listCategoriesPlaces = KindOfCategories.sortData(data, KindOfCategories.getPlace());
        //создаём для каждого спинера свой адаптер и устанавливаем их
        ArrayAdapter<String> adapterTransactions = new ArrayAdapter<>(getApplication(), R.layout.spinner_item, getStringArray(listCategoriesTransactions));
        ArrayAdapter<String> adapterPlaces = new ArrayAdapter<>(getApplication(), R.layout.spinner_item, getStringArray(listCategoriesPlaces));
        spinnerTransaction.setAdapter(adapterTransactions);
        spinnerPlace.setAdapter(adapterPlaces);
        //
        setTitle(name);
        //sum.setHint(transction.price);
        //title.setHint(transction.title);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private List<String> getStringArray(List<Category> listCategories) {
        List<String> list = new ArrayList<>();
        for (Category cat : listCategories) {
            list.add(cat.name);
        }
        return list;
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
            final Category categoryTransaction = listCategoriesTransactions.get(spinnerTransaction.getSelectedItemPosition());
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
                    loader.mDatabase.child(DataLoader.USERS).child(userId).addListenerForSingleValueEvent(
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
                                        String key = loader.mDatabase.child(TRANSACTIONS).push().getKey();
                                        createTransction = new Transaction(name, price, date, description, categoryTransaction.key, categoryPlace.key, userId, key);
                                        loader.writeNewTransaction(createTransction);
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
        this.date = DataLoader.df.format(calendar.getTime());
        dateText.setText(date);
    }
}
