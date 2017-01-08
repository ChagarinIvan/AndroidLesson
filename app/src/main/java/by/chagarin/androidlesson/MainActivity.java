package by.chagarin.androidlesson;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

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
        data.add(new Transaction("Windows","50"));
        data.add(new Transaction("Phone","20"));
        data.add(new Transaction("iOS","100"));
        data.add(new Transaction("Mac","120"));
        data.add(new Transaction("Linux","150"));
        return data;
    }
}
