package com.example.chaofanandshake;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
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

        SharedPreferences sharedPreferences = getSharedPreferences("MyCart", MODE_PRIVATE);
        String jsonCart = sharedPreferences.getString("cart_list", null);

        ArrayList<ProductDomain> cartList;
        if (jsonCart != null) {
            Type type = new TypeToken<ArrayList<ProductDomain>>() {}.getType();
            cartList = new Gson().fromJson(jsonCart, type);
        } else {
            cartList = new ArrayList<>();
        }

        recyclerCart = findViewById(R.id.recyclerCart);
        recyclerCart.setLayoutManager(new LinearLayoutManager(this));
        com.example.chaofanandshake.CheckoutCartAdapter adapter = new com.example.chaofanandshake.CheckoutCartAdapter(cartList);
        recyclerCart.setAdapter(adapter);

        // Calculate total price
        final double[] totalPriceHolder = {0.0};
        StringBuilder orderSummaryBuilder = new StringBuilder();

        for (ProductDomain product : cartList) {
            double productTotal = product.getPrice() * product.getQuantity();
            totalPriceHolder[0] += productTotal;

            orderSummaryBuilder.append(product.getTitle())
                    .append(" x")
                    .append(product.getQuantity())
                    .append(" - ₱")
                    .append(String.format("%.2f", productTotal))
                    .append("\n");
        }

        String orderSummary = orderSummaryBuilder.toString();

        TextView tvTotalPrice = findViewById(R.id.totalPrice);
        tvTotalPrice.setText("₱" + String.format("%.2f", totalPriceHolder[0]));

        TextView usernameTextView = findViewById(R.id.username);
        TextView phoneTextView = findViewById(R.id.phone);
        TextView nameTextView = findViewById(R.id.name);

        // Get user info from SharedPreferences
        SharedPreferences userPrefs = getSharedPreferences("UserProfile", MODE_PRIVATE);
        String username = userPrefs.getString("username", "");
        String phone = userPrefs.getString("phone", "");
        String name = userPrefs.getString("name", "");


        usernameTextView.setText(username);
        phoneTextView.setText(phone);
        nameTextView.setText(name);

        RadioGroup rgPaymentMethod = findViewById(R.id.rgPaymentMethod);
        Button btnPlaceOrder = findViewById(R.id.btnPlaceOrder);

        btnPlaceOrder.setOnClickListener(v -> {
            String customerName = nameTextView.getText().toString().trim();
            String phoneInput = phoneTextView.getText().toString().trim();
            String usernameInput = usernameTextView.getText().toString().trim();
            int selectedPaymentId = rgPaymentMethod.getCheckedRadioButtonId();

            if (selectedPaymentId == -1) {
                Toast.makeText(ActivityCheckout.this, "Select a payment method", Toast.LENGTH_SHORT).show();
                return;
            }

            RadioButton selectedPayment = findViewById(selectedPaymentId);
            String paymentMethod = selectedPayment.getText().toString();

            boolean inserted = dbHelper.insertOrder(orderSummary, customerName, phoneInput, usernameInput, paymentMethod, totalPriceHolder[0]);
            if (inserted) {
                Toast.makeText(ActivityCheckout.this, "Order placed successfully!", Toast.LENGTH_LONG).show();

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove("cart_list");
                editor.apply();

                Intent intent = new Intent(ActivityCheckout.this, DashboardbtnActivity.class);
                intent.putExtra("username", usernameInput);
                startActivity(intent);
                finish();
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
