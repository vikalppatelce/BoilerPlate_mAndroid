package com.netdoers.ui.view;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.netdoers.beans.Feed;
import com.netdoers.beans.FeedGroupSuggestion;
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

public class FeedGroupSuggestionAdapter extends BaseAdapter {
	ArrayList<FeedGroupSuggestion> mFeedGroupSuggestion;
	Context mContext;
	LayoutInflater mInflater;
	DisplayImageOptions options;
	ImageLoader imageLoader;
	private String TAG = FeedGroupSuggestionAdapter.class.getSimpleName();

	public FeedGroupSuggestionAdapter(Context mContext, ArrayList<FeedGroupSuggestion> mFeedGroupSuggestion,
			ImageLoader imageLoader, DisplayImageOptions options) {
		// TODO Auto-generated constructor stub
		this.mFeedGroupSuggestion = mFeedGroupSuggestion;
		this.mContext = mContext;
		this.options = options;
		this.imageLoader = imageLoader;
		mInflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mFeedGroupSuggestion.size();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View v;
		ViewHolder holder = null;
		final FeedGroupSuggestion mFeedGroupSuggestionObj;

		if (convertView == null) {
			LayoutInflater li = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = li.inflate(R.layout.item_grid_feed_group_suggestion, null);
			holder = new ViewHolder();
			holder.mFeedGroupImage = (ImageView) v.findViewById(R.id.item_grid_feed_group_suggestion_image);
			holder.mFeedGroupName = (TextView) v.findViewById(R.id.item_grid_feed_group_suggestion_text);
			holder.mFeedGroupSubscribe = (Button) v.findViewById(R.id.item_grid_feed_group_suggestion_subscribe);
			v.setTag(holder);
		} else {
			v = convertView;
			holder = (ViewHolder) v.getTag();
		}

		mFeedGroupSuggestionObj = mFeedGroupSuggestion.get(position);

		imageLoader.displayImage(mFeedGroupSuggestionObj
				.getGroupImagePath(),holder.mFeedGroupImage, options);
		
		holder.mFeedGroupName.setText(mFeedGroupSuggestionObj.getGroupName());
		
		if(mFeedGroupSuggestionObj.getGroupIsSubscribe().equalsIgnoreCase("0")){
			holder.mFeedGroupSubscribe.setText("Subscribe");	
		}else{
			holder.mFeedGroupSubscribe.setText("UnSubscribe");
		}
		
		holder.mFeedGroupSubscribe.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(mFeedGroupSuggestionObj.getGroupIsSubscribe().equalsIgnoreCase("0")){
					volleySubscribe(mFeedGroupSuggestionObj,"1");	
				}else{
					volleySubscribe(mFeedGroupSuggestionObj,"0");
				}
			}
		});
		return v;
	}

	@Override
	public FeedGroupSuggestion getItem(int position) {
		// TODO Auto-generated method stub
		return mFeedGroupSuggestion.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	static class ViewHolder{
		ImageView mFeedGroupImage;
		TextView mFeedGroupName;
		Button mFeedGroupSubscribe;
		}
	
	private void volleySubscribe(final FeedGroupSuggestion mFeedGroupSuggestionObj, final String toSubscribe) {
		final String TAG = "volleySubscribe";
		NetworkVolley nVolley = new NetworkVolley();
		JSONObject mJSONObject = RequestBuilder.getPostGroupSubscribeData(mFeedGroupSuggestionObj.getGroupId(),toSubscribe);
		Log.i(TAG, AppConstants.URLS.URL_POST_GROUP_SUBSCRIBE);
		Log.i(TAG, mJSONObject.toString());
		VolleyPostJsonRequest req = nVolley.new VolleyPostJsonRequest(AppConstants.URLS.URL_POST_GROUP_SUBSCRIBE, mJSONObject,
				new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						try {
							VolleyLog.v("Response:%n %s", response.toString(4));
							Log.i(TAG, response.toString());
							if (response.getBoolean("status")) {
								mFeedGroupSuggestionObj.setGroupIsSubscribe(toSubscribe);
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