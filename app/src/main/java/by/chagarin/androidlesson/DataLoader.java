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
import java.util.concurrent.Callable;

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
    public static boolean isShow;
    public DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    public static User user;
    public static List<Transaction> transactionList;
    public static List<Proceed> proceedList;
    public static List<Transfer> transferList;
    public static List<Category> categoryList;

    public void writeNewUser(User person) {
        Map<String, Object> postValues = person.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/" + USERS + "/" + person.userKey, postValues);
        mDatabase.updateChildren(childUpdates);
    }

    public static String getUid() {
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

    public void calcAndSetCash(final Object cash, final boolean variant) {
        mDatabase.addListenerForSingleValueEvent(new AllDataLoaderListener(new Callable() {
            @Override
            public Object call() throws Exception {
                float cashCount = calcCashCount(categoryList, transactionList, proceedList, transferList, user.isShow);
                setCash(cashCount, cash, variant);
                return null;
            }
                })
        );
    }

    private float calcCashCount(List<Category> categoryList, List<Transaction> transactionList, List<Proceed> proceedList, List<Transfer> transferList, boolean isShow) {
        float count = 0;
        if (isShow) {
            for (Proceed proceed : proceedList) {
                count += Float.valueOf(proceed.price);
            }
            for (Transaction transaction : transactionList) {
                count -= Float.valueOf(transaction.price);
            }
            return count;
        } else {
            List<Category> data = KindOfCategories.sortData(categoryList, KindOfCategories.getPlace(), isShow);
            List<String> categoriesKeys = new ArrayList<>();
            for (Category cat : data) {
                categoriesKeys.add(cat.key);
            }
            for (Proceed pr : proceedList) {
                if (categoriesKeys.contains(pr.categoryPlaceKey)) {
                    count += Float.valueOf(pr.price);
                }
            }
            for (Transaction tr : transactionList) {
                if (categoriesKeys.contains(tr.categoryPlaceKey)) {
                    count -= Float.valueOf(tr.price);
                }
            }
            for (Transfer trans : transferList) {
                if (categoriesKeys.contains(trans.categoryPlaceFromKey)) {
                    if (!categoriesKeys.contains(trans.categoryPlaceToKey)) {
                        count -= Float.valueOf(trans.price);
                    }
                } else {
                    if (categoriesKeys.contains(trans.categoryPlaceToKey)) {
                        count += Float.valueOf(trans.price);
                    }
                }
            }
            return count;
        }
    }

    private void setCash(float cashCount, Object cash, boolean b) {
        if (b) {
            MenuItem menuItem = (MenuItem) cash;
            menuItem.setTitle(String.format(Locale.ENGLISH, "%.2f", cashCount));
        } else {
            PieChart pie = (PieChart) cash;
            pie.setCenterText(String.format(Locale.ENGLISH, "Общий баланс %.2f BYN", cashCount));
        }
    }

    public static class AllDataLoaderListener implements ValueEventListener {
        private final Callable func;

        public AllDataLoaderListener(final Callable func) {
            this.func = func;
        }

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            user = dataSnapshot.child(USERS).child(getUid()).getValue(User.class);
            transactionList = new ArrayList<>();
            transferList = new ArrayList<>();
            categoryList = new ArrayList<>();
            proceedList = new ArrayList<>();

            for (DataSnapshot areaSnapshot : dataSnapshot.child(TRANSACTIONS).getChildren()) {
                transactionList.add(areaSnapshot.getValue(Transaction.class));
            }
            for (DataSnapshot areaSnapshot : dataSnapshot.child(PROCEEDS).getChildren()) {
                proceedList.add(areaSnapshot.getValue(Proceed.class));
            }
            for (DataSnapshot areaSnapshot : dataSnapshot.child(TRANSFERS).getChildren()) {
                transferList.add(areaSnapshot.getValue(Transfer.class));
            }
            for (DataSnapshot areaSnapshot : dataSnapshot.child(CATEGORIES).getChildren()) {
                categoryList.add(areaSnapshot.getValue(Category.class));
            }
            try {
                func.call();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    }
}

