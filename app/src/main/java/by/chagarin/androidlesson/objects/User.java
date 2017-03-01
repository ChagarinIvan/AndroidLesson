package by.chagarin.androidlesson.objects;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class User {
    public String name;
    public String email;
    public String photoUrl;
    public String userKey;
    public boolean isShow;


    /**
     * @param name     of user
     * @param email    of user
     * @param photoUrl of user
     * @param userKey  of user
     */
    public User(String name, String email, String photoUrl, String userKey, boolean isShow) {
        this.name = name;
        this.email = email;
        this.photoUrl = photoUrl;
        this.userKey = userKey;
        this.isShow = isShow;
    }

    public User() {
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", name);
        result.put("email", email);
        result.put("photoUrl", photoUrl);
        result.put("isShow", isShow);
        return result;
    }
}
