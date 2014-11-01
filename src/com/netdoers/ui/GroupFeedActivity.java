package com.netdoers.ui;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import uk.co.senab.actionbarpulltorefresh.extras.actionbarcompat.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.netdoers.beans.Feed;
import com.netdoers.beans.FeedMedia;
import com.netdoers.tellus.BuildConfig;
import com.netdoers.tellus.R;
import com.netdoers.ui.view.FeedAdapter;
import com.netdoers.utils.AppConstants;
import com.netdoers.utils.ApplicationLoader;
import com.netdoers.utils.NetworkVolley;
import com.netdoers.utils.NetworkVolley.VolleyPostJsonRequest;
import com.netdoers.utils.RequestBuilder;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class GroupFeedActivity extends ActionBarActivity implements OnRefreshListener{
	
	//	private GridView mGridView;
	private ListView mGridView;
	private PullToRefreshLayout mPullToRefreshLayout;
	private View mProgress;
	
	private ProgressDialog mProgressDialog;
	
	private ImageLoader imageLoader;
	private DisplayImageOptions options;
	
	private FeedAdapter mFeedAdapter;
	private ArrayList<Feed> mFeed;
	
	private boolean mLoadMoreFlag = false;
	private int mScrollCount = 0;
	private int mFeedIndex = 0;
	private int mPaginationCount = 10;
	private boolean mIsFirstTime = true;
	
	private String mGroupId;
	private String mGroupName;
	private String mGroupImage;
	private String mGroupSubscribe = "0";
	private String mGroupAdmin;

	
	private static final String TAG = GroupFeedActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_feed_group);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		initUi();
		initObj();
		getIntentData();
		setUniversalImageLoader();
		setScrollListener();
		getDataFromApi();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
