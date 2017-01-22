package by.chagarin.androidlesson;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
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
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.OptionsMenuItem;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.api.BackgroundExecutor;

import java.util.List;

import by.chagarin.androidlesson.adapters.TransactionAdapter;

@EFragment(R.layout.fragment_transactions)
@OptionsMenu(R.menu.menu_transactions)
//создаём файл меню для фрагмента
//android:showAsAction="ifRoom" значит, что элемент если помещается, то будет в тулбаре, иначе поместиться в "три точки"
public class TransactionsFragment extends Fragment {

    private static final String TIMER_NAME = "query_timer";
    private TransactionAdapter transactionAdapter;
    private ActionModeCallback actionModeCallback = new ActionModeCallback();
    private ActionMode actionMode;

    @OptionsMenuItem
    MenuItem menuSearch;

    @ViewById(R.id.transactions_list)
    RecyclerView recyclerView;

    @ViewById(R.id.fab)
    FloatingActionButton fab;

    //резинка от трусов
    @ViewById
    SwipeRefreshLayout swipeLayout;

    private Transaction lastTransaction;

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        final SearchView searchView = (SearchView) menuSearch.getActionView();
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
        loadData(newText);
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
                loadData("");
            }
        });


        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                transactionAdapter.removeItem(viewHolder.getAdapterPosition());
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }


    @Click
    void fabClicked() {
        Intent intent = new Intent(getActivity(), AddTransactionActivity_.class);
        intent.putExtra(Transaction.class.getCanonicalName(), lastTransaction);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.from_midle, R.anim.in_midle);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData("");
    }

    private void toggleSelection(int position) {
        transactionAdapter.togglePosition(position);
        int count = transactionAdapter.getSelectedItemsCount();
        if (count == 0) {
            actionMode.finish();
        } else {
            actionMode.setTitle(String.valueOf(count));
            actionMode.invalidate();
        }
    }

    /**
     * метод с помощью асинхронного загрузчика в доп потоке загружает данные из БД
     * @param filter
     */
    private void loadData(final String filter) {
        getLoaderManager().restartLoader(0, null, new LoaderManager.LoaderCallbacks<List<Transaction>>() {

            /**
             * прозодит в бекграуде
             */
            @Override
            public Loader<List<Transaction>> onCreateLoader(int id, Bundle args) {
                final AsyncTaskLoader<List<Transaction>> loader = new AsyncTaskLoader<List<Transaction>>(getActivity()) {
                    @Override
                    public List<Transaction> loadInBackground() {
                        return Transaction.getDataList(filter);
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
            public void onLoadFinished(Loader<List<Transaction>> loader, List<Transaction> data) {
                //отключаем свайп
                swipeLayout.setRefreshing(false);
                transactionAdapter = new TransactionAdapter(data, getActivity(), new TransactionAdapter.CardViewHolder.ClickListener() {
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
                recyclerView.setAdapter(transactionAdapter);
                if (data.size() != 0) {
                    lastTransaction = data.get(0);
                } else {
                    lastTransaction = new Transaction(
                            getString(R.string.hint_title_example),
                            getString(R.string.hint_price_example),
                            new Category(getString(R.string.hint_category_exemple)));
                }
            }

            @Override
            public void onLoaderReset(Loader<List<Transaction>> loader) {

            }
        });
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
                    transactionAdapter.removeItems(transactionAdapter.getSelectedItem());
                    mode.finish();
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            transactionAdapter.clearSelection();
            actionMode = null;
        }
    }
}
