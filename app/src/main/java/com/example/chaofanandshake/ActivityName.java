package com.example.chaofanandshake;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import androidx.appcompat.app.AppCompatActivity;

public class ActivityName extends AppCompatActivity {

    private ImageView backBtn;
    private TextInputEditText etNewName;
    private TextInputLayout layoutName;
    private Button btnSaveName;
    private DatabaseHelper dbHelper;
    private String currentUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name);

        dbHelper = new DatabaseHelper(this);

        backBtn = findViewById(R.id.backBtn);
        etNewName = findViewById(R.id.etNewName);
        layoutName = findViewById(R.id.layoutName);
        btnSaveName = findViewById(R.id.btnSaveName);

        // Remove error icon
        layoutName.setErrorIconDrawable(null);

        // Get current username and name from intent extras
        currentUsername = getIntent().getStringExtra("currentUsername");
        String currentName = getIntent().getStringExtra("currentName");

        if (currentName != null) {
            etNewName.setText(currentName);
        }

        // Limit to 12 characters para sa name
        etNewName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // chinicheck nito kung ang ang text sa name ay lumampas sa 12 characters
                if (charSequence.length() > 12 ) {
                    // ang ginagawa nito ay pinipigilan nito ang text na lumampas sa 12 characters lang, kahit ipilit ni user na mag type eyy full stack
                    etNewName.setText(charSequence.subSequence(0, 12));
                    etNewName.setSelection(12);
                    Toast.makeText(ActivityName.this, "Name must not exceed 12 characters", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        // Back button
        backBtn.setOnClickListener(v -> finish());



        // Live validation habang nagta-type
        etNewName.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                validateAndShowErrors(s.toString());
            }
        });

        // Save button click
        btnSaveName.setOnClickListener(v -> {
            String input = etNewName.getText().toString().trim();
            if (validateAndShowErrors(input)) {
                showSaveConfirmationDialog(input);
            }
        });
    }

    // Show confirmation dialog before saving
    private void showSaveConfirmationDialog(String newName) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.name_dialog); // Make sure this layout exists

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        }

        TextView dialogTitle = dialog.findViewById(R.id.dialogTitle);
        TextView dialogMessage = dialog.findViewById(R.id.dialogMessage);
        Button btnCancel = dialog.findViewById(R.id.btnCancel);
        Button btnSave = dialog.findViewById(R.id.btnSave);

        dialogTitle.setText("Save Changes");
        dialogMessage.setText("Are you sure you want to change your name?");

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnSave.setOnClickListener(v -> {
            updateNameInDatabase(newName);
            dialog.dismiss();
        });

        dialog.show();
    }

    /**
     * Validates the name and shows error messages in TextInputLayout if invalid.
     * @param name input name
     * @return true if valid, false otherwise
     */
    private boolean validateAndShowErrors(String name) {
        String errors = validateName(name);
        if (!errors.isEmpty()) {
            layoutName.setError(errors);
            return false;
        } else {
            layoutName.setError(null); // clear error
            return true;
        }
    }

    /**
     * Validation logic for the name
     */
    private String validateName(String name) {
        StringBuilder errors = new StringBuilder();

        if (!name.matches(".*[A-Z].*")) {
            errors.append("• At least one uppercase letter (A-Z)\n");
        }
        if (!name.matches(".*[a-z].*")) {
            errors.append("• At least one lowercase letter (a-z)\n");
        }
        if (name.matches(".*\\d.*")) {
            errors.append("• Should not contain numbers\n");
        }
        if (name.contains(" ")) {
            errors.append("• Should not contain spaces\n");
        }

        return errors.toString().trim(); // remove trailing newline
    }

    /**
     * Updates the name in the database
     */
    private void updateNameInDatabase(String updatedName) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", updatedName);

        int result = database.update("users", values, "username = ?", new String[]{currentUsername});

        if (result > 0) {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("updatedName", updatedName);
            setResult(RESULT_OK, resultIntent);
            finish();
        } else {
            Toast.makeText(this, "Update failed. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }
}
