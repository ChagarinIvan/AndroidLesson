package by.chagarin.androidlesson.viewholders;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import by.chagarin.androidlesson.R;
import by.chagarin.androidlesson.objects.Category;

public class CategoryViewHolder extends RecyclerView.ViewHolder {

    public CardView cardView;
    TextView title;

    public CategoryViewHolder(View itemView) {
        super(itemView);
        title = (TextView) itemView.findViewById(R.id.title);
        cardView = (CardView) itemView.findViewById(R.id.card_id);
    }

    public void bindToCategory(Category category) {
        title.setText(category.name);
    }
}
