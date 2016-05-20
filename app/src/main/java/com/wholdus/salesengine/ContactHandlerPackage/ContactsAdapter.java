package com.wholdus.salesengine.ContactHandlerPackage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wholdus.salesengine.R;

import java.util.List;

/**
 * Created by kaustubh on 19/5/16.
 */
public class ContactsAdapter extends BaseAdapter {

    String LOG_TAG = "ContactsAdapter";

    private LayoutInflater layoutInflater;
    private Context context;

    private List<ContactsModel> listData;

    public ContactsAdapter(Context context, List<ContactsModel> listData){

        this.context = context;
        this.listData = listData;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount(){
        return listData.size();
    }

    @Override
    public Object getItem(int position) {
        return listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        ViewHolder holder;
        if(convertView == null){
            convertView = layoutInflater.inflate(R.layout.list_item_layout_contacts, null);
            holder = new ViewHolder();
            holder.contactName = (TextView) convertView.findViewById(R.id.contacts_list_contact_name);
            holder.contactNumber = (TextView) convertView.findViewById(R.id.contacts_list_contact_number);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ContactsModel contactsModel = this.listData.get(position);

        holder.contactName.setText(contactsModel.getContactName());
        holder.contactNumber.setText(contactsModel.getContactNumber());

        return convertView;
    }

    static class ViewHolder{
        int id;
        TextView contactName;
        TextView contactNumber;
    }

}
