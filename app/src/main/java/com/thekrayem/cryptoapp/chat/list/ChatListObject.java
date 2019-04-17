package com.thekrayem.cryptoapp.chat.list;

/**
 * Created by Mhamad on 06/03/2017.
 */

public class ChatListObject {

    private long userID;
    private String userName;
    private String lastMessageContent;
    private long lastMessageDate;

    public ChatListObject(long userID, String userName, String lastMessageContent, long lastMessageDate) {
        this.userID = userID;
        this.userName = userName;
        this.lastMessageContent = lastMessageContent;
        this.lastMessageDate = lastMessageDate;
    }

    public long getUserID() {
        return userID;
    }

    public void setUserID(long userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getLastMessageContent() {
        return lastMessageContent;
    }

    public void setLastMessageContent(String lastMessageContent) {
        this.lastMessageContent = lastMessageContent;
    }

    public long getLastMessageDate() {
        return lastMessageDate;
    }

    public void setLastMessageDate(long lastMessageDate) {
        this.lastMessageDate = lastMessageDate;
    }
}
