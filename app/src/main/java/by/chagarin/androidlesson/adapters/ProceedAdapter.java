package by.chagarin.androidlesson.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import by.chagarin.androidlesson.Proceed;
import by.chagarin.androidlesson.R;

public class ProceedAdapter extends SelectableAdapter<by.chagarin.androidlesson.adapters.ProceedAdapter.CardViewHolder> {

    private List<Proceed> proceeds;
    private ProceedAdapter.CardViewHolder.ClickListener clickListener;
    private Context context;
    private int lastPosition = -1;

    public ProceedAdapter(List<Proceed> proceeds, Context context, ProceedAdapter.CardViewHolder.ClickListener clickListener) {
        this.proceeds = proceeds;
        this.clickListener = clickListener;
        this.context = context;
    }

    @Override
    public ProceedAdapter.CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new ProceedAdapter.CardViewHolder(itemView, clickListener);
    }

    @Override
    public void onBindViewHolder(ProceedAdapter.CardViewHolder holder, int position) {
        Proceed proceed = proceeds.get(position);
        holder.name.setText(proceed.getTitle());
        holder.sum.setText(proceed.getPrice());
        holder.date.setText(proceed.getDate());
        holder.comment.setText(proceed.getComment());
        holder.selectedOverlay.setVisibility(isSelected(position) ? View.VISIBLE : View.INVISIBLE);
        setAnimations(holder.cardView, position);
    }

    /**
     * метод устанавливает анимацию элементу
     *
     * @param view
     * @param position
     */
    public void setAnimations(View view, int position) {
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_up);
            view.startAnimation(animation);
            lastPosition = position;
        }
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
        if (proceeds.get(position) != null) {
            //удаляет запись из БД
            proceeds.get(position).delete();
            proceeds.remove(position);
        }
    }

    @Override
    public int getItemCount() {
        return proceeds.size();
    }


    public static class CardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        protected TextView name;
        private TextView sum;
        private TextView date;
        private TextView comment;
        private ProceedAdapter.CardViewHolder.ClickListener clickListener;
        View selectedOverlay;

        protected CardView cardView;

        public CardViewHolder(View itemView, ProceedAdapter.CardViewHolder.ClickListener clickListener) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.title);
            sum = (TextView) itemView.findViewById(R.id.price);
            date = (TextView) itemView.findViewById(R.id.date);
            comment = (TextView) itemView.findViewById(R.id.comment);
            this.clickListener = clickListener;
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            selectedOverlay = itemView.findViewById(R.id.selected_overlay);
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
