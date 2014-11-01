package com.netdoers.ui.view;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.netdoers.beans.FeedComment;
import com.netdoers.beans.FeedMedia;
import com.netdoers.beans.Group;
import com.netdoers.beans.Notification;
import com.netdoers.tellus.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class NotificationAdapter extends BaseAdapter {
	ArrayList<Notification> mNotification;
	Context mContext;
	LayoutInflater mInflater;

	public NotificationAdapter(Context mContext,
			ArrayList<Notification> mNotification) {
		// TODO Auto-generated constructor stub
		this.mNotification = mNotification;
		this.mContext = mContext;
		mInflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public void addGroup(ArrayList<Notification> mNotification) {
		this.mNotification = mNotification;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mNotification.size();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View v = null;
		if (convertView == null) {
			v = mInflater.inflate(R.layout.item_list_notification, null);
		} else {
			v = convertView;
		}

		final TextView mNotificationTxt = (TextView) v
				.findViewById(R.id.item_list_notification_txt);
		final LinearLayout mNotificationLayout = (LinearLayout) v
				.findViewById(R.id.item_list_notification_layout);
		
		mNotificationTxt.setText(mNotification.get(position).getNotifFromUser()
				+ " " + mNotification.get(position).getNotifWhat() + " "
				+ mNotification.get(position).getNotifFromUserImage());
		
		if(mNotification.get(position).getNotifReadStatus().equalsIgnoreCase("0")){
			mNotificationLayout.setBackgroundColor(Color.parseColor("#FFFFAA"));
		}else{
			mNotificationLayout.setBackgroundColor(Color.WHITE);
		}
		
		v.setTag(R.id.TAG_NOTIFICAION_ID, mNotification.get(position).getNotifId().toString());
		v.setTag(R.id.TAG_POST_ID, mNotification.get(position).getNotifPage().toString());
		return v;
	}

	@Override
	public Notification getItem(int position) {
		// TODO Auto-generated method stub
		return mNotification.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}
}