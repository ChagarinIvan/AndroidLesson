package by.chagarin.androidlesson.objects;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public String name;
    @Column(name = "kind")
    public String kind;
    public String uid;
    public String author;

    public Category(String name, String kind) {
        this.kind = kind;
        this.name = name;
    }

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

    public static List<Category> getDataList() {
        return new Select()
                .from(Category.class)
                .execute();
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
}
