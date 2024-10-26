package com.map.nguyennhatminh.demo2.dao;

import static com.map.nguyennhatminh.demo2.dao.DbHelper.TABLE_CATEGORY;
import static com.map.nguyennhatminh.demo2.dao.DbHelper.TABLE_CAT_IN_OUT;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.map.nguyennhatminh.demo2.R;
import com.map.nguyennhatminh.demo2.model.Category;
import com.map.nguyennhatminh.demo2.model.InOut;
import com.map.nguyennhatminh.demo2.model.CatInOut;

import java.util.ArrayList;
import java.util.List;

public class CategoryDAO {
    private SQLiteDatabase db;
    private DbHelper dbHelper;

    public CategoryDAO(Context context) {
        dbHelper = new DbHelper(context);
    }

    public SQLiteDatabase open() {
        return dbHelper.getWritableDatabase();
    }


    public List<Category> getAll(boolean isIncome) {
        List<Category> categories = new ArrayList<>();
        db = dbHelper.getReadableDatabase();

        String query = "SELECT c.* FROM " + TABLE_CATEGORY + " c " +
                "JOIN " + TABLE_CAT_IN_OUT + " cio ON c.id = cio.idCat " +
                "JOIN " + DbHelper.TABLE_IN_OUT + " io ON cio.idInOut = io.id " +
                "WHERE io.type = ?";

        Cursor cursor = db.rawQuery(query, new String[]{isIncome ? "1" : "2"});

        if (cursor.moveToFirst()) {
            do {
                Category category = new Category(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("name")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("icon")),
                        cursor.getString(cursor.getColumnIndexOrThrow("note")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("idCat")) != 0 ? getCategoryById(cursor.getInt(cursor.getColumnIndexOrThrow("idCat"))) : null
                );
                categories.add(category);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return categories;
    }

    public Category getCategoryById(int id) {
        db = dbHelper.getReadableDatabase();
        Category category = null;

        Cursor cursor = db.query(TABLE_CATEGORY, null, "id = ?",
                new String[]{String.valueOf(id)}, null, null, null);

        if (cursor.moveToFirst()) {
            category = new Category(
                    cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    cursor.getString(cursor.getColumnIndexOrThrow("name")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("icon")),
                    cursor.getString(cursor.getColumnIndexOrThrow("note")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("idCat")) != 0 ? getCategoryById(cursor.getInt(cursor.getColumnIndexOrThrow("idCat"))) : null
            );
        }
        cursor.close();
        db.close();
        return category;
    }

    public boolean add(Category category, InOut inOut) {
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", category.getName());
        values.put("icon", category.getIcon());
        values.put("note", category.getNote());
        values.put("idCat", category.getCategory() != null ? category.getCategory().getId() : null);

        long categoryId = db.insert(TABLE_CATEGORY, null, values);

        if (categoryId != -1) {
            values.clear();
            values.put("idInOut", inOut.getId());
            values.put("idCat", categoryId);
            long catInOutId = db.insert(TABLE_CAT_IN_OUT, null, values);
            db.close();
            return catInOutId != -1;
        }
        db.close();
        return false;
    }

    public boolean update(Category category, InOut inOut) {
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", category.getName());
        values.put("icon", category.getIcon());
        values.put("note", category.getNote());
        values.put("idCat", category.getCategory() != null ? category.getCategory().getId() : null);

        int rowsAffected = db.update(TABLE_CATEGORY, values, "id = ?",
                new String[]{String.valueOf(category.getId())});

        if (rowsAffected > 0) {
            values.clear();
            values.put("idInOut", inOut.getId());
            db.update(TABLE_CAT_IN_OUT, values, "idCat = ?",
                    new String[]{String.valueOf(category.getId())});
        }
        db.close();
        return rowsAffected > 0;
    }

    public boolean delete(int id) {
        db = dbHelper.getWritableDatabase();
        int rowsAffected = db.delete(TABLE_CATEGORY, "id = ?",
                new String[]{String.valueOf(id)});
        db.delete(TABLE_CAT_IN_OUT, "idCat = ?",
                new String[]{String.valueOf(id)});
        db.close();
        return rowsAffected > 0;
    }

    public CatInOut getCatInOut(int categoryId) {
        db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id FROM " + TABLE_CAT_IN_OUT + " WHERE idCat = ?", new String[]{String.valueOf(categoryId)});
        CatInOut catInOut = null;
        if (cursor.moveToFirst()) {
            catInOut = new CatInOut(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
        }
        cursor.close();
        db.close();
        return catInOut;
    }

    public int getLastInsertedCategoryId() {
        int lastId = -1;
        db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT last_insert_rowid() AS lastId", null);
        if (cursor.moveToFirst()) {
            lastId = cursor.getInt(cursor.getColumnIndexOrThrow("lastId"));
        }
        cursor.close();
        db.close();
        return lastId;
    }

    public InOut getInOutForCategory(int categoryId) {
        db = dbHelper.getReadableDatabase();
        InOut inOut = null;

        String query = "SELECT io.* FROM " + DbHelper.TABLE_IN_OUT + " io " +
                "JOIN " + TABLE_CAT_IN_OUT + " cio ON io.id = cio.idInOut " +
                "WHERE cio.idCat = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(categoryId)});

        if (cursor.moveToFirst()) {
            inOut = new InOut(
                    cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    cursor.getString(cursor.getColumnIndexOrThrow("name")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("type"))
            );
        }
        cursor.close();
        db.close();
        return inOut;
    }

    public boolean isIncomeCategory(Category category) {
        InOut inOut = getInOutForCategory(category.getId());
        return inOut != null && inOut.getType() == 1;
    }

    public void initSampleData() {
        db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            // Xóa dữ liệu cũ (nếu có)
            db.delete(TABLE_CAT_IN_OUT, null, null);
            db.delete(TABLE_CATEGORY, null, null);
            db.delete(DbHelper.TABLE_IN_OUT, null, null);

            // Thêm InOut
            ContentValues inOutValues = new ContentValues();
            inOutValues.put("name", "Thu nhập");
            inOutValues.put("type", 1);
            long incomeId = db.insert(DbHelper.TABLE_IN_OUT, null, inOutValues);

            inOutValues.clear();
            inOutValues.put("name", "Chi tiêu");
            inOutValues.put("type", 2);
            long expenseId = db.insert(DbHelper.TABLE_IN_OUT, null, inOutValues);

            // Thêm Categories và CatInOut
            addSampleCategory(db, "Lương", R.drawable.salary, "Thu nhập từ công việc chính", null, incomeId);
            addSampleCategory(db, "Học bổng", R.drawable.school, "Thu nhập từ học bổng", null, incomeId);
            addSampleCategory(db, "Làm thêm", R.drawable.salary, "Thu nhập từ công việc bán thời gian", null, incomeId);
            addSampleCategory(db, "Quà tặng", R.drawable.gift, "Thu nhập từ quà tặng", null, incomeId);

            long educationId = addSampleCategory(db, "Học hành", R.drawable.book, "Chi phí liên quan đến giáo dục", null, expenseId);
            addSampleCategory(db, "Học phí", R.drawable.school, "Chi phí học phí", educationId, expenseId);
            addSampleCategory(db, "Sách vở", R.drawable.book, "Chi phí mua sách và đồ dùng học tập", educationId, expenseId);

            long livingId = addSampleCategory(db, "Sinh hoạt", R.drawable.cart, "Chi phí sinh hoạt hàng ngày", null, expenseId);
            addSampleCategory(db, "Tiền chơi", R.drawable.hang_out, "Chi phí giải trí", livingId, expenseId);
            addSampleCategory(db, "Tiền nhà", R.drawable.home, "Chi phí thuê nhà", livingId, expenseId);

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    private long addSampleCategory(SQLiteDatabase db, String name, int icon, String note, Long parentId, long inOutId) {
        ContentValues categoryValues = new ContentValues();
        categoryValues.put("name", name);
        categoryValues.put("icon", icon);
        categoryValues.put("note", note);
        categoryValues.put("idCat", parentId);
        long categoryId = db.insert(TABLE_CATEGORY, null, categoryValues);

        ContentValues catInOutValues = new ContentValues();
        catInOutValues.put("idInOut", inOutId);
        catInOutValues.put("idCat", categoryId);
        db.insert(TABLE_CAT_IN_OUT, null, catInOutValues);

        return categoryId;
    }

    public boolean isSampleDataInitialized() {
        db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_CATEGORY, null);
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return count > 0;
    }
}