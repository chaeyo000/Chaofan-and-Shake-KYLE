package com.example.chaofanandshake;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chaofanandshake.Domain.ProductDomain;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class ProductdetailsActivity extends AppCompatActivity {

    private ImageView backBtn, productImage;
    private TextView titleText, priceText;
    private Button cartBtn, addtocart;

    private SharedPreferences sharedPreferences;
    private ProductDomain product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_productdetails);

        // Initialize views
        backBtn = findViewById(R.id.backBtn);
        titleText = findViewById(R.id.title);
        priceText = findViewById(R.id.price);
        productImage = findViewById(R.id.image);
        addtocart = findViewById(R.id.addtocart);
        cartBtn = findViewById(R.id.cartbtn);

        sharedPreferences = getSharedPreferences("MyCart", Context.MODE_PRIVATE);

        backBtn.setOnClickListener(view -> onBackPressed());

        // Get product from intent
        product = (ProductDomain) getIntent().getSerializableExtra("product");

        if (product != null) {
            titleText.setText(product.getTitle());
            priceText.setText("₱" + product.getPrice());

            int imageResId = getResources().getIdentifier(product.getImageName(), "drawable", getPackageName());
            productImage.setImageResource(imageResId != 0 ? imageResId : R.drawable.swirls);
        }

        // Add to cart button logic
        addtocart.setOnClickListener(v -> {
            addProductToCart(product);
            Toast.makeText(this, product.getTitle() + " added to cart!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, CartbtnActivity.class));
        });

        // Cart icon button logic
        cartBtn.setOnClickListener(v -> {
            addProductToCart(product);
            Toast.makeText(this, product.getTitle() + " added to cart!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, CartbtnActivity.class));
        });
    }

    private void addProductToCart(ProductDomain product) {
        Gson gson = new Gson();
        String json = sharedPreferences.getString("cart_list", null);
        Type type = new TypeToken<ArrayList<ProductDomain>>(){}.getType();
        ArrayList<ProductDomain> cartList = gson.fromJson(json, type);

        if (cartList == null) {
            cartList = new ArrayList<>();
        }

        boolean found = false;
        for (ProductDomain p : cartList) {
            if (p.getTitle().equals(product.getTitle())) {
                p.setQuantity(p.getQuantity() + 1);
                found = true;
                break;
            }
        }

        if (!found) {
            product.setQuantity(1);
            cartList.add(product);
        }

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("cart_list", gson.toJson(cartList));
        editor.apply();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, DashboardbtnActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }
}
