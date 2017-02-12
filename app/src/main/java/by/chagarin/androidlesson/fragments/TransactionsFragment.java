package by.chagarin.androidlesson.fragments;

import android.app.Fragment;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
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
import by.chagarin.androidlesson.objects.Base;
import by.chagarin.androidlesson.objects.BaseListeners;
import by.chagarin.androidlesson.objects.Category;
import by.chagarin.androidlesson.objects.Transaction;
import by.chagarin.androidlesson.viewholders.TransactionViewHolder;

import static by.chagarin.androidlesson.DataLoader.TRANSACTIONS;

@EFragment(R.layout.fragment_transactions)
@OptionsMenu(R.menu.menu_transactions)
//создаём файл меню для фрагмента
//android:showAsAction="ifRoom" значит, что элемент если помещается, то будет в тулбаре, иначе поместиться в "три точки"
public class TransactionsFragment extends Fragment implements BaseListeners {

    @OptionsMenuItem(R.id.cash)
    MenuItem cash;

    @OptionsMenuItem
    MenuItem menuSearch;

    @ViewById(R.id.transactions_list)
    RecyclerView mRecycler;

    @ViewById(R.id.fab)
    FloatingActionButton fab;

    @ViewById
    SwipeRefreshLayout swipeLayout;

    @Bean
    DataLoader loader;

    @Bean
    Base base;

    private FirebaseRecyclerAdapter<Transaction, TransactionViewHolder> mAdapter;

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        final SearchView searchView = (SearchView) menuSearch.getActionView();
        //делаем хинт из хмл
        final SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        // [END create_database_reference]
        mRecycler.setHasFixedSize(true);

        // Set up Layout Manager, reverse layout
        LinearLayoutManager mManager = new LinearLayoutManager(getActivity());
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        mRecycler.setLayoutManager(mManager);
        // Set up FirebaseRecyclerAdapter with the Query
        final Query postsQuery = loader.getQuery(mDatabase, TRANSACTIONS);
        //адаптер fireBase
        mAdapter = new FirebaseRecyclerAdapter<Transaction, TransactionViewHolder>(Transaction.class, R.layout.list_item,
                TransactionViewHolder.class, postsQuery) {
            @Override
            protected void populateViewHolder(final TransactionViewHolder viewHolder, final Transaction model, final int position) {
                viewHolder.bindToTransaction(model);
                cash.setTitle(loader.getCashCount());

            }
        };
        //слушатель нажатий на остаток кэша
        cash.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                getFragmentManager().beginTransaction().replace(R.id.content_frame, CashStatisticsFragment_.builder().build()).commit();
                return true;
            }
        });
        //свайп слушатель
        final ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
                postsQuery.addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                int position = viewHolder.getAdapterPosition();
                                DatabaseReference ref = mAdapter.getRef(position);
                                ref.removeValue();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(mRecycler);
        mRecycler.setAdapter(mAdapter);
        fab.attachToRecyclerView(mRecycler);

    }

    @AfterViews
    void ready() {
        base.addListener(this);
    }

    @Click
    void fabClicked() {
        List<Transaction> transactions = loader.getTransactions();
        Transaction lastTransaction;
        if (transactions.size() != 0) {
            lastTransaction = transactions.get(0);
        } else {
            lastTransaction = new Transaction(
                    "ВОЙНА и МИР",
                    "300.0",
                    Transaction.df.format(new Date()),
                    "коментарий к трате",
                    new Category(getString(R.string.hint_category_exemple), KindOfCategories.getPlace(), "", ""),
                    new Category(getString(R.string.hint_category_exemple), KindOfCategories.getProceed(), "", ""),
                    "",
                    "");
        }
        Intent intent = new Intent(getActivity(), AddTransactionActivity_.class);
        intent.putExtra(Transaction.class.getCanonicalName(), lastTransaction);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.from_midle, R.anim.in_midle);
    }

    @Override
    public void doEvent() {

    }

    @Override
    public void removeElement() {
        cash.setTitle(loader.getCashCount());
    }
}
