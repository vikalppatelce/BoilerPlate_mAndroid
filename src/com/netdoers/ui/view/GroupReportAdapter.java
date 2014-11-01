package com.netdoers.ui.view;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.netdoers.beans.Group;
import com.netdoers.tellus.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class GroupReportAdapter extends BaseAdapter {
	ArrayList<Group> mGroup;
	Context mContext;
	LayoutInflater mInflater;
	DisplayImageOptions options;
	ImageLoader imageLoader;

	public GroupReportAdapter(Context mContext, ArrayList<Group> mGroup,
			ImageLoader imageLoader, DisplayImageOptions options) {
		// TODO Auto-generated constructor stub
		this.mGroup = mGroup;
		this.mContext = mContext;
		this.options = options;
		this.imageLoader = imageLoader;
		mInflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public void addGroup(ArrayList<Group> mGroup){
		this.mGroup = mGroup;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mGroup.size();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View v = null;
		if (convertView == null) {
			v = mInflater.inflate(R.layout.item_list_group, null);
		} else {
			v = convertView;
		}
		final CircleImageView mGroupImage = (CircleImageView) v
				.findViewById(R.id.item_list_group_image);
		final TextView mGroupName = (TextView)v.findViewById(R.id.item_list_group_name);
		final LinearLayout mGroupActions = (LinearLayout)v.findViewById(R.id.item_list_group_actions);
		
		mGroupName.setText(mGroup.get(position).getGroupName());
		mGroupActions.setVisibility(View.GONE);
		
		((Activity) mContext).runOnUiThread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				imageLoader.displayImage(mGroup.get(position)
						.getGroupImagePath(), mGroupImage, options);
			}
		});
		
		v.setTag(R.id.TAG_GROUP_ID, mGroup.get(position).getGroupId().toString());
		v.setTag(R.id.TAG_GROUP_NAME, mGroup.get(position).getGroupName().toString());
		v.setTag(R.id.TAG_GROUP_IMAGE, mGroup.get(position).getGroupImagePath().toString());
		return v;
	}

	@Override
	public Group getItem(int position) {
		// TODO Auto-generated method stub
		return mGroup.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}
}