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

import com.google.firebase.auth.FirebaseAuth;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import by.chagarin.androidlesson.objects.Category;
import by.chagarin.androidlesson.objects.Transaction;
import by.chagarin.androidlesson.objects.User;

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
    private String date;
    private DatePickerDialog dpd;

    @Bean
    DataLoader loader;

    private DatabaseReference mDatabase;

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
        transction = getIntent().getParcelableExtra(
                Transaction.class.getCanonicalName());
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
        listCategoriesTransactions = KindOfCategories.sortData(data, KindOfCategories.getTransaction());
        listCategoriesPlaces = KindOfCategories.sortData(data, KindOfCategories.getPlace());
        //создаём для каждого спинера свой адаптер и устанавливаем их
        ArrayAdapter<String> adapterTransactions = new ArrayAdapter<>(getApplication(), R.layout.spinner_item, getStringArray(listCategoriesTransactions));
        ArrayAdapter<String> adapterPlaces = new ArrayAdapter<>(getApplication(), R.layout.spinner_item, getStringArray(listCategoriesPlaces));
        spinnerTransaction.setAdapter(adapterTransactions);
        spinnerPlace.setAdapter(adapterPlaces);
        //
        setTitle(name);
        sum.setHint(transction.getPrice());
        title.setHint(transction.getTitle());
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private List<String> getStringArray(List<Category> listCategories) {
        List<String> list = new ArrayList<>();
        for (Category cat : listCategories) {
            list.add(cat.getName());
        }
        return list;
    }

    @OptionsItem(R.id.home)
    void back() {
        onBackPressed();
    }

    private String getUid() {
        //noinspection ConstantConditions
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
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
                                        createTransction = new Transaction(name, price, date, description, categoryTransaction, categoryPlace, userId, user.getEmail());
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
        this.date = Transaction.df.format(calendar.getTime());
        dateText.setText(date);
    }
}
