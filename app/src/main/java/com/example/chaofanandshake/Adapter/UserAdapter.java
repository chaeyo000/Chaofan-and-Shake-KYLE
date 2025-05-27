package com.example.chaofanandshake.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chaofanandshake.DatabaseHelper;
import com.example.chaofanandshake.Domain.User;
import com.example.chaofanandshake.R;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private List<User> userList;
    private final Context context;
    private final DatabaseHelper dbHelper;

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        final TextView usernameText;
        final TextView phoneText;
        final Button deleteButton;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameText = itemView.findViewById(R.id.usernameText);
            phoneText = itemView.findViewById(R.id.phoneText);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }

    public UserAdapter(Context context, List<User> users, DatabaseHelper dbHelper) {
        this.context = context;
        this.userList = users;
        this.dbHelper = dbHelper;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_item, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.usernameText.setText(user.getUsername());
        holder.phoneText.setText(user.getPhone());

        holder.deleteButton.setOnClickListener(v -> {
            boolean deleted = dbHelper.deleteUser(user.getUsername());
            if (deleted) {
                userList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, userList.size());
                Toast.makeText(context, "User deleted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Failed to delete user", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList != null ? userList.size() : 0;
    }

    public void updateList(List<User> newUsers) {
        if (newUsers != null) {
            this.userList = newUsers;
            notifyDataSetChanged();
        }
    }
}