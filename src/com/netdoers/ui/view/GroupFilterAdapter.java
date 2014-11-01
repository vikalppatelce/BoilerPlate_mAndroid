package com.netdoers.ui.view;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.netdoers.beans.GroupFilter;
import com.netdoers.tellus.R;

public class GroupFilterAdapter extends BaseAdapter implements Filterable {

    Context context;
    ArrayList<GroupFilter> countrylist;
    ArrayList<GroupFilter> mStringFilterList;
    ValueFilter valueFilter;
    String TAG = GroupFilterAdapter.class.getSimpleName();

    public GroupFilterAdapter(Context context , ArrayList<GroupFilter> countrylist) {
        this.context = context;
        this.countrylist = countrylist;
        mStringFilterList = countrylist;
    }

    @Override
    public int getCount() {
        return countrylist.size();
    }

    @Override
    public Object getItem(int position) {
        return countrylist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position , View convertView , ViewGroup parent ) {

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        convertView = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_group_filter, null);
        }
        
        TextView name = (TextView) convertView.findViewById(R.id.item_group_filter_name);
        GroupFilter country = countrylist.get(position);
        name.setText(country.getGroupName());
        
        convertView.setTag(R.id.TAG_GROUP_ID, country.getGroupId());
        convertView.setTag(R.id.TAG_GROUP_NAME, country.getGroupName());
        
        return convertView;
    }

    @Override
    public Filter getFilter() {
        if (valueFilter == null) {
            valueFilter = new ValueFilter();
        }
        return valueFilter;
    }

    private class ValueFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            if (constraint != null && constraint.length() > 0) {
                ArrayList<GroupFilter> filterList = new ArrayList<GroupFilter>();
                for (int i = 0; i < mStringFilterList.size(); i++) {
                    if ( (mStringFilterList.get(i).getGroupName().toUpperCase() )
                            .contains(constraint.toString().toUpperCase())) {

                        GroupFilter country = new GroupFilter(mStringFilterList.get(i)
                                .getGroupId() ,  mStringFilterList.get(i)
                                .getGroupName());

                        filterList.add(country);
                    }
                }
                results.count = filterList.size();
                results.values = filterList;
            } else {
                results.count = mStringFilterList.size();
                results.values = mStringFilterList;
            }
            return results;

        }

        @Override
        protected void publishResults(CharSequence constraint,
                FilterResults results) {
            countrylist = (ArrayList<GroupFilter>) results.values;
            notifyDataSetChanged();
        }

    }

}

