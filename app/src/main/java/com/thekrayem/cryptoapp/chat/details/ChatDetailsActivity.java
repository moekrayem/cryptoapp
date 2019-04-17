package com.thekrayem.cryptoapp.chat.details;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.thekrayem.cryptoapp.R;
import com.thekrayem.cryptoapp.boxdb.models.ChatMessageRecord;
import com.thekrayem.cryptoapp.chat.details.recycleview.ChatDetailsAdapter;
import com.thekrayem.cryptoapp.chat.details.recycleview.VerticalDividerItemDecoration;
import com.thekrayem.cryptoapp.helper.App;
import com.thekrayem.cryptoapp.helper.StaticValues;
import com.thekrayem.cryptoapp.mvpbase.BaseActivity;


import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.android.AndroidInjection;


public class ChatDetailsActivity extends BaseActivity implements ChatDetailsContract.View {


    private ChatDetailsAdapter listAdapter;

    @Inject
    ChatDetailsContract.Presenter presenter;

    @Override
    protected int getContentResource() {
        return R.layout.new_activity_chat_details;
    }

    @Override
    protected void init(@Nullable Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        presenter.attach(this);
        presenter.setUserId(getIntent().getLongExtra(StaticValues.IntentExtraValues.CHAT_USER_ID_INTENT_EXTRA_VALUE,-1L));
        // setup RecyclerView
        RecyclerView recyclerView = findViewById(R.id.activity_chat_details_messages_rv);
        listAdapter = new ChatDetailsAdapter(new ArrayList<ChatMessageRecord>());
        LinearLayoutManager layoutManager = new LinearLayoutManager(ChatDetailsActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        VerticalDividerItemDecoration itemDecoration = new VerticalDividerItemDecoration(ChatDetailsActivity.this,android.R.color.transparent,5);
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setAdapter(listAdapter);
        findViewById(R.id.activity_chat_details_send_fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = ((EditText)findViewById(R.id.activity_chat_details_message_et)).getText().toString().trim();
                if(message.length() > 0){
                    presenter.sendMessage(message);
                }
            }
        });

        // register broadcast receiver
        LocalBroadcastManager.getInstance(ChatDetailsActivity.this)
                .registerReceiver(
                        updateListReceiver
                        ,new IntentFilter(StaticValues.IntentExtraValues.CHAT_DETAILS_BROADCAST_RECEIVER_FILTER)
                );


        presenter.loadMessages();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(updateListReceiver);
        presenter.detach();
    }

    private BroadcastReceiver updateListReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            switch(intent.getIntExtra(StaticValues.IntentExtraValues.CHAT_DETAILS_BROADCAST_RECEIVER_COMMAND,-1)){
                case StaticValues.IntentExtraValues.CHAT_DETAILS_BROADCAST_RECEIVER_NEW_MESSAGE:
                    long messageId = intent.getLongExtra(StaticValues.IntentExtraValues.CHAT_DETAILS_BROADCAST_RECEIVER_MESSAGE_ID,-1L);
                    // TODO: 03/02/2019 TEST THIS
                    presenter.receiveMessage(App.getInstance().getBoxStore().boxFor(ChatMessageRecord.class).get(messageId));
                    break;
            }
        }
    };

    @Override
    public void onMessagesLoaded(List<ChatMessageRecord> messages,int totalKeyBytes, int keyIndex) {
        if(listAdapter == null){
            return;
        }
        listAdapter.setObjects(messages);
        listAdapter.notifyDataSetChanged();
        ProgressBar progressBar = findViewById(R.id.activity_chat_details_key_progress_pb);
        progressBar.setMax(totalKeyBytes);
        progressBar.setProgress(keyIndex);
    }

    @Override
    public void onNewMessage(ChatMessageRecord message) {
        if(listAdapter == null){
            return;
        }
        listAdapter.addObject(message);
        listAdapter.notifyItemInserted(listAdapter.getItemCount() - 1);
    }

    @Override
    public void onMessageChanged(ChatMessageRecord messageRecord) {
        listAdapter.notifyItemChanged(listAdapter.replace(messageRecord));
    }

    @Override
    public void onError(ChatDetailsContract.ErrorType errorType) {
        switch (errorType){
            case CANT_LOAD_MESSAGES:
                Snackbar.make(findViewById(R.id.activity_chat_details_root_cl),getString(R.string.error),Snackbar.LENGTH_LONG).show();
                break;
        }
    }

