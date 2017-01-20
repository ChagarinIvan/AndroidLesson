package by.chagarin.androidlesson;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

//наследуемся от нашего селект адаптера
class TransactionAdapter extends SelectableAdapter<TransactionAdapter.CardViewHolder> {

    private List<Transaction> transactions;
    private CardViewHolder.ClickListener clickListener;

    TransactionAdapter(List<Transaction> transactions, CardViewHolder.ClickListener clickListener) {
        this.transactions = transactions;
        this.clickListener = clickListener;
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
        if (transactions.get(position) != null) {
            transactions.get(position).delete();
            transactions.remove(position);
        }
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }


    static class CardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        protected TextView name;
        private TextView sum;
        private TextView date;
        private ClickListener clickListener;
        View selectedOverlay;

        public CardViewHolder(View itemView, ClickListener clickListener) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.title);
            sum = (TextView) itemView.findViewById(R.id.price);
            date = (TextView) itemView.findViewById(R.id.date);
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