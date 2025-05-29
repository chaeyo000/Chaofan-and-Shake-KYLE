package com.example.chaofanandshake;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class AccountActivity extends AppCompatActivity {

    private ImageView backBtn, ivEditName, ivEditUsername, ivEditPhone, ivEditPassword;
    private EditText etName, etUsername, etPhone, etPassword;
    private Button btnDelete;

    private DatabaseHelper dbHelper;
    private String currentUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accountdetails);

        dbHelper = new DatabaseHelper(this);
        currentUsername = getIntent().getStringExtra("username");

        Log.d("AccountActivity", "Username received: " + currentUsername);

        backBtn = findViewById(R.id.backBtn);
        ivEditName = findViewById(R.id.ivEditName);
        ivEditUsername = findViewById(R.id.ivEditUsername);
        ivEditPhone = findViewById(R.id.ivEditPhone);
        ivEditPassword = findViewById(R.id.ivEditPassword);

        etName = findViewById(R.id.etName);
        etUsername = findViewById(R.id.etUsername);
        etPhone = findViewById(R.id.etPhone);
        etPassword = findViewById(R.id.etPassword);
        btnDelete = findViewById(R.id.btnDelete);

        loadUserData();

        backBtn.setOnClickListener(view -> onBackPressed());

        ivEditName.setOnClickListener(view -> openEditActivity(ActivityName.class, "currentName", etName.getText().toString()));
        ivEditUsername.setOnClickListener(view -> openEditActivity(ActivityUsername.class, "currentValue", etUsername.getText().toString()));
        ivEditPhone.setOnClickListener(view -> openEditActivity(ActivityPhone.class, "currentPhone", etPhone.getText().toString()));
        ivEditPassword.setOnClickListener(view -> openEditActivity(ActivityPassword.class, "currentPassword", etPassword.getText().toString()));

        btnDelete.setOnClickListener(v -> confirmDelete());
    }

    private void openEditActivity(Class<?> activityClass, String extraKey, String value) {
        Intent intent = new Intent(AccountActivity.this, activityClass);
        intent.putExtra(extraKey, value);
        intent.putExtra("currentUsername", currentUsername);
        startActivityForResult(intent, 1);
    }

    private void confirmDelete() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.delete_dialog, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        }

        Button btnCancel = dialogView.findViewById(R.id.btnCancel);
        Button btnDelete = dialogView.findViewById(R.id.btnDelete);

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnDelete.setOnClickListener(v -> {
            boolean deleted = dbHelper.deleteUserAndOrders(currentUsername);
            if (deleted) {
                Toast.makeText(AccountActivity.this, "Account deleted", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(AccountActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                dialog.dismiss();
            } else {
                Toast.makeText(AccountActivity.this, "Failed to delete account", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            updateIfChanged("updatedName", etName, "name", data);
            updateIfChanged("updatedUsername", etUsername, "username", data);
            updateIfChanged("updatedPhone", etPhone, "phone", data);
            updateIfChanged("updatedPassword", etPassword, "password", data);
        }
    }

    private void updateIfChanged(String key, EditText editText, String dbField, Intent data) {
        String updatedValue = data.getStringExtra(key);
        if (updatedValue != null && !updatedValue.isEmpty()) {
            editText.setText(updatedValue);
            updateDatabase(dbField, updatedValue);

            SharedPreferences.Editor editor = getSharedPreferences("UserProfile", MODE_PRIVATE).edit();

            switch (dbField) {
                case "username":
                    currentUsername = updatedValue;
                    editor.putString("username", updatedValue);
                    break;
                case "phone":
                    editor.putString("phone", updatedValue);
                    break;
                case "name":
                    editor.putString("name", updatedValue);
                    break;
            }
            editor.apply();

            Intent resultIntent = new Intent();
            resultIntent.putExtra("newUsername", currentUsername);
            setResult(RESULT_OK, resultIntent);
        }
    }

    private void updateDatabase(String field, String value) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(field, value);

        int result = database.update("users", values, "username = ?", new String[]{currentUsername});

        if (result > 0) {
            Log.d("AccountActivity", "Database updated: " + field + " = " + value);
        } else {
            Log.d("AccountActivity", "Failed to update database for field: " + field);
        }
    }

    private void loadUserData() {
        if (currentUsername == null || currentUsername.isEmpty()) {
            Toast.makeText(this, "No username provided", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] userData = dbHelper.getUserDataByUsername(currentUsername);
        if (userData != null && userData.length >= 4) {
            Log.d("AccountActivity", "User Data loaded: Name=" + userData[0] + ", Username=" + userData[1] + ", Phone=" + userData[2] + ", Password=" + userData[3]);

            etName.setText(userData[0]);
            etUsername.setText(userData[1]);
            etPhone.setText(userData[2]);
            etPassword.setText(userData[3]);

            etName.setEnabled(false);
            etUsername.setEnabled(false);
            etPhone.setEnabled(false);
            etPassword.setEnabled(false);

            SharedPreferences.Editor editor = getSharedPreferences("UserProfile", MODE_PRIVATE).edit();
            editor.putString("name", userData[0]);
            editor.putString("username", userData[1]);
            editor.putString("phone", userData[2]);
            editor.apply();
        } else {
            Toast.makeText(this, "User data not found", Toast.LENGTH_SHORT).show();
        }
    }
}
