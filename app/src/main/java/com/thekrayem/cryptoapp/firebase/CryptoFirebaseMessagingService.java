package com.thekrayem.cryptoapp.firebase;


import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.thekrayem.cryptoapp.R;
import com.thekrayem.cryptoapp.db.DBConnector;
import com.thekrayem.cryptoapp.db.chat_message;
import com.thekrayem.cryptoapp.db.chat_user;
import com.thekrayem.cryptoapp.db.chat_userDao;
import com.thekrayem.cryptoapp.helper.HelperMethods;
import com.thekrayem.cryptoapp.helper.StaticValues;

import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Map;

import customlib.org.apache.commons.codec.binary.Base64;

public class CryptoFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onNewToken(String token) {
        HttpURLConnection connection = null;
        try {
            // send new token to server
            JSONObject jsonObject = new JSONObject();
            // instance_id (device identifier?)
            jsonObject.put(StaticValues.JSONTags.Firebase.INSTANCE_ID_FROM, FirebaseInstanceId.getInstance().getId());
            // new token, this can change
            jsonObject.put(StaticValues.JSONTags.Firebase.TOKEN_FROM, FirebaseInstanceId.getInstance().getToken());
            byte[] toSend = Base64.encodeBase64(jsonObject.toString().getBytes(StandardCharsets.UTF_8));
            connection = (HttpURLConnection) new URL(PreferenceManager.getDefaultSharedPreferences(this).getString(getString(R.string.pref_server_address_key), "")).openConnection();
            connection.setInstanceFollowRedirects(false);
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty(StaticValues.ServerValues.REQUEST_HEADER_COMMAND, String.valueOf(StaticValues.ServerValues.SERVER_COMMAND_NEW_TOKEN));
            connection.setRequestProperty("Content-length", String.valueOf(toSend.length));
            connection.setFixedLengthStreamingMode(toSend.length);
            connection.connect();
            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(toSend);
            outputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage){
        try{
            Map<String,String> data = remoteMessage.getData();
            switch (Integer.parseInt(data.get(StaticValues.JSONTags.Server.COMMAND))){

                case StaticValues.ServerValues.SERVER_REPLY_NEW_MESSAGE:
                    String senderInstanceId =  data.get(StaticValues.JSONTags.Server.MESSAGE_SENDER_ID);
                    String encryptedMessageContent =  data.get(StaticValues.JSONTags.Server.MESSAGE_CONTENT);
                    byte[] encryptedBytes = encryptedMessageContent.getBytes(StandardCharsets.UTF_8);
                    chat_user sendingUser = DBConnector.getInstance(this,StaticValues.DB_NAME)
                            .chat_user
                            .queryBuilder()
                            .where(chat_userDao.Properties.Chat_user_their_instance_id.eq(senderInstanceId))
                            .unique();
                    if(sendingUser == null){
                        return;
                    }
                    if(encryptedBytes.length > sendingUser.getChat_user_their_key_bytes().length - sendingUser.getChat_user_their_key_index()){
                        //more key added on the other side?
                        return;
                    }
                    byte[] keyForThisMessage = new byte[encryptedBytes.length];
                    System.arraycopy(
                            sendingUser.getChat_user_their_key_bytes() // source
                            ,sendingUser.getChat_user_their_key_index() // source index
                            ,keyForThisMessage // destination
                            ,0 // destination index
                            ,encryptedBytes.length // length
                    );
                    String decryptedMessage = new String(HelperMethods.xor(keyForThisMessage,encryptedBytes), StandardCharsets.UTF_8);
                    String realMessage =  data.get(StaticValues.JSONTags.Server.MESSAGE_REAL_CONTENT);
                    sendingUser.setChat_user_their_key_index(sendingUser.getChat_user_their_key_index()+encryptedBytes.length);
                    DBConnector.getInstance(this,StaticValues.DB_NAME)
                            .chat_user
                            .update(sendingUser);
                    Calendar currentTime = Calendar.getInstance();
                    chat_message newChatMessage = new chat_message(
                            currentTime.getTimeInMillis()
                            ,realMessage
                            ,false
                            ,sendingUser.getChat_user_id()
                            ,currentTime.getTimeInMillis()
                    );
                    DBConnector.getInstance(this,StaticValues.DB_NAME)
                            .chat_message
                            .insert(newChatMessage);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(
                            new Intent(StaticValues.IntentExtraValues.CHAT_DETAILS_BROADCAST_RECEIVER_FILTER)
                                    .putExtra(StaticValues.IntentExtraValues.CHAT_DETAILS_BROADCAST_RECEIVER_COMMAND,StaticValues.IntentExtraValues.CHAT_DETAILS_BROADCAST_RECEIVER_NEW_MESSAGE)
                                    .putExtra(StaticValues.IntentExtraValues.CHAT_DETAILS_BROADCAST_RECEIVER_MESSAGE_ID,newChatMessage.getChat_message_id()
                                    )
                    );
                    break;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
