package com.map.nguyennhatminh.demo2.dao;

import static com.map.nguyennhatminh.demo2.dao.DbHelper.TABLE_CATEGORY;
import static com.map.nguyennhatminh.demo2.dao.DbHelper.TABLE_CAT_IN_OUT;
import static com.map.nguyennhatminh.demo2.dao.DbHelper.TABLE_IN_OUT;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.map.nguyennhatminh.demo2.model.CatInOut;
import com.map.nguyennhatminh.demo2.model.InOut;

public class CatInOutDAO {

    private DbHelper dbHelper;
    private CategoryDAO categoryDAO;

    public SQLiteDatabase open() {
        return dbHelper.getWritableDatabase();
    }


    public CatInOutDAO(Context context) {
        dbHelper = new DbHelper(context);
        categoryDAO = new CategoryDAO(context);
    }

    public CatInOut getCatInOut(int idCatInOut) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT * " +
                "FROM " + TABLE_CAT_IN_OUT + " c " +
                "JOIN " + TABLE_IN_OUT + " i ON c.idInOut = i.id " +
                "WHERE c.id = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(idCatInOut)});

        CatInOut catInOut = null;
        if (cursor.moveToFirst()) {
            catInOut = new CatInOut(
                    cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    new InOut(cursor.getInt(cursor.getColumnIndexOrThrow("idInOut")),
                            cursor.getString(cursor.getColumnIndexOrThrow("name")),
                            cursor.getInt(cursor.getColumnIndexOrThrow("type"))),
                    categoryDAO.getCategoryById(cursor.getInt(cursor.getColumnIndexOrThrow("idCat"))));
        }
        cursor.close();
        db.close();
        return catInOut;
    }

    public CatInOut getCatInOutBy(int catId, int inoutId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_CAT_IN_OUT + " c " +
                "JOIN " + TABLE_IN_OUT + " i ON c.idInOut = i.id " +
                "JOIN " + TABLE_CATEGORY + " a ON a.id = c.idCat " +
                "WHERE c.idCat = ? AND c.idInOut = ?";

        // Use rawQuery to pass parameters safely
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(catId), String.valueOf(inoutId)});

        CatInOut catInOut = null;
        if (cursor != null && cursor.moveToFirst()) {
            // Create the CatInOut object from the cursor
            catInOut = new CatInOut(
                    cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    new InOut(
                            cursor.getInt(cursor.getColumnIndexOrThrow("idInOut")),
                            cursor.getString(cursor.getColumnIndexOrThrow("name")),
                            cursor.getInt(cursor.getColumnIndexOrThrow("type"))
                    ),
                    categoryDAO.getCategoryById(cursor.getInt(cursor.getColumnIndexOrThrow("idCat")))
            );
        }

        if (cursor != null) {
            cursor.close();  // Ensure cursor is always closed
        }
        db.close();  // Close the database connection

        return catInOut;  // Return the result (or null if no match found)
    }


}
