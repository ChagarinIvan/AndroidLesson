package by.chagarin.androidlesson.viewholders;


import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import by.chagarin.androidlesson.R;
import by.chagarin.androidlesson.objects.Transaction;

public class TransactionViewHolder extends RecyclerView.ViewHolder {

    protected TextView name;
    private TextView sum;
    private TextView date;
    private TextView comment;
    private ImageView userIcon;

    public CardView cardView;

    public TransactionViewHolder(View itemView) {
        super(itemView);
        name = (TextView) itemView.findViewById(R.id.title);
        sum = (TextView) itemView.findViewById(R.id.price);
        date = (TextView) itemView.findViewById(R.id.date);
        comment = (TextView) itemView.findViewById(R.id.comment);
        cardView = (CardView) itemView.findViewById(R.id.card_id);
        userIcon = (ImageView) itemView.findViewById(R.id.user_icon);
    }

    public void bindToTransaction(Transaction transaction) {
        name.setText(transaction.title);
        sum.setText(transaction.price);
        date.setText(transaction.date);
        comment.setText(transaction.comment);
        userIcon.setImageBitmap(transaction.getUserIcon());
    }
}
