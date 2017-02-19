package by.chagarin.androidlesson.fragments;

import android.app.Dialog;
import android.app.Fragment;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import by.chagarin.androidlesson.objects.Transfer;
import by.chagarin.androidlesson.objects.User;

import static by.chagarin.androidlesson.DataLoader.CATEGORIES;
import static by.chagarin.androidlesson.DataLoader.PROCEEDS;
import static by.chagarin.androidlesson.DataLoader.TRANSACTIONS;
import static by.chagarin.androidlesson.DataLoader.TRANSFERS;


/**
 * фрагмент для отображения полу круглой диаграммы с данными о расположеннии денежных средств
 */
@EFragment(R.layout.fragment_statistics)
public class CashStatisticsFragment extends Fragment {
    private Dialog question_dialog;
    private Map<Category, Float> categoryFloatMap;


    @ViewById
    PieChart pieChart;

    @Bean
    DataLoader loader;

    @AfterViews
    public void ready() {
        getActivity().setTitle("Где же Ваши денежки?");
        //згружаем данные
        loader.mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //загружаем все акшны
                final List<Transaction> transactionList = new ArrayList<>();
                final List<Proceed> proceedList = new ArrayList<>();
                final List<Transfer> transferList = new ArrayList<>();
                final List<Category> categoryList = new ArrayList<>();

                for (DataSnapshot areaSnapshot : dataSnapshot.child(TRANSACTIONS).getChildren()) {
                    transactionList.add(areaSnapshot.getValue(Transaction.class));
                }
                for (DataSnapshot areaSnapshot : dataSnapshot.child(PROCEEDS).getChildren()) {
                    proceedList.add(areaSnapshot.getValue(Proceed.class));
                }
                for (DataSnapshot areaSnapshot : dataSnapshot.child(TRANSFERS).getChildren()) {
                    transferList.add(areaSnapshot.getValue(Transfer.class));
                }
                for (DataSnapshot areaSnapshot : dataSnapshot.child(CATEGORIES).getChildren()) {
                    categoryList.add(areaSnapshot.getValue(Category.class));
                }
                createPierChart(categoryList, transactionList, proceedList, transferList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void createPierChart(List<Category> listCategory, List<Transaction> listTransactions, List<Proceed> listProceedes, List<Transfer> listTransfer) {
        categoryFloatMap = sortData(listCategory, listTransactions, listProceedes, listTransfer);
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
        legend.setTextSize(20f);
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
        //настройка описания
        pieChart.setCenterText(calcSumm());
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
                //startAlertDialog(pieEntry);
            }

            @Override
            public void onNothingSelected() {

            }
        });
        pieChart.setDrawEntryLabels(true);
        pieChart.invalidate();
    }

    /**
     * метод запускает диалог перевода денег с одного метса на дрцгое
     */
    private void startAlertDialog(PieEntry pieEntry) {
        question_dialog = new Dialog(getActivity());
        question_dialog.setContentView(R.layout.dialog_transfer_question);
        TextView textView = (TextView) question_dialog.findViewById(R.id.title);
        Button toButton = (Button) question_dialog.findViewById(R.id.transfer_to);
        Button fromButton = (Button) question_dialog.findViewById(R.id.transfer_from);
        toButton.setOnClickListener(new ButtonClickListener(true, pieEntry));
        fromButton.setOnClickListener(new ButtonClickListener(false, pieEntry));

        textView.setText(pieEntry.getLabel());
        //noinspection ConstantConditions
        question_dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        question_dialog.show();
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
        return "Общий баланс " /*loader.getCashCount()*/;
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


    private Map<Category, Float> sortData(List<Category> categoryList, List<Transaction> transactionList, List<Proceed> proceedList, List<Transfer> listTransfer) {
        //создаем мап где для каждой категории указано сколько товаров куплено
        Map<Category, Float> resultList = new HashMap<>();
        //определяем для какие есть категории мест хранения денег
        List<Category> data = KindOfCategories.sortData(categoryList, KindOfCategories.getPlace());
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
                if (transfer.categoryPlaceToKey.equals(mapEntry.getKey().name)) {
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
            if (mapEntry.getKey() == from) {
                if (mapEntry.getValue() > Float.parseFloat(cost)) {
                    return true;
                }
            }
        }
        return false;
    }

    private class ButtonClickListener implements View.OnClickListener {
        private boolean flag;
        private PieEntry pieEntry;
        private Category from;
        private Category to;
        private List<Category> arrayOfCategory;


        public ButtonClickListener(boolean b, PieEntry pieEntry) {
            this.flag = b;
            this.pieEntry = pieEntry;
        }

        @Override
        public void onClick(View v) {
            //arrayOfCategory = getArrayOfCategory();
            final Dialog dialog = new Dialog(getActivity());
            dialog.setContentView(R.layout.dialog_transfer);
            final EditText et = (EditText) dialog.findViewById(R.id.edit_text);
            Button ok = (Button) dialog.findViewById(R.id.ok_button);
            Button cancel = (Button) dialog.findViewById(R.id.cancel_button);
            TextView tv = (TextView) dialog.findViewById(R.id.transfer_to_or_from);
            if (flag) {
                //to = KindOfCategories.findCategory(loader.getCategores(), pieEntry.getLabel());
                tv.setText(getString(R.string.to));
            } else {
                //from = KindOfCategories.findCategory(loader.getCategores(), pieEntry.getLabel());
                tv.setText(getString(R.string.from));
            }
            final Spinner spinner = (Spinner) dialog.findViewById(R.id.transfer_spinner);

            ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, KindOfCategories.getStringArray(arrayOfCategory));
            spinner.setAdapter(adapter);
            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String cost = String.valueOf(et.getText());
                    if (flag) {
                        from = arrayOfCategory.get(spinner.getSelectedItemPosition());
                    } else {
                        to = arrayOfCategory.get(spinner.getSelectedItemPosition());
                    }
                    if (checkCash(from, cost)) {
                        final String userId = loader.getUid();
                        loader.mDatabase.child("users").child(userId).addListenerForSingleValueEvent(
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
                                            //writeNewTransfer(userId, user.userKey, cost, from, to);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                    }
                                });
                        dialog.dismiss();
                        question_dialog.dismiss();
                    } else {
                        Toast.makeText(getActivity(), getString(R.string.warning_no_cash), Toast.LENGTH_LONG).show();
                    }
                }
            });
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    question_dialog.dismiss();
                }
            });
            //noinspection ConstantConditions
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.show();
        }

        /**
         * метод возращает все категории с типом места кроме выбранной
         *
         * @return лист категорий
         */
        private List<Category> getArrayOfCategory(List<Category> listCategory) {
            List<Category> list = new ArrayList<>();
            for (Category category : KindOfCategories.sortData(listCategory, KindOfCategories.getPlace())) {
                if (!TextUtils.equals(category.name, pieEntry.getLabel())) {
                    list.add(category);
                }
            }
            return list;
        }
    }
}
