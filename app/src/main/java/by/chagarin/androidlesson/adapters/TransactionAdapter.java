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

import by.chagarin.androidlesson.R;
import by.chagarin.androidlesson.Transaction;

//наследуемся от нашего селект адаптера
public class TransactionAdapter extends SelectableAdapter<TransactionAdapter.CardViewHolder> {

    private List<Transaction> transactions;
    private CardViewHolder.ClickListener clickListener;
    private Context context;
    private int lastPosition = -1;

    public TransactionAdapter(List<Transaction> transactions, Context context, CardViewHolder.ClickListener clickListener) {
        this.transactions = transactions;
        this.clickListener = clickListener;
        this.context = context;
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new CardViewHolder(itemView, clickListener);
    }

    @Override
    public void onBindViewHolder(CardViewHolder holder, int position) {
        Transaction transaction = transactions.get(position);
        holder.name.setText(transaction.getTitle());
        holder.sum.setText(transaction.getPrice());
        holder.date.setText(transaction.getDate());
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
        if (transactions.get(position) != null) {
            //удаляет запись из БД
            transactions.get(position).delete();
            transactions.remove(position);
        }
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }


    public static class CardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        protected TextView name;
        private TextView sum;
        private TextView date;
        private ClickListener clickListener;
        View selectedOverlay;

        protected CardView cardView;

        public CardViewHolder(View itemView, ClickListener clickListener) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.title);
            sum = (TextView) itemView.findViewById(R.id.price);
            date = (TextView) itemView.findViewById(R.id.date);
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