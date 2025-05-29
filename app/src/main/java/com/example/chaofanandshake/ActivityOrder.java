package com.example.chaofanandshake;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chaofanandshake.Adapter.OrderAdapter;
import com.example.chaofanandshake.Domain.Order;

import java.util.List;

public class ActivityOrder extends AppCompatActivity {

    ImageView backBtn;
    private RecyclerView recyclerView;
    private OrderAdapter orderAdapter;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order); // Make sure you have this layout file

        backBtn = findViewById(R.id.backBtn);

        recyclerView = findViewById(R.id.recyclerOrders);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dbHelper = new DatabaseHelper(this);
        loadOrders();

        // Back button
        backBtn.setOnClickListener(v -> finish());
    }

    private void loadOrders() {
        List<Order> orderList = dbHelper.getAllOrders();
        orderAdapter = new OrderAdapter(this, orderList);
        recyclerView.setAdapter(orderAdapter);
    }
}
