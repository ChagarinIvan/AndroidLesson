package by.chagarin.androidlesson;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class TransactionsFragment extends Fragment {
    private ListView listView;
    private TransactionAdapter transactionAdapter;
    private List<Transaction> data = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View inflate = inflater.inflate(R.layout.fragment_transactions, container, false);
        List<Transaction> adapterData = getDataList();
        transactionAdapter = new TransactionAdapter(getActivity(), adapterData);
        listView = (ListView) inflate.findViewById(R.id.listview);
        listView.setAdapter(transactionAdapter);
        return inflate;
    }

    private List<Transaction> getDataList() {
        try {
            data.add(new Transaction("Windows", 50, "2001-07-25"));
            data.add(new Transaction("Phone", 20, "2010-10-23"));
            data.add(new Transaction("iOS", 100, "2019-03-24"));
            data.add(new Transaction("Mac", 120, "2000-02-26"));
            data.add(new Transaction("Linux", 150, "1989-01-27"));
            data.add(new Transaction("Linux", 150, "1989-01-27"));
            data.add(new Transaction("Linux", 150, "1989-01-27"));
            data.add(new Transaction("Linux", 150, "1989-01-27"));
        } catch (ParseException e) {

        }
        return data;
    }
}
