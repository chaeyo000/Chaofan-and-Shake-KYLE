package com.example.chaofanandshake;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chaofanandshake.Adapter.UserAdapter;
import com.example.chaofanandshake.DatabaseHelper;
import com.example.chaofanandshake.Domain.User;

import java.util.List;

public class ActivityUsers extends AppCompatActivity {

    private ImageView backBtn;
    private RecyclerView recyclerViewUsers;
    private UserAdapter userAdapter;
    private DatabaseHelper dbHelper;
    private List<User> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users); // Make sure your XML file is named correctly

        backBtn = findViewById(R.id.backBtn);
        recyclerViewUsers = findViewById(R.id.recyclerViewUsers);
        recyclerViewUsers.setNestedScrollingEnabled(false);
        recyclerViewUsers.setLayoutManager(new LinearLayoutManager(this));

        dbHelper = new DatabaseHelper(this);

        loadUserList(); // Load user list on first open
        recyclerViewUsers.setNestedScrollingEnabled(false);

        backBtn.setOnClickListener(view -> onBackPressed());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserList(); // Reload user list when returning to this activity
    }

    private void loadUserList() {
        userList = dbHelper.getAllUsers(); // Make sure this method returns updated data
        if (userAdapter == null) {
            userAdapter = new UserAdapter(this, userList, dbHelper);
            recyclerViewUsers.setAdapter(userAdapter);
        } else {
            userAdapter.setUserList(userList); // Update the list
            userAdapter.notifyDataSetChanged(); // Refresh the RecyclerView
        }
    }
}
