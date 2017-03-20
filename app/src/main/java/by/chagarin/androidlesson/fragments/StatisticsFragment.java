package by.chagarin.androidlesson.fragments;

import android.app.Fragment;

import com.github.mikephil.charting.charts.PieChart;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import by.chagarin.androidlesson.DataLoader;
import by.chagarin.androidlesson.MainActivity;
import by.chagarin.androidlesson.R;

@EFragment(R.layout.fragment_statistics)
public class StatisticsFragment extends Fragment {

    @Bean
    DataLoader loader;

    @ViewById
    PieChart pieChart;

    @AfterViews
    void ready() {
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.actualFragment = this;
        mainActivity.setTitle(R.string.statistics);
    }


//    @Override
//    public void onResume() {
//        super.onResume();
//        loadData();
//    }

//    public void onTaskFinished() {
////        List<Transaction> listTransactions = loader.getTransactions();
////        final List<PieEntry> pieEntries = sortData(listTransactions);
////        final PieDataSet set = new PieDataSet(pieEntries, "Общий расход");
////        set.setColors(getRandomColors(listTransactions));
////        final PieData pieData = new PieData(set);
////        pieChart.setData(pieData);
////        pieChart.animateXY(2000, 2000, Easing.EasingOption.EaseInCirc, Easing.EasingOption.EaseInCirc);
////        pieChart.setCenterText(calcSumm(listTransactions));
////        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
////            @Override
////            public void onValueSelected(Entry e, Highlight h) {
////                int entryIndex = set.getEntryIndex(e);
////                PieEntry pieEntry = pieEntries.get(entryIndex);
////                Toast.makeText(getActivity(), pieEntry.getLabel(), Toast.LENGTH_LONG).show();
////            }
////
////            @Override
////            public void onNothingSelected() {
////
////            }
////        });
////        pieChart.invalidate();
//    }
//
//    /**
//     * метод с помощью асинхронного загрузчика в доп потоке загружает данные из БД
//     */
//    private void loadData() {
//        loader.loadData();
//    }
//
//    private String calcSumm(List<Transaction> data) {
//        float summ = 0;
//        for (Transaction tr : data) {
//            summ += Float.parseFloat(tr.getPrice());
//        }
//        return "Общий рассход " + summ;
//    }
//
//    //метод будет возвращать лист рандомных цвето нужного размера
//    private List<Integer> getRandomColors(List<Transaction> data) {
//        List<Integer> colors = new ArrayList<Integer>();
//        Random random = new Random();
//        for (int i = 0; i < data.size(); i++) {
//            colors.add(Color.argb(100, random.nextInt(256), random.nextInt(256), random.nextInt(256)));
//        }
//        return colors;
//    }
//
//    private List<PieEntry> sortData(List<Transaction> data) {
//        //создаем мап где для каждой категории указано сколько товаров куплено
//        HashMap<Category, Float> allCategoryList = new HashMap<Category, Float>();
//        for (Transaction tr : data) {
//            Category category = tr.getCategoryTransaction();
//            float summ = Float.parseFloat(tr.getPrice());
//            if (allCategoryList.containsKey(category)) {
//                summ += allCategoryList.get(category);
//            }
//            allCategoryList.put(category, summ);
//        }
//        //переводим всё в пие ентри
//        List<PieEntry> entries = new ArrayList<>();
//        for (Map.Entry<Category, Float> mapEntry : allCategoryList.entrySet()) {
//            entries.add(new PieEntry(mapEntry.getValue(), mapEntry.getKey().getName()));
//        }
//        return entries;
//    }
}
