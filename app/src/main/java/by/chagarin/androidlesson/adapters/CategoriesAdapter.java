package by.chagarin.androidlesson.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import by.chagarin.androidlesson.Category;
import by.chagarin.androidlesson.R;


public class CategoriesAdapter extends SelectableAdapter<CategoriesAdapter.CardViewHolder> {

    private List<Category> categories;
    private CardViewHolder.ClickListener clickListener;


    public CategoriesAdapter(List<Category> categories, CardViewHolder.ClickListener clickListener) {
        this.categories = categories;
        this.clickListener = clickListener;
    }

    @Override
    public CategoriesAdapter.CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_list_item, parent, false);
        return new CategoriesAdapter.CardViewHolder(itemView, clickListener);
    }

    @Override
    public void onBindViewHolder(CategoriesAdapter.CardViewHolder holder, int position) {
        Category category = categories.get(position);
        holder.title.setText(category.getName());
        holder.selectedOverlay.setVisibility(isSelected(position) ? View.VISIBLE : View.INVISIBLE);
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

    private void removeItem(int position) {
        removeExpenses(position);
        notifyItemRemoved(position);
    }

    private void removeExpenses(int position) {
        if (categories.get(position) != null) {
            //удаляет запись из БД
            categories.get(position).delete();
            categories.remove(position);
        }
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public static class CardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        TextView title;
        ClickListener clickListener;
        View selectedOverlay;

        public CardViewHolder(View itemView, ClickListener clickListener) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            this.clickListener = clickListener;

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            selectedOverlay = itemView.findViewById(R.id.selected_overlay);

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
