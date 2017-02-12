package by.chagarin.androidlesson.fragments;


import android.app.Fragment;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;

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

import by.chagarin.androidlesson.AddProccedActivity_;
import by.chagarin.androidlesson.DataLoader;
import by.chagarin.androidlesson.KindOfCategories;
import by.chagarin.androidlesson.R;
import by.chagarin.androidlesson.objects.Base;
import by.chagarin.androidlesson.objects.BaseListeners;
import by.chagarin.androidlesson.objects.Category;
import by.chagarin.androidlesson.objects.Proceed;
import by.chagarin.androidlesson.viewholders.ProceedViewHolder;

import static by.chagarin.androidlesson.DataLoader.PROCEEDS;

@EFragment(R.layout.fragment_proceeds)
@OptionsMenu(R.menu.menu_transactions)
public class ProceedFragment extends Fragment implements BaseListeners {

    @OptionsMenuItem
    MenuItem menuSearch;

    @OptionsMenuItem
    MenuItem cash;

    @ViewById(R.id.proceeds_list)
    RecyclerView mRecycler;

    @ViewById(R.id.fab)
    FloatingActionButton fab;

    @Bean
    DataLoader loader;

    @ViewById
    SwipeRefreshLayout swipeLayout;

    @Bean
    Base base;

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        // [END create_database_reference]
        mRecycler.setHasFixedSize(true);
        // Set up Layout Manager, reverse layout
        LinearLayoutManager mManager = new LinearLayoutManager(getActivity());
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        mRecycler.setLayoutManager(mManager);
        // Set up FirebaseRecyclerAdapter with the Query
        final Query postsQuery = loader.getQuery(mDatabase, PROCEEDS);
        //адаптер БД
        final FirebaseRecyclerAdapter<Proceed, ProceedViewHolder> mAdapter = new FirebaseRecyclerAdapter<Proceed, ProceedViewHolder>(Proceed.class, R.layout.list_item,
                ProceedViewHolder.class, postsQuery) {
            @Override
            protected void populateViewHolder(final ProceedViewHolder viewHolder, final Proceed model, final int position) {
                viewHolder.bindToProceed(model);
                cash.setTitle(loader.getCashCount());
            }
        };
        //слушатель кэша
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
        List<Proceed> proceeds = loader.getProceedes();
        Proceed lastProcced;
        if (proceeds.size() != 0) {
            lastProcced = proceeds.get(0);
        } else {
            lastProcced = new Proceed(
                    "ЗАРПЛАТА",
                    "1239",
                    Proceed.df.format(new Date()),
                    "наконецто дали денежку!!!",
                    new Category(getString(R.string.hint_category_exemple), KindOfCategories.getPlace(), "", ""),
                    new Category(getString(R.string.hint_category_exemple), KindOfCategories.getProceed(), "", ""),
                    "",
                    "");
        }
        Intent intent = new Intent(getActivity(), AddProccedActivity_.class);
        intent.putExtra(Proceed.class.getCanonicalName(), lastProcced);
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

