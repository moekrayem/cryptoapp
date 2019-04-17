package com.thekrayem.cryptoapp.chat.list;

import android.os.AsyncTask;

import com.thekrayem.cryptoapp.boxdb.models.ChatMessageRecord;
import com.thekrayem.cryptoapp.boxdb.models.ChatMessageRecord_;
import com.thekrayem.cryptoapp.boxdb.models.ChatUserRecord;
import com.thekrayem.cryptoapp.helper.App;
import com.thekrayem.cryptoapp.mvpbase.BasePresenter;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import io.objectbox.Box;

public class ChatListPresenter extends BasePresenter<ChatListContract.View> implements ChatListContract.Presenter {

    @Override
    public void loadChats() {
        new LoadChatsTask(getView()).execute();
    }

    private static class LoadChatsTask extends AsyncTask<Void, Void, List<ChatListObject>> {

        private WeakReference<ChatListContract.View> viewWeakReference;

        private LoadChatsTask(ChatListContract.View view) {
            viewWeakReference = new WeakReference<>(view);
        }

        @Override
        protected List<ChatListObject> doInBackground(Void... voids) {
            try {
                Box<ChatMessageRecord> messageBox = App.getInstance().getBoxStore().boxFor(ChatMessageRecord.class);
                List<ChatUserRecord> dbRecords = App.getInstance().getBoxStore().boxFor(ChatUserRecord.class).getAll();
                List<ChatListObject> objects = new ArrayList<>(dbRecords.size());
                for (ChatUserRecord oneRecord : dbRecords) {
                    ChatMessageRecord lastMessage = messageBox.query().equal(ChatMessageRecord_.userId, oneRecord.getUserId()).orderDesc(ChatMessageRecord_.messageTime).build().findFirst();
                    objects.add(
                            new ChatListObject(
                                    oneRecord.getUserId()
                                    , oneRecord.getUserName()
                                    , lastMessage != null ? lastMessage.getMessageContent() : ""
                                    , lastMessage != null ? lastMessage.getMessageTime() : 0
                            )
                    );
                }
                return objects;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<ChatListObject> objects) {
            ChatListContract.View view = viewWeakReference.get();
            if (view != null) {
                if (objects != null) {
                    view.onListLoaded(objects);
                } else {
                    view.onError(ChatListContract.ErrorType.CANT_LOAD_CHATS);
                }
            }
        }
    }

}
