package com.example.chaofanandshake.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chaofanandshake.Domain.Order;
import com.example.chaofanandshake.R;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    private Context context;
    private List<Order> orderList;

    public OrderAdapter(Context context, List<Order> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.order_item_layout, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);

        // Set all fields with null checks
        holder.name.setText(order.getCustomerName() != null ? order.getCustomerName() : "");
        holder.username.setText(order.getUsername() != null ? order.getUsername() : "");
        holder.orderSummary.setText("Username: \n" + (order.getOrderSummary() != null ? order.getOrderSummary() : ""));
        holder.phone.setText(order.getPhoneNumber() != null ? order.getPhoneNumber() : "");
        holder.paymentMethod.setText(order.getPaymentMethod() != null ? order.getPaymentMethod() : "");
        holder.total.setText("₱" + String.format("%.2f", order.getTotalPrice()));

        // Debug log
        Log.d("OrderAdapter", "Binding - Position: " + position +
                " Phone: " + order.getPhoneNumber());
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView username, orderSummary, phone, paymentMethod, total, name;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            username = itemView.findViewById(R.id.username);
            orderSummary = itemView.findViewById(R.id.orderSummary);
            phone = itemView.findViewById(R.id.phone);
            paymentMethod = itemView.findViewById(R.id.paymentMethod);
            total = itemView.findViewById(R.id.total);

            // Initialize with empty strings
            name.setText("");
            username.setText("");
            orderSummary.setText("");
            phone.setText("");
            paymentMethod.setText("");
            total.setText("");
        }
    }
}
