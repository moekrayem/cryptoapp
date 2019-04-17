package com.thekrayem.cryptoapp.file.decryption;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.thekrayem.cryptoapp.R;
import com.thekrayem.cryptoapp.file.decryption.recyclerview.DecryptFileListAdapter;
import com.thekrayem.cryptoapp.helper.HelperMethods;
import com.thekrayem.cryptoapp.helper.RecyclerViewCallback;
import com.thekrayem.cryptoapp.mvpbase.BaseActivity;


import java.util.List;

public class DecryptFileListActivity extends BaseActivity implements DecryptFileContract.View {


    private DecryptFileContract.Presenter presenter;

    public static void open(Activity activity){
        activity.startActivity(new Intent(activity,DecryptFileListActivity.class));
    }

    @Override
    protected int getContentResource() {
        return R.layout.activity_decrypt_file_list;
    }

    @Override
    protected void init(@Nullable Bundle savedInstanceState) {
        presenter = new DecryptFileListPresenter();
        presenter.attach(this);
        presenter.loadList();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detach();
    }

    private void askForPassAndDecrypt(final long fileId, final boolean fromFile){
        final LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
        params.setMargins(20,20,20,20);
        LinearLayout passDialogView=new LinearLayout(DecryptFileListActivity.this);
        passDialogView.setOrientation(LinearLayout.VERTICAL);
        final EditText passwordField=new EditText(DecryptFileListActivity.this);
        passwordField.setHint(getString(R.string.password));
        passwordField.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        passwordField.setTransformationMethod(PasswordTransformationMethod.getInstance());
        passwordField.setLayoutParams(params);
        passDialogView.addView(passwordField);
        HelperMethods.showViewDialog(
                DecryptFileListActivity.this
                ,getString(R.string.password)
                ,getString(R.string.decrypt)
                ,getString(R.string.cancel)
                ,passDialogView
                ,new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface d,int passWhich){
                        switch (passWhich){
                            case DialogInterface.BUTTON_POSITIVE:
                                String password=passwordField.getText().toString();
                                if(password.length()>0){
                                    presenter.decryptFile(fileId,password,fromFile);
                                }
                            case DialogInterface.BUTTON_NEGATIVE:
                                d.dismiss();
                        }
                    }
                }
        );
    }

    private void showEncryptFileSourceDialog(final DecryptFileListObject listObject) {
        LinearLayout dialogView = new LinearLayout(DecryptFileListActivity.this);
        dialogView.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        params.setMargins(10, 10, 10, 10);
        TextView messageFieldTv = new TextView(DecryptFileListActivity.this);
        messageFieldTv.setText(getString(R.string.select_decryption_source_content));
        messageFieldTv.setLayoutParams(params);
        dialogView.addView(messageFieldTv);
        HelperMethods.showViewDialog(
                DecryptFileListActivity.this
                , getString(R.string.select_decryption_source_title)
                , getString(R.string.from_file)
                , getString(R.string.from_database)
                , dialogView
                , new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        askForPassAndDecrypt(listObject.getFileId(), which == DialogInterface.BUTTON_POSITIVE);
                    }
                }
        );
    }

    private void showFileActionsDialog(final DecryptFileListObject listObject){
        AlertDialog.Builder builder = new AlertDialog.Builder(DecryptFileListActivity.this)
                .setTitle(getString(R.string.select_action))
                .setCancelable(true);
        builder.setItems(R.array.decrypt_file_long_click_options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case 0:
                        try{
                            if(listObject.isFileSaved()){
                                LinearLayout dialogView = new LinearLayout(DecryptFileListActivity.this);
                                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                params.setMargins(10,10,10,10);
                                dialogView.setOrientation(LinearLayout.VERTICAL);
                                TextView messageTv = new TextView(DecryptFileListActivity.this);
                                messageTv.setText(getString(R.string.delete_file_question));
                                messageTv.setLayoutParams(params);
                                dialogView.addView(messageTv);
                                HelperMethods.showViewDialog(
                                        DecryptFileListActivity.this
                                        , getString(R.string.delete_file)
                                        , getString(R.string.delete_file)
                                        , getString(R.string.keep_file)
                                        , dialogView
                                        , new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                presenter.deleteRecord(listObject.getFileId(),which == DialogInterface.BUTTON_POSITIVE);
                                            }
                                        }
                                );
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        break;
                }
            }
        });
    }

    @Override
    public void onListLoaded(List<DecryptFileListObject> objects) {
        if(objects.size() == 0){
            findViewById(R.id.activity_decrypt_file_empty_placeholder_tv).setVisibility(View.VISIBLE);
            findViewById(R.id.activity_decrypt_file_list_rv).setVisibility(View.GONE);
        }else {
            findViewById(R.id.activity_decrypt_file_empty_placeholder_tv).setVisibility(View.GONE);
            RecyclerView recyclerView = findViewById(R.id.activity_decrypt_file_list_rv);
            recyclerView.setVisibility(View.VISIBLE);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            RecyclerViewCallback<DecryptFileListObject> callback = new RecyclerViewCallback<DecryptFileListObject>() {
                @Override
                public void onClick(DecryptFileListObject object) {
                    if (object.isFileSaved() && object.areBytesSaved()) {
                        showEncryptFileSourceDialog(object);
                    } else if (object.isFileSaved()) {
                        askForPassAndDecrypt(object.getFileId(), true);
                    } else if (object.areBytesSaved()) {
                        askForPassAndDecrypt(object.getFileId(), false);
                    } else {
                        this.onLongClick(object);
                    }
                }

                @Override
                public void onLongClick(DecryptFileListObject object) {
                    showFileActionsDialog(object);
                }
            };
            DecryptFileListAdapter listAdapter = new DecryptFileListAdapter(
                    objects
                    ,callback
            );
            recyclerView.setAdapter(listAdapter);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void showAlert(DecryptFileContract.View.AlertType alertType) {
        String message;
        switch (alertType){
            case ERROR_LOADING_LIST:
            case ERROR_DELETING_RECORD:
            case ERROR_DECRYPTING_FILE:
                message = getString(R.string.error);
                break;
            case RECORD_DELETED_FILE_DELETED:
                message = getString(R.string.success_file_deleted);
                break;
            case RECORD_DELETED_FILE_NOT:
                message = getString(R.string.success_file_not_deleted);
                break;
            case FILE_DECRYPTED:
                message = getString(R.string.file_decrypted_and_saved_in);
                break;
            case INCORRECT_PASSWORD:
                message = getString(R.string.password_incorrect);
                break;
            default:
                message = null;// should not be needed but Android Studio keeps thinking it "might night be initialised"
        }
        Snackbar.make(findViewById(R.id.activity_decrypt_file_root_cl),message,Snackbar.LENGTH_LONG).show();

    }





//    @Override
//    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
//        super.onCreateContextMenu(menu, v, menuInfo);
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.decrypt_file_list_context_menu, menu);
//    }

//    @Override
//    public boolean onContextItemSelected(MenuItem item) {
//        final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
//        Log.e("position", info.position + "");
//        Log.e("id", item.getItemId() + "");
//        final DecryptFileListObject currentObject = listAdapter.getObject(info.position);
//        switch (item.getItemId()) {
//            case R.id.decrypt_file_list_context_menu_delete_entry:
//
//                return true;
//            case R.id.decrypt_file_list_context_menu_link_file:
//
//                return true;
//            default:
//                return false;
//        }
//    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
//        switch (requestCode) {
//            case StaticValues.RequestCodes.REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE:
//                // TODO: 31/03/2017 TEST
//                if(grantResults.length >= 1){
//                    if(grantResults[0]!=PackageManager.PERMISSION_GRANTED){
//                        return;
//                    }
//                }
//                Intent intent = new Intent(Intent.ACTION_GET_CONTENT)
//                        .setType("*/*")
//                        .addCategory(Intent.CATEGORY_OPENABLE);
//                try {
//                    startActivityForResult(
//                            Intent.createChooser(intent, getString(R.string.select_file_encrypt)),
//                            StaticValues.RequestCodes.SELECT_FILE_TO_LINK_REQUEST_CODE);
//                } catch (android.content.ActivityNotFoundException ex) {
//                    ex.printStackTrace();
//                }
//                break;
//        }
//    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        switch (requestCode) {
//            case StaticValues.RequestCodes.SELECT_FILE_TO_LINK_REQUEST_CODE:
//                if (resultCode == RESULT_OK) {
//                    try{
//                        presenter.linkFile(selectedFileId);
//                        new LinkFileTask(selectedFileIdForLinking,data.getData()).execute();
//                    }catch (Exception e){
//                        e.printStackTrace();
//                    }
//                }
//                break;
//        }
//        super.onActivityResult(requestCode, resultCode, data);
//    }

//    private class LinkFileTask extends AsyncTask<Void,Void,Integer>{
//
//
//        private static final int RESULT_SUCCESS = 1;
//        private static final int RESULT_INCORRECT_HASH = 2;
//        private static final int RESULT_ERROR = 3;
//        private Uri data;
//        private long fileId;
//
//        private LinkFileTask(long fileId,Uri data){
//            this.fileId = fileId;
//            this.data = data;
//        }
//
//        @Override
//        protected Integer doInBackground(Void... args){
//            try{
//                byte[] fileBytes;
//                InputStream inputStream = getContentResolver().openInputStream(data);
//                long fileSize;
//                Cursor cursor = getContentResolver().query(data, null, null, null, null, null);
//                if(cursor != null && inputStream != null){
//                    int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
//                    cursor.moveToFirst();
//                    if(!cursor.isNull(sizeIndex)){
//                        fileSize = cursor.getLong(sizeIndex);
//                        cursor.close();
//                    }else{
//                        return RESULT_ERROR;
//                    }
//                    fileBytes = new byte[(int)fileSize];
//                    if(inputStream.read(fileBytes) != fileSize){
//                        return RESULT_ERROR;
//                    }
//                }else{
//                    return RESULT_ERROR;
//                }
//                file dbFile = DBConnector.getInstance(DecryptFileListActivity.this,StaticValues.DB_NAME)
//                        .file
//                        .queryBuilder()
//                        .where(fileDao.Properties.File_id.eq(fileId))
//                        .unique();
//                byte[] newFileHash = HelperMethods.getSha256(fileBytes);
//                if(Arrays.equals(newFileHash,dbFile.getFile_hash())){
//                    String filePath = HelperMethods.getRealPathFromURI_API19(DecryptFileListActivity.this,data);
//                    if(filePath != null){
//                        dbFile.setFile_path(filePath);
//                        DBConnector.getInstance(DecryptFileListActivity.this,StaticValues.DB_NAME)
//                                .file
//                                .update(dbFile);
//                        return RESULT_SUCCESS;
//                    }
//                }else{
//                    return RESULT_INCORRECT_HASH;
//                }
//                return RESULT_ERROR;
//            }catch (Exception e){
//                e.printStackTrace();
//                return RESULT_ERROR;
//            }
//        }
//
//        @Override
//        protected void onPostExecute(Integer result){
//            selectedFileIdForLinking = -1;
//            int colorResId = R.color.brown;
//            String message = "";
//            switch (result){
//                case RESULT_OK:
//                    message = getString(R.string.success);
//                    colorResId = R.color.teal;
//                    break;
//                case RESULT_INCORRECT_HASH:
//                    message = getString(R.string.incorrect_file_hash);
//                    break;
//                case RESULT_ERROR:
//                    message = getString(R.string.error);
//            }
//            CustomToast.show(
//                    DecryptFileListActivity.this
//                    ,(ViewGroup)findViewById(R.id.activity_decrypt_file_root_cl)
//                    ,colorResId
//                    ,message
//                    ,null
//            );
//        }
//    }

//    private class DeleteEntryTask extends AsyncTask<Void,Void,Integer>{
//
//        private static final int UNKNOWN_ERROR = 0;
//        private static final int SUCCESS_FILE_DELETED = 1;
//        private static final int SUCCESS_FILE_NOT_DELETED = 2;
//
//        private long fileId;
//        private int position;
//        private boolean deleteFile;
//
//        private DeleteEntryTask(long fileId,int position,boolean deleteFile){
//            this.fileId = fileId;
//            this.position = position;
//            this.deleteFile = deleteFile;
//        }
//
//        @Override
//        protected Integer doInBackground(Void...args){
//            try{
//                file dbFile = DBConnector.getInstance(DecryptFileListActivity.this,StaticValues.DB_NAME)
//                        .file
//                        .queryBuilder()
//                        .where(fileDao.Properties.File_id.eq(fileId))
//                        .unique();
//                DBConnector.getInstance(DecryptFileListActivity.this,StaticValues.DB_NAME)
//                        .file
//                        .delete(dbFile);
//                if(deleteFile){
//                    File memoryFile = new File(dbFile.getFile_path());
//                     return memoryFile.delete()?SUCCESS_FILE_DELETED:SUCCESS_FILE_NOT_DELETED;
//                }
//                return SUCCESS_FILE_NOT_DELETED;
//            }catch (Exception e){
//                e.printStackTrace();
//                return UNKNOWN_ERROR;
//            }
//        }
//
//        @Override
//        protected void onPostExecute(Integer result){
//            String message = "";
//            int colorResId = R.color.teal;
//            switch (result){
//                case UNKNOWN_ERROR:
//                    message = getString(R.string.error);
//                    colorResId = R.color.brown;
//                    break;
//                case SUCCESS_FILE_DELETED:
//                    message = getString(R.string.success_file_deleted);
//                    listAdapter.removeItemByPosition(position);
//                    listAdapter.notifyDataSetChanged();
//                    break;
//                case SUCCESS_FILE_NOT_DELETED:
//                    message = getString(R.string.success_file_not_deleted);
//                    listAdapter.removeItemByPosition(position);
//                    listAdapter.notifyDataSetChanged();
//                    break;
//            }
//
//            CustomToast.show(
//                    DecryptFileListActivity.this
//                    ,(ViewGroup)findViewById(R.id.activity_decrypt_file_outer_rl)
//                    ,colorResId
//                    ,message
//                    ,null
//            );
//        }
//    }
}
