package by.chagarin.androidlesson.viewholders;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import by.chagarin.androidlesson.DataLoader;
import by.chagarin.androidlesson.R;
import by.chagarin.androidlesson.objects.Transfer;


public class TransferViewHolder extends RecyclerView.ViewHolder {

    private ImageView userIcon;
    private TextView sum;
    private TextView date;
    private TextView fromCategoryKey;
    private TextView toCategoryKey;
    public DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    public CardView cardView;


    public TransferViewHolder(View itemView) {
        super(itemView);
        sum = (TextView) itemView.findViewById(R.id.price);
        date = (TextView) itemView.findViewById(R.id.date);
        fromCategoryKey = (TextView) itemView.findViewById(R.id.from_category);
        toCategoryKey = (TextView) itemView.findViewById(R.id.to_category);
        cardView = (CardView) itemView.findViewById(R.id.card_id);
        userIcon = (ImageView) itemView.findViewById(R.id.user_icon);
    }

    public void bindToTransfer(final Transfer transfer) {
        mDatabase.child(DataLoader.CATEGORIES).child(DataLoader.PLACES).child(transfer.categoryPlaceFromKey).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final String fromCategory = dataSnapshot.getValue(String.class);
                mDatabase.child(DataLoader.CATEGORIES).child(DataLoader.PLACES).child(transfer.categoryPlaceToKey).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final String toCategory = dataSnapshot.getValue(String.class);
                        userIcon.setImageBitmap(transfer.getUserIcon());
                        sum.setText(transfer.price);
                        date.setText(transfer.date);
                        fromCategoryKey.setText(fromCategory);
                        toCategoryKey.setText(toCategory);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
