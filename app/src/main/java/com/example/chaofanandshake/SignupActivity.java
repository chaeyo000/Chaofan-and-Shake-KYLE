package com.example.chaofanandshake;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class SignupActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        Button signup = findViewById(R.id.signupbuttonn);
        if (signup != null) {
            signup.setOnClickListener(v -> {
                Intent intent = new Intent(SignupActivity.this, DashboardbtnActivity.class);
                startActivity(intent);
            });
        }

    }
}
