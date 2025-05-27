package com.example.chaofanandshake.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.chaofanandshake.Domain.ProductDomain;
import com.example.chaofanandshake.R;

import java.io.FileInputStream;
import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    private ArrayList<ProductDomain> cartItems;
    private Runnable saveCartCallback;  // callback to save cart externally
    private OnQuantityChangeListener quantityChangeListener;

    public CartAdapter(ArrayList<ProductDomain> cartItems) {
        this.cartItems = cartItems;
    }

    public void setSaveCartCallback(Runnable callback) {
        this.saveCartCallback = callback;
    }

    public void setOnQuantityChangeListener(OnQuantityChangeListener listener) {
        this.quantityChangeListener = listener;
    }

    public interface OnQuantityChangeListener {
        void onQuantityChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, price, quantityText;
        ImageButton removeItem, increaseQuantity, decreaseQuantity;

        ImageView productImage;

        public ViewHolder(View view) {
            super(view);
            productImage = view.findViewById(R.id.cartProductImage);
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

        try {
            Context context = holder.productImage.getContext();
            int resId = context.getResources().getIdentifier(
                    item.getImageName().split("\\.")[0],
                    "drawable",
                    context.getPackageName()
            );

            if (resId != 0) {
                holder.productImage.setImageResource(resId);
            } else {
                FileInputStream fis = context.openFileInput(item.getImageName());
                Bitmap bitmap = BitmapFactory.decodeStream(fis);
                holder.productImage.setImageBitmap(bitmap);
                fis.close();
            }
        } catch (Exception e) {
            holder.productImage.setImageResource(R.drawable.placeholder_image);
        }

        // This part MUST be outside the try-catch!
        double totalPrice = item.getPrice() * item.getQuantity();
        holder.price.setText(String.format("₱%.2f", totalPrice));
        holder.quantityText.setText(String.valueOf(item.getQuantity()));

        holder.increaseQuantity.setOnClickListener(v -> {
            item.setQuantity(item.getQuantity() + 1);
            notifyItemChanged(position);
            if (saveCartCallback != null) saveCartCallback.run();
            if (quantityChangeListener != null) quantityChangeListener.onQuantityChanged();
        });

        holder.decreaseQuantity.setOnClickListener(v -> {
            if (item.getQuantity() > 1) {
                item.setQuantity(item.getQuantity() - 1);
                notifyItemChanged(position);
                if (saveCartCallback != null) saveCartCallback.run();
                if (quantityChangeListener != null) quantityChangeListener.onQuantityChanged();
            }
        });

        holder.removeItem.setOnClickListener(v -> {
            cartItems.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, cartItems.size());
            if (saveCartCallback != null) saveCartCallback.run();
            if (quantityChangeListener != null) quantityChangeListener.onQuantityChanged();
        });
    }


        @Override
        public int getItemCount () {
            return cartItems.size();
        }
    }