//    private class FillListTask extends AsyncTask<Void,Void,Boolean>{
//
//        private byte[] keyBytes;
//        private int keyIndex;
//        private List<ChatDetailsListObject> objects;
//
//        @Override
//        protected Boolean doInBackground(Void... args){
//            try{
//                chat_user chatUser = DBConnector.getInstance(ChatDetailsActivity.this,StaticValues.DB_NAME)
//                        .chat_user
//                        .queryBuilder()
//                        .where(chat_userDao.Properties.Chat_user_id.eq(currentTheirUserId))
//                        .unique();
//                if(chatUser == null){
//                    currentUserName = "Testing";
//                    keyBytes = new byte[1000];
//                    new Random().nextBytes(keyBytes);
//                    keyIndex = 0;
//                    objects = new ArrayList<>();
//                    objects.add(new ChatDetailsListObject(1,1,currentUserName,Calendar.getInstance().getTimeInMillis(),"From me",true));
//                    objects.add(new ChatDetailsListObject(2,1,currentUserName,Calendar.getInstance().getTimeInMillis(),"asdasd asfsdfd gdh dfh dtr hdgdfgdfhfrekla fwelkfn   f j d fj fdmbdflbm f  bfdkkfdkm kdkdf b",false));
//                    objects.add(new ChatDetailsListObject(3,1,currentUserName,Calendar.getInstance().getTimeInMillis(),"asdasd asfsdfd gdh dfh dtr hdgdfgdfhfrekla fwelkfn   f j d fj fdmbdflbm f  bfdkkfdkm kdkdf b",true));
//                    objects.add(new ChatDetailsListObject(4,1,currentUserName,Calendar.getInstance().getTimeInMillis(),"From them",false));
//                    return true;
//                }
//                currentUserName = chatUser.getChat_user_name();
//                keyBytes = chatUser.getChat_user_my_key_bytes();
//                keyIndex = chatUser.getChat_user_my_key_index();
//                objects = new ArrayList<>();
//                List<chat_message> dbMessages = DBConnector.getInstance(ChatDetailsActivity.this,StaticValues.DB_NAME)
//                        .chat_message
//                        .queryBuilder()
//                        .where(chat_messageDao.Properties.Chat_message_user_id.eq(currentTheirUserId))
//                        .list();
//                for(chat_message currentDbMessage:dbMessages){
//                    objects.add(
//                            new ChatDetailsListObject(
//                                    currentDbMessage.getChat_message_id()
//                                    ,currentDbMessage.getChat_message_user_id()
//                                    ,currentDbMessage.getChat_message_mine()?getString(R.string.you):currentUserName
//                                    ,currentDbMessage.getChat_message_time()
//                                    ,currentDbMessage.getChat_message_content()
//                                    ,currentDbMessage.getChat_message_mine()
//                            )
//                    );
//                }
//                return true;
//            }catch (Exception e){
//                e.printStackTrace();
//                return false;
//            }
//        }
//
//        @Override
//        protected void onPostExecute(Boolean result){
//            if(result){
//                recyclerView = findViewById(R.id.activity_chat_details_messages_rv);
//                listAdapter = new ChatDetailsAdapter(ChatDetailsActivity.this,objects);
//                LinearLayoutManager layoutManager = new LinearLayoutManager(ChatDetailsActivity.this);
//                recyclerView.setLayoutManager(layoutManager);
//                recyclerView.setItemAnimator(new DefaultItemAnimator());
//                VerticalDividerItemDecoration itemDecoration = new VerticalDividerItemDecoration(ChatDetailsActivity.this,android.R.color.transparent,5);
//                recyclerView.addItemDecoration(itemDecoration);
//                recyclerView.setAdapter(listAdapter);
//                recyclerView.scrollToPosition(listAdapter.getItemCount() - 1);
//                LocalBroadcastManager.getInstance(ChatDetailsActivity.this)
//                        .registerReceiver(
//                                broadcastReceiver
//                                ,new IntentFilter(StaticValues.IntentExtraValues.CHAT_DETAILS_BROADCAST_RECEIVER_FILTER)
//                        );
//                ProgressBar progressBar = findViewById(R.id.activity_chat_details_key_progress_pb);
//                progressBar.setMax(keyBytes.length);
//                progressBar.setProgress(keyIndex);
//                findViewById(R.id.activity_chat_details_send_fab).setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        String message = ((EditText)findViewById(R.id.activity_chat_details_message_et)).getText().toString();
//                        if(message.length() > 0){
//                            new SaveMyMessageTask(message).execute();
//                        }
//                    }
//                });
//            }else{
//                CustomToast.show(
//                        ChatDetailsActivity.this
//                        ,(ViewGroup)findViewById(R.id.activity_chat_details_outer_rl)
//                        ,R.color.brown
//                        ,getString(R.string.error)
//                        ,null
//                );
//            }
//        }
//    }

