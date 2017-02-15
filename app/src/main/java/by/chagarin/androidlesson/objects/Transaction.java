package by.chagarin.androidlesson.objects;


import java.util.HashMap;
import java.util.Map;

public class Transaction {
    public String title;
    public String price;
    public String date;
    public String comment;
    public String categoryTransactionKey;
    public String categoryPlaceKey;
    public String userKey;
    public String key;

    public Transaction() {
    }

    public Transaction(String title, String price, String date, String comment, String categoryTransactionKey, String categoryPlaceKey, String userKey, String key) {
        this.title = title;
        this.price = price;
        this.date = date;
        this.comment = comment;
        this.categoryTransactionKey = categoryTransactionKey;
        this.categoryPlaceKey = categoryPlaceKey;
        this.userKey = userKey;
        this.key = key;
    }

    public Map<String,Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("title", title);
        result.put("price", price);
        result.put("date", date);
        result.put("comment", comment);
        result.put("categoryTransactionKey", categoryTransactionKey);
        result.put("categoryPlaceKey", categoryPlaceKey);
        result.put("userKey", userKey);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        Transaction transaction = (Transaction) obj;
        return this.key.equals(transaction.key);
    }
}


