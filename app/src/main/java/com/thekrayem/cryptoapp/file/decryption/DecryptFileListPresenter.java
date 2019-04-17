package com.thekrayem.cryptoapp.file.decryption;

import android.os.AsyncTask;
import android.os.Environment;

import com.thekrayem.cryptoapp.boxdb.handler.DatabaseHandler;
import com.thekrayem.cryptoapp.boxdb.models.FileRecord;
import com.thekrayem.cryptoapp.helper.App;
import com.thekrayem.cryptoapp.helper.HelperMethods;
import com.thekrayem.cryptoapp.helper.StaticValues;
import com.thekrayem.cryptoapp.mvpbase.BasePresenter;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

public class DecryptFileListPresenter extends BasePresenter<DecryptFileContract.View> implements DecryptFileContract.Presenter {

    @Inject
    DatabaseHandler databaseHandler;

    @Inject
    DecryptFileListPresenter() {
        App.getInstance().getAppComponent().inject(this);
    }


    @Override
    public void loadList() {
        new LoadListTask(getView(), databaseHandler).execute();
    }

    private static class LoadListTask extends AsyncTask<Void, Void, Boolean> {

        private WeakReference<DecryptFileContract.View> viewWeakReference;

        private List<DecryptFileListObject> objects;

        private DatabaseHandler databaseHandler;

        private LoadListTask(DecryptFileContract.View view, DatabaseHandler databaseHandler) {
            viewWeakReference = new WeakReference<>(view);
            objects = new ArrayList<>();
            this.databaseHandler = databaseHandler;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                List<FileRecord> fileRecords = databaseHandler.getFileHandler().getAllFiles();
                for (FileRecord currentRecord : fileRecords) {
                    objects.add(
                            new DecryptFileListObject(
                                    currentRecord.getFileId()
                                    , currentRecord.getFileEncryptedName()
                                    , currentRecord.getFilePath()
                                    , currentRecord.isChat()
                                    , currentRecord.getFileEncryptedBytes().length > 0
                                    , currentRecord.getFilePath() != null
                            )
                    );
                }
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            DecryptFileContract.View view = viewWeakReference.get();
            if (view != null) {
                if (result) {
                    view.onListLoaded(objects);
                } else {
                    view.showAlert(DecryptFileContract.View.AlertType.ERROR_LOADING_LIST);
                }
            }
        }
    }

    @Override
    public void decryptFile(long fileId, String password, boolean fromFile) {
        new DecryptFileTask(getView(),databaseHandler, fileId, password, fromFile).execute();
    }

    private static class DecryptFileTask extends AsyncTask<Void, Void, Integer> {

        private static final int RESULT_SUCCESS = 1;
        private static final int RESULT_PASSWORD_INCORRECT = 2;
        private static final int RESULT_ERROR = 3;
        private long fileId;
        private String password;
        private boolean getFromFile;

        private WeakReference<DecryptFileContract.View> viewWeakReference;

        private DatabaseHandler databaseHandler;

