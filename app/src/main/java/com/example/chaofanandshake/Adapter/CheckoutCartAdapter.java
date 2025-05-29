package com.example.chaofanandshake.Adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.chaofanandshake.Domain.ProductDomain;
import com.example.chaofanandshake.R;

import java.io.FileInputStream;
import java.util.ArrayList;

public class CheckoutCartAdapter extends RecyclerView.Adapter<CheckoutCartAdapter.ViewHolder> {
    private ArrayList<ProductDomain> cartItems;

    public CheckoutCartAdapter(ArrayList<ProductDomain> cartItems) {
        this.cartItems = cartItems;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, quantity, price;
        ImageView productImage;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.itemTitle);
            quantity = itemView.findViewById(R.id.itemQuantity);
            price = itemView.findViewById(R.id.itemPrice);
            productImage = itemView.findViewById(R.id.itemImage);

            if (title == null) Log.e("ViewHolder", "title TextView is null!");
            if (quantity == null) Log.e("ViewHolder", "quantity TextView is null!");
            if (price == null) Log.e("ViewHolder", "price TextView is null!");
            if (productImage == null) Log.e("ViewHolder", "productImage ImageView is null!");
        }

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.checkout_cart, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ProductDomain product = cartItems.get(position);
        holder.title.setText(product.getTitle());
        holder.quantity.setText("Qty: " + product.getQuantity());
        double productTotal = product.getPrice() * product.getQuantity();
        holder.price.setText("₱" + String.format("%.2f", productTotal));

        try {
            // Assuming product.getImageName() returns the filename, e.g. "chaofan_special.jpg"
            String imageName = product.getImageName();

            // Open file from internal storage
            FileInputStream fis = holder.productImage.getContext().openFileInput(imageName);
            Bitmap bitmap = BitmapFactory.decodeStream(fis);
            holder.productImage.setImageBitmap(bitmap);
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
            // fallback to placeholder image
            holder.productImage.setImageResource(R.drawable.placeholder_image);
        }
    }




    @Override
    public int getItemCount() {
        return cartItems.size();
    }
}
