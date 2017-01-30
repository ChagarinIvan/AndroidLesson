package by.chagarin.androidlesson.fragments;

import android.graphics.Color;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import by.chagarin.androidlesson.DataLoader;
import by.chagarin.androidlesson.R;
import by.chagarin.androidlesson.objects.Category;
import by.chagarin.androidlesson.objects.Transaction;

@EFragment(R.layout.fragment_statistics)
public class StatisticsFragment extends MyFragment {

    private List<Transaction> listTransactions;

    @Bean
    DataLoader loader;

    @ViewById
    PieChart pieChart;


    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }

    @Override
    public void onTaskFinished() {
        listTransactions = loader.getTransactions();
        final List<PieEntry> pieEntries = sortData(listTransactions);
        final PieDataSet set = new PieDataSet(pieEntries, "Общий расход");
        set.setColors(getRandomColors(listTransactions));
        final PieData pieData = new PieData(set);
        pieChart.setData(pieData);
        pieChart.animateXY(2000, 2000, Easing.EasingOption.EaseInCirc, Easing.EasingOption.EaseInCirc);
        pieChart.setCenterText(calcSumm(listTransactions));
        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                int entryIndex = set.getEntryIndex(e);
                PieEntry pieEntry = pieEntries.get(entryIndex);
                Toast.makeText(getActivity(), pieEntry.getLabel(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected() {

            }
        });
        pieChart.invalidate();
    }

    /**
     * метод с помощью асинхронного загрузчика в доп потоке загружает данные из БД
     */
    private void loadData() {
        loader.loadTransactions(this, "");
    }

    private String calcSumm(List<Transaction> data) {
        int summ = 0;
        for (Transaction tr : data) {
            summ += Integer.parseInt(tr.getPrice());
        }
        return "Общий рассход " + summ;
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
        HashMap<Category, Float> categoryList = new HashMap<Category, Float>();
        for (Transaction tr : data) {
            Category category = tr.getCategoryTransaction();
            float summ = Float.parseFloat(tr.getPrice());
            if (categoryList.containsKey(category)) {
                summ += categoryList.get(category);
            }
            categoryList.put(category, summ);
        }
        //переводим всё в пие ентри
        List<PieEntry> entries = new ArrayList<>();
        for (Map.Entry<Category, Float> mapEntry : categoryList.entrySet()) {
            entries.add(new PieEntry(mapEntry.getValue(), mapEntry.getKey().getName()));
        }
        return entries;
    }
}
