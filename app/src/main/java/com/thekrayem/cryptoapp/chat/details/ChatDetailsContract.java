package com.thekrayem.cryptoapp.chat.details;

import com.thekrayem.cryptoapp.boxdb.models.ChatMessageRecord;
import com.thekrayem.cryptoapp.mvpbase.BaseMvpPresenter;
import com.thekrayem.cryptoapp.mvpbase.BaseView;

import java.util.List;

public interface ChatDetailsContract {

    interface View extends BaseView{

        void onMessagesLoaded(List<ChatMessageRecord> messages,int totalKeyBytes, int keyIndex);

        void onNewMessage(ChatMessageRecord message);

        void onMessageChanged(ChatMessageRecord messageRecord);

        void onError(ErrorType errorType);
    }

    enum ErrorType{
        CANT_LOAD_MESSAGES
    }

    interface Presenter extends BaseMvpPresenter<View> {

        void setUserId(long userId);

        void loadMessages();

        void sendMessage(String content);

        void receiveMessage(ChatMessageRecord message);
    }
}
