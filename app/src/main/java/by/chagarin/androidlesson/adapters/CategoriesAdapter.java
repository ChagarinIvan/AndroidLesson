package by.chagarin.androidlesson.adapters;

import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import by.chagarin.androidlesson.Category;
import by.chagarin.androidlesson.KindOfCategories;
import by.chagarin.androidlesson.R;


public class CategoriesAdapter extends SelectableAdapter<CategoriesAdapter.CardViewHolder> {

    private List<Category> categories;
    private CardViewHolder.ClickListener clickListener;
    private Context context;
    private int lastPosition = -1;


    public CategoriesAdapter(List<Category> categories, Context context, CardViewHolder.ClickListener clickListener) {
        this.categories = categories;
        this.clickListener = clickListener;
        this.context = context;
    }

    public void setAnimations(View view, int position) {
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_up);
            view.startAnimation(animation);
            lastPosition = position;
        }
    }
    
    @Override
    public CategoriesAdapter.CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_list_item, parent, false);
        return new CategoriesAdapter.CardViewHolder(itemView, clickListener);
    }

    @Override
    public void onBindViewHolder(CategoriesAdapter.CardViewHolder holder, int position) {
        Category category = categories.get(position);
        if (TextUtils.equals(category.getKindOfCategories(), KindOfCategories.getProceed())) {
            holder.selectedProceed.setVisibility(View.VISIBLE);
        }
        if (TextUtils.equals(category.getKindOfCategories(), KindOfCategories.getPlace())) {
            holder.selectedPlace.setVisibility(View.VISIBLE);
        }
        holder.title.setText(category.getName());
        holder.selectedOverlay.setVisibility(isSelected(position) ? View.VISIBLE : View.INVISIBLE);
        setAnimations(holder.cardView, position);
    }

    public void removeItems(List<Integer> positions) {
        Collections.sort(positions, new Comparator<Integer>() {
            @Override
            public int compare(Integer lhs, Integer rhs) {
                return rhs - lhs;
            }
        });
        while (!positions.isEmpty()) {
            if (positions.size() == 1) {
                removeItem(positions.get(0));
                positions.remove(0);
            } else {
                for (int i = 0; i <= positions.size(); i++) {
                    removeItem(positions.get(0));
                    positions.remove(0);
                }
            }
        }
    }

    public void removeItem(int position) {
        removeExpenses(position);
        notifyItemRemoved(position);
    }

    private void removeExpenses(int position) {
        try {
            if (categories.get(position) != null) {
                //удаляет запись из БД
                categories.get(position).delete();
                categories.remove(position);
            }
        } catch (SQLiteConstraintException e) {
            Toast.makeText(context, context.getString(R.string.warning_sql), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public static class CardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        CardView cardView;
        TextView title;
        ClickListener clickListener;
        View selectedOverlay;
        View selectedProceed;
        View selectedPlace;

        public CardViewHolder(View itemView, ClickListener clickListener) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            this.clickListener = clickListener;

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            selectedOverlay = itemView.findViewById(R.id.selected_overlay);
            selectedProceed = itemView.findViewById(R.id.proceed_category);
            selectedPlace = itemView.findViewById(R.id.place_category);
            cardView = (CardView) itemView.findViewById(R.id.card_id);
        }

        @Override
        public void onClick(View v) {
            if (clickListener != null) {
                clickListener.onItemClick(getPosition());
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (clickListener != null) {
                clickListener.onItemLongClick(getPosition());
            }
            return false;
        }

        public interface ClickListener {
            void onItemClick(int position);

            boolean onItemLongClick(int position);
        }
    }
}
