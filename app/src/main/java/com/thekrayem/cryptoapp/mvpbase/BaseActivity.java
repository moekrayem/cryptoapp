package com.thekrayem.cryptoapp.mvpbase;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import dagger.android.AndroidInjection;

public abstract class BaseActivity extends AppCompatActivity implements BaseView {

    @Override
    protected final void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentResource());
        init(savedInstanceState);
    }

    @LayoutRes
    protected abstract int getContentResource();

    protected abstract void init(@Nullable Bundle savedInstanceState);
}
