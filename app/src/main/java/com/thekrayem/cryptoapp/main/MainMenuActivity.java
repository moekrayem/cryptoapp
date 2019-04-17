package com.thekrayem.cryptoapp.main;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.thekrayem.cryptoapp.R;
import com.thekrayem.cryptoapp.chat.list.ChatListActivity;
import com.thekrayem.cryptoapp.file.encryption.EncryptFileActivity;
import com.thekrayem.cryptoapp.helper.HelperMethods;
import com.thekrayem.cryptoapp.preferences.SettingsActivity;

public class MainMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
//        final TextView chatLabelTv = (TextView)findViewById(R.id.activity_main_menu_chat_label_tv);
//        final TextView encryptionLabelTv = (TextView)findViewById(R.id.activity_main_menu_encryption_label_tv);
//        View.OnTouchListener listener = new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                int colorResId;
//                int chatResId;
//                int encryptionResId;
//                if(event.getAction() == MotionEvent.ACTION_DOWN){
//                    colorResId = android.R.color.darker_gray;
//                }else{
//                    colorResId = android.R.color.white;
//                }
//                int actualColor;
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                    actualColor = getColor(colorResId);
//                }else{
//                    actualColor = getResources().getColor(colorResId);
//                }
//                chatLabelTv.setTextColor(actualColor);
//                encryptionLabelTv.setTextColor(actualColor);
//
//                return false;
//            }
//        };
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.activity_main_menu_chat_container_ll:
                        startActivity(new Intent(MainMenuActivity.this, ChatListActivity.class));
                        break;
                    case R.id.activity_main_menu_encrypt_container_ll:
                        startActivity(new Intent(MainMenuActivity.this, EncryptFileActivity.class));
                        break;
                }
            }
        };
        findViewById(R.id.activity_main_menu_chat_container_ll).setOnClickListener(onClickListener);
        findViewById(R.id.activity_main_menu_encrypt_container_ll).setOnClickListener(onClickListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.default_empty_menu, menu);
        HelperMethods.addToActionBar(
                menu
                , getString(R.string.settings)
                , R.drawable.ic_settings_white
        );

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case 0:
                startActivity(
                        new Intent(
                                this
                                ,SettingsActivity.class
                        )
                );
                return true;
            default:
                return false;
        }
    }



}
