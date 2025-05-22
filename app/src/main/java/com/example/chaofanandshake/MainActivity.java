package com.example.chaofanandshake;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import android.content.SharedPreferences;
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
        checkBox = findViewById(R.id.checkBox);

        dbHelper = new DatabaseHelper(MainActivity.this);

        passwordEditText = findViewById(R.id.passwordEditText);

        originalTypeface = passwordEditText.getTypeface();




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



        findViewById(R.id.dashboardbtn).setOnClickListener(v -> {
            String username = usernameEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
                Toast.makeText(MainActivity.this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
            } else if (username.isEmpty()) {

            } else {
                boolean isValid = dbHelper.checkUser(username, password);

                if (isValid) {
                    Toast.makeText(MainActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();

                    DatabaseHelper db = new DatabaseHelper(this);

                    if (db.isAdmin(username)) {
                        // Redirect to Admin Dashboard
                        Intent intent = new Intent(MainActivity.this, AdminDashboardActivity.class);
                        intent.putExtra("username", username);
                        startActivity(intent);
                    } else {
                        // Redirect to User Dashboard
                        Intent intent = new Intent(MainActivity.this, DashboardbtnActivity.class);
                        intent.putExtra("username", username);
                        startActivity(intent);

                    }




                } else {
                    Toast.makeText(MainActivity.this, "Incorrect username or password", Toast.LENGTH_SHORT).show();

                    SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.clear();  // I-clear ang session data
                    editor.apply();
                }
            }
        });
    }

    // Method to handle signup and login buttons
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

