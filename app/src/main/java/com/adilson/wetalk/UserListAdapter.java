package com.adilson.wetalk;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.UserListViewHolder> {
    private ArrayList<User> listOfUsers;
    private Context context;
    private OnListItemClick<User> onListItemClick;

    public UserListAdapter(Context context) {
        this.context = context;
        listOfUsers = new ArrayList<>();
    }

    @NonNull
    @Override
    public UserListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new UserListViewHolder(LayoutInflater.from(context).inflate(R.layout.user_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull UserListViewHolder holder, int position) {
        final User user = listOfUsers.get(position);

        holder.email.setText(user.getEmail());
        holder.name.setText(user.getName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onListItemClick != null) {
                    onListItemClick.handle(user);
                }
            }
        });


    }

    public void addUser(User user) {
        listOfUsers.add(user);
    }

    @Override
    public int getItemCount() {
        return listOfUsers.size();
    }

    public void setOnListItemClick(OnListItemClick<User> onListItemClick) {
        this.onListItemClick = onListItemClick;
    }

    public class UserListViewHolder extends RecyclerView.ViewHolder {
        private TextView name, email;

        public UserListViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.user_list_item_username);
            email = itemView.findViewById(R.id.user_list_item_email);
        }
    }


}
