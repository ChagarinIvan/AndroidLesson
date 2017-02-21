package by.chagarin.androidlesson;


import android.view.MenuItem;

import com.github.mikephil.charting.charts.PieChart;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.androidannotations.annotations.EBean;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import by.chagarin.androidlesson.objects.Category;
import by.chagarin.androidlesson.objects.Proceed;
import by.chagarin.androidlesson.objects.Transaction;
import by.chagarin.androidlesson.objects.Transfer;
import by.chagarin.androidlesson.objects.User;

@EBean(scope = EBean.Scope.Singleton)
public class DataLoader {
    public static final DateFormat df = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);
    public static final String CATEGORIES = "categories";
    public static final String ACTIONS = "actions";
    public static final String TRANSACTIONS = ACTIONS + "/transactions";
    public static final String PROCEEDS = ACTIONS + "/proceeds";
    public static final String TRANSFERS = ACTIONS + "/transfers";
    public static final String USERS = "users";
    public DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    public void writeNewUser(User person) {
        Map<String, Object> postValues = person.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/" + USERS + "/" + person.userKey, postValues);
        mDatabase.updateChildren(childUpdates);
    }

    public String getUid() {
        //noinspection ConstantConditions
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public Query getQuery(String key) {
        return mDatabase.child(key);
    }

    public void writeNewTransaction(Transaction createTransction) {
        Map<String, Object> postValues = createTransction.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/" + TRANSACTIONS + "/" + createTransction.key, postValues);
        mDatabase.updateChildren(childUpdates);
    }


    public void writeNewProceed(Proceed proceed) {
        Map<String, Object> postValues = proceed.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/" + PROCEEDS + "/" + proceed.key, postValues);
        mDatabase.updateChildren(childUpdates);
    }

    public void writeNewCategory(Category category) {
        Map<String, Object> postValues = category.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/" + CATEGORIES + "/" + category.key, postValues);
        mDatabase.updateChildren(childUpdates);
    }

    public void writeNewTransfer(Transfer transfer) {
        Map<String, Object> postValues = transfer.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/" + TRANSFERS + "/" + transfer.key, postValues);
        mDatabase.updateChildren(childUpdates);
    }

    public void calcAndSetCash(final PieChart cash) {
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final List<Transaction> transactionList = new ArrayList<>();
                final List<Proceed> proceedList = new ArrayList<>();

                for (DataSnapshot areaSnapshot : dataSnapshot.child(TRANSACTIONS).getChildren()) {
                    transactionList.add(areaSnapshot.getValue(Transaction.class));
                }
                for (DataSnapshot areaSnapshot : dataSnapshot.child(PROCEEDS).getChildren()) {
                    proceedList.add(areaSnapshot.getValue(Proceed.class));
                }
                float cashCount = calcCashCount(transactionList, proceedList);
                cash.setCenterText(String.format(Locale.ENGLISH, "Общий баланс %.2f BYN", cashCount));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void calcAndSetCash(final MenuItem cash) {
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final List<Transaction> transactionList = new ArrayList<>();
                final List<Proceed> proceedList = new ArrayList<>();

                for (DataSnapshot areaSnapshot : dataSnapshot.child(TRANSACTIONS).getChildren()) {
                    transactionList.add(areaSnapshot.getValue(Transaction.class));
                }
                for (DataSnapshot areaSnapshot : dataSnapshot.child(PROCEEDS).getChildren()) {
                    proceedList.add(areaSnapshot.getValue(Proceed.class));
                }
                float cashCount = calcCashCount(transactionList, proceedList);
                cash.setTitle(String.format(Locale.ENGLISH, "%.2f", cashCount));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private float calcCashCount(List<Transaction> transactionList, List<Proceed> proceedList) {
        float count = 0;
        for (Proceed proceed : proceedList) {
            count += Float.valueOf(proceed.price);
        }
        for (Transaction transaction : transactionList) {
            count -= Float.valueOf(transaction.price);
        }
        return count;
    }
}
