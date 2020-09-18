package com.messages.recovery.deleted.messages.recovery.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.messages.recovery.deleted.messages.recovery.constants.TableName;
import com.messages.recovery.deleted.messages.recovery.models.Messages;
import com.messages.recovery.deleted.messages.recovery.models.Users;

import java.util.ArrayList;

import static com.messages.recovery.deleted.messages.recovery.constants.TableName.TABLE_NAME_MESSAGES_DEFAULT;
import static com.messages.recovery.deleted.messages.recovery.constants.TableName.TABLE_NAME_MESSAGES_FACEBOOK;
import static com.messages.recovery.deleted.messages.recovery.constants.TableName.TABLE_NAME_MESSAGES_INSTAGRAM;
import static com.messages.recovery.deleted.messages.recovery.constants.TableName.TABLE_NAME_MESSAGES_WHATS_APP;
import static com.messages.recovery.deleted.messages.recovery.constants.TableName.TABLE_NAME_USER_DEFAULT;
import static com.messages.recovery.deleted.messages.recovery.constants.TableName.TABLE_NAME_USER_FACEBOOK;
import static com.messages.recovery.deleted.messages.recovery.constants.TableName.TABLE_NAME_USER_INSTAGRAM;
import static com.messages.recovery.deleted.messages.recovery.constants.TableName.TABLE_NAME_USER_WHATS_APP;


public class MyDataBaseHelper extends SQLiteOpenHelper {

    // Logcat tag
    public static final String LOG = "DatabaseHelper";
    // Database Version
    public static final int DATABASE_VERSION = 1;
    // Database Name
    public static final String DATABASE_NAME = "MyDataBase";

    // Table Names


    // Common column names
    public static final String KEY_ID = "id";
    public static final String KEY_USER_TITLE = "user_title";
    public static final String KEY_USER_READ_STATUS = "read_status";

    // NOTES Table - column nmaes
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_TIME = "time";
    public static final String KEY_LARGE_ICON_URI = "larg_icon_uri";

    private static final String CREATE_TABLE_USER_WHATS_APP = "CREATE TABLE IF NOT EXISTS "
            + TABLE_NAME_USER_WHATS_APP + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_USER_TITLE + " TEXT , " + KEY_LARGE_ICON_URI + " TEXT" + ")";


    // Tag table create statement
    private static final String CREATE_TABLE_MESSAGES_WHATS_APP = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_MESSAGES_WHATS_APP
            + "(" + KEY_USER_TITLE + "  TEXT," + KEY_MESSAGE + " TEXT,"
            + KEY_TIME + " TEXT," + KEY_USER_READ_STATUS + " TEXT" + ")";


    private static final String CREATE_TABLE_USER_FACEBOOK = "CREATE TABLE IF NOT EXISTS "
            + TableName.TABLE_NAME_USER_FACEBOOK + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_USER_TITLE + " TEXT , " + KEY_LARGE_ICON_URI + " TEXT " + ")";


    // Tag table create statement
    private static final String CREATE_TABLE_MESSAGES_FACEBOOK = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_MESSAGES_FACEBOOK
            + "(" + KEY_USER_TITLE + "  TEXT," + KEY_MESSAGE + " TEXT,"
            + KEY_TIME + " TEXT," + KEY_USER_READ_STATUS + " TEXT" + ")";


    private static final String CREATE_TABLE_USER_INSTAGRAM = "CREATE TABLE IF NOT EXISTS "
            + TableName.TABLE_NAME_USER_INSTAGRAM + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_USER_TITLE + " TEXT , " + KEY_LARGE_ICON_URI + " TEXT " + ")";


    // Tag table create statement
    private static final String CREATE_TABLE_MESSAGES_INSTAGRAM = "CREATE TABLE IF NOT EXISTS " + TableName.TABLE_NAME_MESSAGES_INSTAGRAM
            + "(" + KEY_USER_TITLE + "  TEXT," + KEY_MESSAGE + " TEXT,"
            + KEY_TIME + " TEXT," + KEY_USER_READ_STATUS + " TEXT" + ")";

    private static final String CREATE_TABLE_USER_DEFAULT = "CREATE TABLE IF NOT EXISTS "
            + TableName.TABLE_NAME_USER_DEFAULT + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_USER_TITLE + " TEXT , " + KEY_LARGE_ICON_URI + " TEXT " + ")";


    // Tag table create statement
    private static final String CREATE_TABLE_MESSAGES_DEFAULT = "CREATE TABLE IF NOT EXISTS " + TableName.TABLE_NAME_MESSAGES_DEFAULT
            + "(" + KEY_USER_TITLE + "  TEXT," + KEY_MESSAGE + " TEXT,"
            + KEY_TIME + " TEXT," + KEY_USER_READ_STATUS + " TEXT" + ")";


