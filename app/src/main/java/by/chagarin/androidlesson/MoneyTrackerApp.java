package by.chagarin.androidlesson;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.app.Application;

/**
 * Created by IME on 18.01.2017.
 */

public class MoneyTrackerApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ActiveAndroid.initialize(this);
    }
}
