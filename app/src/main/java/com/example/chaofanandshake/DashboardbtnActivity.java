package com.example.chaofanandshake;

import androidx.core.content.res.ResourcesCompat;
import android.content.res.Resources;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.example.chaofanandshake.Adapter.BannerAdapter;
import com.example.chaofanandshake.Adapter.ProductAdapter;
import com.example.chaofanandshake.Domain.Order;
import com.example.chaofanandshake.Domain.ProductDomain;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    private MaterialButton notifButton;

    private ViewPager2 bannerViewPager;
    private Handler sliderHandler = new Handler();
    private Runnable sliderRunnable;
    private int currentPage = 0;
    private static final long AUTO_SLIDE_DELAY = 3000;
    private List<Integer> bannerImages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        notifButton = findViewById(R.id.notif);
        dbHelper = new DatabaseHelper(this);
        welcomeTextView = findViewById(R.id.welcomeTextView);

        notifButton.setOnClickListener(v -> {
            // Show orders that are ready for pickup
            List<Order> readyOrders = dbHelper.getOrdersByStatus("Ready for pickup");

            if (readyOrders.isEmpty()) {
                Toast.makeText(this, "No orders ready for pickup", Toast.LENGTH_SHORT).show();
            } else {
                // Create and show a dialog with ready orders
                showReadyOrdersDialog(readyOrders);
            }
        });

        // Setup Navigation Drawer
        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        setSupportActionBar(toolbar);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.open, R.string.close);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.meduimblack));
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            navigationView.setCheckedItem(R.id.home);
        }

        View headerView = navigationView.getHeaderView(0);
        navName = headerView.findViewById(R.id.nav_name);
        navUsername = headerView.findViewById(R.id.nav_username);

        Button button = findViewById(R.id.cartbtn);
        button.setOnClickListener(v -> startActivity(new Intent(this, CartbtnActivity.class)));

        // ViewPager
        bannerViewPager = findViewById(R.id.bannerViewPager);
        bannerImages = Arrays.asList(R.drawable.banner1, R.drawable.banner2);
        BannerAdapter adapter = new BannerAdapter(bannerImages, bannerViewPager);
        bannerViewPager.setAdapter(adapter);
        adapter.setInitialPosition();

        LinearLayout layoutDots = findViewById(R.id.layoutDots);
        setupDotIndicators(bannerImages.size(), layoutDots);

        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(40));
        compositePageTransformer.addTransformer((page, position) -> {
            float r = 1 - Math.abs(position);
            page.setScaleY(0.85f + r * 0.15f);
        });
        bannerViewPager.setPageTransformer(compositePageTransformer);

        sliderRunnable = () -> {
            if (bannerImages.size() == 0) return;
            currentPage++;
            bannerViewPager.setCurrentItem(currentPage, true);
            sliderHandler.postDelayed(sliderRunnable, AUTO_SLIDE_DELAY);
        };

        bannerViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                currentPage = position;
                updateDotIndicators(position % bannerImages.size(), layoutDots);
                sliderHandler.removeCallbacks(sliderRunnable);
                sliderHandler.postDelayed(sliderRunnable, AUTO_SLIDE_DELAY);
            }
        });

        bannerViewPager.setOnTouchListener((v, event) -> {
            sliderHandler.removeCallbacks(sliderRunnable);
            if (event.getAction() == MotionEvent.ACTION_UP ||
                    event.getAction() == MotionEvent.ACTION_CANCEL) {
                sliderHandler.postDelayed(sliderRunnable, AUTO_SLIDE_DELAY);
            }
            return false;
        });

        // ✅ Get current username
        currentUsername = getIntent().getStringExtra("username");

        // ✅ If SharedPrefs not yet saved, initialize them
        SharedPreferences prefs = getSharedPreferences("UserProfile", MODE_PRIVATE);
        if (!prefs.contains("username") && currentUsername != null) {
            String dbName = dbHelper.getUserName(currentUsername);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("username", currentUsername);
            editor.putString("name", dbName != null ? dbName : "");
            editor.putString("phone", "");
            editor.apply();
        }

        // ✅ Load user info
        updateUserInfo();

        initRecyclerView();

        // ✅ Handle updates from AccountActivity
        ActivityResultLauncher<Intent> launcher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        String updatedUsername = result.getData().getStringExtra("newUsername");
                        if (updatedUsername != null) {
                            currentUsername = updatedUsername;
                            updateUserInfo();
                        }
                    }
                });

        navName.setOnClickListener(v -> {
            Intent intent = new Intent(this, AccountActivity.class);
            intent.putExtra("username", currentUsername);
            launcher.launch(intent);
        });
    }

    private void showReadyOrdersDialog(List<Order> orders) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Orders Ready for Pickup");

        // Create a simple list of order summaries
        String[] items = new String[orders.size()];
        for (int i = 0; i < orders.size(); i++) {
            Order order = orders.get(i);
            items[i] = order.getCustomerName() + " - " + order.getOrderSummary();
        }

        builder.setItems(items, (dialog, which) -> {
            // Optional: Handle when a specific order is clicked
        });

        builder.setPositiveButton("OK", null);
        builder.show();
    }

    private void checkForNotifications() {
        List<Order> readyOrders = dbHelper.getOrdersByStatus("Ready for pickup");
        if (!readyOrders.isEmpty()) {
            try {
                // Try to set the badge icon
                notifButton.setIcon(ResourcesCompat.getDrawable(
                        getResources(),
                        R.drawable.notify,
                        null
                ));
            } catch (Resources.NotFoundException e) {
                // Fallback to regular icon if badge doesn't exist
                notifButton.setIcon(ResourcesCompat.getDrawable(
                        getResources(),
                        R.drawable.notify,
                        null
                ));
            }
        } else {
            // No notifications, use regular icon
            notifButton.setIcon(ResourcesCompat.getDrawable(
                    getResources(),
                    R.drawable.notify,
                    null
            ));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        sliderHandler.postDelayed(sliderRunnable, AUTO_SLIDE_DELAY);
        updateUserInfo();
        checkForNotifications();
    }

    // Rest of your methods remain the same...
    private void updateUserInfo() {
        SharedPreferences prefs = getSharedPreferences("UserProfile", MODE_PRIVATE);
        String name = prefs.getString("name", "");
        String username = prefs.getString("username", currentUsername);
        currentUsername = username;

        if (!name.isEmpty()) {
            String welcomeMessage = "Welcome " + name + ", What would you like to <font color='#FFC107'>eat?</font>";
            setWelcomeMessage(welcomeMessage);
            navName.setText(name);
            navUsername.setText(username);
        } else {
            // Fallback to DB if prefs are missing
            String dbName = dbHelper.getUserName(username);
            if (dbName != null) {
                navName.setText(dbName);
                setWelcomeMessage("Welcome " + dbName + ", What would you like to <font color='#FFC107'>eat?</font>");
            } else {
                navName.setText("User");
                setWelcomeMessage("Welcome User, What would you like to eat?");
            }
            navUsername.setText(username);
        }
    }

    private void setWelcomeMessage(String htmlMessage) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            welcomeTextView.setText(Html.fromHtml(htmlMessage, Html.FROM_HTML_MODE_LEGACY));
        } else {
            welcomeTextView.setText(Html.fromHtml(htmlMessage));
        }
    }

    private void setupDotIndicators(int count, LinearLayout layoutDots) {
        layoutDots.removeAllViews();
        for (int i = 0; i < count; i++) {
            ImageView dot = new ImageView(this);
            dot.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.dot_unselected));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(8, 0, 8, 0);
            layoutDots.addView(dot, params);
        }
        updateDotIndicators(0, layoutDots);
    }

    private void updateDotIndicators(int position, LinearLayout layoutDots) {
        for (int i = 0; i < layoutDots.getChildCount(); i++) {
            ImageView dot = (ImageView) layoutDots.getChildAt(i);
            dot.setImageDrawable(ContextCompat.getDrawable(this,
                    i == position ? R.drawable.dot_selected : R.drawable.dot_unselected));
        }
    }

    private void initRecyclerView() {
        recyclerView = findViewById(R.id.recyclerView);
        if (recyclerView == null) {
            Log.e("RecyclerViewError", "RecyclerView is NULL in DashboardActivity!");
            return;
        }

        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        List<ProductDomain> productList = dbHelper.getAllProducts();
        if (productList == null || productList.isEmpty()) {
            Log.w("DB_Products", "No products found in database");
            productList = new ArrayList<>();
            Toast.makeText(this, "No products available", Toast.LENGTH_SHORT).show();
        }

        ProductAdapter adapter = new ProductAdapter(productList, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sliderHandler.removeCallbacks(sliderRunnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sliderHandler.removeCallbacksAndMessages(null);
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
        } else if (itemId == R.id.Orderhistory) {
            startActivity(new Intent(this, OrderhistoryActivity.class));
        } else if (itemId == R.id.logout) {
            SharedPreferences sharedPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
            sharedPreferences.edit().clear().apply();

            SharedPreferences prefs = getSharedPreferences("UserProfile", MODE_PRIVATE);
            prefs.edit().clear().apply();

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