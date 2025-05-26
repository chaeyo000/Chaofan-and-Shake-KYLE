package com.example.chaofanandshake;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        checkBox = findViewById(R.id.checkBox);
        dbHelper = new DatabaseHelper(this);
        originalTypeface = passwordEditText.getTypeface();

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

        // Login button
        findViewById(R.id.dashboardbtn).setOnClickListener(v -> {
            String username = usernameEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
                Toast.makeText(MainActivity.this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean isValid = dbHelper.checkUser(username, password);
            if (isValid) {
                Toast.makeText(MainActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();

                if (dbHelper.isAdmin(username)) {
                    Intent intent = new Intent(MainActivity.this, AdminDashboardActivity.class);
                    intent.putExtra("username", username);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(MainActivity.this, DashboardbtnActivity.class);
                    intent.putExtra("username", username);
                    startActivity(intent);
                }

            } else {
                Toast.makeText(MainActivity.this, "Incorrect username or password", Toast.LENGTH_SHORT).show();
            }
        });
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
