package com.example.app1;

/**
 * Each individual element in the list is defined by a view holder object. When the view holder is
 * created, it doesn't have any data associated with it. After the view holder is created, the
 * RecyclerView binds it to its data.
 * -> The view holder is defined by the extension of the RecyclerView.ViewHolder.
 * The RecyclerView requests those views, and binds the views to their data, by calling methods in
 * the adapter.
 * -> The adapter is defined by the extension of the RecyclerView.Adapter.
 */

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

// =================================================================================================
// extension of the RecyclerView.Adapter
public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ContactsViewHolder> {
    private Context context;
    private List<ContactInfo> contactInfoList;
    private static ClickListener clickListener;

    public ContactsAdapter(Context context, List<ContactInfo> contactInfoList){
        this.context = context;
        this.contactInfoList = contactInfoList;
    }

    // This method creates a new RecyclerView.ViewHolder of the given type to represent an item.
    @NonNull
    @Override
    public ContactsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.contacts_list, parent, false);
        return new ContactsViewHolder(view);
    }

    // This method updates the contents of the RecyclerView.ViewHolder.itemView to reflect the
    // item at the given position.
    @Override
    public void onBindViewHolder(@NonNull ContactsViewHolder holder, int position) {
        ContactInfo contact = contactInfoList.get(position);
        if (contact != null){
            if (contact.getName() != null){
                holder.name.setText(contact.getName());
            }
        }
    }

    // This method returns the size of the collection that contains the items we want to display.
    @Override
    public int getItemCount() {
        return contactInfoList.size();
    }

    public interface ClickListener {
        void onItemClick(int position, View v);
        void onItemLongClick(int position, View v);
    }

    // =============================================================================================
    // Extension of the RecyclerView.ViewHolder
    public class ContactsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView name, number;

        public ContactsViewHolder(@NonNull View itemView) {
            super (itemView);
            itemView.setOnClickListener(this);
            name = itemView.findViewById(R.id.name);
        }

        @Override
        public void onClick(View v) {
            context = itemView.getContext();
            Intent intent = new Intent(context,ContactCard.class);
            intent.putExtra("id", contactInfoList.get(getAdapterPosition()).getID());
            context.startActivity(intent);
        }
    }
}
