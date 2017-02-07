package by.chagarin.androidlesson.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.melnykov.fab.FloatingActionButton;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.OptionsMenuItem;
import org.androidannotations.annotations.ViewById;

import java.util.Date;
import java.util.List;

import by.chagarin.androidlesson.AddTransactionActivity_;
import by.chagarin.androidlesson.DataLoader;
import by.chagarin.androidlesson.KindOfCategories;
import by.chagarin.androidlesson.R;
import by.chagarin.androidlesson.objects.Category;
import by.chagarin.androidlesson.objects.Transaction;
import by.chagarin.androidlesson.viewholders.TransactionViewHolder;

import static by.chagarin.androidlesson.DataLoader.TRANSACTIONS;

@EFragment(R.layout.fragment_transactions)
@OptionsMenu(R.menu.menu_transactions)
//создаём файл меню для фрагмента
//android:showAsAction="ifRoom" значит, что элемент если помещается, то будет в тулбаре, иначе поместиться в "три точки"
public class TransactionsFragment extends Fragment {

    @OptionsMenuItem
    MenuItem cash;

    @ViewById(R.id.transactions_list)
    RecyclerView mRecycler;

    @ViewById(R.id.fab)
    FloatingActionButton fab;

    @Bean
    DataLoader loader;

    private DatabaseReference mDatabase;
    private LinearLayoutManager mManager;
    private FirebaseRecyclerAdapter<Transaction, TransactionViewHolder> mAdapter;
    private List<Transaction> transactions;
    private Transaction lastTransaction;

    @AfterViews
    void ready() {
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // [END create_database_reference]
        mRecycler.setHasFixedSize(true);

        // Set up Layout Manager, reverse layout
        mManager = new LinearLayoutManager(getActivity());
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        mRecycler.setLayoutManager(mManager);
        // Set up FirebaseRecyclerAdapter with the Query
        Query postsQuery = loader.getQuery(mDatabase, TRANSACTIONS);
        mAdapter = new FirebaseRecyclerAdapter<Transaction, TransactionViewHolder>(Transaction.class, R.layout.list_item,
                TransactionViewHolder.class, postsQuery) {
            @Override
            protected void populateViewHolder(final TransactionViewHolder viewHolder, final Transaction model, final int position) {
                viewHolder.bindToTransaction(model);
            }
        };
        mRecycler.setAdapter(mAdapter);
        fab.attachToRecyclerView(mRecycler);
    }

    @Click
    void fabClicked() {
        transactions = loader.getTransactions();
        if (transactions.size() != 0) {
            lastTransaction = transactions.get(0);
        } else {
            lastTransaction = new Transaction(
                    "ВОЙНА и МИР",
                    "300.0",
                    new Date(),
                    "коментарий к трате",
                    new Category(getString(R.string.hint_category_exemple), KindOfCategories.getPlace()),
                    new Category(getString(R.string.hint_category_exemple), KindOfCategories.getProceed()));
        }
        Intent intent = new Intent(getActivity(), AddTransactionActivity_.class);
        intent.putExtra(Transaction.class.getCanonicalName(), lastTransaction);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.from_midle, R.anim.in_midle);
    }

}
