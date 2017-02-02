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

@Table(name = "Proceeds")
public class Proceed extends Model implements Parcelable {
    public static final String SYSTEM_PROCEED = "system_proceed";

    @Column(name = "title")
    private String title;
    @Column(name = "price")
    private float price;
    @Column(name = "date")
    private Date date;
    @Column(name = "Comment")
    private String comment;
    @Column(name = "categoryplace")
    private Category categoryPlace;
    @Column(name = "categoryproceed")
    private Category categoryProcees;

    public static final DateFormat df = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);

    /**
     * need to active android
     */
    public Proceed(String title, String price, Date date, String comment, Category categoryPlace, Category categoryProceed) {
        this.title = title;
        this.price = Float.parseFloat(price);
        this.categoryPlace = categoryPlace;
        this.date = date;
        this.comment = comment;
        this.categoryProcees = categoryProceed;
    }

    protected Proceed(Parcel in) {
        try {
            title = in.readString();
            price = in.readFloat();
            date = df.parse(in.readString());
            categoryPlace = new Category(in.readString(), KindOfCategories.getPlace());
            comment = in.readString();
            categoryProcees = new Category(in.readString(), KindOfCategories.getProceed());
        } catch (ParseException ignored) {
        }
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

    public Category getCategoryPlace() {
        return categoryPlace;
    }

    public Category getCategoryProcees() {
        return categoryProcees;
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
        parcel.writeFloat(price);
        parcel.writeString(this.getDate());
        parcel.writeString(categoryPlace.getName());
        parcel.writeString(comment);
        parcel.writeString(categoryProcees.getName());
    }

    public static List<Proceed> getDataList() {
        From from = new Select()
                .from(Proceed.class)
                .orderBy("date DESC");
        return from.execute();
    }
}

