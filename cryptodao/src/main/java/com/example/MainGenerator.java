package com.example;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

public class MainGenerator {
    private static final String PROJECT_DIR = System.getProperty("user.dir");

    public static void main(String[] args) {
        Schema schema = new Schema(1, "com.thekrayem.cryptoapp.db");
        schema.enableKeepSectionsByDefault();

        addTables(schema);

        try {
            new DaoGenerator().generateAll(schema, PROJECT_DIR + "\\app\\src\\main\\java");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void addTables(final Schema schema){

        Entity file = schema.addEntity("file");
        file.addLongProperty("file_id").primaryKey().autoincrement();
        file.addByteArrayProperty("file_encrypted_bytes");
        file.addByteArrayProperty("hashed_pass");
        file.addByteArrayProperty("salt");
        file.addByteArrayProperty("iv");
        file.addStringProperty("file_original_name");
        file.addStringProperty("file_encrypted_name");
        file.addStringProperty("file_path");
        file.addIntProperty("iterations");
        file.addIntProperty("key_length");
        file.addBooleanProperty("is_chat");
        file.addByteArrayProperty("file_hash");

        Entity chat_user = schema.addEntity("chat_user");
        chat_user.addLongProperty("chat_user_id").primaryKey();
        chat_user.addStringProperty("chat_user_their_instance_id");
        chat_user.addStringProperty("chat_user_name");
        chat_user.addByteArrayProperty("chat_user_my_key_bytes");
        chat_user.addByteArrayProperty("chat_user_their_key_bytes");
        chat_user.addIntProperty("chat_user_my_key_index");
        chat_user.addIntProperty("chat_user_their_key_index");

        Entity chat_message = schema.addEntity("chat_message");
        chat_message.addLongProperty("chat_message_id").primaryKey();
        chat_message.addStringProperty("chat_message_content");
        chat_message.addBooleanProperty("chat_message_mine");
        chat_message.addLongProperty("chat_message_user_id");
        chat_message.addLongProperty("chat_message_time");

    }
}
