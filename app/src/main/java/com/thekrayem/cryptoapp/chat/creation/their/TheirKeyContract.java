package com.thekrayem.cryptoapp.chat.creation.their;

import android.graphics.Bitmap;

import com.thekrayem.cryptoapp.mvpbase.BaseMvpPresenter;
import com.thekrayem.cryptoapp.mvpbase.BaseView;

public interface TheirKeyContract {

    interface View extends BaseView{

        void onDetectBitmapResult(int result);

    }

    interface Presenter extends BaseMvpPresenter<View>{
        void detectBitmap(Bitmap bitmap, long userId);
    }
}
