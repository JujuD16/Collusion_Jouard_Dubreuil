package com.example.app2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Database extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Search.db";
    private static final int DATABASE_VERSION = 1;

    public Database(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    public void onCreate(SQLiteDatabase db) {

        // SQL statement for creating a new table

        String CREATE_HIST = "CREATE TABLE IF NOT EXISTS HIST_TABLE (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "url TEXT,"
                + "rate INTEGER"
                + ")";
        db.execSQL(CREATE_HIST);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            // Simplest implementation is to drop all old tables and recreate them
            db.execSQL("DROP TABLE IF EXISTS " + "HIST_TABLE");
            onCreate(db);
        }
    }

    public void addHist(String url, Integer note){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("url", url);
        values.put("rate", note);

        // Inserting Row
        db.insert("HIST_TABLE", null, values);
        //2nd argument is String containing nullColumnHack
        db.close(); // Closing database connection
    }

    public List<Search> getAllContacts() {
        List<Search> searchList = new ArrayList<Search>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + "HIST_TABLE";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Search search = new Search();
                search.setID(Integer.parseInt(cursor.getString(0)));
                search.setUrl(cursor.getString(1));
                search.setRate(cursor.getInt(2));
                // Adding contact to list
                searchList.add(search);
            } while (cursor.moveToNext());
        }

        // return contact list
        return searchList;
    }
}
