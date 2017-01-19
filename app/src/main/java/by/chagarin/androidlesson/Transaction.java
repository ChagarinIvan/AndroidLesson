package by.chagarin.androidlesson;


import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Table(name = "Transactions")
public class Transaction extends Model implements Parcelable {

    @Column(name = "title")
    private String title;
    @Column(name = "price")
    private int price;
    @Column(name = "date")
    private Date date;

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
        } catch (ParseException e) {
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

        String data = df.format(this.date);
        return data;
    }

    public Transaction(String title, String price) {
        this.title = title;
        this.price = Integer.parseInt(price);
        date = new Date();
    }

    public String getTitle() {
        return title;
    }

    public String getPrice() {
        return String.valueOf(price);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * для передачи объекта между активностями
     *
     * @param parcel
     * @param i
     */
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeInt(price);
        parcel.writeString(this.getDate());
    }
}
