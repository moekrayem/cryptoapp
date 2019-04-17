package com.thekrayem.cryptoapp.di;

import android.content.Context;

import com.thekrayem.cryptoapp.boxdb.handler.CryptoDatabaseHandler;
import com.thekrayem.cryptoapp.boxdb.handler.DatabaseHandler;
import com.thekrayem.cryptoapp.boxdb.handler.chat.ChatDatabaseHandler;
import com.thekrayem.cryptoapp.boxdb.handler.chat.CryptoChatDatabaseHandler;
import com.thekrayem.cryptoapp.boxdb.handler.file.CryptoFileDatabaseHandler;
import com.thekrayem.cryptoapp.boxdb.handler.file.FileDatabaseHandler;
import com.thekrayem.cryptoapp.helper.App;

import dagger.Module;
import dagger.Provides;
import io.objectbox.BoxStore;

@Module
public class AppModule {

    @Provides
    Context provideContext(App app) {
        return app.getApplicationContext();
    }

    @Provides
    BoxStore provideBoxStore(App app){
        return app.getBoxStore();
    }

    @Provides
    DatabaseHandler provideDataBaseHandler(){
        return new CryptoDatabaseHandler();
    }

    @Provides
    FileDatabaseHandler provideFileDatabaseHandler(){
        return new CryptoFileDatabaseHandler();
    }

    @Provides
    ChatDatabaseHandler provideChatDatabaseHandler(){
        return new CryptoChatDatabaseHandler();
    }

}
