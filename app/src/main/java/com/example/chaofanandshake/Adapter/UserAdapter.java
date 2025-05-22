package com.example.chaofanandshake.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.chaofanandshake.DatabaseHelper;
import com.example.chaofanandshake.Domain.User;
import com.example.chaofanandshake.R;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private List<User> userList;
    private Context context;
    private DatabaseHelper dbHelper;




    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView usernameText, phoneText;
        Button deleteButton;

        public UserViewHolder(View itemView) {
            super(itemView);
            usernameText = itemView.findViewById(R.id.usernameText);
            phoneText = itemView.findViewById(R.id.phoneText);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }

    }

    public UserAdapter(Context context, List<User> users, DatabaseHelper dbHelper) {
        this.userList = users;
        this.context = context;
        this.dbHelper = dbHelper;
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_item, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.usernameText.setText(user.getUsername());
        holder.phoneText.setText(user.getPhone());

        holder.deleteButton.setOnClickListener(v -> {
            dbHelper.deleteUser(user.getUsername()); // Use unique identifier
            userList.remove(position);
            notifyItemRemoved(position);
            Toast.makeText(context, "User deleted", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }
}
