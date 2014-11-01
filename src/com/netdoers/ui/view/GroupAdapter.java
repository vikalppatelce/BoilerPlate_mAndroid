package com.netdoers.ui.view;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.netdoers.beans.Feed;
import com.netdoers.beans.FeedComment;
import com.netdoers.beans.FeedMedia;
import com.netdoers.beans.Group;
import com.netdoers.tellus.BuildConfig;
import com.netdoers.tellus.R;
import com.netdoers.ui.GroupFeedActivity;
import com.netdoers.utils.AppConstants;
import com.netdoers.utils.ApplicationLoader;
import com.netdoers.utils.NetworkVolley;
import com.netdoers.utils.RequestBuilder;
import com.netdoers.utils.NetworkVolley.VolleyPostJsonRequest;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class GroupAdapter extends BaseAdapter {
	ArrayList<Group> mGroup;
	Context mContext;
	LayoutInflater mInflater;
	DisplayImageOptions options;
	ImageLoader imageLoader;
	boolean showOverFlowActions;

	public GroupAdapter(Context mContext, ArrayList<Group> mGroup,
			ImageLoader imageLoader, DisplayImageOptions options, boolean showOverFlowActions) {
		// TODO Auto-generated constructor stub
		this.mGroup = mGroup;
		this.mContext = mContext;
		this.options = options;
		this.imageLoader = imageLoader;
		mInflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.showOverFlowActions = showOverFlowActions;
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
		final TextView mGroupFriends = (TextView)v.findViewById(R.id.item_list_group_friends);
		final LinearLayout mGroupActions = (LinearLayout)v.findViewById(R.id.item_list_group_actions);
		
		mGroupActions.setVisibility(View.GONE);
		
		mGroupName.setText(mGroup.get(position).getGroupName());
		mGroupFriends.setText(mGroup.get(position).getGroupFriends());
		
		((Activity) mContext).runOnUiThread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				imageLoader.displayImage(mGroup.get(position)
						.getGroupImagePath(), mGroupImage, options);
			}
		});
		
		if(showOverFlowActions){
			if(!mGroup.get(position).getGroupIsAdmin().equalsIgnoreCase("1")){
				mGroupActions.setVisibility(View.VISIBLE);
				mGroupActions.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						setPopUpWindow(mContext, v, mGroup.get(position));
					}
				});	
			}
		}
		
		v.setTag(R.id.TAG_GROUP_ID, mGroup.get(position).getGroupId().toString());
		v.setTag(R.id.TAG_GROUP_NAME, mGroup.get(position).getGroupName().toString());
		v.setTag(R.id.TAG_GROUP_IMAGE, mGroup.get(position).getGroupImagePath().toString());
		v.setTag(R.id.TAG_GROUP_SUBSCRIBE, mGroup.get(position).getGroupIsSubscribe().toString());
		v.setTag(R.id.TAG_GROUP_ADMIN, mGroup.get(position).getGroupIsAdmin().toString());
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
	
	private void setPopUpWindow(final Context mContext, View mView, final Group mGroupObj){
		PopupMenu mPopUpMenu = new PopupMenu(mContext, mView);
		try{
			if(mGroupObj.getGroupIsSubscribe().equalsIgnoreCase("1")){
				mPopUpMenu.getMenuInflater().inflate(R.menu.menu_group_pop_up_unsubscribe, mPopUpMenu.getMenu());
			}else{
				mPopUpMenu.getMenuInflater().inflate(R.menu.menu_group_pop_up_subscribe, mPopUpMenu.getMenu());	
			}
		}catch(Exception e){
			mPopUpMenu.getMenuInflater().inflate(R.menu.menu_group_pop_up_subscribe, mPopUpMenu.getMenu());
		}
		mPopUpMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				// TODO Auto-generated method stub
				switch(item.getItemId()){
				case R.id.menu_group_subscribe:
					volleySubscribe(mGroupObj.getGroupId(), "1", mContext, mGroupObj);
					break;
				case R.id.menu_group_unsubscribe:
					volleySubscribe(mGroupObj.getGroupId(), "0",mContext, mGroupObj);
					break;
				}
				return false;
			}
		});
		mPopUpMenu.show();
	}
	
	private void volleySubscribe(String mGroupId, final String toSubscribe, final Context mContext, final Group mGroupObj) {
		final String TAG = "volleySubscribe";
		NetworkVolley nVolley = new NetworkVolley();
		JSONObject mJSONObject = RequestBuilder.getPostGroupSubscribeData(mGroupId,toSubscribe);
		Log.i(TAG, AppConstants.URLS.URL_POST_GROUP_SUBSCRIBE);
		Log.i(TAG, mJSONObject.toString());
		VolleyPostJsonRequest req = nVolley.new VolleyPostJsonRequest(AppConstants.URLS.URL_POST_GROUP_SUBSCRIBE, mJSONObject,
				new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						try {
							VolleyLog.v("Response:%n %s", response.toString(4));
							Log.i(TAG, response.toString());
							if (response.getBoolean("success")) {
								mGroupObj.setGroupIsSubscribe(toSubscribe);
								Toast.makeText(mContext, "Subscribed", Toast.LENGTH_SHORT).show();
								if (BuildConfig.DEBUG) {
									VolleyLog.v("Response:%n %s",
											response.toString(4));
									Log.i(TAG, response.toString());
								}
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}, new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						VolleyLog.e("Error: ", error.getMessage());
						if (BuildConfig.DEBUG) {
							Log.i(TAG, "Volley Error : " + error.getMessage());
							VolleyLog.e("Error: ", error.getMessage());
						}
					}
				});
		ApplicationLoader.getInstance().addToRequestQueue(req, TAG);
	}
}