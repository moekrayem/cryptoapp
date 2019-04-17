package com.thekrayem.cryptoapp.file.decryption;

import com.thekrayem.cryptoapp.mvpbase.BaseMvpPresenter;
import com.thekrayem.cryptoapp.mvpbase.BaseView;

import java.util.List;

public interface DecryptFileContract {

    interface View extends BaseView{

        void onListLoaded(List<DecryptFileListObject> objects);

        void showAlert(AlertType alertType);

        enum AlertType{
            //list loading
            ERROR_LOADING_LIST

            // deletion
            ,ERROR_DELETING_RECORD
            ,RECORD_DELETED_FILE_DELETED
            ,RECORD_DELETED_FILE_NOT

            //decrypt
            ,ERROR_DECRYPTING_FILE
            ,INCORRECT_PASSWORD
            ,FILE_DECRYPTED
        }
    }


    interface Presenter extends BaseMvpPresenter<View>{

        void loadList();

        void decryptFile(long fileId,String password,boolean fromFile);

        void deleteRecord(long fileId, boolean deleteFile);

    }
}
