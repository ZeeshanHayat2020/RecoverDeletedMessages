package com.example.recoverdeletedmessages.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.recoverdeletedmessages.models.Messages;
import com.example.recoverdeletedmessages.models.Users;

import java.util.ArrayList;
import java.util.List;

import static com.example.recoverdeletedmessages.constants.TableName.TABLE_NAME_MESSAGES_WHATS_APP;
import static com.example.recoverdeletedmessages.constants.TableName.TABLE_NAME_USER_WHATS_APP;


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

    // NOTES Table - column nmaes
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_TIME = "time";
    public static final String KEY_LARGE_ICON_URI = "larg_icon_uri";

    private static final String CREATE_TABLE_USER_WHATS_APP = "CREATE TABLE IF NOT EXISTS "
            + TABLE_NAME_USER_WHATS_APP + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_USER_TITLE + " TEXT , " + KEY_LARGE_ICON_URI + " TEXT " + ")";


    // Tag table create statement
    private static final String CREATE_TABLE_MESSAGES_WHATS_APP = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_MESSAGES_WHATS_APP
            + "(" + KEY_USER_TITLE + "  TEXT," + KEY_MESSAGE + " TEXT,"
            + KEY_TIME + " TEXT" + ")";


    public MyDataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USER_WHATS_APP);
        db.execSQL(CREATE_TABLE_MESSAGES_WHATS_APP);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_USER_WHATS_APP);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_MESSAGES_WHATS_APP);
        onCreate(db);
    }


    public long insertUsers(String USERS_TABLE_NAME, long userID, String title, String largeIconUri) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, userID);
        values.put(KEY_USER_TITLE, title);
        values.put(KEY_LARGE_ICON_URI, largeIconUri);
        // insert row
        long todo_id = db.insert(USERS_TABLE_NAME, null, values);
        return todo_id;
    }

    public Users getUsers(long user_id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + TABLE_NAME_USER_WHATS_APP + " WHERE "
                + KEY_ID + " = " + user_id;

        Log.e(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        Users user = new Users();
        user.setId(c.getLong(c.getColumnIndex(KEY_ID)));
        user.setUserTitle((c.getString(c.getColumnIndex(KEY_USER_TITLE))));
        user.setLargeIconUri((c.getString(c.getColumnIndex(KEY_LARGE_ICON_URI))));

        return user;
    }

    public ArrayList<Users> getALLUsers(String TABLE_NAME) {
        ArrayList<Users> usersList = new ArrayList<Users>();
        String selectQuery = "SELECT  * FROM " + TABLE_NAME;

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

        return usersList;
    }

    public int getUsersCount() {
        String countQuery = "SELECT  * FROM " + TABLE_NAME_USER_WHATS_APP;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();
        // return count
        return count;
    }

    public int updateUsers(Users users) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, users.getId());
        values.put(KEY_USER_TITLE, users.getUserTitle());

        // updating row
        return db.update(TABLE_NAME_USER_WHATS_APP, values, KEY_ID + " = ?",
                new String[]{String.valueOf(users.getId())});
    }

    public void deleteUsers(long user_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME_USER_WHATS_APP, KEY_ID + " = ?",
                new String[]{String.valueOf(user_id)});
    }


    public long insertMessages(String MESSAGE_TABLE_NAME, String User_title, String message, long time) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_USER_TITLE, User_title);
        values.put(KEY_MESSAGE, message);
        values.put(KEY_TIME, time);
        // insert row
        long todo_id = db.insert(MESSAGE_TABLE_NAME, null, values);
        return todo_id;
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
                // adding to todo list
                messagesList.add(messages);
            } while (c.moveToNext());
        }

        return messagesList;
    }

    public boolean isColumnExist(String TABLE_NAME, String columnName, String columnValue) {
        String selectQuery = "SELECT * FROM " + TABLE_NAME + " WHERE " + columnName + " = '" + columnValue + " '";
        Log.e(LOG, selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToLast(); //if you not place this cursor.getCount() always give same integer (1) or current position of cursor.
        if (cursor.getCount() > 0) {
            return true;
        } else {
            return false;
        }
    }
}
