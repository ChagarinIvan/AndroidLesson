package by.chagarin.androidlesson.fragments;


import android.app.Fragment;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;

import com.melnykov.fab.FloatingActionButton;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.OptionsMenuItem;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.api.BackgroundExecutor;

import java.util.Date;
import java.util.List;

import by.chagarin.androidlesson.AddProccedActivity_;
import by.chagarin.androidlesson.DataLoader;
import by.chagarin.androidlesson.KindOfCategories;
import by.chagarin.androidlesson.R;
import by.chagarin.androidlesson.adapters.ProceedAdapter;
import by.chagarin.androidlesson.objects.Category;
import by.chagarin.androidlesson.objects.Proceed;

@EFragment(R.layout.fragment_proceeds)
@OptionsMenu(R.menu.menu_transactions)
public class ProceedFragment extends Fragment {

    private static final String TIMER_NAME = "query_timer";
    private ProceedAdapter proceedAdapter;
    private ActionModeCallback actionModeCallback = new ActionModeCallback();
    private ActionMode actionMode;
    private Proceed lastProcced;

    @OptionsMenuItem
    MenuItem menuSearch;

    @OptionsMenuItem
    MenuItem cash;

    @ViewById(R.id.proceeds_list)
    RecyclerView recyclerView;

    @ViewById(R.id.fab)
    FloatingActionButton fab;

    //резинка от трусов
    @ViewById
    SwipeRefreshLayout swipeLayout;

    @Bean
    DataLoader loader;

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        final SearchView searchView = (SearchView) menuSearch.getActionView();
        //делаем хинт из хмл
        final SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                BackgroundExecutor.cancelAll(TIMER_NAME, true);
                filterDelayed(newText);
                return false;
            }
        });
    }

    @Background(delay = 300, id = TIMER_NAME)
    void filterDelayed(String newText) {
        loadData();
    }

    @AfterViews
    void ready() {
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        fab.attachToRecyclerView(recyclerView);
        //настраиваем цветавую схесу свайпа и устанавливаем слушателя
        swipeLayout.setColorSchemeColors(R.color.green, R.color.orange, R.color.blue);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData();
            }
        });

        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                proceedAdapter.removeItem(viewHolder.getAdapterPosition());
                loadData();
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    public void onTaskFinished() {
        List<Proceed> proceedList = loader.getProceedesWithoutSystem();
        swipeLayout.setRefreshing(false);
        proceedAdapter = new ProceedAdapter(proceedList, getActivity(), new ProceedAdapter.CardViewHolder.ClickListener() {
            @Override
            public void onItemClick(int position) {
                if (actionMode != null) {
                    toggleSelection(position);
                }
            }

            @Override
            public boolean onItemLongClick(int position) {
                if (actionMode == null) {
                    ActionBarActivity activity = (ActionBarActivity) getActivity();
                    actionMode = activity.startSupportActionMode(actionModeCallback);
                }
                toggleSelection(position);
                return true;
            }
        });
        recyclerView.setAdapter(proceedAdapter);
        if (proceedList.size() != 0) {
            lastProcced = proceedList.get(0);
        } else {
            lastProcced = new Proceed(
                    getString(R.string.hint_title_example),
                    getString(R.string.hint_price_example),
                    new Date(), "",
                    new Category(getString(R.string.hint_category_exemple), KindOfCategories.getPlace()),
                    new Category(getString(R.string.hint_category_exemple), KindOfCategories.getProceed()));
        }
        cash.setTitle("Хуй"/*loader.calcCash()*/);
        cash.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                getFragmentManager().beginTransaction().replace(R.id.content_frame, CashStatisticsFragment_.builder().build()).commit();
                return true;
            }
        });
    }

    @Click
    void fabClicked() {
        Intent intent = new Intent(getActivity(), AddProccedActivity_.class);
        intent.putExtra(Proceed.class.getCanonicalName(), lastProcced);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.from_midle, R.anim.in_midle);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }

    private void toggleSelection(int position) {
        proceedAdapter.togglePosition(position);
        int count = proceedAdapter.getSelectedItemsCount();
        if (count == 0) {
            actionMode.finish();
        } else {
            actionMode.setTitle(String.valueOf(count));
            actionMode.invalidate();
        }
    }


    /**
     * метод с помощью асинхронного загрузчика в доп потоке загружает данные из БД
     *
     */
    private void loadData() {
        loader.loadData();
    }

    /**
     * берёт данные из БД с сортировкой от большего к меньшему
     * @return
     */


    /**
     * исспользуеться для создания актив мода
     * тут описывается поведение акшн мода
     */
    private class ActionModeCallback implements ActionMode.Callback {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.contextual_bar, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.menu_remove:
                    proceedAdapter.removeItems(proceedAdapter.getSelectedItem());
                    mode.finish();
                    loadData();
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            proceedAdapter.clearSelection();
            actionMode = null;
        }
    }
}

