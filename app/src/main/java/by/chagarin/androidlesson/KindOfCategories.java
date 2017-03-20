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

    public static String[] getLatinKinds() {
        return new String[]{"transactions", "proceed", "places"};
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
    public static List<Category> sortData(List<Category> data, boolean enabled) {
        if (enabled) {
            return data;
        } else {
            List<Category> resultList = new ArrayList<>();
            for (Category category : data) {
                if (category.isShow.equals("yes")) {
                    resultList.add(category);
                }
            }
            return resultList;
        }
    }

    /**
     * метод дает категорию по имени
     */
    public static Category findCategory(List<Category> list, String name) {
        for (Category category : list) {
            if (TextUtils.equals(category.name, name)) {
                return category;
            }
        }
        return null;
    }

    public static List<String> getStringArray(List<Category> listCategories) {
        List<String> list = new ArrayList<>();
        for (Category cat : listCategories) {
            list.add(cat.name);
        }
        return list;
    }


    public static int getPosition(List<Category> listCategoriesTransactions, String categoryKey) {
        int n = 0;
        for (Category cat : listCategoriesTransactions) {
            if (cat.key.equals(categoryKey)) {
                return n;
            }
            n++;
        }
        return 0;
    }
}
