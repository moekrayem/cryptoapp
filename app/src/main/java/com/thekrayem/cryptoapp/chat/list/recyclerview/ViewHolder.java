package com.thekrayem.cryptoapp.chat.list.recyclerview;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.thekrayem.cryptoapp.R;

public class ViewHolder extends RecyclerView.ViewHolder {

    public View container;
    public TextView name, content, time;

    ViewHolder(View itemView) {
        super(itemView);
        container = itemView;
        content = itemView.findViewById(R.id.chat_list_item_last_message_tv);
        time = itemView.findViewById(R.id.chat_list_item_date_tv);
        name = itemView.findViewById(R.id.chat_list_item_name_tv);
    }
}
