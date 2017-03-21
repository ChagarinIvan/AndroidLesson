package by.chagarin.androidlesson.viewholders;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import by.chagarin.androidlesson.R;
import by.chagarin.androidlesson.objects.Transfer;


public class TransferViewHolder extends RecyclerView.ViewHolder {

    protected TextView name;
    private TextView sum;
    private TextView date;
    private TextView fromCategoryKey;
    private TextView toCategoryKey;

    public CardView cardView;

    public TransferViewHolder(View itemView) {
        super(itemView);
        name = (TextView) itemView.findViewById(R.id.title);
        sum = (TextView) itemView.findViewById(R.id.price);
        date = (TextView) itemView.findViewById(R.id.date);
        fromCategoryKey = (TextView) itemView.findViewById(R.id.from_category);
        toCategoryKey = (TextView) itemView.findViewById(R.id.to_category);
        cardView = (CardView) itemView.findViewById(R.id.card_id);
    }

    public void bindToTransfer(Transfer transfer) {
        name.setText(transfer.title);
        sum.setText(transfer.price);
        date.setText(transfer.date);
        fromCategoryKey.setText(transfer.categoryPlaceFromKey);
        toCategoryKey.setText(transfer.categoryPlaceToKey);
    }
}
