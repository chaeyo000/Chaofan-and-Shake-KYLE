package com.example.chaofanandshake;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ActivityPhone extends AppCompatActivity {

    private ImageView backBtn;
    private EditText etNewPhone;
    private Button btnSavePhone;
    private TextView nameError;
    private String currentUsername;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone);

        dbHelper = new DatabaseHelper(this);

        // Initialize views
        backBtn = findViewById(R.id.backBtn);
        etNewPhone = findViewById(R.id.etNewPhone);
        nameError = findViewById(R.id.nameError);
        btnSavePhone = findViewById(R.id.btnSavePhone);

        etNewPhone.setFilters(new InputFilter[]{new InputFilter.LengthFilter(11)});

        // Get current phone number and username passed from AccountActivity
        String currentPhone = getIntent().getStringExtra("currentPhone");
        currentUsername = getIntent().getStringExtra("currentUsername");  // Add this line to get currentUsername

        if (currentPhone != null) {
            etNewPhone.setText(currentPhone);  // Set current phone number in the EditText
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

        // Save button listener
        btnSavePhone.setOnClickListener(v -> {
            String input = etNewPhone.getText().toString().trim();
            if (validateAndShowErrors(input)) {
                showSaveConfirmationDialog(input);
            }
        });
    }

    private void showSaveConfirmationDialog(String newNumber) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.phone_dialog);

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

    private boolean validateAndShowErrors(String phone) {
        String errors = validatePhone(phone);
        if (!errors.isEmpty()) {
            nameError.setText(errors);
            nameError.setVisibility(View.VISIBLE);
            return false;
        } else {
            nameError.setVisibility(View.GONE);
            return true;
        }
    }

        private String validatePhone(String phone) {
            StringBuilder errors = new StringBuilder();

            if ((!phone.matches("^09\\d{9}$"))) {
                errors.append("• Please enter a valid number (e.g., 09XXXXXXXXX)\n");
            }
            return errors.toString();
        }

    private void updatePhoneInDatabase(String updatedPhone) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("phone", updatedPhone);  // Correct field name here is "phone"

        // Update the phone number in the database using the current username
        int result = database.update("users", values, "username = ?", new String[]{currentUsername});

        if (result > 0) {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("updatedPhone", updatedPhone);
            setResult(RESULT_OK, resultIntent);
            finish();  // Close ActivityPhone
        } else {
            Toast.makeText(this, "Update failed. Please try again.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
