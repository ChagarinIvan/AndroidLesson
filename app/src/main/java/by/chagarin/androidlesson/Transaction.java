package by.chagarin.androidlesson;

/**
 * Created by IME on 07.01.2017.
 */

public class Transaction {
    private String title;
    private String price;

    public Transaction(String title, String price) {
        this.title = title;
        this.price = price;
    }

    public String getTitle() {
        return title;
    }

    public String getPrice() {
        return price;
    }
}
