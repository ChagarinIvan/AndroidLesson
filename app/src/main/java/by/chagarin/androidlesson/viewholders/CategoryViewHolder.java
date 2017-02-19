package by.chagarin.androidlesson.viewholders;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import by.chagarin.androidlesson.KindOfCategories;
import by.chagarin.androidlesson.R;
import by.chagarin.androidlesson.objects.Category;

public class CategoryViewHolder extends RecyclerView.ViewHolder {

    public CardView cardView;
    TextView title;
    View selectedProceed;
    View selectedPlace;

    public CategoryViewHolder(View itemView) {
        super(itemView);
        title = (TextView) itemView.findViewById(R.id.title);
        selectedProceed = itemView.findViewById(R.id.proceed_category);
        selectedPlace = itemView.findViewById(R.id.place_category);
        cardView = (CardView) itemView.findViewById(R.id.card_id);
    }

    public void bindToCategory(Category category) {
        if (TextUtils.equals(category.kind, KindOfCategories.getProceed())) {
            selectedProceed.setVisibility(View.VISIBLE);
        }
        if (TextUtils.equals(category.kind, KindOfCategories.getPlace())) {
            selectedPlace.setVisibility(View.VISIBLE);
        }
        title.setText(category.name);
    }
}
