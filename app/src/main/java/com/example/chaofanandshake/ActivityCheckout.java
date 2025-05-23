package com.example.chaofanandshake;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ActivityCheckout extends AppCompatActivity {

    private ImageView backBtn;
    private DatabaseHelper dbHelper;  // your SQLite helper

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        dbHelper = new DatabaseHelper(this);

        // Existing code...

        RadioButton gcashRadioButton = findViewById(R.id.gcash);
        ImageView gcashImageView = findViewById(R.id.gcashqr);

        gcashRadioButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                gcashImageView.setVisibility(View.VISIBLE);
            } else {
                gcashImageView.setVisibility(View.GONE);
            }
        });

        backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(view -> onBackPressed());

        String username = getIntent().getStringExtra("username");
        SharedPreferences sharedPref = getSharedPreferences("userPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("username", username);
        editor.apply();

        // New part: Checkout logic
        TextView tvOrderSummary = findViewById(R.id.tvOrderSummary);
        TextView tvTotalPrice = findViewById(R.id.totalPrice);
        EditText phoneTextView = findViewById(R.id.phoneTextView);
        RadioGroup rgPaymentMethod = findViewById(R.id.rgPaymentMethod);
        Button btnPlaceOrder = findViewById(R.id.btnPlaceOrder);

        // Get order details from Intent extras (sent from previous activity)
        String orderSummary = getIntent().getStringExtra("order_summary");
        double totalPrice = getIntent().getDoubleExtra("total_price", 0);

        if(orderSummary != null) {
            tvOrderSummary.setText(orderSummary);
        }
        tvTotalPrice.setText("Total: ₱" + String.format("%.2f", totalPrice));

        btnPlaceOrder.setOnClickListener(v -> {
            String phone = phoneTextView.getText().toString().trim();
            int selectedPaymentId = rgPaymentMethod.getCheckedRadioButtonId();

            if(phone.isEmpty()) {
                phoneTextView.setError("Phone number required");
                phoneTextView.requestFocus();
                return;
            }

            if(selectedPaymentId == -1) {
                Toast.makeText(ActivityCheckout.this, "Select a payment method", Toast.LENGTH_SHORT).show();
                return;
            }

            RadioButton selectedPayment = findViewById(selectedPaymentId);
            String paymentMethod = selectedPayment.getText().toString();

            boolean inserted = dbHelper.insertOrder(orderSummary, phone, paymentMethod, totalPrice);

            if(inserted) {
                Toast.makeText(ActivityCheckout.this, "Order placed successfully!", Toast.LENGTH_LONG).show();
                finish(); // Close checkout screen
            } else {
                Toast.makeText(ActivityCheckout.this, "Failed to place order. Try again.", Toast.LENGTH_LONG).show();
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



