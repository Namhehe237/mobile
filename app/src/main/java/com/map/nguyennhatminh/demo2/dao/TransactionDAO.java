package com.map.nguyennhatminh.demo2.dao;

import static com.map.nguyennhatminh.demo2.dao.DbHelper.TABLE_CATEGORY;
import static com.map.nguyennhatminh.demo2.dao.DbHelper.TABLE_CAT_IN_OUT;
import static com.map.nguyennhatminh.demo2.dao.DbHelper.TABLE_IN_OUT;
import static com.map.nguyennhatminh.demo2.dao.DbHelper.TABLE_TRANSACTION;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.map.nguyennhatminh.demo2.model.CatInOut;
import com.map.nguyennhatminh.demo2.model.Transaction;
import com.map.nguyennhatminh.demo2.model.Category;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TransactionDAO {
    private CatInOutDAO catInOutDAO;
    private DbHelper dbHelper;
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    public SQLiteDatabase open() {
        return dbHelper.getWritableDatabase();
    }


    public TransactionDAO(Context context) {
        dbHelper = new DbHelper(context);
        catInOutDAO = new CatInOutDAO(context);
    }

    public boolean add(Transaction transaction) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("name", transaction.getName());
        values.put("amount", transaction.getAmount());
        values.put("day", dateFormat.format(transaction.getDay()));
        values.put("note", transaction.getNote());
        values.put("idCatInOut", transaction.getCatInOut().getId());

        long result = db.insert(TABLE_TRANSACTION, null, values);

        db.close();

        return result != -1;
    }

    public List<Transaction> getTransactionsByDay(Date date) throws ParseException {
        List<Transaction> transactions = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String dateStringStart = new SimpleDateFormat("yyyy-MM-dd 00:00:00", Locale.getDefault()).format(date);
        String dateStringEnd = new SimpleDateFormat("yyyy-MM-dd 23:59:59", Locale.getDefault()).format(date);
        String query = "SELECT * FROM " + TABLE_TRANSACTION + " WHERE day BETWEEN ? AND ?";
        Cursor cursor = db.rawQuery(query, new String[]{dateStringStart, dateStringEnd});

        if (cursor.moveToFirst()) {
            do {
                Transaction transaction = new Transaction(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("name")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("amount")),
                        dateFormat.parse(cursor.getString(cursor.getColumnIndexOrThrow("day"))),
                        cursor.getString(cursor.getColumnIndexOrThrow("note")),
                        catInOutDAO.getCatInOut(cursor.getInt(cursor.getColumnIndexOrThrow("idCatInOut")))
                );
                transactions.add(transaction);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return transactions;
    }
    public boolean isIncomeCategory(Category category) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        boolean isIncome = false;

        try {
            String query = "SELECT type FROM " + TABLE_IN_OUT +
                    " WHERE id = (SELECT idInOut FROM " + TABLE_CAT_IN_OUT +
                    " WHERE idCat = ?)";
            cursor = db.rawQuery(query, new String[]{String.valueOf(category.getId())});

            if (cursor != null && cursor.moveToFirst()) {
                int type = cursor.getInt(cursor.getColumnIndexOrThrow("type"));
                isIncome = (type == 1); // Assuming type 1 is for income
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return isIncome;
    }

    public List<Transaction> getAll() throws ParseException {
        List<Transaction> transactions = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_TRANSACTION;
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                Transaction transaction = new Transaction(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("name")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("amount")),
                        dateFormat.parse(cursor.getString(cursor.getColumnIndexOrThrow("day"))),
                        cursor.getString(cursor.getColumnIndexOrThrow("note")),
                        catInOutDAO.getCatInOut(cursor.getInt(cursor.getColumnIndexOrThrow("idCatInOut")))
                );
                transactions.add(transaction);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        Collections.reverse(transactions);
        return transactions;
    }

    public int getTotalIncome() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        int totalIncome = 0;

        String query = "SELECT SUM(amount) as total FROM " + TABLE_TRANSACTION +
                " WHERE idCatInOut IN (SELECT id FROM " + TABLE_CAT_IN_OUT +
                " WHERE idInOut IN (SELECT id FROM " + TABLE_IN_OUT + " WHERE type = 1))";

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            totalIncome = cursor.getInt(cursor.getColumnIndexOrThrow("total"));
        }

        cursor.close();
        db.close();
        return totalIncome;
    }

    public int getTotalOut() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        int totalOut = 0;

        String query = "SELECT SUM(amount) as total FROM " + TABLE_TRANSACTION +
                " WHERE idCatInOut IN (SELECT id FROM " + TABLE_CAT_IN_OUT +
                " WHERE idInOut IN (SELECT id FROM " + TABLE_IN_OUT + " WHERE type = 2))";

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            totalOut = cursor.getInt(cursor.getColumnIndexOrThrow("total"));
        }

        cursor.close();
        db.close();
        return totalOut;
    }


}