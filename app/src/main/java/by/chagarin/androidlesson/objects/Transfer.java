package by.chagarin.androidlesson.objects;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Transfer {
    private String title;
    private String price;
    private String date;
    private Category categoryPlaceFrom;
    private Category categoryPlaceTo;
    public String uid;
    public String author;
    public static final DateFormat df = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);

    public Transfer(String price, String date, Category categoryPlaceFrom, Category categoryPlaceTo, String uid, String author) {
        this.title = createTitle(categoryPlaceFrom, categoryPlaceTo);
        this.price = price;
        this.date = date;
        this.categoryPlaceFrom = categoryPlaceFrom;
        this.categoryPlaceTo = categoryPlaceTo;
        this.uid = uid;
        this.author = author;
    }

    private String createTitle(Category categoryPlaceFrom, Category categoryPlaceTo) {
        return String.format("%s ==> %s", categoryPlaceFrom.getName(), categoryPlaceTo.getName());
    }

    public Transfer() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Category getCategoryPlaceFrom() {
        return categoryPlaceFrom;
    }

    public void setCategoryPlaceFrom(Category categoryPlaceFrom) {
        this.categoryPlaceFrom = categoryPlaceFrom;
    }

    public Category getCategoryPlaceTo() {
        return categoryPlaceTo;
    }

    public void setCategoryPlaceTo(Category categoryPlaceTo) {
        this.categoryPlaceTo = categoryPlaceTo;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("author", author);
        result.put("title", title);
        result.put("price", price);
        result.put("date", date);
        result.put("categoryPlaceFrom", categoryPlaceFrom);
        result.put("categoryPlaceTo", categoryPlaceTo);
        return result;
    }

    public String getUid() {
        return uid;
    }

    public String getAuthor() {
        return author;
    }

    @Override
    public boolean equals(Object obj) {
        Transfer transfer = (Transfer) obj;
        return this.getTitle().equals(transfer.getTitle()) &&
                this.getPrice().equals(transfer.getPrice()) &&
                this.getDate().equals(transfer.getDate()) &&
                this.getCategoryPlaceFrom().equals(transfer.getCategoryPlaceFrom()) &&
                this.getCategoryPlaceTo().equals(transfer.getCategoryPlaceTo()) &&
                this.getUid().equals(transfer.getUid()) &&
                this.getAuthor().equals(transfer.getAuthor());
    }
}
