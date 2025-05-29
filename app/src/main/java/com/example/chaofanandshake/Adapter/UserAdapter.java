package com.example.chaofanandshake.Adapter;

import android.content.Context;
import android.content.Intent;
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

    public static final int EDIT_USER_REQUEST_CODE = 1001;

    public void update(List<User> users) {
        this.userList = users;
        notifyDataSetChanged();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        final TextView usernameText;
        final TextView phoneText;
        final TextView nameText;
        final Button deleteButton;
        final Button editButton;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameText = itemView.findViewById(R.id.usernameText);
            nameText = itemView.findViewById(R.id.nameText);
            phoneText = itemView.findViewById(R.id.phoneText);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            editButton = itemView.findViewById(R.id.editButton);
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
        holder.nameText.setText(user.getName());
        holder.phoneText.setText(user.getPhone());

        // DELETE button logic (fixed)
        holder.deleteButton.setOnClickListener(v -> {
            int currentPosition = holder.getAdapterPosition();
            if (currentPosition != RecyclerView.NO_POSITION) {
                User currentUser = userList.get(currentPosition);
                boolean deleted = dbHelper.deleteUserAndOrders(currentUser.getUsername());

                if (deleted) {
                    userList.remove(currentPosition);
                    notifyItemRemoved(currentPosition);
                    notifyItemRangeChanged(currentPosition, userList.size());
                    Toast.makeText(context, "User deleted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Failed to delete user", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, "Invalid position", Toast.LENGTH_SHORT).show();
            }
        });

        // EDIT button logic
        holder.editButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditUserActivity.class);
            intent.putExtra("userId", user.getId());
            intent.putExtra("name", user.getName());
            intent.putExtra("username", user.getUsername());
            intent.putExtra("phone", user.getPhone());

            if (context instanceof android.app.Activity) {
                ((android.app.Activity) context).startActivityForResult(intent, EDIT_USER_REQUEST_CODE);
            } else {
                context.startActivity(intent);
                Toast.makeText(context, "Cannot get result because context is not an Activity", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList != null ? userList.size() : 0;
    }

    // Tawagin mo 'to sa ActivityUsers after editing a user
    public void setUserList(List<User> newUsers) {
        this.userList = newUsers;
        notifyDataSetChanged();
    }
}
