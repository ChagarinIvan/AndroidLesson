package by.chagarin.androidlesson;


import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Loader;
import android.os.Bundle;
import android.text.TextUtils;

import org.androidannotations.annotations.EBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import by.chagarin.androidlesson.fragments.MyFragment;
import by.chagarin.androidlesson.objects.Category;
import by.chagarin.androidlesson.objects.Proceed;
import by.chagarin.androidlesson.objects.Transaction;

@EBean(scope = EBean.Scope.Singleton)
public class DataLoader {
    private static List<Proceed> proceedList;
    private static List<Transaction> transactionList;
    private static List<Category> categoryList;
    private static Category systemCategory;


    private Callbacks mCallbacks = sDummyCallbacks;
    private boolean isCashLoading = false;
    private boolean isProceedesLoading = false;
    private float cashCount;
    private MyFragment fragment;

    public void loadData(MyFragment fragment) {
        this.fragment = fragment;
        mCallbacks = fragment;
        loadTransactions();
    }

    public Category getSystemCategories() {
        return systemCategory;
    }

    public List<Transaction> getTransactionsWithoutSystem() {
        List<Transaction> list = new ArrayList<>();
        for (Transaction tr : transactionList) {
            if (tr.getCategoryTransaction() != systemCategory) {
                list.add(tr);
            }
        }
        return list;
    }

    public List<Proceed> getProceedesWithoutSystem() {
        List<Proceed> list = new ArrayList<>();
        for (Proceed tr : proceedList) {
            if (tr.getCategoryProcees() != systemCategory) {
                list.add(tr);
            }
        }
        return list;
    }

    public interface Callbacks {
        void onTaskFinished();
    }

    private static Callbacks sDummyCallbacks = new Callbacks() {
        public void onTaskFinished() {
        }
    };

    public void loadCategores() {
        fragment.getLoaderManager().restartLoader(0, null, new LoaderManager.LoaderCallbacks<List<Category>>() {

            /**
             * прозодит в бекграуде
             */
            @Override
            public Loader<List<Category>> onCreateLoader(int id, Bundle args) {
                final AsyncTaskLoader<List<Category>> loader = new AsyncTaskLoader<List<Category>>(fragment.getActivity()) {
                    @Override
                    public List<Category> loadInBackground() {
                        return Category.getDataList();
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
                categoryList = data;
                loadProceedes();
            }

            @Override
            public void onLoaderReset(Loader<List<Category>> loader) {

            }
        });
    }

    public void loadTransactions() {
        fragment.getLoaderManager().restartLoader(0, null, new LoaderManager.LoaderCallbacks<List<Transaction>>() {

            /**
             * прозодит в бекграуде
             */
            @Override
            public Loader<List<Transaction>> onCreateLoader(int id, Bundle args) {
                final AsyncTaskLoader<List<Transaction>> loader = new AsyncTaskLoader<List<Transaction>>(fragment.getActivity()) {
                    @Override
                    public List<Transaction> loadInBackground() {
                        return Transaction.getDataList();
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
                transactionList = data;
                loadCategores();
            }

            @Override
            public void onLoaderReset(Loader<List<Transaction>> loader) {

            }
        });
    }

    private void goesToCallBack() {
        //проверяем создана ли системная категория
        if (findSysCat()) {
            mCallbacks.onTaskFinished();
        } else {
            new Category(Category.SYSTEM_CATEGORY, KindOfCategories.getSYSTEM()).save();
            loadTransactions();
        }
    }

    private boolean findSysCat() {
        for (Category category : categoryList) {
            if (TextUtils.equals(category.getName(), Category.SYSTEM_CATEGORY) && (TextUtils.equals(category.getKindOfCategories(), KindOfCategories.getSYSTEM()))) {
                systemCategory = category;
                return true;
            }
        }
        return false;
    }

    public String calcCash() {
        cashCount = 0;
        for (Proceed proceed : proceedList) {
            cashCount += Float.valueOf(proceed.getPrice());
        }
        for (Transaction transaction : transactionList) {
            cashCount -= Float.valueOf(transaction.getPrice());
        }
        return String.format(Locale.ENGLISH, "%.2f", cashCount) + " BYN";
    }

    public void loadProceedes() {
        fragment.getLoaderManager().restartLoader(0, null, new LoaderManager.LoaderCallbacks<List<Proceed>>() {

            /**
             * прозодит в бекграуде
             */
            @Override
            public Loader<List<Proceed>> onCreateLoader(int id, Bundle args) {
                final AsyncTaskLoader<List<Proceed>> loader = new AsyncTaskLoader<List<Proceed>>(fragment.getActivity()) {
                    @Override
                    public List<Proceed> loadInBackground() {
                        return Proceed.getDataList();
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
            public void onLoadFinished(Loader<List<Proceed>> loader, List<Proceed> data) {
                proceedList = data;
                goesToCallBack();
            }

            @Override
            public void onLoaderReset(Loader<List<Proceed>> loader) {

            }
        });
    }

    public List<Proceed> getProceedes() {
        return proceedList;
    }

    public List<Transaction> getTransactions() {
        return transactionList;
    }

    public List<Category> getCategores() {
        //отдаёт список категорий без системной категории
        return KindOfCategories.sortDataWithout(categoryList, KindOfCategories.getSYSTEM());
    }
}
