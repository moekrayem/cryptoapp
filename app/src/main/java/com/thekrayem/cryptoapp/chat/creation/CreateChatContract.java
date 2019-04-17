package com.thekrayem.cryptoapp.chat.creation;

import com.thekrayem.cryptoapp.mvpbase.BaseMvpPresenter;
import com.thekrayem.cryptoapp.mvpbase.BaseView;

public interface CreateChatContract {

    interface View extends BaseView{

        void showAlert(AlertType alertType);

        void onCreateDone();

        enum AlertType{
            // finalization
            NAME_NOT_SET
            ,MY_KEY_NOT_SET
            ,THEIR_KEY_NOT_SET
            ,ERROR_UNKNOWN

        }

    }

    interface Presenter extends BaseMvpPresenter<View>{

        void setChatUserId(long chatUserId);

        void finalizeCreation();


    }

}
