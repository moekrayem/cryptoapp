package com.thekrayem.cryptoapp.db;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END
/**
 * Entity mapped to table "CHAT_USER".
 */
public class chat_user {

    private Long chat_user_id;
    private String chat_user_their_instance_id;
    private String chat_user_name;
    private byte[] chat_user_my_key_bytes;
    private byte[] chat_user_their_key_bytes;
    private Integer chat_user_my_key_index;
    private Integer chat_user_their_key_index;

    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END

    public chat_user() {
    }

    public chat_user(Long chat_user_id) {
        this.chat_user_id = chat_user_id;
    }

    public chat_user(Long chat_user_id, String chat_user_their_instance_id, String chat_user_name, byte[] chat_user_my_key_bytes, byte[] chat_user_their_key_bytes, Integer chat_user_my_key_index, Integer chat_user_their_key_index) {
        this.chat_user_id = chat_user_id;
        this.chat_user_their_instance_id = chat_user_their_instance_id;
        this.chat_user_name = chat_user_name;
        this.chat_user_my_key_bytes = chat_user_my_key_bytes;
        this.chat_user_their_key_bytes = chat_user_their_key_bytes;
        this.chat_user_my_key_index = chat_user_my_key_index;
        this.chat_user_their_key_index = chat_user_their_key_index;
    }

    public Long getChat_user_id() {
        return chat_user_id;
    }

    public void setChat_user_id(Long chat_user_id) {
        this.chat_user_id = chat_user_id;
    }

    public String getChat_user_their_instance_id() {
        return chat_user_their_instance_id;
    }

    public void setChat_user_their_instance_id(String chat_user_their_instance_id) {
        this.chat_user_their_instance_id = chat_user_their_instance_id;
    }

    public String getChat_user_name() {
        return chat_user_name;
    }

    public void setChat_user_name(String chat_user_name) {
        this.chat_user_name = chat_user_name;
    }

    public byte[] getChat_user_my_key_bytes() {
        return chat_user_my_key_bytes;
    }

    public void setChat_user_my_key_bytes(byte[] chat_user_my_key_bytes) {
        this.chat_user_my_key_bytes = chat_user_my_key_bytes;
    }

    public byte[] getChat_user_their_key_bytes() {
        return chat_user_their_key_bytes;
    }

    public void setChat_user_their_key_bytes(byte[] chat_user_their_key_bytes) {
        this.chat_user_their_key_bytes = chat_user_their_key_bytes;
    }

    public Integer getChat_user_my_key_index() {
        return chat_user_my_key_index;
    }

    public void setChat_user_my_key_index(Integer chat_user_my_key_index) {
        this.chat_user_my_key_index = chat_user_my_key_index;
    }

    public Integer getChat_user_their_key_index() {
        return chat_user_their_key_index;
    }

    public void setChat_user_their_key_index(Integer chat_user_their_key_index) {
        this.chat_user_their_key_index = chat_user_their_key_index;
    }

    // KEEP METHODS - put your custom methods here
    // KEEP METHODS END

}
