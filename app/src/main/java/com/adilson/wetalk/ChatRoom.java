package com.adilson.wetalk;

public class ChatRoom {
    private ChatData chatData;
    private Message lastMessage;

    public ChatData getChatData() {
        return chatData;
    }

    public void setChatData(ChatData chatData) {
        this.chatData = chatData;
    }

    @Override
    public String toString() {
        return "ChatRoom{" +
                "chatData=" + chatData +
                ", lastMessage=" + lastMessage +
                '}';
    }

    public Message getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(Message lastMessage) {
        this.lastMessage = lastMessage;
    }
}
