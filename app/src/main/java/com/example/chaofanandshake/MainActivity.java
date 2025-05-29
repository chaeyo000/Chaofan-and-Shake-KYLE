package com.example.chaofanandshake;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText usernameEditText, passwordEditText;
    private boolean isPasswordVisible = false;
    private Typeface originalTypeface;
    private CheckBox checkBox;
    private DatabaseHelper dbHelper;
    private CheckBox rememberMeCheckbox;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        checkBox = findViewById(R.id.checkBox);
        dbHelper = new DatabaseHelper(this);
        rememberMeCheckbox = findViewById(R.id.checkBox);
        Button loginButton = findViewById(R.id.dashboardbtn);
        originalTypeface = passwordEditText.getTypeface();

        sharedPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);

        // Check if user should be remembered
        checkRememberedUser();

        // Toggle password visibility
        passwordEditText.setOnTouchListener((v, event) -> {
            final int DRAWABLE_END = 2;
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (passwordEditText.getRight()
                        - passwordEditText.getCompoundDrawables()[DRAWABLE_END].getBounds().width())) {

                    if (isPasswordVisible) {
                        passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    } else {
                        passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    }

                    passwordEditText.setTypeface(originalTypeface);
                    passwordEditText.setSelection(passwordEditText.getText().length());
                    isPasswordVisible = !isPasswordVisible;
                    return true;
                }
            }
            return false;
        });



        loginButton.setOnClickListener(v -> attemptLogin());
    }

    private void checkRememberedUser() {
        SharedPreferences sharedPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);

        // Check if user explicitly logged out
        boolean explicitLogout = sharedPreferences.getBoolean("explicitLogout", false);

        if (explicitLogout) {
            // Clear all preferences if user explicitly logged out
            sharedPreferences.edit().clear().apply();
            return;
        }

        // Proceed with auto-login only if not explicitly logged out
        boolean isRemembered = sharedPreferences.getBoolean("isRemembered", false);
        if (isRemembered) {
            String savedUsername = sharedPreferences.getString("username", "");
            String savedPassword = sharedPreferences.getString("password", "");

            usernameEditText.setText(savedUsername);
            passwordEditText.setText(savedPassword);
            rememberMeCheckbox.setChecked(true);

            // Auto-login only if credentials exist
            if (!savedUsername.isEmpty() && !savedPassword.isEmpty()) {
                attemptLogin();
            }
        }
    }

    private void attemptLogin() {
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (dbHelper.checkUser(username, password)) {
            // Clear any previous explicit logout flag
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove("explicitLogout");

            // Save login state if "Remember Me" is checked
            if (rememberMeCheckbox.isChecked()) {
                editor.putBoolean("isRemembered", true);
                editor.putString("username", username);
                editor.putString("password", password);
            } else {
                editor.putBoolean("isRemembered", false);
            }
            editor.apply();

            // Proceed to dashboard
            if (dbHelper.isAdmin(username)) {
                startActivity(new Intent(this, AdminDashboardActivity.class)
                        .putExtra("username", username));
            } else {
                startActivity(new Intent(this, DashboardbtnActivity.class)
                        .putExtra("username", username));
            }
            finish();
        } else {
            Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show();
        }
    }

    // Signup, Login, Forgot Password
        public void onClick(View view) {
            Intent intent;
            if (view.getId() == R.id.signupbtn) {
                intent = new Intent(this, SignupActivity.class);
            } else if (view.getId() == R.id.loginbtn) {
                intent = new Intent(this, MainActivity.class);
            } else if (view.getId() == R.id.btnForgotPassword) {
                intent = new Intent(this, ForgotPasswordActivity.class);
            } else {
                return;
            }
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            finish();
        }
    }
}