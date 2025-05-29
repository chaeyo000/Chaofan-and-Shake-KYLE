package com.example.chaofanandshake;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chaofanandshake.Adapter.AdminProductAdapter;
import com.example.chaofanandshake.Domain.ProductDomain;

import java.util.List;

public class ActivityProducts extends AppCompatActivity {

    ImageView backBtn;
    private RecyclerView recyclerView;
    private AdminProductAdapter adminProductAdapter;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products); // Make sure you have this layout file

        backBtn = findViewById(R.id.backBtn);

        recyclerView = findViewById(R.id.productsRecyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));

        dbHelper = new DatabaseHelper(this);
        loadProducts();

        // Back button
        backBtn.setOnClickListener(v -> finish());
    }

    private void loadProducts() {
        List<ProductDomain> productList = dbHelper.getAllProducts();
        adminProductAdapter = new AdminProductAdapter(productList, this, position -> {
            ProductDomain productToDelete = productList.get(position);
            boolean deleted = dbHelper.deleteProduct(productToDelete.getTitle());

            if (deleted) {
                productList.remove(position);
                adminProductAdapter.notifyItemRemoved(position);
                Toast.makeText(this, "Product deleted", Toast.LENGTH_SHORT).show();
                deleteFile(productToDelete.getImageName());
            } else {
                Toast.makeText(this, "Failed to delete product", Toast.LENGTH_SHORT).show();
            }
        });

        recyclerView.setAdapter(adminProductAdapter);
    }
}
