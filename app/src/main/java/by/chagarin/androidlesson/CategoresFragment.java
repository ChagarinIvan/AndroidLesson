package by.chagarin.androidlesson;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class CategoresFragment extends Fragment {
    private ListView listView;
    private ArrayAdapter<String> transactionAdapter;
    private List<Category> data = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View inflate = inflater.inflate(R.layout.fragment_transactions, container, false);
        List<Category> adapterData = getDataList();
        String[] data = convert(adapterData);
        transactionAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, data);
        listView = (ListView) inflate.findViewById(R.id.listview);
        listView.setAdapter(transactionAdapter);
        return inflate;
    }

    private String[] convert(List<Category> adapterData) {
        String[] data = new String[adapterData.size()];
        int n = 0;
        for (Category category : adapterData) {
            data[n++] = category.getName();
        }
        return data;
    }

    private List<Category> getDataList() {
        data.add(new Category("магазин"));
        data.add(new Category("интернет"));
        data.add(new Category("супермаркет"));
        data.add(new Category("бабушки"));
        return data;
    }
}
