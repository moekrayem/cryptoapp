package com.thekrayem.cryptoapp.boxdb.models;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

@Entity
public class ChatUserRecord {

    @Id
    private Long userId;

    private String serverId;
    private String userName;
    private byte[] myKey;
    private byte[] theirKey;
    private int myIndex;
    private int theirIndex;

    public ChatUserRecord(Long userId, String serverId, String userName, byte[] myKey, byte[] theirKey, int myIndex, int theirIndex) {
        this.userId = userId;
        this.serverId = serverId;
        this.userName = userName;
        this.myKey = myKey;
        this.theirKey = theirKey;
        this.myIndex = myIndex;
        this.theirIndex = theirIndex;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public byte[] getMyKey() {
        return myKey;
    }

    public void setMyKey(byte[] myKey) {
        this.myKey = myKey;
    }

    public byte[] getTheirKey() {
        return theirKey;
    }

    public void setTheirKey(byte[] theirKey) {
        this.theirKey = theirKey;
    }

    public int getMyIndex() {
        return myIndex;
    }

    public void setMyIndex(int myIndex) {
        this.myIndex = myIndex;
    }

    public int getTheirIndex() {
        return theirIndex;
    }

    public void setTheirIndex(int theirIndex) {
        this.theirIndex = theirIndex;
    }
}
