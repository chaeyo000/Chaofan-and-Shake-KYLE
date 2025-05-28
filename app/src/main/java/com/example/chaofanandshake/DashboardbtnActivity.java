package com.example.chaofanandshake;

import android.content.Intent;
import android.content.SharedPreferences;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.example.chaofanandshake.Adapter.BannerAdapter;
import com.example.chaofanandshake.Adapter.ProductAdapter;
import com.example.chaofanandshake.Domain.ProductDomain;
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

        dbHelper = new DatabaseHelper(this);

        // Initialize views
        welcomeTextView = findViewById(R.id.welcomeTextView);
        if (welcomeTextView == null) {
            Log.e("DashboardError", "Welcome TextView not found!");
        }

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

        // Sidebar header references
        View headerView = navigationView.getHeaderView(0);
        navName = headerView.findViewById(R.id.nav_name);
        navUsername = headerView.findViewById(R.id.nav_username);

        // Cart Button
        Button button = findViewById(R.id.cartbtn);
        button.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardbtnActivity.this, CartbtnActivity.class);
            startActivity(intent);
        });

        // Banner ViewPager setup
        bannerViewPager = findViewById(R.id.bannerViewPager);
        bannerImages = Arrays.asList(R.drawable.banner1, R.drawable.banner2);

        BannerAdapter adapter = new BannerAdapter(bannerImages, bannerViewPager);
        bannerViewPager.setAdapter(adapter);
        adapter.setInitialPosition();

        // Dot indicators
        LinearLayout layoutDots = findViewById(R.id.layoutDots);
        setupDotIndicators(bannerImages.size(), layoutDots);

        // Smooth page transitions
        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(40));
        compositePageTransformer.addTransformer((page, position) -> {
            float r = 1 - Math.abs(position);
            page.setScaleY(0.85f + r * 0.15f);
        });
        bannerViewPager.setPageTransformer(compositePageTransformer);

        // Auto-slide runnable
        sliderRunnable = new Runnable() {
            @Override
            public void run() {
                if (bannerImages.size() == 0) return;
                currentPage++;
                bannerViewPager.setCurrentItem(currentPage, true);
                sliderHandler.postDelayed(this, AUTO_SLIDE_DELAY);
            }
        };

        // ViewPager page change callback
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

        // Pause auto-slide on touch
        bannerViewPager.setOnTouchListener((v, event) -> {
            sliderHandler.removeCallbacks(sliderRunnable);
            if (event.getAction() == MotionEvent.ACTION_UP ||
                    event.getAction() == MotionEvent.ACTION_CANCEL) {
                sliderHandler.postDelayed(sliderRunnable, AUTO_SLIDE_DELAY);
            }
            return false;
        });

        // Get passed username (email or id) from intent
        currentUsername = getIntent().getStringExtra("username");
        updateUserInfo();

        // Recycler View for products
        initRecyclerView();

        ActivityResultLauncher<Intent> launcher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.hasExtra("newUsername")) {
                            currentUsername = data.getStringExtra("newUsername");
                            updateUserInfo();
                        }
                    }
                }
        );

        // Click listener for navName to go to AccountActivity
        navName.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardbtnActivity.this, AccountActivity.class);
            intent.putExtra("username", currentUsername);
            launcher.launch(intent);
        });
    }

    private void updateUserInfo() {
        if (currentUsername != null) {
            String userName = dbHelper.getUserName(currentUsername);
            if (userName != null && !userName.isEmpty()) {
                String welcomeMessage = "Welcome " + userName + ", What would you like to <font color='#FFC107'>eat?</font>";
                setWelcomeMessage(welcomeMessage);
                navName.setText(userName);
                navUsername.setText(currentUsername);
            } else {
                navName.setText("User");
                navUsername.setText(currentUsername);
                setWelcomeMessage("Welcome User, What would you like to eat?");
            }
        } else {
            setWelcomeMessage("Welcome, What would you like to eat?");
            navName.setText("User");
            navUsername.setText("");
        }
    }

    private void setupDotIndicators(int count, LinearLayout layoutDots) {
        ImageView[] dots = new ImageView[count];
        layoutDots.removeAllViews();

        for (int i = 0; i < count; i++) {
            dots[i] = new ImageView(this);
            dots[i].setImageDrawable(ContextCompat.getDrawable(this,
                    R.drawable.dot_unselected));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(8, 0, 8, 0);
            layoutDots.addView(dots[i], params);
        }

        updateDotIndicators(0, layoutDots);
    }

    private void updateDotIndicators(int position, LinearLayout layoutDots) {
        int childCount = layoutDots.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ImageView imageView = (ImageView) layoutDots.getChildAt(i);
            if (i == position) {
                imageView.setImageDrawable(ContextCompat.getDrawable(this,
                        R.drawable.dot_selected));
            } else {
                imageView.setImageDrawable(ContextCompat.getDrawable(this,
                        R.drawable.dot_unselected));
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        sliderHandler.postDelayed(sliderRunnable, AUTO_SLIDE_DELAY);
        initRecyclerView();
        updateUserInfo();
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
                // Clear saved login preferences
                SharedPreferences sharedPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
                sharedPreferences.edit().clear().apply();

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