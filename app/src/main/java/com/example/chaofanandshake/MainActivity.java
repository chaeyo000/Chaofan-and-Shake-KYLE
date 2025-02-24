package com.example.chaofanandshake;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main); // Keep using activity_main.xml

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Button to go to DashboardbtnActivity
        Button dashboardButton = findViewById(R.id.dashboardbtn);
        if (dashboardButton != null) {
            dashboardButton.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, DashboardbtnActivity.class);
                startActivity(intent);
            });
        }
    }

    public void onClick(View view) {
        if (view.getId() == R.id.signupbtn) {
            startActivity(new Intent(this, SignupActivity.class));
        } else if (view.getId() == R.id.loginbtn) {
            startActivity(new Intent(this, LoginActivity.class));
        }
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