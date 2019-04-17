package com.thekrayem.cryptoapp.chat.creation;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.thekrayem.cryptoapp.R;
import com.thekrayem.cryptoapp.db.DBConnector;
import com.thekrayem.cryptoapp.db.chat_user;
import com.thekrayem.cryptoapp.db.chat_userDao;
import com.thekrayem.cryptoapp.helper.CustomToast;
import com.thekrayem.cryptoapp.helper.StaticValues;

import java.util.Calendar;


public class CreateNewChatSetupFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_create_new_chat_setup, container, false);
        final CreateNewChatActivity activity = (CreateNewChatActivity)getActivity();
        final EditText userNameEt = (EditText)view.findViewById(R.id.fragment_create_new_chat_user_setup_name_et);
        userNameEt.setText(activity.userName != null? activity.userName:"");
        view.findViewById(R.id.fragment_create_new_chat_user_setup_save_bu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newName = userNameEt.getText().toString();
                if(newName.length() != 0){
                    new SaveUserNameTask(newName).execute();
                }else{
                    CustomToast.show(
                            getContext()
                            ,(ViewGroup)view.findViewById(R.id.fragment_create_new_chat_setup_outer_rl)
                            ,R.color.brown
                            ,getString(R.string.no_valid_name)
                            ,null
                    );
                }
            }
        });
        return view;
    }

    private class SaveUserNameTask extends AsyncTask<Void,Void,Boolean>{

        private String newName;

        private SaveUserNameTask(String newName){
            this.newName = newName;
        }

        @Override
        protected Boolean doInBackground(Void... args){
            try{
                CreateNewChatActivity activity = (CreateNewChatActivity)getActivity();
                if(activity.chatUserId == -1L){
                    activity.chatUserId = Calendar.getInstance().getTimeInMillis();
                }
                chat_user chatUser = DBConnector.getInstance(getContext(),StaticValues.DB_NAME)
                        .chat_user
                        .queryBuilder()
                        .where(chat_userDao.Properties.Chat_user_id.eq(activity.chatUserId))
                        .unique();
                if(chatUser == null){
                    chatUser = new chat_user(
                            activity.chatUserId
                            ,""
                            ,newName
                            ,new byte[0]
                            ,new byte[0]
                            ,0
                            ,0
                    );
                }else{
                    chatUser.setChat_user_name(newName);
                }
                DBConnector.getInstance(getContext(), StaticValues.DB_NAME)
                        .chat_user
                        .insertOrReplace(chatUser);
                activity.userName = newName;
                return true;
            }catch (Exception e){
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result){
            View view = getView();
            if(view != null){
                CustomToast.show(
                        getContext()
                        ,(ViewGroup)view.findViewById(R.id.fragment_create_new_chat_setup_outer_rl)
                        ,result?R.color.teal:R.color.brown
                        ,result?getString(R.string.name_saved):getString(R.string.error)
                        ,null
                );
            }
        }
    }
}
