package com.netdoers.ui;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.bolts.BuildConfig;
import com.netdoers.beans.Feed;
import com.netdoers.beans.FeedMedia;
import com.netdoers.tellus.R;
import com.netdoers.ui.view.FeedAdapter;
import com.netdoers.utils.AppConstants;
import com.netdoers.utils.ApplicationLoader;
import com.netdoers.utils.NetworkVolley;
import com.netdoers.utils.NetworkVolley.VolleyPostJsonRequest;
import com.netdoers.utils.RequestBuilder;
import com.netdoers.utils.Utilities;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class FragmentFeedAll extends Fragment implements IFragmentCommunicator, OnRefreshListener {

	private IActivityCommunicator mActivityCommunicator;
	
	private Context mContext;
	
//	private GridView mGridView;
	private ListView mGridView;
//	private ProgressBar mProgress;
	private View mProgress;
	private PullToRefreshLayout mPullToRefreshLayout;
	
	private ImageLoader imageLoader;
	private DisplayImageOptions options;
	
	private RelativeLayout mNoDataLayout;
	private TextView mNoDataTxt;
	private TextView mNoDataRetry;
	
	private FeedAdapter mFeedAdapter;
	private ArrayList<Feed> mFeed;
	
	private boolean mLoadMoreFlag = false;
	private int mScrollCount = 0;
	private int mPaginationCount = 10;
	private boolean mIsFirstTime = true;
	
	private static String TAG = FragmentFeedAll.class.getSimpleName();

	 @Override
     public void onAttach(Activity activity){
       super.onAttach(activity);
       mContext = getActivity();
       mActivityCommunicator =(IActivityCommunicator)mContext;
       ((MotherActivity)mContext).mFragmentCommunicator = this;
     }
	 
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		ActionBarPullToRefresh.from(getActivity())
		.allChildrenArePullable()
        .listener(this)
        .setup(mPullToRefreshLayout);
		setUniversalImageLoader();
		setScrollListener();
		setEventListeners();
		initObj();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_feed, container, false);
//		mGridView = (GridView) view.findViewById(R.id.fragment_feed_gridview);
		mGridView = (ListView) view.findViewById(R.id.fragment_feed_gridview);
//		mProgress = (ProgressBar)view.findViewById(R.id.fragment_feed_progress);
		mPullToRefreshLayout = (PullToRefreshLayout)view.findViewById(R.id.fragment_swipe_refresh_layout);
		mNoDataLayout = (RelativeLayout)view.findViewById(R.id.no_data_layout);
		mNoDataTxt = (TextView)view.findViewById(R.id.no_data_text);
		mNoDataRetry = (TextView)view.findViewById(R.id.no_data_retry);
		mProgress = ((LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.item_footerview, null, false);
		ProgressBar mProgressBar = (ProgressBar)mProgress.findViewById(R.id.fragment_feed_group_progress);
		mProgressBar.getIndeterminateDrawable().setColorFilter(
	            getResources().getColor(R.color.background_actionbar),
	            android.graphics.PorterDuff.Mode.SRC_IN);

		return view;
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		getLoadMoreData();
	}

	private void initObj(){
		mFeed = new ArrayList<Feed>();
	}

	private void setEventListeners(){
		mNoDataRetry.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				initVars();
				getLoadMoreData();
			}
		});
	}
