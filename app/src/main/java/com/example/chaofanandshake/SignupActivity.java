package com.example.chaofanandshake;

import com.example.chaofanandshake.DatabaseHelper;

import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Patterns;
import android.text.TextUtils;
import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class SignupActivity extends AppCompatActivity {

    private EditText nameEditText, emailEditText, phoneEditText, passwordEditText, confirmPasswordEditText;
    private CheckBox termsCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //Tinatawag ang ID para sa input ng user
        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        termsCheckBox = findViewById(R.id.termsCheckBox);

        // Limit kung lumampas sa 11 digits ang phone number sheshh
        phoneEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(11)});


        // Limit to 11 characters para sa name
        nameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // chinicheck nito kung ang ang text sa name ay lumampas sa 11 characters
                if (charSequence.length() > 11) {
                    // ang ginagawa nito ay pinipigilan nito ang text na lumampas sa 11 characters lang, kahit ipilit ni user na mag type eyy full stack
                    nameEditText.setText(charSequence.subSequence(0, 11));
                    nameEditText.setSelection(11);
                    Toast.makeText(SignupActivity.this, "Name must not exceed 11 characters", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        // Pag set ng OnClickListener para sa Sign Up button
        findViewById(R.id.signUpButton).setOnClickListener(v -> {

            // Pagkuha ng mga input
            String name = nameEditText.getText().toString();
            String email = emailEditText.getText().toString();
            String phone = phoneEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            String confirmPassword = confirmPasswordEditText.getText().toString();

            // Validation kung may nawawalang input
            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
                Toast.makeText(SignupActivity.this, "Please fill out this field", Toast.LENGTH_SHORT).show();
            } else if (!name.matches("^[a-zA-Z\\s]+$")) {
                Toast.makeText(SignupActivity.this, "Name must contain only letters", Toast.LENGTH_SHORT).show();
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(SignupActivity.this, "Please enter a valid email address (e.g., user@example.com", Toast.LENGTH_SHORT).show();
            } else if (!phone.matches("^09\\d{9}$")) {
                Toast.makeText(SignupActivity.this, "Please enter a valid number (e.g., 09XXXXXXXXX)", Toast.LENGTH_SHORT).show();
            } else if (!password.equals(confirmPassword)) {
                // Validation kung magkaiba ng password at confirmPassword
                Toast.makeText(SignupActivity.this, "Password do not match", Toast.LENGTH_SHORT).show();
            } else if (!termsCheckBox.isChecked()) {
                Toast.makeText(SignupActivity.this,"You must agree to the Terms & Conditions to create an account.", Toast.LENGTH_SHORT).show();
            } else {
                // Pag-check kung may existing email sa database
                DatabaseHelper dbHelper = new DatabaseHelper(SignupActivity.this);
                if (dbHelper.checkEmailExists(email)) {
                    Toast.makeText(SignupActivity.this, "Email already exists. Please use a different email.", Toast.LENGTH_SHORT).show();
                    return;  // Ititigil pag may kaparehong email
                } else if (dbHelper.checkPhoneExists(phone)) {
                    Toast.makeText(SignupActivity.this, "Phone number already exists. Please use a different phone number.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Pag-save ng user sa database
                boolean isInserted = dbHelper.insertUser(name, email, phone, password);
                if (isInserted) {
                    Toast.makeText(SignupActivity.this, "Sign Up Successful!", Toast.LENGTH_LONG).show();

                    // Magre-redirect sa LoginActivity pagkatapos mag-sign up
                    Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                    intent.putExtra("email", email);
                    startActivity(intent);
                    finish();
                }
            }

            });
        }
    }

