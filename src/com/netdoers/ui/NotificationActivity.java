package com.netdoers.ui;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.netdoers.tellus.BuildConfig;
import com.netdoers.tellus.R;
import com.netdoers.ui.view.NotificationAdapter;
import com.netdoers.utils.AppConstants;
import com.netdoers.utils.ApplicationLoader;
import com.netdoers.utils.DBConstant;
import com.netdoers.utils.NetworkVolley;
import com.netdoers.utils.NetworkVolley.VolleyPostJsonRequest;
import com.netdoers.utils.RequestBuilder;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class NotificationActivity extends ActionBarActivity implements
		OnRefreshListener {

	private ListView mListView;
	private PullToRefreshLayout mPullToRefreshLayout;

	private ImageLoader imageLoader;
	private DisplayImageOptions options;
	private ArrayList<com.netdoers.beans.Notification> mNotification;
	
	private NotificationAdapter mAdapter;

	private static String TAG = NotificationActivity.class.getSimpleName();

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_notification);
		setActionBar();
		initUi();
		setUniversalImageLoader();
		setEventListeners();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		initObj();
		setPullToRefreshLayout();
		feedNotificationCollection();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	private void setActionBar(){
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		getSupportActionBar().setTitle(getResources().getString(R.string.activity_notification));
	}
	
	private void initUi() {
		mListView = (ListView) findViewById(R.id.activity_notification_list_view);
		mPullToRefreshLayout = (PullToRefreshLayout) findViewById(R.id.fragment_swipe_refresh_layout);
		ActionBarPullToRefresh.from(this).allChildrenArePullable()
				.listener(this).setup(mPullToRefreshLayout);

	}

	private void initObj(){
		mNotification = new ArrayList<com.netdoers.beans.Notification>();
	}
	
	private void setPullToRefreshLayout(){
		mPullToRefreshLayout.setRefreshing(true);
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
				Intent mCommentIntent = new Intent(NotificationActivity.this, FeedDetailActivity.class);
				String mPostId = view.getTag(R.id.TAG_POST_ID).toString();
				String mNotificationId = view.getTag(R.id.TAG_NOTIFICAION_ID).toString();
				
				mCommentIntent.putExtra(AppConstants.TAGS.FEED_POSITION, String.valueOf(position));
				mCommentIntent.putExtra(AppConstants.TAGS.POST_ID, mPostId);

				volleySeen(mPostId, mNotificationId);

				startActivityForResult(mCommentIntent, AppConstants.INTENT.FEED_COMMENT);
			}
		});
	}
	
	@Override
	public void onRefreshStarted(View view) {
		// TODO Auto-generated method stub
	}
	
	private void feedNotificationCollection(){
		Cursor cr = getContentResolver().query(
				DBConstant.Notification_Columns.CONTENT_URI, null, null, null,
				DBConstant.Notification_Columns.COLUMN_ID + " DESC");
		int columnId = cr.getColumnIndex(DBConstant.Notification_Columns.COLUMN_ID);
		int columnNotifId = cr.getColumnIndex(DBConstant.Notification_Columns.COLUMN_NOTIF_ID);
		int columnNotifFromUser = cr.getColumnIndex(DBConstant.Notification_Columns.COLUMN_NOTIF_FROM_USER);
		int columnNotifFromUserImage = cr.getColumnIndex(DBConstant.Notification_Columns.COLUMN_NOTIF_FROM_USER_PATH);
		int columnNotifWhat = cr.getColumnIndex(DBConstant.Notification_Columns.COLUMN_NOTIF_WHAT);
//		int columnNotifTo = cr.getColumnIndex(DBConstant.Notification_Columns.COLUMN_NOTIF_TO);
//		int columnNotifPostId = cr.getColumnIndex(DBConstant.Notification_Columns.COLUMN_NOTIF_POST_ID);
		int columnNotifPage = cr.getColumnIndex(DBConstant.Notification_Columns.COLUMN_NOTIF_PAGE);
		int columnNotifReadStatus = cr.getColumnIndex(DBConstant.Notification_Columns.COLUMN_READ_STATUS);
		
		if(cr.getCount() > 0){
			while(cr.moveToNext()){
				com.netdoers.beans.Notification  notification = new com.netdoers.beans.Notification();
				notification.setId(cr.getString(columnId));
				notification.setNotifId(cr.getString(columnNotifId));
				notification.setNotifFromUser(cr.getString(columnNotifFromUser));
				notification.setNotifFromUserImage(cr.getString(columnNotifFromUserImage));
				notification.setNotifWhat(cr.getString(columnNotifWhat));
//				notification.setNotifTo(cr.getString(columnNotifTo));
//				notification.setNotifPostId(cr.getString(columnNotifPostId));
				notification.setNotifPage(cr.getString(columnNotifPage));
				notification.setNotifReadStatus(cr.getString(columnNotifReadStatus));
				mNotification.add(notification);
			}
			setAdapter();
		}
	}
	
	private void setAdapter(){
		mAdapter = new NotificationAdapter(NotificationActivity.this, mNotification);
		mListView.setAdapter(mAdapter);
		mPullToRefreshLayout.setRefreshComplete();
	}
	
	private void volleySeen(String mPostId, final String mNotificationId) {
		final String TAG = "volleySeen";
		NetworkVolley nVolley = new NetworkVolley();
		JSONObject mJSONObject = RequestBuilder.getPostNotifSeenData(mPostId, mNotificationId);
		Log.i(TAG, AppConstants.URLS.URL_POST_NOTI_SEEN);
		Log.i(TAG, mJSONObject.toString());
		VolleyPostJsonRequest req = nVolley.new VolleyPostJsonRequest(AppConstants.URLS.URL_POST_NOTI_SEEN, mJSONObject,
				new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						try {
							VolleyLog.v("Response:%n %s", response.toString(4));
							Log.i(TAG, response.toString());
							if (response.getBoolean("success")) {
								updateNotificationStatus(mNotificationId);
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
	
	private void updateNotificationStatus(String mNotificationId){
		ContentValues values = new ContentValues();
		values.put(DBConstant.Notification_Columns.COLUMN_READ_STATUS, "1");
		getContentResolver()
				.update(DBConstant.Notification_Columns.CONTENT_URI,
						values,
						DBConstant.Notification_Columns.COLUMN_NOTIF_ID + "=?",
						new String[] { mNotificationId });
	}
}
