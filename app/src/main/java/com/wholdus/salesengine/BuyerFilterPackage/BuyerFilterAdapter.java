package com.wholdus.salesengine.BuyerFilterPackage;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.wholdus.salesengine.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by kaustubh on 19/5/16.
 */
public class BuyerFilterAdapter extends BaseExpandableListAdapter {

    String LOG_TAG = "BuyerFilterAdapter";

    private Context mContext;
    private ArrayList<BuyerFilterModel> filterNames;

    public BuyerFilterAdapter(Context context, ArrayList<BuyerFilterModel> filterNames) {
        this.mContext = context;
        this.filterNames = filterNames;
    }

    @Override
    public FilterConditionModel getChild(int groupPosition, int childPosititon) {
        return this.filterNames.get(groupPosition).getFilterConditions().get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final String childText = getChild(groupPosition, childPosition).getConditionName();
        final boolean childBool = getChild(groupPosition, childPosition).getConditionApplied();

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_item_layout_buyer_filter, null);
        }

        TextView txtListChild = (TextView) convertView.findViewById(R.id.buyer_filter_list_group_item);
        CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.buyer_filter_list_group_item_checkbox);

        txtListChild.setText(childText);
        checkBox.setChecked(childBool);

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (childBool){
                    getChild(groupPosition, childPosition).setConditionApplied(false);
                }
                else {
                    getChild(groupPosition, childPosition).setConditionApplied(true);
                }
                notifyDataSetChanged();
            }
        });

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.filterNames.get(groupPosition).getFilterConditions().size();
    }

    @Override
    public BuyerFilterModel getGroup(int groupPosition) {
        return this.filterNames.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this.filterNames.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {

        String headerTitle = getGroup(groupPosition).getFilterName();

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_group_layout_buyer_filter, null);
        }

        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.buyer_filter_list_group_header);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
