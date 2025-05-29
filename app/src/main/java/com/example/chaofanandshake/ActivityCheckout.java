package com.example.chaofanandshake;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chaofanandshake.Adapter.CheckoutCartAdapter;
import com.example.chaofanandshake.Domain.ProductDomain;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class ActivityCheckout extends AppCompatActivity {

    private ImageView backBtn;
    private DatabaseHelper dbHelper;
    private RecyclerView recyclerCart;
    private TextView tvTotalPrice, usernameTextView, phoneTextView, nameTextView;
    private RadioGroup rgPaymentMethod;
    private Button btnPlaceOrder;

    private ArrayList<ProductDomain> cartList;
    private double totalPrice = 0.0;
    private String orderSummary = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        dbHelper = new DatabaseHelper(this);
        initViews();
        handleBackButton();
        loadCartData();
        setupRecyclerView();
        calculateTotalAndSummary();
        loadUserInfo();
        handlePlaceOrder();
    }

    private void initViews() {
        backBtn = findViewById(R.id.backBtn);
        recyclerCart = findViewById(R.id.recyclerCart);
        tvTotalPrice = findViewById(R.id.totalPrice);
        usernameTextView = findViewById(R.id.username);
        phoneTextView = findViewById(R.id.phone);
        nameTextView = findViewById(R.id.name);
        rgPaymentMethod = findViewById(R.id.rgPaymentMethod);
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder);
    }

    private void handleBackButton() {
        backBtn.setOnClickListener(view -> onBackPressed());
    }

    private void loadCartData() {
        Intent intent = getIntent();
        String json = intent.getStringExtra("selected_product");

        if (json != null) {
            ProductDomain buyNowProduct = new Gson().fromJson(json, ProductDomain.class);
            cartList = new ArrayList<>();
            cartList.add(buyNowProduct);
        } else {
            SharedPreferences sharedPreferences = getSharedPreferences("MyCart", MODE_PRIVATE);
            String jsonCart = sharedPreferences.getString("cart_list", null);
            if (jsonCart != null) {
                Type type = new TypeToken<ArrayList<ProductDomain>>() {}.getType();
                cartList = new Gson().fromJson(jsonCart, type);
            } else {
                cartList = new ArrayList<>();
            }
        }
    }

    private void setupRecyclerView() {
        recyclerCart.setLayoutManager(new LinearLayoutManager(this));
        CheckoutCartAdapter adapter = new CheckoutCartAdapter(cartList);
        recyclerCart.setAdapter(adapter);
    }

    private void calculateTotalAndSummary() {
        StringBuilder summaryBuilder = new StringBuilder();

        for (ProductDomain product : cartList) {
            double subtotal = product.getPrice() * product.getQuantity();
            totalPrice += subtotal;

            summaryBuilder.append(product.getTitle())
                    .append(" x")
                    .append(product.getQuantity())
                    .append(" - ₱")
                    .append(String.format("%.2f", subtotal))
                    .append("\n");
        }

        orderSummary = summaryBuilder.toString();
        tvTotalPrice.setText("₱" + String.format("%.2f", totalPrice));
    }

    private void loadUserInfo() {
        SharedPreferences userPrefs = getSharedPreferences("UserProfile", MODE_PRIVATE);
        usernameTextView.setText(userPrefs.getString("username", ""));
        phoneTextView.setText(userPrefs.getString("phone", ""));
        nameTextView.setText(userPrefs.getString("name", ""));
    }

    private void handlePlaceOrder() {
        btnPlaceOrder.setOnClickListener(v -> {
            int selectedPaymentId = rgPaymentMethod.getCheckedRadioButtonId();

            if (selectedPaymentId == -1) {
                Toast.makeText(this, "Select a payment method", Toast.LENGTH_SHORT).show();
                return;
            }

            // Show confirmation dialog
            showConfirmationDialog();
        });
    }

    private void showConfirmationDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_confirm_checkout    , null);
        android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(false)
                .create();

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);

        Button btnCancel = dialogView.findViewById(R.id.btnCancel);
        Button btnConfirm = dialogView.findViewById(R.id.btnConfirm);

        btnCancel.setOnClickListener(view -> dialog.dismiss());

        btnConfirm.setOnClickListener(view -> {
            dialog.dismiss();
            placeOrder();
        });

        dialog.show();
    }

    private void placeOrder() {
        String name = nameTextView.getText().toString().trim();
        String phone = phoneTextView.getText().toString().trim();
        String username = usernameTextView.getText().toString().trim();
        int selectedPaymentId = rgPaymentMethod.getCheckedRadioButtonId();
        String paymentMethod = ((RadioButton) findViewById(selectedPaymentId)).getText().toString();
        long timestamp = System.currentTimeMillis();

        boolean inserted = dbHelper.insertOrder(orderSummary, name, phone, username, paymentMethod, totalPrice, timestamp);
        if (inserted) {
            SharedPreferences.Editor editor = getSharedPreferences("UserProfile", MODE_PRIVATE).edit();
            editor.putString("username", username);
            editor.putString("name", name);
            editor.putString("phone", phone);
            editor.apply();

            if (getIntent().getStringExtra("selected_product") == null) {
                SharedPreferences.Editor cartEditor = getSharedPreferences("MyCart", MODE_PRIVATE).edit();
                cartEditor.remove("cart_list");
                cartEditor.apply();
            }

            showOrderSuccessDialog(username);
        } else {
            Toast.makeText(this, "Failed to place order. Try again.", Toast.LENGTH_LONG).show();
        }
    }

    private void showOrderSuccessDialog(String username) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.order_success_dialog, null);
        android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(false)
                .create();

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);

        Button btnTrackOrder = dialogView.findViewById(R.id.btnTrackOrder);
        Button btnBackHome = dialogView.findViewById(R.id.btnBackHome);

        btnTrackOrder.setOnClickListener(v -> {
            Intent i = new Intent(this, OrderhistoryActivity.class);
            i.putExtra("username", username);
            startActivity(i);
            dialog.dismiss();
            finish();
        });

        btnBackHome.setOnClickListener(v -> {
            Intent i = new Intent(this, DashboardbtnActivity.class);
            i.putExtra("username", username);
            startActivity(i);
            dialog.dismiss();
            finish();
        });

        dialog.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
