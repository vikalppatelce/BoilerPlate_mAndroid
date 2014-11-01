package com.netdoers.ui;

import java.io.File;
import java.io.IOException;
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
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore.MediaColumns;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.netdoers.beans.Group;
import com.netdoers.tellus.BuildConfig;
import com.netdoers.tellus.R;
import com.netdoers.ui.view.CircleImageView;
import com.netdoers.ui.view.GroupAdapter;
import com.netdoers.ui.view.GroupReportAdapter;
import com.netdoers.ui.view.ScrollableListView;
import com.netdoers.utils.AppConstants;
import com.netdoers.utils.ApplicationLoader;
import com.netdoers.utils.ImageCompression;
import com.netdoers.utils.NetworkVolley;
import com.netdoers.utils.NetworkVolley.VolleyPostJsonRequest;
import com.netdoers.utils.RequestBuilder;
import com.netdoers.utils.RestClient;
import com.netdoers.utils.Utilities;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class ProfileActivity extends ActionBarActivity implements OnRefreshListener{
	
	private CircleImageView mCircleImageView;
	private ImageView mImageView;
	private TextView mUserName, mUserCity, mUserPostsCount, mUserGroupsCount;
	private ScrollableListView mScrollableListView;
	
	private static final int IMPORT_PICTURE = 10001;
	private static final String TAG = ProfileActivity.class.getSimpleName();
	
	private PullToRefreshLayout mPullToRefreshLayout;
	
	private ImageLoader imageLoader;
	private DisplayImageOptions options;
	
	private ArrayList<Group> mGroup;
	
	private GroupReportAdapter mAdapter;

	private String mPicturePath, mCompressPath, mProfilePicPath;
	private Uri mUri;
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);
		initUi();
		setActionBar();
		setUniversalImageLoader();
		setEventListeners();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		initObjects();
		setProfileData();
		displayProfileImage();
		getLoadMoreDataVolley();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_profile, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		case R.id.action_profile_update:
			Intent mIntent = new Intent(ProfileActivity.this, ProfileUpdateActivity.class);
			startActivity(mIntent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	private void initObjects(){
		mGroup = new ArrayList<Group>();
	}
	
	private void initUi(){
		mCircleImageView = (CircleImageView)findViewById(R.id.activity_profile_user_picture);
		mImageView = (ImageView)findViewById(R.id.activity_profile_user_update_picture);
		mUserName = (TextView)findViewById(R.id.activity_profile_user_id);
		mUserCity = (TextView)findViewById(R.id.activity_profile_user_city);
		mUserPostsCount = (TextView)findViewById(R.id.activity_profile_user_post_count);
		mUserGroupsCount = (TextView)findViewById(R.id.activity_profile_user_groups_count);
		mScrollableListView = (ScrollableListView) findViewById(R.id.activity_profile_listview);
		mScrollableListView.setExpanded(true);
		
		mPullToRefreshLayout = (PullToRefreshLayout)findViewById(R.id.fragment_swipe_refresh_layout);
		ActionBarPullToRefresh.from(this)
        .allChildrenArePullable()
        .listener(this)
        .setup(mPullToRefreshLayout);
	}
	
	private void setActionBar(){
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setTitle("Profile");
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
	
	private void setProfileData(){
		mUserName.setText(ApplicationLoader.getPreferences().getUserName());
		mUserCity.setText(ApplicationLoader.getPreferences().getUserCity());
		mUserPostsCount.setText(ApplicationLoader.getPreferences().getProfilePostCount());
		mUserGroupsCount.setText(ApplicationLoader.getPreferences().getProfileGroupCount());
		Utilities.logUserInfo();
	}
	
	private void setEventListeners(){
		mImageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onUploadPicture();
			}
		});
		
		mCircleImageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onUploadPicture();
			}
		});
		
		mScrollableListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				String mGroupId = view.getTag(R.id.TAG_GROUP_ID).toString();
				String mGroupName = view.getTag(R.id.TAG_GROUP_NAME).toString();
				
				Intent mIntent = new Intent(ProfileActivity.this, GroupManagementActivity.class);
				mIntent.putExtra(AppConstants.TAGS.GROUP_ID, mGroupId);
				mIntent.putExtra(AppConstants.TAGS.GROUP_NAME, mGroupName);
				startActivity(mIntent);
			}
		});
	}
	
	private void displayProfileImage(){
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				imageLoader.displayImage(ApplicationLoader.getPreferences().getProfilePicPath(), mCircleImageView, options);
			}
		});
	}
	
	private void onUploadPicture(){
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(Intent.createChooser(intent, "Select Picture"), IMPORT_PICTURE);
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
			if (resultCode == RESULT_OK) {
				if (requestCode == IMPORT_PICTURE) {
					Uri selectedImage = data.getData();
					String[] filePathColumn = { MediaColumns.DATA };
					Cursor cursor = getContentResolver().query(selectedImage,filePathColumn, null, null, null);
					cursor.moveToFirst();
					int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
					mPicturePath = cursor.getString(columnIndex);
					cursor.close();
					
					mUri = Utilities.getImagePath();
					try {
						Utilities.copy(new File(mPicturePath), new File(mUri.getPath()));
						Utilities.galleryAddPic(ProfileActivity.this, mUri);
						mProfilePicPath = mPicturePath.toString().substring(mPicturePath.toString().lastIndexOf("/") + 1);
						mCompressPath = ImageCompression.compressImage(mPicturePath);;
						mCircleImageView.setImageBitmap(Utilities.getBitmapFromUri(Uri.parse("file:///"+mPicturePath)));
						new RegistrationUploadTask(ProfileActivity.this).execute();
						}catch (IOException e){
						Log.e(TAG, e.toString());
					}
				}
			}
		}
	
	public class RegistrationUploadTask extends AsyncTask<Void, Void, Void>
	{
		Context context;
		ProgressDialog progressDialog;
		String errorValue;
		boolean successValue = false;

		public RegistrationUploadTask(Context context){
			this.context = context;
		}
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			progressDialog = new ProgressDialog(context);
			progressDialog.setMessage("Registration...");
			progressDialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			try{
				if(!TextUtils.isEmpty(mCompressPath)){
					String typ = "profile_pic";
						File f = new File(mCompressPath);
						String s = RestClient.uploadFile(typ, f, AppConstants.URLS.URL_POST_PROFILE_PIC);
						JSONObject object = new JSONObject(s);
						if((successValue = object.getBoolean("success"))){
						}else{
							errorValue = object.getString("errors");
						}
						Log.i("MediaUpload", s.toString());
				}	
			}
			catch(Exception e){
			Log.e(TAG, e.toString());	
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			
			if (progressDialog != null)
				progressDialog.dismiss();

			if (!successValue) {
				Toast.makeText(ProfileActivity.this, errorValue, Toast.LENGTH_SHORT).show();
			}else{
				volleyProfilePicture();
			}
		}
	}

	private void getLoadMoreDataVolley(){
		if(Utilities.isInternetConnected()){
			Log.i(TAG, "getLoadMoreDataVolley");
			setPullToRefreshLoader();
			NetworkVolley nVolley = new NetworkVolley();
			JSONObject mDataJSON = RequestBuilder.getPostApiData();
			Log.i(TAG, AppConstants.URLS.URL_POST_GROUP_ADMIN);
			Log.i(TAG, mDataJSON.toString());
			VolleyPostJsonRequest req = nVolley.new VolleyPostJsonRequest(AppConstants.URLS.URL_POST_GROUP_ADMIN, mDataJSON,
				       new Listener<JSONObject>() {
				           @Override
				           public void onResponse(JSONObject response) {
							try {
								VolleyLog.v("Response:%n %s",response.toString(4));
								Log.i(TAG, response.toString());
								if(response.getBoolean("success")){
									parseJSON(response);
									if (mGroup != null && mGroup.size() > 0){
										ProfileActivity.this.runOnUiThread(new Runnable() {
											@Override
											public void run() {
												// TODO Auto-generated method
												// stub
												mAdapter = new GroupReportAdapter(ProfileActivity.this, mGroup, imageLoader,options);
												mScrollableListView.setAdapter(mAdapter);
											}
										});
									}
								}
								mPullToRefreshLayout.setRefreshComplete();
							} catch (JSONException e) {
				                   e.printStackTrace();
				               }
				           }
				       }, new ErrorListener() {
				           @Override
				           public void onErrorResponse(VolleyError error) {
				               VolleyLog.e("Error: ", error.getMessage());
								mPullToRefreshLayout.setRefreshComplete();
				           }
				       });
			ApplicationLoader.getInstance().addToRequestQueue(req,TAG);
		}else{
			Toast.makeText(ProfileActivity.this, R.string.noconnection, Toast.LENGTH_SHORT).show();
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
				mGroupObj.setGroupImagePath(groupObj.getString("group_pic"));
				mGroup.add(mGroupObj);	
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void setPullToRefreshLoader(){
		mPullToRefreshLayout.setRefreshing(true);
	}
	
	@Override
	public void onRefreshStarted(View view) {
		// TODO Auto-generated method stub
	mPullToRefreshLayout.setRefreshComplete();	
	}
	
	private void volleyProfilePicture() {
		final String TAG = "volleyProfilePicture";
		NetworkVolley nVolley = new NetworkVolley();
		JSONObject mJSONObject = RequestBuilder.getPostApiData();
		Log.i(TAG, AppConstants.URLS.URL_POST_GET_PROFILE_PIC);
		Log.i(TAG, mJSONObject.toString());
		VolleyPostJsonRequest req = nVolley.new VolleyPostJsonRequest(AppConstants.URLS.URL_POST_GET_PROFILE_PIC, mJSONObject,
				new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						try {
							VolleyLog.v("Response:%n %s", response.toString(4));
							Log.i(TAG, response.toString());
							if (response.getBoolean("success")) {
								if (BuildConfig.DEBUG) {
									ApplicationLoader.getPreferences().setProfilePicPath(response.getString("profile_pic"));
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
	
}
