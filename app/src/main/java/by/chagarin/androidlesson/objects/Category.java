package by.chagarin.androidlesson.objects;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.List;

/**
 * делаем класс для БД
 * анотация Table с именем таблицы
 * наследуем класс модел
 * нужные поля помечяем анотацией Column с именем
 * ВАЖНО! создаём пустой конструктор
 */
@Table(name = "Categories")
public class Category extends Model {

    public static final String SYSTEM_CATEGORY = "system_category";

    @Column(name = "title")
    private String name;
    @Column(name = "kind")
    private String kindOfCategories;

    public Category(String name, String kindOfCategories) {
        this.kindOfCategories = kindOfCategories;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getKindOfCategories() {
        return kindOfCategories;
    }

    public Category() {
    }

    public static List<Category> getDataList() {
        return new Select()
                .from(Category.class)
                .execute();
    }
}
