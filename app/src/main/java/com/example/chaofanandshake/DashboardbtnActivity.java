package com.example.chaofanandshake;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chaofanandshake.Adapter.ProductAdapter;
import com.example.chaofanandshake.Domain.ProductDomain;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class DashboardbtnActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private TextView welcomeTextView;
    private DatabaseHelper dbHelper;
    private TextView navName;
    private TextView navUsername;
    private String currentUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        dbHelper = new DatabaseHelper(this);

        // Setup Navigation Drawer
        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        setSupportActionBar(toolbar);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.meduimblack));
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            navigationView.setCheckedItem(R.id.home);
        }

        // Sidebar header references
        View headerView = navigationView.getHeaderView(0);
        navName = headerView.findViewById(R.id.nav_name);
        navUsername = headerView.findViewById(R.id.nav_username);

        // Welcome TextView
        welcomeTextView = findViewById(R.id.welcomeTextView);

        // Cart Button
        Button button = findViewById(R.id.cartbtn);
        button.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardbtnActivity.this, CartbtnActivity.class);
            startActivity(intent);
        });

        // Get passed username (email or id) from intent
        currentUsername = getIntent().getStringExtra("username");

        if (currentUsername != null) {
            String userName = dbHelper.getUserName(currentUsername);
            if (userName != null && !userName.isEmpty()) {
                String welcomeMessage = "Welcome " + userName + ", What would you like to <font color='#FFC107'>eat?</font>";
                setWelcomeMessage(welcomeMessage);

                navName.setText(userName);
                navUsername.setText(currentUsername);
            } else {
                // If username not found in DB
                navName.setText("User");
                navUsername.setText(currentUsername);
                setWelcomeMessage("Welcome User, What would you like to eat?");
            }
        } else {
            // No username passed
            setWelcomeMessage("Welcome, What would you like to eat?");
            navName.setText("User");
            navUsername.setText("");
        }

        // Recycler View for products
        initRecyclerView();

        ActivityResultLauncher<Intent> launcher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.hasExtra("newUsername")) {
                            String updatedUsername = data.getStringExtra("newUsername");
                            navUsername.setText(updatedUsername);

                            String updatedName = dbHelper.getUserName(updatedUsername);
                            if (updatedName != null && !updatedName.isEmpty()) {
                                navName.setText(updatedName);
                                String welcomeMessage = "Welcome " + updatedName + ", What would you like to <font color='#FFC107'>eat?</font>";
                                setWelcomeMessage(welcomeMessage);
                            }
                        }
                    }
                }
        );

        // Click listener for navName to go to AccountActivity
        navName.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardbtnActivity.this, AccountActivity.class);
            intent.putExtra("username", navUsername.getText().toString());
            launcher.launch(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (currentUsername != null) {
            String updatedName = dbHelper.getUserName(currentUsername);

            if (updatedName != null && !updatedName.isEmpty()) {
                navName.setText(updatedName);
                String welcomeMessage = "Welcome " + updatedName + ", What would you like to <font color='#FFC107'>eat?</font>";
                setWelcomeMessage(welcomeMessage);
            }
        }
    }

    private void setWelcomeMessage(String htmlMessage) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            welcomeTextView.setText(Html.fromHtml(htmlMessage, Html.FROM_HTML_MODE_LEGACY));
        } else {
            welcomeTextView.setText(Html.fromHtml(htmlMessage));
        }
    }

    private void initRecyclerView() {
        recyclerView = findViewById(R.id.recyclerView);
        if (recyclerView == null) {
            Log.e("RecyclerViewError", "RecyclerView is NULL in DashboardActivity!");
            return;
        }

        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        ArrayList<ProductDomain> productList = new ArrayList<>();
        productList.add(new ProductDomain("swirls", "ChaoRolls", 45.0));
        productList.add(new ProductDomain("rolls", "FanSizzle", 65.0));
        productList.add(new ProductDomain("swirls", "ChaoSiRolls", 85.0));
        productList.add(new ProductDomain("rolls", "Swirls", 45.0));
        productList.add(new ProductDomain("rolls", "Fruit Tea", 45.0));

        ProductAdapter adapter = new ProductAdapter(productList, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.home) {
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        } else if (itemId == R.id.terms) {
            startActivity(new Intent(this, TermsandcondiActivity.class));
        } else if (itemId == R.id.contact) {
            startActivity(new Intent(this, ContactActivity.class));
        } else if (itemId == R.id.logout) {
            Toast.makeText(this, "You have been Logged Out", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
