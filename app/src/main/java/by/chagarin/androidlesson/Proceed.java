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

@Table(name = "Proceeds")
public class Proceed extends Model implements Parcelable {

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
     *
     * @param title
     * @param price
     * @param date
     * @param categoryPlace
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

    public static final Creator<by.chagarin.androidlesson.Proceed> CREATOR = new Creator<Proceed>() {
        @Override
        public by.chagarin.androidlesson.Proceed createFromParcel(Parcel in) {
            return new by.chagarin.androidlesson.Proceed(in);
        }

        @Override
        public by.chagarin.androidlesson.Proceed[] newArray(int size) {
            return new by.chagarin.androidlesson.Proceed[size];
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
     *
     * @param parcel
     * @param i
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

    public static List<by.chagarin.androidlesson.Proceed> getDataList(String filter) {
        From from = new Select()
                .from(by.chagarin.androidlesson.Proceed.class)
                .orderBy("date DESC");
        if (!TextUtils.isEmpty(filter)) {
            from.where("title LIKE?", "%" + filter + "%");
        }
        return from.execute();
    }
}

