package com.thekrayem.cryptoapp.helper;

import android.app.Activity;
import android.app.Application;

//import com.thekrayem.cryptoapp.boxdb.MyObjectBox;
import com.thekrayem.cryptoapp.boxdb.models.MyObjectBox;
import com.thekrayem.cryptoapp.di.AppComponent;
import com.thekrayem.cryptoapp.di.DaggerAppComponent;
//import com.thekrayem.cryptoapp.di.DaggerAppComponent;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;
import io.objectbox.BoxStore;

public class App extends Application implements HasActivityInjector {


//    private static App instance;
    private static BoxStore boxStore;

    public BoxStore getBoxStore() {
        return boxStore;
    }

    private static App instance;

    public static App getInstance(){
        return instance;
    }

    private AppComponent appComponent;

    public AppComponent getAppComponent() {
        return appComponent;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        boxStore = MyObjectBox.builder().androidContext(App.this).build();
        appComponent = DaggerAppComponent
                .builder()
                .application(this)
                .build();
        appComponent.inject(this);
    }

    @Inject
    DispatchingAndroidInjector<Activity> dispatchingAndroidInjector;

    @Override
    public AndroidInjector<Activity> activityInjector() {
        return dispatchingAndroidInjector;
    }
}