//	@Override
//	public void passDataToFragment(String someValue) {
//		// TODO Auto-generated method stub
//		Toast.makeText(getActivity(), String.valueOf(someValue)+"Yuppie!", Toast.LENGTH_SHORT).show();
//	}
	
	@Override
	public void passFeedDataToFragment(Feed mFeed, final int mPosition ,String someValue) {
		// TODO Auto-generated method stub
		if(someValue.equalsIgnoreCase("ALL")){
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
				this.mFeed.get(mPosition).setFeedCommentCount(mFeed.getFeedCommentCount());
				getActivity().runOnUiThread(new Runnable() {
					public void run() {
						mFeedAdapter.addFeed(FragmentFeedAll.this.mFeed);
						mFeedAdapter.notifyDataSetChanged();
						mGridView.setSelection(mPosition);
					}
				});
				
				if(BuildConfig.DEBUG){
					Log.i(TAG, "passFeedDataToFragmentALL");
					Toast.makeText(getActivity(), someValue+"Hurray!", Toast.LENGTH_SHORT).show();
				}
				
			}catch(Exception e){
				Log.e(TAG, e.toString());
			}
		}
	}
	
	private void setUniversalImageLoader() {
		imageLoader = ImageLoader.getInstance();
		imageLoader.init(ImageLoaderConfiguration.createDefault(getActivity()));
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.feed_gallery_bg)
				.showImageForEmptyUri(R.drawable.feed_gallery_bg)
				.showImageOnFail(R.drawable.feed_gallery_bg).cacheInMemory()
				.cacheOnDisc().build();
	}
	
	private void setScrollListener() {
		mGridView.setOnScrollListener(new OnScrollListener() {
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				
				if (firstVisibleItem + visibleItemCount == totalItemCount
						&& totalItemCount != 0){/*
					synchronized (this) */{
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
	
	private void setPullToRefreshLoader(){
		mPullToRefreshLayout.setRefreshing(true);
	}
	
	private void getLoadMoreData() {
		if (!mIsFirstTime) {
			mGridView.addFooterView(mProgress);
		} else {
			setPullToRefreshLoader();
		}
		
		NetworkVolley nVolley = new NetworkVolley();
		JSONObject mJSONObject = RequestBuilder.getPostFeedData(String
				.valueOf(mScrollCount));
		Log.i(TAG, mJSONObject.toString());
		Log.i(TAG, AppConstants.URLS.URL_POST_FEED_ALL);
		VolleyPostJsonRequest req = nVolley.new VolleyPostJsonRequest(
				AppConstants.URLS.URL_POST_FEED_ALL, mJSONObject,
				new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						try {
							VolleyLog.v("Response:%n %s", response.toString(4));
							Log.i(TAG, response.toString());
							if (response.getBoolean("success")) {
								mScrollCount += mPaginationCount;
								parseFeedFromJSON(response);
								if (mFeed != null && mFeed.size() > 0) {
									Utilities.errorApiGone(mNoDataLayout,
											mNoDataTxt);
								} else {
									Utilities.errorNoData(mNoDataLayout,
											mNoDataTxt);
								}
								if (mIsFirstTime) {
									getActivity().runOnUiThread(new Runnable() {
										public void run() {
											mFeedAdapter = new FeedAdapter(
													getActivity(),
													FragmentFeedAll.TAG, mFeed,
													imageLoader, options);
											mGridView.addFooterView(mProgress);
											mGridView.setAdapter(mFeedAdapter);
										}
									});
									mIsFirstTime = false;
									mPullToRefreshLayout.setRefreshComplete();
									if (mFeed.size() < mPaginationCount) {
										mLoadMoreFlag = true;
									}
								} else {
									getActivity().runOnUiThread(new Runnable() {
										public void run() {
											mFeedAdapter.addFeed(mFeed);
											mFeedAdapter.notifyDataSetChanged();
										}
									});
								}
								mGridView.setSelection(mScrollCount
										- mPaginationCount);
								mGridView.removeFooterView(mProgress);
								mLoadMoreFlag = false;
								mPullToRefreshLayout.setRefreshComplete();
							} else {
								if (mScrollCount < mPaginationCount) {
									Utilities.errorNoData(mNoDataLayout,
											mNoDataTxt);
									if(mIsFirstTime)
										mPullToRefreshLayout.setRefreshComplete();
									
								} else {
									try {
										mGridView.removeFooterView(mProgress);
										mLoadMoreFlag = true;
									} catch (Exception e) {
										Log.i(TAG, e.toString());
									}
								}
							}
						} catch (JSONException e) {
							e.printStackTrace();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}, new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						VolleyLog.e("Error: ", error.getMessage());
						try {
//							Utilities.errorApi(mNoDataLayout, mNoDataTxt);
							
							if(mIsFirstTime)
								mPullToRefreshLayout.setRefreshComplete();
							
							if (mFeedAdapter != null) {
								mGridView.removeFooterView(mProgress);
								mLoadMoreFlag = false;
							}
						} catch (Exception e) {
							Log.e(TAG, e.toString());
						}
					}
				});
		ApplicationLoader.getInstance().addToRequestQueue(req, TAG);
	}
	
	@Override
	public void onRefreshStarted(View view) {
		initVars();
		getLoadMoreData();
	}
	
	private void initVars(){
		mScrollCount = 0;
		mFeed.clear();
		mIsFirstTime = true;
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
				
				try{
					feedMediaArray = mFeedObj.getJSONArray("resources");
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
}
