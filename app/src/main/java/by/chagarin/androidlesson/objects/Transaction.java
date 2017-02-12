package by.chagarin.androidlesson.objects;


import android.os.Parcel;
import android.os.Parcelable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Transaction implements Parcelable {
    private String title;
    private String price;
    private String date;
    private String comment;
    private Category categoryTransaction;
    private Category categoryPlace;
    public String uid;
    public String author;

    public Transaction(String title, String price, String date, String comment, Category categoryTransaction, Category categoryPlace, String uid, String author) {
        this.title = title;
        this.price = price;
        this.date = date;
        this.comment = comment;
        this.categoryTransaction = categoryTransaction;
        this.categoryPlace = categoryPlace;
        this.uid = uid;
        this.author = author;
    }

    public static final DateFormat df = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);

    protected Transaction(Parcel in) {
        title = in.readString();
        price = in.readString();
        date = in.readString();
        categoryTransaction = Category.createCategory(in.readString());
        comment = in.readString();
        categoryPlace = Category.createCategory(in.readString());
        this.uid = in.readString();
        this.author = in.readString();
    }

    public Transaction() {
    }

    public static final Creator<Transaction> CREATOR = new Creator<Transaction>() {
        @Override
        public Transaction createFromParcel(Parcel in) {
            return new Transaction(in);
        }

        @Override
        public Transaction[] newArray(int size) {
            return new Transaction[size];
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

    public Category getCategoryTransaction() {
        return categoryTransaction;
    }

    public Category getCategoryPlace() {
        return categoryPlace;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getUid() {
        return uid;
    }

    public String getAuthor() {
        return author;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(price);
        parcel.writeString(this.getDate());
        parcel.writeString(categoryTransaction.toString());
        parcel.writeString(comment);
        parcel.writeString(categoryPlace.toString());
        parcel.writeString(uid);
        parcel.writeString(author);
    }

    public Map<String,Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("author", author);
        result.put("title", title);
        result.put("comment", comment);
        result.put("price", price);
        result.put("date", date);
        result.put("categoryTransaction", categoryTransaction);
        result.put("categoryPlace", categoryPlace);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        Transaction transaction = (Transaction) obj;
        return this.getTitle().equals(transaction.getTitle()) &&
                this.getPrice().equals(transaction.getPrice()) &&
                this.getDate().equals(transaction.getDate()) &&
                this.getComment().equals(transaction.getComment()) &&
                this.getCategoryPlace().equals(transaction.getCategoryPlace()) &&
                this.getCategoryTransaction().equals(transaction.getCategoryTransaction()) &&
                this.getUid().equals(transaction.getUid()) &&
                this.getAuthor().equals(transaction.getAuthor());
    }

    public String[] toArray() {
        String[] list = new String[6];
        list[0] = this.getTitle();
        list[1] = this.getPrice();
        list[2] = this.getComment();
        list[3] = this.getDate();
        list[4] = this.getCategoryTransaction().getName();
        list[5] = this.getCategoryPlace().getName();
        return list;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
