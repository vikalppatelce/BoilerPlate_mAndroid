package com.netdoers.ui.view;

import it.sephiroth.android.library.widget.HListView;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.PopupMenu;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.netdoers.beans.Feed;
import com.netdoers.tellus.BuildConfig;
import com.netdoers.tellus.R;
import com.netdoers.ui.FeedDetailActivity;
import com.netdoers.utils.AppConstants;
import com.netdoers.utils.ApplicationLoader;
import com.netdoers.utils.NetworkVolley;
import com.netdoers.utils.NetworkVolley.VolleyPostJsonRequest;
import com.netdoers.utils.RequestBuilder;
import com.netdoers.utils.Utilities;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class FeedAdapter extends BaseAdapter {

	Context mContext = null;
	ArrayList<Feed> mFeed = null;
	DisplayImageOptions options;
	ImageLoader imageLoader;
	private String TAG;

	public FeedAdapter(Context mContext, String TAG, ArrayList<Feed> mFeed,
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
		final Feed mFeedObj;

		if (convertView == null) {
			LayoutInflater li = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = li.inflate(R.layout.item_grid_feed, null);
			holder = new ViewHolder();
			holder.mFeedPostedImage = (CircleImageView) v.findViewById(R.id.item_grid_feed_profile_image);
			holder.mFeedPostedStatus = (TextView) v.findViewById(R.id.item_grid_feed_title);
			holder.mFeedPostedContent = (TextView) v.findViewById(R.id.item_grid_feed_content);
			// mFeedGallery = (Gallery)v.findViewById(R.id.item_grid_feed_gallery);
			holder.mFeedGallery = (HListView) v.findViewById(R.id.item_grid_feed_gallery);
			holder.mFeedReport = (LinearLayout) v.findViewById(R.id.item_grid_feed_report);
			holder.mFeedTime = (TextView) v.findViewById(R.id.item_grid_feed_time);
//			holder.mFeedTrue = (ImageView) v.findViewById(R.id.item_grid_feed_true);
			holder.mFeedTrue = (Button) v.findViewById(R.id.item_grid_feed_true);
			holder.mFeedTrueCount = (TextView) v.findViewById(R.id.item_grid_feed_true_count);
			holder.mFeedTruePer = (TextView) v.findViewById(R.id.item_grid_feed_true_bar);
			holder.mFeedPer = (TextView) v.findViewById(R.id.item_grid_feed_true_false_percent);
//			holder.mFeedFalse = (ImageView) v.findViewById(R.id.item_grid_feed_false);
			holder.mFeedFalse = (Button) v.findViewById(R.id.item_grid_feed_false);
			holder.mFeedFalseCount = (TextView) v.findViewById(R.id.item_grid_feed_false_count);
			holder.mFeedFalsePer = (TextView) v.findViewById(R.id.item_grid_feed_false_bar);

			holder.mFeedLike = (TextView) v.findViewById(R.id.item_grid_feed_like);
			holder.mFeedLikeLayout = (RelativeLayout) v.findViewById(R.id.item_grid_feed_like_layout);
			holder.mFeedCommentCount = (TextView)v.findViewById(R.id.item_grid_feed_comment_count);
			holder.mFeedComment = (RelativeLayout) v.findViewById(R.id.item_grid_feed_comment);
			holder.mFeedShare = (RelativeLayout) v.findViewById(R.id.item_grid_feed_share);
			holder.mFeedLikeImage = (ImageView) v.findViewById(R.id.item_grid_feed_like_image);			v.setTag(holder);
		} else {
			v = convertView;
			holder = (ViewHolder) v.getTag();
		}

		mFeedObj = mFeed.get(position);
		
		holder.mFeedPostedStatus.setText(Html.fromHtml("<b>"
				+ mFeedObj.getFeedPostedUser().toString() + "</b>"
				+ " posted in " + "<b>"
				+ mFeedObj.getFeedPostedInGroup() + "</b>"));
		holder.mFeedPostedContent.setText(mFeedObj.getFeedContent());
		holder.mFeedTime.setText(mFeedObj.getFeedTime());
		
		setLikeValues(holder.mFeedLike, holder.mFeedLikeImage, mFeedObj);
		
		setCommentValues(holder.mFeedCommentCount, mFeedObj);
		
		setTrueFalseValues(holder.mFeedTrue, holder.mFeedFalse,
				holder.mFeedTruePer, holder.mFeedFalsePer,
				holder.mFeedTrueCount, holder.mFeedFalseCount, holder.mFeedPer,
				mFeedObj);
		
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
		
		imageLoader.displayImage(mFeed.get(position)
				.getFeedPostedImage(),holder.mFeedPostedImage, options);
		
		setLikeEventListeners(holder.mFeedLikeLayout,holder.mFeedLike, holder.mFeedLikeImage, mFeedObj,position);
		setTrueFalseEventListeners(holder.mFeedTrue, holder.mFeedTrueCount,
				holder.mFeedTruePer, holder.mFeedFalse, holder.mFeedFalseCount, holder.mFeedFalsePer,
				holder.mFeedPer, mFeedObj,position);
		
		holder.mFeedReport.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				setPopUpWindow(mContext, v, mFeed, mFeedObj,position);
			}
		});

		holder.mFeedComment.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent mCommentIntent = new Intent(mContext, FeedDetailActivity.class);
				mCommentIntent.putExtra(AppConstants.TAGS.POST_ID, mFeedObj.getFeedId());
				mCommentIntent.putExtra(AppConstants.TAGS.FEED_POSITION, String.valueOf(position));
				((Activity)mContext).startActivityForResult(mCommentIntent, AppConstants.INTENT.FEED_COMMENT);
