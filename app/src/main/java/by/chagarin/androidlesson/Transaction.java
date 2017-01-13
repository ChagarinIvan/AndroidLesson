package by.chagarin.androidlesson;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Transaction {
    private String title;
    private int price;
    private Date date;

    public String getDate() {
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);
        String data = df.format(this.date);
        return data;
    }

    public Transaction(String title, int price, String date) throws ParseException {
        this.title = title;
        this.price = price;
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        this.date = format.parse(date);
    }

    public String getTitle() {
        return title;
    }

    public String getPrice() {
        return String.valueOf(price);
    }
}
