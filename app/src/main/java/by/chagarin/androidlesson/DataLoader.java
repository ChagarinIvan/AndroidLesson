package by.chagarin.androidlesson;


import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import org.androidannotations.annotations.EBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import by.chagarin.androidlesson.objects.Category;
import by.chagarin.androidlesson.objects.Proceed;
import by.chagarin.androidlesson.objects.Transaction;

@EBean(scope = EBean.Scope.Singleton)
public class DataLoader {
    public static final String CATEGORIES = "categories";
    public static final String TRANSACTIONS = "transactions";
    private static List<Proceed> proceedList = new ArrayList<>();
    private static List<Transaction> transactionList = new ArrayList<>();
    private static List<Category> categoryList = new ArrayList<>();
    private static Category systemCategory = new Category(Category.SYSTEM_CATEGORY, KindOfCategories.getSYSTEM());
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();


    private boolean isCashLoading = false;
    private boolean isProceedesLoading = false;
    private float cashCount;

    public Category getSystemCategories() {
        return systemCategory;
    }

    public List<Transaction> getTransactionsWithoutSystem() {
        List<Transaction> list = new ArrayList<>();
        for (Transaction tr : transactionList) {
            if (tr.getCategoryTransaction() != systemCategory) {
                list.add(tr);
            }
        }
        return list;
    }

    public List<Proceed> getProceedesWithoutSystem() {
        List<Proceed> list = new ArrayList<>();
        for (Proceed tr : proceedList) {
            if (tr.getCategoryProcees() != systemCategory) {
                list.add(tr);
            }
        }
        return list;
    }

