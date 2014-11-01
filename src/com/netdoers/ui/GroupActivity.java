package com.netdoers.ui;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import uk.co.senab.actionbarpulltorefresh.extras.actionbarcompat.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.netdoers.beans.Group;
import com.netdoers.tellus.R;
import com.netdoers.ui.view.GroupAdapter;
import com.netdoers.utils.AppConstants;
import com.netdoers.utils.ApplicationLoader;
import com.netdoers.utils.NetworkVolley;
import com.netdoers.utils.NetworkVolley.VolleyPostJsonRequest;
import com.netdoers.utils.RequestBuilder;
import com.netdoers.utils.Utilities;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class GroupActivity extends ActionBarActivity implements OnRefreshListener{

	private ListView mListView;
	private PullToRefreshLayout mPullToRefreshLayout;
	private ProgressBar mProgress;
	private RelativeLayout mNoDataLayout;
	private TextView mNoDataTxt;
	private TextView mNoDataRetry;
	
	private ImageLoader imageLoader;
	private DisplayImageOptions options;
	
	private ArrayList<Group> mGroup;
	
	private GroupAdapter mAdapter;
	
	private static final String TAG = GroupActivity.class.getSimpleName();
	private int mScrollCount;
	private int mPaginationCount = 20;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_group);
		setActionBar();
		initUi();
		initObjects();
		setUniversalImageLoader();
		setEventListeners();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		initObjects();
		getLoadMoreDataVolley();
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_group, menu);
		/*MenuItem  searchMenuItem = menu.findItem(R.id.action_search);
	    SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchMenuItem);
	    searchView.setOnQueryTextListener(this);*/
		return super.onCreateOptionsMenu(menu);
	}
	
	private void initUi(){
		mListView = (ListView)findViewById(R.id.activity_group_listview);
		mPullToRefreshLayout = (PullToRefreshLayout)findViewById(R.id.activity_group_swipe_refresh_layout);
		mNoDataLayout = (RelativeLayout)findViewById(R.id.no_data_layout);
		mNoDataTxt = (TextView)findViewById(R.id.no_data_text);
		mNoDataRetry = (TextView)findViewById(R.id.no_data_retry);
		mProgress = (ProgressBar)findViewById(R.id.activity_group_progress);
		ActionBarPullToRefresh.from(this)
        .allChildrenArePullable()
        .listener(this)
        .setup(mPullToRefreshLayout);
	}
	
	private void initObjects(){
		mGroup = new ArrayList<Group>();
	}
	
	private void initVars(){
		mScrollCount = 0;
		mGroup.clear();
	}
	
	private void setActionBar(){
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		getSupportActionBar().setTitle(getResources().getString(R.string.activity_groups));
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
	
	private void setEventListeners(){
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				String mGroupId = view.getTag(R.id.TAG_GROUP_ID).toString();
				String mGroupName = view.getTag(R.id.TAG_GROUP_NAME).toString();
				String mGroupImage = view.getTag(R.id.TAG_GROUP_IMAGE).toString();
				String mGroupSubscribe = view.getTag(R.id.TAG_GROUP_SUBSCRIBE).toString();
				String mGroupAdmin = view.getTag(R.id.TAG_GROUP_ADMIN).toString();
				Intent mIntent = new Intent(GroupActivity.this, GroupFeedActivity.class);
				mIntent.putExtra(AppConstants.TAGS.GROUP_ID, mGroupId);
				mIntent.putExtra(AppConstants.TAGS.GROUP_NAME, mGroupName);
				mIntent.putExtra(AppConstants.TAGS.GROUP_IMAGE, mGroupImage);
				mIntent.putExtra(AppConstants.TAGS.GROUP_SUBSCRIBE, mGroupSubscribe);
				mIntent.putExtra(AppConstants.TAGS.GROUP_ADMIN, mGroupAdmin);
				startActivity(mIntent);
			}
		});
		
		mNoDataRetry.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				initVars();
				getLoadMoreDataVolley();
			}
		});
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		case R.id.action_add_group:
			Intent mIntent = new Intent(GroupActivity.this, CreateGroupActivity.class);
			startActivity(mIntent);
			return true;
		case R.id.action_group_search:
			Intent mSearchIntent = new Intent(GroupActivity.this, GroupSearchActivity.class);
			startActivity(mSearchIntent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void setPullToRefreshLoader(){
		mPullToRefreshLayout.setRefreshing(true);
	}
	
	public void onRefreshStarted(View view) {
		mPullToRefreshLayout.setRefreshComplete();
	}
	
	private void getLoadMoreDataVolley(){
		if(Utilities.isInternetConnected()){
			Log.i(TAG, "getLoadMoreDataVolley");
			setPullToRefreshLoader();
			NetworkVolley nVolley = new NetworkVolley();
			JSONObject mDataJSON = RequestBuilder.getPostApiData();
			Log.i(TAG, mDataJSON.toString());
			Log.i(TAG, AppConstants.URLS.URL_POST_GROUPS);
			VolleyPostJsonRequest req = nVolley.new VolleyPostJsonRequest(AppConstants.URLS.URL_POST_GROUPS, mDataJSON,
				       new Listener<JSONObject>() {
				           @Override
				           public void onResponse(JSONObject response) {
				               try {
				                   VolleyLog.v("Response:%n %s", response.toString(4));
				                   Log.i(TAG, response.toString());
//										mScrollCount += mPaginationCount;
										parseJSON(response);
										
										if(mGroup!=null && mGroup.size() > 0){
											Utilities.errorApiGone(mNoDataLayout, mNoDataTxt);
										}else{
											Utilities.errorNoData(mNoDataLayout, mNoDataTxt);
										}
										
										if(mAdapter!=null){
					                		   GroupActivity.this.runOnUiThread(new Runnable() {
												public void run() {
											   mAdapter.addGroup(mGroup);
					                		   mListView.setAdapter(mAdapter);
					                		   mAdapter.notifyDataSetChanged();		
												}
											});
					                	   }else{
					                		   GroupActivity.this.runOnUiThread(new Runnable() {
												@Override
												public void run() {
													// TODO Auto-generated method stub
													mAdapter = new GroupAdapter(GroupActivity.this, mGroup, imageLoader, options,true);
							                		mListView.setAdapter(mAdapter); 													
												}
											});  
					                	   }
										mPullToRefreshLayout.setRefreshComplete();
				               } catch (JSONException e) {
				                   e.printStackTrace();
				               }
				           }
				       }, new ErrorListener() {
				           @Override
				           public void onErrorResponse(VolleyError error) {
				        	   Utilities.errorApi(mNoDataLayout, mNoDataTxt);
				               VolleyLog.e("Error: ", error.getMessage());
								mPullToRefreshLayout.setRefreshComplete();
				           }
				       });
			ApplicationLoader.getInstance().addToRequestQueue(req,TAG);
		}else{
			Toast.makeText(GroupActivity.this, R.string.noconnection, Toast.LENGTH_SHORT).show();
		}
	}
	
	private void parseJSON(JSONObject jsonObject) {
		try {
			JSONArray groupsArray = jsonObject.getJSONArray("groups");
			for (int i = 0; i < groupsArray.length(); i++) {
				Group mGroupObj = new Group();
				JSONObject groupObj = (JSONObject) groupsArray.get(i);
				mGroupObj.setGroupId(groupObj.getString("group_id"));
				mGroupObj.setGroupName(groupObj.getString("group_name"));
				mGroupObj.setGroupFriends(groupObj.getString("friends_count"));
				mGroupObj.setGroupImagePath(groupObj.getString("group_pic"));
				mGroupObj.setGroupIsSubscribe(groupObj.getString("is_subscribe"));
				mGroupObj.setGroupIsAdmin(groupObj.getString("is_admin"));
				mGroup.add(mGroupObj);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
