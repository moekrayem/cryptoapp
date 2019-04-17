package com.thekrayem.cryptoapp.preferences;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.thekrayem.cryptoapp.R;
import com.thekrayem.cryptoapp.helper.StaticValues;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.spongycastle.util.encoders.Base64Encoder;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;

import customlib.org.apache.commons.codec.binary.Base64;


public class EncryptionPreferenceFragment extends PreferenceFragment {

    private boolean taskRunning = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.encryption_preference);

        try{
            Preference preference = findPreference(getString(R.string.pref_firebase_token_gen_key));
            String summary = FirebaseInstanceId.getInstance().getToken();
            int iconId = R.drawable.icon_check_teal;
            if(summary == null || "".equals(summary)){
                summary = getString(R.string.pref_firebase_token_gen_summary_placeholder);
                iconId = R.drawable.icon_cancel_brown;
            }else{
                summary = getString(R.string.token_starts_with) + " " + summary.substring(0,5);
                summary += "\n" + getString(R.string.pref_firebase_token_gen_summary_send_to_server);
                preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        if(!taskRunning){
                            taskRunning = true;
                            new SendTokenToServerTask().execute();
                        }
                        return true;
                    }
                });
            }
            preference.setSummary(summary);
            preference.setIcon(iconId);
        }catch (Exception e){
            e.printStackTrace();
        }

        findPreference(getString(R.string.pref_server_test_key)).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if(!taskRunning){
                    taskRunning = true;
                    new CheckServerTask().execute();
                }
                return true;
            }
        });
        findPreference(getString(R.string.pref_firebase_token_status_key)).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if(!taskRunning){
                    taskRunning = true;
                    new CheckTokenTask().execute();
                }
                return true;
            }
        });
    }

    private class CheckServerTask extends AsyncTask<Void,Void,Boolean>{

        @Override
        protected Boolean doInBackground(Void... args){
            boolean result = false;
            HttpURLConnection connection = null;
            Context context = getActivity();
            try{
                connection = (HttpURLConnection) new URL(PreferenceManager.getDefaultSharedPreferences(context).getString(getString(R.string.pref_server_address_key),"")).openConnection();
                connection.setInstanceFollowRedirects(false);
                connection.setRequestMethod("POST");
                connection.setRequestProperty(StaticValues.ServerValues.REQUEST_HEADER_COMMAND,String.valueOf(StaticValues.ServerValues.SERVER_COMMAND_TEST));
                connection.connect();
                if(connection.getResponseCode() == 204){
                    result = "1".equals(connection.getHeaderField(StaticValues.ServerValues.RESPONSE_HEADER_RESULT_KEY));
                }
            }catch (Exception e){
                e.printStackTrace();
                result = false;
            }finally {
                if(connection != null){
                    connection.disconnect();
                }
            }
            return result;
        }
        @Override
        protected void onPostExecute(Boolean result){
            taskRunning = false;
            Preference preference = findPreference(getString(R.string.pref_server_test_key));
            preference.setIcon(result?R.drawable.icon_check_teal_small:R.drawable.icon_cancel_brown_small);
            preference.setSummary(result?R.string.server_reachable:R.string.server_unreachable);

        }
    }

    private class CheckTokenTask extends AsyncTask<Void,Void,Boolean>{

        @Override
        protected Boolean doInBackground(Void... args){
            boolean result = false;
            HttpURLConnection connection = null;
            Context context = getActivity();
            try{
                JSONObject jsonObject = new JSONObject();
                // instance_id (device identifier?)
                jsonObject.put(StaticValues.JSONTags.Firebase.INSTANCE_ID_FROM, FirebaseInstanceId.getInstance().getId());
                // new token, this can change
                jsonObject.put(StaticValues.JSONTags.Firebase.TOKEN_FROM, FirebaseInstanceId.getInstance().getToken());
                byte[] toSend = Base64.encodeBase64(jsonObject.toString().getBytes("UTF-8"));
                connection = (HttpURLConnection) new URL(PreferenceManager.getDefaultSharedPreferences(context).getString(getString(R.string.pref_server_address_key),"")).openConnection();
                connection.setInstanceFollowRedirects(false);
                connection.setRequestMethod("POST");
                connection.setDoInput(true);
                connection.setFixedLengthStreamingMode(toSend.length);
                connection.setRequestProperty("Content-length",String.valueOf(toSend.length));
                connection.setRequestProperty(StaticValues.ServerValues.REQUEST_HEADER_COMMAND,String.valueOf(StaticValues.ServerValues.SERVER_COMMAND_CHECK_TOKEN));
                connection.connect();
                OutputStream outputStream = connection.getOutputStream();
                outputStream.write(toSend);
                outputStream.flush();
                if(connection.getResponseCode() == 204){
                    result = "1".equals(connection.getHeaderField(StaticValues.ServerValues.RESPONSE_HEADER_RESULT_KEY));
                }
            }catch (Exception e){
                e.printStackTrace();
                result = false;
            }finally {
                if(connection != null){
                    connection.disconnect();
                }
            }
            return result;
        }
        @Override
        protected void onPostExecute(Boolean result){
            taskRunning = false;
            Preference preference = findPreference(getString(R.string.pref_firebase_token_status_key));
            preference.setIcon(result?R.drawable.icon_check_teal_small:R.drawable.icon_cancel_brown_small);
            preference.setSummary(result?R.string.pref_firebase_token_status_positive_summary:R.string.pref_firebase_token_status_negative_summary);
        }
    }

    private class SendTokenToServerTask extends AsyncTask<Void,Void,Boolean>{

        @Override
        protected Boolean doInBackground(Void... args){
            boolean result = false;
            HttpURLConnection connection = null;
            Context context = getActivity();
            try{
                // send new token to server
                JSONObject jsonObject = new JSONObject();
                // instance_id (device identifier?)
                jsonObject.put(StaticValues.JSONTags.Firebase.INSTANCE_ID_FROM, FirebaseInstanceId.getInstance().getId());
                // new token, this can change
                jsonObject.put(StaticValues.JSONTags.Firebase.TOKEN_FROM, FirebaseInstanceId.getInstance().getToken());
                byte[] toSend = Base64.encodeBase64(jsonObject.toString().getBytes("UTF-8"));
                connection = (HttpURLConnection) new URL(PreferenceManager.getDefaultSharedPreferences(context).getString(getString(R.string.pref_server_address_key),"")).openConnection();
                connection.setInstanceFollowRedirects(false);
                connection.setDoOutput(true);
                connection.setRequestMethod("POST");
                connection.setRequestProperty(StaticValues.ServerValues.REQUEST_HEADER_COMMAND,String.valueOf(StaticValues.ServerValues.SERVER_COMMAND_NEW_TOKEN));
                connection.setRequestProperty("Content-length",String.valueOf(toSend.length));
                connection.setFixedLengthStreamingMode(toSend.length);
                connection.connect();
                OutputStream outputStream = connection.getOutputStream();
                outputStream.write(toSend);
                outputStream.flush();
                if(connection.getResponseCode() == 204){
                    result = "1".equals(connection.getHeaderField(StaticValues.ServerValues.RESPONSE_HEADER_RESULT_KEY));
                }
            }catch (Exception e){
                e.printStackTrace();
                result = false;
            }finally {
                if(connection != null){
                    connection.disconnect();
                }
            }
            return result;
        }

        @Override
        protected void onPostExecute(Boolean result){
            taskRunning = false;
            Toast.makeText(getActivity(),result?getString(R.string.token_sent_to_server):getString(R.string.error),Toast.LENGTH_SHORT).show();
            Preference preference = findPreference(getString(R.string.pref_firebase_token_status_key));
            preference.setIcon(result?R.drawable.icon_check_teal:R.drawable.icon_cancel_brown);
            preference.setSummary(result?R.string.pref_firebase_token_status_positive_summary:R.string.pref_firebase_token_status_negative_summary);
        }
    }

}
