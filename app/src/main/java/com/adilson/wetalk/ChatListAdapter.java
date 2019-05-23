package com.adilson.wetalk;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatListViewHolder> {
    private String TAG = getClass().getSimpleName();
    private ArrayList<ChatData> listOfChats;
    private Context context;
    private String userId;
    private OnListItemClick<ChatData> onListItemClick;

    public ChatListAdapter(Context context, String userId) {
        this.context = context;
        this.userId = userId;
        listOfChats = new ArrayList<>();
    }

    @NonNull
    @Override
    public ChatListAdapter.ChatListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ChatListAdapter.ChatListViewHolder(LayoutInflater.from(context).inflate(R.layout.chat_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ChatListAdapter.ChatListViewHolder holder, int position) {
        final ChatData chatData = listOfChats.get(position);

        if (chatData.isGroupChat()) {
            holder.chatName.setText(chatData.getChatName());

            for (String key : chatData.getListOfParticipants().keySet()) {
                if (key.equals(userId))
                    continue;

                String participantName = chatData.getListOfParticipants().get(key);
                String text = String.format(Locale.US, "com %s e mais %d", participantName, chatData.getListOfParticipants().size() - 1);

                Log.d(TAG, "onBindViewHolder: "+ text);

                holder.lastMessage.setText(text);
                break;

            }
        } else {

                holder.lastMessage.setVisibility(View.INVISIBLE);
            for (String key : chatData.getListOfParticipants().keySet()) {
                if (key.equals(userId))
                    continue;

                String chatName = chatData.getListOfParticipants().get(key);
                holder.chatName.setText(chatName);
                break;

            }


        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onListItemClick != null) {
                    onListItemClick.handle(chatData);
                }
            }
        });


    }

    public void addUser(ChatData chatData) {
        listOfChats.add(chatData);
    }

    @Override
    public int getItemCount() {
        return listOfChats.size();
    }

    public void setOnListItemClick(OnListItemClick<ChatData> onListItemClick) {
        this.onListItemClick = onListItemClick;
    }

    public void updateData(ChatData chatData) {
        Log.d(TAG,"updateData new data: "+ chatData);
        Log.d(TAG,"updateData list of data: "+ listOfChats);


        for (int i = 0; i < listOfChats.size(); i++) {
            ChatData data = listOfChats.get(i);
            if (chatData.getChatRoomId().equals(data.getChatRoomId())) {
                listOfChats.remove(i);
                listOfChats.add(chatData);
                break;
            }
        }
    }

    public void removeChatdata(ChatData chatData) {
        for (int i = 0; i < listOfChats.size(); i++) {
            ChatData data = listOfChats.get(i);
            if (chatData.getChatRoomId().equals(data)) {
                listOfChats.remove(i);
                break;
            }
        }
    }

    public class ChatListViewHolder extends RecyclerView.ViewHolder {
        private TextView lastMessage, chatName;

        public ChatListViewHolder(@NonNull View itemView) {
            super(itemView);

            chatName = itemView.findViewById(R.id.chat_list_item_name);
            lastMessage = itemView.findViewById(R.id.chat_list_item_last_message);
        }
    }

}
