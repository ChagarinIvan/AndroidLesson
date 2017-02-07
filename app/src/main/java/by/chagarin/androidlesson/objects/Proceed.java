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

@Table(name = "Proceeds")
public class Proceed extends Model implements Parcelable {
    public static final String SYSTEM_PROCEED = "system_proceed";

    @Column(name = "title")
    private String title;
    @Column(name = "price")
    private String price;
    @Column(name = "date")
    private String date;
    @Column(name = "Comment")
    private String comment;
    @Column(name = "categoryplace")
    private Category categoryPlace;
    @Column(name = "categoryproceed")
    private Category categoryProcees;
    public String uid;
    public String author;

    public static final DateFormat df = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);

    /**
     * need to active android
     */
    public Proceed(String title, String price, Date date, String comment, Category categoryPlace, Category categoryProceed) {
        this.title = title;
        this.price = price;
        this.categoryPlace = categoryPlace;
        this.date = df.format(date);
        this.comment = comment;
        this.categoryProcees = categoryProceed;
    }

    public Proceed(String title, String price, String date, String comment, Category categoryPlace, Category categoryProcees, String uid, String author) {
        this.title = title;
        this.price = price;
        this.date = date;
        this.comment = comment;
        this.categoryPlace = categoryPlace;
        this.categoryProcees = categoryProcees;
        this.uid = uid;
        this.author = author;
    }

    protected Proceed(Parcel in) {
        title = in.readString();
        price = in.readString();
        date = in.readString();
        categoryPlace = new Category(in.readString(), KindOfCategories.getPlace());
        comment = in.readString();
        categoryProcees = new Category(in.readString(), KindOfCategories.getProceed());
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
        parcel.writeString(price);
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
        result.put("categoryProceed", categoryProcees);
        result.put("categoryPlace", categoryPlace);
        return result;
    }
}

