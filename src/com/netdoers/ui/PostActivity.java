
package com.netdoers.ui;

import it.sephiroth.android.library.widget.HListView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.netdoers.beans.GroupFilter;
import com.netdoers.tellus.R;
import com.netdoers.ui.view.GroupFilterAdapter;
import com.netdoers.ui.view.StatusMediaAdapter;
import com.netdoers.utils.AppConstants;
import com.netdoers.utils.ApplicationLoader;
import com.netdoers.utils.GalleryMedia;
import com.netdoers.utils.ImageCompression;
import com.netdoers.utils.NetworkVolley;
import com.netdoers.utils.NetworkVolley.VolleyPostJsonRequest;
import com.netdoers.utils.RequestBuilder;
import com.netdoers.utils.RestClient;
import com.netdoers.utils.Utilities;

public class PostActivity extends ActionBarActivity {

	private ActionBar mActionBar;
	private HListView mListView;
	private ImageView mStatusMedia, mStatusVideo;
	private EditText  mStatusTxt;
	private AutoCompleteTextView mStatusInGroup;
	private TextView mTitle;
	private Button mStatusPost;
	
	private List<String> mStrGroup, mStrGroupId;
	private ArrayList<GroupFilter> mGroupFilter;
	private CustomArrayAdapter<String> mGroupListAdapter;
	
	private ProgressDialog mProgressDialog;
	
	private static final int IMPORT_PICTURE = 10001;
	private static final int IMPORT_VIDEO = 10002;
	private static final String TAG = PostActivity.class.getSimpleName();
	
	private Uri mUri;
	private ArrayList<Bitmap> mBitmap;
	private ArrayList<String> mPath;
	private ArrayList<String> mPathIsVideo;
	private StatusMediaAdapter mAdapter = null;
	private GroupFilterAdapter mGroupFilterAdapter = null;
	private String mCompressedPath;
	
