package com.netdoers.ui.view;

import it.sephiroth.android.library.widget.HListView;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.PopupMenu;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.netdoers.beans.Feed;
import com.netdoers.tellus.BuildConfig;
import com.netdoers.tellus.R;
import com.netdoers.utils.AppConstants;
import com.netdoers.utils.ApplicationLoader;
import com.netdoers.utils.NetworkVolley;
import com.netdoers.utils.NetworkVolley.VolleyGetJsonRequest;
import com.netdoers.utils.Utilities;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class FeedAdapterBackUp extends BaseAdapter {

	Context mContext = null;
	ArrayList<Feed> mFeed = null;
	DisplayImageOptions options;
	ImageLoader imageLoader;
	private int mIncrement = 1;
	private int mDecrement = -1;
	private String TAG;

	public FeedAdapterBackUp(Context mContext, String TAG, ArrayList<Feed> mFeed,
			ImageLoader imageLoader, DisplayImageOptions options) {
		this.mContext = mContext;
		this.mFeed = mFeed;
		this.options = options;
		this.imageLoader = imageLoader;
		this.TAG = TAG;
	}
	
	public void addFeed(ArrayList<Feed> mFeed){
		this.mFeed = mFeed;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mFeed.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		// return coupons.get(position).getTitle();
		return mFeed.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@SuppressWarnings("deprecation")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View v;
		ViewHolder holder = null;
		Feed mFeedObj;
		
		/*final CircleImageView mFeedPostedImage;
		TextView mFeedPostedStatus;
//		Gallery mFeedGallery;
		HListView mFeedGallery;
		ImageView mFeedReport;
		ImageView mFeedLikeImage;
		TextView mFeedTime;
		TextView mFeedContent;
		ImageView mFeedTrue, mFeedFalse;
		TextView mFeedTrueCount,mFeedFalseCount;
		TextView mFeedTruePer, mFeedFalsePer, mFeedPer;
		TextView mFeedComment, mFeedShare, mFeedLike;*/

		if (convertView == null) {
			LayoutInflater li = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = li.inflate(R.layout.item_grid_feed, null);
			holder = new ViewHolder();
			holder.mFeedPostedImage = (CircleImageView) v.findViewById(R.id.item_grid_feed_profile_image);
			holder.mFeedPostedStatus = (TextView) v.findViewById(R.id.item_grid_feed_title);
			// mFeedGallery = (Gallery)v.findViewById(R.id.item_grid_feed_gallery);
			holder.mFeedGallery = (HListView) v.findViewById(R.id.item_grid_feed_gallery);
			holder.mFeedReport = (ImageView) v.findViewById(R.id.item_grid_feed_report);
			holder.mFeedTime = (TextView) v.findViewById(R.id.item_grid_feed_time);
			holder.mFeedTrue = (ImageView) v.findViewById(R.id.item_grid_feed_true);
			holder.mFeedTrueCount = (TextView) v.findViewById(R.id.item_grid_feed_true_count);
			holder.mFeedTruePer = (TextView) v.findViewById(R.id.item_grid_feed_true_bar);
			holder.mFeedPer = (TextView) v.findViewById(R.id.item_grid_feed_true_false_percent);
			holder.mFeedFalse = (ImageView) v.findViewById(R.id.item_grid_feed_false);
			holder.mFeedFalseCount = (TextView) v.findViewById(R.id.item_grid_feed_false_count);
			holder.mFeedFalsePer = (TextView) v.findViewById(R.id.item_grid_feed_false_bar);

			holder.mFeedLike = (TextView) v.findViewById(R.id.item_grid_feed_like);
			holder.mFeedComment = (TextView) v.findViewById(R.id.item_grid_feed_comment);
			holder.mFeedShare = (TextView) v.findViewById(R.id.item_grid_feed_share);
			holder.mFeedLikeImage = (ImageView) v.findViewById(R.id.item_grid_feed_like_image);			v.setTag(holder);
		} else {
			v = convertView;
			holder = (ViewHolder) v.getTag();
		}
		
		/*mFeedPostedImage = (CircleImageView) v
				.findViewById(R.id.item_grid_feed_profile_image);
		mFeedPostedStatus = (TextView) v
				.findViewById(R.id.item_grid_feed_title);
//		mFeedGallery = (Gallery)v.findViewById(R.id.item_grid_feed_gallery);
		mFeedGallery = (HListView)v.findViewById(R.id.item_grid_feed_gallery);
		mFeedReport = (ImageView) v.findViewById(R.id.item_grid_feed_report);
		mFeedTime = (TextView) v.findViewById(R.id.item_grid_feed_time);
		mFeedTrue = (ImageView) v.findViewById(R.id.item_grid_feed_true);
		mFeedTrueCount = (TextView) v.findViewById(R.id.item_grid_feed_true_count);
		mFeedTruePer = (TextView) v.findViewById(R.id.item_grid_feed_true_bar);
		mFeedPer = (TextView)v.findViewById(R.id.item_grid_feed_true_false_percent);
		mFeedFalse = (ImageView) v.findViewById(R.id.item_grid_feed_false);
		mFeedFalseCount = (TextView)v.findViewById(R.id.item_grid_feed_false_count);
		mFeedFalsePer = (TextView) v.findViewById(R.id.item_grid_feed_false_bar);
		
		mFeedLike = (TextView)v.findViewById(R.id.item_grid_feed_like);
		mFeedComment = (TextView)v.findViewById(R.id.item_grid_feed_comment);
		mFeedShare = (TextView)v.findViewById(R.id.item_grid_feed_share);
		mFeedLikeImage = (ImageView)v.findViewById(R.id.item_grid_feed_like_image);*/
		
		/*mFeedPostedImage = ViewHolder.Holder.get(convertView, R.id.item_grid_feed_profile_image); 
		mFeedPostedStatus = ViewHolder.Holder.get(convertView, R.id.item_grid_feed_title);
//		mFeedGallery = (Gallery)v.findViewById(R.id.item_grid_feed_gallery);
		mFeedGallery = ViewHolder.Holder.get(convertView, R.id.item_grid_feed_gallery);
		mFeedReport = ViewHolder.Holder.get(convertView, R.id.item_grid_feed_report);
		mFeedTime = ViewHolder.Holder.get(convertView, R.id.item_grid_feed_time);
		mFeedTrue = ViewHolder.Holder.get(convertView, R.id.item_grid_feed_true);
		mFeedTrueCount = ViewHolder.Holder.get(convertView, R.id.item_grid_feed_true_count);
		mFeedTruePer = ViewHolder.Holder.get(convertView, R.id.item_grid_feed_true_bar);
		mFeedPer = ViewHolder.Holder.get(convertView, R.id.item_grid_feed_true_false_percent);
		mFeedFalse = ViewHolder.Holder.get(convertView, R.id.item_grid_feed_false);
		mFeedFalseCount = ViewHolder.Holder.get(convertView, R.id.item_grid_feed_false_count);
		mFeedFalsePer = ViewHolder.Holder.get(convertView, R.id.item_grid_feed_false_bar);
		
		mFeedLike = ViewHolder.Holder.get(convertView, R.id.item_grid_feed_like);
		mFeedComment = ViewHolder.Holder.get(convertView, R.id.item_grid_feed_comment);
		mFeedShare = ViewHolder.Holder.get(convertView, R.id.item_grid_feed_share);
		mFeedLikeImage = ViewHolder.Holder.get(convertView, R.id.item_grid_feed_like_image);*/

		mFeedObj = mFeed.get(position);
		
		holder.mFeedPostedStatus.setText(Html.fromHtml("<b>"
				+ mFeedObj.getFeedPostedUser().toString() + "</b>"
				+ " posted in " + "<b>"
				+ mFeedObj.getFeedPostedInGroup() + "</b>"));
		
		calculatePer(mFeedObj);
		setLikeValues(holder.mFeedLike, holder.mFeedLikeImage, mFeedObj);
		
		setTrueFalseBarValues(holder.mFeedTruePer, holder.mFeedFalsePer,holder.mFeedPer, mFeedObj);		
		setTrueFalseButtonValues(holder.mFeedTrue, holder.mFeedFalse, holder.mFeedTrueCount,
				holder.mFeedFalseCount, mFeedObj);
		
		setFeedGallery(mContext, mFeed, position, holder.mFeedGallery, imageLoader,
				options);

		/*((Activity) mContext).runOnUiThread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				imageLoader.displayImage(mFeed.get(position)
						.getFeedPostedImage(),holder.mFeedPostedImage, options);
			}
		});*/
		
		setLikeEventListeners(holder.mFeedLike, holder.mFeedLikeImage, mFeedObj);
		setTrueFalseSelectedEventListeners(holder.mFeedTrue, holder.mFeedTrueCount,
				holder.mFeedTruePer, holder.mFeedFalse, holder.mFeedFalseCount, holder.mFeedFalsePer,
				holder.mFeedPer, mFeedObj);
		
		holder.mFeedReport.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				setPopUpWindow(mContext, v, mFeed, position);
			}
		});
		
		/*mFeedPostedStatus.setText(Html.fromHtml("<b>"
				+ mFeed.get(position).getFeedPostedUser().toString() + "</b>"
				+ " posted in " + "<b>"
				+ mFeed.get(position).getFeedPostedInGroup() + "</b>"));
		
		calculatePer(mFeed, position);
//		logFeedObj(mFeed, position);
//		Log.i(TAG, "mFeed Size : " + mFeed.size() + " position : " +position);
		setLikeValues(mFeedLike, mFeedLikeImage, mFeed, position);
		
		setTrueFalseBarValues(mFeedTruePer, mFeedFalsePer, mFeedPer, position);		
		setTrueFalseButtonValues(mFeedTrue, mFeedFalse, mFeedTrueCount,
				mFeedFalseCount, mFeed, position);
		
		setFeedGallery(mContext, mFeed, position, mFeedGallery, imageLoader,
				options);

		((Activity) mContext).runOnUiThread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				imageLoader.displayImage(mFeed.get(position)
						.getFeedPostedImage(), mFeedPostedImage, options);
			}
		});
		
		setLikeEventListeners(mFeedLike, mFeedLikeImage, mFeed, position);
		setTrueFalseSelectedEventListeners(mFeedTrue, mFeedTrueCount,
				mFeedTruePer, mFeedFalse, mFeedFalseCount, mFeedFalsePer,
				mFeedPer, mFeed, position);
		
		mFeedReport.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				setPopUpWindow(mContext, v, mFeed, position);
			}
		});*/
		return v;
	}
	
	
	/*private void setTrueFalseBarValues(TextView mFeedTruePer, TextView mFeedFalsePer, TextView mFeedPer, int position){
		final String TAG = "setTrueFalseBarValues";
		LinearLayout.LayoutParams paramsMFeedTruePer,paramsMFeedFalsePer;
		String strFeedTruePer = mFeed.get(position).getFeedTruePer().toString();
		String strFeedFalsePer = mFeed.get(position).getFeedFalsePer().toString();
		if(strFeedTruePer.equalsIgnoreCase("0") && strFeedFalsePer.equalsIgnoreCase("0")){
			paramsMFeedTruePer = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 0.0f);
			paramsMFeedFalsePer = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 0.0f);
			mFeedPer.setText("No Value");
		}else{
			paramsMFeedTruePer = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, Float.parseFloat(strFeedTruePer));
			paramsMFeedFalsePer = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, Float.parseFloat(strFeedFalsePer));
		}
		
		if(Integer.parseInt(strFeedTruePer) > Integer.parseInt(strFeedFalsePer)){
			mFeedPer.setText(strFeedTruePer +" % True");
		}else{
			mFeedPer.setText(strFeedFalsePer +" % False");
		}
		
		mFeedTruePer.setLayoutParams(paramsMFeedTruePer);
		mFeedFalsePer.setLayoutParams(paramsMFeedFalsePer);
		
		if(BuildConfig.DEBUG){
			if(AppConstants.DEBUG){
				Log.i(TAG, "True Per : " +strFeedTruePer);
				Log.i(TAG, "Feed Per : " +mFeedPer.getText().toString());
			}
		}
	}*/
	
	private void setTrueFalseBarValues(TextView mFeedTruePer, TextView mFeedFalsePer, TextView mFeedPer, Feed mFeedObj){
		final String TAG = "setTrueFalseBarValues";
		LinearLayout.LayoutParams paramsMFeedTruePer,paramsMFeedFalsePer;
		String strFeedTruePer = mFeedObj.getFeedTruePer().toString();
		String strFeedFalsePer = mFeedObj.getFeedFalsePer().toString();
		if(strFeedTruePer.equalsIgnoreCase("0") && strFeedFalsePer.equalsIgnoreCase("0")){
			paramsMFeedTruePer = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 0.0f);
			paramsMFeedFalsePer = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 0.0f);
			mFeedPer.setText("No Value");
		}else{
			paramsMFeedTruePer = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, Float.parseFloat(strFeedTruePer));
			paramsMFeedFalsePer = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, Float.parseFloat(strFeedFalsePer));
		}
		
		if(Integer.parseInt(strFeedTruePer) > Integer.parseInt(strFeedFalsePer)){
			mFeedPer.setText(strFeedTruePer +" % True");
		}else{
			mFeedPer.setText(strFeedFalsePer +" % False");
		}
		
		mFeedTruePer.setLayoutParams(paramsMFeedTruePer);
		mFeedFalsePer.setLayoutParams(paramsMFeedFalsePer);
		
		if(BuildConfig.DEBUG){
			if(AppConstants.DEBUG){
				Log.i(TAG, "True Per : " +strFeedTruePer);
				Log.i(TAG, "Feed Per : " +mFeedPer.getText().toString());
			}
		}
	}
	
	/*private void setTrueFalseSelectedEventListeners(final ImageView mFeedTrue,
			final TextView mFeedTrueCount, final TextView mFeedTruePer,
			final ImageView mFeedFalse, final TextView mFeedFalseCount,
			final TextView mFeedFalsePer, final TextView mFeedPer,
			final ArrayList<Feed> mFeed, final int position) {
		
			mFeedTrue.setOnClickListener(new View.OnClickListener() {
			private String TAG = "setTrueFalseSelectedEventListeners";
			private String PRETAG = "Pre";
				
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				((Activity) mContext).runOnUiThread(new Runnable() {
					@Override
					public void run() {
						setTrueBarAfterVolley(mFeed, position,
								mFeedTrue, mFeedTruePer, mFeedTrueCount,
								mFeedFalse, mFeedFalsePer,mFeedFalseCount,
								mFeedPer);
						// TODO Auto-generated method stub
						   if(BuildConfig.DEBUG){
							   if(AppConstants.DEBUG){
							   Log.i(PRETAG, "isTrue : " + mFeed.get(position).getFeedIsTrue().toString());
							   Log.i(PRETAG, "True Count : "+mFeed.get(position).getFeedTrueCount().toString());
							   Log.i(PRETAG, "True Per : "+mFeed.get(position).getFeedTruePer().toString());
							   Log.i(PRETAG, "isFalse : "+mFeed.get(position).getFeedIsFalse().toString());
							   Log.i(PRETAG, "False Count : "+mFeed.get(position).getFeedFalseCount().toString());
							   Log.i(PRETAG, "False Per :"+mFeed.get(position).getFeedFalsePer().toString());
							   }
						   }
					}
				});
				
				NetworkVolley nVolley = new NetworkVolley();
				String url = "http://myzname.netdoers.com/api/v1/znames/207b1ef1b96a11eb42c297bc36e84ddf94584953/callstatus/0";
				VolleyGetJsonRequest req = nVolley.new VolleyGetJsonRequest(url, null,
					       new Listener<JSONObject>() {
					           @Override
					           public void onResponse(JSONObject response) {
					               try {
					                   VolleyLog.v("Response:%n %s", response.toString(4));
					                   Log.i(TAG, response.toString());
					                   if(!response.getBoolean("status")){
										setTrueBarAfterVolley(mFeed, position,
												mFeedTrue, mFeedTruePer,mFeedTrueCount,
												mFeedFalse, mFeedFalsePer, mFeedFalseCount,
												mFeedPer);
									  }
					               } catch (JSONException e) {
					                   e.printStackTrace();
					               }
					           }
					       }, new ErrorListener() {
					           @Override
					           public void onErrorResponse(VolleyError error) {
					               VolleyLog.e("Error: ", error.getMessage());
					           }
					       });
				ApplicationLoader.getInstance().addToRequestQueue(req,TAG);
			}
		});
		
		mFeedFalse.setOnClickListener(new View.OnClickListener() {
			private String TAG = "setTrueFalseSelectedEventListeners";
			private String PRETAG = "Pre-setTrueFalseSelectedEventListeners";
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				((Activity) mContext).runOnUiThread(new Runnable() {
					@Override
					public void run() {
						setFalseBarAfterVolley(mFeed, position,
								mFeedTrue, mFeedTruePer,mFeedTrueCount,
								mFeedFalse, mFeedFalsePer,mFeedFalseCount,
								mFeedPer);
						// TODO Auto-generated method stub
						if(BuildConfig.DEBUG){
							if(AppConstants.DEBUG){
							   Log.i(PRETAG, "isTrue : " + mFeed.get(position).getFeedIsTrue().toString());
							   Log.i(PRETAG, "True Count : "+mFeed.get(position).getFeedTrueCount().toString());
							   Log.i(PRETAG, "True Per : "+mFeed.get(position).getFeedTruePer().toString());
							   Log.i(PRETAG, "isFalse : "+mFeed.get(position).getFeedIsFalse().toString());
							   Log.i(PRETAG, "False Count : "+mFeed.get(position).getFeedFalseCount().toString());
							   Log.i(PRETAG, "False Per :"+mFeed.get(position).getFeedFalsePer().toString());
							}
						   }
					}
				});
				
				NetworkVolley nVolley = new NetworkVolley();
				String url = "http://myzname.netdoers.com/api/v1/znames/207b1ef1b96a11eb42c297bc36e84ddf94584953/callstatus/0";
				VolleyGetJsonRequest req = nVolley.new VolleyGetJsonRequest(url, null,
					       new Listener<JSONObject>() {
					           @Override
					           public void onResponse(JSONObject response) {
					               try {
					                   VolleyLog.v("Response:%n %s", response.toString(4));
					                   Log.i(TAG, response.toString());
					                   if(!response.getBoolean("status")){
					                	   setFalseBarAfterVolley(mFeed, position,
													mFeedTrue, mFeedTruePer,mFeedTrueCount,
													mFeedFalse, mFeedFalsePer,mFeedFalseCount,
													mFeedPer);
									  }
					               } catch (JSONException e) {
					                   e.printStackTrace();
					               }
					           }
					       }, new ErrorListener() {
					           @Override
					           public void onErrorResponse(VolleyError error) {
					               VolleyLog.e("Error: ", error.getMessage());
					           }
					       });
				ApplicationLoader.getInstance().addToRequestQueue(req,TAG);
			}
		});
	}*/
	
	
	private void setTrueFalseSelectedEventListeners(final ImageView mFeedTrue,
			final TextView mFeedTrueCount, final TextView mFeedTruePer,
			final ImageView mFeedFalse, final TextView mFeedFalseCount,
			final TextView mFeedFalsePer, final TextView mFeedPer,
			final Feed mFeedObj) {
		
			mFeedTrue.setOnClickListener(new View.OnClickListener() {
			private String TAG = "setTrueFalseSelectedEventListeners";
			private String PRETAG = "Pre";
				
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				((Activity) mContext).runOnUiThread(new Runnable() {
					@Override
					public void run() {
						setTrueBarAfterVolley(mFeedObj,
								mFeedTrue, mFeedTruePer, mFeedTrueCount,
								mFeedFalse, mFeedFalsePer,mFeedFalseCount,
								mFeedPer);
						// TODO Auto-generated method stub
						   if(BuildConfig.DEBUG){
							   if(AppConstants.DEBUG){
							   Log.i(PRETAG, "isTrue : " + mFeedObj.getFeedIsTrue().toString());
							   Log.i(PRETAG, "True Count : "+mFeedObj.getFeedTrueCount().toString());
							   Log.i(PRETAG, "True Per : "+mFeedObj.getFeedTruePer().toString());
							   Log.i(PRETAG, "isFalse : "+mFeedObj.getFeedIsFalse().toString());
							   Log.i(PRETAG, "False Count : "+mFeedObj.getFeedFalseCount().toString());
							   Log.i(PRETAG, "False Per :"+mFeedObj.getFeedFalsePer().toString());
							   }
						   }
					}
				});
				
				NetworkVolley nVolley = new NetworkVolley();
				String url = "http://myzname.netdoers.com/api/v1/znames/207b1ef1b96a11eb42c297bc36e84ddf94584953/callstatus/0";
				VolleyGetJsonRequest req = nVolley.new VolleyGetJsonRequest(url, null,
					       new Listener<JSONObject>() {
					           @Override
					           public void onResponse(JSONObject response) {
					               try {
					                   VolleyLog.v("Response:%n %s", response.toString(4));
					                   Log.i(TAG, response.toString());
					                   if(!response.getBoolean("status")){
										setTrueBarAfterVolley(mFeedObj,
												mFeedTrue, mFeedTruePer,mFeedTrueCount,
												mFeedFalse, mFeedFalsePer, mFeedFalseCount,
												mFeedPer);
									  }
					               } catch (JSONException e) {
					                   e.printStackTrace();
					               }
					           }
					       }, new ErrorListener() {
					           @Override
					           public void onErrorResponse(VolleyError error) {
					               VolleyLog.e("Error: ", error.getMessage());
					           }
					       });
				ApplicationLoader.getInstance().addToRequestQueue(req,TAG);
			}
		});
		
		mFeedFalse.setOnClickListener(new View.OnClickListener() {
			private String TAG = "setTrueFalseSelectedEventListeners";
			private String PRETAG = "Pre-setTrueFalseSelectedEventListeners";
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				((Activity) mContext).runOnUiThread(new Runnable() {
					@Override
					public void run() {
						setFalseBarAfterVolley(mFeedObj,
								mFeedTrue, mFeedTruePer,mFeedTrueCount,
								mFeedFalse, mFeedFalsePer,mFeedFalseCount,
								mFeedPer);
						// TODO Auto-generated method stub
						if(BuildConfig.DEBUG){
							if(AppConstants.DEBUG){
							   Log.i(PRETAG, "isTrue : " + mFeedObj.getFeedIsTrue().toString());
							   Log.i(PRETAG, "True Count : "+mFeedObj.getFeedTrueCount().toString());
							   Log.i(PRETAG, "True Per : "+mFeedObj.getFeedTruePer().toString());
							   Log.i(PRETAG, "isFalse : "+mFeedObj.getFeedIsFalse().toString());
							   Log.i(PRETAG, "False Count : "+mFeedObj.getFeedFalseCount().toString());
							   Log.i(PRETAG, "False Per :"+mFeedObj.getFeedFalsePer().toString());
							}
						   }
					}
				});
				
				NetworkVolley nVolley = new NetworkVolley();
				String url = "http://myzname.netdoers.com/api/v1/znames/207b1ef1b96a11eb42c297bc36e84ddf94584953/callstatus/0";
				VolleyGetJsonRequest req = nVolley.new VolleyGetJsonRequest(url, null,
					       new Listener<JSONObject>() {
					           @Override
					           public void onResponse(JSONObject response) {
					               try {
					                   VolleyLog.v("Response:%n %s", response.toString(4));
					                   Log.i(TAG, response.toString());
					                   if(!response.getBoolean("status")){
					                	   setFalseBarAfterVolley(mFeedObj,
													mFeedTrue, mFeedTruePer,mFeedTrueCount,
													mFeedFalse, mFeedFalsePer,mFeedFalseCount,
													mFeedPer);
									  }
					               } catch (JSONException e) {
					                   e.printStackTrace();
					               }
					           }
					       }, new ErrorListener() {
					           @Override
					           public void onErrorResponse(VolleyError error) {
					               VolleyLog.e("Error: ", error.getMessage());
					           }
					       });
				ApplicationLoader.getInstance().addToRequestQueue(req,TAG);
			}
		});
	}
	/*private void setTrueFalseButtonValues(ImageView mFeedTrue, ImageView mFeedFalse, TextView mFeedTrueCount, TextView mFeedFalseCount, ArrayList<Feed> mFeed, int position){
		final String TAG = "setTrueFalseButtonValues";
      if(mFeed.get(position).getFeedIsTrue().toString().equalsIgnoreCase("1")){
    	  mFeedTrue.setImageResource(R.drawable.ic_true_pressed);
     	  mFeedFalse.setImageResource(R.drawable.ic_false_default);
      }else{
    	  mFeedTrue.setImageResource(R.drawable.ic_true_default);
    	  mFeedFalse.setImageResource(R.drawable.ic_false_pressed);
      }
      mFeedTrueCount.setText(mFeed.get(position).getFeedTrueCount());
      mFeedFalseCount.setText(mFeed.get(position).getFeedFalseCount());
      
      if(BuildConfig.DEBUG){
    	  if(AppConstants.DEBUG){
    	  Log.i(TAG, "Is True : Position :"+ position +" : " +mFeed.get(position).getFeedIsTrue());
          Log.i(TAG, "True Count : Position :"+ position +" : " +mFeed.get(position).getFeedTrueCount());
          Log.i(TAG, "False Count : Position :"+ position +" : " +mFeed.get(position).getFeedFalseCount());
    	  }
      }
	}*/
	
	private void setTrueFalseButtonValues(ImageView mFeedTrue, ImageView mFeedFalse, TextView mFeedTrueCount, TextView mFeedFalseCount, Feed mFeedObj){
		final String TAG = "setTrueFalseButtonValues";
      if(mFeedObj.getFeedIsTrue().toString().equalsIgnoreCase("1")){
    	  mFeedTrue.setImageResource(R.drawable.ic_true_pressed);
     	  mFeedFalse.setImageResource(R.drawable.ic_false_default);
      }else{
    	  mFeedTrue.setImageResource(R.drawable.ic_true_default);
    	  mFeedFalse.setImageResource(R.drawable.ic_false_pressed);
      }
      mFeedTrueCount.setText(mFeedObj.getFeedTrueCount());
      mFeedFalseCount.setText(mFeedObj.getFeedFalseCount());
      
      if(BuildConfig.DEBUG){
    	  if(AppConstants.DEBUG){
    	  Log.i(TAG, "Is True : Position :" +mFeedObj.getFeedIsTrue());
          Log.i(TAG, "True Count : Position " +mFeedObj.getFeedTrueCount());
          Log.i(TAG, "False Count : Position :" +mFeedObj.getFeedFalseCount());
    	  }
      }
	}
	
	private void setPopUpWindow(final Context mContext, View mView, final ArrayList<Feed> mFeed, final int position){
		PopupMenu mPopUpMenu = new PopupMenu(mContext, mView);
		mPopUpMenu.getMenuInflater().inflate(R.menu.menu_feed_pop_up_report, mPopUpMenu.getMenu());
		mPopUpMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				// TODO Auto-generated method stub
				switch(item.getItemId()){
				case R.id.menu_feed_report:
					Toast.makeText(mContext, "Report "+ mFeed.get(position).getFeedId(), Toast.LENGTH_SHORT).show();
					break;
//				case R.id.menu_feed_vote:
//					Toast.makeText(mContext, "Vote "+ mFeed.get(position).getFeedId(), Toast.LENGTH_SHORT).show();
//					break;
				}
				return false;
			}
		});
		mPopUpMenu.show();
	}
	
	private void setFeedGallery(Context mContext, ArrayList<Feed> mFeed,
			int position, HListView mFeedGallery,ImageLoader imageLoader, DisplayImageOptions options) {
		FeedGalleryAdapter mFeedGalleryAdapter = new FeedGalleryAdapter(mContext, mFeed.get(position).getFeedMedia(), imageLoader, options);
		mFeedGallery.setAdapter(mFeedGalleryAdapter);
	}
	
	/*private void setLikeEventListeners(final TextView mFeedLike,
			final ImageView mFeedLikeImage, final ArrayList<Feed> mFeed, final int position) {
		final String TAG = "setLikeEventListeners";
		final String PRETAG = "setLikeEventListener-Pre";
		
		mFeedLike.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
				
				((Activity) mContext).runOnUiThread(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						   if(mFeed.get(position).getFeedIsLike().toString().equalsIgnoreCase("0")){
		       					mFeedLike.setTextColor(Color.BLUE);
		       					mFeedLikeImage.setImageResource(R.drawable.feed_like_pressed);
		       				}else{
		       					mFeedLike.setTextColor(Color.BLACK);
		       					mFeedLikeImage.setImageResource(R.drawable.feed_like_default);
		       				}
						   if(BuildConfig.DEBUG){
							   if(AppConstants.DEBUG)
							   Log.i(PRETAG, mFeed.get(position).getFeedIsLike().toString());
						   }
					}
				});
				
				// TODO Auto-generated method stub
				NetworkVolley nVolley = new NetworkVolley();
				String url = "http://myzname.netdoers.com/api/v1/znames/207b1ef1b96a11eb42c297bc36e84ddf94584953/callstatus/0";
				VolleyGetJsonRequest req = nVolley.new VolleyGetJsonRequest(url, null,
					       new Listener<JSONObject>() {
					           @Override
					           public void onResponse(JSONObject response) {
					               try {
					                   if(!response.getBoolean("status")){
					                	   if(mFeed.get(position).getFeedIsLike().toString().equalsIgnoreCase("0")){
					       					mFeed.get(position).setFeedIsLike("1");
					       					mFeedLike.setTextColor(Color.BLUE);
					       					mFeedLikeImage.setImageResource(R.drawable.feed_like_pressed);
					       				}else{
					       					mFeed.get(position).setFeedIsLike("0");
					       					mFeedLike.setTextColor(Color.BLACK);
					       					mFeedLikeImage.setImageResource(R.drawable.feed_like_default);
					       				}
					                	   if(BuildConfig.DEBUG){
					                		   if(AppConstants.DEBUG){
					                		   VolleyLog.v("Response:%n %s", response.toString(4));
					                		   Log.i(TAG, response.toString());
				                			   Log.i(TAG, mFeed.get(position).getFeedIsLike().toString());
					                		   }
					                	   }
									  }
					               } catch (JSONException e) {
					                   e.printStackTrace();
					               }
					           }
					       }, new ErrorListener() {
					           @Override
					           public void onErrorResponse(VolleyError error) {
			                	   if(mFeed.get(position).getFeedIsLike().toString().equalsIgnoreCase("0")){
			       					mFeedLike.setTextColor(Color.BLACK);
			       					mFeedLikeImage.setImageResource(R.drawable.feed_like_default);
			       					}else{
			       					mFeedLike.setTextColor(Color.BLUE);
			       					mFeedLikeImage.setImageResource(R.drawable.feed_like_pressed);
			       					}
			                	   if(BuildConfig.DEBUG){
			                		   if(AppConstants.DEBUG){
			                		   Log.i(TAG, "Error : "+mFeed.get(position).getFeedIsLike().toString());
			                		   VolleyLog.e("Error: ", error.getMessage());
			                		   }
			                	   }
					           }
					       });
				ApplicationLoader.getInstance().addToRequestQueue(req,TAG);
			}
		});
	}*/
	
	
	private void setLikeEventListeners(final TextView mFeedLike,
			final ImageView mFeedLikeImage, final Feed mFeedObj) {
		final String TAG = "setLikeEventListeners";
		final String PRETAG = "setLikeEventListener-Pre";
		
		mFeedLike.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
				
				((Activity) mContext).runOnUiThread(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						   if(mFeedObj.getFeedIsLike().toString().equalsIgnoreCase("0")){
		       					mFeedLike.setTextColor(Color.BLUE);
		       					mFeedLikeImage.setImageResource(R.drawable.feed_like_pressed);
		       				}else{
		       					mFeedLike.setTextColor(Color.BLACK);
		       					mFeedLikeImage.setImageResource(R.drawable.feed_like_default);
		       				}
						   if(BuildConfig.DEBUG){
							   if(AppConstants.DEBUG)
							   Log.i(PRETAG, mFeedObj.getFeedIsLike().toString());
						   }
					}
				});
				
				// TODO Auto-generated method stub
				NetworkVolley nVolley = new NetworkVolley();
				String url = "http://myzname.netdoers.com/api/v1/znames/207b1ef1b96a11eb42c297bc36e84ddf94584953/callstatus/0";
				VolleyGetJsonRequest req = nVolley.new VolleyGetJsonRequest(url, null,
					       new Listener<JSONObject>() {
					           @Override
					           public void onResponse(JSONObject response) {
					               try {
					                   if(!response.getBoolean("status")){
					                	   if(mFeedObj.getFeedIsLike().toString().equalsIgnoreCase("0")){
					       					mFeedObj.setFeedIsLike("1");
					       					mFeedLike.setTextColor(Color.BLUE);
					       					mFeedLikeImage.setImageResource(R.drawable.feed_like_pressed);
					       				}else{
					       					mFeedObj.setFeedIsLike("0");
					       					mFeedLike.setTextColor(Color.BLACK);
					       					mFeedLikeImage.setImageResource(R.drawable.feed_like_default);
					       				}
					                	   if(BuildConfig.DEBUG){
					                		   if(AppConstants.DEBUG){
					                		   VolleyLog.v("Response:%n %s", response.toString(4));
					                		   Log.i(TAG, response.toString());
				                			   Log.i(TAG, mFeedObj.getFeedIsLike().toString());
					                		   }
					                	   }
									  }
					               } catch (JSONException e) {
					                   e.printStackTrace();
					               }
					           }
					       }, new ErrorListener() {
					           @Override
					           public void onErrorResponse(VolleyError error) {
			                	   if(mFeedObj.getFeedIsLike().toString().equalsIgnoreCase("0")){
			       					mFeedLike.setTextColor(Color.BLACK);
			       					mFeedLikeImage.setImageResource(R.drawable.feed_like_default);
			       					}else{
			       					mFeedLike.setTextColor(Color.BLUE);
			       					mFeedLikeImage.setImageResource(R.drawable.feed_like_pressed);
			       					}
			                	   if(BuildConfig.DEBUG){
			                		   if(AppConstants.DEBUG){
			                		   Log.i(TAG, "Error : "+mFeedObj.getFeedIsLike().toString());
			                		   VolleyLog.e("Error: ", error.getMessage());
			                		   }
			                	   }
					           }
					       });
				ApplicationLoader.getInstance().addToRequestQueue(req,TAG);
			}
		});
	}
	/*private void setLikeValues(TextView mFeedLike, ImageView mFeedLikeImage,
			ArrayList<Feed> mFeed, int position) {
		if (mFeed.get(position).getFeedIsLike().toString().equalsIgnoreCase("0")) {
			mFeedLike.setTextColor(Color.BLACK);
			mFeedLikeImage.setImageResource(R.drawable.feed_like_default);
		} else {
			mFeedLike.setTextColor(Color.BLUE);
			mFeedLikeImage.setImageResource(R.drawable.feed_like_pressed);
		}
		
		if(BuildConfig.DEBUG){
			if(AppConstants.DEBUG)
			Log.i(TAG, "Is Like : Position :"+ position +" : " +mFeed.get(position).getFeedIsLike());
		}
	}*/
	
	private void setLikeValues(TextView mFeedLike, ImageView mFeedLikeImage,
			Feed mFeedObj) {
		if (mFeedObj.getFeedIsLike().toString().equalsIgnoreCase("0")) {
			mFeedLike.setTextColor(Color.BLACK);
			mFeedLikeImage.setImageResource(R.drawable.feed_like_default);
		} else {
			mFeedLike.setTextColor(Color.BLUE);
			mFeedLikeImage.setImageResource(R.drawable.feed_like_pressed);
		}
		
		if(BuildConfig.DEBUG){
			if(AppConstants.DEBUG)
				Log.i(TAG, "Is Like :" + mFeedObj.getFeedIsLike());
		}
	}
	
	/*private void calculatePer(ArrayList<Feed> mFeed, int position) {
		mFeed.get(position).setFeedTruePer(
				String.valueOf(Utilities.getTrueFalsePer(Integer.parseInt(mFeed
						.get(position).getFeedTrueCount()), Integer
						.parseInt(mFeed.get(position).getFeedFalseCount()), 0,
						0)));
		mFeed.get(position).setFeedFalsePer(
				String.valueOf(100 - Integer.parseInt(mFeed.get(position)
						.getFeedTruePer())));
	}*/
	
	private void calculatePer(Feed mFeedObj) {
		mFeedObj.setFeedTruePer(
				String.valueOf(Utilities.getTrueFalsePer(Integer.parseInt(mFeedObj.getFeedTrueCount()), Integer
						.parseInt(mFeedObj.getFeedFalseCount()), 0,
						0)));
		mFeedObj.setFeedFalsePer(
				String.valueOf(100 - Integer.parseInt(mFeedObj.getFeedTruePer())));
	}
	
