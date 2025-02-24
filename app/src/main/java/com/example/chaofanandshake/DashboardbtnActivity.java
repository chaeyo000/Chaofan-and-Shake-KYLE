package com.example.chaofanandshake;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chaofanandshake.Adapter.ProductAdapter;
import com.example.chaofanandshake.Domain.ProductDomain;

import java.util.ArrayList;

public class DashboardbtnActivity extends AppCompatActivity {
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Initialize RecyclerView
        initRecyclerView();

        // Cart Button - Redirect to Cart page
        Button button = findViewById(R.id.cartbtn);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(DashboardbtnActivity.this, CartbtnActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initRecyclerView() {
        recyclerView = findViewById(R.id.recyclerView);

        if (recyclerView == null) {
            Log.e("RecyclerViewError", "RecyclerView is NULL in DashboardActivity!");
            return;
        }

        // Set RecyclerView to slide vertically
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

        // Sample Product List
        ArrayList<ProductDomain> productList = new ArrayList<>();
        productList.add(new ProductDomain("swirls", "Swirls Ice Cream", 50.0));
        productList.add(new ProductDomain("rolls", "Chaofan", 99.0));
        productList.add(new ProductDomain("swirls", "Basta Ice Cream", 50.0));

        // Set Adapter
        ProductAdapter adapter = new ProductAdapter(productList, this);
        recyclerView.setAdapter(adapter);
    }
}
