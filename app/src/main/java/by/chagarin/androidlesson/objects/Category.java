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
    public String uid;
    public String author;

    public Category(String name, String kind, String uid, String author) {
        this.name = name;
        this.kind = kind;
        this.uid = uid;
        this.author = author;
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
        result.put("uid", uid);
        result.put("author", author);
        result.put("name", name);
        result.put("kind", kind);
        return result;
    }

    @Override
    public String toString() {
        return this.toMap().toString();
    }

    public String getUid() {
        return uid;
    }

    public String getAuthor() {
        return author;
    }

    @Override
    public boolean equals(Object obj) {
        Category category = (Category) obj;
        return this.getName().equals(category.getName()) &&
                this.getKind().equals(category.getKind()) &&
                this.getUid().equals(category.getUid()) &&
                this.getAuthor().equals(category.getAuthor());
    }

    public static Category createCategory(String s) {
        String[] array = s.split(",");
        String[] categoryArray = new String[4];
        int n = 0;
        for (String line : array) {
            categoryArray[n++] = line.split("=")[1];
        }
        return new Category(categoryArray[3], categoryArray[2], categoryArray[0], categoryArray[1]);
    }
}