/*	private void afterTrueFalseEvent(ArrayList<Feed> mFeed, int position,
			int mFeedTrueFalsePer, ImageView mFeedTrue, TextView mFeedTrueCount,int isTrue,
			int trueDrawable, int mTrueAdd,ImageView mFeedFalse, TextView mFeedFalseCount,int isFalse,
			int falseDrawable, int mFalseAdd) {
		
		final String TAG = "afterTrueFalseEvent";
		String strFeedTrueCount = mFeed.get(position).getFeedTrueCount();
		String strFeedFalseCount = mFeed.get(position).getFeedFalseCount();
		
		mFeed.get(position).setFeedIsTrue(String.valueOf(isTrue));
		mFeed.get(position).setFeedIsFalse(String.valueOf(isFalse));

		mFeed.get(position).setFeedTruePer(String.valueOf(mFeedTrueFalsePer));
		mFeed.get(position).setFeedFalsePer(
				String.valueOf(100 - mFeedTrueFalsePer));
		
		Log.i(TAG, "Before True Count :  " + strFeedTrueCount);
		Log.i(TAG, "Before False Count :  " + strFeedFalseCount);
		
		mFeed.get(position).setFeedTrueCount(
				String.valueOf(Integer.parseInt(strFeedTrueCount) + mTrueAdd));
		mFeed.get(position).setFeedFalseCount(
				String.valueOf(Integer.parseInt(strFeedFalseCount) + mFalseAdd));
		
		strFeedTrueCount = mFeed.get(position).getFeedTrueCount();
		strFeedFalseCount = mFeed.get(position).getFeedFalseCount();
		
		mFeedTrueCount.setText(strFeedTrueCount);
		mFeedFalseCount.setText(strFeedFalseCount);

		mFeedFalse.setImageResource(falseDrawable);
		mFeedTrue.setImageResource(trueDrawable);
		
		if(BuildConfig.DEBUG){
			if(AppConstants.DEBUG){
			Log.i(TAG, "True Per : " + String.valueOf(mFeedTrueFalsePer));
			Log.i(TAG, "False Per : " + String.valueOf(100 - mFeedTrueFalsePer));
			Log.i(TAG, "True Count :  " + strFeedTrueCount);
			Log.i(TAG, "False Count :  " + strFeedFalseCount);
			}
		}
	}*/
	
	private void afterTrueFalseEvent(Feed mFeedObj,
			int mFeedTrueFalsePer, ImageView mFeedTrue, TextView mFeedTrueCount,int isTrue,
			int trueDrawable, int mTrueAdd,ImageView mFeedFalse, TextView mFeedFalseCount,int isFalse,
			int falseDrawable, int mFalseAdd) {
		
		final String TAG = "afterTrueFalseEvent";
		String strFeedTrueCount = mFeedObj.getFeedTrueCount();
		String strFeedFalseCount = mFeedObj.getFeedFalseCount();
		
		mFeedObj.setFeedIsTrue(String.valueOf(isTrue));
		mFeedObj.setFeedIsFalse(String.valueOf(isFalse));

		mFeedObj.setFeedTruePer(String.valueOf(mFeedTrueFalsePer));
		mFeedObj.setFeedFalsePer(
				String.valueOf(100 - mFeedTrueFalsePer));
		
		Log.i(TAG, "Before True Count :  " + strFeedTrueCount);
		Log.i(TAG, "Before False Count :  " + strFeedFalseCount);
		
		mFeedObj.setFeedTrueCount(
				String.valueOf(Integer.parseInt(strFeedTrueCount) + mTrueAdd));
		mFeedObj.setFeedFalseCount(
				String.valueOf(Integer.parseInt(strFeedFalseCount) + mFalseAdd));
		
		strFeedTrueCount = mFeedObj.getFeedTrueCount();
		strFeedFalseCount = mFeedObj.getFeedFalseCount();
		
		mFeedTrueCount.setText(strFeedTrueCount);
		mFeedFalseCount.setText(strFeedFalseCount);

		mFeedFalse.setImageResource(falseDrawable);
		mFeedTrue.setImageResource(trueDrawable);
		
		if(BuildConfig.DEBUG){
			if(AppConstants.DEBUG){
			Log.i(TAG, "True Per : " + String.valueOf(mFeedTrueFalsePer));
			Log.i(TAG, "False Per : " + String.valueOf(100 - mFeedTrueFalsePer));
			Log.i(TAG, "True Count :  " + strFeedTrueCount);
			Log.i(TAG, "False Count :  " + strFeedFalseCount);
			}
		}
	}
