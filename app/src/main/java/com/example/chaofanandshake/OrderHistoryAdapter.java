package com.example.chaofanandshake;

import android.content.Context;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.example.chaofanandshake.NotificationHelper;



import com.example.chaofanandshake.Domain.Order;
import com.google.android.material.button.MaterialButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.OrderViewHolder> {

    private List<Order> orderList;
    private long latestOrderTimestamp;
    private DatabaseHelper dbHelper;

    public OrderHistoryAdapter(List<Order> orderList, DatabaseHelper dbHelper) {
        this.orderList = orderList;
        this.dbHelper = dbHelper;


        for (Order order : orderList) {
            if (order.getOrderPlacedTimestamp() > latestOrderTimestamp) {
                latestOrderTimestamp = order.getOrderPlacedTimestamp();
            }
        }

    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_history, parent, false);
        return new OrderViewHolder(view);
    }

    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);
        Context context = holder.itemView.getContext();
        NotificationHelper notificationHelper = new NotificationHelper(context);

        holder.tvOrderDate.setText("Order Date: " + order.getDate());
        holder.tvOrderItems.setText("Items: " + order.getOrderSummary());
        holder.tvTotalAmount.setText("Total: ₱" + String.format("%.2f", order.getTotalPrice()));
        holder.tvPaymentMethod.setText("Payment: " + order.getPaymentMethod());
        holder.tvCustomerName.setText("Name: " + order.getCustomerName());
        holder.tvUsername.setText("Username: " + order.getUsername());
        holder.tvPhone.setText("Phone: " + order.getPhoneNumber());

        String orderStatus = order.getStatus();

        holder.btnCompleteOrder.setOnClickListener(v -> {
            holder.tvOrderStatus.setText("Order Picked Up");
            holder.btnCompleteOrder.setVisibility(View.GONE);
            order.setStatus("Order Pickup Up");

            if (dbHelper != null) {
                boolean success = dbHelper.updateOrderStatus(order.getId(), "Completed");
                if (!success) {
                    Toast.makeText(v.getContext(), "Failed to update status", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(v.getContext(), "Order Completed", Toast.LENGTH_SHORT).show();
            }
        });


        if ("Completed".equals(orderStatus)) {
            holder.tvOrderStatus.setText("Order Picked Up");
            holder.btnCompleteOrder.setVisibility(View.GONE);
            if (holder.countDownTimer != null) {
                holder.countDownTimer.cancel();
                holder.countDownTimer = null;
            }
        } else if ("Ready for pickup".equals(orderStatus)) {
            holder.tvOrderStatus.setText("Ready for pickup");
            holder.btnCompleteOrder.setVisibility(View.VISIBLE);
            if (holder.countDownTimer != null) {
                holder.countDownTimer.cancel();
                holder.countDownTimer = null;
            }
        } else {
            // Status is neither Completed nor Ready for pickup, so show countdown

            long placedTime = order.getOrderPlacedTimestamp();
            long now = System.currentTimeMillis();
            long elapsed = now - placedTime;
            long thirtySecs = 30 * 1000;

            if (elapsed < thirtySecs) {
                long timeLeft = thirtySecs - elapsed;
                holder.tvOrderStatus.setText("");
                holder.btnCompleteOrder.setVisibility(View.GONE);

                holder.countDownTimer = new CountDownTimer(timeLeft, 1000) {
                    public void onTick(long millisUntilFinished) {
                        long minutes = (millisUntilFinished / 1000) / 60;
                        long seconds = (millisUntilFinished / 1000) % 60;
                        holder.tvOrderStatus.setText(String.format("Ready for pickup in %d:%02d", minutes, seconds));
                    }

                    public void onFinish() {
                        holder.tvOrderStatus.setText("Ready for pickup");
                        holder.btnCompleteOrder.setVisibility(View.VISIBLE);

                        // Update status in database
                        if (dbHelper != null) {
                            dbHelper.updateOrderStatus(order.getId(), "Ready for pickup");
                        }

                        // Send notification
                        notificationHelper.sendNotification(
                                "Order Ready",
                                "Your order is ready for pickup!"
                        );
                    }
                }.start();

            } else {
                holder.tvOrderStatus.setText("Ready for pickup");
                holder.btnCompleteOrder.setVisibility(View.VISIBLE);
            }
        }

    }


    @Override
    public int getItemCount() {
        return orderList.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderDate, tvOrderItems, tvTotalAmount, tvPaymentMethod;
        TextView tvCustomerName, tvPhone, tvUsername;
        TextView tvOrderStatus;
        Button btnCompleteOrder;


        android.os.CountDownTimer countDownTimer;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);

            tvOrderDate = itemView.findViewById(R.id.tvOrderDate);
            btnCompleteOrder = itemView.findViewById(R.id.btnCompleteOrder);
            tvOrderItems = itemView.findViewById(R.id.tvOrderItems);
            tvTotalAmount = itemView.findViewById(R.id.tvTotalAmount);
            tvPaymentMethod = itemView.findViewById(R.id.tvPaymentMethod);
            tvCustomerName = itemView.findViewById(R.id.tvCustomerName);
            tvPhone = itemView.findViewById(R.id.tvPhone);
            tvOrderStatus = itemView.findViewById(R.id.tvOrderStatus);
            tvUsername = itemView.findViewById(R.id.tvUsername);
        }
    }
}

