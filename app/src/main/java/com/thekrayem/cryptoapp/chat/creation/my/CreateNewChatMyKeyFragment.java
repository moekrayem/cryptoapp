package com.thekrayem.cryptoapp.chat.creation.my;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.thekrayem.cryptoapp.R;
import com.thekrayem.cryptoapp.helper.StaticValues;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;

public class CreateNewChatMyKeyFragment extends Fragment implements MyKeyContract.View {

    @Inject
    MyKeyContract.Presenter presenter;

    private long userId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidSupportInjection.inject(this);
        Bundle data = getArguments();
        if (data != null) {
            userId = data.getLong(StaticValues.IntentExtraValues.CHAT_USER_ID_INTENT_EXTRA_VALUE, -1L);
        } else {
            userId = -1L;
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_new_chat_my_key, container, false);
        view.findViewById(R.id.fragment_create_new_chat_my_key_generate_bu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.generateKey(userId);
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter.checkForExistingKey(userId);
    }

    @Override
    public void onKeyCreated(int result, Bitmap bitmap) {
        View view = getView();
        if (view == null) {
            return;
        }
        String message;
        switch (result) {
            case MyKeyPresenter.GenerateMyKeyTask.KEY_CREATED:
                message = getString(R.string.key_created);
                break;
            case MyKeyPresenter.GenerateMyKeyTask.USER_DOESNT_EXIST:
                message = getString(R.string.user_doesnt_exist);
                break;
            case MyKeyPresenter.GenerateMyKeyTask.KEY_EXISTS_AND_USED:
                message = getString(R.string.key_exists_and_used);
                break;
            case MyKeyPresenter.GenerateMyKeyTask.UNKNOWN_ERROR:
                message = getString(R.string.error);
                break;
            default:
                message = "";
        }
        Snackbar.make(
                view.findViewById(R.id.fragment_create_new_chat_my_key_outer_rl)
                , message
                , Snackbar.LENGTH_LONG
        ).show();
        onKeyChecked(bitmap);
    }

    @Override
    public void setLoading(boolean loading, LoadingType type) {
        View view = getView();
        if (view == null) {
            return;
        }
        if (loading) {
            view.findViewById(R.id.fragment_create_new_chat_my_key_progress_pb).setVisibility(View.VISIBLE);
            view.findViewById(R.id.fragment_create_new_chat_my_key_generate_bu).setEnabled(false);
            TextView progressLabel = view.findViewById(R.id.fragment_create_new_chat_my_key_progress_label_tv);
            progressLabel.setVisibility(View.VISIBLE);
            view.findViewById(R.id.fragment_create_new_chat_my_key_qr_iv).setVisibility(View.INVISIBLE);
            switch (type) {
                case CHECKING:
                    progressLabel.setText(getString(R.string.checking_existing_key));
                    break;
                case GENERATING:
                    progressLabel.setText(getString(R.string.generating_key));
                    break;
            }
        } else {
            view.findViewById(R.id.fragment_create_new_chat_my_key_progress_pb).setVisibility(View.INVISIBLE);
            view.findViewById(R.id.fragment_create_new_chat_my_key_generate_bu).setEnabled(true);
        }
    }

    @Override
    public void onKeyChecked(Bitmap bitmap) {
        View view = getView();
        if (view == null) {
            return;
        }
        ImageView keyIv = view.findViewById(R.id.fragment_create_new_chat_my_key_qr_iv);
        TextView progressLabel = view.findViewById(R.id.fragment_create_new_chat_my_key_progress_label_tv);
        if (bitmap != null) {
            keyIv.setVisibility(View.VISIBLE);
            keyIv.setImageBitmap(bitmap);
            progressLabel.setVisibility(View.INVISIBLE);
        } else {
            keyIv.setVisibility(View.INVISIBLE);
            progressLabel.setVisibility(View.VISIBLE);
            progressLabel.setText(getString(R.string.no_existing_key));
        }
    }
}
