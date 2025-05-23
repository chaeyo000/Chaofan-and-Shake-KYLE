package com.example.chaofanandshake;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class ActivityPassword extends AppCompatActivity {

    private ImageView backBtn;
    private TextInputEditText etCurrentPassword, etNewPassword, etConfirmPassword;
    private TextInputLayout layoutCurrentPassword, layoutNewPassword, layoutConfirmPassword;
    private Button btnSavePassword;

    private DatabaseHelper dbHelper;
    private String currentUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        dbHelper = new DatabaseHelper(this);

        backBtn = findViewById(R.id.backBtn);
        etCurrentPassword = findViewById(R.id.etCurrentPassword);
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);

        layoutCurrentPassword = findViewById(R.id.layoutCurrentPassword);
        layoutNewPassword = findViewById(R.id.layoutNewPassword);
        layoutConfirmPassword = findViewById(R.id.layoutConfirmPassword);

        layoutCurrentPassword.setErrorIconDrawable(null);
        layoutNewPassword.setErrorIconDrawable(null);
        layoutConfirmPassword.setErrorIconDrawable(null);

        btnSavePassword = findViewById(R.id.btnSavePassword);

        currentUsername = getIntent().getStringExtra("currentUsername");
        if (currentUsername == null || currentUsername.isEmpty()) {
            Toast.makeText(this, "User not found, please login again.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        backBtn.setOnClickListener(v -> finish());

        setupLiveValidation();

        btnSavePassword.setOnClickListener(v -> {
            boolean isCurrentValid = validateCurrentPassword();
            boolean isNotSame = validateSamePassword();
            boolean isConfirmValid = validateConfirmPassword();

            if (isCurrentValid && isNotSame && isConfirmValid) {
                // Inflate custom dialog layout
                View dialogView = LayoutInflater.from(ActivityPassword.this).inflate(R.layout.custom_confirm_dialog, null);

                TextView dialogTitle = dialogView.findViewById(R.id.dialogTitle);
                TextView dialogMessage = dialogView.findViewById(R.id.dialogMessage);
                Button btnCancel = dialogView.findViewById(R.id.btnCancel);
                Button btnConfirm = dialogView.findViewById(R.id.btnConfirm);

                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityPassword.this);
                builder.setView(dialogView);

                AlertDialog customDialog = builder.create();

                btnCancel.setOnClickListener(view -> customDialog.dismiss());

                btnConfirm.setOnClickListener(view -> {
                    String newPassword = etNewPassword.getText().toString().trim();
                    updatePasswordInDatabase(currentUsername, newPassword);
                    customDialog.dismiss();
                });

                customDialog.show();
            }
        });
    }

    private void setupLiveValidation() {
        etCurrentPassword.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateCurrentPassword();
                if (!s.toString().trim().isEmpty()) {
                    validateSamePassword();
                }
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        etNewPassword.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateSamePassword();
                validateConfirmPassword();
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        etConfirmPassword.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateConfirmPassword();
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    // RED ERROR UNDERLINE IN TEXT_INPUT_LAYOUT
    private boolean validateCurrentPassword() {
        String inputPassword = etCurrentPassword.getText().toString().trim();
        if (inputPassword.isEmpty()) {
            layoutCurrentPassword.setError("Current pass word cannot be empty");
            return false;
        }

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        boolean isValid = false;

        try {
            cursor = db.rawQuery("SELECT password FROM users WHERE username = ?", new String[]{currentUsername});
            if (cursor.moveToFirst()) {
                String actualPassword = cursor.getString(cursor.getColumnIndexOrThrow("password"));
                isValid = inputPassword.equals(actualPassword);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
        }

        if (!isValid) {
            layoutCurrentPassword.setError("Incorrect current password");
        } else {
            layoutCurrentPassword.setError(null);
        }

        return isValid;
    }

    private boolean validateSamePassword() {
        String current = etCurrentPassword.getText().toString().trim();
        String newPass = etNewPassword.getText().toString().trim();

        if (newPass.isEmpty() || current.isEmpty()) {
            layoutNewPassword.setError(null);
            return false;
        }

        if (newPass.length() < 6) {
            layoutNewPassword.setError("Password must be at least 6 characters");
            return false;
        }

        if (current.equals(newPass)) {
            layoutNewPassword.setError("New password must be different");
            return false;
        }

        layoutNewPassword.setError(null);
        return true;
    }

    private boolean validateConfirmPassword() {
        String newPass = etNewPassword.getText().toString().trim();
        String confirmPass = etConfirmPassword.getText().toString().trim();

        if (newPass.isEmpty() || confirmPass.isEmpty()) {
            layoutConfirmPassword.setError(null);
            return false;
        }

        if (!newPass.equals(confirmPass)) {
            layoutConfirmPassword.setError("Passwords do not match");
            return false;
        }

        layoutConfirmPassword.setError(null);
        return true;
    }

    private void updatePasswordInDatabase(String username, String newPassword) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("password", newPassword);

        int updated = db.update("users", cv, "username = ?", new String[]{username});
        if (updated > 0) {
            Toast.makeText(this, "Password updated successfully", Toast.LENGTH_LONG).show();
            finish();
        } else {
            Toast.makeText(this, "Error updating password", Toast.LENGTH_LONG).show();
        }
    }
}
