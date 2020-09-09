package com.example.recoverdeletedmessages.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.example.recoverdeletedmessages.R;
import com.example.recoverdeletedmessages.activities.MainActivity;
import com.example.recoverdeletedmessages.constants.ApplicationPackagesName;
import com.example.recoverdeletedmessages.constants.Constant;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NotificationService extends NotificationListenerService {

    private String TAG = this.getClass().getSimpleName();
    private final static String default_notification_channel_id = "default";
    private String largeIconUri = "";

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


//        checkNotificationComeFrom(sbn);

        Log.i(TAG, "Notification Success :" + sbn.getPackageName() + "Larg Icon URi" + largeIconUri);
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.i(TAG, "********** onNotificationRemoved");
        Log.i(TAG, "ID :" + sbn.getId() + " \t " + sbn.getNotification().tickerText + " \t " + sbn.getPackageName());
        checkNotificationComeFrom(sbn);
    }

    private void checkNotificationComeFrom(StatusBarNotification sbn) {
        String packageName = sbn.getPackageName();
        Bundle extras = sbn.getNotification().extras;

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Icon iconLarge = extras.getParcelable(Notification.EXTRA_LARGE_ICON);
                Drawable drawable = null;
                Bitmap bitmap = null;
                if (iconLarge != null) {

                    drawable = iconLarge.loadDrawable(this);
                    bitmap = drawableToBitmap(drawable);
                    saveImage(bitmap);
                }
                if (bitmap != null) {
                    Toast.makeText(this, "bitmap get success", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "bitmap get null", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            Log.d(TAG, "Catch: " + e);
        }


        long id = sbn.getId();
        long timeStamp = sbn.getPostTime();
        String title = extras.getString("android.title");
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
        intent.putExtra(Constant.KEY_INTENT_LATG_ICON_URI, largeIconUri);
        sendBroadcast(intent);
    }


    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public void saveImage(Bitmap bitmap) {
        File rootDir = Environment.getExternalStorageDirectory();
        File dir = new File(rootDir.getAbsolutePath() + "/ZeeshanRecovery");
        final String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        File file = new File(dir, "Large_" + timeStamp + ".png");
        if (!dir.exists()) {
            dir.mkdir();
        }
        OutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
            largeIconUri = file.getAbsolutePath();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            assert outputStream != null;
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}