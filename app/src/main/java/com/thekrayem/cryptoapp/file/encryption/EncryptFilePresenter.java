package com.thekrayem.cryptoapp.file.encryption;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.OpenableColumns;

import com.thekrayem.cryptoapp.boxdb.handler.DatabaseHandler;
import com.thekrayem.cryptoapp.boxdb.models.ChatMessageRecord;
import com.thekrayem.cryptoapp.boxdb.models.ChatUserRecord;
import com.thekrayem.cryptoapp.boxdb.models.FileRecord;
import com.thekrayem.cryptoapp.helper.App;
import com.thekrayem.cryptoapp.helper.HelperMethods;
import com.thekrayem.cryptoapp.helper.StaticValues;
import com.thekrayem.cryptoapp.helper.password.PasswordChecker;
import com.thekrayem.cryptoapp.helper.password.PasswordStrengthResult;
import com.thekrayem.cryptoapp.mvpbase.BasePresenter;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.security.SecureRandom;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

public class EncryptFilePresenter extends BasePresenter<EncryptFileContract.View> implements EncryptFileContract.Presenter {

    private long userId;
    private boolean encryptingChat;
    private Uri data;

    @Inject
    Context context;

    @Inject
    DatabaseHandler databaseHandler;


    @Inject
    EncryptFilePresenter() {
        App.getInstance().getAppComponent().inject(this);
    }

    @Override
    public void setupEncryptionMode(long userId) {
        this.userId = userId;
        encryptingChat = userId != -1L;
        getView().onEncryptionModeSet(encryptingChat);
    }

    @Override
    public void selectFileForEncryption(Uri data) {
        this.data = data;
        new GetFileInfoTask(getView(), data, context.getContentResolver()).execute();

    }

    private static class GetFileInfoTask extends AsyncTask<Void, Void, Boolean> {

        private Uri data;
        private String fileName = null;
        private long size = -1;
        private ContentResolver contentResolver;
        private WeakReference<EncryptFileContract.View> viewWeakReference;

        private GetFileInfoTask(EncryptFileContract.View view, Uri data, ContentResolver contentResolver) {
            this.data = data;
            this.contentResolver = contentResolver;
            viewWeakReference = new WeakReference<>(view);
        }

