package by.chagarin.androidlesson.fragments;

import android.app.Fragment;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.github.androidprogresslayout.ProgressLayout;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Callable;

import by.chagarin.androidlesson.ColorRandom;
import by.chagarin.androidlesson.DataLoader;
import by.chagarin.androidlesson.KindOfCategories;
import by.chagarin.androidlesson.MainActivity;
import by.chagarin.androidlesson.R;
import by.chagarin.androidlesson.objects.Category;
import by.chagarin.androidlesson.objects.Proceed;
import by.chagarin.androidlesson.objects.Transaction;
import by.chagarin.androidlesson.objects.Transfer;
import by.chagarin.androidlesson.objects.User;

import static by.chagarin.androidlesson.DataLoader.ACTIONS;
import static by.chagarin.androidlesson.DataLoader.AllDataLoaderListener;
import static by.chagarin.androidlesson.DataLoader.CATEGORIES;
import static by.chagarin.androidlesson.DataLoader.PLACES;
import static by.chagarin.androidlesson.DataLoader.TRANSFERS;
import static by.chagarin.androidlesson.DataLoader.USERS;
import static by.chagarin.androidlesson.DataLoader.df;
import static by.chagarin.androidlesson.DataLoader.isShow;


/**
 * фрагмент для отображения полу круглой диаграммы с данными о расположеннии денежных средств
 */
@EFragment(R.layout.fragment_statistics)
public class CashStatisticsFragment extends Fragment {
    private Map<Category, Float> categoryFloatMap;

    @ViewById
    PieChart pieChart;

    @Bean
    DataLoader loader;

    @ViewById
    LinearLayout childLayout;
    private final DataLoader.AllDataLoaderListener singleValueListener = new AllDataLoaderListener(new Callable() {
        @Override
        public Object call() throws Exception {
            createPierChart(DataLoader.placesCategoryList, DataLoader.transactionList, DataLoader.proceedList, DataLoader.transferList);
            return null;
        }
    });

    @ViewById(R.id.progress_layout)
    ProgressLayout progressLayout;

    @Bean
    ColorRandom colorRandom;

    @AfterViews
    public void ready() {
        final MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.actualFragment = this;
        getView().setBackgroundColor(getResources().getColor(colorRandom.getRandomColor()));
        startLoad();
        mainActivity.setTitle("Где же Ваши денежки?");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        loader.mDatabase.removeEventListener(singleValueListener);
    }

    private void startLoad() {
        //загружаем данные
        loader.mDatabase.addValueEventListener(singleValueListener);
    }

    private void createPierChart(List<Category> listCategory, List<Transaction> listTransactions, List<Proceed> listProceedes, List<Transfer> listTransfer) {
        categoryFloatMap = sortData(listCategory, listTransactions, listProceedes, listTransfer, isShow);
        final List<PieEntry> pieEntries = convertToEntry(categoryFloatMap);

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
        legend.setTextSize(22f);
        legend.setTextColor(Color.BLACK);
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
        int colorcodes[] = legend.getColors();
        childLayout.removeAllViews();
        for (int n = 0; n < legend.getColors().length - 1; n++) {
            LinearLayout.LayoutParams parms_left_layout = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            parms_left_layout.weight = 1F;
            LinearLayout left_layout = new LinearLayout(getActivity());
            left_layout.setOrientation(LinearLayout.HORIZONTAL);
            left_layout.setGravity(Gravity.CENTER);
            left_layout.setLayoutParams(parms_left_layout);

            LinearLayout.LayoutParams parms_legen_layout = new LinearLayout.LayoutParams(
                    20, 20);
            parms_legen_layout.setMargins(0, 0, 20, 0);
            LinearLayout legend_layout = new LinearLayout(getActivity());
            legend_layout.setLayoutParams(parms_legen_layout);
            legend_layout.setOrientation(LinearLayout.HORIZONTAL);
            legend_layout.setBackgroundColor(colorcodes[n]);
            left_layout.addView(legend_layout);

            TextView txt_unit = new TextView(getActivity());
            txt_unit.setText(legend.getLabels()[n]);
            left_layout.addView(txt_unit);

            LinearLayout.LayoutParams parms_middle_layout = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            parms_middle_layout.weight = 1F;
            LinearLayout middle_layout = new LinearLayout(getActivity());
            middle_layout.setOrientation(LinearLayout.HORIZONTAL);
            middle_layout.setGravity(Gravity.CENTER);
            middle_layout.setLayoutParams(parms_middle_layout);

            TextView txt_leads = new TextView(getActivity());
            txt_leads.setText(String.format(Locale.ENGLISH, "%.2f", pieEntries.get(n).getY()));
            middle_layout.addView(txt_leads);

            childLayout.addView(left_layout);
            childLayout.addView(middle_layout);
        }
        legend.setEnabled(false);
        //настройка описания
        calcSumm(pieChart);
        pieChart.setCenterTextColor(Color.BLACK);
        pieChart.setCenterTextSize(24f);
        //убираем ценральный круг
        pieChart.setDrawHoleEnabled(false);
        pieChart.setHighlightPerTapEnabled(true);
        //слушатель нажатий
        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                int entryIndex = set.getEntryIndex(e);
                PieEntry pieEntry = pieEntries.get(entryIndex);
                startAlertDialog(pieEntry);
            }

