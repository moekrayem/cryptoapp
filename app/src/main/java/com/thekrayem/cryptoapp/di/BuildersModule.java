package com.thekrayem.cryptoapp.di;

import com.thekrayem.cryptoapp.chat.details.ChatDetailsActivity;
import com.thekrayem.cryptoapp.chat.details.ChatDetailsModule;
import com.thekrayem.cryptoapp.file.encryption.EncryptFileActivity;
import com.thekrayem.cryptoapp.file.encryption.EncryptFileModule;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class BuildersModule {

    @ContributesAndroidInjector(modules = {EncryptFileModule.class})
    abstract EncryptFileActivity bindEncryptFileActivity();

    @ContributesAndroidInjector(modules = {ChatDetailsModule.class})
    abstract ChatDetailsActivity bindChatDetailsActivity();
}
