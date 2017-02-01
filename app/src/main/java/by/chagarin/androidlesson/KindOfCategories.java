package by.chagarin.androidlesson;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import by.chagarin.androidlesson.objects.Category;

public class KindOfCategories {
    private static String TRANSACTION = "Категория трат";
    private static String PROCEED = "Категоря источника поступлений средств";
    private static String PLACE = "Категория места хранения средств";

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

    public static Category findCategory(List<Category> list, String name) {
        for (Category category : list) {
            if (TextUtils.equals(category.getName(), name)) {
                return category;
            }
        }
        return null;
    }

    public static List<String> getStringArray(List<Category> listCategories) {
        List<String> list = new ArrayList<String>();
        for (Category cat : listCategories) {
            list.add(cat.getName());
        }
        return list;
    }
}
