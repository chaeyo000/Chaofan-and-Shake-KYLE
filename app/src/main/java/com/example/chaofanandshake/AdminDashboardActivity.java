package com.example.chaofanandshake;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chaofanandshake.Adapter.UserAdapter;
import com.example.chaofanandshake.Domain.User;

import java.util.List;

public class AdminDashboardActivity extends AppCompatActivity {

    private RecyclerView productRecyclerView;
    private UserAdapter userAdapter;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard); // your XML filename

        productRecyclerView = findViewById(R.id.productRecyclerView);
        productRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        dbHelper = new DatabaseHelper(this);
        List<User> userList = dbHelper.getAllUsers(); // You’ll define this next

        userAdapter = new UserAdapter(this, userList, dbHelper);
        productRecyclerView.setAdapter(userAdapter);
    }

}