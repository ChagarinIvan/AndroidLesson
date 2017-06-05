package by.chagarin.androidlesson.viewholders;


import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import by.chagarin.androidlesson.R;
import by.chagarin.androidlesson.objects.Proceed;

public class ProceedViewHolder extends RecyclerView.ViewHolder {

    protected TextView name;
    private TextView sum;
    private TextView date;
    private TextView comment;
    private ImageView userIcon;

    public CardView cardView;

    public ProceedViewHolder(View itemView) {
        super(itemView);
        name = (TextView) itemView.findViewById(R.id.title);
        sum = (TextView) itemView.findViewById(R.id.price);
        date = (TextView) itemView.findViewById(R.id.date);
        comment = (TextView) itemView.findViewById(R.id.comment);
        cardView = (CardView) itemView.findViewById(R.id.card_id);
        userIcon = (ImageView) itemView.findViewById(R.id.user_icon);
    }

    public void bindToProceed(Proceed proceed) {
        name.setText(proceed.title);
        sum.setText(proceed.price);
        date.setText(proceed.date);
        comment.setText(proceed.comment);
        userIcon.setImageBitmap(proceed.getUserIcon());
    }
}
