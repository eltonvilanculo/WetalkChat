package com.adilson.wetalk;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import static com.adilson.wetalk.ChatRoomActivity.CHAT;
import static com.adilson.wetalk.ChatRoomActivity.USER;

public class AddParticipantActivity extends AppCompatActivity {

    private User currentUser;
    private ChatData chatData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_participant);

        try {
            currentUser = getIntent().getParcelableExtra(USER);
            chatData = getIntent().getParcelableExtra(CHAT);

        } catch (ClassCastException e) {
            e.printStackTrace();

        }

        UserListFragment userListFragment = UserListFragment.participantInstance(currentUser, chatData);

        getSupportFragmentManager().beginTransaction().replace(R.id.participant_container, userListFragment).commit();

    }
}
