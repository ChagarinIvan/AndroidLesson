package by.chagarin.androidlesson;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * делаем класс для БД
 * анотация Table с именем таблицы
 * наследуем класс модел
 * нужные поля помечяем анотацией Column с именем
 * ВАЖНО! создаём пустой конструктор
 */
@Table(name = "Categories")
public class Category extends Model {

    @Column(name = "title")
    private String name;

    Category(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Category() {
    }
}