	private String mGroupId;
	private String mGroupName;
	private String mPostId;
	private boolean isFromGroup = false;
	private boolean isValid = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_post);
		initUi();
		getIntentData();
		setCustomActionBar();
		setEventListeners();
		initObjects();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		unregisterReceiver(broadcastPostServiceReceiver);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		registerReceiver(broadcastPostServiceReceiver, new IntentFilter(AppConstants.BROADCAST_ACTION.POST_SERVICE_MEDIA));
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
	
	private void setCustomActionBar(){
		mActionBar = getSupportActionBar();
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		mActionBar.setDisplayShowTitleEnabled(false);
		LayoutInflater mInflater = LayoutInflater.from(this);
		View mCustomView = mInflater.inflate(R.layout.actionbar_post, null);
		mActionBar.setCustomView(mCustomView);
		mActionBar.setDisplayShowCustomEnabled(true);
		mStatusPost = (Button)mCustomView.findViewById(R.id.actionbar_post_submit);
		mTitle = (TextView)mCustomView.findViewById(R.id.actionbar_post_user_id);
		if(isFromGroup){
			mTitle.setText(mGroupName);
			mStatusInGroup.setVisibility(View.GONE);
			RelativeLayout.LayoutParams layoutParams = 
				    (RelativeLayout.LayoutParams)mStatusTxt.getLayoutParams();
				layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
				mStatusTxt.setLayoutParams(layoutParams);
		}else{
			setAutoSuggest();
//			setAutoSuggestFilter();
		}
	}
	
	private void initUi(){
		mStatusInGroup = (AutoCompleteTextView)findViewById(R.id.activity_post_in_group);
		mStatusTxt = (EditText)findViewById(R.id.activity_post_text);
		mStatusMedia = (ImageView)findViewById(R.id.activity_post_add_media);
		mStatusVideo= (ImageView)findViewById(R.id.activity_post_add_video);
		mListView = (HListView)findViewById(R.id.activity_post_media_gallery);
	}
	
	private void getIntentData(){
		try{
			mGroupId = getIntent().getStringExtra(AppConstants.TAGS.GROUP_ID);
			mGroupName = getIntent().getStringExtra(AppConstants.TAGS.GROUP_NAME);
			if(!TextUtils.isEmpty(mGroupId))
				isFromGroup = true;
		}catch(Exception e){
			isFromGroup = false;
			Log.e(TAG, e.toString());
		}
	}
	private void initObjects(){
		mBitmap = new ArrayList<Bitmap>();
		mAdapter = new StatusMediaAdapter(this, mBitmap);
		mListView.setAdapter(mAdapter);
		mPath =  new ArrayList<String>();
		mPathIsVideo = new ArrayList<String>();
	}
	
	private void setEventListeners(){
		mStatusMedia.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setType("image/*");
				intent.setAction(Intent.ACTION_GET_CONTENT);
				startActivityForResult(Intent.createChooser(intent, "Select Picture"), IMPORT_PICTURE);
			}
		});
		
		mStatusVideo.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setType("video/*");
				intent.setAction(Intent.ACTION_GET_CONTENT);
				startActivityForResult(Intent.createChooser(intent, "Select Video"), IMPORT_VIDEO);				
			}
		});
		
		mStatusTxt.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			}
		});

		mStatusInGroup.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				popUpSelectGroupDialog();
			}
		});
		
		mStatusPost.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				validate();
				if(isValid){
					if(Utilities.isInternetConnected()){
						if(!TextUtils.isEmpty(mGroupId)){
							new UploadPostTask().execute();
						}else{
							Toast.makeText(PostActivity.this, "Please select group", Toast.LENGTH_SHORT).show();
						}
					}else{
						Toast.makeText(PostActivity.this, R.string.noconnection, Toast.LENGTH_SHORT).show();
					}
				}
			}
		});
	}

	private void validate(){
		if(!TextUtils.isEmpty(mStatusTxt.getText().toString())){
			isValid = true;
		}else{
			isValid = false;
			mStatusTxt.requestFocus();
			mStatusTxt.setError("Please enter post");
		}
	}
	
	private void setAutoSuggest(){
		NetworkVolley nVolley = new NetworkVolley();
		if(Utilities.isInternetConnected()){
			JSONObject mJsonObject = RequestBuilder.getPostStatusGroupIdData(mStatusInGroup.getText().toString());
			Log.i(TAG, AppConstants.URLS.URL_POST_GROUP_WHILE_POST);
			Log.i(TAG, mJsonObject.toString());
			VolleyPostJsonRequest req = nVolley.new VolleyPostJsonRequest(AppConstants.URLS.URL_POST_GROUP_WHILE_POST, mJsonObject,
				       new Listener<JSONObject>() {
				           @Override
				           public void onResponse(JSONObject response) {
				               try {
				                   VolleyLog.v("Response:%n %s", response.toString(4));
				                   parseGroup(response);
//				                   mGroupListAdapter = new CustomArrayAdapter<String>(PostActivity.this, mStrGroup.toArray(new String[mStrGroup.size()]));
//				                   mStatusInGroup.setAdapter(mGroupListAdapter);
				                   Log.i(TAG, response.toString());
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
		}else{
			Toast.makeText(PostActivity.this, getString(R.string.noconnection), Toast.LENGTH_SHORT).show();
		}
	}
	
	private void setAutoSuggestFilter(){
		mStatusInGroup.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				mStatusInGroup.setText(mStrGroup.get(position).toString());
				mGroupId = mStrGroupId.get(position).toString();
			}
		});
	}
	
	private BroadcastReceiver broadcastPostServiceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        	updateUI(intent);       
        }
    };
	
    private void updateUI(Intent mIntent){
    	String counter = mIntent.getStringExtra("text");
    	if(mProgressDialog!=null){
    		mProgressDialog.dismiss();
    		Toast.makeText(PostActivity.this, "Posted", Toast.LENGTH_SHORT).show();
    		finish();
    	}
    }
    
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		Bitmap bm = null;
		if (resultCode == RESULT_OK) {
			if (requestCode == IMPORT_PICTURE) {

				Uri selectedImage = data.getData();
				String[] filePathColumn = { MediaColumns.DATA };
				Cursor cursor = getContentResolver().query(selectedImage,filePathColumn, null, null, null);
				cursor.moveToFirst();
				int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
				String picturePath = cursor.getString(columnIndex);
				cursor.close();

				mUri = Utilities.getImagePath();
				try {
					Utilities.copy(new File(picturePath),new File(mUri.getPath()));
					Utilities.galleryAddPic(PostActivity.this, mUri);
				} catch (IOException e) {
					Log.e(TAG, e.toString());
				}

				BitmapFactory.Options btmapOptions = new BitmapFactory.Options();
				btmapOptions.inSampleSize = 2;
				bm = BitmapFactory.decodeFile(picturePath, btmapOptions);
				PostActivity.this.mBitmap.add(bm);
				mAdapter.notifyDataSetChanged();
				mCompressedPath = ImageCompression.compressImage(mUri.getPath());
				mPath.add(mCompressedPath);
				mPathIsVideo.add("0");
			}
		}
		
		if (requestCode == IMPORT_VIDEO) {
			if (resultCode == RESULT_OK) {
				Uri selectedImage = data.getData();
				String[] filePathColumn = { MediaColumns.DATA };
				Cursor cursor = getContentResolver().query(selectedImage,filePathColumn, null, null, null);
				cursor.moveToFirst();
				int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
				String videoPath = cursor.getString(columnIndex);
				cursor.close();
				if(videoPath.endsWith(".mp4"))
				{
					mUri = Utilities.getImagePath();
					bm = ThumbnailUtils.createVideoThumbnail(videoPath,MediaStore.Video.Thumbnails.MICRO_KIND);
					try {
						Resources r = getResources();
						Drawable[] layers = new Drawable[2];
						layers[0] = new BitmapDrawable(bm);
						layers[1] = r.getDrawable(R.drawable.play_icon);
						LayerDrawable layerDrawable = new LayerDrawable(layers);
						PostActivity.this.mBitmap.add(GalleryMedia.drawableToBitmap(GalleryMedia.geSingleDrawable(layerDrawable)));
					} catch (Exception e) {
						Log.e("Creating Thumbnail",e.toString());
					}
/*					try {
						Utilities.copy(new File(videoPath),new File(mUri.getPath()));
					} catch (Exception e) {
						Log.e("COPY VIDEO", e.toString());
					}
*/					mAdapter.notifyDataSetChanged();
					mPath.add(videoPath);
					mPathIsVideo.add("1");
				}
				else{
					Toast.makeText(PostActivity.this, "Please add mp4 videos only!", Toast.LENGTH_SHORT).show();
				}
			} else {
				Toast.makeText(PostActivity.this, "Error while importing video!", Toast.LENGTH_SHORT).show();
			}
		}

	}
	
	private class UploadPostTask extends AsyncTask<Void, Void, String>
	{
		private String errorValue;
		private boolean successValue = false;
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			mProgressDialog = new ProgressDialog(PostActivity.this);
			mProgressDialog.setMessage("Posting...");
			mProgressDialog.setCancelable(false);
			mProgressDialog.show();
		}
		@Override
		protected String doInBackground(Void... params) {
			// TODO Auto-generated method stub
			String n = null;
			try {
				JSONObject mDataToSend = RequestBuilder.getPostStatusData(mGroupId, mStatusTxt.getText().toString());
				Log.i(TAG, mDataToSend.toString());
				Log.i(TAG, AppConstants.URLS.URL_POST_STATUS);
				String s = RestClient.postJSON(AppConstants.URLS.URL_POST_STATUS, mDataToSend);
				JSONObject jsonObject = new JSONObject(s);
				successValue = jsonObject.getBoolean("success");
				if (successValue) {
					mPostId = jsonObject.getString("post_id");
				} else {
					errorValue = jsonObject.getString("errors");
				}
				Log.i(TAG, s);
			} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			return n;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			
			if (!TextUtils.isEmpty(errorValue)) {
				if (mProgressDialog != null) {
					mProgressDialog.dismiss();
				}
				Toast.makeText(PostActivity.this, errorValue,Toast.LENGTH_SHORT).show();
			}
			
			if(mPath.size() > 0 && !TextUtils.isEmpty(mPostId)){
				Intent mIntent = new Intent(PostActivity.this, PostService.class);
				mIntent.putExtra(PostService.POST_MEDIA_PATH, mPath);
				mIntent.putExtra(PostService.POST_MEDIA_PATH_IS_VIDEO, mPathIsVideo);
				mIntent.putExtra(PostService.POST_ID, mPostId);
				startService(mIntent);				
			}else{
				if (mProgressDialog != null) {
					mProgressDialog.dismiss();
					Toast.makeText(PostActivity.this, "Posted", Toast.LENGTH_SHORT).show();
		    		finish();
				}
			}
		}
	}
	
	static class CustomArrayAdapter<T> extends ArrayAdapter<T> {
		public CustomArrayAdapter(Context ctx, T[] objects) {
			super(ctx, android.R.layout.simple_list_item_1, objects);
		}

		@Override
		public TextView getView(int position, View convertView, ViewGroup parent) {
			TextView v = (TextView) super.getView(position, convertView, parent);
			v.setPadding(15, 15, 0, 15);
			return v;
		}
	}
	
	private void parseGroup(JSONObject jsonGroup) {
		mStrGroup = new ArrayList<String>();
		mStrGroupId = new ArrayList<String>();
		try {
			JSONArray mJsonArray = jsonGroup.getJSONArray("groups");
			for (int i = 0; i < mJsonArray.length(); i++) {
				JSONObject mJsonObject = mJsonArray.getJSONObject(i);
				mStrGroup.add(mJsonObject.getString("group_name"));
				mStrGroupId.add(mJsonObject.getString("group_id"));
			}
			
			mGroupFilter = new ArrayList<GroupFilter>();
			for (int i = 0; i < mJsonArray.length(); i++) {
				GroupFilter mGroupFilterObj = new GroupFilter();
				JSONObject mJsonObject = mJsonArray.getJSONObject(i);
				mGroupFilterObj.setGroupName(mJsonObject.getString("group_name"));
				mGroupFilterObj.setGroupId(mJsonObject.getString("group_id"));
				mGroupFilter.add(mGroupFilterObj);
			}
			
			if(mGroupFilter!=null && mGroupFilter.size() > 0){
				mGroupFilterAdapter = new GroupFilterAdapter(PostActivity.this , mGroupFilter);
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void popUpSelectGroupDialog() {
		final Dialog dialog = new Dialog(PostActivity.this);
		try {
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		} catch (Exception e) {
			Log.e(TAG, "popUpSelectGroupDialog : " + e.toString());
		}
		dialog.setContentView(R.layout.dialog_select_group);

		EditText mGroupSearch = (EditText) dialog.findViewById(R.id.dialog_select_group_name);
		ListView mSubscribeGroupListView = (ListView) dialog.findViewById(R.id.dialog_select_group_listview);

		if(mGroupFilterAdapter!=null)
			mSubscribeGroupListView.setAdapter(mGroupFilterAdapter);
		
		mGroupSearch.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				mGroupFilterAdapter.getFilter().filter(s.toString());
			}
		});
		
		mSubscribeGroupListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				mStatusInGroup.setText("");
				mGroupId =  view.getTag(R.id.TAG_GROUP_ID).toString();
				mStatusInGroup.setText(view.getTag(R.id.TAG_GROUP_NAME).toString());
				dialog.dismiss();
			}
		});
		dialog.show();
	}
}
