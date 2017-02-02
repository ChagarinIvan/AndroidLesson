package by.chagarin.androidlesson;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import org.androidannotations.annotations.EService;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.UiThread;

import java.util.concurrent.TimeUnit;

@EService
public class MyService extends Service {
    @SystemService
    NotificationManager notificationManager;

    public MyService() {
        super();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    protected void onHandleIntent(Intent intent) {
        showToast();
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        someTask();
        return super.onStartCommand(intent, flags, startId);
    }

    public void onCreate() {
        super.onCreate();
    }

    public void onDestroy() {
        super.onDestroy();
    }

    void someTask() {
        new Thread(new Runnable() {
            public void run() {
                for (int i = 1; i <= 150; i++) {
                    try {
                        TimeUnit.SECONDS.sleep(3);
                        showToast();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                stopSelf();
            }
        }).start();
    }

    @UiThread
    void showToast() {
        Notification notif = new Notification(R.drawable.ic_add_white_24dp, "Text in status bar", System.currentTimeMillis());

        Intent intent = new Intent(this, MainActivity_.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);

    }
}
