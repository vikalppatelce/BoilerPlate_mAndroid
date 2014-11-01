package com.netdoers.ui;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import uk.co.senab.actionbarpulltorefresh.extras.actionbarcompat.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.netdoers.beans.Group;
import com.netdoers.tellus.BuildConfig;
import com.netdoers.tellus.R;
import com.netdoers.ui.view.GroupAdapter;
import com.netdoers.utils.AppConstants;
import com.netdoers.utils.ApplicationLoader;
import com.netdoers.utils.NetworkVolley;
import com.netdoers.utils.NetworkVolley.VolleyPostJsonRequest;
import com.netdoers.utils.RequestBuilder;
import com.netdoers.utils.RestClient;
import com.netdoers.utils.Utilities;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class GroupSearchActivity extends ActionBarActivity implements SearchView.OnQueryTextListener{

	private ListView mListView;
	private ProgressBar mProgress;
	
	private ImageLoader imageLoader;
	private DisplayImageOptions options;
	
	private ArrayList<Group> mGroup;
	
	private GroupAdapter mAdapter;
	
	private static final String TAG = GroupSearchActivity.class.getSimpleName();
	private int mScrollCount;
	private int mPaginationCount = 20;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_group_search);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		initUi();
		initObjects();
		setUniversalImageLoader();
		setEventListeners();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mGroup.clear();
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_group_search, menu);
		MenuItem  searchMenuItem = menu.findItem(R.id.action_search);
	    SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchMenuItem);
	    searchView.setOnQueryTextListener(this);
	    searchView.setIconified(false);
		return super.onCreateOptionsMenu(menu);
	}
	
	private void initUi(){
		mListView = (ListView)findViewById(R.id.activity_group_search_listview);
		mProgress = (ProgressBar)findViewById(R.id.activity_group_search_progress);
		mProgress.getIndeterminateDrawable().setColorFilter(
	            getResources().getColor(R.color.background_actionbar),
	            android.graphics.PorterDuff.Mode.SRC_IN);
	}
	
	private void initObjects(){
		mGroup = new ArrayList<Group>();
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
				Intent mIntent = new Intent(GroupSearchActivity.this, GroupFeedActivity.class);
				mIntent.putExtra(AppConstants.TAGS.GROUP_ID, mGroupId);
				mIntent.putExtra(AppConstants.TAGS.GROUP_NAME, mGroupName);
				mIntent.putExtra(AppConstants.TAGS.GROUP_IMAGE, mGroupImage);
				mIntent.putExtra(AppConstants.TAGS.GROUP_SUBSCRIBE, mGroupSubscribe);
				mIntent.putExtra(AppConstants.TAGS.GROUP_ADMIN, mGroupAdmin);
				startActivity(mIntent);
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
			Intent mIntent = new Intent(GroupSearchActivity.this, CreateGroupActivity.class);
			startActivity(mIntent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	private void parseJSON(JSONObject jsonObject) {
		try {
			mGroup.clear();
			JSONArray groupsArray = jsonObject.getJSONArray("groups");
			for (int i = 0; i < groupsArray.length(); i++) {
				Group mGroupObj = new Group();
				JSONObject groupObj = (JSONObject) groupsArray.get(i);
				mGroupObj.setGroupId(groupObj.getString("group_id"));
				mGroupObj.setGroupName(groupObj.getString("group_name"));
				mGroupObj.setGroupFriends("");
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

	private void volleySearch(String mGroupName){
		final String TAG = "volleySearch";
		NetworkVolley nVolley = new NetworkVolley();
		mProgress.setVisibility(View.VISIBLE);
		JSONObject mJSONObject = RequestBuilder.getPostGroupSearchData(mGroupName);
		Log.i(TAG, AppConstants.URLS.URL_POST_GROUP_SEARCH);
		Log.i(TAG, mJSONObject.toString());
		VolleyPostJsonRequest req = nVolley.new VolleyPostJsonRequest(AppConstants.URLS.URL_POST_GROUP_SEARCH, mJSONObject,
			       new Listener<JSONObject>() {
			           @Override
			           public void onResponse(JSONObject response) {
			               try {
			                   VolleyLog.v("Response:%n %s", response.toString(4));
			                   Log.i(TAG, response.toString());
     	                	   parseJSON(response);
	          				   runOnUiThread(new Runnable() {
								public void run() {
									mAdapter = new GroupAdapter(GroupSearchActivity.this, mGroup,imageLoader, options,false);
									mListView.setAdapter(mAdapter);
								}
							  });
			                   mProgress.setVisibility(View.GONE);
			               } catch (JSONException e) {
			                   e.printStackTrace();
			               }
			           }
			       }, new ErrorListener() {
			           @Override
			           public void onErrorResponse(VolleyError error) {
			               VolleyLog.e("Error: ", error.getMessage());
							try {
								mProgress.setVisibility(View.GONE);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
			           }
			       });
		ApplicationLoader.getInstance().addToRequestQueue(req,TAG);
	}
	
	@Override
	public boolean onQueryTextChange(String searchTxt) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onQueryTextSubmit(String searchTxt) {
		// TODO Auto-generated method stub
		volleySearch(searchTxt);
		return true;
	} 	
	
}
