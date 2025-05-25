package com.example.chaofanandshake;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class ForgotPasswordActivity extends AppCompatActivity {

    private TextInputEditText etUsername, etPhone, etNewPassword;
    private TextInputLayout layoutUsername, layoutPhonenumber, layoutNewPassword;
    private Button btnSave;
    private ImageView backBtn;
    private DatabaseHelper dbHelper;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable validationRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        dbHelper = new DatabaseHelper(this);

        etUsername = findViewById(R.id.etUsername);
        etPhone = findViewById(R.id.etPhone);
        etNewPassword = findViewById(R.id.etNewPassword);

        layoutUsername = findViewById(R.id.layoutUsername);
        layoutPhonenumber = findViewById(R.id.layoutPhonenumber);
        layoutNewPassword = findViewById(R.id.layoutNewPassword);

        layoutUsername.setErrorIconDrawable(null);
        layoutPhonenumber.setErrorIconDrawable(null);
        layoutNewPassword.setErrorIconDrawable(null);

        btnSave = findViewById(R.id.btnForgotPassword);
        backBtn = findViewById(R.id.backBtn);

        backBtn.setOnClickListener(v -> finish());

        // Live validation: listen to username and phone changes
        TextWatcher liveValidationWatcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Clear errors immediately when typing
                layoutPhonenumber.setError(null);
                layoutUsername.setError(null);
            }
            @Override
            public void afterTextChanged(Editable s) {
                // Debounce validation to run 500ms after user stops typing
                if (validationRunnable != null) {
                    handler.removeCallbacks(validationRunnable);
                }
                validationRunnable = () -> validateUsernamePhone();
                handler.postDelayed(validationRunnable, 500);
            }
        };

        etUsername.addTextChangedListener(liveValidationWatcher);
        etPhone.addTextChangedListener(liveValidationWatcher);

        btnSave.setOnClickListener(v -> {
            // Clear previous errors
            layoutUsername.setError(null);
            layoutPhonenumber.setError(null);
            layoutNewPassword.setError(null);

            String username = etUsername.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String newPassword = etNewPassword.getText().toString().trim();

            boolean hasError = false;

            if (username.isEmpty() || phone.isEmpty() || newPassword.isEmpty()) {
                layoutUsername.setError("Please fill out this field");
                hasError = true;
                return;
            }

            if (!phone.matches("^09\\d{9}$")) {
                layoutPhonenumber.setError("Please enter a valid number (e.g., 09XXXXXXXXX)");
                hasError = true;
                return;
            }
             if (newPassword.length() < 6) {
                layoutNewPassword.setError("Choose a password that's at least 6 characters and not used anywhere else.");
                hasError = true;
                return;
            }

            if (!hasError) {
                // Verify user again before confirming password reset
                if (verifyUser(username, phone)) {
                    showConfirmationDialog(username, newPassword);
                } else {
                    layoutPhonenumber.setError("Username and phone number do not match");
                }
            }
        });
    }

    private void validateUsernamePhone() {
        String username = etUsername.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();

        if (username.isEmpty() || phone.isEmpty()) {
            // Don’t validate if either is empty
            return;
        }

        if (!verifyUser(username, phone)) {
            runOnUiThread(() -> {
                layoutPhonenumber.setError("Username and phone number do not match");
            });
        } else {
            runOnUiThread(() -> {
                layoutPhonenumber.setError(null);
            });
        }
    }

    private boolean verifyUser(String username, String phone) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE username = ? AND phone = ?", new String[]{username, phone});
        boolean found = cursor.moveToFirst();
        cursor.close();
        db.close();
        return found;
    }

    private void showConfirmationDialog(String username, String newPassword) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.custom_confirm_dialog, null);

        TextView dialogTitle = dialogView.findViewById(R.id.dialogTitle);
        TextView dialogMessage = dialogView.findViewById(R.id.dialogMessage);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);
        Button btnConfirm = dialogView.findViewById(R.id.btnConfirm);

        dialogTitle.setText("Password Reset");
        dialogMessage.setText("Are you sure you want to change your password?");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialog.show();

        btnConfirm.setOnClickListener(v -> {
            updatePassword(username, newPassword);
            dialog.dismiss();
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());
    }

    private void updatePassword(String username, String newPassword) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("password", newPassword);
        int rows = db.update("users", values, "username = ?", new String[]{username});
        db.close();

        if (rows > 0) {
            Toast.makeText(this, "Password updated!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to update password", Toast.LENGTH_SHORT).show();
        }
    }
}
