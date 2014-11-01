package com.netdoers.ui;

import it.sephiroth.android.library.widget.HListView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import uk.co.senab.actionbarpulltorefresh.extras.actionbarcompat.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.MediaColumns;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.google.gson.Gson;
import com.netdoers.beans.Feed;
import com.netdoers.beans.FeedComment;
import com.netdoers.beans.FeedMedia;
import com.netdoers.tellus.BuildConfig;
import com.netdoers.tellus.R;
import com.netdoers.ui.view.CircleImageView;
import com.netdoers.ui.view.FeedCommentAdapter;
import com.netdoers.ui.view.FeedGalleryAdapter;
import com.netdoers.ui.view.ScrollableListView;
import com.netdoers.utils.AppConstants;
import com.netdoers.utils.ApplicationLoader;
import com.netdoers.utils.ImageCompression;
import com.netdoers.utils.NetworkVolley;
import com.netdoers.utils.NetworkVolley.VolleyPostJsonRequest;
import com.netdoers.utils.RequestBuilder;
import com.netdoers.utils.Utilities;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class FeedDetailActivity extends ActionBarActivity implements
		OnRefreshListener {

	private CircleImageView mFeedDetailUserImage;
	private TextView mFeedDetailPostTitle;
	private TextView mFeedDetailContent;
	private HListView mFeedGallery;
	private TextView mFeedTime;
//	private ImageView mFeedTrue, mFeedFalse;
	private Button mFeedTrue, mFeedFalse;
	private ImageView mFeedLikeImage;
	private TextView mFeedTrueCount,mFeedFalseCount;
	private TextView mFeedTruePer, mFeedFalsePer, mFeedPer;
	private TextView mFeedLike;
	private RelativeLayout mFeedShare, mFeedLikeLayout;
	private ScrollableListView mScrollableListView;
	private PullToRefreshLayout mPullToRefreshLayout;
	private EditText mUserComment;
	private Button mAddComment;
	private LinearLayout mFeedLayout, mCommentLayout;
	
	private Feed mFeed;
	private ArrayList<FeedComment> mFeedComment;
	private FeedCommentAdapter mFeedCommentAdapter;

	private ImageLoader imageLoader;
	private DisplayImageOptions options;
	private Intent mIntent;
	
	private static final String TAG = FeedDetailActivity.class.getSimpleName();
	
	private String mPostId;
	private String mPosition;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feed_detail);
		initUi();
		initObj();
		setUniversalImageLoader();
		getIntentData();
		getFeedDetailData();
		setEventListeners();
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			Intent mIntent = new Intent();
			if (mFeed != null) {
				mIntent.putExtra(AppConstants.TAGS.FEED_POSITION, mPosition);
				mIntent.putExtra(AppConstants.INTENT.COMMENT_DATA, mFeed);
				setResult(RESULT_OK, mIntent);
			}
			initVars();
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	private void initUi() {
		mFeedDetailUserImage = (CircleImageView) findViewById(R.id.item_grid_feed_profile_image);
		mFeedDetailPostTitle = (TextView) findViewById(R.id.item_grid_feed_title);
		mFeedDetailContent = (TextView) findViewById(R.id.item_grid_feed_content);
		mFeedGallery = (HListView) findViewById(R.id.item_grid_feed_gallery);
		mFeedTime = (TextView) findViewById(R.id.item_grid_feed_time);
		mUserComment = (EditText)findViewById(R.id.activity_feed_detail_comment);
		mAddComment = (Button)findViewById(R.id.activity_feed_detail_comment_submit);
		
//		mFeedTrue = (ImageView) findViewById(R.id.item_grid_feed_true);
		mFeedTrue = (Button) findViewById(R.id.item_grid_feed_true);
		mFeedTrueCount = (TextView) findViewById(R.id.item_grid_feed_true_count);
		mFeedTruePer = (TextView) findViewById(R.id.item_grid_feed_true_bar);
		mFeedPer = (TextView) findViewById(R.id.item_grid_feed_true_false_percent);
//		mFeedFalse = (ImageView) findViewById(R.id.item_grid_feed_false);
		mFeedFalse = (Button) findViewById(R.id.item_grid_feed_false);
		mFeedFalseCount = (TextView) findViewById(R.id.item_grid_feed_false_count);
		mFeedFalsePer = (TextView) findViewById(R.id.item_grid_feed_false_bar);

		mFeedLike = (TextView) findViewById(R.id.item_grid_feed_like);
		mFeedLikeLayout = (RelativeLayout) findViewById(R.id.item_grid_feed_like_layout);
		mFeedShare = (RelativeLayout) findViewById(R.id.item_grid_feed_share);
		mFeedLikeImage = (ImageView) findViewById(R.id.item_grid_feed_like_image);
		
		mFeedLayout = (LinearLayout)findViewById(R.id.activity_feed_detail_layout);
		mCommentLayout = (LinearLayout)findViewById(R.id.activity_feed_detail_comment_layout);
		
		mPullToRefreshLayout = (PullToRefreshLayout) findViewById(R.id.fragment_swipe_refresh_layout);
		mScrollableListView = (ScrollableListView) findViewById(R.id.activity_feed_detail_comment_listview);
		mScrollableListView.setExpanded(true);
	}

	private void initObj() {
		ActionBarPullToRefresh.from(this).allChildrenArePullable()
				.listener(this).setup(mPullToRefreshLayout);
	}
	
	private void setFeedValues(){
		mFeedDetailPostTitle.setText(Html.fromHtml("<b>"
				+ mFeed.getFeedPostedUser().toString() + "</b>"
				+ " posted in " + "<b>"
				+ mFeed.getFeedPostedInGroup().toString() + "</b>"));
		mFeedDetailContent.setText(mFeed.getFeedContent().toString());
		mFeedTime.setText(mFeed.getFeedTime().toString());
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				imageLoader.displayImage(mFeed.getFeedPostedImage(),
						mFeedDetailUserImage, options);
			}
		});
		
		setLikeValues(mFeedLike, mFeedLikeImage, mFeed);
		
		setTrueFalseValues(mFeedTrue, mFeedFalse, mFeedTruePer, mFeedFalsePer,
				mFeedTrueCount, mFeedFalseCount, mFeedPer, mFeed);
	}
	
	private void initAdapters(){
		setFeedGallery(FeedDetailActivity.this, mFeedGallery, imageLoader,
				options);
		setFeedComments(FeedDetailActivity.this, mFeedGallery, imageLoader,
		options);
	}

	private void initVars(){
		if(mFeedComment!=null && mFeed!=null && mFeedCommentAdapter!=null){
			mFeedComment.clear();
			mFeed = null;
			mFeedCommentAdapter = null;	
		}
	}
	
	private void setUniversalImageLoader() {
		imageLoader = ImageLoader.getInstance();
		imageLoader.init(ImageLoaderConfiguration.createDefault(this));
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable._def_contact)
				.showImageForEmptyUri(R.drawable._def_contact)
				.showImageOnFail(R.drawable._def_contact).cacheInMemory()
				.cacheOnDisc().build();
	}
	
	private void getIntentData(){
		mIntent = getIntent();
		mPostId = mIntent.getStringExtra(AppConstants.TAGS.POST_ID);
		mPosition = mIntent.getStringExtra(AppConstants.TAGS.FEED_POSITION);
	}
	
	private void setEventListeners(){
		mAddComment.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(!TextUtils.isEmpty(mUserComment.getText().toString()))
					onComment();
			}
		});
		
		
		mFeedLikeLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						if (mFeed.getFeedIsLike().equalsIgnoreCase("0")) {
							mFeedLike.setTextColor(Color.BLUE);
							mFeedLikeImage.setImageResource(R.drawable.feed_like_pressed);
						} else {
							mFeedLike.setTextColor(Color.BLACK);
							mFeedLikeImage.setImageResource(R.drawable.feed_like_default);
						}
					}
				});
				
				volleyLike(mFeed, mFeedLike, mFeedLikeImage);
				// TODO Auto-generated method stub
			}
		});
		
		setTrueFalseEventListeners(mFeedTrue, mFeedTrueCount,
				mFeedTruePer, mFeedFalse, mFeedFalseCount, mFeedFalsePer,
				mFeedPer);
		
		mFeedShare.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent shareIntent = new Intent(Intent.ACTION_SEND);
				shareIntent.setType("text/plain");
				shareIntent.putExtra(Intent.EXTRA_TEXT,
						"View it on TellUs " + mFeed.getFeedContent());
				startActivity(shareIntent);
			}
		});
	}
	
	private void setTrueFalseEventListeners(final Button mFeedTrue,
			final TextView mFeedTrueCount, final TextView mFeedTruePer,
			final Button mFeedFalse, final TextView mFeedFalseCount,
			final TextView mFeedFalsePer, final TextView mFeedPer) {
		
			mFeedTrue.setOnClickListener(new View.OnClickListener() {
			private String TAG = "setTrueFalseSelectedEventListeners";
			private String PRETAG = "Pre";
				
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if(mFeed.getFeedIsTrue().equalsIgnoreCase("0")){
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
					volleyTrue(mFeed,mFeedTrue, mFeedTruePer,
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
				
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if(mFeed.getFeedIsFalse().equalsIgnoreCase("0")){
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
					volleyFalse(mFeed, mFeedTrue, mFeedTruePer,
							mFeedTrueCount, mFeedFalse, mFeedFalsePer,
							mFeedFalseCount, mFeedPer);
				}
			 }
		});
	}

	private void setFeedGallery(Context mContext,HListView mFeedGallery,ImageLoader imageLoader, DisplayImageOptions options) {
		if(mFeed.getFeedMedia()!=null && mFeed.getFeedMedia().size() > 0){
			mFeedGallery.setVisibility(View.VISIBLE);
			FeedGalleryAdapter mFeedGalleryAdapter = new FeedGalleryAdapter(mContext, mFeed.getFeedMedia(), imageLoader, options);
			mFeedGallery.setAdapter(mFeedGalleryAdapter);	
		}else{
			mFeedGallery.setVisibility(View.GONE);
		}
	}
	
	private void setFeedComments(Context mContext,HListView mFeedGallery,ImageLoader imageLoader, DisplayImageOptions options) {
		if(mFeedComment!=null && mFeedComment.size() > 0){
			mScrollableListView.setVisibility(View.VISIBLE);
			mFeedCommentAdapter = new FeedCommentAdapter(mContext, mFeedComment, imageLoader, options);
			mScrollableListView.setAdapter(mFeedCommentAdapter);	
		}else{
//			mScrollableListView.setVisibility(View.GONE);
		}
	}
	
	private void onComment(){
		volleyComment();
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
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		Intent mIntent = new Intent();
		if (mFeed != null) {
			mIntent.putExtra(AppConstants.TAGS.FEED_POSITION, mPosition);
			mIntent.putExtra(AppConstants.INTENT.COMMENT_DATA, mFeed);
			setResult(RESULT_OK, mIntent);
		}else{
			Log.i(TAG, "mFeed is null!");
		}
		initVars();
		finish();
	}
	
	@Override
	public void onRefreshStarted(View view) {
		// TODO Auto-generated method stub
		mPullToRefreshLayout.setRefreshComplete();
	}

	private void setPullToRefreshLoader() {
		mPullToRefreshLayout.setRefreshing(true);
	}
	
	private void getFeedDetailData(){
		Log.i(TAG, "getFeedDetailData");
		setPullToRefreshLoader();
		NetworkVolley nVolley = new NetworkVolley();
		JSONObject mJSONObject = RequestBuilder.getPostFeedDetailData(mPostId);
		Log.i(TAG, mJSONObject.toString());
		Log.i(TAG, AppConstants.URLS.URL_POST_FEED_DETAIL);
		VolleyPostJsonRequest req = nVolley.new VolleyPostJsonRequest(AppConstants.URLS.URL_POST_FEED_DETAIL, mJSONObject,
			       new Listener<JSONObject>() {
			           @Override
			           public void onResponse(JSONObject response) {
			               try {
			                   VolleyLog.v("Response:%n %s", response.toString(4));
			                   Log.i(TAG, response.toString());
			                   if(response.getBoolean("success")){
			                	   parseFeedDetailFromJson(response.toString());
			                	   mFeedLayout.setVisibility(View.VISIBLE);
			                	   mCommentLayout.setVisibility(View.VISIBLE);
			                	   setFeedValues();
			                	   initAdapters();
			                	   mPullToRefreshLayout.setRefreshComplete();
							  }else{
							  }
			               } catch (JSONException e) {
			                   e.printStackTrace();
			               }
			           }
			       }, new ErrorListener() {
			           @Override
			           public void onErrorResponse(VolleyError error) {
			               VolleyLog.e("Error: ", error.getMessage());
							try {
								mPullToRefreshLayout.setRefreshComplete();
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
			           }
			       });
		ApplicationLoader.getInstance().addToRequestQueue(req,TAG);
	}

	
	private void parseFeedDetailFromJson(String str){
		String TAG = "parseFeedDetailFromJson";
		try{
			Log.i(TAG, TAG);
			JSONObject mJSONObject = new JSONObject(str); 
			JSONObject mFeedObj = mJSONObject.getJSONObject("feeds");
			mFeed = new Feed();
			JSONArray feedMediaArray = null;
			JSONArray feedCommentArray = null;
			JSONArray mPostArray = mFeedObj.getJSONArray("posts");
			JSONObject mPostObj = (JSONObject) mPostArray.get(0);
				
				try{
					feedMediaArray = mFeedObj.getJSONArray("resources");
				}catch(JSONException e){
					Log.e(TAG, e.toString());
				}
				try{
					feedCommentArray = mFeedObj.getJSONArray("comments");
				}catch(JSONException e){
					Log.e(TAG, e.toString());
				}
				
				mFeed.setFeedId(mPostObj.getString("id"));//feed_id
				mFeed.setFeedContent(mPostObj.getString("posted_status"));//posted text
				mFeed.setFeedPostedUser(mPostObj.getString("username"));//posted user
				mFeed.setFeedPostedImage(mPostObj.getString("profile_pic"));//posted user image
				mFeed.setFeedPostedInGroup(mPostObj.getString("posted_in_group"));//posted in group
				mFeed.setFeedTime(mPostObj.getString("created_time"));// posted time
				mFeed.setFeedIsLike(mPostObj.getString("is_like"));//is user like
				mFeed.setFeedIsTrue(mPostObj.getString("is_true"));//is user false
				mFeed.setFeedIsFalse(mPostObj.getString("is_false"));// is user true
				mFeed.setFeedTrueCount(mPostObj.getString("true_count"));//post true count
				mFeed.setFeedFalseCount(mPostObj.getString("false_count"));//post false count
				mFeed.setFeedTruePer(mPostObj.getString("true_percent"));//true per
				mFeed.setFeedFalsePer(mPostObj.getString("false_percent"));//false per
				mFeed.setFeedPer(mPostObj.getString("max_percentage"));
				mFeed.setFeedCommentCount(mPostObj.getString("comments_count"));//comment_count
				mFeed.setFeedShare(mPostObj.getString("share_link"));//share_link
				mFeed.setFeedIsReport(mPostObj.getString("is_reported"));
				
				
				ArrayList<FeedMedia> arrayFeedMedia = new ArrayList<FeedMedia>();
				if(feedMediaArray!=null){
					for (int j = 0; j < feedMediaArray.length(); j++) {
						JSONObject mFeedMediaObj = (JSONObject) feedMediaArray.get(j);
						FeedMedia feedMedia = new FeedMedia();
						feedMedia.setFeedId(mFeedMediaObj.getString("post_id")); // feed_id
						feedMedia.setFeedMediaPath(mFeedMediaObj.getString("path"));//feed_media_path
						feedMedia.setFeedIsVideo(mFeedMediaObj.getString("type"));//feed_media_is_video
						feedMedia.setFeedVideoThumbail(mFeedMediaObj.getString("thumbnail"));//feed_media_video_thumbnail
						arrayFeedMedia.add(feedMedia);
					}
				}
				mFeed.setFeedMedia(arrayFeedMedia);
				
				mFeedComment= new ArrayList<FeedComment>();
				if(feedCommentArray!=null){
					for (int j = 0; j < feedCommentArray.length(); j++) {
						JSONObject mFeedMediaObj = (JSONObject) feedCommentArray.get(j);
						FeedComment feedComment = new FeedComment();
						feedComment.setFeedUserProfile(mFeedMediaObj.getString("profile_pic"));//feed_media_path
						feedComment.setFeedUserComment(mFeedMediaObj.getString("comment"));//feed_media_is_video
						mFeedComment.add(feedComment);
					}
				}
		}catch(JSONException e){
			Log.i(TAG, e.toString());
		}
		catch(Exception e){
			Log.i(TAG, e.toString());
		}
	}
	
	private void volleyComment(){
		final String TAG = "volleyComment";
		NetworkVolley nVolley = new NetworkVolley();
		JSONObject mDataJson = RequestBuilder.getPostFeedCommentData(mPostId, mUserComment.getText().toString());
		Log.i(TAG, mDataJson.toString());
		mAddComment.setEnabled(false);
		mUserComment.setEnabled(false);
		VolleyPostJsonRequest req = nVolley.new VolleyPostJsonRequest(AppConstants.URLS.URL_POST_FEED_COMMENT, mDataJson,
			       new Listener<JSONObject>() {
			           @Override
			           public void onResponse(JSONObject response) {
			               try {
			                   VolleyLog.v("Response:%n %s", response.toString(4));
			                   Log.i(TAG, response.toString());
			                   if(response.getBoolean("success")){
			                	   FeedComment mFeedCommentObj = new FeedComment();
			                	   mFeedCommentObj.setFeedUserProfile(ApplicationLoader
											.getPreferences().getProfilePicPath());
			                	   mFeedCommentObj.setFeedUserComment(mUserComment.getText().toString());
			                	   
								if (mFeedComment == null) {
			                		   mFeedComment = new ArrayList<FeedComment>();
			                	   }
			                	   
			                	   mFeedComment.add(mFeedCommentObj);
			                	   mUserComment.setText("");
			                	   mAddComment.setEnabled(true);
			                	   if(mFeedCommentAdapter!=null){
			                		   mFeedCommentAdapter.addFeed(mFeedComment);
			                		   mFeedCommentAdapter.notifyDataSetChanged();
			                	   }else{
			                		   mFeedCommentAdapter = new FeedCommentAdapter(FeedDetailActivity.this, mFeedComment, imageLoader, options);
			                		   mScrollableListView.setAdapter(mFeedCommentAdapter);
			                	   }
			                	   
			                	   if(mFeedComment!=null){
			                		   mFeed.setFeedCommentCount(String.valueOf(mFeedComment.size()));
			                	   }else{
			                		   mFeed.setFeedCommentCount("0");
			                	   }
			                		   
							  }
			               } catch (JSONException e) {
			            	   mAddComment.setEnabled(true);
			            	   mUserComment.setEnabled(false);
			                   e.printStackTrace();
			               }
			           }
			       }, new ErrorListener() {
			           @Override
			           public void onErrorResponse(VolleyError error) {
			               VolleyLog.e("Error: ", error.getMessage());
			               mAddComment.setEnabled(true);
			               mUserComment.setEnabled(false);
			           }
			       });
		ApplicationLoader.getInstance().addToRequestQueue(req,TAG);
	}
	
	private void volleyLike(final Feed mFeedObj, final TextView mFeedLike,
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
								setLikeValues(mFeedLike, mFeedLikeImage, mFeedObj);
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
}
