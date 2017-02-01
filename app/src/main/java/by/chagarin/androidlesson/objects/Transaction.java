package by.chagarin.androidlesson.objects;


import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.From;
import com.activeandroid.query.Select;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import by.chagarin.androidlesson.KindOfCategories;

@Table(name = "Transactions")
public class Transaction extends Model implements Parcelable {

    @Column(name = "title")
    private String title;
    @Column(name = "price")
    private float price;
    @Column(name = "date")
    private Date date;
    @Column(name = "Comment")
    private String comment;
    @Column(name = "categorytransaction")
    private Category categoryTransaction;
    @Column(name = "categoryplace")
    private Category categoryPlace;

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
        this.price = Float.parseFloat(price);
        this.categoryTransaction = categoryTransaction;
        this.categoryPlace = categoryPlace;
        this.date = date;
        this.comment = comment;
    }

    protected Transaction(Parcel in) {
        try {
            title = in.readString();
            price = in.readFloat();
            date = df.parse(in.readString());
            categoryTransaction = new Category(in.readString(), KindOfCategories.getTransaction());
            comment = in.readString();
            categoryPlace = new Category(in.readString(), KindOfCategories.getPlace());
        } catch (ParseException ignored) {
        }
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
        return df.format(this.date);
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
        parcel.writeFloat(price);
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
}
