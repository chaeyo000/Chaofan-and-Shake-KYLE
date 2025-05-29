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

    import com.example.chaofanandshake.Adapter.AdminProductAdapter;
    import com.example.chaofanandshake.Adapter.OrderAdapter;
    import com.example.chaofanandshake.Domain.Order;
    import com.example.chaofanandshake.Domain.ProductDomain;
    import com.google.android.material.navigation.NavigationView;

    import java.io.FileOutputStream;
    import java.io.IOException;
    import java.io.InputStream;
    import java.util.List;
    import com.example.chaofanandshake.Adapter.ProductAdapter;
    import androidx.recyclerview.widget.GridLayoutManager;

    public class AdminDashboardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

        private DrawerLayout drawerLayout;
        private NavigationView navigationView;
        private AdminProductAdapter adminProductAdapter;

        private Toolbar toolbar;
        private RecyclerView ordersRecyclerView;
        private OrderAdapter orderAdapter;
        private RecyclerView productsRecyclerView;
        private ProductAdapter productAdapter;

        private DatabaseHelper dbHelper;

        private Uri selectedImageUri;
        private ImageView productImageView;

        private AlertDialog addProductDialog;

        private static final int PERMISSION_REQUEST_CODE = 200;
        private static final int IMAGE_PICK_CODE = 100;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_admin_dashboard);

            Button addProductBtn = findViewById(R.id.addProductButton);
            addProductBtn.setOnClickListener(v -> showAddProductDialog());

            toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            drawerLayout = findViewById(R.id.drawer_layout);
            navigationView = findViewById(R.id.sidebar_admin);
            navigationView.setNavigationItemSelectedListener(this);

            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawerLayout, toolbar, R.string.open, R.string.close);
            drawerLayout.addDrawerListener(toggle);
            toggle.syncState();

            ordersRecyclerView = findViewById(R.id.Orders);
            ordersRecyclerView.setLayoutManager(new LinearLayoutManager(this));

            dbHelper = new DatabaseHelper(this);

            List<Order> orderList = dbHelper.getAllOrders();
            orderAdapter = new OrderAdapter(this, orderList);
            ordersRecyclerView.setAdapter(orderAdapter);
            productsRecyclerView = findViewById(R.id.productsRecyclerView);
            productsRecyclerView.setLayoutManager(new GridLayoutManager(this, 1));
            loadProducts();

        }

        private void loadProducts() {
            List<ProductDomain> productList = dbHelper.getAllProducts();
            adminProductAdapter = new AdminProductAdapter(productList, this, position -> {
                // Handle delete button click
                ProductDomain productToDelete = productList.get(position);
                boolean deleted = dbHelper.deleteProduct(productToDelete.getTitle());

                if (deleted) {
                    // Remove from list and notify adapter
                    productList.remove(position);
                    adminProductAdapter.notifyItemRemoved(position);
                    Toast.makeText(this, "Product deleted", Toast.LENGTH_SHORT).show();

                    // Optionally delete the image file
                    AdminDashboardActivity.this.deleteFile(productToDelete.getImageName());
                } else {
                    Toast.makeText(this, "Failed to delete product", Toast.LENGTH_SHORT).show();
                }
            });

            productsRecyclerView.setAdapter(adminProductAdapter);
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

                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {

                    new AlertDialog.Builder(this)
                            .setTitle("Permission Needed")
                            .setMessage("This permission is needed to select images")
                            .setPositiveButton("OK", (dialog, which) -> {
                                ActivityCompat.requestPermissions(this,
                                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                        PERMISSION_REQUEST_CODE);
                            })
                            .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                            .create().show();
                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            PERMISSION_REQUEST_CODE);
                }
            } else {
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
                    launchImagePicker();
                } else {
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

            productImageView.setImageResource(R.drawable.placeholder_image);

            View.OnClickListener imagePickerListener = v -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
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

                String imageName = "product_" + System.currentTimeMillis();
                if (selectedImageUri != null) {
                    imageName = saveImageToInternalStorage(selectedImageUri);
                    if (imageName == null) {
                        Toast.makeText(this, "Failed to save image", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

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

                loadProducts();
                // Refresh order list or any other UI you want here if needed
            });

            addProductDialog = builder.create();
            addProductDialog.show();
        }

        private String saveImageToInternalStorage(Uri imageUri) {
            try {
                String mimeType = getContentResolver().getType(imageUri);
                if (mimeType == null || !mimeType.contains("/")) {
                    return null;
                }
                String[] parts = mimeType.split("/");
                if (parts.length < 2) {
                    return null;
                }
                String fileExtension = parts[1];
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
            } else if (id == R.id.order) {
                Intent intent = new Intent(this, ActivityOrder.class);
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
