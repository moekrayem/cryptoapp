package com.thekrayem.cryptoapp.file.decryption.recyclerview;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import com.thekrayem.cryptoapp.R;
import com.thekrayem.cryptoapp.file.decryption.DecryptFileListObject;
import com.thekrayem.cryptoapp.helper.RecyclerViewCallback;

import java.util.ArrayList;
import java.util.List;

public class DecryptFileListAdapter extends RecyclerView.Adapter<ViewHolder> implements Filterable {

    private List<DecryptFileListObject> objects;
    private List<DecryptFileListObject> filteredObjects;
    private RecyclerViewCallback<DecryptFileListObject> callback;

    public DecryptFileListAdapter(List<DecryptFileListObject> objects, RecyclerViewCallback<DecryptFileListObject> callback) {
        this.objects = objects;
        this.filteredObjects = objects;
        this.callback = callback;
    }

    public void setObjects(List<DecryptFileListObject> objects) {
        this.objects = objects;
        this.filteredObjects = objects;
    }

    public void addObject(DecryptFileListObject object) {
        filteredObjects.add(object);
        objects.add(object);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_decrypt_file_list_item, parent, false);
        return new ViewHolder(itemView);
    }

//    private SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm", Locale.getDefault());

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final DecryptFileListObject currentObject = filteredObjects.get(position);

        holder.fileNametv.setText(currentObject.getFileName());
        holder.filePathtv.setText(currentObject.getFilePath());
        holder.isChativ.setImageResource(currentObject.isChat()?R.drawable.icon_chat_teal:R.drawable.icon_chat_brown);
        holder.bytesExistiv.setImageResource(currentObject.areBytesSaved()?R.drawable.icon_bytes_teal:R.drawable.icon_bytes_brown);
        holder.fileExistsiv.setImageResource(currentObject.isFileSaved()?R.drawable.icon_file_teal:R.drawable.icon_file_brown);
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onClick(currentObject);
            }
        });
        holder.container.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                callback.onLongClick(currentObject);
                return false;
            }
        });

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
                    List<DecryptFileListObject> filteredList = new ArrayList<>();
                    for (DecryptFileListObject current : objects) {
                        if (current.getFileName().toLowerCase().contains(charString.toLowerCase())) {
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
                    filteredObjects = (ArrayList<DecryptFileListObject>) filterResults.values;
                    // refresh the list with filtered data
                    notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }
}
