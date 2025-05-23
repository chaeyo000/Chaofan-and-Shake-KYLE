package com.example.chaofanandshake;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chaofanandshake.Domain.ProductDomain;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class ActivityCheckout extends AppCompatActivity {

    private ImageView backBtn;
    private DatabaseHelper dbHelper;
    private RecyclerView recyclerCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        dbHelper = new DatabaseHelper(this);

        backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(view -> onBackPressed());

        // Load cart from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyCart", MODE_PRIVATE);
        String jsonCart = sharedPreferences.getString("cart_list", null);

        ArrayList<ProductDomain> cartList;

        if (jsonCart != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<ProductDomain>>() {}.getType();
            cartList = gson.fromJson(jsonCart, type);
        } else {
            cartList = new ArrayList<>();
        }

        // Setup RecyclerView
        recyclerCart = findViewById(R.id.recyclerCart);
        recyclerCart.setLayoutManager(new LinearLayoutManager(this));
        com.example.chaofanandshake.CheckoutCartAdapter adapter = new com.example.chaofanandshake.CheckoutCartAdapter(cartList);
        recyclerCart.setAdapter(adapter);

        // Calculate total and prepare order summary
        double totalPrice = 0;
        StringBuilder orderSummaryBuilder = new StringBuilder();

        for (ProductDomain product : cartList) {
            double productTotal = product.getPrice() * product.getQuantity();
            totalPrice += productTotal;

            orderSummaryBuilder.append(product.getTitle())
                    .append(" x")
                    .append(product.getQuantity())
                    .append(" - ₱")
                    .append(String.format("%.2f", productTotal))
                    .append("\n");
        }

        String orderSummary = orderSummaryBuilder.toString();

        // Set summary and total price
        TextView tvOrderSummary = findViewById(R.id.tvOrderSummary);
        TextView tvTotalPrice = findViewById(R.id.totalPrice);

        // Phone input & payment
        EditText phoneTextView = findViewById(R.id.phoneTextView);
        RadioGroup rgPaymentMethod = findViewById(R.id.rgPaymentMethod);
        Button btnPlaceOrder = findViewById(R.id.btnPlaceOrder);

        // Limit phone to 11 digits
        phoneTextView.setFilters(new InputFilter[]{new InputFilter.LengthFilter(11)});

        double finalTotalPrice = totalPrice;
        btnPlaceOrder.setOnClickListener(v -> {
            String phone = phoneTextView.getText().toString().trim();
            int selectedPaymentId = rgPaymentMethod.getCheckedRadioButtonId();

            boolean hasError = false;

            if (phone.isEmpty()) {
                phoneTextView.setError("Phone number required");
                phoneTextView.requestFocus();
                hasError = true;

            } else if (!phone.matches("^09\\d{9}$")) {
                phoneTextView.setError("Please enter a valid number (e.g., 09XXXXXXXXX)");
                phoneTextView.requestFocus();
                hasError = true;

            } else {
                phoneTextView.setError(null);
            }

            if (hasError) return;

            if (selectedPaymentId == -1) {
                Toast.makeText(ActivityCheckout.this, "Select a payment method", Toast.LENGTH_SHORT).show();
                return;
            }

            RadioButton selectedPayment = findViewById(selectedPaymentId);
            String paymentMethod = selectedPayment.getText().toString();

            boolean inserted = dbHelper.insertOrder(orderSummary, phone, paymentMethod, finalTotalPrice);

            if (inserted) {
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
        finish();
    }
}