/*	private void setTrueBarAfterVolley(ArrayList<Feed> mFeed, int position, ImageView mFeedTrue, TextView mFeedTruePer, TextView mFeedTrueCount, ImageView mFeedFalse, TextView mFeedFalsePer, TextView mFeedFalseCount, TextView mFeedPer){
		final String TAG = "setTrueBarAfterVolley";
		if(mFeed.get(position).getFeedIsTrue().toString().equalsIgnoreCase("0") && mFeed.get(position).getFeedIsFalse().toString().equalsIgnoreCase("0")){
				
			mFeed.get(position).setFeedIsTrue("1");
			int mFeedTrueFalsePer = Utilities.getTrueFalsePer(Integer
					.parseInt(mFeed.get(position).getFeedTrueCount()),
					Integer.parseInt(mFeed.get(position)
							.getFeedFalseCount()), mIncrement, 0);
			
			if(BuildConfig.DEBUG){
				if(AppConstants.DEBUG){
				Log.i(TAG, "Before True Per :" +  mFeed.get(position).getFeedTruePer());
				Log.i(TAG, "True Per :" +  String.valueOf(mFeedTrueFalsePer));
				}
			}
			
			afterTrueFalseEvent(mFeed,
					position,
					mFeedTrueFalsePer,
					mFeedTrue,
					mFeedTrueCount,
					1,
					R.drawable.ic_true_pressed,
					1,
					mFeedFalse,
					mFeedFalseCount,
					0,
					R.drawable.ic_false_default,
					0
					);
			
			setTrueFalseBarValues(mFeedTruePer, mFeedFalsePer, mFeedPer, position);
		}else if(mFeed.get(position).getFeedIsTrue().toString().equalsIgnoreCase("0") && mFeed.get(position).getFeedIsFalse().toString().equalsIgnoreCase("1")){
				
				int mFeedTrueFalsePer = Utilities.getTrueFalsePer(Integer
					.parseInt(mFeed.get(position).getFeedTrueCount()),
					Integer.parseInt(mFeed.get(position)
							.getFeedFalseCount()), mIncrement, mDecrement);

				if(BuildConfig.DEBUG){
					if(AppConstants.DEBUG){
					Log.i(TAG, "Before True Per :" +  mFeed.get(position).getFeedTruePer());
					Log.i(TAG, "True Per :" +  String.valueOf(mFeedTrueFalsePer));
					}
				}
				
			afterTrueFalseEvent(mFeed,
					position,
					mFeedTrueFalsePer,
					mFeedTrue,
					mFeedTrueCount,
					1,
					R.drawable.ic_true_pressed,
					1,
					mFeedFalse,
					mFeedFalseCount,
					0,
					R.drawable.ic_false_default,
					-1);
			
			setTrueFalseBarValues(mFeedTruePer, mFeedFalsePer, mFeedPer, position);
		}else if(mFeed.get(position).getFeedIsTrue().toString().equalsIgnoreCase("1") && mFeed.get(position).getFeedIsFalse().toString().equalsIgnoreCase("0")){
				
				int mFeedTrueFalsePer = Utilities.getTrueFalsePer(Integer
					.parseInt(mFeed.get(position).getFeedTrueCount()),
					Integer.parseInt(mFeed.get(position)
							.getFeedFalseCount()), mDecrement, 0);
				
				if(BuildConfig.DEBUG){
					if(AppConstants.DEBUG){
					Log.i(TAG, "Before True Per :" +  mFeed.get(position).getFeedTruePer());
					Log.i(TAG, "True Per :" +  String.valueOf(mFeedTrueFalsePer));
					}
				}
				
			afterTrueFalseEvent(mFeed,
					position,
					mFeedTrueFalsePer,
					mFeedTrue,
					mFeedTrueCount,
					0,
					R.drawable.ic_true_pressed,
					-1,
					mFeedFalse,
					mFeedFalseCount,
					0,
					R.drawable.ic_false_default,
					0);
			setTrueFalseBarValues(mFeedTruePer, mFeedFalsePer, mFeedPer, position);
		}
	}*/

	private void setTrueBarAfterVolley(Feed mFeedObj, ImageView mFeedTrue, TextView mFeedTruePer, TextView mFeedTrueCount, ImageView mFeedFalse, TextView mFeedFalsePer, TextView mFeedFalseCount, TextView mFeedPer){
		final String TAG = "setTrueBarAfterVolley";
		if(mFeedObj.getFeedIsTrue().toString().equalsIgnoreCase("0") && mFeedObj.getFeedIsFalse().toString().equalsIgnoreCase("0")){
				
			mFeedObj.setFeedIsTrue("1");
			int mFeedTrueFalsePer = Utilities.getTrueFalsePer(Integer
					.parseInt(mFeedObj.getFeedTrueCount()),
					Integer.parseInt(mFeedObj.getFeedFalseCount()), mIncrement, 0);
			
			if(BuildConfig.DEBUG){
				if(AppConstants.DEBUG){
				Log.i(TAG, "Before True Per :" +  mFeedObj.getFeedTruePer());
				Log.i(TAG, "True Per :" +  String.valueOf(mFeedTrueFalsePer));
				}
			}
			
			afterTrueFalseEvent(mFeedObj,
					mFeedTrueFalsePer,
					mFeedTrue,
					mFeedTrueCount,
					1,
					R.drawable.ic_true_pressed,
					1,
					mFeedFalse,
					mFeedFalseCount,
					0,
					R.drawable.ic_false_default,
					0
					);
			
			setTrueFalseBarValues(mFeedTruePer, mFeedFalsePer, mFeedPer, mFeedObj);
		}else if(mFeedObj.getFeedIsTrue().toString().equalsIgnoreCase("0") && mFeedObj.getFeedIsFalse().toString().equalsIgnoreCase("1")){
				
				int mFeedTrueFalsePer = Utilities.getTrueFalsePer(Integer
					.parseInt(mFeedObj.getFeedTrueCount()),
					Integer.parseInt(mFeedObj
							.getFeedFalseCount()), mIncrement, mDecrement);

				if(BuildConfig.DEBUG){
					if(AppConstants.DEBUG){
					Log.i(TAG, "Before True Per :" +  mFeedObj.getFeedTruePer());
					Log.i(TAG, "True Per :" +  String.valueOf(mFeedTrueFalsePer));
					}
				}
				
			afterTrueFalseEvent(mFeedObj,
					mFeedTrueFalsePer,
					mFeedTrue,
					mFeedTrueCount,
					1,
					R.drawable.ic_true_pressed,
					1,
					mFeedFalse,
					mFeedFalseCount,
					0,
					R.drawable.ic_false_default,
					-1);
			
			setTrueFalseBarValues(mFeedTruePer, mFeedFalsePer, mFeedPer, mFeedObj);
		}else if(mFeedObj.getFeedIsTrue().toString().equalsIgnoreCase("1") && mFeedObj.getFeedIsFalse().toString().equalsIgnoreCase("0")){
				
				int mFeedTrueFalsePer = Utilities.getTrueFalsePer(Integer
					.parseInt(mFeedObj.getFeedTrueCount()),
					Integer.parseInt(mFeedObj
							.getFeedFalseCount()), mDecrement, 0);
				
				if(BuildConfig.DEBUG){
					if(AppConstants.DEBUG){
					Log.i(TAG, "Before True Per :" +  mFeedObj.getFeedTruePer());
					Log.i(TAG, "True Per :" +  String.valueOf(mFeedTrueFalsePer));
					}
				}
				
			afterTrueFalseEvent(mFeedObj,
					mFeedTrueFalsePer,
					mFeedTrue,
					mFeedTrueCount,
					0,
					R.drawable.ic_true_pressed,
					-1,
					mFeedFalse,
					mFeedFalseCount,
					0,
					R.drawable.ic_false_default,
					0);
			setTrueFalseBarValues(mFeedTruePer, mFeedFalsePer, mFeedPer, mFeedObj);
		}
	}

	
