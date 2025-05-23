package com.example.chaofanandshake.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {
    private List<ProductDomain> productList;
    private Context context;

    public ProductAdapter(List<ProductDomain> productList, Context context) {
        this.productList = productList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProductDomain product = productList.get(position);
        holder.title.setText(product.getTitle());
        holder.fee.setText("₱" + product.getPrice());

        int imageResId = holder.itemView.getContext().getResources().getIdentifier(
                product.getImageName(), "drawable", holder.itemView.getContext().getPackageName()
        );

        if (imageResId != 0) {
            holder.imageView.setImageResource(imageResId);
        } else {
            holder.imageView.setImageResource(R.drawable.swirls);
        }

        // Clicking product opens details
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProductdetailsActivity.class);
            intent.putExtra("product", product);
            context.startActivity(intent);
        });

        // Add to cart button click
        holder.addButton.setOnClickListener(v -> {
            // Save product info to SharedPreferences
            SharedPreferences sharedPreferences = context.getSharedPreferences("MyCart", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("product_name", product.getTitle());
            editor.putFloat("product_price", (float) product.getPrice());
            editor.putString("product_image", product.getImageName());
            editor.apply();

            Toast.makeText(context, product.getTitle() + " added to cart", Toast.LENGTH_SHORT).show();
        });
    }



    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, fee;
        ImageView imageView;
        Button addButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            fee = itemView.findViewById(R.id.fee);
            imageView = itemView.findViewById(R.id.imageView);
            addButton = itemView.findViewById(R.id.addbtn);
        }
    }
}
