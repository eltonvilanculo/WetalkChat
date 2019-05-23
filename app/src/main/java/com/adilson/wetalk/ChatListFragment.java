package com.adilson.wetalk;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatListFragment extends Fragment {
    private String TAG = getClass().getSimpleName();
    private RecyclerView recyclerView;
    private ChatListAdapter adapter;
    private User currentUser;

    public ChatListFragment() {
        // Required empty public constructor
    }

    public static ChatListFragment newInstance(User user) {

        Bundle args = new Bundle();
        args.putParcelable("user", user);
        ChatListFragment fragment = new ChatListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_list, container, false);

        getCurrentUser();

        recyclerView = view.findViewById(R.id.chat_list_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new ChatListAdapter(getActivity(), currentUser.getUserId());
        adapter.setOnListItemClick(new OnListItemClick<ChatData>() {
            @Override
            public void handle(ChatData data) {
                startActivity(currentUser, data);
            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);

        findMatchingChat();

        return view;
    }

    private void findMatchingChat() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(DatabaseHelper.CHAT);
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d(TAG, "onChildAdded: " + dataSnapshot.getValue());

                ChatData chatData = convertToChatData(dataSnapshot);

                if (chatData != null) {
                    if (chatData.getListOfParticipants().containsKey(currentUser.getUserId())) {
                        addChatData(chatData);
                    }
                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d(TAG, "onChildChanged: " + dataSnapshot.getValue());
                ChatData chatData = convertToChatData(dataSnapshot);

                if (chatData != null){
                    updateChatData(chatData);
                }

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                Log.d(TAG, "onChildRemoved, key: " + dataSnapshot.getKey());
                Log.d(TAG, "onChildRemoved: " + dataSnapshot.getValue());
                ChatData chatData = convertToChatData(dataSnapshot);

                if (chatData != null){
                    removeChatData(chatData);
                }


            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "onCancelled: " + databaseError.toException());
            }
        });


    }

    private void removeChatData(ChatData chatData) {
        adapter.removeChatdata(chatData);
        adapter.notifyDataSetChanged();
    }

    private void updateChatData(ChatData chatData) {
        Log.e(TAG, "updateChatData: " + chatData);


        adapter.updateData(chatData);
        adapter.notifyDataSetChanged();
    }

    private ChatData convertToChatData(DataSnapshot dataSnapshot) {
        try {
            ChatData value = dataSnapshot.getValue(ChatData.class);
            value.setChatRoomId(dataSnapshot.getKey());
            return value;
        } catch (Exception ex) {
            Log.e(TAG, "convertToChatData: ", ex);
            return null;
        }
    }

    private void addChatData(ChatData data) {
        adapter.addUser(data);
        adapter.notifyDataSetChanged();
    }

    private void startActivity(User currentUser, ChatData chatRoom) {
        Intent intent = new Intent(getActivity(), ChatRoomActivity.class);
        intent.putExtra(ChatRoomActivity.USER, currentUser);
        intent.putExtra(ChatRoomActivity.CHAT, chatRoom);
        startActivity(intent);
    }

    private void getCurrentUser() {
        currentUser = getArguments().getParcelable("user");
        Log.d(TAG, "currentUser: " + currentUser);
    }

}
