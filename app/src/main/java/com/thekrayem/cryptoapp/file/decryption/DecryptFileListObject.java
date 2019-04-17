package com.thekrayem.cryptoapp.file.decryption;


public class DecryptFileListObject {

    private long fileId;
    private String fileName;
    private String filePath;
    private boolean bytesSaved;
    private boolean fileSaved;
    private boolean isChat;

    public DecryptFileListObject(long fileId, String fileName, String filePath, boolean isChat, boolean bytesSaved, boolean fileSaved){
        setFileId(fileId);
        setFileName(fileName);
        setFilePath(filePath);
        setChat(isChat);
        setBytesSaved(bytesSaved);
        setFileSaved(fileSaved);
    }

    public long getFileId() {
        return fileId;
    }

    public void setFileId(long fileId) {
        this.fileId = fileId;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public boolean isChat() {
        return isChat;
    }

    public void setChat(boolean chat) {
        isChat = chat;
    }

    public boolean areBytesSaved() {
        return bytesSaved;
    }

    public void setBytesSaved(boolean bytesSaved) {
        this.bytesSaved = bytesSaved;
    }

    public boolean isFileSaved() {
        return fileSaved;
    }

    public void setFileSaved(boolean fileSaved) {
        this.fileSaved = fileSaved;
    }
}