        private DecryptFileTask(DecryptFileContract.View view, DatabaseHandler databaseHandler, long fileId, String password, boolean getFromFile) {
            this.fileId = fileId;
            this.password = password;
            this.getFromFile = getFromFile;
            viewWeakReference = new WeakReference<>(view);
            this.databaseHandler = databaseHandler;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Integer doInBackground(Void... args) {
            try {
                FileRecord selectedFileDb = databaseHandler.getFileHandler().getFileById(fileId);
                byte[] hashedPassBytes = selectedFileDb.getHashedPass();
                byte[] key = HelperMethods.getEncryptionKeyFromPassword(password.toCharArray(), selectedFileDb.getSalt(), selectedFileDb.getIterations(), selectedFileDb.getKeyLength());
                if (!Arrays.equals(hashedPassBytes, HelperMethods.getSha256(key))) {
                    return RESULT_PASSWORD_INCORRECT;
                }
                byte[] encrypted;
                if (getFromFile) {
                    File encryptedFile = new File(selectedFileDb.getFilePath());
                    if (!encryptedFile.exists()) {
                        return RESULT_ERROR;
                    }
                    encrypted = FileUtils.readFileToByteArray(encryptedFile);
                } else {
                    encrypted = selectedFileDb.getFileEncryptedBytes();
                    if (encrypted == null || encrypted.length == 0) {
                        return RESULT_ERROR;
                    }
                }

                byte[] decrypted = HelperMethods.decrypt(
                        encrypted
                        , key
                        , selectedFileDb.getIv()
                );
                if (decrypted == null) {
                    return RESULT_ERROR;
                }
                File saveDirectory = new File(
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
                        , StaticValues.DEFAULT_SAVE_FOLDER_PLAIN);
                File decryptedFile = new File(saveDirectory, selectedFileDb.getFileOriginalName() + "_" + Calendar.getInstance().getTimeInMillis());
                FileUtils.writeByteArrayToFile(decryptedFile, decrypted);
                return RESULT_SUCCESS;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return RESULT_ERROR;
        }

        @Override
        protected void onPostExecute(Integer result) {
            DecryptFileContract.View view = viewWeakReference.get();
            if (view != null) {
                switch (result) {
                    case RESULT_SUCCESS:
                        view.showAlert(DecryptFileContract.View.AlertType.FILE_DECRYPTED);
                        break;
                    case RESULT_PASSWORD_INCORRECT:
                        view.showAlert(DecryptFileContract.View.AlertType.INCORRECT_PASSWORD);
                        break;
                    case RESULT_ERROR:
                        view.showAlert(DecryptFileContract.View.AlertType.ERROR_DECRYPTING_FILE);
                        break;
                }
            }
        }
    }

    @Override
    public void deleteRecord(long fileId, boolean deleteFile) {
        new DeleteFileRecordTask(getView(),databaseHandler, fileId, deleteFile).execute();
    }

    private static class DeleteFileRecordTask extends AsyncTask<Void, Void, Integer> {

        private long fileId;
        private boolean deleteFile;
        private WeakReference<DecryptFileContract.View> viewWeakReference;
        private DatabaseHandler databaseHandler;

        private DeleteFileRecordTask(DecryptFileContract.View view,DatabaseHandler databaseHandler, long fileId, boolean deleteFile) {
            viewWeakReference = new WeakReference<>(view);
            this.fileId = fileId;
            this.deleteFile = deleteFile;
            this.databaseHandler = databaseHandler;
        }

        private static final int UNKNOWN_ERROR = 0;
        private static final int SUCCESS_FILE_DELETED = 1;
        private static final int SUCCESS_FILE_NOT_DELETED = 2;

        @Override
        protected Integer doInBackground(Void... voids) {
            try {
                boolean fileDeletedResult = false;
                if (deleteFile) {
                    File file = new File(databaseHandler.getFileHandler().getFileById(fileId).getFilePath());
                    fileDeletedResult = file.delete();// ? SUCCESS_FILE_DELETED : SUCCESS_FILE_NOT_DELETED;
                }
                databaseHandler.getFileHandler().deleteById(fileId);
                return fileDeletedResult ? SUCCESS_FILE_DELETED : SUCCESS_FILE_NOT_DELETED;
            } catch (Exception e) {
                e.printStackTrace();
                return UNKNOWN_ERROR;
            }
        }

        @Override
        protected void onPostExecute(Integer result) {
            DecryptFileContract.View view = viewWeakReference.get();
            if (view != null) {
                switch (result) {
                    case UNKNOWN_ERROR:
                        view.showAlert(DecryptFileContract.View.AlertType.ERROR_DELETING_RECORD);
                        break;
                    case SUCCESS_FILE_DELETED:
                        view.showAlert(DecryptFileContract.View.AlertType.RECORD_DELETED_FILE_DELETED);
                        break;
                    case SUCCESS_FILE_NOT_DELETED:
                        view.showAlert(DecryptFileContract.View.AlertType.RECORD_DELETED_FILE_NOT);
                        break;
                }
            }
        }
    }
}