//				mContext.startActivity(mCommentIntent);
			}
		});
		
		holder.mFeedShare.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent shareIntent = new Intent(Intent.ACTION_SEND);
				shareIntent.setType("text/plain");
				shareIntent.putExtra(Intent.EXTRA_TEXT,
						"View it on TellUs " + mFeedObj.getFeedShare());
				mContext.startActivity(shareIntent);
			}
		});
		
		return v;
	}
	
	private void setTrueFalseValues(Button mFeedTrue, Button mFeedFalse,
			TextView mFeedTruePer, TextView mFeedFalsePer, TextView mFeedTrueCount, TextView mFeedFalseCount,TextView mFeedPer,
			Feed mFeedObj) {
		LinearLayout.LayoutParams paramsMFeedTruePer,paramsMFeedFalsePer;

		//TRUE FALSE BUTTON 
		if(mFeedObj.getFeedIsTrue().equalsIgnoreCase("0") && mFeedObj.getFeedIsFalse().equalsIgnoreCase("1")){
			mFeedTrue.setBackgroundResource(R.drawable.ic_true_default);
			mFeedFalse.setBackgroundResource(R.drawable.ic_false_pressed);
			mFeedTrue.setTextColor(Color.parseColor(AppConstants.BLUE));
			mFeedFalse.setTextColor(Color.WHITE);
		}else if(mFeedObj.getFeedIsTrue().equalsIgnoreCase("1") && mFeedObj.getFeedIsFalse().equalsIgnoreCase("0")){
			mFeedTrue.setBackgroundResource(R.drawable.ic_true_pressed);
			mFeedFalse.setBackgroundResource(R.drawable.ic_false_default);
			mFeedTrue.setTextColor(Color.WHITE);
			mFeedFalse.setTextColor(Color.parseColor(AppConstants.GREEN));
		}else{
			mFeedTrue.setBackgroundResource(R.drawable.ic_true_default);
			mFeedFalse.setBackgroundResource(R.drawable.ic_false_default);
			mFeedTrue.setTextColor(Color.parseColor(AppConstants.BLUE));
			mFeedFalse.setTextColor(Color.parseColor(AppConstants.GREEN));
		}
		
		// TRUE FALSE BAR VALUE
		paramsMFeedTruePer = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, Float.parseFloat(mFeedObj.getFeedTruePer().toString()));
		paramsMFeedFalsePer = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, Float.parseFloat(mFeedObj.getFeedFalsePer().toString()));
		mFeedTruePer.setLayoutParams(paramsMFeedTruePer);
		mFeedFalsePer.setLayoutParams(paramsMFeedFalsePer);
		
		//POST PERCENTAGE VALUE
		mFeedPer.setText(mFeedObj.getFeedPer().toString());
		
		//TRUE FALSE COUNT VALUES
		mFeedTrueCount.setText(mFeedObj.getFeedTrueCount().toString());
		mFeedFalseCount.setText(mFeedObj.getFeedFalseCount().toString());
	}
	
	private void setTrueFalseEventListeners(final Button mFeedTrue,
			final TextView mFeedTrueCount, final TextView mFeedTruePer,
			final Button mFeedFalse, final TextView mFeedFalseCount,
			final TextView mFeedFalsePer, final TextView mFeedPer,
			final Feed mFeedObj, final int position) {
		
			mFeedTrue.setOnClickListener(new View.OnClickListener() {
			private String TAG = "setTrueFalseSelectedEventListeners";
			private String PRETAG = "Pre";
				
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				((Activity) mContext).runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if(mFeedObj.getFeedIsTrue().equalsIgnoreCase("0")){
							mFeedTrue.setBackgroundResource(R.drawable.ic_true_pressed);
							mFeedFalse.setBackgroundResource(R.drawable.ic_false_default);
							mFeedTrue.setTextColor(Color.WHITE);
							mFeedFalse.setTextColor(Color.parseColor(AppConstants.GREEN));
						}else{
							mFeedTrue.setBackgroundResource(R.drawable.ic_true_default);
							mFeedFalse.setBackgroundResource(R.drawable.ic_false_pressed);
							mFeedTrue.setTextColor(Color.parseColor(AppConstants.BLUE));
							mFeedFalse.setTextColor(Color.WHITE);
						}
					}
				});
				
				if(Utilities.isInternetConnected()){
					volleyTrue(mFeedObj,mFeedTrue, mFeedTruePer,
							mFeedTrueCount, mFeedFalse, mFeedFalsePer,
							mFeedFalseCount, mFeedPer);
				}
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
						if(mFeedObj.getFeedIsFalse().equalsIgnoreCase("0")){
							mFeedTrue.setBackgroundResource(R.drawable.ic_true_default);
							mFeedFalse.setBackgroundResource(R.drawable.ic_false_pressed);
							mFeedTrue.setTextColor(Color.parseColor(AppConstants.BLUE));
							mFeedFalse.setTextColor(Color.WHITE);
						}else{
							mFeedTrue.setBackgroundResource(R.drawable.ic_true_pressed);
							mFeedFalse.setBackgroundResource(R.drawable.ic_false_default);
							mFeedTrue.setTextColor(Color.WHITE);
							mFeedFalse.setTextColor(Color.parseColor(AppConstants.GREEN));
						}
						// TODO Auto-generated method stub
					}
				});
				if(Utilities.isInternetConnected()){
					volleyFalse(mFeedObj, mFeedTrue, mFeedTruePer,
							mFeedTrueCount, mFeedFalse, mFeedFalsePer,
							mFeedFalseCount, mFeedPer);
				}
			 }
		});
	}
	
	private void setPopUpWindow(final Context mContext, View mView, final ArrayList<Feed> mFeed, final Feed mFeedObj,final int position){
		PopupMenu mPopUpMenu = new PopupMenu(mContext, mView);
		try{
			if(mFeedObj.getFeedIsReport().equalsIgnoreCase("1")){
				mPopUpMenu.getMenuInflater().inflate(R.menu.menu_feed_pop_up_unreport, mPopUpMenu.getMenu());
			}else{
				mPopUpMenu.getMenuInflater().inflate(R.menu.menu_feed_pop_up_report, mPopUpMenu.getMenu());	
			}
		}catch(Exception e){
			mPopUpMenu.getMenuInflater().inflate(R.menu.menu_feed_pop_up_report, mPopUpMenu.getMenu());
		}
		mPopUpMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				// TODO Auto-generated method stub
				switch(item.getItemId()){
				case R.id.menu_feed_report:
					volleyReport(mFeed, position, mFeedObj, "1");
					break;
				case R.id.menu_feed_unreport:
					volleyReport(mFeed, position, mFeedObj, "0");
					break;
				}
				return false;
			}
		});
		mPopUpMenu.show();
	}
	
	private void setFeedGallery(Context mContext, ArrayList<Feed> mFeed,
			int position, HListView mFeedGallery,ImageLoader imageLoader, DisplayImageOptions options) {
		if(mFeed.get(position).getFeedMedia().size() > 0){
			mFeedGallery.setVisibility(View.VISIBLE);
			FeedGalleryAdapter mFeedGalleryAdapter = new FeedGalleryAdapter(mContext, mFeed.get(position).getFeedMedia(), imageLoader, options);
			mFeedGallery.setAdapter(mFeedGalleryAdapter);	
		}else{
			mFeedGallery.setVisibility(View.GONE);
		}
	}
	
	private void setLikeEventListeners(final RelativeLayout mFeedLikeLayout,final TextView mFeedLike,
			final ImageView mFeedLikeImage, final Feed mFeedObj, final int position) {
		
		mFeedLikeLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				((Activity) mContext).runOnUiThread(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						if (mFeedObj.getFeedIsLike().equalsIgnoreCase("0")) {
							mFeedLike.setTextColor(Color.BLUE);
							mFeedLikeImage.setImageResource(R.drawable.feed_like_pressed);
						} else {
							mFeedLike.setTextColor(Color.BLACK);
							mFeedLikeImage.setImageResource(R.drawable.feed_like_default);
						}
					}
				});
				
				volleyLike(mFeed,position,mFeedObj,mFeedLike,mFeedLikeImage);
				// TODO Auto-generated method stub
			}
		});
	}
	
	private void setLikeValues(TextView mFeedLike, ImageView mFeedLikeImage,
			Feed mFeedObj) {
		if (mFeedObj.getFeedIsLike().equalsIgnoreCase("0")) {
			mFeedLike.setTextColor(Color.BLACK);
			mFeedLikeImage.setImageResource(R.drawable.feed_like_default);
		} else {
			mFeedLike.setTextColor(Color.BLUE);
			mFeedLikeImage.setImageResource(R.drawable.feed_like_pressed);
		}
		mFeedLike.setText(mFeedObj.getFeedLikeCount());
	}
	
	private void setCommentValues(TextView mFeedCommentCount, Feed mFeedObj) {
		try {
			mFeedCommentCount.setText(mFeedObj.getFeedCommentCount());
		} catch (Exception e) {
			Log.e(TAG, "setCommentValues" + e.toString());
		}
	}
	
	static class ViewHolder{
		CircleImageView mFeedPostedImage;
		TextView mFeedPostedStatus;
		TextView mFeedPostedContent;
//		Gallery mFeedGallery;
		HListView mFeedGallery;
		LinearLayout mFeedReport;
		ImageView mFeedLikeImage;
		TextView mFeedTime;
		TextView mFeedContent;
//		ImageView mFeedTrue, mFeedFalse;
		Button mFeedTrue, mFeedFalse;
		TextView mFeedTrueCount,mFeedFalseCount;
		TextView mFeedTruePer, mFeedFalsePer, mFeedPer;
		TextView mFeedLike;
		TextView mFeedCommentCount;
		RelativeLayout mFeedShare, mFeedComment , mFeedLikeLayout;
		}
	
	/*
	 * Network I/0 - VOLLEY
	 */
	
	private void volleyLike(final ArrayList<Feed> mFeed, final int position,
			final Feed mFeedObj, final TextView mFeedLike,
			final ImageView mFeedLikeImage) {
		final String TAG = "volleyLike";
		NetworkVolley nVolley = new NetworkVolley();
		JSONObject mJSONObject = RequestBuilder.getPostLikeFeedData(mFeedObj
				.getFeedId(),
				mFeedObj.getFeedIsLike().equalsIgnoreCase("1") ? "0" : "1");
		Log.i(TAG, AppConstants.URLS.URL_POST_FEED_LIKE);
		Log.i(TAG, mJSONObject.toString());
		VolleyPostJsonRequest req = nVolley.new VolleyPostJsonRequest(AppConstants.URLS.URL_POST_FEED_LIKE, mJSONObject,
				new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						try {
							VolleyLog.v("Response:%n %s", response.toString(4));
							Log.i(TAG, response.toString());
							if (response.getBoolean("success")) {
								if (mFeedObj.getFeedIsLike().toString().equalsIgnoreCase("0")) {
									mFeedObj.setFeedIsLike("1");
								} else {
									mFeedObj.setFeedIsLike("0");
								}

								mFeedObj.setFeedLikeCount(response.getString("like_count"));
								mFeedLike.setText(mFeedObj.getFeedLikeCount());	
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
						if (mFeedObj.getFeedIsLike().equalsIgnoreCase("0")) {
							mFeedLike.setTextColor(Color.BLACK);
							mFeedLikeImage
									.setImageResource(R.drawable.feed_like_default);
						} else {
							mFeedLike.setTextColor(Color.BLUE);
							mFeedLikeImage
									.setImageResource(R.drawable.feed_like_pressed);
						}
						if (BuildConfig.DEBUG) {
							Log.i(TAG, "Error : "
									+ mFeedObj.getFeedIsLike().toString());
							Log.i(TAG, "Volley Error : " + error.getMessage());
							VolleyLog.e("Error: ", error.getMessage());
						}
					}
				});
		ApplicationLoader.getInstance().addToRequestQueue(req, TAG);
	}
	
	private void volleyTrue(final Feed mFeedObj,
			final Button mFeedTrue, final TextView mFeedTruePer, final TextView mFeedTrueCount,
			final Button mFeedFalse, final TextView mFeedFalsePer, final TextView mFeedFalseCount,
			final TextView mFeedPer){
		final String TAG = "volleyTrue";
		NetworkVolley nVolley = new NetworkVolley();
		JSONObject mDataJson = RequestBuilder.getPostTrueFalseFeedData(
				mFeedObj.getFeedId(), "1");
		Log.i(TAG, mDataJson.toString());
		VolleyPostJsonRequest req = nVolley.new VolleyPostJsonRequest(AppConstants.URLS.URL_POST_FEED_TRUE_FALSE, mDataJson,
			       new Listener<JSONObject>() {
			           @Override
			           public void onResponse(JSONObject response) {
			               try {
			                   VolleyLog.v("Response:%n %s", response.toString(4));
			                   Log.i(TAG, response.toString());
			                   if(response.getBoolean("success")){
			                	   mFeedObj.setFeedIsTrue("1");
			                	   mFeedObj.setFeedIsFalse("0");
			                	   mFeedObj.setFeedTrueCount(response.getString("true_count"));
			                	   mFeedObj.setFeedFalseCount(response.getString("false_count"));
			                	   mFeedObj.setFeedTruePer(response.getString("true_percent"));
			                	   mFeedObj.setFeedFalsePer(response.getString("false_percent"));
			                	   mFeedObj.setFeedPer(response.getString("max_percentage"));
			                	   setTrueFalseValues(mFeedTrue, mFeedFalse,
										mFeedTruePer, mFeedFalsePer,
										mFeedTrueCount, mFeedFalseCount,
										mFeedPer, mFeedObj);  
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
	
	private void volleyFalse(final Feed mFeedObj, final Button mFeedTrue,
			final TextView mFeedTruePer, final TextView mFeedTrueCount,
			final Button mFeedFalse, final TextView mFeedFalsePer,
			final TextView mFeedFalseCount, final TextView mFeedPer) {
		final String TAG = "volleyFalse";
		NetworkVolley nVolley = new NetworkVolley();
		JSONObject mDataJson = RequestBuilder.getPostTrueFalseFeedData(
				mFeedObj.getFeedId(), "0");
		Log.i(TAG, mDataJson.toString());
		VolleyPostJsonRequest req = nVolley.new VolleyPostJsonRequest(AppConstants.URLS.URL_POST_FEED_TRUE_FALSE, mDataJson,
			       new Listener<JSONObject>() {
			           @Override
			           public void onResponse(JSONObject response) {
			               try {
			                   VolleyLog.v("Response:%n %s", response.toString(4));
			                   Log.i(TAG, response.toString());
			                   if(response.getBoolean("success")){
			                	   mFeedObj.setFeedIsTrue("0");
			                	   mFeedObj.setFeedIsFalse("1");
			                	   mFeedObj.setFeedTrueCount(response.getString("true_count"));
			                	   mFeedObj.setFeedFalseCount(response.getString("false_count"));
			                	   mFeedObj.setFeedTruePer(response.getString("true_percent"));
			                	   mFeedObj.setFeedFalsePer(response.getString("false_percent"));
			                	   mFeedObj.setFeedPer(response.getString("max_percentage"));
			                	   setTrueFalseValues(mFeedTrue, mFeedFalse,
										mFeedTruePer, mFeedFalsePer,
										mFeedTrueCount, mFeedFalseCount,
										mFeedPer, mFeedObj);
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
	
	private void volleyReport(final ArrayList<Feed> mFeed, final int position,
			final Feed mFeedObj, final String isReport) {
		final String TAG = "volleyReport";
		NetworkVolley nVolley = new NetworkVolley();
		JSONObject mJSONObject = RequestBuilder.getPostReportFeedData(mFeedObj
				.getFeedId(),isReport);
		Log.i(TAG, AppConstants.URLS.URL_POST_FEED_REPORT);
		Log.i(TAG, mJSONObject.toString());
		VolleyPostJsonRequest req = nVolley.new VolleyPostJsonRequest(AppConstants.URLS.URL_POST_FEED_REPORT, mJSONObject,
				new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						try {
							VolleyLog.v("Response:%n %s", response.toString(4));
							Log.i(TAG, response.toString());
							if (response.getBoolean("success")) {
								mFeedObj.setFeedIsReport(isReport);
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
							Log.i(TAG, "Error : "
									+ mFeedObj.getFeedIsLike().toString());
							Log.i(TAG, "Volley Error : " + error.getMessage());
							VolleyLog.e("Error: ", error.getMessage());
						}
					}
				});
		ApplicationLoader.getInstance().addToRequestQueue(req, TAG);
	}
}

