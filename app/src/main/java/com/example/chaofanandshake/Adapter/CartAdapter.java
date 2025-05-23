package com.example.chaofanandshake.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.chaofanandshake.Domain.ProductDomain;
import com.example.chaofanandshake.R;

import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    private ArrayList<ProductDomain> cartItems;
    private Runnable saveCartCallback;  // callback to save cart externally

    public CartAdapter(ArrayList<ProductDomain> cartItems) {
        this.cartItems = cartItems;
    }

    public void setSaveCartCallback(Runnable callback) {
        this.saveCartCallback = callback;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, price, quantityText;
        ImageButton removeItem, increaseQuantity, decreaseQuantity;

        public ViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.title);
            price = view.findViewById(R.id.cartPrice);
            quantityText = view.findViewById(R.id.quantityText);
            removeItem = view.findViewById(R.id.removeItem);
            increaseQuantity = view.findViewById(R.id.increaseQuantity);
            decreaseQuantity = view.findViewById(R.id.decreaseQuantity);
        }
    }

    @Override
    public CartAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cart, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CartAdapter.ViewHolder holder, int position) {
        ProductDomain item = cartItems.get(position);
        holder.title.setText(item.getTitle());

        // Price multiplied by quantity
        holder.price.setText("₱" + (item.getPrice() * item.getQuantity()));
        holder.quantityText.setText(String.valueOf(item.getQuantity()));

        // Increase quantity button
        holder.increaseQuantity.setOnClickListener(v -> {
            item.setQuantity(item.getQuantity() + 1);
            notifyItemChanged(position);
            if (saveCartCallback != null) saveCartCallback.run();
        });

        // Decrease quantity button (minimum 1)
        holder.decreaseQuantity.setOnClickListener(v -> {
            if (item.getQuantity() > 1) {
                item.setQuantity(item.getQuantity() - 1);
                notifyItemChanged(position);
                if (saveCartCallback != null) saveCartCallback.run();
            }
        });

        // Remove item button
        holder.removeItem.setOnClickListener(v -> {
            cartItems.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, cartItems.size());
            if (saveCartCallback != null) saveCartCallback.run();
        });

        // Increase quantity button
        holder.increaseQuantity.setOnClickListener(v -> {
            item.setQuantity(item.getQuantity() + 1);
            notifyItemChanged(position);
            if (saveCartCallback != null) saveCartCallback.run();
            if (quantityChangeListener != null) quantityChangeListener.onQuantityChanged();
        });

// Decrease quantity button
        holder.decreaseQuantity.setOnClickListener(v -> {
            if (item.getQuantity() > 1) {
                item.setQuantity(item.getQuantity() - 1);
                notifyItemChanged(position);
                if (saveCartCallback != null) saveCartCallback.run();
                if (quantityChangeListener != null) quantityChangeListener.onQuantityChanged();
            }
        });

// Remove item button
        holder.removeItem.setOnClickListener(v -> {
            cartItems.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, cartItems.size());
            if (saveCartCallback != null) saveCartCallback.run();
            if (quantityChangeListener != null) quantityChangeListener.onQuantityChanged();
        });

    }

    public interface OnQuantityChangeListener {
        void onQuantityChanged();
    }

    private OnQuantityChangeListener quantityChangeListener;

    public void setOnQuantityChangeListener(OnQuantityChangeListener listener) {
        this.quantityChangeListener = listener;
    }


    @Override
    public int getItemCount() {
        return cartItems.size();
    }
}