    public void loadData() {
        Query queryRef = getQuery(mDatabase, CATEGORIES);
        queryRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChild) {
                Map<String, String> value = (Map<String, String>) snapshot.getValue();
                categoryList.add(createCategory(snapshot));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        Query transactionQuery = getQuery(mDatabase, TRANSACTIONS);
        transactionQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChild) {
                Map<String, String> value = (Map<String, String>) snapshot.getValue();
                transactionList.add(createTransaction(snapshot));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private Transaction createTransaction(DataSnapshot snapshot) {
        String title = (String) snapshot.child("title").getValue();
        String price = String.valueOf(snapshot.child("price").getValue());
        Category categoryTransaction = createCategory(snapshot.child("categoryTransaction"));
        Category categoryPlace = createCategory(snapshot.child("categoryPlace"));
        String date = (String) snapshot.child("date").getValue();
        String comment = (String) snapshot.child("comment").getValue();
        String uid = (String) snapshot.child("uid").getValue();
        String author = (String) snapshot.child("author").getValue();
        //return new Transaction(title,price,categoryTransaction,categoryPlace,date,comment);
        return new Transaction(title,price,date,comment, categoryTransaction, categoryPlace, uid, author);
    }

    private Category createCategory(DataSnapshot snapshot) {
        String name = (String) snapshot.child("name").getValue();
        String kind = (String) snapshot.child("kind").getValue();
        String uid = (String) snapshot.child("uid").getValue();
        String author = (String) snapshot.child("author").getValue();
        return new Category(name, kind, uid, author);
    }

    public Query getQuery(DatabaseReference mDatabase, String key) {
        return mDatabase.child(key);
    }

//    public interface Callbacks {
//        void onTaskFinished();
//    }
//
//    private static Callbacks sDummyCallbacks = new Callbacks() {
//        public void onTaskFinished() {
//        }
//    };

//    public void loadCategores() {
//        fragment.getLoaderManager().restartLoader(0, null, new LoaderManager.LoaderCallbacks<List<Category>>() {
//
//            /**
//             * прозодит в бекграуде
//             */
//            @Override
//            public Loader<List<Category>> onCreateLoader(int id, Bundle args) {
//                final AsyncTaskLoader<List<Category>> loader = new AsyncTaskLoader<List<Category>>(fragment.getActivity()) {
//                    @Override
//                    public List<Category> loadInBackground() {
//                        return Category.getDataList();
//                    }
//                };
//                //важно
//                loader.forceLoad();
//                return loader;
//            }
//
//            /**
//             * в основном потоке после загрузки
//             */
//            @Override
//            public void onLoadFinished(Loader<List<Category>> loader, List<Category> data) {
//                categoryList = data;
//                loadProceedes();
//            }
//
//            @Override
//            public void onLoaderReset(Loader<List<Category>> loader) {
//
//            }
//        });
//    }
//
//    public void loadTransactions() {
//        fragment.getLoaderManager().restartLoader(0, null, new LoaderManager.LoaderCallbacks<List<Transaction>>() {
//
//            /**
//             * прозодит в бекграуде
//             */
//            @Override
//            public Loader<List<Transaction>> onCreateLoader(int id, Bundle args) {
//                final AsyncTaskLoader<List<Transaction>> loader = new AsyncTaskLoader<List<Transaction>>(fragment.getActivity()) {
//                    @Override
//                    public List<Transaction> loadInBackground() {
//                        return Transaction.getDataList();
//                    }
//                };
//                //важно
//                loader.forceLoad();
//                return loader;
//            }
//
//            /**
//             * в основном потоке после загрузки
//             */
//            @Override
//            public void onLoadFinished(Loader<List<Transaction>> loader, List<Transaction> data) {
//                transactionList = data;
//                loadCategores();
//            }
//
//            @Override
//            public void onLoaderReset(Loader<List<Transaction>> loader) {
//
//            }
//        });
//    }
//
//    private void goesToCallBack() {
//        //проверяем создана ли системная категория
//        if (findSysCat()) {
//            mCallbacks.onTaskFinished();
//        } else {
//            new Category(Category.SYSTEM_CATEGORY, KindOfCategories.getSYSTEM()).save();
//            loadTransactions();
//        }
//    }
//
//    private boolean findSysCat() {
//        for (Category category : categoryList) {
//            if (TextUtils.equals(category.getName(), Category.SYSTEM_CATEGORY) && (TextUtils.equals(category.getKind(), KindOfCategories.getSYSTEM()))) {
//                systemCategory = category;
//                return true;
//            }
//        }
//        return false;
//    }
//
//    public String calcCash() {
//        cashCount = 0;
//        for (Proceed proceed : proceedList) {
//            cashCount += Float.valueOf(proceed.getPrice());
//        }
//        for (Transaction transaction : transactionList) {
//            cashCount -= Float.valueOf(transaction.getPrice());
//        }
//        return String.format(Locale.ENGLISH, "%.2f", cashCount) + " BYN";
//    }
//
//    public void loadProceedes() {
//        fragment.getLoaderManager().restartLoader(0, null, new LoaderManager.LoaderCallbacks<List<Proceed>>() {
//
//            /**
//             * прозодит в бекграуде
//             */
//            @Override
//            public Loader<List<Proceed>> onCreateLoader(int id, Bundle args) {
//                final AsyncTaskLoader<List<Proceed>> loader = new AsyncTaskLoader<List<Proceed>>(fragment.getActivity()) {
//                    @Override
//                    public List<Proceed> loadInBackground() {
//                        return Proceed.getDataList();
//                    }
//                };
//                //важно
//                loader.forceLoad();
//                return loader;
//            }
//
//            /**
//             * в основном потоке после загрузки
//             */
//            @Override
//            public void onLoadFinished(Loader<List<Proceed>> loader, List<Proceed> data) {
//                proceedList = data;
//                goesToCallBack();
//            }
//
//            @Override
//            public void onLoaderReset(Loader<List<Proceed>> loader) {
//
//            }
//        });
//    }

    public List<Proceed> getProceedes() {
        return proceedList;
    }

    public List<Transaction> getTransactions() {
        return transactionList;
    }

    public List<Category> getCategores() {
        //отдаёт список категорий без системной категории
        return KindOfCategories.sortDataWithout(categoryList, KindOfCategories.getSYSTEM());
    }
}
