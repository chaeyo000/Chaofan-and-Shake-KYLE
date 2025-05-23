package com.example.chaofanandshake.Adapter;

import android.content.Context;
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
        holder.orderSummary.setText(order.getOrderSummary());
        holder.phone.setText(order.getPhone());
        holder.paymentMethod.setText(order.getPaymentMethod());
        holder.total.setText("₱" + String.format("%.2f", order.getTotal()));
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView orderSummary, phone, paymentMethod, total;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderSummary = itemView.findViewById(R.id.orderSummary);
            phone = itemView.findViewById(R.id.phone);
            paymentMethod = itemView.findViewById(R.id.paymentMethod);
            total = itemView.findViewById(R.id.total);
        }
    }
}
