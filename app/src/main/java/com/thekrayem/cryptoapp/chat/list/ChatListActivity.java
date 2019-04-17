package com.thekrayem.cryptoapp.chat.list;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.thekrayem.cryptoapp.R;
import com.thekrayem.cryptoapp.chat.creation.CreateNewChatActivity;
import com.thekrayem.cryptoapp.chat.list.recyclerview.ChatListAdapter;
import com.thekrayem.cryptoapp.helper.HelperMethods;
import com.thekrayem.cryptoapp.helper.StaticValues;
import com.thekrayem.cryptoapp.mvpbase.BaseActivity;

import java.util.ArrayList;
import java.util.List;

public class ChatListActivity extends BaseActivity implements ChatListContract.View {

    private ChatListAdapter listAdapter;
    private ChatListContract.Presenter presenter;

    @Override
    protected int getContentResource() {
        return R.layout.activity_chat_list;
    }

    @Override
    protected void init(@Nullable Bundle savedInstanceState) {
        presenter = new ChatListPresenter();
        presenter.attach(this);
        RecyclerView recyclerView = findViewById(R.id.activity_chat_list_rv);
        registerForContextMenu(recyclerView);
        listAdapter = new ChatListAdapter(ChatListActivity.this,new ArrayList<ChatListObject>());
        recyclerView.setAdapter(listAdapter);
        presenter.loadChats();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detach();
        unregisterForContextMenu(findViewById(R.id.activity_chat_list_rv));
    }

    @Override
    public void onListLoaded(List<ChatListObject> objects) {
        manageVisibility(objects.size() == 0);
        listAdapter.setObjects(objects);
        listAdapter.notifyDataSetChanged();
    }

    private void manageVisibility(boolean hide){
        if(hide){
            findViewById(R.id.activity_chat_list_rv).setVisibility(View.GONE);
            findViewById(R.id.activity_chat_list_no_chats_tv).setVisibility(View.VISIBLE);
        }else{
            findViewById(R.id.activity_chat_list_rv).setVisibility(View.VISIBLE);
            findViewById(R.id.activity_chat_list_no_chats_tv).setVisibility(View.GONE);
        }
    }

