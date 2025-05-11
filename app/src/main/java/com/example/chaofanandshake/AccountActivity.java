package com.example.chaofanandshake;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.text.TextUtils;
import android.util.Patterns;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class AccountActivity extends AppCompatActivity {

    private ImageView backBtn, ivEditPersonal, ivEditContact;
    private EditText etName, etEmail, etPhone;
    private Button btnSave, btnDelete;

    private DatabaseHelper dbHelper;
    private String currentEmail; // Email ng naka-login na user

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accountdetails);

        dbHelper = new DatabaseHelper(this);

        // Kunin ang email mula sa Intent
        currentEmail = getIntent().getStringExtra("email");

        // I-initialize ang mga view
        backBtn = findViewById(R.id.backBtn);
        ivEditPersonal = findViewById(R.id.ivEditPersonal);
        ivEditContact = findViewById(R.id.ivEditContact);
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        btnSave = findViewById(R.id.btnSave);
        btnDelete = findViewById(R.id.btnDelete);

        // Default na hindi editable ang fields
        setFieldsEditable(false);

        // I-load ang data ng user gamit ang currentEmail
        loadUserData();

        // Back button
        backBtn.setOnClickListener(view -> onBackPressed());

        // I-toggle editing ng name at email
        ivEditPersonal.setOnClickListener(v -> toggleEditText(etName, etEmail, ivEditPersonal));

        // I-toggle editing ng phone
        ivEditContact.setOnClickListener(v -> toggleEditText(etPhone, null, ivEditContact));

        // Save button
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newName = etName.getText().toString();
                String newEmail = etEmail.getText().toString();
                String newPhone = etPhone.getText().toString();

                if (newName.isEmpty() || newEmail.isEmpty() || newPhone.isEmpty()) {
                    Toast.makeText(AccountActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                } else if (!newName.matches("^[a-zA-Z\\s]+$")) {
                    Toast.makeText(AccountActivity.this, "Name must contain only letters", Toast.LENGTH_SHORT).show();
                    return;
                } else if (!Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
                    Toast.makeText(AccountActivity.this, "Please enter a valid email address (e.g., user@example.com)", Toast.LENGTH_SHORT).show();
                    return;
                } else if (!newPhone.matches("^09\\d{9}$")) {
                    Toast.makeText(AccountActivity.this, "Please enter a valid number (e.g., 09XXXXXXXXX)", Toast.LENGTH_SHORT).show();
                    return;
                }

                SQLiteDatabase database = dbHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put("name", newName);
                values.put("email", newEmail);
                values.put("phone", newPhone);

                String whereClause = "email =?"; // kukunin ang currentEmail kung ano ang new Email mo na ipinalit
                String[] whereArgs = new String[] {currentEmail }; // Ang ginagawa nito ay kung ano ang inilagay mong new email ay kukunin ni whereClause "?"

                int result = database.update("users", values, whereClause, whereArgs);

                if (result > 0) {
                    Toast.makeText(AccountActivity.this, "Profile Updated!", Toast.LENGTH_SHORT).show();
                    currentEmail = newEmail;
                    setFieldsEditable(false);

                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("newEmail", newEmail);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                } else {
                    Toast.makeText(AccountActivity.this, "Updated Failed!", Toast.LENGTH_SHORT).show();
                    setFieldsEditable(false);
                    ivEditPersonal.setImageResource(R.drawable.edit_icon);
                    ivEditContact.setImageResource(R.drawable.edit_icon);
                }

            }
        });

        // Delete account button
        btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Delete Account")
                    .setMessage("Are you sure you want to delete your account?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        boolean deleted = dbHelper.deleteUser(currentEmail);
                        if (deleted) {
                            Toast.makeText(this, "Account deleted", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(AccountActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        } else {
                            Toast.makeText(this, "Failed to delete account", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    // Method para i-set kung editable ang mga fields
    private void setFieldsEditable(boolean enabled) {
        etName.setEnabled(enabled);
        etEmail.setEnabled(enabled);
        etPhone.setEnabled(enabled);
    }

    // Method para kunin at i-set ang user data mula sa database
    private void loadUserData() {
        if (currentEmail == null || currentEmail.isEmpty()) {
            Toast.makeText(this, "No user email provided", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] userData = dbHelper.getUserDataByEmail(currentEmail);
        if (userData != null && userData.length >= 3) {
            etName.setText(userData[0]);
            etEmail.setText(userData[1]);
            etPhone.setText(userData[2]);
        } else {
            Toast.makeText(this, "User data not found", Toast.LENGTH_SHORT).show();
        }
    }



    private void toggleEditText(EditText editText1, EditText editText2, ImageView imageView) {
        boolean isEditable = !editText1.isEnabled();
        editText1.setEnabled(isEditable);
        if (editText2 != null) editText2.setEnabled(isEditable);
        imageView.setImageResource(R.drawable.edit_icon);
    }
}

//