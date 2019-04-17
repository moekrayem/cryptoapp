package com.thekrayem.cryptoapp.chat.details;

import dagger.Module;
import dagger.Provides;

@Module
public class ChatDetailsModule {

    @Provides
    ChatDetailsContract.Presenter provideEncryptFilePresenter() {
        return new ChatDetailsPresenter();
    }
}