            @Override
            public void onNothingSelected() {

            }
        });
        pieChart.setDrawEntryLabels(true);
        pieChart.invalidate();
        progressLayout.showContent();
    }

    /**
     * метод запускает диалог перевода денег с одного метса на дрцгое
     */
    private void startAlertDialog(PieEntry pieEntry) {
        new MaterialDialog.Builder(getActivity())
                .title(R.string.transfer)
                .content(R.string.dialog_transfer_question)
                .positiveText(R.string.transfer_to)
                .negativeText(R.string.transfer_from)
                .onPositive(new ButtonClickListener(true, pieEntry))
                .onNegative(new ButtonClickListener(false, pieEntry))
                .show();
    }

    private void moveOffScreen() {
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        @SuppressWarnings("deprecation") int height = display.getHeight();  // deprecated

        int offset = (int) (height * 0.30); /* percent to move */

        LinearLayout.LayoutParams rlParams =
                (LinearLayout.LayoutParams) pieChart.getLayoutParams();
        rlParams.setMargins(0, 0, 0, -offset);
        pieChart.setLayoutParams(rlParams);
    }

    private void calcSumm(PieChart pieChart) {
        loader.calcAndSetCash(pieChart, false);
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


    private Map<Category, Float> sortData(List<Category> categoryList, List<Transaction> transactionList, List<Proceed> proceedList, List<Transfer> listTransfer, boolean enabled) {
        //создаем мап где для каждой категории указано сколько товаров куплено
        Map<Category, Float> resultList = new HashMap<>();
        //определяем для какие есть категории мест хранения денег
        List<Category> data = KindOfCategories.sortData(categoryList, enabled);
        for (Category category : data) {
            resultList.put(category, 0f);
        }
        //в мап по категориям добавляем поступления
        for (Map.Entry<Category, Float> mapEntry : resultList.entrySet()) {
            for (Proceed proceed : proceedList) {
                if (proceed.categoryPlaceKey.equals(mapEntry.getKey().key)) {
                    float value = mapEntry.getValue() + Float.parseFloat(proceed.price);
                    mapEntry.setValue(value);
                }
            }
        }
        //в мап по категорям отнимаеи транзакции
        for (Map.Entry<Category, Float> mapEntry : resultList.entrySet()) {
            for (Transaction transaction : transactionList) {
                if (transaction.categoryPlaceKey.equals(mapEntry.getKey().key)) {
                    float value = mapEntry.getValue() - Float.parseFloat(transaction.price);
                    mapEntry.setValue(value);
                }
            }
        }

        for (Map.Entry<Category, Float> mapEntry : resultList.entrySet()) {
            for (Transfer transfer : listTransfer) {
                if (transfer.categoryPlaceFromKey.equals(mapEntry.getKey().key)) {
                    float value = mapEntry.getValue() - Float.parseFloat(transfer.price);
                    mapEntry.setValue(value);
                }
                if (transfer.categoryPlaceToKey.equals(mapEntry.getKey().key)) {
                    float value = mapEntry.getValue() + Float.parseFloat(transfer.price);
                    mapEntry.setValue(value);
                }
            }
        }

        return resultList;
    }

    private List<PieEntry> convertToEntry(Map<Category, Float> resultList) {
        List<PieEntry> entries = new ArrayList<>();
        for (Map.Entry<Category, Float> mapEntry : resultList.entrySet()) {
            entries.add(new PieEntry(mapEntry.getValue(), mapEntry.getKey().name));
        }
        return entries;
    }

    //метод проверяет хватает ли на выбранном балансе средств
    private boolean checkCash(Category from, String cost) {
        for (Map.Entry<Category, Float> mapEntry : categoryFloatMap.entrySet()) {
            if (mapEntry.getKey().equals(from)) {
                if (mapEntry.getValue() > Float.parseFloat(cost)) {
                    return true;
                }
            }
        }
        return false;
    }

    private class ButtonClickListener implements MaterialDialog.SingleButtonCallback {
        private boolean flag;
        private PieEntry pieEntry;
        private Category from;
        private Category to;
        private List<Category> arrayOfCategory;


        public ButtonClickListener(boolean b, PieEntry pieEntry) {
            this.flag = b;
            this.pieEntry = pieEntry;
        }

        /**
         * метод возращает все категории с типом места кроме выбранной
         *
         * @return лист категорий
         */
        private List<Category> getArrayOfCategory(List<Category> listCategory) {
            List<Category> list = new ArrayList<>();
            for (Category category : listCategory) {
                if (!TextUtils.equals(category.name, pieEntry.getLabel())) {
                    list.add(category);
                }
            }
            return list;
        }

        @Override
        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
            loader.mDatabase.child(CATEGORIES + PLACES).addListenerForSingleValueEvent(new ValueEventListener() {
                public EditText et;
                public ArrayAdapter<String> adapter;
                public Spinner spinner;

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //загружаем категории
                    final List<Category> categoryList = new ArrayList<>();

                    for (DataSnapshot areaSnapshot : dataSnapshot.getChildren()) {
                        categoryList.add(areaSnapshot.getValue(Category.class));
                    }
                    arrayOfCategory = getArrayOfCategory(categoryList);
                    MaterialDialog newDialog = new MaterialDialog.Builder(getActivity())
                            .title(R.string.transfer_title)
                            .customView(R.layout.dialog_transfer, true)
                            .positiveText(R.string.save)
                            .negativeText(R.string.dont_save)
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    final String cost = String.valueOf(et.getText());
                                    if (flag) {
                                        from = arrayOfCategory.get(spinner.getSelectedItemPosition());
                                    } else {
                                        to = arrayOfCategory.get(spinner.getSelectedItemPosition());
                                    }
                                    if (checkCash(from, cost)) {
                                        final String userId = DataLoader.getUid();
                                        loader.mDatabase.child(USERS).child(userId).addListenerForSingleValueEvent(
                                                new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        // Get user value
                                                        User user = dataSnapshot.getValue(User.class);

                                                        // [START_EXCLUDE]
                                                        if (user == null) {
                                                            // User is null, error out
                                                            Toast.makeText(getActivity(), "Error: could not fetch user.", Toast.LENGTH_SHORT).show();
                                                        } else {
                                                            // Write new post
                                                            String key = loader.mDatabase.child(ACTIONS + TRANSFERS).push().getKey();
                                                            loader.writeNewTransfer(new Transfer(
                                                                    cost,
                                                                    df.format(new Date()),
                                                                    from.key,
                                                                    to.key,
                                                                    userId,
                                                                    key));
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {

                                                    }
                                                });
                                        dialog.dismiss();
                                    } else {
                                        Toast.makeText(getActivity(), getString(R.string.warning_no_cash), Toast.LENGTH_LONG).show();
                                    }
                                }
                            })
                            .onNegative(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    dialog.dismiss();
                                }
                            }).build();


                    TextView tv = (TextView) newDialog.getCustomView().findViewById(R.id.transfer_to_or_from);
                    et = (EditText) newDialog.getCustomView().findViewById(R.id.price);
                    if (flag) {
                        to = KindOfCategories.findCategory(categoryList, pieEntry.getLabel());
                        tv.setText(getString(R.string.to));
                    } else {
                        from = KindOfCategories.findCategory(categoryList, pieEntry.getLabel());
                        tv.setText(getString(R.string.from));
                    }
                    spinner = (Spinner) newDialog.getCustomView().findViewById(R.id.transfer_spinner);
                    adapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, KindOfCategories.getStringArray(arrayOfCategory));
                    spinner.setAdapter(adapter);
                    newDialog.show();

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
}
