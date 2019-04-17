package com.thekrayem.cryptoapp.chat.creation.their;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.thekrayem.cryptoapp.R;
import com.thekrayem.cryptoapp.boxdb.handler.DatabaseHandler;
import com.thekrayem.cryptoapp.boxdb.models.ChatUserRecord;
import com.thekrayem.cryptoapp.chat.creation.CreateNewChatActivity;
import com.thekrayem.cryptoapp.db.DBConnector;
import com.thekrayem.cryptoapp.db.chat_user;
import com.thekrayem.cryptoapp.db.chat_userDao;
import com.thekrayem.cryptoapp.helper.App;
import com.thekrayem.cryptoapp.helper.CustomToast;
import com.thekrayem.cryptoapp.helper.StaticValues;
import com.thekrayem.cryptoapp.mvpbase.BasePresenter;

import org.json.JSONObject;

import java.lang.ref.WeakReference;

import javax.inject.Inject;

public class TheirKeyPresenter extends BasePresenter<TheirKeyContract.View> implements TheirKeyContract.Presenter {

    @Inject
    DatabaseHandler databaseHandler;

    @Inject
    Context context;

    @Inject
    public TheirKeyPresenter() {
        App.getInstance().getAppComponent().inject(this);
    }

    @Override
    public void detectBitmap(Bitmap bitmap, long userId) {
        new DetectBitmapTask(getView(), context, databaseHandler, bitmap, userId).execute();
    }

    public static class DetectBitmapTask extends AsyncTask<Void, Void, Integer> {

        public static final int UNKNOWN_ERROR = -1;
        public static final int KEY_SAVED = 1;
        public static final int USER_DOESNT_EXIST = 2;
        public static final int KEY_EXISTS_AND_USED = 3;
        public static final int NO_KEY_FOUND = 4;

        private Bitmap bitmap;
        private WeakReference<TheirKeyContract.View> viewWeakReference;
        private WeakReference<Context> contextWeakReference;
        private DatabaseHandler databaseHandler;
        private long userId;

        private DetectBitmapTask(TheirKeyContract.View view, Context context, DatabaseHandler databaseHandler, Bitmap bitmap, long userId) {
            viewWeakReference = new WeakReference<>(view);
            this.bitmap = bitmap;
            this.databaseHandler = databaseHandler;
            contextWeakReference = new WeakReference<>(context);
            this.userId = userId;
        }

        @Override
        protected Integer doInBackground(Void... args) {
            try {
                if (bitmap == null) {
                    return UNKNOWN_ERROR;
                }
                ChatUserRecord chatUser = databaseHandler.getChatHandler().getUser(userId);
                if (chatUser == null) {
                    return USER_DOESNT_EXIST;
                }
                if (chatUser.getTheirIndex() != 0) {
                    return KEY_EXISTS_AND_USED;
                }
                BarcodeDetector detector = new BarcodeDetector.Builder(contextWeakReference.get())
                        .setBarcodeFormats(Barcode.DATA_MATRIX | Barcode.QR_CODE)
                        .build();
                Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                SparseArray<Barcode> barcodes = detector.detect(frame);
                if (barcodes.size() == 1) {
                    Barcode code = barcodes.valueAt(0);
                    JSONObject qrData = new JSONObject(code.displayValue);
                    String theirInstanceId = qrData.getString(StaticValues.JSONTags.KeyBitmap.ID);
                    String theirKey = qrData.getString(StaticValues.JSONTags.KeyBitmap.KEY);
                    chatUser.setServerId(theirInstanceId);
                    chatUser.setTheirKey(theirKey.getBytes());
                    databaseHandler.getChatHandler().updateUser(chatUser);
                    return KEY_SAVED;
                } else {
                    return NO_KEY_FOUND;
                }

            } catch (Exception e) {
                e.printStackTrace();
                return UNKNOWN_ERROR;
            }
        }

        @Override
        protected void onPostExecute(Integer result) {
            TheirKeyContract.View view = viewWeakReference.get();
            if (view != null) {
                view.onDetectBitmapResult(result);
            }
        }
    }


//    private BarcodeDetector detector;
//    detector = new BarcodeDetector.Builder(view.getContext().getApplicationContext())
//            .setBarcodeFormats(Barcode.DATA_MATRIX | Barcode.QR_CODE)
//                .build();
}
