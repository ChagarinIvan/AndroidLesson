package by.chagarin.androidlesson.objects;


import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.From;
import com.activeandroid.query.Select;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import by.chagarin.androidlesson.KindOfCategories;

@Table(name = "Transactions")
public class Transaction extends Model implements Parcelable {
    public static final String SYSTEM_TRANSACTION = "system_transaction";


    @Column(name = "title")
    private String title;
    @Column(name = "price")
    private String price;
    @Column(name = "date")
    private String date;
    @Column(name = "Comment")
    private String comment;
    @Column(name = "categorytransaction")
    private Category categoryTransaction;
    @Column(name = "categoryplace")
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

    /**
     * need to active android
     * @param title
     * @param price
     * @param date
     * @param categoryTransaction
     * @param categoryPlace
     */
    public Transaction(String title, String price, Date date, String comment, Category categoryTransaction, Category categoryPlace) {
        this.title = title;
        this.categoryTransaction = categoryTransaction;
        this.categoryPlace = categoryPlace;
        this.date = df.format(date);
        this.comment = comment;
        this.price = price;
    }

    protected Transaction(Parcel in) {
        title = in.readString();
        price = in.readString();
        date = in.readString();
        categoryTransaction = new Category(in.readString(), KindOfCategories.getTransaction());
        comment = in.readString();
        categoryPlace = new Category(in.readString(), KindOfCategories.getPlace());
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

    /**
     * для передачи объекта между активностями
     * имплементим парселабле
     * @param parcel
     * @param i
     */
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(price);
        parcel.writeString(this.getDate());
        parcel.writeString(categoryTransaction.getName());
        parcel.writeString(comment);
        parcel.writeString(categoryPlace.getName());
    }

    public static List<Transaction> getDataList() {
        From from = new Select()
                .from(Transaction.class)
                .orderBy("date DESC");
        return from.execute();
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

    public void setAuthor(String userId, String author) {
        this.uid = userId;
        this.author = author;
    }
}
