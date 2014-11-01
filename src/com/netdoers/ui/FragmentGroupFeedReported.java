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

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.netdoers.beans.Feed;
import com.netdoers.beans.FeedMedia;
import com.netdoers.tellus.R;
import com.netdoers.ui.view.FeedReportedAdapter;
import com.netdoers.utils.AppConstants;
import com.netdoers.utils.ApplicationLoader;
import com.netdoers.utils.NetworkVolley;
import com.netdoers.utils.NetworkVolley.VolleyPostJsonRequest;
import com.netdoers.utils.RequestBuilder;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class FragmentGroupFeedReported extends Fragment implements OnRefreshListener {

	private Context mContext;
	
//	private GridView mGridView;
	private ListView mGridView;
//	private ProgressBar mProgress;
	private View mProgress;
	private PullToRefreshLayout mPullToRefreshLayout;
	
	private ImageLoader imageLoader;
	private DisplayImageOptions options;
	
	private FeedReportedAdapter mFeedAdapter;
	private static ArrayList<Feed> mFeed;
	
	private boolean mLoadMoreFlag = false;
	private int mScrollCount = 0;
	private int mPaginationCount = 3;
	private boolean mIsFirstTime = true;
	
	private String mGroupId;
	
	private static String TAG = FragmentGroupFeedReported.class.getSimpleName();

	 @Override
     public void onAttach(Activity activity){
       super.onAttach(activity);
       mContext = getActivity();
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
		initObj();
		getIntentData();
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
		if(mFeedAdapter!=null){
			mGridView.setAdapter(mFeedAdapter);
			mIsFirstTime = false;
		}else{
			mIsFirstTime = true;
			getLoadMoreData();
		}
	}

	private void initObj(){
		mFeed = new ArrayList<Feed>();
	}
	
	private void getIntentData(){
		mGroupId = getActivity().getIntent().getExtras().getString(AppConstants.TAGS.GROUP_ID);
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
//								getLoadMoreData();
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
	
	
	private void getLoadMoreData(){
		Log.i(TAG, "getLoadMoreData");
		if(!mIsFirstTime){
			mGridView.addFooterView(mProgress);
		}else{
			setPullToRefreshLoader();
		}
		NetworkVolley nVolley = new NetworkVolley();
		JSONObject mJSONObject = RequestBuilder.getPostGroupFeedData(mGroupId, String.valueOf(mScrollCount));
		Log.i(TAG, AppConstants.URLS.URL_POST_GROUP_REPORT_FEED);
		Log.i(TAG, mJSONObject.toString());
		VolleyPostJsonRequest req = nVolley.new VolleyPostJsonRequest(AppConstants.URLS.URL_POST_GROUP_REPORT_FEED, mJSONObject,
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
										getActivity().runOnUiThread(new Runnable() {
											public void run() {
										mFeedAdapter = new FeedReportedAdapter(getActivity(),TAG, mFeed, imageLoader, options);
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
										getActivity().runOnUiThread(new Runnable() {
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
				
				ArrayList<FeedMedia> arrayFeedMedia = new ArrayList<FeedMedia>();
				if(feedMediaArray!=null){
					for (int j = 0; j < feedMediaArray.length(); j++) {
						JSONObject mFeedMediaObj = (JSONObject) feedMediaArray.get(j);
						FeedMedia feedMedia = new FeedMedia();
						feedMedia.setFeedId(mFeedObj.getString("id")); // feed_id
						feedMedia.setFeedMediaPath(mFeedMediaObj.getString("path"));//feed_media_path
						feedMedia.setFeedIsVideo(mFeedMediaObj.getString("type"));//feed_media_is_video
//						feedMedia.setFeedMediaId(mFeedMediaObj.getString(""));//feed_media_id
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
	
	@Override
	public void onRefreshStarted(View view) {
		mPullToRefreshLayout.setRefreshComplete();
	}
}
