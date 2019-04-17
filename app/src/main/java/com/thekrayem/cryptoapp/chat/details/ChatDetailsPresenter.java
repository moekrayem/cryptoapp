package com.thekrayem.cryptoapp.chat.details;

import android.os.AsyncTask;

import com.thekrayem.cryptoapp.boxdb.models.ChatMessageRecord;
import com.thekrayem.cryptoapp.boxdb.models.ChatMessageRecord_;
import com.thekrayem.cryptoapp.boxdb.models.ChatUserRecord;
import com.thekrayem.cryptoapp.helper.App;
import com.thekrayem.cryptoapp.mvpbase.BasePresenter;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import javax.inject.Inject;

import io.objectbox.Box;

public class ChatDetailsPresenter extends BasePresenter<ChatDetailsContract.View> implements ChatDetailsContract.Presenter {

    private long userId;

    @Inject
    ChatDetailsPresenter(){
    }

    @Override
    public void setUserId(long userId) {
        this.userId = userId;
    }

    @Override
    public void loadMessages() {
        new LoadMessagesTask(getView(), userId).execute();
    }


    private static class LoadMessagesTask extends AsyncTask<Void,Void,Boolean>{

        private WeakReference<ChatDetailsContract.View> viewWeakReference;
        private List<ChatMessageRecord> messageList;
        private long userId;
        private int totalKeyBytes = 0;
        private int keyIndex = 0;

        private LoadMessagesTask(ChatDetailsContract.View view, long userId){
            viewWeakReference = new WeakReference<>(view);
            messageList = new ArrayList<>();
            this.userId = userId;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                Box<ChatMessageRecord> messageBox = App.getInstance().getBoxStore().boxFor(ChatMessageRecord.class);
                messageList = messageBox.query().equal(ChatMessageRecord_.userId,userId).build().find();
                Box<ChatUserRecord> userBox = App.getInstance().getBoxStore().boxFor(ChatUserRecord.class);
                ChatUserRecord user = userBox.get(userId);
                keyIndex = user.getMyIndex();
                totalKeyBytes = user.getMyKey().length;
                return true;
            }catch (Exception e){
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            ChatDetailsContract.View view = viewWeakReference.get();
            if(view != null){
                if(result){
                    view.onMessagesLoaded(messageList,totalKeyBytes,keyIndex);
                }else {
                    view.onError(ChatDetailsContract.ErrorType.CANT_LOAD_MESSAGES);
                }
            }
        }
    }

    @Override
    public void sendMessage(String content) {
        ChatMessageRecord messageRecord = new ChatMessageRecord(0,userId,Calendar.getInstance().getTimeInMillis(),content,true,ChatMessageRecord.MESSAGE_STATUS_PENDING);
        getView().onNewMessage(messageRecord);
        new SendMessageTask(getView(),messageRecord).execute();
    }

    private static class SendMessageTask extends AsyncTask<Void,Void,Boolean>{

        private WeakReference<ChatDetailsContract.View> viewWeakReference;
        private ChatMessageRecord messageRecord;

        private SendMessageTask(ChatDetailsContract.View view,ChatMessageRecord messageRecord){
            viewWeakReference = new WeakReference<>(view);
            this.messageRecord = messageRecord;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try{

                long id = App.getInstance().getBoxStore().boxFor(ChatMessageRecord.class).put(messageRecord);
                messageRecord.setMessageId(id);
                // simulate sending to server
                Thread.sleep(200);
                // fail half the times
                return new Random().nextInt() % 2 == 0;
            }catch (Exception e){
                e.printStackTrace();
                return false;
            }
        }


        @Override
        protected void onPostExecute(Boolean result) {
            messageRecord.setStatus(result ? ChatMessageRecord.MESSAGE_STATUS_SENT : ChatMessageRecord.MESSAGE_STATUS_FAILED);
            ChatDetailsContract.View view = viewWeakReference.get();
            if(view != null){
                view.onMessageChanged(messageRecord);
            }
        }
    }

    @Override
    public void receiveMessage(ChatMessageRecord message) {
        getView().onNewMessage(message);
    }
}
