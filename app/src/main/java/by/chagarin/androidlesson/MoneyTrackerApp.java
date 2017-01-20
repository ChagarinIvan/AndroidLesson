package by.chagarin.androidlesson;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.app.Application;

public class MoneyTrackerApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ActiveAndroid.initialize(this);
    }
}
