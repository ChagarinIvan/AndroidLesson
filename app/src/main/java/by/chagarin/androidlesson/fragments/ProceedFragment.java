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

import by.chagarin.androidlesson.AddProccedActivity_;
import by.chagarin.androidlesson.DataLoader;
import by.chagarin.androidlesson.KindOfCategories;
import by.chagarin.androidlesson.R;
import by.chagarin.androidlesson.objects.Category;
import by.chagarin.androidlesson.objects.Proceed;
import by.chagarin.androidlesson.viewholders.ProceedViewHolder;

import static by.chagarin.androidlesson.DataLoader.PROCEEDS;

@EFragment(R.layout.fragment_proceeds)
@OptionsMenu(R.menu.menu_transactions)
public class ProceedFragment extends Fragment {

    private static final String TIMER_NAME = "query_timer";
    private Proceed lastProcced;

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
    private DatabaseReference mDatabase;
    private LinearLayoutManager mManager;
    private FirebaseRecyclerAdapter<Proceed, ProceedViewHolder> mAdapter;
    private List<Proceed> proceeds;


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
        Query postsQuery = loader.getQuery(mDatabase, PROCEEDS);
        mAdapter = new FirebaseRecyclerAdapter<Proceed, ProceedViewHolder>(Proceed.class, R.layout.list_item,
                ProceedViewHolder.class, postsQuery) {
            @Override
            protected void populateViewHolder(final ProceedViewHolder viewHolder, final Proceed model, final int position) {
                viewHolder.bindToProceed(model);
            }
        };
        mRecycler.setAdapter(mAdapter);
        fab.attachToRecyclerView(mRecycler);
    }

    @Click
    void fabClicked() {
        proceeds = loader.getProceedes();
        if (proceeds.size() != 0) {
            lastProcced = proceeds.get(0);
        } else {
            lastProcced = new Proceed(
                    "ЗАРПЛАТА",
                    "1239",
                    new Date(),
                    "наконецто дали денежку!!!",
                    new Category(getString(R.string.hint_category_exemple), KindOfCategories.getPlace()),
                    new Category(getString(R.string.hint_category_exemple), KindOfCategories.getProceed()));
        }
        Intent intent = new Intent(getActivity(), AddProccedActivity_.class);
        intent.putExtra(Proceed.class.getCanonicalName(), lastProcced);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.from_midle, R.anim.in_midle);
    }
}

