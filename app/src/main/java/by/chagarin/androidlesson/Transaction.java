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
    private int price;
    @Column(name = "date")
    private Date date;
    @Column(name = "category")
    private Category category;

    DateFormat df = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);

    /**
     * need to active android
     */
    public Transaction() {
    }

    protected Transaction(Parcel in) {
        try {
            title = in.readString();
            price = in.readInt();
            date = df.parse(in.readString());
            category = new Category(in.readString());
        } catch (ParseException ignored) {
        }
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

    public Transaction(String title, String price, Category category) {
        this.title = title;
        this.price = Integer.parseInt(price);
        this.category = category;
        date = new Date();
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
        parcel.writeInt(price);
        parcel.writeString(this.getDate());
        parcel.writeString(category.getName());
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
