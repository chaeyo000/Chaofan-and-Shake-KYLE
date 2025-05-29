package com.example.chaofanandshake;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chaofanandshake.Domain.ProductDomain;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileInputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class ProductdetailsActivity extends AppCompatActivity {

    private ImageView backBtn, productImage;
    private TextView titleText, priceText;
    private MaterialButton cartBtn, addtocart, buynow;
    private TextView descriptionText;

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
        descriptionText = findViewById(R.id.description);
        productImage = findViewById(R.id.ImageView);
        addtocart = findViewById(R.id.addtocart);
        cartBtn = findViewById(R.id.cartbtn);
        buynow = findViewById(R.id.buynow);

        sharedPreferences = getSharedPreferences("MyCart", Context.MODE_PRIVATE);

        backBtn.setOnClickListener(view -> onBackPressed());

        // Get product from intent
        String json = getIntent().getStringExtra("product");
        product = new Gson().fromJson(json, ProductDomain.class);

        if (product != null) {
            titleText.setText(product.getTitle());
            priceText.setText("₱" + product.getPrice());
            descriptionText.setText(product.getDescription());

            String imageNameWithoutExt = product.getImageName().toLowerCase().split("\\.")[0];
            int imageResId = getResources().getIdentifier(imageNameWithoutExt, "drawable", getPackageName());

            if (imageResId != 0) {
                productImage.setImageResource(imageResId);
            } else {
                // try loading from internal storage
                try {
                    FileInputStream fis = openFileInput(product.getImageName());
                    Bitmap bitmap = BitmapFactory.decodeStream(fis);
                    productImage.setImageBitmap(bitmap);
                    fis.close();
                } catch (Exception e) {
                    productImage.setImageResource(R.drawable.swirls);
                    Log.e("ProductDetails", "Failed to load image from storage: " + e.getMessage());
                }
            }
        }

        // Add to cart button logic
        addtocart.setOnClickListener(v -> {
            addProductToCart(product);
            Toast.makeText(this, product.getTitle() + " added to cart!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, CartbtnActivity.class));
        });

        // Buy Now button logic
        buynow.setOnClickListener(v -> {
            Intent intent = new Intent(this, ActivityCheckout.class);

            // Pass the selected product as JSON
            String selectedProductJson = new Gson().toJson(product);
            intent.putExtra("selected_product", selectedProductJson);

            startActivity(intent);
        });

        // Cart icon button logic - NO addProductToCart call here now!
        cartBtn.setOnClickListener(v -> {
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
