package com.example.recoverdeletedmessages.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.recoverdeletedmessages.models.ModelMain;
import com.example.recoverdeletedmessages.models.TableSetter;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "my_database";
    private TableSetter tableSetter;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        tableSetter = new TableSetter();

    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {

        // create notes table
        db.execSQL(queryCreateTable());
    }

    private String queryCreateTable() {
        String CREATE_TABLE =
                "CREATE TABLE IF NOT EXISTS " + tableSetter.getTABLE_NAME() + "("
                        + tableSetter.getCOLUMN_ID() + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + tableSetter.getCOLUMN_TITLE() + " TEXT,"
                        + tableSetter.getCOLUMN_MESSAGE() + " DATETIME DEFAULT CURRENT_TIMESTAMP,"
                        + tableSetter.getCOLUMN_TIMESTAMP() + " TIME STAMP"
                        + ")";
        return CREATE_TABLE;
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + tableSetter.getTABLE_NAME());

        // Create tables again
        onCreate(db);
    }

    public void createNewTable(SQLiteDatabase db) {
        db.execSQL(queryCreateTable());
    }

    public long insertData(long columnId, String title, String message, long timeStamp) {
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        // `id` and `timestamp` will be inserted automatically.
        // no need to add them
        values.put(tableSetter.getCOLUMN_ID(), columnId);
        values.put(tableSetter.getCOLUMN_TITLE(), title);
        values.put(tableSetter.getCOLUMN_MESSAGE(), message);
        values.put(tableSetter.getCOLUMN_TIMESTAMP(), timeStamp);

        // insert row
        long id = db.insert(tableSetter.getTABLE_NAME(), null, values);

        // close db connection
        db.close();

        // return newly inserted row id
        return id;
    }

    public ModelMain getData(String title) {
        // get readable database as we are not inserting anything
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(tableSetter.getTABLE_NAME(),
                new String[]{tableSetter.getCOLUMN_ID(), tableSetter.getCOLUMN_TITLE(), tableSetter.getCOLUMN_MESSAGE(), tableSetter.getCOLUMN_TIMESTAMP()},
                tableSetter.getCOLUMN_TITLE() + "=?",
                new String[]{String.valueOf(title)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        // prepare note object
        ModelMain modelMain = new ModelMain(
                cursor.getInt(cursor.getColumnIndex(tableSetter.getCOLUMN_ID())),
                cursor.getString(cursor.getColumnIndex(tableSetter.getCOLUMN_TITLE())),
                cursor.getString(cursor.getColumnIndex(tableSetter.getCOLUMN_MESSAGE())),
                cursor.getString(cursor.getColumnIndex(tableSetter.getCOLUMN_TIMESTAMP())));

        // close the db connection
        cursor.close();

        return modelMain;
    }

    public List<ModelMain> getAllData() {
        List<ModelMain> modelMainList = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + tableSetter.getTABLE_NAME() + " ORDER BY " +
                tableSetter.getCOLUMN_TIMESTAMP() + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ModelMain modelMain = new ModelMain();
                modelMain.setId(cursor.getInt(cursor.getColumnIndex(tableSetter.getCOLUMN_ID())));
                modelMain.setTitle(cursor.getString(cursor.getColumnIndex(tableSetter.getCOLUMN_TITLE())));
                modelMain.setMessage(cursor.getString(cursor.getColumnIndex(tableSetter.getCOLUMN_MESSAGE())));
                modelMain.setTimeStamp(cursor.getString(cursor.getColumnIndex(tableSetter.getCOLUMN_TIMESTAMP())));

                modelMainList.add(modelMain);
            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return notes list
        return modelMainList;
    }

    public int getNotesCount() {
        String countQuery = "SELECT  * FROM " + tableSetter.getTABLE_NAME();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();


        // return count
        return count;
    }

    public int updateNote(ModelMain modelMain) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(tableSetter.getCOLUMN_ID(), modelMain.getId());
        values.put(tableSetter.getCOLUMN_TITLE(), modelMain.getTitle());
        values.put(tableSetter.getCOLUMN_MESSAGE(), modelMain.getMessage());
        values.put(tableSetter.getCOLUMN_TIMESTAMP(), modelMain.getTimeStamp());

        // updating row
        return db.update(tableSetter.getTABLE_NAME(), values, tableSetter.getCOLUMN_ID() + " = ?",
                new String[]{String.valueOf(modelMain.getId())});
    }

    public void deleteNote(ModelMain modelMain) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(tableSetter.getTABLE_NAME(), tableSetter.getCOLUMN_ID() + " = ?",
                new String[]{String.valueOf(modelMain.getId())});
        db.close();
    }
}
