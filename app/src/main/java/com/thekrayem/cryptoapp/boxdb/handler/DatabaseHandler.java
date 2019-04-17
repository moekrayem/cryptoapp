package com.thekrayem.cryptoapp.boxdb.handler;

import com.thekrayem.cryptoapp.boxdb.handler.chat.ChatDatabaseHandler;
import com.thekrayem.cryptoapp.boxdb.handler.file.FileDatabaseHandler;

public interface DatabaseHandler {

    FileDatabaseHandler getFileHandler();

    ChatDatabaseHandler getChatHandler();



}