//		initObj();
//		getDataFromApi();
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuInflater inflater = getMenuInflater();
		if(mGroupSubscribe.equalsIgnoreCase("0") && mGroupAdmin.equalsIgnoreCase("1")){
			inflater.inflate(R.menu.menu_group_feed_admin, menu);
		}else if(mGroupSubscribe.equalsIgnoreCase("1")){
			inflater.inflate(R.menu.menu_group_feed_subscribed, menu);	
		}else{
			inflater.inflate(R.menu.menu_group_feed_unsubscribed, menu);
		}
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		case R.id.action_group_subscribe:
			volleySubscribe();
			return true;
		case R.id.action_group_unsubscribe:
			volleyUnSubscribe();
			return true;
		case R.id.action_add_group_post:
			Intent mIntent = new Intent(GroupFeedActivity.this, PostActivity.class);
			mIntent.putExtra(AppConstants.TAGS.GROUP_ID, mGroupId);
			mIntent.putExtra(AppConstants.TAGS.GROUP_NAME, mGroupName);
			startActivity(mIntent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		finish();
	}
	
	private void initUi(){
//		mGridView = (GridView) findViewById(R.id.fragment_feed_group_gridview);
		mGridView = (ListView) findViewById(R.id.fragment_feed_group_gridview);
//		mProgress = (ProgressBar) findViewById(R.id.fragment_feed_group_progress);
		mPullToRefreshLayout = (PullToRefreshLayout) findViewById(R.id.fragment_feed_group_swipe_refresh_layout);
		mProgress = ((LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.item_footerview, null, false);
		ProgressBar mProgressBar = (ProgressBar)mProgress.findViewById(R.id.fragment_feed_group_progress);
		mProgressBar.getIndeterminateDrawable().setColorFilter(
	            getResources().getColor(R.color.background_actionbar),
	            android.graphics.PorterDuff.Mode.SRC_IN);
	}
	
	private void initObj(){
		ActionBarPullToRefresh.from(this)
        .allChildrenArePullable()
        .listener(this)
        .setup(mPullToRefreshLayout);
		mFeed = new ArrayList<Feed>();
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
		mGroupId = getIntent().getStringExtra(AppConstants.TAGS.GROUP_ID);
		mGroupName = getIntent().getStringExtra(AppConstants.TAGS.GROUP_NAME);
		mGroupImage = getIntent().getStringExtra(AppConstants.TAGS.GROUP_IMAGE);
		mGroupSubscribe = getIntent().getStringExtra(AppConstants.TAGS.GROUP_SUBSCRIBE);
		mGroupAdmin= getIntent().getStringExtra(AppConstants.TAGS.GROUP_ADMIN);
		setActionBarTitle(mGroupName);
		if (Build.VERSION.SDK_INT < 11) {
			supportInvalidateOptionsMenu();
		} else {
			invalidateOptionsMenu();
		}
	}
	
	private void setActionBarTitle(String mGroupName){
		getSupportActionBar().setTitle(mGroupName);
	}
	
	private void getDataFromApi(){
		initVars();
		getLoadMoreData();
	}
	
	private void setScrollListener() {
		mGridView.setOnScrollListener(new OnScrollListener() {
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				
				if (firstVisibleItem + visibleItemCount == totalItemCount
						&& totalItemCount != 0){
					synchronized (this) {
						if (mLoadMoreFlag == false) {
							mLoadMoreFlag = true;
							Log.i(TAG, "mLoadMore");
							if(mFeedAdapter!=null){
								getLoadMoreData();
							}
						}
					}					
				}
			}
		});
	}
	
	private void initVars(){
		mScrollCount = 0;
		mFeed.clear();
		mFeedIndex = 0;
		mIsFirstTime = true;
	}
	
	@Override
	public void onRefreshStarted(View view) {
		// TODO Auto-generated method stub
			mPullToRefreshLayout.setRefreshComplete();
	}
	
	private void setPullToRefreshLoader(){
		mPullToRefreshLayout.setRefreshing(true);
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (requestCode == AppConstants.INTENT.FEED_COMMENT) {
				Feed mFeedRefershed =data.getExtras().getParcelable(AppConstants.INTENT.COMMENT_DATA);
				setFeedDataChange(mFeedRefershed,Integer.parseInt(data.getExtras().getString(AppConstants.TAGS.FEED_POSITION)));
			}
		}
	}
	
	private void setFeedDataChange(Feed mFeed, final int mPosition){
		try{
			this.mFeed.get(mPosition).setFeedIsLike(mFeed.getFeedIsLike());;
			this.mFeed.get(mPosition).setFeedIsTrue(mFeed.getFeedIsTrue());
			this.mFeed.get(mPosition).setFeedIsFalse(mFeed.getFeedIsFalse());
			this.mFeed.get(mPosition).setFeedLikeCount(mFeed.getFeedLikeCount());
			this.mFeed.get(mPosition).setFeedTrueCount(mFeed.getFeedTrueCount());
			this.mFeed.get(mPosition).setFeedFalseCount(mFeed.getFeedFalseCount());
			this.mFeed.get(mPosition).setFeedTruePer(mFeed.getFeedTruePer());
			this.mFeed.get(mPosition).setFeedFalsePer(mFeed.getFeedFalsePer());
			this.mFeed.get(mPosition).setFeedPer(mFeed.getFeedPer());
			
			runOnUiThread(new Runnable() {
				public void run() {
					mFeedAdapter.notifyDataSetChanged();
					mGridView.setSelection(mPosition);
				}
			});
		}catch(Exception e){
			Log.e(TAG, e.toString());
		}
	}
	
	private void getLoadMoreData(){
		Log.i(TAG, "getLoadMoreData");
		if(!mIsFirstTime){
			mGridView.addFooterView(mProgress);
		}else{
			setPullToRefreshLoader();
		}
		NetworkVolley nVolley = new NetworkVolley();
		JSONObject mJSONObject = RequestBuilder.getPostGroupFeedData(mGroupId, String.valueOf(mScrollCount));
		Log.i(TAG, AppConstants.URLS.URL_POST_GROUP_FEED);
		Log.i(TAG, mJSONObject.toString());
		VolleyPostJsonRequest req = nVolley.new VolleyPostJsonRequest(AppConstants.URLS.URL_POST_GROUP_FEED, mJSONObject,
			       new Listener<JSONObject>() {
			           @Override
			           public void onResponse(JSONObject response) {
			               try {
			                   VolleyLog.v("Response:%n %s", response.toString(4));
			                   Log.i(TAG, response.toString());
			                   if(response.getBoolean("success")){
									mScrollCount += mPaginationCount;
									parseFeedFromJSON(response);
									if(mIsFirstTime){
										GroupFeedActivity.this.runOnUiThread(new Runnable() {
											public void run() {
										mFeedAdapter = new FeedAdapter(GroupFeedActivity.this,GroupFeedActivity.TAG, mFeed, imageLoader, options);
										mGridView.addFooterView(mProgress);
										mGridView.setAdapter(mFeedAdapter);		
											}
										});
										mIsFirstTime = false;
										mPullToRefreshLayout.setRefreshComplete();
										if(mFeed.size() < mPaginationCount){
										mLoadMoreFlag = true;
										}
									}else{
										GroupFeedActivity.this.runOnUiThread(new Runnable() {
											public void run() {
												mFeedAdapter.addFeed(mFeed);
												mFeedAdapter.notifyDataSetChanged();		
											}
										});
									}
									mGridView.setSelection(mScrollCount-mPaginationCount);
									mGridView.removeFooterView(mProgress);
									mLoadMoreFlag = false;
							  }else{
								  try {
									  mGridView.removeFooterView(mProgress);
										mLoadMoreFlag = true;
									  if(mPullToRefreshLayout.isRefreshing()){
										  mPullToRefreshLayout.setRefreshComplete();
									  }
									} catch (Exception e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
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
							try {
								mGridView.removeFooterView(mProgress);
								mLoadMoreFlag = false;
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
			           }
			       });
		ApplicationLoader.getInstance().addToRequestQueue(req,TAG);
	}
	
	private void parseFeedFromJSON(JSONObject mJSONObject){
		String TAG = "parseFeedFromJSON";
		try{
			Log.i(TAG, TAG);
			JSONArray feedArray = mJSONObject.getJSONArray("feeds");
			for (int i = 0; i < feedArray.length(); i++) {
				Feed feed = new Feed();
				JSONObject mFeedObj = (JSONObject) feedArray.get(i);
				JSONArray feedMediaArray = null;
				JSONArray feedCommentArray = null;
				
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
				
				feed.setFeedId(mFeedObj.getString("id"));//feed_id
				feed.setFeedContent(mFeedObj.getString("posted_status"));//posted text
				feed.setFeedPostedUser(mFeedObj.getString("username"));//posted user
				feed.setFeedPostedImage(mFeedObj.getString("profile_pic"));//posted user image
				feed.setFeedPostedInGroup(mFeedObj.getString("posted_in_group"));//posted in group
				feed.setFeedIsLike(mFeedObj.getString("is_like"));//is user like
				feed.setFeedIsTrue(mFeedObj.getString("is_true"));//is user true
				feed.setFeedIsFalse(mFeedObj.getString("is_false"));// is user false
				feed.setFeedTime(mFeedObj.getString("created_time"));// posted time
				feed.setFeedTrueCount(mFeedObj.getString("true_count"));//post true count
				feed.setFeedFalseCount(mFeedObj.getString("false_count"));//post false count
				feed.setFeedLikeCount(mFeedObj.getString("like_count"));//like count
				feed.setFeedTruePer(mFeedObj.getString("true_percent"));//true per
				feed.setFeedFalsePer(mFeedObj.getString("false_percent"));//false per
				feed.setFeedPer(mFeedObj.getString("max_percentage"));//max_percentage
				feed.setFeedCommentCount(mFeedObj.getString("comments_count"));//comment_count
				feed.setFeedShare(mFeedObj.getString("share_link"));//share_link
				feed.setFeedIsReport(mFeedObj.getString("is_reported"));
				
				ArrayList<FeedMedia> arrayFeedMedia = new ArrayList<FeedMedia>();
				if(feedMediaArray!=null){
					for (int j = 0; j < feedMediaArray.length(); j++) {
						JSONObject mFeedMediaObj = (JSONObject) feedMediaArray.get(j);
						FeedMedia feedMedia = new FeedMedia();
						feedMedia.setFeedId(mFeedObj.getString("id")); // feed_id
						feedMedia.setFeedMediaPath(mFeedMediaObj.getString("path"));//feed_media_path
						feedMedia.setFeedIsVideo(mFeedMediaObj.getString("type"));//feed_media_is_video
						feedMedia.setFeedVideoThumbail(mFeedMediaObj.getString("thumbnail"));//feed_media_video_thumbnail
						arrayFeedMedia.add(feedMedia);
						
					}
				}
				feed.setFeedMedia(arrayFeedMedia);
				mFeed.add(feed);
			}
		}catch(JSONException e){
			Log.i(TAG, e.toString());
		}
		catch(Exception e){
			Log.i(TAG, e.toString());
		}
	}
	
	private void volleySubscribe() {
		final String TAG = "volleySubscribe";
		showProgressDialog("Subscribing..", GroupFeedActivity.this);
		NetworkVolley nVolley = new NetworkVolley();
		JSONObject mJSONObject = RequestBuilder.getPostGroupSubscribeData(mGroupId,"1");
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
								mGroupSubscribe="1";
								dismissProgressDialog();
								if (Build.VERSION.SDK_INT < 11) {
									supportInvalidateOptionsMenu();
								} else {
									invalidateOptionsMenu();
								}
								Toast.makeText(GroupFeedActivity.this, "Subscribed", Toast.LENGTH_SHORT).show();
								if (BuildConfig.DEBUG) {
									VolleyLog.v("Response:%n %s",
											response.toString(4));
									Log.i(TAG, response.toString());
								}
							}
						} catch (JSONException e) {
							e.printStackTrace();
							dismissProgressDialog();
						}
					}
				}, new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						dismissProgressDialog();
						VolleyLog.e("Error: ", error.getMessage());
						if (BuildConfig.DEBUG) {
							Log.i(TAG, "Volley Error : " + error.getMessage());
							VolleyLog.e("Error: ", error.getMessage());
						}
					}
				});
		ApplicationLoader.getInstance().addToRequestQueue(req, TAG);
	}

	private void volleyUnSubscribe() {
		final String TAG = "volleyUnSubscribe";
		showProgressDialog("Unsubscribing..", GroupFeedActivity.this);
		NetworkVolley nVolley = new NetworkVolley();
		JSONObject mJSONObject = RequestBuilder.getPostGroupSubscribeData(mGroupId,"0");
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
								mGroupSubscribe="0";
								dismissProgressDialog();
								if (Build.VERSION.SDK_INT < 11) {
									supportInvalidateOptionsMenu();
								} else {
									invalidateOptionsMenu();
								}
								Toast.makeText(GroupFeedActivity.this, "Unsubscribed", Toast.LENGTH_SHORT).show();
								if (BuildConfig.DEBUG) {
									VolleyLog.v("Response:%n %s",
											response.toString(4));
									Log.i(TAG, response.toString());
								}
							}
						} catch (JSONException e) {
							e.printStackTrace();
							dismissProgressDialog();
						}
					}
				}, new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						dismissProgressDialog();
						VolleyLog.e("Error: ", error.getMessage());
						if (BuildConfig.DEBUG) {
							Log.i(TAG, "Volley Error : " + error.getMessage());
							VolleyLog.e("Error: ", error.getMessage());
						}
					}
				});
		ApplicationLoader.getInstance().addToRequestQueue(req, TAG);
	}

	private void showProgressDialog(String str, Context mContext){
		mProgressDialog = new ProgressDialog(mContext);
		mProgressDialog.setCancelable(true);
		mProgressDialog.setMessage(str);
		mProgressDialog.show();
	}
	
	private void dismissProgressDialog(){
		if(mProgressDialog!=null)
			mProgressDialog.dismiss();
	}
}
