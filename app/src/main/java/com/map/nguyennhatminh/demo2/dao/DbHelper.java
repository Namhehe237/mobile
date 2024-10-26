package com.map.nguyennhatminh.demo2.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {


    private static final String DATABASE_NAME = "demo2minhhehehe.db";

    private static final int DATABASE_VERSION = 3;

    public static final String TABLE_CATEGORY = "tblCategory";
    public static final String TABLE_CAT_IN_OUT = "tblCatInOut";
    public static final String TABLE_IN_OUT = "tblInOut";
    public static final String TABLE_TRANSACTION = "tblTransaction";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableCategory = "CREATE TABLE " + TABLE_CATEGORY + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT, " +
                "icon INTEGER, " +
                "note TEXT, " +
                "idCat INTEGER, " +
                "FOREIGN KEY(idCat) REFERENCES " + TABLE_CATEGORY + "(id) ON DELETE SET NULL);";
        db.execSQL(createTableCategory);

        String createTableInOut = "CREATE TABLE " + TABLE_IN_OUT + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT, " +
                "type INTEGER);";
        db.execSQL(createTableInOut);

        String createTableCatInOut = "CREATE TABLE " + TABLE_CAT_IN_OUT + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "idInOut INTEGER, " +
                "idCat INTEGER, " +
                "FOREIGN KEY(idInOut) REFERENCES " + TABLE_IN_OUT + "(id) ON DELETE SET NULL, " +
                "FOREIGN KEY(idCat) REFERENCES " + TABLE_CATEGORY + "(id) ON DELETE SET NULL);";
        db.execSQL(createTableCatInOut);


        String createTableTransaction = "CREATE TABLE " + TABLE_TRANSACTION + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT, " +
                "amount INTEGER, " +
                "day TEXT, " +
                "note TEXT, " +
                "idCatInOut INTEGER, " +
                "FOREIGN KEY(idCatInOut) REFERENCES " + TABLE_CAT_IN_OUT + "(id) ON DELETE SET NULL);";
        db.execSQL(createTableTransaction);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSACTION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CAT_IN_OUT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_IN_OUT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORY);

        onCreate(db);
    }
}

