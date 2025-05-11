package com.example.chaofanandshake;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chaofanandshake.Adapter.ProductAdapter;
import com.example.chaofanandshake.Domain.ProductDomain;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class DashboardbtnActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    private RecyclerView recyclerView;
    private TextView welcomeTextView;
    private DatabaseHelper dbHelper;
    private TextView navName;
    private TextView navEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        dbHelper = new DatabaseHelper(DashboardbtnActivity.this);

        navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);

        navName = headerView.findViewById(R.id.nav_name);
        navEmail = headerView.findViewById(R.id.nav_email);

        // OnClick to go to AccountActivity
        navName.setOnClickListener(v -> {
            Toast.makeText(DashboardbtnActivity.this, "Clicked name!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(DashboardbtnActivity.this, AccountActivity.class);
            intent.putExtra("email", navEmail.getText().toString());  // Pass current email
            startActivity(intent);
        });

        initRecyclerView();

        Button button = findViewById(R.id.cartbtn);
        button.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardbtnActivity.this, CartbtnActivity.class);
            startActivity(intent);
        });

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.meduimblack));
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            navigationView.setCheckedItem(R.id.home);
        }

        String email = getIntent().getStringExtra("email");
        if (email != null) {
            String userName = dbHelper.getUserName(email);
            if (userName != null && !userName.isEmpty()) {
                welcomeTextView = findViewById(R.id.welcomeTextView);
                welcomeTextView.setText("Welcome " + userName + ", What would you like to eat?");
                navName.setText(userName);
                navEmail.setText(email);
            } else {
                navName.setText("User");
                navEmail.setText(email);
            }
        }
    }


    // Refresh data when returning from AccountActivity
    @Override
    protected void onResume() {
        super.onResume();
        if (navEmail != null && navName != null) {
            String email = navEmail.getText().toString();
            String userName = dbHelper.getUserName(email);
            if (userName != null && !userName.isEmpty()) {
                welcomeTextView = findViewById(R.id.welcomeTextView);
                welcomeTextView.setText("Welcome " + userName + ", What would you like to eat?");
                navName.setText(userName);
            }
        }
    }





    private void initRecyclerView() {
        recyclerView = findViewById(R.id.recyclerView);
        if (recyclerView == null) {
            Log.e("RecyclerViewError", "RecyclerView is NULL in DashboardActivity!");
            return;
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

        ArrayList<ProductDomain> productList = new ArrayList<>();
        productList.add(new ProductDomain("swirls", "Swirls Ice Cream", 50.0));
        productList.add(new ProductDomain("rolls", "Chaofan", 99.0));
        productList.add(new ProductDomain("swirls", "Basta Ice Cream", 50.0));

        ProductAdapter adapter = new ProductAdapter(productList, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        String currentActivity = this.getClass().getSimpleName();
        Class<?> targetActivity = null;

        if (itemId == R.id.home) {
            targetActivity = DashboardbtnActivity.class;
        } else if (itemId == R.id.terms) {
            targetActivity = TermsandcondiActivity.class;
        } else if (itemId == R.id.contact) {
            targetActivity = ContactActivity.class;
        } else if (itemId == R.id.logout) {
            Toast.makeText(this, "You have been Logged Out", Toast.LENGTH_SHORT).show();
            Intent logout = new Intent(this, LoginActivity.class);
            startActivity(logout);
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        }

        if (targetActivity != null && targetActivity.getSimpleName().equals(currentActivity)) {
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        }

        if (targetActivity != null) {
            Intent intent = new Intent(this, targetActivity);
            startActivity(intent);
            finish();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}

