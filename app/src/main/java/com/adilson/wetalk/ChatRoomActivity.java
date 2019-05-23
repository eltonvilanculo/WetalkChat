package com.adilson.wetalk;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class ChatRoomActivity extends AppCompatActivity {
    public static final String USER = "current_user";
    public static final String CHAT = "chat_room";

    private final String TAG = getClass().getSimpleName();
    private LinearLayout contentLayout;
    private EditText inputSendMsg;
    DatabaseReference messagesDatabase;

    private ChatData chatData;
    private User currentUser;
    private ChildEventListener childEventListener;
    private ImageButton sendMessageButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        contentLayout = findViewById(R.id.chat_room_layout);
        inputSendMsg = findViewById(R.id.input_send_chat);
        Toolbar toolbar = findViewById(R.id.chat_room_toolbar);


        try {
            currentUser = getIntent().getParcelableExtra(USER);
            chatData = getIntent().getParcelableExtra(CHAT);

        } catch (ClassCastException e) {
            e.printStackTrace();

        }

        toolbar.setTitle(getChatName(chatData, currentUser));
        setSupportActionBar(toolbar);

        setUpDatabase();


        sendMessageButton = findViewById(R.id.btn_send_chat);
        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = inputSendMsg.getText().toString();

                Message message = new Message();
                message.setText(text);
                message.setSenderId(currentUser.getUserId());
                message.setSendDate(System.currentTimeMillis());
                message.setChatId(chatData.getChatRoomId());

                sendMessage(message);


            }
        });


    }

    private void sendMessage(Message message) {
        messagesDatabase.push().setValue(message, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if (databaseError == null) {
                    inputSendMsg.setText("");

                } else {
                    Log.e(TAG, "sendMessage: ", databaseError.toException());
                    inputSendMsg.setError("Nao foi possivel enviar mensagem");

                }
            }
        });
    }

    private void setUpDatabase() {
        messagesDatabase = FirebaseDatabase.getInstance().getReference(DatabaseHelper.CHAT_ROOM)
                .child(chatData.getChatRoomId());


        childEventListener = new ChildEventListener() {

            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.e(TAG, "onChildAdded: " + dataSnapshot.getValue());
                Message message = convertToMessage(dataSnapshot);

                if (message != null) {
                    updateMessageUi(message);
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
                Log.e(TAG, "onCancelled: " + databaseError.toException());

            }
        };

        messagesDatabase.addChildEventListener(childEventListener);

    }

    private Message convertToMessage(DataSnapshot dataSnapshot) {
        try {
            Message value = dataSnapshot.getValue(Message.class);
            value.setMessageId(dataSnapshot.getKey());
            return value;
        } catch (Exception ex) {
            Log.e(TAG, "convertToMessage: ", ex);
            return null;
        }

    }

    private void updateMessageUi(Message message) {
        View view = null;
        if (message.getSenderId().equals(currentUser.getUserId()))
            view = addMyMessage(message);
        else view = addOtherMessage(message);

        contentLayout.addView(view);
    }

    private View addOtherMessage(Message message) {
        View view = LayoutInflater.from(this).inflate(R.layout.received_message, null, false);
        TextView name = view.findViewById(R.id.receiver_sender_name_out);
        TextView msg = view.findViewById(R.id.receiver_message_out);
        TextView date = view.findViewById(R.id.receiver_date_out);

        if (chatData.isGroupChat())
            name.setText(getParticipantName(message.getSenderId()));
        else name.setVisibility(View.GONE);

        msg.setText(message.getText());

        date.setText(formatDate(message.getSendDate()));

        return view;

    }

    private View addMyMessage(Message message) {
        View view = LayoutInflater.from(this).inflate(R.layout.sent_message, null, false);
        TextView name = view.findViewById(R.id.sent_sender_name_out);
        TextView msg = view.findViewById(R.id.sent_message_out);
        TextView date = view.findViewById(R.id.sent_date_out);

        if (chatData.isGroupChat())
            name.setText("Voce");
        else name.setVisibility(View.GONE);

        msg.setText(message.getText());

        date.setText(formatDate(message.getSendDate()));

        return view;
    }

    private String formatDate(long sendDate) {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        return format.format(new Date(sendDate));
    }

    private String getParticipantName(String senderId) {
        for (Map.Entry<String, String> participant : chatData.getListOfParticipants().entrySet()) {
            if (participant.getKey().equals(senderId))
                return participant.getValue();

        }
        return "";
    }

    private String getChatName(ChatData chat, User currentUser) {
        if (chat.isGroupChat())
            return chat.getChatName();

        else {
            for (Map.Entry<String, String> participant : chat.getListOfParticipants().entrySet()) {
                if (!participant.getKey().equals(currentUser.getUserId()))
                    return participant.getValue();

            }
            return "";
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        final MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.chat_room_menu, menu);
        MenuItem item = menu.findItem(R.id.menu_item_add_participant);

        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = new Intent(ChatRoomActivity.this, AddParticipantActivity.class);
                intent.putExtra(CHAT, chatData);
                intent.putExtra(USER, currentUser);
                startActivity(intent);

                return true;
            }
        });

        return true;
    }

}
