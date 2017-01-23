package by.chagarin.androidlesson;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Loader;
import android.graphics.Color;
import android.os.Bundle;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@EFragment(R.layout.fragment_statistics)
public class StatisticsFragment extends Fragment {
    private float[] dataPoints = {400, 50, 70, 90, 100};


    @ViewById
    PieChart pieChart;

    @AfterViews
    void ready() {
        List<PieEntry> entries = new ArrayList<>();

        entries.add(new PieEntry(18.5f, "Green"));
        entries.add(new PieEntry(26.7f, "Yellow"));
        entries.add(new PieEntry(24.0f, "Red"));
        entries.add(new PieEntry(30.8f, "Blue"));


    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }

    /**
     * метод с помощью асинхронного загрузчика в доп потоке загружает данные из БД
     */
    private void loadData() {
        getLoaderManager().restartLoader(0, null, new LoaderManager.LoaderCallbacks<List<Transaction>>() {

            /**
             * прозодит в бекграуде
             */
            @Override
            public Loader<List<Transaction>> onCreateLoader(int id, Bundle args) {
                final AsyncTaskLoader<List<Transaction>> loader = new AsyncTaskLoader<List<Transaction>>(getActivity()) {
                    @Override
                    public List<Transaction> loadInBackground() {
                        return Transaction.getDataList("");
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
                //тут будем создавать пич чат
                PieDataSet set = new PieDataSet(sortData(data), "Election Results");
                set.setColors(getRandomColors(data));
                PieData pieData = new PieData(set);
                pieChart.setData(pieData);
                pieChart.invalidate();
            }

            @Override
            public void onLoaderReset(Loader<List<Transaction>> loader) {

            }
        });
    }

    //метод будет возвращать лист рандомных цвето нужного размера
    private List<Integer> getRandomColors(List<Transaction> data) {
        List<Integer> colors = new ArrayList<Integer>();
        Random random = new Random();
        for (int i = 0; i < data.size(); i++) {
            colors.add(Color.argb(100, random.nextInt(256), random.nextInt(256), random.nextInt(256)));
        }
        return colors;
    }

    private List<PieEntry> sortData(List<Transaction> data) {
        //создаем мап где для каждой категории указано сколько товаров куплено
        HashMap<Category, Integer> categoryList = new HashMap<Category, Integer>();
        for (Transaction tr : data) {
            Category category = tr.getCategory();
            int summ = Integer.parseInt(tr.getPrice());
            if (categoryList.containsKey(category)) {
                summ += categoryList.get(category);
            }
            categoryList.put(category, summ);
        }
        //переводим всё в пие ентри
        List<PieEntry> entries = new ArrayList<>();
        for (Map.Entry<Category, Integer> mapEntry : categoryList.entrySet()) {
            entries.add(new PieEntry(mapEntry.getValue(), mapEntry.getKey().getName()));
        }
        return entries;
    }
}