    public MyDataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USER_WHATS_APP);
        db.execSQL(CREATE_TABLE_MESSAGES_WHATS_APP);
        db.execSQL(CREATE_TABLE_USER_FACEBOOK);
        db.execSQL(CREATE_TABLE_MESSAGES_FACEBOOK);
        db.execSQL(CREATE_TABLE_USER_INSTAGRAM);
        db.execSQL(CREATE_TABLE_MESSAGES_INSTAGRAM);
        db.execSQL(CREATE_TABLE_USER_DEFAULT);
        db.execSQL(CREATE_TABLE_MESSAGES_DEFAULT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_USER_WHATS_APP);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_MESSAGES_WHATS_APP);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_USER_FACEBOOK);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_MESSAGES_FACEBOOK);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_USER_INSTAGRAM);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_MESSAGES_INSTAGRAM);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_USER_DEFAULT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_MESSAGES_DEFAULT);

        onCreate(db);
    }


    public void insertUsers(String USERS_TABLE_NAME, long userID, String title, String largeIconUri) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_USER_TITLE, title);
        values.put(KEY_LARGE_ICON_URI, largeIconUri);
        // insert row
        db.insert(USERS_TABLE_NAME, null, values);
        db.close();
    }

    public ArrayList<Users> getALLUsers(String USERS_TABLE_NAME) {
        ArrayList<Users> usersList = new ArrayList<Users>();
        String selectQuery = "SELECT  * FROM " + USERS_TABLE_NAME;

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Users user = new Users();
                user.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                user.setUserTitle((c.getString(c.getColumnIndex(KEY_USER_TITLE))));
                user.setLargeIconUri((c.getString(c.getColumnIndex(KEY_LARGE_ICON_URI))));
                // adding to todo list
                usersList.add(user);
            } while (c.moveToNext());
        }
        db.close();
        return usersList;
    }

    public int getUsersCount(String USERS_TABLE_NAME) {
        String countQuery = "SELECT  * FROM " + USERS_TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();
        // return count
        return count;
    }

    public void updateUsers(String USERS_TABLE_NAME, Users users) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, users.getId());
        values.put(KEY_USER_TITLE, users.getUserTitle());

        // updating row
        db.update(USERS_TABLE_NAME, values, KEY_ID + " = ?",

                new String[]{String.valueOf(users.getId())});
        db.close();
    }

    public void deleteUsers(String USER_TABLE_NAME, String columnName) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(USER_TABLE_NAME, KEY_USER_TITLE + " = ?",
                new String[]{String.valueOf(columnName)});
        db.close();
    }


    public void insertMessages(String MESSAGE_TABLE_NAME, String User_title, String message, long time, String readStatus) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_USER_TITLE, User_title);
        values.put(KEY_MESSAGE, message);
        values.put(KEY_TIME, time);
        values.put(KEY_USER_READ_STATUS, readStatus);
        // insert row
        db.insert(MESSAGE_TABLE_NAME, null, values);
        db.close();
    }

    public void deleteMessages(String MESSAGE_TABLE_NAME, String columnName) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(MESSAGE_TABLE_NAME, KEY_USER_TITLE + " = ?",
                new String[]{String.valueOf(columnName)});
        db.close();
    }

    public void updateMessages(String USERS_TABLE_NAME, Messages messages) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
       /* values.put(KEY_USER_TITLE, messages.getTitle());
        values.put(KEY_MESSAGE, messages.getMessage());
        values.put(KEY_TIME, messages.getTimeStamp());*/
        values.put(KEY_USER_READ_STATUS, messages.getReadStatus());

        // updating row
        db.update(USERS_TABLE_NAME, values, KEY_USER_TITLE + " = ?",

                new String[]{String.valueOf(messages.getTitle())});
        db.close();
    }

    public int getMessagesCount(String MESSAGE_TABLE_NAME) {
        String countQuery = "SELECT  * FROM " + MESSAGE_TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();
        // return count
        return count;
    }

    public ArrayList<Messages> getAllMessages(String MESSAGE_TABLE_NAME) {
        ArrayList<Messages> messagesList = new ArrayList<Messages>();
        String selectQuery = "SELECT  * FROM " + MESSAGE_TABLE_NAME;

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Messages message = new Messages();
                message.setTitle(c.getString(c.getColumnIndex(KEY_USER_TITLE)));
                message.setMessage((c.getString(c.getColumnIndex(KEY_MESSAGE))));
                message.setTimeStamp((c.getLong(c.getColumnIndex(KEY_TIME))));
                // adding to todo list
                messagesList.add(message);
            } while (c.moveToNext());
        }
        db.close();
        return messagesList;
    }


    public ArrayList<Messages> getSelectedMessages(String MESSAGES_TABLE_NAME, String title) {
        ArrayList<Messages> messagesList = new ArrayList<Messages>();
        String selectQuery = "SELECT  * FROM " + MESSAGES_TABLE_NAME + " WHERE "
                + KEY_USER_TITLE + " = '" + title + "'";

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Messages messages = new Messages();
                messages.setTitle(c.getString(c.getColumnIndex(KEY_USER_TITLE)));
                messages.setMessage((c.getString(c.getColumnIndex(KEY_MESSAGE))));
                messages.setTimeStamp((c.getLong(c.getColumnIndex(KEY_TIME))));
                messages.setReadStatus((c.getString(c.getColumnIndex(KEY_USER_READ_STATUS))));
                // adding to todo list
                messagesList.add(messages);
            } while (c.moveToNext());
        }

        db.close();
        return messagesList;
    }

    public boolean checkIsRecordExist(String nameOfTable, String columnName, String columnValue) {
        SQLiteDatabase objDatabase = this.getReadableDatabase();
        try {
            Cursor cursor = objDatabase.rawQuery("SELECT " + columnName + " FROM " + nameOfTable + " WHERE " + columnName + "='" + columnValue + "'", null);
            if (cursor.moveToFirst()) {

                Log.d(LOG, "Record  Already Exists" + "Table is:" + nameOfTable + " ColumnName:" + columnName);
                objDatabase.close();
                return true;//record Exists

            }
            Log.d(LOG, "New Record  " + "Table is:" + nameOfTable + " ColumnName:" + columnName + " Column Value:" + columnValue);

        } catch (Exception errorException) {
            Log.d(LOG, "Exception occured" + "Exception occured " + errorException);
            objDatabase.close();
        }
        return false;
    }

}
