package com.example.chaofanandshake;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.InputFilter;
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
        etPhone.setFilters(new InputFilter[]{new InputFilter.LengthFilter(11)});

        btnSave = findViewById(R.id.btnForgotPassword);
        backBtn = findViewById(R.id.backBtn);

        backBtn.setOnClickListener(v -> finish());

        // Live validation watcher for username & phone inputs
        TextWatcher liveValidationWatcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Do NOT clear username error here because
                // username error must stay if invalid or empty
                // Only clear phone error if phone field changes
                if (s == etPhone.getEditableText()) {
                    layoutPhonenumber.setError(null);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
                if (validationRunnable != null) {
                    handler.removeCallbacks(validationRunnable);
                }
                validationRunnable = () -> validateUsernamePhone(s);
                handler.postDelayed(validationRunnable, 500);
            }
        };

        etUsername.addTextChangedListener(liveValidationWatcher);
        etPhone.addTextChangedListener(liveValidationWatcher);

        // Password live validation (check only if username & phone valid)
        etNewPassword.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Don't clear username error here!
                layoutNewPassword.setError(null);
            }
            @Override
            public void afterTextChanged(Editable s) {
                String username = etUsername.getText().toString().trim();
                String phone = etPhone.getText().toString().trim();
                String newPassword = s.toString().trim();

                // Username required always
                if (username.isEmpty()) {
                    layoutUsername.setError("Please fill out this field");
                    layoutPhonenumber.setError(null);
                    layoutNewPassword.setError(null);
                    return;
                } else if (!checkUsernameExists(username)) {
                    layoutUsername.setError("No account found with this username");
                    layoutPhonenumber.setError(null);
                    layoutNewPassword.setError(null);
                    return;
                } else {
                    layoutUsername.setError(null);
                }

                // Phone required always before validating password length
                if (phone.isEmpty()) {
                    layoutPhonenumber.setError("Please fill out this field");
                    layoutNewPassword.setError(null);
                    return;
                } else if (!verifyUser(username, phone)) {
                    layoutPhonenumber.setError("Username and phone number do not match");
                    layoutNewPassword.setError(null);
                    return;
                } else {
                    layoutPhonenumber.setError(null);
                }

                // Password length check
                if (!newPassword.isEmpty() && newPassword.length() < 6) {
                    layoutNewPassword.setError("Password must be at least 6 characters");
                } else {
                    layoutNewPassword.setError(null);
                }
            }
        });

        btnSave.setOnClickListener(v -> {
            layoutUsername.setError(null);
            layoutPhonenumber.setError(null);
            layoutNewPassword.setError(null);

            String username = etUsername.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String newPassword = etNewPassword.getText().toString().trim();

            if (username.isEmpty()) {
                layoutUsername.setError("Please fill out this field");
                return;
            }
            if (!checkUsernameExists(username)) {
                layoutUsername.setError("No account found with this username");
                return;
            }
            if (phone.isEmpty()) {
                layoutPhonenumber.setError("Please fill out this field");
                return;
            }
            if (!verifyUser(username, phone)) {
                layoutPhonenumber.setError("Username and phone number do not match");
                return;
            }
            if (newPassword.isEmpty()) {
                layoutNewPassword.setError("Please fill out this field");
                return;
            }
            if (newPassword.length() < 6) {
                layoutNewPassword.setError("Password must be at least 6 characters");
                return;
            }

            showConfirmationDialog(username, newPassword);
        });
    }

    // Pass the Editable s to know which field changed
    private void validateUsernamePhone(Editable s) {
        String username = etUsername.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();

        // Username empty check first - ALWAYS ERROR if empty, no delay
        if (username.isEmpty()) {
            runOnUiThread(() -> {
                layoutUsername.setError("Please fill out this field");
                // Clear phone error (but phone error shows only if phone typed)
                layoutPhonenumber.setError(null);
            });
            return;
        }

        // Username exists check
        if (!checkUsernameExists(username)) {
            runOnUiThread(() -> {
                layoutUsername.setError("No account found with this username");
                layoutPhonenumber.setError(null);
            });
            return;
        }

        // If username is valid, clear username error
        runOnUiThread(() -> layoutUsername.setError(null));

        // Phone validation:
        // Only show phone error if user already typed something in phone field,
        // otherwise do not show error while user hasn't typed phone yet.
        if (phone.isEmpty()) {
            // Only clear phone error, no error if phone empty and user not typed anything
            // But if current Editable is phone field and length == 0, then no error
            if (s == etPhone.getEditableText() && s.length() == 0) {
                runOnUiThread(() -> layoutPhonenumber.setError(null));
            }
            // else do nothing (don't show error)
            return;
        }

        // Check if username and phone match
        if (!verifyUser(username, phone)) {
            runOnUiThread(() -> layoutPhonenumber.setError("Username and phone number do not match"));
        } else {
            runOnUiThread(() -> layoutPhonenumber.setError(null));
        }
    }

    private boolean checkUsernameExists(String username) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE username = ?", new String[]{username});
        boolean exists = cursor.moveToFirst();
        cursor.close();
        db.close();
        return exists;
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
