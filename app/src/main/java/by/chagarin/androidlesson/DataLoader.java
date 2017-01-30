package by.chagarin.androidlesson;


import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Loader;
import android.os.Bundle;

import org.androidannotations.annotations.EBean;

import java.util.List;

import by.chagarin.androidlesson.fragments.MyFragment;
import by.chagarin.androidlesson.objects.Category;
import by.chagarin.androidlesson.objects.Proceed;
import by.chagarin.androidlesson.objects.Transaction;

@EBean(scope = EBean.Scope.Singleton)
public class DataLoader {
    private static List<Proceed> proceedList;
    private static List<Transaction> transactionList;
    private static List<Category> categoryList;


    private Callbacks mCallbacks = sDummyCallbacks;
    private boolean isCashLoading = false;
    private boolean isProceedesLoading = false;
    private float cashCount;

    public float getCashCount() {
        return cashCount;
    }

    public interface Callbacks {
        public void onTaskFinished();
    }

    private static Callbacks sDummyCallbacks = new Callbacks() {
        public void onTaskFinished() {
        }
    };

    public void loadCash(final MyFragment fragment, final String filter) {
        isCashLoading = true;
        loadTransactions(fragment, filter);
    }

    public void loadCategores(final MyFragment fragment) {
        mCallbacks = (Callbacks) fragment;
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
                goesToCallBack(fragment);
            }

            @Override
            public void onLoaderReset(Loader<List<Category>> loader) {

            }
        });
    }

    public void loadTransactions(final MyFragment fragment, final String filter) {
        mCallbacks = (Callbacks) fragment;
        fragment.getLoaderManager().restartLoader(0, null, new LoaderManager.LoaderCallbacks<List<Transaction>>() {

            /**
             * прозодит в бекграуде
             */
            @Override
            public Loader<List<Transaction>> onCreateLoader(int id, Bundle args) {
                final AsyncTaskLoader<List<Transaction>> loader = new AsyncTaskLoader<List<Transaction>>(fragment.getActivity()) {
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
                transactionList = data;
                goesToCallBack(fragment);
            }

            @Override
            public void onLoaderReset(Loader<List<Transaction>> loader) {

            }
        });
    }

    private void goesToCallBack(MyFragment fragment) {
        if (isCashLoading) {
            if (!isProceedesLoading) {
                isProceedesLoading = true;
                loadProceedes(fragment, "");
            } else {
                isProceedesLoading = false;
                isCashLoading = false;
                calcCash();
                mCallbacks.onTaskFinished();
            }
        } else {
            mCallbacks.onTaskFinished();
        }
    }

    private void calcCash() {
        cashCount = 0;
        for (Proceed proceed : proceedList) {
            cashCount += Float.parseFloat(proceed.getPrice());
        }
        for (Transaction transaction : transactionList) {
            cashCount -= Float.parseFloat(transaction.getPrice());
        }
    }

    public void loadProceedes(final MyFragment fragment, final String filter) {
        mCallbacks = (Callbacks) fragment;
        fragment.getLoaderManager().restartLoader(0, null, new LoaderManager.LoaderCallbacks<List<Proceed>>() {

            /**
             * прозодит в бекграуде
             */
            @Override
            public Loader<List<Proceed>> onCreateLoader(int id, Bundle args) {
                final AsyncTaskLoader<List<Proceed>> loader = new AsyncTaskLoader<List<Proceed>>(fragment.getActivity()) {
                    @Override
                    public List<Proceed> loadInBackground() {
                        return Proceed.getDataList(filter);
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
                goesToCallBack(fragment);
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
        return categoryList;
    }
}
