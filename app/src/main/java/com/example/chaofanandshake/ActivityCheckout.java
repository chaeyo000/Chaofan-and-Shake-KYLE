package com.example.chaofanandshake;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;

import androidx.appcompat.app.AppCompatActivity;

public class ActivityCheckout extends AppCompatActivity {

    private ImageView backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        RadioButton gcashRadioButton = findViewById(R.id.gcash);
        ImageView gcashImageView = findViewById(R.id.gcashqr);

        gcashRadioButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                gcashImageView.setVisibility(View.VISIBLE); // Show ImageView
            } else {
                gcashImageView.setVisibility(View.GONE); // Hide ImageView
            }
        });

        backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
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



