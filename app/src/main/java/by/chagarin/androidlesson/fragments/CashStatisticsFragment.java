package by.chagarin.androidlesson.fragments;

import android.graphics.Color;
import android.view.Display;
import android.widget.RelativeLayout;

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

import by.chagarin.androidlesson.DataLoader;
import by.chagarin.androidlesson.KindOfCategories;
import by.chagarin.androidlesson.R;
import by.chagarin.androidlesson.objects.Category;
import by.chagarin.androidlesson.objects.Proceed;
import by.chagarin.androidlesson.objects.Transaction;


/**
 * фрагмент для отображения полу круглой диаграммы с данными о расположеннии денежных средств
 */
@EFragment(R.layout.fragment_statistics)
public class CashStatisticsFragment extends MyFragment {

    @ViewById
    PieChart pieChart;

    @Bean
    DataLoader loader;

    @AfterViews
    public void ready() {
        getActivity().setTitle("Где же Ваши денежки?");
        //берём необходимые листы данных
        List<Category> listCategory = loader.getCategores();
        List<Transaction> listTransactions = loader.getTransactions();
        List<Proceed> listProceedes = loader.getProceedes();
        //получаем значения для отображения
        final List<PieEntry> pieEntries = sortData(listCategory, listTransactions, listProceedes);

        final PieDataSet set = new PieDataSet(pieEntries, "");
        //устанавливаем разделители между элементами данных
        set.setSliceSpace(5f);
        set.setColors(getRandomColors(pieEntries.size()));
        final PieData pieData = new PieData(set);
        pieChart.setData(pieData);
        //опускаем диаграмму вниз
        moveOffScreen();
        pieChart.setHighlightPerTapEnabled(true);
        pieChart.setDrawSlicesUnderHole(false);
        pieChart.setTouchEnabled(true);
        pieChart.setRotationEnabled(false);
        pieChart.setMaxAngle(180f);
        pieChart.setRotationAngle(180f);
        pieChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);
        //настройка легенды
        Legend legend = pieChart.getLegend();
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setTextSize(20f);
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
        //настройка описания
        pieChart.setCenterText(calcSumm());
        pieChart.setCenterTextColor(Color.BLACK);
        pieChart.setCenterTextSize(24f);

        //диаграмма некликабельна
        pieChart.setTouchEnabled(false);
        //убираем ценральный круг
        pieChart.setDrawHoleEnabled(false);

        pieChart.setHighlightPerTapEnabled(true);

        pieChart.setDrawEntryLabels(true);
        pieChart.invalidate();
    }

    private void moveOffScreen() {
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        @SuppressWarnings("deprecation") int height = display.getHeight();  // deprecated

        int offset = (int) (height * 0.75); /* percent to move */

        RelativeLayout.LayoutParams rlParams =
                (RelativeLayout.LayoutParams) pieChart.getLayoutParams();
        rlParams.setMargins(0, 0, 0, -offset);
        pieChart.setLayoutParams(rlParams);
    }

    private String calcSumm() {
        return "Общий баланс " + loader.calcCash();
    }

    //метод будет возвращать лист рандомных цвето нужного размера
    private List<Integer> getRandomColors(int size) {
        List<Integer> colors = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < size; i++) {
            colors.add(Color.argb(180, random.nextInt(256), random.nextInt(256), random.nextInt(256)));
        }
        return colors;
    }


    private List<PieEntry> sortData(List<Category> categoryList, List<Transaction> transactionList, List<Proceed> proceedList) {
        //создаем мап где для каждой категории указано сколько товаров куплено
        HashMap<Category, Float> resultList = new HashMap<>();
        //определяем для какие есть категории мест хранения денег
        List<Category> data = KindOfCategories.sortData(categoryList, KindOfCategories.getPlace());
        for (Category category : data) {
            resultList.put(category, 0f);
        }
        //в мап по категориям добавляем поступления
        for (Map.Entry<Category, Float> mapEntry : resultList.entrySet()) {
            for (Proceed proceed : proceedList) {
                if (proceed.getCategoryPlace() == mapEntry.getKey()) {
                    float value = mapEntry.getValue() + Float.parseFloat(proceed.getPrice());
                    mapEntry.setValue(value);
                }
            }
        }
        //в мап по категорям отнимаеи транзакции
        for (Map.Entry<Category, Float> mapEntry : resultList.entrySet()) {
            for (Transaction transaction : transactionList) {
                if (transaction.getCategoryPlace() == mapEntry.getKey()) {
                    float value = mapEntry.getValue() - Float.parseFloat(transaction.getPrice());
                    mapEntry.setValue(value);
                }
            }
        }

        //переводим всё в пие ентри
        List<PieEntry> entries = new ArrayList<>();
        for (Map.Entry<Category, Float> mapEntry : resultList.entrySet()) {
            entries.add(new PieEntry(mapEntry.getValue(), mapEntry.getKey().getName()));
        }
        return entries;
    }
}