    @Override
    public void onError(ChatListContract.ErrorType errorType) {
        switch (errorType){
            case CANT_LOAD_CHATS:
                manageVisibility(true);
                Snackbar.make(findViewById(R.id.chat_list_item_root_cl),getString(R.string.error),Snackbar.LENGTH_LONG).show();
                break;
        }
    }

//    private class GetChatUsersTask extends AsyncTask<Void,Void,Boolean>{
//
//        private List<ChatListObject> objects = null;
//
//        @Override
//        protected Boolean doInBackground(Void... args){
//            try{
//                objects = new ArrayList<>();
//                List<chat_user> chat_users = DBConnector.getInstance(ChatListActivity.this,StaticValues.DB_NAME)
//                        .chat_user
//                        .loadAll();
//                for(chat_user currentUser : chat_users){
//                    String lastMessageContent = "";
//                    long lastMessageDate = 0;
//                    chat_message lastMessage = DBConnector.getInstance(ChatListActivity.this,StaticValues.DB_NAME)
//                            .chat_message
//                            .queryBuilder()
//                            .where(chat_messageDao.Properties.Chat_message_user_id.eq(currentUser.getChat_user_id()))
//                            .orderDesc(chat_messageDao.Properties.Chat_message_time)
//                            .limit(1)
//                            .unique();
//                    if(lastMessage != null){
//                        lastMessageContent = lastMessage.getChat_message_content();
//                        lastMessageDate = lastMessage.getChat_message_time();
//                    }
//                    objects.add(
//                            new ChatListObject(
//                                    currentUser.getChat_user_id()
//                                    ,currentUser.getChat_user_name()
//                                    ,lastMessageContent
//                                    ,lastMessageDate
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
//
//            }else{
//                CustomToast.show(
//                        ChatListActivity.this
//                        ,(ViewGroup)findViewById(R.id.activity_chat_list_outer_rl)
//                        ,R.color.brown
//                        ,getString(R.string.error)
//                        ,null
//                );
//            }
//
//        }
//    }

//    private class DeleteChatTask extends AsyncTask<Void,Void,Boolean>{
//
//        private long userId = -1;
//        private int position;
//
//
//        private DeleteChatTask(int position){
//            this.position = position;
//        }
//
//        @Override
//        protected void onPreExecute(){
//            ChatListObject object = listAdapter.getItem(position);
//            if(object != null){
//                userId = object.getUserID();
//            }
//        }
//
//        @Override
//        protected Boolean doInBackground(Void... args){
//            try{
//                if(userId == -1){
//                    return false;
//                }
//                chat_user user = DBConnector.getInstance(ChatListActivity.this,StaticValues.DB_NAME)
//                        .chat_user
//                        .queryBuilder()
//                        .where(chat_userDao.Properties.Chat_user_id.eq(userId))
//                        .unique();
//                List<chat_message> messages = DBConnector.getInstance(ChatListActivity.this, StaticValues.DB_NAME)
//                        .chat_message
//                        .queryBuilder()
//                        .where(chat_messageDao.Properties.Chat_message_user_id.eq(userId))
//                        .list();
//                DBConnector.getInstance(ChatListActivity.this,StaticValues.DB_NAME)
//                        .chat_user
//                        .delete(user);
//                DBConnector.getInstance(ChatListActivity.this,StaticValues.DB_NAME)
//                        .chat_message
//                        .deleteInTx(messages);
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
//                listAdapter.deleteItemByPosition(position);
//                listAdapter.notifyDataSetChanged();
//            }else{
//                CustomToast.show(
//                        ChatListActivity.this
//                        ,(ViewGroup)findViewById(R.id.activity_chat_list_outer_rl)
//                        ,R.color.brown
//                        ,getString(R.string.error)
//                        ,null
//                );
//            }
//        }
//
//    }

//    private class ChatListAdapter extends ArrayAdapter<ChatListObject> {
//
//        private Context context;
//        private int resId;
//        private List<ChatListObject> objects;
//
//        private ChatListAdapter(Context context, int resId, List<ChatListObject> objects){
//            super(context,resId,objects);
//            this.context=context;
//            this.resId=resId;
//            this.objects=objects;
//        }
//
//        class ViewHolder{
//            TextView messageContenttv;
//            TextView messageUsertv;
//            TextView messageTimetv;
//        }
//
//        private void deleteItemByPosition(int position){
//            if(objects != null){
//                objects.remove(position);
//            }
//        }
//
//        @Override
//        public ChatListObject getItem(int position){
//            if(objects != null && position >= 0 && objects.size()>position){
//                return objects.get(position);
//            }else{
//                return null;
//            }
//        }
//
//
//        @Override
//        @NonNull
//        public View getView(int position, View convertView, @NonNull ViewGroup parent){
//            ChatListObject currentObject=objects.get(position);
//            ViewHolder viewHolder;
//
//            if(convertView==null){
//                convertView=((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(resId,null);
//                viewHolder=new ViewHolder();
//                viewHolder.messageContenttv=(TextView)convertView.findViewById(R.id.chat_list_item_last_message_tv);
//                viewHolder.messageTimetv=(TextView)convertView.findViewById(R.id.chat_list_item_date_tv);
//                viewHolder.messageUsertv=(TextView)convertView.findViewById(R.id.chat_list_item_name_tv);
//                convertView.setTag(viewHolder);
//            }else{
//                viewHolder=(ViewHolder)convertView.getTag();
//            }
//
//            viewHolder.messageContenttv.setText(currentObject.getLastMessageContent());
//            viewHolder.messageUsertv.setText(currentObject.getUserName());
//            viewHolder.messageUsertv.setTag(currentObject.getUserID());
//            viewHolder.messageTimetv.setText(DateFormat.format("dd-MM-yy (HH:mm:ss)", currentObject.getLastMessageDate()));
//            return convertView;
//        }
//
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.default_empty_menu, menu);
        HelperMethods.addToActionBar(
                menu
                , getString(R.string.create_chat)
                , R.drawable.ic_add
        );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case 0:
                startActivityForResult(
                        new Intent(this,CreateNewChatActivity.class)
                        , StaticValues.RequestCodes.CREATE_NEW_CHAT_REQUEST_CODE
                );
                return true;
            default:
                return false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case StaticValues.RequestCodes.CREATE_NEW_CHAT_REQUEST_CODE:
                if(resultCode == RESULT_OK || presenter != null){
                    presenter.loadChats();
                }
                break;
            case StaticValues.RequestCodes.ENCRYPT_CHAT_REQUEST_CODE:
                if(resultCode == RESULT_OK || presenter != null){
                    presenter.loadChats();
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.chat_list_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
//        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.chat_list_context_menu_encrypt_delete_item:
                try{
//                    ChatListObject object = listAdapter.getItem(info.position);
//                    if(object == null){
//                        CustomToast.show(
//                                ChatListActivity.this
//                                ,(ViewGroup)findViewById(R.id.activity_chat_list_outer_rl)
//                                ,R.color.brown
//                                ,getString(R.string.error)
//                                ,null
//                        );
//                        return false;
//                    }
//                    Intent intent = new Intent(this,EncryptFileActivity.class)
//                            .putExtra(
//                                    StaticValues.IntentExtraValues.ENCRYPT_CHAT_USER_ID_INTENT_EXTRA_VALUE
//                                    ,object.getUserID()
//                            );
//                    startActivityForResult(
//                            intent
//                            ,StaticValues.RequestCodes.ENCRYPT_CHAT_REQUEST_CODE
//                    );
                    return true;
                }catch (Exception e){
                    e.printStackTrace();
                    return false;
                }
            case R.id.chat_list_context_menu_delete_item:
//                new DeleteChatTask(info.position).execute();
                return true;
            case R.id.chat_list_context_menu_edit_item:
//                ChatListObject object = listAdapter.getItem(info.position);
//                if(object == null){
//                    return false;
//                }
//
//                Intent intent = new Intent(this,CreateNewChatActivity.class)
//                        .putExtra(
//                                StaticValues.IntentExtraValues.CHAT_USER_ID_INTENT_EXTRA_VALUE
//                                ,object.getUserID())
//                        .putExtra(
//                                StaticValues.IntentExtraValues.USER_NAME_INTENT_EXTRA_VALUE
//                                ,object.getUserName()
//                        );
//                startActivityForResult(
//                        intent
//                        ,StaticValues.RequestCodes.CREATE_NEW_CHAT_REQUEST_CODE
//                );
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
}
