package com.example.chaofanandshake;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
                String newPassword = etNewPassword.getText().toString().trim();
                showPasswordConfirmationDialog(newPassword);
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

    private boolean validateCurrentPassword() {
        String inputPassword = etCurrentPassword.getText().toString().trim();
        if (inputPassword.isEmpty()) {
            layoutCurrentPassword.setError("Current password cannot be empty");
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
            layoutNewPassword.setError("Choose a password that's at least 6 characters and not used anywhere else");
            return false;
        }

        if (current.equals(newPass)) {
            layoutNewPassword.setError("New password must be different from current password");
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

    private void showPasswordConfirmationDialog(String newPassword) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_confirm_dialog);

        TextView dialogTitle = dialog.findViewById(R.id.dialogTitle);
        TextView dialogMessage = dialog.findViewById(R.id.dialogMessage);
        Button btnCancel = dialog.findViewById(R.id.btnCancel);
        Button btnConfirm = dialog.findViewById(R.id.btnConfirm);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        }

        dialogTitle.setText("Change password");
        dialogMessage.setText("Are you sure you want to change your password?");

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnConfirm.setOnClickListener(v -> {
            updatePasswordInDatabase(currentUsername, newPassword);
            dialog.dismiss();
        });

        dialog.show();
    }

    private void updatePasswordInDatabase(String username, String newPassword) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("password", newPassword);

        int updated = db.update("users", cv, "username = ?", new String[]{username});
        if (updated > 0) {
            Toast.makeText(this, "Updated Successfully", Toast.LENGTH_LONG).show();
            Intent resultIntent = new Intent();
            resultIntent.putExtra("updatedPassword", newPassword);
            setResult(RESULT_OK, resultIntent);
            finish();

        } else {
            Toast.makeText(this, "Error updating password", Toast.LENGTH_LONG).show();
        }
    }

    public void onClick(View view) {
        Intent intent;
        if (view.getId() == R.id.btnForgotPassword) {
            intent = new Intent(this, ForgotPasswordActivity.class);
        } else {
            return;
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }
}
