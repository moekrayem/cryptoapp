package com.thekrayem.cryptoapp.chat.creation.my;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.google.firebase.iid.FirebaseInstanceId;
import com.thekrayem.cryptoapp.boxdb.handler.DatabaseHandler;
import com.thekrayem.cryptoapp.boxdb.models.ChatUserRecord;
import com.thekrayem.cryptoapp.helper.App;
import com.thekrayem.cryptoapp.helper.HelperMethods;
import com.thekrayem.cryptoapp.helper.StaticValues;
import com.thekrayem.cryptoapp.mvpbase.BasePresenter;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.security.SecureRandom;

import javax.inject.Inject;

public class MyKeyPresenter extends BasePresenter<MyKeyContract.View> implements MyKeyContract.Presenter {

    @Inject
    DatabaseHandler databaseHandler;

    @Inject
    public MyKeyPresenter() {
        App.getInstance().getAppComponent().inject(this);

    }

    @Override
    public void checkForExistingKey(long userId) {
        new CheckForExistingKeyTask(getView(), databaseHandler, userId).execute();
    }

    @Override
    public void generateKey(long userId) {
        new GenerateMyKeyTask(getView(),databaseHandler,userId).execute();

    }

    private static class CheckForExistingKeyTask extends AsyncTask<Void, Void, Void> {

        private Bitmap bitmap;
        private WeakReference<MyKeyContract.View> viewWeakReference;
        private DatabaseHandler databaseHandler;
        private long userId;

        private CheckForExistingKeyTask(MyKeyContract.View view, DatabaseHandler databaseHandler, long userId) {
            viewWeakReference = new WeakReference<>(view);
            this.databaseHandler = databaseHandler;
            this.userId = userId;
        }

        @Override
        protected void onPreExecute() {
            MyKeyContract.View view = viewWeakReference.get();
            if (view != null) {
                view.setLoading(true, MyKeyContract.View.LoadingType.CHECKING);
            }
        }

        @Override
        protected Void doInBackground(Void... args) {
            try {
                ChatUserRecord chatUser = databaseHandler.getChatHandler().getUser(userId);
                if (chatUser == null) {
                    return null;
                }
                byte[] savedKey = chatUser.getMyKey();
                if (savedKey.length == 0) {
                    return null;
                }
                String keyString = new String(savedKey);
                JSONObject bitmapJsonObject = new JSONObject();
                bitmapJsonObject.put(StaticValues.JSONTags.KeyBitmap.ID, FirebaseInstanceId.getInstance().getId());
                bitmapJsonObject.put(StaticValues.JSONTags.KeyBitmap.KEY, keyString);
                bitmap = HelperMethods.encodeAsBitmap(bitmapJsonObject.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void arg) {
            MyKeyContract.View view = viewWeakReference.get();
            if (view != null) {
                view.setLoading(false, null);
                view.onKeyChecked(bitmap);
            }

        }
    }

    public static class GenerateMyKeyTask extends AsyncTask<Void, Void, Integer> {

        public static final int UNKNOWN_ERROR = -1;
        public static final int KEY_CREATED = 1;
        public static final int USER_DOESNT_EXIST = 2;
        public static final int KEY_EXISTS_AND_USED = 3;

        private byte[] myKey = null;
        private Bitmap bitmap = null;

        private WeakReference<MyKeyContract.View> viewWeakReference;
        private DatabaseHandler databaseHandler;
        private long userId;

        private GenerateMyKeyTask(MyKeyContract.View view, DatabaseHandler databaseHandler, long userId) {
            viewWeakReference = new WeakReference<>(view);
            this.databaseHandler = databaseHandler;
            this.userId = userId;
        }

        @Override
        protected void onPreExecute() {
            MyKeyContract.View view = viewWeakReference.get();
            if (view != null) {
                view.setLoading(true, MyKeyContract.View.LoadingType.GENERATING);
            }
        }

        @Override
        protected Integer doInBackground(Void... args) {
            try {
                ChatUserRecord chatUser = databaseHandler.getChatHandler().getUser(userId);
                if (chatUser == null) {
                    return USER_DOESNT_EXIST;
                }
                if (chatUser.getMyIndex() != 0) {
                    JSONObject bitmapJsonObject = new JSONObject();
                    bitmapJsonObject.put(StaticValues.JSONTags.KeyBitmap.ID, FirebaseInstanceId.getInstance().getId());
                    bitmapJsonObject.put(StaticValues.JSONTags.KeyBitmap.KEY, new String(chatUser.getMyKey()));
                    bitmap = HelperMethods.encodeAsBitmap(bitmapJsonObject.toString());
                    return KEY_EXISTS_AND_USED;

                }
                SecureRandom secureRandom = new SecureRandom();
                myKey = new byte[StaticValues.DEFAULT_CHAT_KEY_LENGTH];
                secureRandom.nextBytes(myKey);
                chatUser.setMyKey(myKey);
                databaseHandler.getChatHandler().updateUser(chatUser);
                String keyString = new String(myKey);
                JSONObject bitmapJsonObject = new JSONObject();
                bitmapJsonObject.put(StaticValues.JSONTags.KeyBitmap.ID, FirebaseInstanceId.getInstance().getId());
                bitmapJsonObject.put(StaticValues.JSONTags.KeyBitmap.KEY, keyString);
                bitmap = HelperMethods.encodeAsBitmap(bitmapJsonObject.toString());
                return KEY_CREATED;
            } catch (Exception e) {
                e.printStackTrace();
                return UNKNOWN_ERROR;
            }
        }

        @Override
        protected void onPostExecute(Integer result) {
            MyKeyContract.View view = viewWeakReference.get();
            if (view != null) {
                view.setLoading(false, null);
                view.onKeyCreated(result, bitmap);
            }
        }
    }

}
