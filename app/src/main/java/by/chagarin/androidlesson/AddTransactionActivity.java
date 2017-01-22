package by.chagarin.androidlesson;

import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Loader;
import android.net.ParseException;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.activeandroid.query.Select;

import org.androidannotations.annotations.AfterTextChange;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.TextRes;

import java.util.ArrayList;
import java.util.List;

@EActivity(R.layout.activity_add_transaction)
public class AddTransactionActivity extends ActionBarActivity {

    @ViewById
    Toolbar toolbar;

    @ViewById
    Button addButton;

    @ViewById
    EditText title, sum;

    @ViewById
    Spinner spinner;

    @TextRes(R.string.add_transaction)
    CharSequence name;

    private Transaction transction;
    private List<Category> listCategories;


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


    /**
     * собственно метод загрузчик из БД
     *
     * @return
     */
    private List<Category> getDataList() {
        return new Select()
                .from(Category.class)
                .execute();
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
                        return getDataList();
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
                listCategories = data;
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplication(), R.layout.spinner_item, getStringArray(data));
                spinner.setAdapter(adapter);
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
            String title = this.title.getText().toString();
            String price = sum.getText().toString();
            Category category = listCategories.get(spinner.getSelectedItemPosition());
            if (title.equals("") || price.equals("")) {
                Toast.makeText(this, getString(R.string.warning_null), Toast.LENGTH_LONG).show();
                addButton.setEnabled(false);
            } else {

                Integer.parseInt(price);
                new Transaction(title, price, category).save();
                finish();
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


    private class CategoryAdapter<T> {
    }
}
