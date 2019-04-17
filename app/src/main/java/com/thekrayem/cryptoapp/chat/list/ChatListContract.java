package com.thekrayem.cryptoapp.chat.list;

import com.thekrayem.cryptoapp.mvpbase.BaseMvpPresenter;
import com.thekrayem.cryptoapp.mvpbase.BaseView;

import java.util.List;

public interface ChatListContract {

    interface View extends BaseView {

        void onListLoaded(List<ChatListObject> objects);

        void onError(ErrorType errorType);
    }

    enum ErrorType {
        CANT_LOAD_CHATS
    }

    interface Presenter extends BaseMvpPresenter<View> {

        void loadChats();
    }
}
