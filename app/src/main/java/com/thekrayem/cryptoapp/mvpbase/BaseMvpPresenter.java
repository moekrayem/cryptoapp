package com.thekrayem.cryptoapp.mvpbase;

public interface BaseMvpPresenter<V extends BaseView> {

    void attach(V view);

    void detach();

}
