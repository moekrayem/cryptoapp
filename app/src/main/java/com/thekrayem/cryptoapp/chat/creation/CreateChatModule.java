package com.thekrayem.cryptoapp.chat.creation;

import com.thekrayem.cryptoapp.chat.creation.my.MyKeyContract;
import com.thekrayem.cryptoapp.chat.creation.my.MyKeyPresenter;
import com.thekrayem.cryptoapp.chat.creation.their.TheirKeyContract;
import com.thekrayem.cryptoapp.chat.creation.their.TheirKeyPresenter;

import dagger.Module;
import dagger.Provides;

@Module
public class CreateChatModule {

    @Provides
    CreateChatContract.Presenter provideEncryptFilePresenter() {
        return new CreateChatPresenter();
    }

    @Provides
    MyKeyContract.Presenter provideMyKeyPresenter() {
        return new MyKeyPresenter();
    }

    @Provides
    TheirKeyContract.Presenter provideTheirKeyPresenter() {
        return new TheirKeyPresenter();
    }
}
