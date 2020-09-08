package com.example.recoverdeletedmessages.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.recoverdeletedmessages.R;
import com.example.recoverdeletedmessages.activities.MainActivity;
import com.example.recoverdeletedmessages.constants.ApplicationPackagesName;
import com.example.recoverdeletedmessages.constants.Constant;

public class NotificationService extends NotificationListenerService {

    private String TAG = this.getClass().getSimpleName();
    private final static String default_notification_channel_id = "default";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotification();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    private void createNotification() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(this, Constant.CHANNEL_ID)
                .setContentTitle("Message Recovery")
                .setContentText("This Application will read all of your comming notification")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(1, notification);
    }


    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {


        checkNotificationComeFrom(sbn);

        Log.i(TAG, "Notification Success :  id" + sbn.getId() + " \t " + " \t " + sbn.getPackageName());
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.i(TAG, "********** onNotificationRemoved");
        Log.i(TAG, "ID :" + sbn.getId() + " \t " + sbn.getNotification().tickerText + " \t " + sbn.getPackageName());
    }

    private void checkNotificationComeFrom(StatusBarNotification sbn) {
        String packageName = sbn.getPackageName();
        Bundle extras = sbn.getNotification().extras;
        long id = sbn.getId();
        long timeStamp = sbn.getPostTime();
        String title = "TITLE:" + extras.getString("android.title");
        String message = extras.getCharSequence("android.text").toString();
        switch (packageName) {
            case ApplicationPackagesName.FACEBOOK_PACK_NAME: {
            }
            break;
            case ApplicationPackagesName.FACEBOOK_MESSENGER_PACK_NAME: {
            }
            break;
            case ApplicationPackagesName.INSTAGRAM_PACK_NAME: {
            }
            break;
            case ApplicationPackagesName.WHATSAPP_PACK_NAME: {
                sendBroadCast(Constant.ACTION_INTENT_FILTER_WHATS_APP_RECEIVER, id, title, message, timeStamp);
            }
            break;
            case ApplicationPackagesName.MY_NOTIFICATION_SENDER: {
                sendBroadCast(Constant.ACTION_INTENT_FILTER_WHATS_APP_RECEIVER, id, title, message, timeStamp);
            }
            break;

        }

    }

    private void sendBroadCast(String action, long id, String title, String message, long timeStamp) {
        Intent intent = new Intent(action);
        intent.putExtra(Constant.KEY_INTENT_ID, id);
        intent.putExtra(Constant.KEY_INTENT_TITLE, title);
        intent.putExtra(Constant.KEY_INTENT_MESSAGE, message);
        intent.putExtra(Constant.KEY_INTENT_TIMESTAMP, timeStamp);
        sendBroadcast(intent);
    }


}