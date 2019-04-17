package com.thekrayem.cryptoapp.boxdb.models;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

@Entity
public class FileRecord {

    @Id
    private long fileId;

    private byte[] fileEncryptedBytes;
    private byte[] hashedPass;
    private byte[] salt;
    private byte[] iv;
    private String fileOriginalName;
    private String fileEncryptedName;
    private String filePath;
    private int iterations;
    private int keyLength;
    private boolean isChat;
    private byte[] fileHash;

    public FileRecord(long fileId, byte[] fileEncryptedBytes, byte[] hashedPass, byte[] salt, byte[] iv, String fileOriginalName, String fileEncryptedName, String filePath, int iterations, int keyLength, boolean isChat, byte[] fileHash) {
        this.fileId = fileId;
        this.fileEncryptedBytes = fileEncryptedBytes;
        this.hashedPass = hashedPass;
        this.salt = salt;
        this.iv = iv;
        this.fileOriginalName = fileOriginalName;
        this.fileEncryptedName = fileEncryptedName;
        this.filePath = filePath;
        this.iterations = iterations;
        this.keyLength = keyLength;
        this.isChat = isChat;
        this.fileHash = fileHash;
    }

    public long getFileId() {
        return fileId;
    }

    public void setFileId(long fileId) {
        this.fileId = fileId;
    }

    public byte[] getFileEncryptedBytes() {
        return fileEncryptedBytes;
    }

    public void setFileEncryptedBytes(byte[] fileEncryptedBytes) {
        this.fileEncryptedBytes = fileEncryptedBytes;
    }

    public byte[] getHashedPass() {
        return hashedPass;
    }

    public void setHashedPass(byte[] hashedPass) {
        this.hashedPass = hashedPass;
    }

    public byte[] getSalt() {
        return salt;
    }

    public void setSalt(byte[] salt) {
        this.salt = salt;
    }

    public byte[] getIv() {
        return iv;
    }

    public void setIv(byte[] iv) {
        this.iv = iv;
    }

    public String getFileOriginalName() {
        return fileOriginalName;
    }

    public void setFileOriginalName(String fileOriginalName) {
        this.fileOriginalName = fileOriginalName;
    }

    public String getFileEncryptedName() {
        return fileEncryptedName;
    }

    public void setFileEncryptedName(String fileEncryptedName) {
        this.fileEncryptedName = fileEncryptedName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public int getIterations() {
        return iterations;
    }

    public void setIterations(int iterations) {
        this.iterations = iterations;
    }

    public int getKeyLength() {
        return keyLength;
    }

    public void setKeyLength(int keyLength) {
        this.keyLength = keyLength;
    }

    public boolean isChat() {
        return isChat;
    }

    public void setChat(boolean chat) {
        isChat = chat;
    }

    public byte[] getFileHash() {
        return fileHash;
    }

    public void setFileHash(byte[] fileHash) {
        this.fileHash = fileHash;
    }
}
