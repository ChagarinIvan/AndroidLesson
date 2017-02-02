package by.chagarin.androidlesson;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;

import org.androidannotations.annotations.EService;
import org.androidannotations.annotations.UiThread;

import java.util.concurrent.TimeUnit;

@EService
public class MyService extends Service {
    private static final int NOTIFY_ID = 101;

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
                //noinspection InfiniteLoopStatement
                while (true) {
                    try {
                        TimeUnit.HOURS.sleep(8);
                        showToast();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @UiThread
    void showToast() {
        Context context = getApplicationContext();

        Intent notificationIntent = new Intent(context, MainActivity_.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context,
                0, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        Resources res = context.getResources();
        Notification.Builder builder = new Notification.Builder(context);

        builder.setContentIntent(contentIntent)
                .setSmallIcon(R.drawable.ic_attach_money)
                // большая картинка
                .setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.drawer_header))
                //.setTicker(res.getString(R.string.warning)) // текст в строке состояния
                .setTicker(getString(R.string.alarm_message))
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                //.setContentTitle(res.getString(R.string.notifytitle)) // Заголовок уведомления
                .setContentTitle(getString(R.string.notification_title))
                //.setContentText(res.getString(R.string.notifytext))
                .setContentText(getString(R.string.notifaction_text)); // Текст уведомления

        // Notification notification = builder.getNotification(); // до API 16
        Notification notification = builder.build();

        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFY_ID, notification);
    }
}