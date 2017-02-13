package by.chagarin.androidlesson.objects;

import com.activeandroid.Model;
import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * делаем класс для БД
 * анотация Table с именем таблицы
 * наследуем класс модел
 * нужные поля помечяем анотацией Column с именем
 * ВАЖНО! создаём пустой конструктор
 */
public class Category extends Model {
    public String name;
    public String kind;
    public String author;
    public boolean isShowIt;

    public Category(String name, String kind, String author, boolean isShowIt) {
        this.name = name;
        this.kind = kind;
        this.author = author;
        this.isShowIt = isShowIt;
    }

    public String getName() {
        return name;
    }

    public String getKind() {
        return kind;
    }

    public Category() {
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("author", author);
        result.put("kind", kind);
        result.put("isShow", isShowIt);
        return result;
    }

    @Override
    public String toString() {
        return this.toMap().toString();
    }


    public String getAuthor() {
        return author;
    }

    public boolean isShowIt() {
        return isShowIt;
    }

    public void setShowIt(boolean showIt) {
        isShowIt = showIt;
    }

    @Override
    public boolean equals(Object obj) {
        Category category = (Category) obj;
        return this.getName().equals(category.getName()) &&
                this.getKind().equals(category.getKind()) &&
                this.getAuthor().equals(category.getAuthor()) &&
                this.isShowIt() == category.isShowIt();
    }

    public static Category createCategory(String s) {
        String[] array = s.split(",");
        String[] categoryArray = new String[4];
        int n = 0;
        for (String line : array) {
            categoryArray[n++] = line.split("=")[1];
        }
        return new Category(categoryArray[2], categoryArray[1], categoryArray[0], Boolean.parseBoolean(categoryArray[3]));
    }
}
