package com.example.chaofanandshake;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chaofanandshake.Domain.Order;

import java.util.List;

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.OrderViewHolder> {

    private List<Order> orderList;

    public OrderHistoryAdapter(List<Order> orderList) {
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_history, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);

        holder.tvOrderDate.setText("Order Date: " + order.getDate());
        holder.tvOrderItems.setText("Items: " + order.getOrderSummary());
        holder.tvTotalAmount.setText("Total: ₱" + String.format("%.2f", order.getTotalPrice()));
        holder.tvPaymentMethod.setText("Payment: " + order.getPaymentMethod());

        // New customer info
        holder.tvCustomerName.setText("Name: " + order.getCustomerName());
        holder.tvPhone.setText("Phone: " + order.getPhoneNumber());
        holder.tvUsername.setText("Username: " + order.getUsername());
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderDate, tvOrderItems, tvTotalAmount, tvPaymentMethod;
        TextView tvCustomerName, tvPhone, tvUsername;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderDate = itemView.findViewById(R.id.tvOrderDate);
            tvOrderItems = itemView.findViewById(R.id.tvOrderItems);
            tvTotalAmount = itemView.findViewById(R.id.tvTotalAmount);
            tvPaymentMethod = itemView.findViewById(R.id.tvPaymentMethod);

            // New customer info
            tvCustomerName = itemView.findViewById(R.id.tvCustomerName);
            tvPhone = itemView.findViewById(R.id.tvPhone);
            tvUsername = itemView.findViewById(R.id.tvUsername);
        }
    }
}