//    private class SaveMyMessageTask extends AsyncTask<Void,Void,Integer>{
//
//        private String messageContent;
//        private Calendar currentTime;
//        private int keyProgress = 0;
//        private int keyMax = 0;
//
//        private static final int UNKNOWN_ERROR = -1;
//        private static final int RESULT_SUCCESS = 1;
//        private static final int RESULT_NOT_ENOUGH_KEY = 2;
//
//        private SaveMyMessageTask(String message){
//            this.messageContent = message;
//        }
//
//        @Override
//        protected Integer doInBackground(Void... args){
//            HttpURLConnection connection = null;
//            int result;
//            try{
//                chat_user currentUser = DBConnector.getInstance(ChatDetailsActivity.this,StaticValues.DB_NAME)
//                        .chat_user
//                        .queryBuilder()
//                        .where(chat_userDao.Properties.Chat_user_id.eq(currentTheirUserId))
//                        .unique();
//                byte[] myKeyBytes = currentUser.getChat_user_my_key_bytes();
//                int myKeyIndex = currentUser.getChat_user_my_key_index();
//                byte[] myMessageBytes = messageContent.getBytes();
//                if(myMessageBytes.length > (myKeyBytes.length - myKeyIndex)){// not enough key
//                    result = RESULT_NOT_ENOUGH_KEY;
//                }else{
//                    byte[] keyForCurrentMessage = new byte[myMessageBytes.length];
//                    System.arraycopy(
//                            myKeyBytes
//                            ,myKeyIndex
//                            ,keyForCurrentMessage
//                            ,0
//                            ,myMessageBytes.length
//                            );
//                    myKeyIndex += myMessageBytes.length;
//                    keyProgress = myKeyIndex;
//                    keyMax = myKeyBytes.length;
//                    currentUser.setChat_user_my_key_index(myKeyIndex);
//                    DBConnector.getInstance(ChatDetailsActivity.this,StaticValues.DB_NAME)
//                            .chat_user
//                            .update(currentUser);
//                    byte[] encryptedBytes = HelperMethods.xor(keyForCurrentMessage,myMessageBytes);
//                    currentTime = Calendar.getInstance();
//                    chat_message chatMessage = new chat_message(
//                            currentTime.getTimeInMillis()
//                            , messageContent
//                            ,true
//                            ,currentTheirUserId
//                            ,currentTime.getTimeInMillis()
//                    );
//                    DBConnector.getInstance(ChatDetailsActivity.this,StaticValues.DB_NAME)
//                            .chat_message
//                            .insert(chatMessage);
//                    JSONObject jsonObject = new JSONObject();
//                    jsonObject.put(StaticValues.JSONTags.Server.MESSAGE_CONTENT,new String(encryptedBytes));
//                    jsonObject.put(StaticValues.JSONTags.Server.MESSAGE_SENDER_ID, FirebaseInstanceId.getInstance().getId());
//                    jsonObject.put(StaticValues.JSONTags.Server.MESSAGE_RECEIVER_ID, currentUser.getChat_user_their_instance_id());
//                    jsonObject.put(StaticValues.JSONTags.Server.MESSAGE_REAL_CONTENT, new String(myMessageBytes));
//                    byte[] toSend = Base64.encodeBase64(jsonObject.toString().getBytes("UTF-8"));
//                    connection = (HttpURLConnection) new URL(PreferenceManager.getDefaultSharedPreferences(ChatDetailsActivity.this).getString(getString(R.string.pref_server_address_key),"")).openConnection();
//                    connection.setInstanceFollowRedirects(false);
//                    connection.setDoOutput(true);
//                    connection.setRequestMethod("POST");
//                    connection.setRequestProperty(StaticValues.ServerValues.REQUEST_HEADER_COMMAND,String.valueOf(StaticValues.ServerValues.SERVER_COMMAND_NEW_MESSAGE));
//                    connection.setRequestProperty("Content-length",String.valueOf(toSend.length));
//                    connection.setFixedLengthStreamingMode(toSend.length);
//                    connection.connect();
//                    OutputStream outputStream = connection.getOutputStream();
//                    outputStream.write(toSend);
//                    outputStream.flush();
//                    if(connection.getResponseCode() == 204){
//                        result = RESULT_SUCCESS;
//                    }else{
//                        result = UNKNOWN_ERROR;
//                    }
//                }
//            }catch (Exception e){
//                e.printStackTrace();
//                result = UNKNOWN_ERROR;
//            }finally {
//                if(connection != null){
//                    connection.disconnect();
//                }
//            }
//            return result;
//        }
//
//        @Override
//        protected void onPostExecute(Integer result){
//            switch (result){
//                case RESULT_SUCCESS:
//                    ((EditText)findViewById(R.id.activity_chat_details_message_et)).setText("");
//                    listAdapter.addObject(
//                            new ChatDetailsListObject(
//                                    currentTime.getTimeInMillis()
//                                    ,currentTheirUserId
//                                    ,getString(R.string.you)
//                                    ,currentTime.getTimeInMillis()
//                                    , messageContent
//                                    ,true
//                            )
//                    );
//                    ProgressBar progressBar = (ProgressBar) findViewById(R.id.activity_chat_details_key_progress_pb);
//                    progressBar.setMax(keyMax);
//                    progressBar.setProgress(keyProgress);
//                    listAdapter.notifyDataSetChanged();
//                    recyclerView.smoothScrollToPosition(listAdapter.getItemCount() - 1);
//                    break;
//                case RESULT_NOT_ENOUGH_KEY:
//                    CustomToast.show(
//                            ChatDetailsActivity.this
//                            ,(ViewGroup)findViewById(R.id.activity_chat_details_outer_rl)
//                            ,R.color.brown
//                            ,getString(R.string.not_enough_key_error)
//                            ,null
//                    );
//                    findViewById(R.id.activity_chat_details_key_progress_pb).startAnimation(AnimationUtils.loadAnimation(ChatDetailsActivity.this,R.anim.shake_anim));
//                    break;
//                case UNKNOWN_ERROR:
//                    CustomToast.show(
//                            ChatDetailsActivity.this
//                            ,(ViewGroup)findViewById(R.id.activity_chat_details_outer_rl)
//                            ,R.color.brown
//                            ,getString(R.string.error)
//                            ,null
//                    );
//                    break;
//            }
//        }
//    }

