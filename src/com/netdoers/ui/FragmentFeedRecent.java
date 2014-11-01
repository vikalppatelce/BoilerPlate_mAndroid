package com.netdoers.ui;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

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
import android.widget.GridView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.netdoers.beans.Feed;
import com.netdoers.beans.FeedMedia;
import com.netdoers.tellus.R;
import com.netdoers.ui.view.FeedAdapter;
import com.netdoers.utils.ApplicationLoader;
import com.netdoers.utils.NetworkVolley;
import com.netdoers.utils.NetworkVolley.VolleyGetJsonRequest;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class FragmentFeedRecent extends Fragment implements IFragmentCommunicator{

	private IActivityCommunicator mActivityCommunicator;
	
	private Context mContext;
	
//	private GridView mGridView;
	private ListView mGridView;
//	private ProgressBar mProgress;
	private View mProgress;
	
	private ImageLoader imageLoader;
	private DisplayImageOptions options;
	
	private FeedAdapter mFeedAdapter;
	private ArrayList<Feed> mFeed;
	
	private boolean mLoadMoreFlag = false;
	private boolean mIsFirstTime = true;
	private int mScrollCount = 0;
	private int mPaginationCount = 3;
	
	private static String TAG = FragmentFeedRecent.class.getSimpleName();

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
		setUniversalImageLoader();
		setScrollListener();
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
			tempFeedFiller(mPaginationCount);
			mFeedAdapter = new FeedAdapter(getActivity(), FragmentFeedRecent.TAG,mFeed, imageLoader, options);
			mGridView.addFooterView(mProgress);
			mGridView.setAdapter(mFeedAdapter);
		}
	}
	
	private void initObj(){
		mFeed = new ArrayList<Feed>();
	}

	private void tempFeedFiller(int k){
		for(int i=mScrollCount ; i < k;i++){
			String truePer = String.valueOf(0 + (10 * i));
			String falsePer = String.valueOf(100 - (10 * i));
			Feed feedObj = new Feed();
			feedObj.setFeedId(String.valueOf(i));
			feedObj.setFeedPostedInGroup("Kollywood_Release_" +String.valueOf(i));
			feedObj.setFeedPostedUser("Kumod Bhai_"+String.valueOf(i));
			feedObj.setFeedPostedImage("https://scontent-b-atl.xx.fbcdn.net/hphotos-xpf1/t1.0-9/381957_2254986699630_1998059521_n.jpg");
			feedObj.setFeedTrueCount(truePer);
			feedObj.setFeedFalseCount(falsePer);
			feedObj.setFeedTime("24 minutes ago");
			feedObj.setFeedIsTrue("0");
			feedObj.setFeedIsFalse("0");
			feedObj.setFeedIsLike("0");
			feedObj.setFeedContent("Tell Us Tell Us");
			feedObj.setFeedFalsePer(falsePer);
			feedObj.setFeedTruePer(truePer);
			feedObj.setFeedPer(truePer);
			feedObj.setFeedLikeCount(truePer);
			ArrayList<FeedMedia> feedObjMedia  = new ArrayList<FeedMedia>();
			for(int j=0 ; j < 3;j++){
				FeedMedia mFeedMedia = new FeedMedia();
				mFeedMedia.setFeedMediaPath("https://scontent-b-atl.xx.fbcdn.net/hphotos-xpf1/t1.0-9/381957_2254986699630_1998059521_n.jpg");
				mFeedMedia.setFeedIsVideo("image");
				feedObjMedia.add(mFeedMedia);
			}
			feedObj.setFeedMedia(feedObjMedia);
			mFeed.add(feedObj);
		}
		mLoadMoreFlag = false;
	}

//	@Override
//	public void passDataToFragment(String someValue) {
//		// TODO Auto-generated method stub
//		Toast.makeText(getActivity(), String.valueOf(someValue), Toast.LENGTH_SHORT).show();
//	}
	
	@Override
	public void passFeedDataToFragment(Feed mFeed, int mPosition ,String someValue) {
		// TODO Auto-generated method stub
		Toast.makeText(getActivity(), someValue, Toast.LENGTH_SHORT).show();
	}
	
	private void setUniversalImageLoader() {
		imageLoader = ImageLoader.getInstance();
		imageLoader.init(ImageLoaderConfiguration.createDefault(getActivity()));
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable._def_contact)
				.showImageForEmptyUri(R.drawable._def_contact)
				.showImageOnFail(R.drawable._def_contact).cacheInMemory()
				.cacheOnDisc().build();
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
	
	
	private void getLoadMoreData(){
		Log.i(TAG, "getLoadMoreData");
		if(!mIsFirstTime){
			mGridView.addFooterView(mProgress);
		}
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
									mScrollCount += mPaginationCount;
									tempFeedFiller(mScrollCount+mPaginationCount);
//									mFeedAdapter = new FeedAdapter(getActivity(), FragmentFeedRecent.TAG, mFeed, imageLoader, options);
//									mGridView.setAdapter(mFeedAdapter);
//									if(mIsFirstTime){
//										mFeedAdapter = new FeedAdapter(
//												getActivity(),
//												FragmentFeedRecent.TAG, mFeed,
//												imageLoader, options);
//											mGridView.setAdapter(mFeedAdapter);
//											mIsFirstTime = false;
//										}else{
//											mFeedAdapter.addFeed(mFeed);
//											mFeedAdapter.notifyDataSetChanged();	
//										}
									mFeedAdapter.addFeed(mFeed);
									mFeedAdapter.notifyDataSetChanged();
//									mFeedAdapter.notifyDataSetChanged();
//									mGridView.setSelection(mGridView.getLastVisiblePosition()+1);
									mGridView.setSelection(mScrollCount);
									mGridView.removeFooterView(mProgress);
//									mGridView.smoothScrollToPosition(3);
							  }
			               } catch (JSONException e) {
			                   e.printStackTrace();
			               }catch (Exception e) {
			                   e.printStackTrace();
			               }
			           }
			       }, new ErrorListener() {
			           @Override
			           public void onErrorResponse(VolleyError error) {
			               VolleyLog.e("Error: ", error.getMessage());
//							mGridView.removeFooterView(mProgress);
			               try{
			            	   if(mFeedAdapter!=null){
					            	  mGridView.removeFooterView(mProgress); 
					               }
			               }catch(Exception e){
			            	   Log.e(TAG, e.toString());
			               }
			           }
			       });
		ApplicationLoader.getInstance().addToRequestQueue(req,TAG);
	}
}
