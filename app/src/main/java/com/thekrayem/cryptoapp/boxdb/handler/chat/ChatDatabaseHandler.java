package com.thekrayem.cryptoapp.boxdb.handler.chat;

import com.thekrayem.cryptoapp.boxdb.models.ChatMessageRecord;
import com.thekrayem.cryptoapp.boxdb.models.ChatUserRecord;

import java.util.List;

public interface ChatDatabaseHandler {

    List<ChatMessageRecord> getUserMessages(long userId);

    ChatUserRecord getUser(long userId);

    void deleteAllForUser(long userId);

    void updateUser(ChatUserRecord user);

}
