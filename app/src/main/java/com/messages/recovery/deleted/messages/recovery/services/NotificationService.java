package com.messages.recovery.deleted.messages.recovery.services;

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
import android.provider.Telephony;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.messages.recovery.deleted.messages.recovery.R;
import com.messages.recovery.deleted.messages.recovery.activities.MainActivity;
import com.messages.recovery.deleted.messages.recovery.constants.ApplicationPackagesName;
import com.messages.recovery.deleted.messages.recovery.constants.Constant;
import com.messages.recovery.deleted.messages.recovery.constants.TableName;
import com.messages.recovery.deleted.messages.recovery.database.MyDataBaseHelper;

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
    private MyDataBaseHelper myDataBaseHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotification();
    }

  /*  @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String command = intent.getStringExtra(Constant.KEY_INTENT_START_STOP_SERVICE);
        if (command != null) {
            switch (command) {
                case "START": {
                    createNotification();
                    Log.d(TAG, "onStartCommand: Command to START");
                }
                break;
                case "STOP": {
                    *//*stopForeground(true);
                    stopSelf();*//*
                    Log.d(TAG, "onStartCommand: Command to STOP");
                }
                break;
            }

        }
        return START_STICKY;
    }*/

    private void createNotification() {
        Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
        notificationIntent.putExtra(Constant.KEY_INTENT_FROM_NOTIFICATION, "StopService");
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        Notification notification = new NotificationCompat.Builder(this, Constant.CHANNEL_ID)
                .setContentTitle(getResources().getString(R.string.app_name))
                .setContentText("Background service is running tap to stop.")
                .setSmallIcon(R.mipmap.ic_notification_round)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(1, notification);
    }


    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        if ((sbn.getNotification().flags & Notification.FLAG_GROUP_SUMMARY) != 0) {
            Log.d(TAG, "Ignore the notification FLAG_GROUP_SUMMARY");
            Log.d(TAG, "May be twice");
            return;
        }
        checkNotificationComeFrom(sbn);
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
    }


    private void checkNotificationComeFrom(StatusBarNotification sbn) {
        myDataBaseHelper = new MyDataBaseHelper(getApplicationContext());
        String packageName = sbn.getPackageName();
        if (packageName != null) {
            Bundle extras = sbn.getNotification().extras;
            if (extras != null) {
                if (packageName.equals(Telephony.Sms.getDefaultSmsPackage(getApplicationContext()))) {
                    long id = sbn.getId();
                    long timeStamp = sbn.getPostTime();
                    String title = extras.getString("android.title");
                    String message = extras.getCharSequence("android.text").toString();
                    String readStatus = "unRead";
                    if (title != null && !title.toLowerCase().equals("Mes sage not sent".toLowerCase())) {
                        boolean recordExists = myDataBaseHelper.checkIsRecordExist(TableName.TABLE_NAME_USER_DEFAULT, myDataBaseHelper.KEY_USER_TITLE, title);
                        if (!recordExists) {
                            getLargeIcon(extras);
                            myDataBaseHelper.insertUsers(TableName.TABLE_NAME_USER_DEFAULT, id, title, largeIconUri);
                            largeIconUri = "";
                        }
                        if (!message.contains("new messages")) {
                            myDataBaseHelper.insertMessages(TableName.TABLE_NAME_MESSAGES_DEFAULT, title, message, timeStamp, readStatus);
                        }
                        sendBroadCast(Constant.ACTION_INTENT_FILTER_DEFAULT_RECEIVER);
                    }
                } else {
                    switch (packageName) {
                        case ApplicationPackagesName.FACEBOOK_MESSENGER_PACK_NAME: {
                            long id = sbn.getId();
                            long timeStamp = sbn.getPostTime();
                            String title = extras.getString("android.title");
                            String message = extras.getCharSequence("android.text").toString();
                            String readStatus = "unRead";

                            if (title != null && !title.toLowerCase().equals("Messenger".toLowerCase())) {
                                boolean recordExists = myDataBaseHelper.checkIsRecordExist(TableName.TABLE_NAME_USER_FACEBOOK, myDataBaseHelper.KEY_USER_TITLE, title);
                                if (!recordExists) {
                                    getLargeIcon(extras);
                                    myDataBaseHelper.insertUsers(TableName.TABLE_NAME_USER_FACEBOOK, id, title, largeIconUri);
                                    largeIconUri = "";
                                }
                                if (!message.contains("new messages")) {
                                    myDataBaseHelper.insertMessages(TableName.TABLE_NAME_MESSAGES_FACEBOOK, title, message, timeStamp, readStatus);
                                }
                                sendBroadCast(Constant.ACTION_INTENT_FILTER_FACEBOOK_RECEIVER);
                            }
                        }
                        break;

                        case ApplicationPackagesName.INSTAGRAM_PACK_NAME: {

                            long id = sbn.getId();
                            long timeStamp = sbn.getPostTime();
                            String title = extras.getString("android.title");
                            String message = extras.getCharSequence("android.text").toString();
                            String readStatus = "unRead";

                            if (title != null) {
                                if (!title.toLowerCase().equals("Instagram".toLowerCase())) {
                                    boolean recordExists = myDataBaseHelper.checkIsRecordExist(TableName.TABLE_NAME_USER_INSTAGRAM, myDataBaseHelper.KEY_USER_TITLE, title);
                                    if (!recordExists) {
                                        getLargeIcon(extras);
                                        myDataBaseHelper.insertUsers(TableName.TABLE_NAME_USER_INSTAGRAM, id, title, largeIconUri);
                                        largeIconUri = "";
                                    }
                                    if (!message.contains("new messages")) {
                                        myDataBaseHelper.insertMessages(TableName.TABLE_NAME_MESSAGES_INSTAGRAM, title, message, timeStamp, readStatus);
                                    }
                                    sendBroadCast(Constant.ACTION_INTENT_FILTER_INSTAGRAM_RECEIVER);
                                }
                            }


                        }
                        break;
                        case ApplicationPackagesName.WHATSAPP_PACK_NAME: {
                            long id = sbn.getId();
                            long timeStamp = sbn.getPostTime();
                            String title = extras.getString("android.title");
                            String message = extras.getCharSequence("android.text").toString();
                            String readStatus = "unRead";
                            if (title != null) {
                                if (!title.equals("WhatsApp") && !title.toLowerCase().equals("Backup Paused".toLowerCase()) && !title.toLowerCase().equals("Backup in progress".toLowerCase())) {
                                    boolean recordExists = myDataBaseHelper.checkIsRecordExist(TableName.TABLE_NAME_USER_WHATS_APP, myDataBaseHelper.KEY_USER_TITLE, title);
                                    if (!recordExists) {
                                        getLargeIcon(extras);
                                        myDataBaseHelper.insertUsers(TableName.TABLE_NAME_USER_WHATS_APP, id, title, largeIconUri);
                                        largeIconUri = "";
                                    }
                                    if (!message.contains("new messages")) {
                                        myDataBaseHelper.insertMessages(TableName.TABLE_NAME_MESSAGES_WHATS_APP, title, message, timeStamp, readStatus);
                                    }
                                    sendBroadCast(Constant.ACTION_INTENT_FILTER_WHATS_APP_RECEIVER);
                                }
                            }
                        }
                        break;
                    }
                }
            }
        }

    }


    private void sendBroadCast(String action) {
        Intent intent = new Intent(action);/*
        intent.putExtra(Constant.KEY_INTENT_ID, id);
        intent.putExtra(Constant.KEY_INTENT_TITLE, title);
        intent.putExtra(Constant.KEY_INTENT_MESSAGE, message);
        intent.putExtra(Constant.KEY_INTENT_TIMESTAMP, timeStamp);
        intent.putExtra(Constant.KEY_INTENT_LATG_ICON_URI, largeIconUri);*/
        sendBroadcast(intent);
    }

    private void getLargeIcon(Bundle extras) {

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
            }
        } catch (Exception e) {
            Log.d(TAG, "Catch: " + e);
        }
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
        File file = new File(dir, "Profile_" + timeStamp + ".jpg");
        if (!dir.exists()) {
            dir.mkdir();
        }
        OutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
            largeIconUri = file.getAbsolutePath();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
        stopSelf();
        Log.d(TAG, "onDestroy: Service Destroyed");
    }
}