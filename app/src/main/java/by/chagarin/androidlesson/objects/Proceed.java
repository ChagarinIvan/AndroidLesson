package by.chagarin.androidlesson.objects;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Proceed implements Parcelable {
    private String title;
    private String price;
    private String date;
    private String comment;
    private Category categoryPlace;
    private Category categoryProceedes;
    public String uid;
    public String author;

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setCategoryPlace(Category categoryPlace) {
        this.categoryPlace = categoryPlace;
    }

    public Category getCategoryProceedes() {
        return categoryProceedes;
    }

    public void setCategoryProceedes(Category categoryProceedes) {
        this.categoryProceedes = categoryProceedes;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public static final DateFormat df = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);

    public Proceed(String title, String price, String date, String comment, Category categoryPlace, Category categoryProceedes, String uid, String author) {
        this.title = title;
        this.price = price;
        this.date = date;
        this.comment = comment;
        this.categoryPlace = categoryPlace;
        this.categoryProceedes = categoryProceedes;
        this.uid = uid;
        this.author = author;
    }

    protected Proceed(Parcel in) {
        title = in.readString();
        price = in.readString();
        date = in.readString();
        categoryPlace = Category.createCategory(in.readString());
        comment = in.readString();
        categoryProceedes = Category.createCategory(in.readString());
        this.uid = in.readString();
        this.author = in.readString();
    }

    public Proceed() {
    }

    public static final Creator<Proceed> CREATOR = new Creator<Proceed>() {
        @Override
        public Proceed createFromParcel(Parcel in) {
            return new Proceed(in);
        }

        @Override
        public Proceed[] newArray(int size) {
            return new Proceed[size];
        }
    };

    public String getDate() {
        return date;
    }

    public String getComment() {
        return comment;
    }

    public String getTitle() {
        return title;
    }

    public String getPrice() {
        return String.valueOf(price);
    }

    public Category getCategoryPlace() {
        return categoryPlace;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * для передачи объекта между активностями
     * имплементим парселабле
     */
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(price);
        parcel.writeString(this.getDate());
        parcel.writeString(categoryPlace.toString());
        parcel.writeString(comment);
        parcel.writeString(categoryProceedes.toString());
        parcel.writeString(uid);
        parcel.writeString(author);
    }

    public void setAuthor(String userId, String username) {
        this.uid = userId;
        this.author = username;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("author", author);
        result.put("title", title);
        result.put("comment", comment);
        result.put("price", price);
        result.put("date", date);
        result.put("categoryProceed", categoryProceedes);
        result.put("categoryPlace", categoryPlace);
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
        Proceed proceed = (Proceed) obj;
        return this.getTitle().equals(proceed.getTitle()) &&
                this.getPrice().equals(proceed.getPrice()) &&
                this.getDate().equals(proceed.getDate()) &&
                this.getComment().equals(proceed.getComment()) &&
                this.getCategoryPlace().equals(proceed.getCategoryPlace()) &&
                this.getCategoryProceedes().equals(proceed.getCategoryProceedes()) &&
                this.getUid().equals(proceed.getUid()) &&
                this.getAuthor().equals(proceed.getAuthor());
    }
}

