package com.example.chaofanandshake;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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



        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.Open, R.string.Close);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.meduimblack));
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            navigationView.setCheckedItem(R.id.home);
        }

    }

    private void initRecyclerView() {
        recyclerView = findViewById(R.id.recyclerView);

        if (recyclerView == null) {
            Log.e("RecyclerViewError", "RecyclerView is NULL in DashboardActivity!");
            return;
        }

        // Set RecyclerView to slide horizontally
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


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        // Get current activity name
        String currentActivity = this.getClass().getSimpleName();

        // Define a map of menu items to their corresponding activities
        Class<?> targetActivity = null;

        if (itemId == R.id.home) {
            targetActivity = DashboardbtnActivity.class;
        } else if (itemId == R.id.terms) {
            targetActivity = TermsandcondiActivity.class;
        } else if (itemId == R.id.contact) {
            targetActivity = ContactActivity.class;
        } else if (itemId == R.id.logout) {
            Toast.makeText(this, "You have been Logged Out", Toast.LENGTH_SHORT).show();

            // Redirect to login activity
            Intent logout = new Intent(this, LoginActivity.class);
            startActivity(logout);

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        }


        // If the user is already in the selected activity, just close the drawer
        if (targetActivity != null && targetActivity.getSimpleName().equals(currentActivity)) {
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        }

        // If not in the current activity, navigate to the selected one
        if (targetActivity != null) {
            Intent intent = new Intent(this, targetActivity);
            startActivity(intent);
            finish();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }



    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void onClick(View view) {
        if (view.getId() == R.id.accountbtn) {
            startActivity(new Intent(this, AccountActivity.class));
        }
    }

}
