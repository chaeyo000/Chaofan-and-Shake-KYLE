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

    private static final String DATABASE_NAME = "UserDatabase";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_USERS = "users";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PHONE = "phone";
    private static final String COLUMN_PASSWORD = "password";

    private static final String CREATE_TABLE_USERS = "CREATE TABLE " + TABLE_USERS + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_NAME + " TEXT, " +
            COLUMN_EMAIL + " TEXT, " +
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
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    // Check if email exists in the database
    public boolean checkEmailExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE email = ?", new String[]{email});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    // Check if phone exists in the database
    public boolean checkPhoneExists(String phone) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE phone = ?", new String[]{phone});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    // Insert new user into the database
    public boolean insertUser(String name, String email, String phone, String password) {
        if (checkEmailExists(email)) {
            Log.d("DATABASE", "Email already exists");
            return false;
        }
        if (checkPhoneExists(phone)) {
            Log.d("DATABASE", "Phone number already exists");
            return false;
        }

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_PHONE, phone);
        values.put(COLUMN_PASSWORD, password);
        long result = db.insert(TABLE_USERS, null, values);
        db.close();

        return result != -1;
    }

    // Validate user login with email and password
    public boolean checkUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_EMAIL + "=? AND " + COLUMN_PASSWORD + "=?";
        Cursor cursor = db.rawQuery(query, new String[]{email, password});
        boolean userExists = cursor.getCount() > 0;
        cursor.close();
        return userExists;
    }

    // Get user name based on email
    public String getUserName(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COLUMN_NAME + " FROM " + TABLE_USERS + " WHERE " + COLUMN_EMAIL + "=?";
        Cursor cursor = db.rawQuery(query, new String[]{email});
        String userName = "";

        if (cursor != null && cursor.moveToFirst()) {
            try {
                int columnIndex = cursor.getColumnIndexOrThrow(COLUMN_NAME);
                userName = cursor.getString(columnIndex);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (cursor != null) {
            cursor.close();
        }

        return userName;
    }

    // Get user data by email
    @SuppressLint("Range")
    public String[] getUserDataByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_EMAIL + "=?", new String[]{email});

        if (cursor != null && cursor.moveToFirst()) {
            String[] userData = new String[3];
            userData[0] = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
            userData[1] = cursor.getString(cursor.getColumnIndex(COLUMN_EMAIL));
            userData[2] = cursor.getString(cursor.getColumnIndex(COLUMN_PHONE));
            cursor.close();
            return userData;
        }

        if (cursor != null) cursor.close();
        return null;
    }

    //  Update user data using email — cleaned up & added phone check
    public boolean updateUser(String currentEmail, String newEmail, String newName, String newPhone) {
        // Check if the new email or phone number already exists
        SQLiteDatabase db = this.getReadableDatabase();

        // Check if email already exists (excluding current email)
        if (!currentEmail.equals(newEmail) && checkEmailExists(newEmail)) {
            return false;
        }

        // Check if phone number already exists (excluding current phone)
        if (!checkPhoneExists(newPhone)) {
            Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_PHONE + "=? AND " + COLUMN_EMAIL + "!=?", new String[]{newPhone, newEmail});
            if (cursor != null && cursor.getCount() > 0) {
                cursor.close();
                return false;
            }
            cursor.close();
        }

        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, newName);
        values.put(COLUMN_EMAIL, newEmail);
        values.put(COLUMN_PHONE, newPhone);

        // Update user info
        int rowsUpdated = db.update(TABLE_USERS, values, COLUMN_EMAIL + "=?", new String[]{currentEmail});

        return rowsUpdated > 0;
    }

    // Delete user by email
    public boolean deleteUser(String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete(TABLE_USERS, COLUMN_EMAIL + "=?", new String[]{email});
        db.close();
        return rowsDeleted > 0;
    }
}
