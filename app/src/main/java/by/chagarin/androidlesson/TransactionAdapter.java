package by.chagarin.androidlesson;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;


public class TransactionAdapter extends ArrayAdapter<Transaction> {
    private List<Transaction> transactions;

    public TransactionAdapter(Context context, List<Transaction> resource) {
        super(context, 0, resource);
        this.transactions = resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Transaction transaction = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }
        RelativeLayout listItem = (RelativeLayout) convertView.findViewById(R.id.listItem);
        TextView title = (TextView) convertView.findViewById(R.id.title);
        TextView price = (TextView) convertView.findViewById(R.id.price);
        TextView date = (TextView) convertView.findViewById(R.id.date);
        if (position % 2 == 0) {
            listItem.setBackgroundColor(Color.RED);
        } else {
            listItem.setBackgroundColor(Color.GREEN);
        }
        title.setText(transaction.getTitle());
        price.setText(transaction.getPrice());
        date.setText(transaction.getDate());

        return convertView;
    }
}
