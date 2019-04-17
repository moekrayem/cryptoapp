package com.thekrayem.cryptoapp.chat.details.recycleview;

import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import com.thekrayem.cryptoapp.R;
import com.thekrayem.cryptoapp.boxdb.models.ChatMessageRecord;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatDetailsAdapter extends RecyclerView.Adapter<ViewHolder> implements Filterable {

    private List<ChatMessageRecord> objects;
    private List<ChatMessageRecord> filteredObjects;

    public ChatDetailsAdapter(List<ChatMessageRecord> objects) {
        this.objects = objects;
        this.filteredObjects = objects;
    }

    public void setObjects(List<ChatMessageRecord> objects) {
        this.objects = objects;
        this.filteredObjects = objects;
    }

    public void addObject(ChatMessageRecord object) {
        filteredObjects.add(object);
        objects.add(object);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_chat_details_list_item, parent, false);
        return new ViewHolder(itemView);
    }

    private SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm", Locale.getDefault());

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final ChatMessageRecord currentObject = filteredObjects.get(position);
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone((ConstraintLayout) holder.outerContainer);
        if (currentObject.isMine()) {
            holder.container.setBackgroundResource(R.drawable.chat_message_background_right);
            constraintSet.connect(R.id.chat_details_list_item_root_cl, ConstraintSet.END, R.id.chat_details_list_item_outer_container_cl, ConstraintSet.END, 0);
        } else {
            holder.container.setBackgroundResource(R.drawable.chat_message_background_left);
            constraintSet.connect(holder.container.getId(), ConstraintSet.START, holder.outerContainer.getId(), ConstraintSet.START, 0);
        }
        constraintSet.applyTo((ConstraintLayout) holder.outerContainer);
        holder.content.setText(currentObject.getMessageContent());
        holder.time.setText(dateFormat.format(new Date(currentObject.getMessageTime())));
    }


    @Override
    public int getItemCount() {
        return filteredObjects == null ? 0 : filteredObjects.size();
    }

    public int replace(ChatMessageRecord message){
        for(int i = 0; i < filteredObjects.size(); i++){
            ChatMessageRecord current = filteredObjects.get(i);
            if(message.getMessageId() == current.getMessageId()){
                filteredObjects.set(i,message);
                return i;
            }
        }
        return -1;
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
                    List<ChatMessageRecord> filteredList = new ArrayList<>();
                    for (ChatMessageRecord current : objects) {
                        if (current.getMessageContent().toLowerCase().contains(charString.toLowerCase())) {
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
                    filteredObjects = (ArrayList<ChatMessageRecord>) filterResults.values;
                    // refresh the list with filtered data
                    notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        };
    }
}