//    private class ShowTheirMessageTask extends AsyncTask<Void,Void,Boolean>{
//
//        private long messageId;
//        private ChatDetailsListObject messageObject;
//
//        private ShowTheirMessageTask(long messageId){
//            this.messageId = messageId;
//        }
//
//        @Override
//        protected Boolean doInBackground(Void... args){
//            try{
//                chat_message receivedMessage = DBConnector.getInstance(ChatDetailsActivity.this,StaticValues.DB_NAME)
//                        .chat_message
//                        .queryBuilder()
//                        .where(chat_messageDao.Properties.Chat_message_id.eq(messageId))
//                        .unique();
//                if(receivedMessage == null){
//                    return false;
//                }
//                if(receivedMessage.getChat_message_user_id() != currentTheirUserId){
//                    // TODO: 19/04/2017 MAYBE SHOW A NOTIFICATION WITH A LINK TO THE OTHER CHAT?
//                    return false;// not mine
//                }
//                messageObject = new ChatDetailsListObject(
//                        receivedMessage.getChat_message_id()
//                        ,receivedMessage.getChat_message_user_id()
//                        ,currentUserName
//                        ,receivedMessage.getChat_message_time()
//                        ,receivedMessage.getChat_message_content()
//                        ,receivedMessage.getChat_message_mine()
//                );
//                return true;
//            }catch (Exception e){
//                e.printStackTrace();
//                return false;
//            }
//        }
//
//        @Override
//        protected void onPostExecute(Boolean result){
//            if(result){
//                if(messageObject != null){
//                    listAdapter.addObject(messageObject);
//                    listAdapter.notifyDataSetChanged();
//                    recyclerView.smoothScrollToPosition(listAdapter.getItemCount() - 1);
//                }
//            }
//        }
//    }
}
