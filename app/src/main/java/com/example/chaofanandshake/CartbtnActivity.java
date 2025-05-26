package com.example.chaofanandshake;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chaofanandshake.Domain.ProductDomain;
import com.example.chaofanandshake.Adapter.CartAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class CartbtnActivity extends AppCompatActivity {

    private ImageView backBtn;
    private RecyclerView recyclerView;
    private TextView totalPriceText;
    private ArrayList<ProductDomain> cartList;
    private CartAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        backBtn = findViewById(R.id.backBtn);
        recyclerView = findViewById(R.id.cartRecyclerView);
        totalPriceText = findViewById(R.id.totalPrice);
        Button checkoutButton = findViewById(R.id.checkoutbtn);

        backBtn.setOnClickListener(view -> onBackPressed());

        checkoutButton.setOnClickListener(v -> {
            if (cartList == null || cartList.isEmpty()) {
                showCartEmptyDialog();
            } else {
                Intent intent = new Intent(this, ActivityCheckout.class);
                startActivity(intent);
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadCartItems();
    }

    private void showCartEmptyDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.cart_dialog);
        dialog.setCancelable(true);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        }

        Button btnClose = dialog.findViewById(R.id.btnClose);
        btnClose.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void loadCartItems() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyCart", MODE_PRIVATE);
        String json = sharedPreferences.getString("cart_list", null);

        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<ProductDomain>>(){}.getType();

        cartList = gson.fromJson(json, type);
        if (cartList == null) {
            cartList = new ArrayList<>();
        }

        adapter = new CartAdapter(cartList);
        recyclerView.setAdapter(adapter);

        adapter.setSaveCartCallback(() -> {
            saveCartItems();
            updateTotalPrice();
        });

        adapter.setOnQuantityChangeListener(() -> updateTotalPrice());

        updateTotalPrice();
    }

    private void saveCartItems() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyCart", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();

        String json = gson.toJson(cartList);
        editor.putString("cart_list", json);
        editor.apply();
    }

    private void updateTotalPrice() {
        double total = 0;
        for (ProductDomain p : cartList) {
            total += p.getPrice() * p.getQuantity();
        }
        totalPriceText.setText(String.format("Total: ₱%.2f", total));
    }

    // Optional: addToCart method if needed in the activity
    private void addToCart(ProductDomain product) {
        if (cartList == null) {
            cartList = new ArrayList<>();
        }
        cartList.add(product);

        saveCartItems();

        if (adapter == null) {
            adapter = new CartAdapter(cartList);
            recyclerView.setAdapter(adapter);

            adapter.setSaveCartCallback(() -> {
                saveCartItems();
                updateTotalPrice();
            });

            adapter.setOnQuantityChangeListener(() -> updateTotalPrice());
        } else {
            adapter.notifyDataSetChanged();
        }

        updateTotalPrice();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
