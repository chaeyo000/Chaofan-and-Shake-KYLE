package com.example.chaofanandshake;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
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

        Log.d("AccountActivity", "Username received: " + currentUsername); // Debugging

        // Initialize views
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

        loadUserData(); // Load user data

        backBtn.setOnClickListener(view -> onBackPressed());

        // Edit actions
        ivEditName.setOnClickListener(view -> openEditActivity(ActivityName.class, "currentName", etName.getText().toString()));
        ivEditUsername.setOnClickListener(view -> openEditActivity(ActivityUsername.class, "currentValue", etUsername.getText().toString()));
        ivEditPhone.setOnClickListener(view -> openEditActivity(ActivityPhone.class, "currentPhone", etPhone.getText().toString()));
        ivEditPassword.setOnClickListener(view -> openEditActivity(ActivityPassword.class, "currentPassword", etPassword.getText().toString()));
        // Delete account
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

        Button btnCancel = dialogView.findViewById(R.id.btnCancel);
        Button btnDelete = dialogView.findViewById(R.id.btnDelete);

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnDelete.setOnClickListener(v -> {
            boolean deleted = dbHelper.deleteUser(currentUsername);
            if (deleted) {
                Toast.makeText(this, "Account deleted", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(AccountActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Failed to delete account", Toast.LENGTH_SHORT).show();
            }
            dialog.dismiss();
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
        if (updatedValue != null) {
            editText.setText(updatedValue);
            updateDatabase(dbField, updatedValue);

            if (dbField.equals("username")) {
                currentUsername = updatedValue; //
            }

            // Always return updated username sa result, kahit anong field ang binago
            Intent resultIntent = new Intent();
            resultIntent.putExtra("newUsername", currentUsername); // ← ITO ANG IBABALIK SA DASHBOARD
            setResult(RESULT_OK, resultIntent);

        }
    }

    private void updateDatabase(String field, String value) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(field, value);

        int result = database.update("users", values, "username = ?", new String[]{currentUsername});

        if (result > 0) {
        } else {
            Toast.makeText(this, "Failed to update ", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadUserData() {
        if (currentUsername == null || currentUsername.isEmpty()) {
            Toast.makeText(this, "No user email provided", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] userData = dbHelper.getUserDataByUsername(currentUsername);
        if (userData != null && userData.length >= 3) {
            Log.d("AccountActivity", "User Data: Name=" + userData[0] + ", Username=" + userData[1] + ", Phone=" + userData[2] + ", Password=" + userData[3]);

            etName.setText(userData[0]);
            etUsername.setText(userData[1]);
            etPhone.setText(userData[2]);
            etPassword.setText(userData[3]);

            // Disable fields from editing
            etName.setEnabled(false);
            etUsername.setEnabled(false);
            etPhone.setEnabled(false);
            etPassword.setEnabled(false);
        } else {
            Toast.makeText(this, "User data not found", Toast.LENGTH_SHORT).show();
        }
    }
}
