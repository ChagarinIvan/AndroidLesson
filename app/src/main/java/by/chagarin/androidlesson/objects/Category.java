package by.chagarin.androidlesson.objects;

import android.graphics.Bitmap;

import com.activeandroid.Model;
import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

import by.chagarin.androidlesson.MainActivity;

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
    public String userKey;
    public String isShow;
    public String key;

    public Category(String name, String kind, String userKey, String isShow, String key) {
        this.name = name;
        this.kind = kind;
        this.userKey = userKey;
        this.isShow = isShow;
        this.key = key;
    }

    public Category() {
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", name);
        result.put("userKey", userKey);
        result.put("kind", kind);
        result.put("isShow", isShow);
        result.put("key", key);
        return result;
    }

    @Override
    public String toString() {
        return this.toMap().toString();
    }

    @Override
    public boolean equals(Object obj) {
        Category category = (Category) obj;
        return this.key.equals(category.key);
    }

    public Bitmap getUserIcon() {
        for (User user : MainActivity.userList) {
            if (user.userKey.equals(this.userKey)) {
                return user.bitmap;
            }
        }
        return null;
    }
}
