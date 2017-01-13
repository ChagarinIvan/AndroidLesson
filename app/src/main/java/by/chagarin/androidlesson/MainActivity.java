package by.chagarin.androidlesson;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private TransactionAdapter transactionAdapter;
    private List<Transaction> data = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        List<Transaction> adapterData = getData();
        transactionAdapter = new TransactionAdapter(this,adapterData);
        listView = (ListView) findViewById(R.id.listview);
        listView.setAdapter(transactionAdapter);
    }

    private List<Transaction> getData(){
        try {
            data.add(new Transaction("Windows",50, "2001-07-25"));
            data.add(new Transaction("Phone",20, "2010-10-23"));
            data.add(new Transaction("iOS",100, "2019-03-24"));
            data.add(new Transaction("Mac",120, "2000-02-26"));
            data.add(new Transaction("Linux",150, "1989-01-27"));
            data.add(new Transaction("Linux",150, "1989-01-27"));
            data.add(new Transaction("Linux",150, "1989-01-27"));
            data.add(new Transaction("Linux",150, "1989-01-27"));
        } catch (ParseException e) {

        }
        return data;
    }
}
