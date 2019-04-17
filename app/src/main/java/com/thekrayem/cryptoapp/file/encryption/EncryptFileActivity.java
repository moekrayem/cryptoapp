package com.thekrayem.cryptoapp.file.encryption;

import android.Manifest;
import android.content.Intent;

import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.thekrayem.cryptoapp.R;
import com.thekrayem.cryptoapp.file.decryption.DecryptFileListActivity;
import com.thekrayem.cryptoapp.helper.CustomToast;
import com.thekrayem.cryptoapp.helper.HelperMethods;
import com.thekrayem.cryptoapp.helper.StaticValues;
import com.thekrayem.cryptoapp.helper.password.PasswordStrengthResult;
import com.thekrayem.cryptoapp.mvpbase.BaseActivity;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

import static com.thekrayem.cryptoapp.file.encryption.EncryptFilePresenter.EncryptFileTask;

public class EncryptFileActivity extends BaseActivity implements EncryptFileContract.View {


    @Inject
    EncryptFileContract.Presenter presenter;

    // View
    @Override
    public void setLoading(boolean loading) {
        findViewById(R.id.activity_encrypt_file_select_path_iv).setVisibility(loading ? View.GONE : View.VISIBLE);
        findViewById(R.id.activity_encrypt_file_encryption_progress_pb).setVisibility(loading ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onPasswordChecked(PasswordStrengthResult result) {
        switch (result){
            case FAIL:{
                ImageView checkPasswordIv = findViewById(R.id.activity_encrypt_file_check_password_iv);
                checkPasswordIv.setImageResource(R.drawable.icon_cancel_brown);
                Snackbar.make(
                        findViewById(R.id.activity_encrypt_file_root_cl)
                        ,getString(R.string.password_can_be_stronger)
                        ,Snackbar.LENGTH_LONG
                ).show();
                EditText passwordEditText = findViewById(R.id.activity_encrypt_file_password_et);
                passwordEditText.setText("");
                passwordEditText.startAnimation(AnimationUtils.loadAnimation(EncryptFileActivity.this, R.anim.shake_anim));
                break;
            }
            case WEAK:
            case MEH:{
                ImageView checkPasswordIv = findViewById(R.id.activity_encrypt_file_check_password_iv);
                checkPasswordIv.setImageResource(R.drawable.icon_cancel_brown);
                Snackbar.make(
                        findViewById(R.id.activity_encrypt_file_root_cl)
                        ,getString(R.string.password_can_be_stronger)
                        ,Snackbar.LENGTH_LONG
                ).show();
                break;
            }
            case STRONG:{
                ImageView checkPasswordIv = findViewById(R.id.activity_encrypt_file_check_password_iv);
                checkPasswordIv.setImageResource(R.drawable.icon_check_teal);
            }
        }
    }

    private void openFileSelector(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    StaticValues.RequestCodes.REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE);
        }else{
            // if we have them, open an app to choose a file
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT)
                    .setType("*/*")
                    .addCategory(Intent.CATEGORY_OPENABLE);
            try {
                startActivityForResult(
                        Intent.createChooser(intent, this.getString(R.string.select_file_encrypt)),
                        StaticValues.RequestCodes.SELECT_FILE_ENCRYPTION_REQUEST_CODE);
            } catch (android.content.ActivityNotFoundException ex) {
                ex.printStackTrace();
                showAlert(AlertType.ERROR_FILE_SELECTOR);
            }
        }
    }
    @Override
    public void onEncryptionModeSet(boolean encryptingChat) {
        ImageView selectPathButton = findViewById(R.id.activity_encrypt_file_select_path_iv);
        if (encryptingChat) {
            selectPathButton.setVisibility(View.GONE);
            findViewById(R.id.activity_encrypt_file_path_tv).setVisibility(View.GONE);
        } else {
            selectPathButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openFileSelector();
                }
            });
        }
        findViewById(R.id.activity_encrypt_file_check_password_iv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.checkPassword(((EditText) findViewById(R.id.activity_encrypt_file_password_et)).getText().toString());
            }
        });

        findViewById(R.id.activity_encrypt_file_encrypt_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.encrypt(
                        ((TextView) findViewById(R.id.activity_encrypt_file_name_et)).getText().toString()
                        ,((TextView) findViewById(R.id.activity_encrypt_file_password_et)).getText().toString()
                        ,((CheckBox) findViewById(R.id.activity_encrypt_file_save_bytes_cb)).isChecked()
                        ,((CheckBox) findViewById(R.id.activity_encrypt_file_delete_cb)).isChecked()
                );
            }
        });
    }

    @Override
    public void onFileSelected(String fileName, long size) {
        TextView pathTv = findViewById(R.id.activity_encrypt_file_path_tv);
        pathTv.setText(String.format(getString(R.string.selected_file_label),fileName,HelperMethods.getSizeLabelFromByte(size)));
    }

    @Override
    public void showAlert(AlertType alertType) {
        String errorMessage;
        switch (alertType){
            case ERROR_ENCRYPTING:
                errorMessage = getString(R.string.error_encryption);
                break;
            case ERROR_FILE_SELECTOR:
                errorMessage = getString(R.string.error);
                break;
            case NAME_NOT_VALID:
                errorMessage = getString(R.string.no_valid_name);
                break;
            case PASS_NOT_VALID:
                errorMessage = getString(R.string.no_valid_pass);
                break;
            case ERROR_FILE_INFO:
                errorMessage = getString(R.string.error_file_info);
                break;
            case NO_FILE_SELECTED:
                errorMessage = getString(R.string.no_file_selected);
                break;
            default:
                errorMessage = null;
        }
        Snackbar.make(findViewById(R.id.activity_encrypt_file_root_cl),errorMessage,Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onEncryptResult(int result,boolean encryptingChat) {
            findViewById(R.id.activity_encrypt_file_encryption_progress_pb).setVisibility(View.GONE);
            if (!encryptingChat) {
                findViewById(R.id.activity_encrypt_file_select_path_iv).setVisibility(View.VISIBLE);
            }
            String message;
            int colorResId = R.color.teal;
            switch (result) {
                case EncryptFileTask.SUCCESS_FILE_DELETED:
                    message = getString(R.string.success_file_deleted);
                    break;
                case EncryptFileTask.SUCCESS:
                    message = getString(R.string.success_file_not_deleted);
                    break;
                case EncryptFileTask.SUCCESS_CHAT_ENCRYPTED:
                    setResult(RESULT_OK);
                    finish();
                    return;
                default:
                    message = getString(R.string.error);
                    colorResId = R.color.brown;
            }
            CustomToast.show(
                    EncryptFileActivity.this
                    , (ViewGroup) findViewById(R.id.activity_encrypt_file_root_cl)
                    , colorResId
                    , message
                    , null
            );
    }

    // BaseActivity
    @Override
    protected void init(@Nullable Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        presenter.attach(this);
        presenter.setupEncryptionMode(getIntent().getLongExtra(StaticValues.IntentExtraValues.ENCRYPT_CHAT_USER_ID_INTENT_EXTRA_VALUE,-1L));
    }

    @Override
    protected int getContentResource() {
        return R.layout.new_activity_encrypt_file;
    }



    // Android
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.default_empty_menu, menu);
        HelperMethods.addToActionBar(
                menu
                , getString(R.string.decrypt_files)
                , R.drawable.list_icon
        );

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case 0:
                DecryptFileListActivity.open(this);
                return true;
            default:
                return false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case StaticValues.RequestCodes.SELECT_FILE_ENCRYPTION_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    // when a file is selected, start a task to get its info and store them in a view for later
                    presenter.selectFileForEncryption(data.getData());
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    // if permissions are granted, re-click the button so the user doesn't have to do it manually
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case StaticValues.RequestCodes.REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && (grantResults[0] == PackageManager.PERMISSION_GRANTED && permissions[0].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE))) {
                    findViewById(R.id.activity_encrypt_file_select_path_iv).callOnClick();
                }
        }
    }

}