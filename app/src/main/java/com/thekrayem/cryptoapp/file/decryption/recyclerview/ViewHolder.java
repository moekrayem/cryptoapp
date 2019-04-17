package com.thekrayem.cryptoapp.file.decryption.recyclerview;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.thekrayem.cryptoapp.R;

public class ViewHolder extends RecyclerView.ViewHolder {

    public View container;
    public TextView fileNametv, filePathtv;
    public ImageView fileExistsiv, bytesExistiv, isChativ;

    ViewHolder(View itemView) {
        super(itemView);
        container = itemView;
        fileNametv = itemView.findViewById(R.id.decrypt_file_list_item_file_name_tv);
        filePathtv = itemView.findViewById(R.id.decrypt_file_list_item_file_path_tv);
        fileExistsiv = itemView.findViewById(R.id.decrypt_file_list_item_file_exists_iv);
        bytesExistiv = itemView.findViewById(R.id.decrypt_file_list_item_bytes_exist_iv);
        isChativ = itemView.findViewById(R.id.decrypt_file_list_item_is_chat_iv);

    }
}
