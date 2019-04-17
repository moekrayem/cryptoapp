package com.thekrayem.cryptoapp.chat.creation;

import android.os.AsyncTask;

import com.thekrayem.cryptoapp.boxdb.handler.DatabaseHandler;
import com.thekrayem.cryptoapp.boxdb.models.ChatUserRecord;
import com.thekrayem.cryptoapp.helper.App;
import com.thekrayem.cryptoapp.mvpbase.BasePresenter;

import java.lang.ref.WeakReference;

import javax.inject.Inject;

import io.objectbox.BoxStore;

public class CreateChatPresenter extends BasePresenter<CreateChatContract.View> implements CreateChatContract.Presenter {

    DatabaseHandler databaseHandler;

    @Inject
    CreateChatPresenter(){
        App.getInstance().getAppComponent().inject(this);
    }

    private long chatUserId;


    @Override
    public void setChatUserId(long chatUserId) {
        this.chatUserId = chatUserId;
    }

    @Override
    public void finalizeCreation() {
        new CheckChatData(getView(),databaseHandler,chatUserId).execute();
    }

    private static class CheckChatData extends AsyncTask<Void,Void,Integer>{

        private static final int RESULT_ERROR = -1;
        private static final int RESULT_SUCCESS = 1;
        private static final int RESULT_SETUP_NAME = 2;
        private static final int RESULT_SETUP_YOUR_KEY = 3;
        private static final int RESULT_SETUP_THEIR_KEY = 4;

        private DatabaseHandler databaseHandler;
        private long chatUserId;
        private WeakReference<CreateChatContract.View> viewWeakReference;

        private CheckChatData(CreateChatContract.View view,DatabaseHandler databaseHandler,long chatUserId){
            viewWeakReference = new WeakReference<>(view);
            this.databaseHandler = databaseHandler;
            this.chatUserId = chatUserId;
        }

        @Override
        protected Integer doInBackground(Void... args){
            try{
                ChatUserRecord chatUserRecord = databaseHandler.getChatHandler().getUser(chatUserId);
                if(chatUserRecord == null || chatUserId == -1L){
                    return RESULT_SETUP_NAME;
                }
                if(chatUserRecord.getMyKey().length == 0){
                    return RESULT_SETUP_YOUR_KEY;
                }
                if(chatUserRecord.getTheirKey().length == 0){
                    return RESULT_SETUP_THEIR_KEY;
                }
                return RESULT_SUCCESS;
            }catch (Exception e){
                e.printStackTrace();
                return RESULT_ERROR;
            }
        }

        @Override
        protected void onPostExecute(Integer result){
            CreateChatContract.View view = viewWeakReference.get();
            if (view != null){
                switch (result){
                    case RESULT_SUCCESS:
                        view.onCreateDone();
                        break;
                    case RESULT_ERROR:
                        view.showAlert(CreateChatContract.View.AlertType.ERROR_UNKNOWN);
                        break;
                    case RESULT_SETUP_YOUR_KEY:
                        view.showAlert(CreateChatContract.View.AlertType.MY_KEY_NOT_SET);
                        break;
                    case RESULT_SETUP_THEIR_KEY:
                        view.showAlert(CreateChatContract.View.AlertType.THEIR_KEY_NOT_SET);
                        break;
                    case RESULT_SETUP_NAME:
                        view.showAlert(CreateChatContract.View.AlertType.NAME_NOT_SET);
                        break;
                }
            }
        }
    }
}