/*	private void setFalseBarAfterVolley(ArrayList<Feed> mFeed, int position, ImageView mFeedTrue, TextView mFeedTruePer, TextView mFeedTrueCount,ImageView mFeedFalse, TextView mFeedFalsePer, TextView mFeedFalseCount, TextView mFeedPer){
		final String TAG = "setFalseBarAfterVolley";
		if(mFeed.get(position).getFeedIsFalse().toString().equalsIgnoreCase("0") && mFeed.get(position).getFeedIsTrue().toString().equalsIgnoreCase("0")){
				int mFeedTrueFalsePer = Utilities.getTrueFalsePer(Integer
					.parseInt(mFeed.get(position).getFeedTrueCount()),
					Integer.parseInt(mFeed.get(position)
							.getFeedFalseCount()), 0, mIncrement);
				if(BuildConfig.DEBUG){
					if(AppConstants.DEBUG){
					Log.i(TAG, "Before True Per :" +  mFeed.get(position).getFeedTruePer());
					Log.i(TAG, "True Per :" +  String.valueOf(mFeedTrueFalsePer));
					}
				}
				
			afterTrueFalseEvent(mFeed,
					position,
					mFeedTrueFalsePer,
					mFeedTrue,
					mFeedTrueCount,
					0,
					R.drawable.ic_true_default,
					0,
					mFeedFalse,
					mFeedFalseCount,
					1,
					R.drawable.ic_false_pressed,
					1);
			
			setTrueFalseBarValues(mFeedTruePer, mFeedFalsePer, mFeedPer, position);
		}else if(mFeed.get(position).getFeedIsFalse().toString().equalsIgnoreCase("0") && mFeed.get(position).getFeedIsTrue().toString().equalsIgnoreCase("1")){
				mFeed.get(position).setFeedIsFalse("1");
				mFeed.get(position).setFeedIsTrue("0");
			int mFeedTrueFalsePer = Utilities.getTrueFalsePer(Integer
					.parseInt(mFeed.get(position).getFeedTrueCount()),
					Integer.parseInt(mFeed.get(position)
							.getFeedFalseCount()),  mDecrement, mIncrement);
			
			if(BuildConfig.DEBUG){
				if(AppConstants.DEBUG){
				Log.i(TAG, "Before True Per :" +  mFeed.get(position).getFeedTruePer());
				Log.i(TAG, "True Per :" +  String.valueOf(mFeedTrueFalsePer));
				}
			}

			afterTrueFalseEvent(mFeed,
					position,
					mFeedTrueFalsePer,
					mFeedTrue,
					mFeedTrueCount,
					0,
					R.drawable.ic_true_default,
					-1,
					mFeedFalse,
					mFeedFalseCount,
					1,
					R.drawable.ic_false_pressed,
					0);
			
			setTrueFalseBarValues(mFeedTruePer, mFeedFalsePer, mFeedPer, position);
			
		}else if(mFeed.get(position).getFeedIsFalse().toString().equalsIgnoreCase("1") && mFeed.get(position).getFeedIsTrue().toString().equalsIgnoreCase("0")){
				mFeed.get(position).setFeedIsFalse("0");
				mFeed.get(position).setFeedIsTrue("0");
			int mFeedTrueFalsePer = Utilities.getTrueFalsePer(Integer
					.parseInt(mFeed.get(position).getFeedTrueCount()),
					Integer.parseInt(mFeed.get(position)
							.getFeedFalseCount()),  0, mDecrement);

			if(BuildConfig.DEBUG){
				if(AppConstants.DEBUG){
				Log.i(TAG, "Before True Per :" +  mFeed.get(position).getFeedTruePer());
				Log.i(TAG, "True Per :" +  String.valueOf(mFeedTrueFalsePer));
				}
			}
			
			afterTrueFalseEvent(mFeed,
					position,
					mFeedTrueFalsePer,
					mFeedTrue,
					mFeedTrueCount,
					0,
					R.drawable.ic_true_default,
					0,
					mFeedFalse,
					mFeedFalseCount,
					0,
					R.drawable.ic_false_pressed,
					-1);
			
			setTrueFalseBarValues(mFeedTruePer, mFeedFalsePer, mFeedPer, position);
		}
	}*/
	
	
	private void setFalseBarAfterVolley(Feed mFeedObj, ImageView mFeedTrue, TextView mFeedTruePer, TextView mFeedTrueCount,ImageView mFeedFalse, TextView mFeedFalsePer, TextView mFeedFalseCount, TextView mFeedPer){
		final String TAG = "setFalseBarAfterVolley";
		if(mFeedObj.getFeedIsFalse().toString().equalsIgnoreCase("0") && mFeedObj.getFeedIsTrue().toString().equalsIgnoreCase("0")){
				int mFeedTrueFalsePer = Utilities.getTrueFalsePer(Integer
					.parseInt(mFeedObj.getFeedTrueCount()),
					Integer.parseInt(mFeedObj
							.getFeedFalseCount()), 0, mIncrement);
				if(BuildConfig.DEBUG){
					if(AppConstants.DEBUG){
					Log.i(TAG, "Before True Per :" +  mFeedObj.getFeedTruePer());
					Log.i(TAG, "True Per :" +  String.valueOf(mFeedTrueFalsePer));
					}
				}
				
			afterTrueFalseEvent(mFeedObj,
					mFeedTrueFalsePer,
					mFeedTrue,
					mFeedTrueCount,
					0,
					R.drawable.ic_true_default,
					0,
					mFeedFalse,
					mFeedFalseCount,
					1,
					R.drawable.ic_false_pressed,
					1);
			
			setTrueFalseBarValues(mFeedTruePer, mFeedFalsePer, mFeedPer, mFeedObj);
		}else if(mFeedObj.getFeedIsFalse().toString().equalsIgnoreCase("0") && mFeedObj.getFeedIsTrue().toString().equalsIgnoreCase("1")){
				mFeedObj.setFeedIsFalse("1");
				mFeedObj.setFeedIsTrue("0");
			int mFeedTrueFalsePer = Utilities.getTrueFalsePer(Integer
					.parseInt(mFeedObj.getFeedTrueCount()),
					Integer.parseInt(mFeedObj
							.getFeedFalseCount()),  mDecrement, mIncrement);
			
			if(BuildConfig.DEBUG){
				if(AppConstants.DEBUG){
				Log.i(TAG, "Before True Per :" +  mFeedObj.getFeedTruePer());
				Log.i(TAG, "True Per :" +  String.valueOf(mFeedTrueFalsePer));
				}
			}

			afterTrueFalseEvent(mFeedObj,
					mFeedTrueFalsePer,
					mFeedTrue,
					mFeedTrueCount,
					0,
					R.drawable.ic_true_default,
					-1,
					mFeedFalse,
					mFeedFalseCount,
					1,
					R.drawable.ic_false_pressed,
					0);
			
			setTrueFalseBarValues(mFeedTruePer, mFeedFalsePer, mFeedPer, mFeedObj);
			
		}else if(mFeedObj.getFeedIsFalse().toString().equalsIgnoreCase("1") && mFeedObj.getFeedIsTrue().toString().equalsIgnoreCase("0")){
				mFeedObj.setFeedIsFalse("0");
				mFeedObj.setFeedIsTrue("0");
			int mFeedTrueFalsePer = Utilities.getTrueFalsePer(Integer
					.parseInt(mFeedObj.getFeedTrueCount()),
					Integer.parseInt(mFeedObj
							.getFeedFalseCount()),  0, mDecrement);

			if(BuildConfig.DEBUG){
				if(AppConstants.DEBUG){
				Log.i(TAG, "Before True Per :" +  mFeedObj.getFeedTruePer());
				Log.i(TAG, "True Per :" +  String.valueOf(mFeedTrueFalsePer));
				}
			}
			
			afterTrueFalseEvent(mFeedObj,
					mFeedTrueFalsePer,
					mFeedTrue,
					mFeedTrueCount,
					0,
					R.drawable.ic_true_default,
					0,
					mFeedFalse,
					mFeedFalseCount,
					0,
					R.drawable.ic_false_pressed,
					-1);
			
			setTrueFalseBarValues(mFeedTruePer, mFeedFalsePer, mFeedPer, mFeedObj);
		}
	}
	private void logFeedObj(ArrayList<Feed> mFeed, int position){
		String TAG = "logFeedObj";
		if(BuildConfig.DEBUG){
		try{
			if(AppConstants.DEBUG){
			Log.i(TAG, "Feed Postion : "+ String.valueOf(position));
			Log.i(TAG, "FeedId : "+mFeed.get(position).getFeedId().toString());
			Log.i(TAG, "FeedPosterUser : "+mFeed.get(position).getFeedPostedUser().toString());
			Log.i(TAG, "FeedPostedInGroup : "+mFeed.get(position).getFeedPostedInGroup().toString());
			Log.i(TAG, "FeedPostedImage : "+mFeed.get(position).getFeedPostedImage().toString());
			Log.i(TAG, "FeedTime : "+mFeed.get(position).getFeedTime().toString());
			Log.i(TAG, "FeedContent : "+mFeed.get(position).getFeedContent().toString());
			Log.i(TAG, "FeedIsLike : "+mFeed.get(position).getFeedIsLike().toString());
			Log.i(TAG, "FeedIsTrue : "+mFeed.get(position).getFeedIsTrue().toString());
			Log.i(TAG, "FeedIsFalse : "+mFeed.get(position).getFeedIsFalse().toString());
			Log.i(TAG, "FeedTrueCount : "+mFeed.get(position).getFeedTrueCount().toString());
			Log.i(TAG, "FeedFalseCount : "+mFeed.get(position).getFeedFalseCount().toString());
			Log.i(TAG, "FeedTruePer : "+mFeed.get(position).getFeedTruePer().toString());
			Log.i(TAG, "FeedFalsePer : "+mFeed.get(position).getFeedFalsePer().toString());
			Log.i(TAG, "FeedMedia : "+mFeed.get(position).getFeedMedia().toString());
			}
		} catch(Exception e){
			Log.e(TAG, e.toString());
		}
	  }
	}
	
	static class ViewHolder{
		CircleImageView mFeedPostedImage;
		TextView mFeedPostedStatus;
//		Gallery mFeedGallery;
		HListView mFeedGallery;
		ImageView mFeedReport;
		ImageView mFeedLikeImage;
		TextView mFeedTime;
		TextView mFeedContent;
		ImageView mFeedTrue, mFeedFalse;
		TextView mFeedTrueCount,mFeedFalseCount;
		TextView mFeedTruePer, mFeedFalsePer, mFeedPer;
		TextView mFeedComment, mFeedShare, mFeedLike;
		}
}

