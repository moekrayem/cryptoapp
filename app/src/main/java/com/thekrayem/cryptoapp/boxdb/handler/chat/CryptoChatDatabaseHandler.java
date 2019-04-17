package com.thekrayem.cryptoapp.boxdb.handler.chat;

import com.thekrayem.cryptoapp.boxdb.models.ChatMessageRecord;
import com.thekrayem.cryptoapp.boxdb.models.ChatMessageRecord_;
import com.thekrayem.cryptoapp.boxdb.models.ChatUserRecord;
import com.thekrayem.cryptoapp.helper.App;

import java.util.List;

import javax.inject.Inject;

import io.objectbox.Box;
import io.objectbox.BoxStore;

public class CryptoChatDatabaseHandler implements ChatDatabaseHandler {

    @Inject
    BoxStore boxStore;

    @Inject
    public CryptoChatDatabaseHandler() {
        App.getInstance().getAppComponent().inject(this);
    }

    @Override
    public List<ChatMessageRecord> getUserMessages(long userId) {
        return boxStore.boxFor(ChatMessageRecord.class)
                .query()
                .equal(ChatMessageRecord_.userId, userId)
                .orderDesc(ChatMessageRecord_.messageTime)
                .build()
                .find();
    }

    @Override
    public ChatUserRecord getUser(long userId) {
        return boxStore.boxFor(ChatUserRecord.class).get(userId);
    }

    @Override
    public void deleteAllForUser(long userId) {
        Box<ChatMessageRecord> messageBox = boxStore.boxFor(ChatMessageRecord.class);
        List<ChatMessageRecord> toDelete = messageBox.query().equal(ChatMessageRecord_.userId, userId).build().find();
        messageBox.remove(toDelete);
        boxStore.boxFor(ChatUserRecord.class).remove(userId);
    }

    @Override
    public void updateUser(ChatUserRecord user){
        boxStore.boxFor(ChatUserRecord.class).put(user);
    }
}
