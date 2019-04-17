package com.thekrayem.cryptoapp.boxdb.models;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

@Entity
public class ChatMessageRecord {

    @Id
    private long messageId;

    private long messageTime;
    private String messageContent;
    private long userId;
    private boolean mine;
    private int status;

    public static final int MESSAGE_STATUS_BLANK = 0;
    public static final int MESSAGE_STATUS_PENDING = 1;
    public static final int MESSAGE_STATUS_SENT = 2;
    public static final int MESSAGE_STATUS_FAILED = 3;


    public ChatMessageRecord(long messageId,long userId, long messageTime, String messageContent,boolean mine,int status) {
        this.messageId = messageId;
        this.messageTime = messageTime;
        this.messageContent = messageContent;
        this.userId = userId;
        this.mine = mine;
        this.status = status;
    }

    public boolean isMine() {
        return mine;
    }

    public void setMine(boolean mine) {
        this.mine = mine;
    }

    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    public long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
