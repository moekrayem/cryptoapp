package com.thekrayem.cryptoapp.chat.details.recycleview;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.thekrayem.cryptoapp.R;

public class ViewHolder extends RecyclerView.ViewHolder {

    public View container,outerContainer;
    public TextView content, time;


    ViewHolder(View itemView) {
        super(itemView);
        outerContainer = itemView;
        container = itemView.findViewById(R.id.chat_details_list_item_root_cl);
        content = itemView.findViewById(R.id.chat_details_list_item_message_content_tv);
        time = itemView.findViewById(R.id.chat_details_list_item_message_time_tv);
    }
}
