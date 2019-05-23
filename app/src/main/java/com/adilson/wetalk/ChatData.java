package com.adilson.wetalk;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ChatData implements Parcelable {
    private String chatRoomId, chatName;
    private Map<String, String> listOfParticipants;
    private String chatLastMessage;

    public ChatData() {
    }

    protected ChatData(Parcel in) {
        chatRoomId = in.readString();
        chatName = in.readString();
        chatLastMessage = in.readString();
        Bundle bundle = in.readBundle();

        Set<String> strings = bundle.keySet();

        listOfParticipants = new HashMap<>();

        for (String key :
                strings) {
            String value = bundle.getString(key);

            listOfParticipants.put(key, value);
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(chatRoomId);
        dest.writeString(chatName);
        dest.writeString(chatLastMessage);

        Bundle bundle = new Bundle();

        for (Map.Entry<String, String> entrySet :
                listOfParticipants.entrySet()) {
            bundle.putString(entrySet.getKey(), entrySet.getValue());
        }

        dest.writeBundle(bundle);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ChatData> CREATOR = new Creator<ChatData>() {
        @Override
        public ChatData createFromParcel(Parcel in) {
            return new ChatData(in);
        }

        @Override
        public ChatData[] newArray(int size) {
            return new ChatData[size];
        }
    };

    public String getChatRoomId() {
        return chatRoomId;
    }

    public void setChatRoomId(String chatRoomId) {
        this.chatRoomId = chatRoomId;
    }

    public String getChatName() {
        return chatName;
    }

    public void setChatName(String chatName) {
        this.chatName = chatName;
    }

    public Map<String, String> getListOfParticipants() {
        if (listOfParticipants == null) {
            return new HashMap<>();
        }

        return listOfParticipants;
    }

    public void setListOfParticipants(Map<String, String> listOfParticipants) {
        this.listOfParticipants = listOfParticipants;
    }

    public String getChatLastMessage() {
        return chatLastMessage;
    }

    public void setChatLastMessage(String chatLastMessage) {
        this.chatLastMessage = chatLastMessage;
    }

    public boolean isGroupChat() {
        return listOfParticipants.size() > 2;
    }


    @Override
    public String toString() {
        return "ChatData{" +
                "chatRoomId='" + chatRoomId + '\'' +
                ", chatName='" + chatName + '\'' +
                ", listOfParticipants=" + listOfParticipants +
                ", chatLastMessage='" + chatLastMessage + '\'' +
                '}';
    }
}
