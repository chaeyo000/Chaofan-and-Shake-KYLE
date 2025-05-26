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
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class ActivityPhone extends AppCompatActivity {

    private ImageView backBtn;
    private TextInputEditText etNewPhone;
    private TextInputLayout layoutPhone;
    private Button btnSavePhone;

    private String currentUsername;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone);

        dbHelper = new DatabaseHelper(this);

        backBtn = findViewById(R.id.backBtn);
        etNewPhone = findViewById(R.id.etNewPhone);
        layoutPhone = findViewById(R.id.layoutPhone);
        btnSavePhone = findViewById(R.id.btnSavePhone);

        layoutPhone.setErrorIconDrawable(null);
        etNewPhone.setFilters(new InputFilter[]{new InputFilter.LengthFilter(11)});

        String currentPhone = getIntent().getStringExtra("currentPhone");
        currentUsername = getIntent().getStringExtra("currentUsername");

        if (currentPhone != null) {
            etNewPhone.setText(currentPhone);
        }

        backBtn.setOnClickListener(v -> finish());

        etNewPhone.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                validateAndShowErrors(s.toString());
            }
        });

        btnSavePhone.setOnClickListener(v -> {
            String input = etNewPhone.getText().toString().trim();
            if (validateAndShowErrors(input)) {
                showSaveConfirmationDialog(input);
            }
        });
    }

    private boolean validateAndShowErrors(String phoneNumber) {
        String errors = validatePhone(phoneNumber);
        if (!errors.isEmpty()) {
            layoutPhone.setError(errors);
            return false;
        } else {
            layoutPhone.setError(null);
            return true;
        }
    }

    private String validatePhone(String phoneNumber) {
        StringBuilder errors = new StringBuilder();

        if (!phoneNumber.matches("^09\\d{9}$")) {
            errors.append("• Please enter a valid number (e.g., 09XXXXXXXXX)\n");
        } else if (dbHelper.checkPhoneExists(phoneNumber) && !isSameAsCurrent(phoneNumber)) {
            errors.append("• This phone number is already in use\n");
        }

        return errors.toString();
    }

    private boolean isSameAsCurrent(String phoneNumber) {
        // Optional check: skip error if user didn’t change their phone
        return phoneNumber.equals(getIntent().getStringExtra("currentPhone"));
    }

    private void showSaveConfirmationDialog(String newNumber) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.phone_dialog);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        }

        TextView dialogTitle = dialog.findViewById(R.id.dialogTitle);
        TextView dialogMessage = dialog.findViewById(R.id.dialogMessage);
        Button btnCancel = dialog.findViewById(R.id.btnCancel);
        Button btnSave = dialog.findViewById(R.id.btnSave);

        dialogTitle.setText("Save Number");
        dialogMessage.setText("Are you sure you want to change your number?");

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnSave.setOnClickListener(v -> {
            updatePhoneInDatabase(newNumber);
            dialog.dismiss();
        });

        dialog.show();
    }

    private void updatePhoneInDatabase(String updatedPhone) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("phone", updatedPhone);

        int result = database.update("users", values, "username = ?", new String[]{currentUsername});

        if (result > 0) {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("updatedPhone", updatedPhone);
            setResult(RESULT_OK, resultIntent);
            finish();
        } else {
            Toast.makeText(this, "Update failed. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }
}
