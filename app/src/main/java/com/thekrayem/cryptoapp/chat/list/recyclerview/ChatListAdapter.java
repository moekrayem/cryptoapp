package com.thekrayem.cryptoapp.chat.list.recyclerview;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import com.thekrayem.cryptoapp.R;
import com.thekrayem.cryptoapp.chat.details.ChatDetailsActivity;
import com.thekrayem.cryptoapp.chat.list.ChatListActivity;
import com.thekrayem.cryptoapp.chat.list.ChatListObject;
import com.thekrayem.cryptoapp.helper.StaticValues;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatListAdapter extends RecyclerView.Adapter<ViewHolder> implements Filterable {

    private List<ChatListObject> objects;
    private List<ChatListObject> filteredObjects;
    private ChatListActivity activity;

    public ChatListAdapter(ChatListActivity activity, List<ChatListObject> objects) {
        this.objects = objects;
        this.filteredObjects = objects;
        this.activity = activity;
    }

    public void setObjects(List<ChatListObject> objects) {
        this.objects = objects;
        this.filteredObjects = objects;
    }

    public void addObject(ChatListObject object) {
        filteredObjects.add(object);
        objects.add(object);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_chat_list_item, parent, false);
        return new ViewHolder(itemView);
    }

    private SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm", Locale.getDefault());

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final ChatListObject currentObject = filteredObjects.get(position);
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.startActivity(
                        new Intent(
                                activity
                                , ChatDetailsActivity.class
                        ).putExtra(
                                StaticValues.IntentExtraValues.CHAT_USER_ID_INTENT_EXTRA_VALUE
                                , currentObject.getUserID()
                        )
                );
            }
        });
//        holder.container.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//
//                return false;
//            }
//        });
        holder.name.setText(currentObject.getUserName());
        holder.content.setText(currentObject.getLastMessageContent());
        holder.time.setText(dateFormat.format(new Date(currentObject.getLastMessageDate())));
    }


    @Override
    public int getItemCount() {
        return filteredObjects == null ? 0 : filteredObjects.size();
    }


    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    filteredObjects = objects;
                } else {
                    List<ChatListObject> filteredList = new ArrayList<>();
                    for (ChatListObject current : objects) {
                        if (current.getUserName().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(current);
                        }
                    }
                    filteredObjects = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredObjects;
                return filterResults;
            }

            @Override
            @SuppressWarnings("unchecked")
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                try {
                    filteredObjects = (ArrayList<ChatListObject>) filterResults.values;
                    // refresh the list with filtered data
                    notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }
}
