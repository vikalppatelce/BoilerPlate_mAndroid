package com.netdoers.ui;

import java.io.File;
import java.util.ArrayList;

import org.json.JSONObject;

import android.app.IntentService;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.netdoers.utils.AppConstants;
import com.netdoers.utils.RestClient;
import com.netdoers.utils.Utilities;

public class PostService extends IntentService {

	private static final String TAG = PostService.class.getSimpleName();
	
	public static final String  POST_MEDIA_PATH = "mPath";
	public static final String  POST_MEDIA_PATH_IS_VIDEO = "mPathIsVideo";
	public static final String  POST_ID = "mPostId";
	
	private ArrayList<String> mPath;
	private ArrayList<String> mPathIsVideo;
	private String mPostId;
	private Intent broadCastIntent;
	

	public PostService() {
		super(TAG);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub
		mPath = intent.getStringArrayListExtra(POST_MEDIA_PATH);
		mPathIsVideo = intent.getStringArrayListExtra(POST_MEDIA_PATH_IS_VIDEO);
		mPostId = intent.getStringExtra(POST_ID);
		Log.i(TAG, "PostService");
		onStartService();
	}

	private void onStartService() {
		if (Utilities.isInternetConnected()) {
			if(mPath.size() > 0)
				new UploadMediaTask().execute();
		}
	}
	
	private void DisplayLoggingInfo(String message) {
    	Log.i(TAG, "entered DisplayLoggingInfo");
    	broadCastIntent = new Intent(AppConstants.BROADCAST_ACTION.POST_SERVICE_MEDIA);
    	broadCastIntent.putExtra("text", message);
    	sendBroadcast(broadCastIntent);
    }                                                     
	
	private class UploadMediaTask extends AsyncTask<Void, Void, String>
	{
		private String errorValue;
		private boolean successValue;
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
		}
		@Override
		protected String doInBackground(Void... params) {
			// TODO Auto-generated method stub
			String n = null;
			for (int i = 0; i < mPath.size(); i++) {
				try {
					String type = "file_upload";
					String path = mPath.get(i).toString();
					String isVideo = mPathIsVideo.get(i).toString();
					File f = new File(path);
					String s = RestClient.uploadMediaFile(type, mPostId, f, AppConstants.URLS.URL_POST_MEDIA_UPLOAD);
					Log.i(TAG," : "+ path + " : "+  s);
					JSONObject jsonObject = new JSONObject(s);
					successValue = jsonObject.getBoolean("status");
					if(!successValue){
						Log.i(TAG, path +" : "+String.valueOf(successValue));
						Log.i(TAG, errorValue);
					}
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}	
			return n;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			DisplayLoggingInfo("success");
		}
	}
}
