package com.example.chaofanandshake;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.chaofanandshake.Domain.Order;
import com.example.chaofanandshake.Domain.User;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "ChaofanUserDb";
    private static final int DATABASE_VERSION = 5;

    private static final String TABLE_USERS = "users";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PHONE = "phone";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_ROLE = "role";

    private static final String TABLE_ORDERS = "orders";

    private static final String CREATE_TABLE_USERS = "CREATE TABLE " + TABLE_USERS + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_NAME + " TEXT, " +
            COLUMN_USERNAME + " TEXT, " +
            COLUMN_PHONE + " TEXT, " +
            COLUMN_PASSWORD + " TEXT, " +
            COLUMN_ROLE + " TEXT);";

    private static final String CREATE_TABLE_ORDERS = "CREATE TABLE " + TABLE_ORDERS + " (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "order_summary TEXT, " +
            "phone TEXT, " +
            "username TEXT, " +
            "payment_method TEXT, " +
            "total_price REAL);";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_ORDERS);

        ContentValues admin = new ContentValues();
        admin.put(COLUMN_NAME, "Administrator");
        admin.put(COLUMN_USERNAME, "admin");
        admin.put(COLUMN_PHONE, "09123456789");
        admin.put(COLUMN_PASSWORD, "1");
        admin.put(COLUMN_ROLE, "admin");
        db.insert(TABLE_USERS, null, admin);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDERS);
        onCreate(db);
    }
    public boolean checkUsernameExists(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_USERNAME + " = ?", new String[]{username});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public boolean checkPhoneExists(String phone) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_PHONE + " = ?", new String[]{phone});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public boolean insertUser(String name, String username, String phone, String password) {
        return insertUser(name, username, phone, password, "user");
    }

    public boolean insertUser(String name, String username, String phone, String password, String role) {
        if (checkUsernameExists(username) || checkPhoneExists(phone)) return false;

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_PHONE, phone);
        values.put(COLUMN_PASSWORD, password);
        values.put(COLUMN_ROLE, role);

        long result = db.insert(TABLE_USERS, null, values);
        db.close();
        return result != -1;
    }

    public boolean isAdmin(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_ROLE + " FROM " + TABLE_USERS + " WHERE " + COLUMN_USERNAME + "=?", new String[]{username});
        if (cursor != null && cursor.moveToFirst()) {
            String role = cursor.getString(0);
            cursor.close();
            return "admin".equalsIgnoreCase(role);
        }
        return false;
    }

    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_USERNAME + "=? AND " + COLUMN_PASSWORD + "=?", new String[]{username, password});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public String getUserName(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_NAME + " FROM " + TABLE_USERS + " WHERE " + COLUMN_USERNAME + "=?", new String[]{username});
        String userName = "";
        if (cursor.moveToFirst()) {
            userName = cursor.getString(0);
        }
        cursor.close();
        return userName;
    }

    public String getUsernameByName(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_USERNAME + " FROM " + TABLE_USERS + " WHERE " + COLUMN_NAME + " = ?", new String[]{name});
        if (cursor != null && cursor.moveToFirst()) {
            String updatedUsername = cursor.getString(0);
            cursor.close();
            return updatedUsername;
        }
        return null;
    }

    @SuppressLint("Range")
    public String[] getUserDataByUsername(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_USERNAME + "=?", new String[]{username});
        if (cursor != null && cursor.moveToFirst()) {
            String[] userData = new String[4];
            userData[0] = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
            userData[1] = cursor.getString(cursor.getColumnIndex(COLUMN_USERNAME));
            userData[2] = cursor.getString(cursor.getColumnIndex(COLUMN_PHONE));
            userData[3] = cursor.getString(cursor.getColumnIndex(COLUMN_PASSWORD));
            cursor.close();
            return userData;
        }
        return null;
    }

    public boolean updateUser(String currentUsername, String newUsername, String newName, String newPhone, String newPassword) {
        SQLiteDatabase db = this.getReadableDatabase();
        if (!currentUsername.equals(newUsername) && checkUsernameExists(newUsername)) return false;

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_PHONE + "=? AND " + COLUMN_USERNAME + "!=?", new String[]{newPhone, currentUsername});
        if (cursor != null && cursor.getCount() > 0) {
            cursor.close();
            return false;
        }
        if (cursor != null) cursor.close();

        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, newName);
        values.put(COLUMN_USERNAME, newUsername);
        values.put(COLUMN_PHONE, newPhone);
        values.put(COLUMN_PASSWORD, newPassword);

        int rowsUpdated = db.update(TABLE_USERS, values, COLUMN_USERNAME + "=?", new String[]{currentUsername});
        return rowsUpdated > 0;
    }

    public boolean deleteUser(String username) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete(TABLE_USERS, COLUMN_USERNAME + "=?", new String[]{username});
        db.close();
        return rowsDeleted > 0;
    }

    public boolean checkUserPhone(String username, String phone) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE username = ? AND phone = ?", new String[]{username, phone});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public boolean updatePassword(String username, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("password", newPassword);
        int rows = db.update("users", values, "username = ?", new String[]{username});
        return rows > 0;
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT username, phone FROM users", null);

        if (cursor.moveToFirst()) {
            do {
                String username = cursor.getString(0);
                String phone = cursor.getString(1);
                users.add(new User(username, phone));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return users;
    }

    public boolean insertOrder(String orderSummary, String phone, String username, String paymentMethod, double totalPrice) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("order_summary", orderSummary);
        values.put("phone", phone);
        values.put("username", username);
        values.put("payment_method", paymentMethod);
        values.put("total_price", totalPrice);

        long result = -1;
        try {
            result = db.insertOrThrow("orders", null, values);
            Log.i("DB_INSERT", "Insert result: " + result);
        } catch (Exception e) {
            Log.e("DB_INSERT", "Insert failed with exception: " + e.getMessage());
        } finally {
            db.close();
        }
        return result != -1;
    }

    @SuppressLint("Range")
    public List<Order> getAllOrders() {
        List<Order> orderList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_ORDERS, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String summary = cursor.getString(cursor.getColumnIndex("order_summary"));
                String phone = cursor.getString(cursor.getColumnIndex("phone"));
                String username = cursor.getString(cursor.getColumnIndex("username"));
                String paymentMethod = cursor.getString(cursor.getColumnIndex("payment_method"));
                double totalPrice = cursor.getDouble(cursor.getColumnIndex("total_price"));

                orderList.add(new Order(id, summary, phone, username, paymentMethod, totalPrice));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return orderList;
    }
}
