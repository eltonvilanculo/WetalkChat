package com.adilson.wetalk;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


/**
 * A simple {@link Fragment} subclass.
 */
public class UserListFragment extends MyFragment {
    private final String TAG = getClass().getSimpleName();

    private RecyclerView recyclerView;
    private UserListAdapter adapter;
    private User currentUser;
    private DatabaseReference usersDatabaseRef;
    private ChildEventListener usersEventListener;
    private ChatData chatData;
    private EditText groupNameTxt;

    public UserListFragment() {
        // Required empty public constructor
    }

    public static UserListFragment cleanUserListInstance(User user) {

        Bundle args = new Bundle();

        Log.d("UserListFragment", "cleanUserListInstance: " + user);

        args.putParcelable(ChatRoomActivity.USER, user);
        UserListFragment fragment = new UserListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static UserListFragment participantInstance(User user, ChatData chatData) {
        Bundle args = new Bundle();

        Log.d("UserListFragment", "participantInstance: " + user);

        args.putParcelable(ChatRoomActivity.USER, user);
        args.putParcelable(ChatRoomActivity.CHAT, chatData);
        UserListFragment fragment = new UserListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_list, container, false);


        recyclerView = view.findViewById(R.id.user_list_recycler_view);
        groupNameTxt = view.findViewById(R.id.input_user_group_name);

        initData();

        adapter = new UserListAdapter(getActivity());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        adapter.setOnListItemClick(new OnListItemClick<User>() {
            @Override
            public void handle(User data) {
                if (chatData == null)
                    findMatchingChat(currentUser, data);
                else addParticipant(data);
            }
        });


        getAllUsers();


        return view;
    }

    private void addParticipant(User data) {
        String chatName = groupNameTxt.getText().toString();

        if (chatName.isEmpty()) {
            groupNameTxt.setError("Escreva o nome do grupo");
            return;
        }
        chatData.getListOfParticipants().put(data.getUserId(), data.getName());

        chatData.setChatName(chatName);

        publishParticipant(chatData);

    }

    private void getAllUsers() {
        usersDatabaseRef = FirebaseDatabase.getInstance().getReference(DatabaseHelper.USERS);
        usersEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                User user = convertToUser(dataSnapshot);
                if (user != null) {
                    addUser(user);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "onCancelled: ", databaseError.toException());

            }
        };
        usersDatabaseRef.addChildEventListener(usersEventListener);

    }

    private User convertToUser(DataSnapshot dataSnapshot) {
        try {
            User value = dataSnapshot.getValue(User.class);
            value.setUserId(dataSnapshot.getKey());
            return value;
        } catch (Exception ex) {
            Log.e(TAG, "convertToUser: ", ex);
            return null;
        }

    }

    private void createChatRoom(final User currentUser, User otherUser) {
        Log.e(TAG, "createChatRoom, current: " + currentUser);
        Log.e(TAG, "createChatRoom, current: " + otherUser);


        final ChatData chat = new ChatData();
        Map<String, String> participants = new HashMap<>();
        participants.put(currentUser.getUserId(), currentUser.getName());
        participants.put(otherUser.getUserId(), otherUser.getName());
        chat.setListOfParticipants(participants);

        publishParticipant(chat);


    }

    private void publishParticipant(final ChatData chat) {
        final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.show();
        DatabaseReference chatData = FirebaseDatabase.getInstance().getReference(DatabaseHelper.CHAT);

        if (TextUtils.isEmpty(chat.getChatRoomId())) {
            chatData = chatData.push();
        } else chatData = chatData.child(chat.getChatRoomId());

        chatData.setValue(chat, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if (databaseError == null) {
                    String key = databaseReference.getKey();
                    Log.d(TAG, "key: " + key);
                    dialog.dismiss();

                    chat.setChatRoomId(key);
                    startActivity(currentUser, chat);

                } else {
                    Log.e(TAG, "onComplete: ", databaseError.toException());
                    dialog.dismiss();
                }
            }
        });
    }

    private void initData() {
        try {
            currentUser = getArguments().getParcelable(ChatRoomActivity.USER);
            Log.d("UserListFragment", "cleanUserListInstance: " + currentUser);

            chatData = getArguments().getParcelable(ChatRoomActivity.CHAT);

            if (chatData == null) {
                groupNameTxt.setVisibility(View.GONE);
            } else groupNameTxt.setText(chatData.getChatName());


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void findMatchingChat(final User currentUser, final User otherUserChats) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(DatabaseHelper.CHAT);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data :
                        dataSnapshot.getChildren()) {
                    ChatData chatData = convertToChatData(data);

                    if (chatData == null)
                        continue;


                    if (findMatch(currentUser.getUserId(), otherUserChats.getUserId(), chatData)) {
                        startActivity(currentUser, chatData);
                        return;
                    }
                }

                createChatRoom(currentUser, otherUserChats);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Log.e(TAG, "onCancelled: ", databaseError.toException());

            }
        });


    }

    private boolean findMatch(String currentUserChats, String otherUserChats, ChatData chatData) {
        if (chatData.isGroupChat()) {
            return false;
        }

        if (chatData.getListOfParticipants().isEmpty())
            return false;

        if (chatData.getListOfParticipants().containsKey(currentUserChats))
            if (chatData.getListOfParticipants().containsKey(otherUserChats))
                return true;
        return false;
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

    private void addUser(User user) {

        if (user.getUserId().equals(currentUser.getUserId()))
            return;

        adapter.addUser(user);
        adapter.notifyDataSetChanged();
    }

    public String getTitle() {
        return "Usuarios";
    }

    private void startActivity(User currentUser, ChatData chatRoom) {
        Intent intent = new Intent(getActivity(), ChatRoomActivity.class);
        intent.putExtra(ChatRoomActivity.USER, currentUser);
        intent.putExtra(ChatRoomActivity.CHAT, chatRoom);
        startActivity(intent);
    }

    @Override
    public void onStop() {
        super.onStop();
        usersDatabaseRef.removeEventListener(usersEventListener);
    }
}
