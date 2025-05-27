package com.example.chaofanandshake;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chaofanandshake.Adapter.EditUserActivity;
import com.example.chaofanandshake.Adapter.OrderAdapter;
import com.example.chaofanandshake.Adapter.UserAdapter;
import com.example.chaofanandshake.Domain.Order;
import com.example.chaofanandshake.Domain.ProductDomain;
import com.example.chaofanandshake.Domain.User;
import com.google.android.material.navigation.NavigationView;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class AdminDashboardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    // UI Components
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private RecyclerView usersRecyclerView;  // Renamed from productRecyclerView
    private RecyclerView ordersRecyclerView;

    // Adapters
    private UserAdapter userAdapter;
    private OrderAdapter orderAdapter;

    // Database
    private DatabaseHelper dbHelper;

    // Image Handling
    private Uri selectedImageUri;
    private ImageView productImageView;

    // Dialogs
    private AlertDialog addProductDialog;

    // Request Codes
    private static final int PERMISSION_REQUEST_CODE = 200;
    private static final int IMAGE_PICK_CODE = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);




        // Initialize views
        Button addProductBtn = findViewById(R.id.addProductButton);
        addProductBtn.setOnClickListener(v -> showAddProductDialog());

        // Setup Toolbar and Drawer
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.sidebar_admin);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Initialize RecyclerViews
        usersRecyclerView = findViewById(R.id.productRecyclerView);
        usersRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        ordersRecyclerView = findViewById(R.id.Orders);
        ordersRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // DatabaseHelper and adapters
        dbHelper = new DatabaseHelper(this);

        List<User> userList = dbHelper.getAllUsers();
        userAdapter = new UserAdapter(this, userList, dbHelper);
        usersRecyclerView.setAdapter(userAdapter);

        List<Order> orderList = dbHelper.getAllOrders();
        orderAdapter = new OrderAdapter(this, orderList);
        ordersRecyclerView.setAdapter(orderAdapter);


    }

    private boolean checkStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST_CODE);
                return false;
            }
        }
        return true;
    }

    private void openImagePicker() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // Show an explanation to the user
                new AlertDialog.Builder(this)
                        .setTitle("Permission Needed")
                        .setMessage("This permission is needed to select images")
                        .setPositiveButton("OK", (dialog, which) -> {
                            // Request the permission
                            ActivityCompat.requestPermissions(this,
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                    PERMISSION_REQUEST_CODE);
                        })
                        .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                        .create().show();
            } else {
                // No explanation needed, request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST_CODE);
            }
        } else {
            // Permission already granted
            launchImagePicker();
        }
    }

    private void launchImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted
                launchImagePicker();
            } else {
                // Permission denied
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_PICK_CODE && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            if (productImageView != null) {
                productImageView.setImageURI(selectedImageUri);
            }
        }
    }

    private void refreshUserList() {
        List<User> users = dbHelper.getAllUsers();
        if (users != null) {
            userAdapter.updateList(users);
        } else {
            Toast.makeText(this, "Failed to load users", Toast.LENGTH_SHORT).show();
        }
    }

    private void showAddProductDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_product, null);
        builder.setView(dialogView);

        productImageView = dialogView.findViewById(R.id.productImage);
        Button selectImageBtn = dialogView.findViewById(R.id.selectImageButton);
        EditText titleEditText = dialogView.findViewById(R.id.productTitle);
        EditText descriptionEditText = dialogView.findViewById(R.id.productDescription);
        EditText priceEditText = dialogView.findViewById(R.id.productPrice);
        Button saveButton = dialogView.findViewById(R.id.saveProductButton);

        // Set default image
        productImageView.setImageResource(R.drawable.placeholder_image);

        // Open gallery when image is clicked
        View.OnClickListener imagePickerListener = v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // For Android 13+, we don't need storage permission for image picker
                launchImagePicker();
            } else {
                openImagePicker();
            }
        };
        productImageView.setOnClickListener(imagePickerListener);
        selectImageBtn.setOnClickListener(imagePickerListener);

        saveButton.setOnClickListener(v -> {
            String title = titleEditText.getText().toString().trim();
            String description = descriptionEditText.getText().toString().trim();
            String priceStr = priceEditText.getText().toString().trim();

            // Validate inputs
            if (title.isEmpty()) {
                titleEditText.setError("Title is required");
                return;
            }

            if (priceStr.isEmpty()) {
                priceEditText.setError("Price is required");
                return;
            }

            double price;
            try {
                price = Double.parseDouble(priceStr);
                if (price <= 0) {
                    priceEditText.setError("Price must be greater than 0");
                    return;
                }
            } catch (NumberFormatException e) {
                priceEditText.setError("Invalid price format");
                return;
            }

            // Handle image
            String imageName = "product_" + System.currentTimeMillis();
            if (selectedImageUri != null) {
                imageName = saveImageToInternalStorage(selectedImageUri);
                if (imageName == null) {
                    Toast.makeText(this, "Failed to save image", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            // Create and save new product
            ProductDomain newProduct = new ProductDomain(
                    imageName,
                    title,
                    description,
                    price
            );

            dbHelper.insertProduct(
                    newProduct.getImageName(),
                    newProduct.getTitle(),
                    newProduct.getDescription(),
                    newProduct.getPrice()
            );

            Toast.makeText(this, "Product added successfully", Toast.LENGTH_SHORT).show();
            addProductDialog.dismiss();
            refreshUserList();
        });

        addProductDialog = builder.create();
        addProductDialog.show();
    }

    private String saveImageToInternalStorage(Uri imageUri) {
        try {
            // Get the file extension
            String fileExtension = getContentResolver().getType(imageUri).split("/")[1];

            // Create unique filename with extension
            String fileName = "product_" + System.currentTimeMillis() + "." + fileExtension;

            try (InputStream inputStream = getContentResolver().openInputStream(imageUri);
                 FileOutputStream outputStream = openFileOutput(fileName, Context.MODE_PRIVATE)) {

                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }
            }
            return fileName;
        } catch (IOException e) {
            Log.e("ImageSave", "Error saving image", e);
            return null;
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.dashboard) {
            Intent intent = new Intent(this, AdminDashboardActivity.class);
            startActivity(intent);
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        } else if (id == R.id.user) {
            Intent intent = new Intent(this, ActivityUsers.class);
            startActivity(intent);
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        } else if (id == R.id.logout) {
            Toast.makeText(this, "You have been Logged Out", Toast.LENGTH_SHORT).show();
            drawerLayout.closeDrawer(GravityCompat.START);

            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();

            return true;
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

}