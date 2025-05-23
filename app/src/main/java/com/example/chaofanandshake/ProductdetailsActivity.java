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
    private Button cartBtn;

    private SharedPreferences sharedPreferences;

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
        cartBtn = findViewById(R.id.cartbtn);

        sharedPreferences = getSharedPreferences("MyCart", Context.MODE_PRIVATE);

        backBtn.setOnClickListener(view -> onBackPressed());

        // Get product from intent
        ProductDomain product = (ProductDomain) getIntent().getSerializableExtra("product");

        if (product != null) {
            String title = product.getTitle();
            double price = product.getPrice();
            String imageName = product.getImageName();

            titleText.setText(title);
            priceText.setText("₱" + price);

            int imageResId = getResources().getIdentifier(imageName, "drawable", getPackageName());
            productImage.setImageResource(imageResId != 0 ? imageResId : R.drawable.swirls);

            cartBtn.setOnClickListener(v -> {
                Gson gson = new Gson();

                // Load existing cart list
                String json = sharedPreferences.getString("cart_list", null);
                Type type = new TypeToken<ArrayList<ProductDomain>>(){}.getType();
                ArrayList<ProductDomain> cartList = gson.fromJson(json, type);

                if (cartList == null) {
                    cartList = new ArrayList<>();
                }

                // Check if product already exists in cart to increase quantity (optional)
                boolean found = false;
                for (ProductDomain p : cartList) {
                    if (p.getTitle().equals(product.getTitle())) {
                        p.setQuantity(p.getQuantity() + 1);
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    product.setQuantity(1);  // initialize quantity
                    cartList.add(product);
                }

                // Save updated cart list
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("cart_list", gson.toJson(cartList));
                editor.apply();

                Toast.makeText(ProductdetailsActivity.this, product.getTitle() + " added to cart!", Toast.LENGTH_SHORT).show();

                // Optionally open cart activity
                Intent intent = new Intent(ProductdetailsActivity.this, CartbtnActivity.class);
                startActivity(intent);
            });

        }
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
