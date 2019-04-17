package com.thekrayem.cryptoapp.file.encryption;

import dagger.Module;
import dagger.Provides;

@Module
public class EncryptFileModule {

    @Provides
    EncryptFileContract.Presenter provideEncryptFilePresenter() {
        return new EncryptFilePresenter();
    }
}
