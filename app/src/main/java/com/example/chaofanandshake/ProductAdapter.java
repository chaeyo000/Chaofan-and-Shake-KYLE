package com.example.chaofanandshake;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chaofanandshake.Domain.ProductDomain;
import com.example.chaofanandshake.ProductdetailsActivity;
import com.example.chaofanandshake.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {
    private List<ProductDomain> productList;
    private Context context;

    public ProductAdapter(List<ProductDomain> productList, Context context) {
        this.productList = productList != null ? productList : new ArrayList<>();
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.viewholder_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProductDomain product = productList.get(position);
        holder.title.setText(product.getTitle());
        holder.fee.setText(String.format("₱%.2f", product.getPrice()));

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProductdetailsActivity.class);
            intent.putExtra("product", new Gson().toJson(product)); // Pass product as JSON string
            context.startActivity(intent);
        });

        // Add to Cart Button Listener
        holder.addButton.setOnClickListener(v -> {
            addToCart(product);
        });

        // Handle image loading
        try {
            int resId = context.getResources().getIdentifier(
                    product.getImageName().split("\\.")[0], // Remove file extension
                    "drawable",
                    context.getPackageName()
            );

            if (resId != 0) {
                holder.imageView.setImageResource(resId);
            } else {
                try {
                    FileInputStream fis = context.openFileInput(product.getImageName());
                    Bitmap bitmap = BitmapFactory.decodeStream(fis);
                    holder.imageView.setImageBitmap(bitmap);
                    fis.close();
                } catch (Exception e) {
                    holder.imageView.setImageResource(R.drawable.placeholder_image);
                    Log.e("ImageLoad", "Error loading from storage: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            holder.imageView.setImageResource(R.drawable.placeholder_image);
            Log.e("ImageLoad", "General image load error: " + e.getMessage());
        }
    }

    private void addToCart(ProductDomain product) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyCart", Context.MODE_PRIVATE);
        Gson gson = new Gson();

        List<ProductDomain> cartItems = gson.fromJson(
                sharedPreferences.getString("cart_items", "[]"),
                new TypeToken<List<ProductDomain>>(){}.getType()
        );

        cartItems.add(product);

        sharedPreferences.edit()
                .putString("cart_items", gson.toJson(cartItems))
                .apply();

        Toast.makeText(context, product.getTitle() + " added to cart", Toast.LENGTH_SHORT).show();
    }

    @Override
    public int getItemCount() {
        return productList != null ? productList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, fee, description;
        ImageView imageView;
        Button addButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            try {
                title = itemView.findViewById(R.id.title);
                fee = itemView.findViewById(R.id.fee);
                description = itemView.findViewById(R.id.description);
                imageView = itemView.findViewById(R.id.imageView);

                if (title == null) Log.e("ViewHolder", "title TextView not found");
                if (fee == null) Log.e("ViewHolder", "fee TextView not found");
                if (description == null) Log.e("ViewHolder", "description TextView not found");
                if (imageView == null) Log.e("ViewHolder", "imageView not found");
                if (addButton == null) Log.e("ViewHolder", "addButton not found");
            } catch (Exception e) {
                Log.e("ViewHolder", "Error initializing views", e);
            }
        }
    }
}
