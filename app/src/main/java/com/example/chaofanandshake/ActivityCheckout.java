package com.example.chaofanandshake;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;

import androidx.appcompat.app.AppCompatActivity;

public class ActivityCheckout extends AppCompatActivity {
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
    }


}
