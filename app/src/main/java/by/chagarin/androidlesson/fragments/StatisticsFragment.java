package by.chagarin.androidlesson.fragments;

import android.app.Fragment;
import android.graphics.Color;
import android.widget.LinearLayout;

import com.github.androidprogresslayout.ProgressLayout;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Callable;

import by.chagarin.androidlesson.ColorRandom;
import by.chagarin.androidlesson.DataLoader;
import by.chagarin.androidlesson.KindOfCategories;
import by.chagarin.androidlesson.MainActivity;
import by.chagarin.androidlesson.R;
import by.chagarin.androidlesson.objects.Category;
import by.chagarin.androidlesson.objects.Transaction;

import static by.chagarin.androidlesson.DataLoader.isShow;
import static by.chagarin.androidlesson.DataLoader.transactionList;

@EFragment(R.layout.fragment_statistics)
public class StatisticsFragment extends Fragment {
    private Map<Category, Float> categoryFloatMap;

    @ViewById
    LinearLayout childLayout;

    @Bean
    DataLoader loader;

    @ViewById
    PieChart pieChart;

    @Bean
    ColorRandom colorRandom;

    @ViewById(R.id.progress_layout)
    ProgressLayout progressLayout;

    private final DataLoader.AllDataLoaderListener singleValueListener = new DataLoader.AllDataLoaderListener(new Callable() {
        @Override
        public Object call() throws Exception {
            createPierChart(DataLoader.transactionsCategoryList, DataLoader.transactionList);
            return null;
        }
    });

    @AfterViews
    void ready() {
        final MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.actualFragment = this;
        getView().setBackgroundColor(getResources().getColor(colorRandom.getRandomColor()));
        startLoad();
        mainActivity.setTitle("Статистика");
    }

    private void startLoad() {
        //загружаем данные
        loader.mDatabase.addValueEventListener(singleValueListener);
    }

    private void createPierChart(List<Category> categoryList, List<Transaction> listTransactions) {
        categoryFloatMap = sortData(categoryList, listTransactions, isShow);
        final List<PieEntry> pieEntries = convertToEntry(categoryFloatMap);

        final PieDataSet set = new PieDataSet(pieEntries, "");
        //устанавливаем разделители между элементами данных

        set.setColors(getRandomColors(pieEntries.size()));
        final PieData pieData = new PieData(set);
        pieChart.setData(pieData);
        //опускаем диаграмму вниз

        pieChart.setHighlightPerTapEnabled(true);
        pieChart.setDrawSlicesUnderHole(false);
        pieChart.setTouchEnabled(true);
        pieChart.setRotationEnabled(true);
//        pieChart.setMaxAngle(180f);
//        pieChart.setRotationAngle(180f);
        pieChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);
        pieChart.setDrawEntryLabels(true);
        pieChart.invalidate();
        progressLayout.showContent();
        //убираем легенду
        Legend l = pieChart.getLegend();
        l.setEnabled(false);
        pieChart.setDescription(null);
    }

    private Map<Category, Float> sortData(List<Category> categoryList, List<Transaction> listTransactions, boolean enabled) {
        //создаем мап где для каждой категории указано сколько товаров куплено
        Map<Category, Float> resultList = new HashMap<>();
        //определяем для какие есть категории мест хранения денег
        List<Category> data = KindOfCategories.sortData(categoryList, enabled);
        for (Category category : data) {
            resultList.put(category, 0f);
        }
        //в мап по категориям добавляем траты
        for (Map.Entry<Category, Float> mapEntry : resultList.entrySet()) {
            for (Transaction transaction : transactionList) {
                if (transaction.categoryTransactionKey.equals(mapEntry.getKey().key)) {
                    float value = mapEntry.getValue() + Float.parseFloat(transaction.price);
                    mapEntry.setValue(value);
                }
            }
        }
        //удаляем категории с нулевыми значениями
        Map<Category, Float> map = new HashMap<>();
        for (Map.Entry<Category, Float> mapEntry : resultList.entrySet()) {
            if (mapEntry.getValue() != 0f) {
                map.put(mapEntry.getKey(), mapEntry.getValue());
            }
        }

        return map;
    }

    private List<PieEntry> convertToEntry(Map<Category, Float> resultList) {
        List<PieEntry> entries = new ArrayList<>();
        for (Map.Entry<Category, Float> mapEntry : resultList.entrySet()) {
            entries.add(new PieEntry(mapEntry.getValue(), mapEntry.getKey().name));
        }
        return entries;
    }

    private List<Integer> getRandomColors(int size) {
        List<Integer> colors = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < size; i++) {
            colors.add(Color.argb(180, random.nextInt(256), random.nextInt(256), random.nextInt(256)));
        }
        return colors;
    }
}
