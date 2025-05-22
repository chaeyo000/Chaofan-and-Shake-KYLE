package com.example.chaofanandshake;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    // DATABASE INFO
    private static final String DATABASE_NAME = "ChaofanUserDb";
    private static final int DATABASE_VERSION = 3; // Taasan kung may changes sa structure

    // TABLE & COLUMNS
    private static final String TABLE_USERS = "users";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PHONE = "phone";
    private static final String COLUMN_PASSWORD = "password";

    // CREATE TABLE QUERY
    private static final String CREATE_TABLE_USERS = "CREATE TABLE " + TABLE_USERS + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_NAME + " TEXT, " +
            COLUMN_USERNAME + " TEXT, " +
            COLUMN_PHONE + " TEXT, " +
            COLUMN_PASSWORD + " TEXT);";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USERS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Kung kailangan i-drop at i-recreate ang table
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    // Check if username exists
    public boolean checkUsernameExists(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_USERNAME + " = ?", new String[]{username});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    // Check if phone exists
    public boolean checkPhoneExists(String phone) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_PHONE + " = ?", new String[]{phone});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    // Insert new user
    public boolean insertUser(String name, String username, String phone, String password) {
        if (checkUsernameExists(username)) {
            Log.d("DATABASE", "Username already exists");
            return false;
        }
        if (checkPhoneExists(phone)) {
            Log.d("DATABASE", "Phone number already exists");
            return false;
        }

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_PHONE, phone);
        values.put(COLUMN_PASSWORD, password);
        long result = db.insert(TABLE_USERS, null, values);
        db.close();

        return result != -1;
    }

    // Login validation
    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_USERNAME + "=? AND " + COLUMN_PASSWORD + "=?";
        Cursor cursor = db.rawQuery(query, new String[]{username, password});
        boolean userExists = cursor.getCount() > 0;
        cursor.close();
        return userExists;
    }

    // Get name using username
    public String getUserName(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COLUMN_NAME + " FROM " + TABLE_USERS + " WHERE " + COLUMN_USERNAME + "=?";
        Cursor cursor = db.rawQuery(query, new String[]{username});
        String userName = "";

        if (cursor.moveToFirst()) {
            userName = cursor.getString(0);
        }

        cursor.close();
        db.close();

        return userName;
    }

    // Get username using name
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

    // Get full user data by username
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

        if (cursor != null) cursor.close();
        return null;
    }

    // Update user info
    public boolean updateUser(String currentUsername, String newUsername, String newName, String newPhone, String newPassword) {
        SQLiteDatabase db = this.getReadableDatabase();

        if (!currentUsername.equals(newUsername) && checkUsernameExists(newUsername)) {
            return false;
        }

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

    // Delete user by username
    public boolean deleteUser(String username) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete(TABLE_USERS, COLUMN_USERNAME + "=?", new String[]{username});
        db.close();
        return rowsDeleted > 0;
    }
}
