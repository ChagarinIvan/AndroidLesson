package by.chagarin.androidlesson;


import android.app.Activity;
import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Loader;
import android.os.Bundle;

import java.util.List;

import by.chagarin.androidlesson.objects.Category;
import by.chagarin.androidlesson.objects.Proceed;
import by.chagarin.androidlesson.objects.Transaction;

public class DataLoader {
    private static List<Proceed> proceedList;
    private static List<Transaction> transactionList;
    private static List<Category> categoryList;

    public static void loadCategory(final Activity activity) {
        activity.getLoaderManager().restartLoader(0, null, new LoaderManager.LoaderCallbacks<List<Category>>() {

            /**
             * прозодит в бекграуде
             */
            @Override
            public Loader<List<Category>> onCreateLoader(int id, Bundle args) {
                final AsyncTaskLoader<List<Category>> loader = new AsyncTaskLoader<List<Category>>(activity) {
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
            }

            @Override
            public void onLoaderReset(Loader<List<Category>> loader) {

            }
        });
    }

}
