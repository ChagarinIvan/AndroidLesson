package by.chagarin.androidlesson.objects;


import java.util.HashMap;
import java.util.Map;

public class Transfer {
    public String title;
    public String price;
    public String date;
    public String categoryPlaceFromKey;
    public String categoryPlaceToKey;
    public String userKey;
    public String key;

    private String createTitle(String categoryPlaceFrom, String categoryPlaceTo) {
        return String.format("%s ==> %s", categoryPlaceFrom, categoryPlaceTo);
    }

    public Transfer(String price, String date, String categoryPlaceFromKey, String categoryPlaceToKey, String userKey, String key) {
        this.title = createTitle(categoryPlaceFromKey, categoryPlaceToKey);
        this.price = price;
        this.date = date;
        this.categoryPlaceFromKey = categoryPlaceFromKey;
        this.categoryPlaceToKey = categoryPlaceToKey;
        this.userKey = userKey;
        this.key = key;
    }

    public Transfer() {
    }
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("title", title);
        result.put("price", price);
        result.put("date", date);
        result.put("categoryFrom", categoryPlaceFromKey);
        result.put("categoryTo", categoryPlaceToKey);
        result.put("userKey", userKey);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        Transfer transfer = (Transfer) obj;
        return this.key.equals(transfer.key);
    }
}
