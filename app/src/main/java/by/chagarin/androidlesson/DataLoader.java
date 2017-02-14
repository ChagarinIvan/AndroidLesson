package by.chagarin.androidlesson;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

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
    public static final String TRANSACTIONS = "transactions";
    public static final String PROCEEDS = "proceeds";
    public static final String TRANSFERS = "transfers";
    public static final String USERS = "users";
    public static List<Transfer> transferList = new ArrayList<>();
    public static List<Proceed> proceedList = new ArrayList<>();
    public static List<Transaction> transactionList = new ArrayList<>();
    public static List<Category> categoryList = new ArrayList<>();
    public DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    public boolean isWorkc = false;

    public float cashCount;


    public void writeNewUser(User person) {
        Map<String, Object> postValues = person.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/" + USERS + "/" + person.userKey, postValues);
        mDatabase.updateChildren(childUpdates);
    }

    //    @Bean
//    Base base;
//
//    public String getCashCount() {
//        calcCash();
//        return String.format(Locale.ENGLISH, "%.2f", cashCount) + " BYN";
//    }
//
    public String getUid() {
        //noinspection ConstantConditions
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    //
//    public void loadData() {
//        isWorkc = true;
//        Query queryRef = getQuery(mDatabase, CATEGORIES);
//        queryRef.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(DataSnapshot snapshot, String previousChild) {
//                //noinspection unchecked
//                categoryList.add(createCategory(snapshot));
//                base.doSomething();
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot) {
//                removeCategory(createCategory(dataSnapshot));
//                base.removeElementToListeners();
//            }
//
//            @Override
//            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//        Query transactionQuery = getQuery(mDatabase, TRANSACTIONS);
//        transactionQuery.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(DataSnapshot snapshot, String previousChild) {
//                //noinspection unchecked
//                transactionList.add(createTransaction(snapshot));
//                base.doSomething();
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot) {
//                removeTransaction(createTransaction(dataSnapshot));
//                base.removeElementToListeners();
//            }
//
//            @Override
//            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//        final Query proceedQuery = getQuery(mDatabase, PROCEEDS);
//        proceedQuery.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(DataSnapshot snapshot, String previousChild) {
//                //noinspection unchecked
//                proceedList.add(createProceed(snapshot));
//                base.doSomething();
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot) {
//                removeProceed(createProceed(dataSnapshot));
//                base.removeElementToListeners();
//            }
//
//            @Override
//            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//        Query transferQue = getQuery(mDatabase, TRANSFERS);
//        transferQue.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(DataSnapshot snapshot, String previousChild) {
//                //noinspection unchecked
//                transferList.add(createTransfer(snapshot));
//                base.doSomething();
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot) {
//                removeTransfer(createTransfer(dataSnapshot));
//                base.removeElementToListeners();
//            }
//
//            @Override
//            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//    }
//
//    private void changedTransaction(Transaction transaction) {
//
//    }
//
//    private void removeTransfer(Transfer transfer) {
//        for (Transfer tf : transferList) {
//            if (tf.equals(transfer)) {
//                transferList.remove(tf);
//            }
//        }
//    }
//
//    private void removeCategory(Category category) {
//        for (Category cat : categoryList) {
//            if (cat.equals(category)) {
//                categoryList.remove(cat);
//            }
//        }
//    }
//
//    private void removeProceed(Proceed proceed) {
//        for (int n = 0; n < proceedList.size(); ) {
//            Proceed pr = proceedList.get(n);
//            if (pr.equals(proceed)) {
//                proceedList.remove(pr);
//                return;
//            } else {
//                n++;
//            }
//        }
//    }
//
//    private void removeTransaction(Transaction transaction) {
//        for (int n = 0; n < transferList.size(); ) {
//            Transaction pr = transactionList.get(n);
//            if (pr.equals(transaction)) {
//                transactionList.remove(pr);
//                return;
//            } else {
//                n++;
//            }
//        }
//    }
//
//    public DatabaseReference getmDatabase() {
//        return mDatabase;
//    }
//
//    private Proceed createProceed(DataSnapshot snapshot) {
//        String title = (String) snapshot.child("title").getValue();
//        String price = String.valueOf(snapshot.child("price").getValue());
//        Category categoryTransaction = createCategory(snapshot.child("categoryProceed"));
//        Category categoryPlace = createCategory(snapshot.child("categoryPlace"));
//        String date = (String) snapshot.child("date").getValue();
//        String comment = (String) snapshot.child("comment").getValue();
//        String uid = (String) snapshot.child("uid").getValue();
//        String author = (String) snapshot.child("author").getValue();
//        return new Proceed(title, price, date, comment, categoryTransaction, categoryPlace, uid, author);
//    }
//
//    private Transfer createTransfer(DataSnapshot snapshot) {
//        String price = String.valueOf(snapshot.child("price").getValue());
//        Category categoryPlaceFrom = createCategory(snapshot.child("categoryPlaceFrom"));
//        Category categoryPlaceTo = createCategory(snapshot.child("categoryPlaceTo"));
//        String date = (String) snapshot.child("date").getValue();
//        String uid = (String) snapshot.child("uid").getValue();
//        String author = (String) snapshot.child("author").getValue();
//        return new Transfer(price, date, categoryPlaceFrom, categoryPlaceTo, uid, author);
//    }
//
//    private Transaction createTransaction(DataSnapshot snapshot) {
//        String title = (String) snapshot.child("title").getValue();
//        String price = String.valueOf(snapshot.child("price").getValue());
//        Category categoryTransaction = createCategory(snapshot.child("categoryTransaction"));
//        Category categoryPlace = createCategory(snapshot.child("categoryPlace"));
//        String date = (String) snapshot.child("date").getValue();
//        String comment = (String) snapshot.child("comment").getValue();
//        String uid = (String) snapshot.child("uid").getValue();
//        String author = (String) snapshot.child("author").getValue();
//        return new Transaction(title,price,date,comment, categoryTransaction, categoryPlace, uid, author);
//    }
//
//    private Category createCategory(DataSnapshot snapshot) {
//        String name = (String) snapshot.child("name").getValue();
//        String kind = (String) snapshot.child("kind").getValue();
//        String author = (String) snapshot.child("author").getValue();
//        boolean isShow = (boolean) snapshot.child("isShow").getValue();
//        String key = snapshot.getKey();
//        return new Category(name, kind, author, isShow, key);
//    }
//
    public Query getQuery(String key) {
        return mDatabase.child(key);
    }

    public boolean checkCatrgory(int position) {
        //тут проверка на есть ли записи с данной категорией
        return true;
    }

    //
//    private void calcCash() {
//        cashCount = 0;
//        for (Proceed proceed : proceedList) {
//            cashCount += Float.valueOf(proceed.getPrice());
//        }
//        for (Transaction transaction : transactionList) {
//            cashCount -= Float.valueOf(transaction.getPrice());
//        }
//    }
//
//    public List<Proceed> getProceedes() {
//        return proceedList;
//    }
//
//    public List<Transaction> getTransactions() {
//        return transactionList;
//    }
//
//    public List<Category> getCategores() {
//        //отдаёт список категорий без системной категории
//        return categoryList;
//    }
//
//    public List<Transfer> getTransfers() {
//        return transferList;
//    }
//
//    public boolean checkCatrgory(int position) {
//        Category cat = categoryList.get(position);
//        for (Transaction tr : transactionList) {
//            if (TextUtils.equals(tr.getCategoryPlace().getName(), cat.getName()) || TextUtils.equals(tr.getCategoryTransaction().getName(), cat.getName())) {
//                return false;
//            }
//        }
//        for (Proceed pr : proceedList) {
//            if (TextUtils.equals(pr.getCategoryPlace().getName(), cat.getName()) || TextUtils.equals(pr.getCategoryProceedes().getName(), cat.getName())) {
//                return false;
//            }
//        }
//        for (Transfer tf : transferList) {
//            if (TextUtils.equals(tf.getCategoryPlaceFrom().getName(), cat.getName()) || TextUtils.equals(tf.getCategoryPlaceTo().getName(), cat.getName())) {
//                return false;
//            }
//        }
//        return true;
//    }
//
    public void writeNewTransaction(Transaction createTransction) {
        Map<String, Object> postValues = createTransction.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/" + TRANSACTIONS + "/" + createTransction.key, postValues);
        mDatabase.updateChildren(childUpdates);
    }

    //
//    public void writeNewProceed(Proceed proceed) {
//        String key = mDatabase.child(PROCEEDS).push().getKey();
//        Map<String, Object> postValues = proceed.toMap();
//        Map<String, Object> childUpdates = new HashMap<>();
//        childUpdates.put("/" + PROCEEDS + "/" + key, postValues);
//        mDatabase.updateChildren(childUpdates);
//    }
//
    public void writeNewCategory(Category category) {
        String key = mDatabase.child(CATEGORIES).push().getKey();
        category.key = key;
        Map<String, Object> postValues = category.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/" + CATEGORIES + "/" + category.key, postValues);
        mDatabase.updateChildren(childUpdates);
    }

    private void writeNewTransfer(String userId, String author, String cost, Category from, Category to) {
        //Transfer transfer = new Transfer(cost, df.format(new Date()), from, to, userId, author);
        String key = mDatabase.child(TRANSFERS).push().getKey();
        //Map<String, Object> postValues = transfer.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        //childUpdates.put("/" + TRANSFERS + "/" + key, postValues);
        mDatabase.updateChildren(childUpdates);
    }
}
