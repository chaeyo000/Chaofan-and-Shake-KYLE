package com.example.chaofanandshake;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.chaofanandshake.Domain.ProductDomain;

import java.util.ArrayList;

public class CheckoutCartAdapter extends RecyclerView.Adapter<CheckoutCartAdapter.ViewHolder> {
    private ArrayList<ProductDomain> cartItems;

    public CheckoutCartAdapter(ArrayList<ProductDomain> cartItems) {
        this.cartItems = cartItems;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, quantity, price;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.itemName);
            quantity = itemView.findViewById(R.id.itemQty);
            price = itemView.findViewById(R.id.itemPrice);
        }
    }

    @Override
    public CheckoutCartAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.checkout_cart, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CheckoutCartAdapter.ViewHolder holder, int position) {
        ProductDomain product = cartItems.get(position);
        holder.title.setText(product.getTitle());
        holder.quantity.setText("Quantity: " + product.getQuantity());
        double productTotal = product.getPrice() * product.getQuantity();
        holder.price.setText("Price: ₱" + String.format("%.2f", productTotal));
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }
}
