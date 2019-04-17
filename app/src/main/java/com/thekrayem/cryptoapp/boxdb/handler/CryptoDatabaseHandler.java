package com.thekrayem.cryptoapp.boxdb.handler;

import com.thekrayem.cryptoapp.boxdb.handler.chat.ChatDatabaseHandler;
import com.thekrayem.cryptoapp.boxdb.handler.file.FileDatabaseHandler;
import com.thekrayem.cryptoapp.helper.App;

import javax.inject.Inject;

public class CryptoDatabaseHandler implements DatabaseHandler {

    @Inject
    FileDatabaseHandler fileHandler;
    @Inject
    ChatDatabaseHandler chatHandler;

    @Inject
    public CryptoDatabaseHandler(){
        App.getInstance().getAppComponent().inject(this);
    }

    @Override
    public FileDatabaseHandler getFileHandler() {
        return fileHandler;
    }

    @Override
    public ChatDatabaseHandler getChatHandler() {
        return chatHandler;
    }
}
