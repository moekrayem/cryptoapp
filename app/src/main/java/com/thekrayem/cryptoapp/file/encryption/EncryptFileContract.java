package com.thekrayem.cryptoapp.file.encryption;

import android.net.Uri;

import com.thekrayem.cryptoapp.helper.password.PasswordStrengthResult;
import com.thekrayem.cryptoapp.mvpbase.BaseMvpPresenter;
import com.thekrayem.cryptoapp.mvpbase.BaseView;

public interface EncryptFileContract {

    interface View extends BaseView{

        void onEncryptionModeSet(boolean encryptingChat);

        void onFileSelected(String fileName,long size);

        void showAlert(AlertType alertType);

        void onEncryptResult(int result,boolean encryptingChat);

        void onPasswordChecked(PasswordStrengthResult result);

        void setLoading(boolean loading);

        enum AlertType{
            ERROR_ENCRYPTING
            ,ERROR_FILE_SELECTOR
            ,ERROR_FILE_INFO

            // data check before encrypting
            ,NAME_NOT_VALID
            ,PASS_NOT_VALID
            ,NO_FILE_SELECTED
        }
    }

    interface Presenter extends BaseMvpPresenter<View>{

        void setupEncryptionMode(long userId);

        void selectFileForEncryption(Uri data);

        void checkPassword(String password);

        void encrypt(String name, String password, boolean saveFileBytes, boolean deleteOriginalFile);
    }
}
