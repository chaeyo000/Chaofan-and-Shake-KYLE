package com.example.chaofanandshake.Adapter;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.AutoCompleteTextView;
import android.widget.ArrayAdapter;

import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chaofanandshake.DatabaseHelper;
import com.example.chaofanandshake.R;

public class EditUserActivity extends AppCompatActivity {

    private EditText etEditName, etEditUsername, etEditPhone;
    private AutoCompleteTextView autoCompleteRole;
    private Button btnSaveEdit;
    private DatabaseHelper dbHelper;
    private int userId; // Use id for updating

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);

        dbHelper = new DatabaseHelper(this);

        etEditName = findViewById(R.id.etEditName);
        etEditUsername = findViewById(R.id.etEditUsername);
        etEditPhone = findViewById(R.id.etEditPhone);
        btnSaveEdit = findViewById(R.id.btnSaveEdit);
        autoCompleteRole = findViewById(R.id.autoCompleteRole);
        String[] roles = {"user", "admin"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, roles);
        autoCompleteRole.setAdapter(adapter);

        // Show dropdown on click
        autoCompleteRole.setOnClickListener(v -> autoCompleteRole.showDropDown());

        // Get user data from Intent
        userId = getIntent().getIntExtra("userId", -1);
        String currentName = getIntent().getStringExtra("name");
        String currentUsername = getIntent().getStringExtra("username");
        String currentPhone = getIntent().getStringExtra("phone");
        String currentRole = getIntent().getStringExtra("role");
        if (currentRole != null) {
            autoCompleteRole.setText(currentRole, false);
        } // Set current role in dropdown

        etEditName.setText(currentName);
        etEditUsername.setText(currentUsername);
        etEditPhone.setText(currentPhone);

        btnSaveEdit.setOnClickListener(v -> {
            String newName = etEditName.getText().toString().trim();
            String newUsername = etEditUsername.getText().toString().trim();
            String newPhone = etEditPhone.getText().toString().trim();
            String newRole = autoCompleteRole.getText().toString().trim();

            if (newName.isEmpty() || newUsername.isEmpty() || newPhone.isEmpty() || newRole.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean updated = dbHelper.updateUser(userId, newName, newUsername, newPhone, newRole);
            if (updated) {
                Toast.makeText(this, "User updated", Toast.LENGTH_SHORT).show();

                // Prepare data to return to caller (AdminDashboard)
                Intent resultIntent = new Intent();
                resultIntent.putExtra("updatedRole", newRole);
                resultIntent.putExtra("updatedName", newName);
                resultIntent.putExtra("updatedUsername", newUsername);
                resultIntent.putExtra("updatedPhone", newPhone);
                setResult(RESULT_OK, resultIntent);
                finish();
            } else {
                Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
