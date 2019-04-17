package com.thekrayem.cryptoapp.chat.creation;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.thekrayem.cryptoapp.R;
import com.thekrayem.cryptoapp.chat.creation.my.CreateNewChatMyKeyFragment;
import com.thekrayem.cryptoapp.chat.creation.their.CreateNewChatTheirKeyFragment;
import com.thekrayem.cryptoapp.helper.HelperMethods;
import com.thekrayem.cryptoapp.helper.StaticValues;
import com.thekrayem.cryptoapp.mvpbase.BaseActivity;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

public class CreateNewChatActivity extends BaseActivity implements CreateChatContract.View {

    public long chatUserId = -1L;
    public String userName = null;

    @Inject
    CreateChatContract.Presenter presenter;

    @Override
    protected int getContentResource() {
        return R.layout.activity_create_new_chat;
    }

    @Override
    protected void init(@Nullable Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        Intent intent = getIntent();
        chatUserId = intent.getLongExtra(StaticValues.IntentExtraValues.CHAT_USER_ID_INTENT_EXTRA_VALUE,-1L);
        userName = intent.getStringExtra(StaticValues.IntentExtraValues.USER_NAME_INTENT_EXTRA_VALUE);
        presenter.setChatUserId(chatUserId);
        presenter.attach(this);
        TabLayout tabLayout = findViewById(R.id.tablayout);
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.chat_setup)));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.my_key)));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.their_key)));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        NewChatPagerAdapter pagerAdapter = new NewChatPagerAdapter(getSupportFragmentManager());
        final ViewPager viewPager = findViewById(R.id.activity_create_new_chat_pager_vp);
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.default_empty_menu, menu);
        HelperMethods.addToActionBar(
                menu
                , getString(R.string.done)
                , R.drawable.icon_checkmark_white
        );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case 0:
                presenter.finalizeCreation();
                return true;
            default:
                return false;
        }
    }

    @Override
    public void showAlert(AlertType alertType) {
        String errorMessage;
        switch (alertType){
            case ERROR_UNKNOWN:
                errorMessage = getString(R.string.error);
                break;
            case NAME_NOT_SET:
                errorMessage = getString(R.string.setup_chat_name);
                break;
            case THEIR_KEY_NOT_SET:
                errorMessage = getString(R.string.setup_their_key);
                break;
            case MY_KEY_NOT_SET:
                errorMessage = getString(R.string.setup_your_key);
                break;
            default:
                errorMessage = null;
        }
        Snackbar.make(findViewById(R.id.activity_create_new_chat_outer_cl),errorMessage,Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onCreateDone() {
        setResult(RESULT_OK);
        finish();
    }


    private class NewChatPagerAdapter extends FragmentPagerAdapter {

        private NewChatPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    return new CreateNewChatSetupFragment();
                case 1:
                    return new CreateNewChatMyKeyFragment();
                case 2:{
                    Bundle args = new Bundle();
                    args.putLong(StaticValues.IntentExtraValues.CHAT_USER_ID_INTENT_EXTRA_VALUE,chatUserId);
                    CreateNewChatTheirKeyFragment fragment = new CreateNewChatTheirKeyFragment();
                    fragment.setArguments(args);
                    return fragment;
                }

                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 3;
        }
    }
}
