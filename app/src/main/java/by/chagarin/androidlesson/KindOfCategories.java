package by.chagarin.androidlesson;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import by.chagarin.androidlesson.objects.Category;

public class KindOfCategories {
    private static String TRANSACTION = "Категория трат";
    private static String PROCEED = "Категоря поступлений";
    private static String PLACE = "Категория источника средств";

    public static String[] getKinds() {
        return new String[]{TRANSACTION, PROCEED, PLACE};
    }

    public static String getTransaction() {
        return TRANSACTION;
    }

    public static String getProceed() {
        return PROCEED;
    }

    public static String getPlace() {
        return PLACE;
    }

    /**
     * метод сортирует данные
     * выбирает из списка категорий только с нужным типом
     */
    public static List<Category> sortData(List<Category> data, String kind) {
        List<Category> list = new ArrayList<Category>();
        for (Category cat : data) {
            if (TextUtils.equals(cat.getKindOfCategories(), kind)) {
                list.add(cat);
            }
        }
        return list;
    }
}
