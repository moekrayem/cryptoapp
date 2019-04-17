package com.thekrayem.cryptoapp.mvpbase;

public class BasePresenter<V extends BaseView> implements BaseMvpPresenter<V> {

    private V view;

    @Override
    public void attach(V view) {
        this.view = view;
    }

    @Override
    public void detach() {
        view = null;
    }

    public V getView() {
        return view;
    }
}