        @Override
        protected Boolean doInBackground(Void... args) {
            try (Cursor cursor = contentResolver.query(data, null, null, null, null, null)) {
                // query the uri for the file name
                if (cursor != null) {
                    cursor.moveToFirst();
                    fileName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    size = cursor.getLong(cursor.getColumnIndex(OpenableColumns.SIZE));
                    cursor.close();
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }


        @Override
        protected void onPostExecute(Boolean result) {
            EncryptFileContract.View view = viewWeakReference.get();
            if (view != null) {
                if (result) {
                    view.onFileSelected(fileName, size);
                } else {
                    view.showAlert(EncryptFileContract.View.AlertType.ERROR_FILE_INFO);
                }
            }
        }
    }

    @Override
    public void checkPassword(String password) {
        getView().onPasswordChecked(PasswordChecker.checkPasswordStrength(password));
    }

    @Override
    public void encrypt(String name, String password, boolean saveFileBytes, boolean deleteOriginalFile) {
        EncryptFileContract.View.AlertType alertType = null;
        if (data == null && !encryptingChat) {
            alertType = EncryptFileContract.View.AlertType.NO_FILE_SELECTED;
        } else if (!HelperMethods.checkFileName(name)) {
            alertType = EncryptFileContract.View.AlertType.NAME_NOT_VALID;
        } else if (PasswordChecker.checkPasswordStrength(password) == PasswordStrengthResult.FAIL) {
            alertType = EncryptFileContract.View.AlertType.PASS_NOT_VALID;
        }
        if (alertType == null) {
            new EncryptFileTask(
                    getView()
                    , context
                    , databaseHandler
                    , data
                    , name
                    , password
                    , deleteOriginalFile && !encryptingChat
                    , StaticValues.DEFAULT_ITERATIONS
                    , StaticValues.DEFAULT_KEY_LENGTH
                    , saveFileBytes
                    , encryptingChat
                    , userId
            ).execute();
        } else {
            getView().showAlert(alertType);
        }
    }

    public static class EncryptFileTask extends AsyncTask<Void, Void, Integer> {

        static final int ERROR_NO_SIZE = -1;
        static final int ERROR_UNKNOWN = 0;
        static final int SUCCESS = 1;
        static final int SUCCESS_FILE_DELETED = 2;
        static final int SUCCESS_CHAT_ENCRYPTED = 3;

        private DatabaseHandler databaseHandler;
        private WeakReference<EncryptFileContract.View> viewWeakReference;
        private WeakReference<Context> contextWeakReference;
        private ContentResolver contentResolver;


        private Uri data;
        private String password;
        private String name;
        private boolean deleteOriginal;
        private int iterations;
        private int keyLength;
        private boolean saveBytes;
        private boolean encryptingChat;
        private long userId;


        private EncryptFileTask(EncryptFileContract.View view, Context context, DatabaseHandler databaseHandler, Uri data, String name, String password, boolean deleteOriginal, int iterations, int keyLength, boolean saveBytes, boolean encryptingChat, long userId) {
            viewWeakReference = new WeakReference<>(view);
            this.databaseHandler = databaseHandler;
            contextWeakReference = new WeakReference<>(context);
            contentResolver = context.getContentResolver();

            this.iterations = iterations;
            this.keyLength = keyLength;
            this.data = data;
            this.name = name;
            this.password = password;
            this.deleteOriginal = deleteOriginal;
            this.saveBytes = saveBytes;
            this.encryptingChat = encryptingChat;
            this.userId = userId;
        }

        @Override
        protected void onPreExecute() {
            EncryptFileContract.View view = viewWeakReference.get();
            if (view != null) {
                view.setLoading(true);
            }
        }

        @Override
        protected Integer doInBackground(Void... args) {
            try {
                byte[] plain;
                String chatUserName = null;
                // create the default save directory if it doesn't exist
                File saveDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), StaticValues.DEFAULT_SAVE_FOLDER_ENC);
                if (!saveDirectory.exists()) {
                    saveDirectory.mkdirs();
                }
                // if we are encrypting a chat, we get all its messages and store them in a JSON object
                // that is to be written to the file and encrypted below
                if (encryptingChat) {
                    List<ChatMessageRecord> chatMessageRecords = databaseHandler.getChatHandler().getUserMessages(userId);
                    ChatUserRecord chatUserRecord = databaseHandler.getChatHandler().getUser(userId);
                    chatUserName = chatUserRecord.getUserName();
                    JSONObject toEncrypt = new JSONObject();
                    JSONObject userObject = new JSONObject();
                    userObject.put(StaticValues.JSONTags.EncryptChat.ENCRYPT_CHAT_ID, userId);
                    userObject.put(StaticValues.JSONTags.EncryptChat.ENCRYPT_CHAT_NAME, chatUserRecord.getUserName());
                    toEncrypt.put(StaticValues.JSONTags.EncryptChat.ENCRYPT_CHAT_USER_OBJECT, userObject);
                    JSONArray array = new JSONArray();
                    for (ChatMessageRecord currentMessage : chatMessageRecords) {
                        JSONObject currentObject = new JSONObject();
                        currentObject.put(StaticValues.JSONTags.EncryptChat.ENCRYPT_CHAT_MESSAGE_TIME, currentMessage.getMessageTime());
                        currentObject.put(StaticValues.JSONTags.EncryptChat.ENCRYPT_CHAT_MESSAGE_CONTENT, currentMessage.getMessageContent());
                        array.put(currentObject);
                    }
                    toEncrypt.put(StaticValues.JSONTags.EncryptChat.ENCRYPT_CHAT_CHAT, array);
                    plain = toEncrypt.toString().getBytes();
                } else {
                    // if we are encrypting a normal file, we use the InputStream we got from
                    // the Uri to read its bytes
                    InputStream inputStream = contentResolver.openInputStream(data);
                    long fileSize;
                    Cursor cursor = contentResolver.query(data, null, null, null, null, null);
                    if (cursor != null && inputStream != null) {
                        int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
                        cursor.moveToFirst();
                        if (!cursor.isNull(sizeIndex)) {
                            fileSize = cursor.getLong(sizeIndex);
                            cursor.close();
                        } else {
                            return ERROR_NO_SIZE;
                        }
                        plain = new byte[(int) fileSize];
                        if (inputStream.read(plain) <= 0) {
                            return ERROR_UNKNOWN;
                        }
                    } else {
                        return ERROR_UNKNOWN;
                    }
                }
                // generate random values
                SecureRandom secureRandom = new SecureRandom();
                byte[] iv = new byte[16];
                byte[] salt = new byte[8];
                byte[] passSalt = new byte[8];
                secureRandom.nextBytes(iv);
                secureRandom.nextBytes(salt);
                secureRandom.nextBytes(passSalt);

                // encrypt the plain byte array
                byte[] key = HelperMethods.getEncryptionKeyFromPassword(password.toCharArray(), salt, iterations, keyLength);
                byte[] encrypted = HelperMethods.encrypt(plain, key, iv);

                // write the encrypted byte array to a file, hash it, and store everything in the DB
                File encryptedFile = new File(saveDirectory, name);
                FileUtils.writeByteArrayToFile(encryptedFile, encrypted);
                byte[] hashOfFile = HelperMethods.getSha256(encrypted);
                FileRecord fileRecord = new FileRecord(
                        0
                        , saveBytes ? encrypted : new byte[0]
                        , HelperMethods.getSha256(key)
                        , salt
                        , iv
                        , name
                        , encryptingChat ? "CHAT" + chatUserName + "_" + Calendar.getInstance().getTimeInMillis() + "_" + name : name
                        , encryptedFile.getAbsolutePath()
                        , iterations
                        , keyLength
                        , encryptingChat
                        , hashOfFile
                );
                databaseHandler.getFileHandler().put(fileRecord);
                // if we are encrypting a chat, delete it
                if (encryptingChat) {
                    databaseHandler.getChatHandler().deleteAllForUser(userId);
//                    Box<ChatUserRecord> userBox = boxStore.boxFor(ChatUserRecord.class);
//                    Box<ChatMessageRecord> messageBox = boxStore.boxFor(ChatMessageRecord.class);
//                    List<ChatMessageRecord> messagesToDelete = messageBox.query().equal(ChatMessageRecord_.userId, userId).build().find();
//                    ChatUserRecord userToDelete = userBox.get(userId);
//                    messageBox.remove(messagesToDelete);
//                    userBox.remove(userToDelete);
                    return SUCCESS_CHAT_ENCRYPTED;
                }

                // if the original file is to be deleted, check first if we can extract the path
                // by assuming the uri comes in a file://something format
                // or if the file is selected from google drive or whatever
                Context context = contextWeakReference.get();
                if (deleteOriginal && context != null) {
                    String originalFilePath = HelperMethods.getRealPathFromURI_API19(context, data);
                    if (originalFilePath != null) {
                        File originalFile = new File(originalFilePath);
                        if (originalFile.exists()) {
                            if (originalFile.delete()) {
                                MediaScannerConnection.scanFile(context, new String[]{originalFilePath}, null, null);
                                return SUCCESS_FILE_DELETED;
                            }
                        }
                    }
                }
                return SUCCESS;
            } catch (Exception e) {
                e.printStackTrace();
                return ERROR_UNKNOWN;
            }
        }

        @Override
        protected void onPostExecute(Integer result) {
            EncryptFileContract.View view = viewWeakReference.get();
            if (view != null) {
                view.onEncryptResult(result, encryptingChat);
            }
        }
    }
}
