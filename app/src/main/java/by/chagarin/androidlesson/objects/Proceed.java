package by.chagarin.androidlesson.objects;

import android.graphics.Bitmap;

import java.util.HashMap;
import java.util.Map;

import by.chagarin.androidlesson.MainActivity;

public class Proceed {
    public String title;
    public String price;
    public String date;
    public String comment;
    public String categoryProceedesKey;
    public String categoryPlaceKey;
    public String userKey;
    public String key;

    public Proceed() {
    }

    public Proceed(String title, String price, String date, String comment, String categoryProceedesKey, String categoryPlaceKey, String userKey, String key) {
        this.title = title;
        this.price = price;
        this.date = date;
        this.comment = comment;
        this.categoryProceedesKey = categoryProceedesKey;
        this.categoryPlaceKey = categoryPlaceKey;
        this.userKey = userKey;
        this.key = key;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("title", title);
        result.put("price", price);
        result.put("date", date);
        result.put("comment", comment);
        result.put("categoryProceedesKey", categoryProceedesKey);
        result.put("categoryPlaceKey", categoryPlaceKey);
        result.put("userKey", userKey);
        result.put("key", key);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        Proceed proceed = (Proceed) obj;
        return this.key.equals(proceed.key);
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

