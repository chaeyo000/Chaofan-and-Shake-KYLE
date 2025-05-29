package com.example.chaofanandshake;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chaofanandshake.Domain.Order;

import java.util.List;

public class OrderhistoryActivity extends AppCompatActivity {

    private ImageView backBtn;
    private RecyclerView orderHistoryRecyclerView;
    private OrderHistoryAdapter adapter;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orderhistory);

        backBtn = findViewById(R.id.backBtn);

        // Back button
        backBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, DashboardbtnActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });


        orderHistoryRecyclerView = findViewById(R.id.orderHistoryRecyclerView);
        dbHelper = new DatabaseHelper(this);

        // Set layout manager
        orderHistoryRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Get all orders
        List<Order> orderList = dbHelper.getAllOrders();

        if (orderList.isEmpty()) {
            Toast.makeText(this, "No order history found.", Toast.LENGTH_SHORT).show();
        }

        // Set adapter
        adapter = new OrderHistoryAdapter(orderList); // <- Pass context if needed in adapter
        orderHistoryRecyclerView.setAdapter(adapter);
    }



}
