package com.example.chaofanandshake;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chaofanandshake.Domain.ProductDomain;

public class ProductdetailsActivity extends AppCompatActivity {

    private ImageView backBtn, productImage;
    private TextView titleText, priceText;
    private Button cartBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_productdetails);

        // Initialize views after setContentView
        backBtn = findViewById(R.id.backBtn);
        titleText = findViewById(R.id.title);
        priceText = findViewById(R.id.price);
        productImage = findViewById(R.id.image);
        cartBtn = findViewById(R.id.cartbtn);

        backBtn.setOnClickListener(view -> onBackPressed());

        cartBtn.setOnClickListener(v -> {
            Intent intent = new Intent(ProductdetailsActivity.this, CartbtnActivity.class);
            startActivity(intent);
        });

        // Get product data from intent
        ProductDomain product = (ProductDomain) getIntent().getSerializableExtra("product");

        if (product != null) {
            titleText.setText(product.getTitle());
            priceText.setText("₱" + product.getPrice());

            int imageResId = getResources().getIdentifier(
                    product.getImageName(), "drawable", getPackageName()
            );

            if (imageResId != 0) {
                productImage.setImageResource(imageResId);
            } else {
                productImage.setImageResource(R.drawable.swirls); // fallback image
            }
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
