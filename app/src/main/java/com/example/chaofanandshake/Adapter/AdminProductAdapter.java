package com.example.chaofanandshake.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.example.chaofanandshake.R;
import com.google.android.material.button.MaterialButton;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

public class AdminProductAdapter extends RecyclerView.Adapter<AdminProductAdapter.ViewHolder> {
    private List<ProductDomain> productList;
    private Context context;
    private OnProductActionListener listener;

    private OnDeleteClickListener onDeleteClickListener;

    public interface OnDeleteClickListener {
        void onDeleteClick(int position);
    }

    public AdminProductAdapter(List<ProductDomain> productList, Context context, OnProductActionListener listener) {
        this.productList = productList;
        this.context = context;
        this.listener = listener;
    }


    public interface OnProductActionListener {
        void onProductDelete(int position);
        void onEditClick(int position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.viewholder_admin_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProductDomain product = productList.get(position);
        holder.title.setText(product.getTitle());
        holder.fee.setText(String.format("₱%.2f", product.getPrice()));
        holder.description.setText(product.getDescription());

        // Handle image loading (same as your original adapter)
        try {
            int resId = context.getResources().getIdentifier(
                    product.getImageName().split("\\.")[0],
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
                }
            }
        } catch (Exception e) {
            holder.imageView.setImageResource(R.drawable.placeholder_image);
        }

        holder.deleteButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onProductDelete(position);
            }
        });
        holder.editButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList != null ? productList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        MaterialButton editButton;

        TextView title, fee, description;
        ImageView imageView;
        MaterialButton deleteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            fee = itemView.findViewById(R.id.fee);
            description = itemView.findViewById(R.id.description);
            imageView = itemView.findViewById(R.id.imageView);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            editButton = itemView.findViewById(R.id.editButton);

        }
    }
}