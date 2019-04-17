package com.thekrayem.cryptoapp.di;

import com.thekrayem.cryptoapp.boxdb.handler.CryptoDatabaseHandler;
import com.thekrayem.cryptoapp.boxdb.handler.chat.CryptoChatDatabaseHandler;
import com.thekrayem.cryptoapp.boxdb.handler.file.CryptoFileDatabaseHandler;
import com.thekrayem.cryptoapp.chat.creation.CreateChatPresenter;
import com.thekrayem.cryptoapp.chat.creation.my.MyKeyPresenter;
import com.thekrayem.cryptoapp.chat.creation.their.TheirKeyPresenter;
import com.thekrayem.cryptoapp.file.decryption.DecryptFileListPresenter;
import com.thekrayem.cryptoapp.file.encryption.EncryptFileActivity;
import com.thekrayem.cryptoapp.file.encryption.EncryptFilePresenter;
import com.thekrayem.cryptoapp.helper.App;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.support.AndroidSupportInjectionModule;

@Singleton
@Component(modules = {
        AndroidSupportInjectionModule.class,
        AppModule.class,
        BuildersModule.class})
public interface AppComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder application(App application);

        AppComponent build();
    }

    void inject(App app);

    void inject(EncryptFilePresenter presenter);

    void inject(DecryptFileListPresenter presenter);

    void inject(TheirKeyPresenter presenter);

    void inject(MyKeyPresenter presenter);

    void inject(CreateChatPresenter presenter);

    void inject(CryptoDatabaseHandler handler);

    void inject(CryptoChatDatabaseHandler handler);

    void inject(CryptoFileDatabaseHandler handler);


}
