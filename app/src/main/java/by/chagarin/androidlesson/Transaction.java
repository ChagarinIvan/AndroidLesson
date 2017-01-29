package by.chagarin.androidlesson;


import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

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
    @Column(name = "category")
    private Category category;

    public static final DateFormat df = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);

    /**
     * need to active android
     * @param title
     * @param price
     * @param date
     * @param category
     */
    public Transaction(String title, String price, Date date, String comment, Category category) {
        this.title = title;
        this.price = Float.parseFloat(price);
        this.category = category;
        this.date = date;
        this.comment = comment;
    }

    protected Transaction(Parcel in) {
        try {
            title = in.readString();
            price = in.readFloat();
            date = df.parse(in.readString());
            category = new Category(in.readString(), in.readString());
            comment = in.readString();
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

    public Category getCategory() {
        return category;
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
        parcel.writeString(category.getName());
        parcel.writeString(category.getKindOfCategories());
        parcel.writeString(comment);
    }

    public static List<Transaction> getDataList(String filter) {
        From from = new Select()
                .from(Transaction.class)
                .orderBy("date DESC");
        if (!TextUtils.isEmpty(filter)) {
            from.where("title LIKE?", "%" + filter + "%");
        }
        return from.execute();
    }
}
