package com.thekrayem.cryptoapp.chat.creation.my;

import android.graphics.Bitmap;

import com.thekrayem.cryptoapp.mvpbase.BaseMvpPresenter;
import com.thekrayem.cryptoapp.mvpbase.BaseView;

public interface MyKeyContract {

    interface View extends BaseView {
        void onKeyCreated(int result,Bitmap bitmap);

        void setLoading(boolean loading,LoadingType type);

        enum LoadingType{
            CHECKING
            ,GENERATING
        }
        void onKeyChecked(Bitmap bitmap);
    }

    interface Presenter extends BaseMvpPresenter<View> {

        void checkForExistingKey(long userId);

        void generateKey(long userId);
    }
}
