package by.chagarin.androidlesson.fragments;

import android.app.Fragment;
import android.app.SearchManager;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.github.androidprogresslayout.ProgressLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.OptionsMenuItem;
import org.androidannotations.annotations.ViewById;

import by.chagarin.androidlesson.ColorRandom;
import by.chagarin.androidlesson.DataLoader;
import by.chagarin.androidlesson.MainActivity;
import by.chagarin.androidlesson.R;
import by.chagarin.androidlesson.objects.Transfer;
import by.chagarin.androidlesson.viewholders.TransferViewHolder;

import static by.chagarin.androidlesson.DataLoader.ACTIONS;
import static by.chagarin.androidlesson.DataLoader.TRANSFERS;

@EFragment(R.layout.fragment_transfer)
@OptionsMenu(R.menu.menu_transactions)
public class TransferFragment extends Fragment {

    @OptionsMenuItem(R.id.cash)
    MenuItem cash;

    @OptionsMenuItem
    MenuItem menuSearch;

    @ViewById(R.id.transfer_list)
    RecyclerView mRecycler;

    @Bean
    DataLoader loader;

    @ViewById(R.id.progress_layout)
    ProgressLayout progressLayout;

    @Bean
    ColorRandom colorRandom;
    private FirebaseRecyclerAdapter<Transfer, TransferViewHolder> mAdapter;
    private ValueEventListener valueEventListener;


    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        final MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.actualFragment = this;
        mainActivity.setTitle(R.string.transfer);
        this.getView().setBackgroundColor(getResources().getColor(colorRandom.getRandomColor()));
        super.onPrepareOptionsMenu(menu);
        final SearchView searchView = (SearchView) menuSearch.getActionView();
        //делаем хинт из хмл
        final SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        // [END create_database_reference]
        mRecycler.setHasFixedSize(true);

        // Set up Layout Manager, reverse layout
        LinearLayoutManager mManager = new LinearLayoutManager(getActivity());
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        mRecycler.setLayoutManager(mManager);
        // Set up FirebaseRecyclerAdapter with the Query
        final Query postsQuery = loader.getQuery(ACTIONS + TRANSFERS);
        //адаптер fireBase
        mAdapter = new FirebaseRecyclerAdapter<Transfer, TransferViewHolder>(Transfer.class, R.layout.transfer_item,
                TransferViewHolder.class, postsQuery) {
            @Override
            protected void populateViewHolder(TransferViewHolder viewHolder, Transfer model, int position) {
                viewHolder.bindToTransfer(model);
                progressLayout.showContent();
            }
        };
        //слушатель нажатий на остаток кэша
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                loader.calcAndSetCash(cash, true);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        loader.mDatabase.addValueEventListener(valueEventListener);
        final Fragment fragment = this;
        cash.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                mainActivity.parentFragment = fragment;
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
                                new MaterialDialog.Builder(getActivity())
                                        .title(R.string.delet_dialog)
                                        .positiveText(R.string.ok_button)
                                        .negativeText(R.string.cancel_button)
                                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                                            @Override
                                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                int position = viewHolder.getAdapterPosition();
                                                DatabaseReference ref = mAdapter.getRef(position);
                                                ref.removeValue();
                                            }
                                        })
                                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                                            @Override
                                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                dialog.dismiss();
                                                mRecycler.setAdapter(mAdapter);
                                            }
                                        }).show();
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
    }
}
