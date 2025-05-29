package com.example.chaofanandshake;

import com.example.chaofanandshake.DatabaseHelper;

import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Patterns;
import android.text.TextUtils;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class SignupActivity extends AppCompatActivity {

    private EditText nameEditText, usernameEditText, phoneEditText, passwordEditText, confirmPasswordEditText;
    private CheckBox termsCheckBox;
    private boolean isPasswordVisible = false;
    private Typeface originalTypeface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //Tinatawag ang ID para sa input ng user
        nameEditText = findViewById(R.id.nameEditText);
        usernameEditText = findViewById(R.id.usernameEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        termsCheckBox = findViewById(R.id.termsCheckBox);

        originalTypeface = passwordEditText.getTypeface();

        TextView termsAndCondiTextView = findViewById(R.id.termsandcondi);
        termsAndCondiTextView.setOnClickListener(v -> {
            Intent intent = new Intent(SignupActivity.this, TermsandcondiActivity.class);
            startActivity(intent);
        });

        passwordEditText.setOnTouchListener((v, event) -> {
            final int DRAWABLE_END = 2;

            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (passwordEditText.getRight()
                        - passwordEditText.getCompoundDrawables()[DRAWABLE_END].getBounds().width())) {

                    // Toggle password visibility
                    if (isPasswordVisible) {
                        passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    } else {
                        passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    }

                    // Restore font and cursor position
                    passwordEditText.setTypeface(originalTypeface);
                    passwordEditText.setSelection(passwordEditText.getText().length());

                    isPasswordVisible = !isPasswordVisible;
                    return true;
                }
            }
            return false;



        });

        confirmPasswordEditText.setOnTouchListener((v, event) -> {
            final int DRAWABLE_END = 2;

            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (confirmPasswordEditText.getRight()
                        - confirmPasswordEditText.getCompoundDrawables()[DRAWABLE_END].getBounds().width())) {

                    // Toggle password visibility
                    if (isPasswordVisible) {
                        confirmPasswordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    } else {
                        confirmPasswordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    }

                    // Restore font and cursor position
                    confirmPasswordEditText.setTypeface(originalTypeface);
                    confirmPasswordEditText.setSelection(confirmPasswordEditText.getText().length());

                    isPasswordVisible = !isPasswordVisible;
                    return true;
                }
            }
            return false;
        });

        // Limit kung lumampas sa 11 digits ang phone number sheshh
        phoneEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(11)});

        // Limit to 12 characters para sa name
        nameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // chinicheck nito kung ang ang text sa name ay lumampas sa 12 characters
                if (charSequence.length() > 12 ) {
                    // ang ginagawa nito ay pinipigilan nito ang text na lumampas sa 12 characters lang, kahit ipilit ni user na mag type eyy full stack
                    nameEditText.setText(charSequence.subSequence(0, 12));
                    nameEditText.setSelection(12);
                    Toast.makeText(SignupActivity.this, "Name must not exceed 12 characters", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        usernameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }



            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 15 ) {
                    usernameEditText.setText(charSequence.subSequence(0, 15));
                    usernameEditText.setSelection(15);
                    Toast.makeText(SignupActivity.this, "Username can be up to 15 characters only.", Toast.LENGTH_SHORT).show();
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
            String username = usernameEditText.getText().toString();
            String phone = phoneEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            String confirmPassword = confirmPasswordEditText.getText().toString();

            // Validation kung may nawawalang input
            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(username) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
                Toast.makeText(SignupActivity.this, "Please fill out this field", Toast.LENGTH_SHORT).show();

            } else if (!name.matches("^[a-zA-Z\\s]+$")) {
                Toast.makeText(SignupActivity.this, "Name must contain only letters", Toast.LENGTH_SHORT).show();

            } else if (!phone.matches("^09\\d{9}$")) {
                Toast.makeText(SignupActivity.this, "Please enter a valid number (e.g., 09XXXXXXXXX)", Toast.LENGTH_SHORT).show();

            } else if (password.length() < 6) {
                Toast.makeText(SignupActivity.this, "Choose a password that's at least 6 characters and not used anywhere else.", Toast.LENGTH_SHORT).show();

            } else if (!password.equals(confirmPassword)) {
                // Validation kung magkaiba ng password at confirmPassword
                Toast.makeText(SignupActivity.this, "Password do not match", Toast.LENGTH_SHORT).show();

            } else if (!termsCheckBox.isChecked()) {
                Toast.makeText(SignupActivity.this,"You must agree to the Terms & Conditions to create an account.", Toast.LENGTH_SHORT).show();

            } else {

                // Pag-check kung may existing email sa database
                DatabaseHelper dbHelper = new DatabaseHelper(SignupActivity.this);
                if (dbHelper.checkUsernameExists(username)) {
                    Toast.makeText(SignupActivity.this, "Username already exists. Please use a different username.", Toast.LENGTH_SHORT).show();
                    return;  // Ititigil pag may kaparehong Username

                } else if (dbHelper.checkPhoneExists(phone)) {
                    Toast.makeText(SignupActivity.this, "Phone number already exists. Please use a different phone number.", Toast.LENGTH_SHORT).show();
                    return;
                }

                boolean isInserted = dbHelper.insertUser(name, username, phone, password);
                if (isInserted) {
                    Toast.makeText(SignupActivity.this, "Sign Up Successful!", Toast.LENGTH_LONG).show();

                    // Save user info in SharedPreferences so ActivityCheckout can access it
                    SharedPreferences userPrefs = getSharedPreferences("UserProfile", MODE_PRIVATE);
                    SharedPreferences.Editor editor = userPrefs.edit();
                    editor.putString("username", username);
                    editor.putString("phone", phone);
                    editor.putString("name", name);
                    editor.apply();

                    Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                    intent.putExtra("username", username);
                    intent.putExtra("name", name);
                    intent.putExtra("phone", phone);
                    startActivity(intent);
                    finish();
                }

            }

        });


    }
}