package com.thekrayem.cryptoapp.helper;


public interface RecyclerViewCallback<T> {

    void onClick(T object);

    void onLongClick(T object);
}
