package com.netdoers.ui;

import java.io.File;
import java.io.IOException;

import org.json.JSONObject;

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
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.netdoers.tellus.R;
import com.netdoers.ui.view.CircleImageView;
import com.netdoers.utils.AppConstants;
import com.netdoers.utils.ApplicationLoader;
import com.netdoers.utils.ImageCompression;
import com.netdoers.utils.RequestBuilder;
import com.netdoers.utils.RestClient;
import com.netdoers.utils.Utilities;

public class CreateGroupActivity extends ActionBarActivity{

//	private CircleImageView mCircleImageView;
	private ImageView mCircleImageView;
	private ImageView mImageView;
	private EditText mGroupName, mGroupDes;
	private Button mSubmit;
	
	private static final int IMPORT_PICTURE = 10001;
	private static final String TAG = CreateGroupActivity.class.getSimpleName();
	
	private String mPicturePath, mCompressPath, mProfilePicPath;
	private Uri mUri;
	private boolean isValid = false;
	private String mGroupId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_group_create);
		setActionBar();
		initUi();
		setEventListeners();
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
	private void initUi(){
		mCircleImageView = (ImageView)findViewById(R.id.activity_group_create_group_picture);
		mImageView = (ImageView)findViewById(R.id.activity_group_create_group_picture_upload);
		mGroupName = (EditText)findViewById(R.id.activity_group_create_groupname);
		mGroupDes = (EditText)findViewById(R.id.activity_group_create_groupdescription);
		mSubmit = (Button)findViewById(R.id.activity_group_create_submit);
	}
	
	private void setActionBar(){
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		getSupportActionBar().setTitle(getResources().getString(R.string.action_create_group));
	}
	
	private void setEventListeners() {
		mCircleImageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onUploadPicture();
			}
		});

		mImageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onUploadPicture();
			}
		});
		
		mSubmit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onSubmit();
			}
		});
	}
	
	private void onUploadPicture(){
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(Intent.createChooser(intent, "Select Picture"), IMPORT_PICTURE);
	}
	
	private void onSubmit(){
		validate();
		if(isValid){
			new GroupTask(CreateGroupActivity.this).execute();
		}
	}
	
	private void validate() {

		if (!TextUtils.isEmpty(mGroupDes.getText().toString())) {
			isValid = true;
		} else {
			isValid = false;
			mGroupDes.setError("Please enter group description");
			mGroupDes.requestFocus();
		}

		if (!TextUtils.isEmpty(mGroupName.getText().toString())) {
			isValid = true;
		} else {
			isValid = false;
			mGroupName.setError("Please enter group name");
			mGroupName.requestFocus();
		}
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
						Utilities.galleryAddPic(CreateGroupActivity.this, mUri);
						mProfilePicPath = mPicturePath.toString().substring(mPicturePath.toString().lastIndexOf("/") + 1);
						mCompressPath = ImageCompression.compressImage(mPicturePath);;
						mCircleImageView.setImageBitmap(Utilities.getBitmapFromUri(Uri.parse("file:///"+mPicturePath)));
						}catch (IOException e){
						Log.e(TAG, e.toString());
					}
				}
			}
		}
	
	public class GroupTask extends AsyncTask<Void, Void, Void>
	{
		Context context;
		ProgressDialog progressDialog;
		String errorValue;
		boolean successValue = false;

		public GroupTask(Context context){
			this.context = context;
		}
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			progressDialog = new ProgressDialog(context);
			progressDialog.setMessage("Creating group...");
			progressDialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			try{
				JSONObject dataJSON = RequestBuilder.getPostAddGroupData(ApplicationLoader.getPreferences().getApiKey(),
						mGroupName.getText().toString(), mGroupDes.getText()
								.toString());
				Log.i(TAG, dataJSON.toString());
				String str = RestClient.postJSON(AppConstants.URLS.URL_POST_CREATE_GROUP, dataJSON);
				JSONObject object = new JSONObject(str);
				if(!(successValue = object.getBoolean("success"))){
					errorValue = object.getString("errors");
				}else{
					mGroupId = object.getString("group_id");
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
			
			if(progressDialog!=null)
				progressDialog.dismiss();
			
			if(successValue){
				if(!TextUtils.isEmpty(mCompressPath) && !TextUtils.isEmpty(mGroupId)){
					new GroupUploadTask(CreateGroupActivity.this).execute();
				}else{
					finish();
				}
			}else{
				Toast.makeText(CreateGroupActivity.this, errorValue, Toast.LENGTH_SHORT).show();
			}
				
		}
	}
	
	public class GroupUploadTask extends AsyncTask<Void, Void, Void>
	{
		Context context;
		ProgressDialog progressDialog;
		String errorValue;
		boolean successValue = false;

		public GroupUploadTask(Context context){
			this.context = context;
		}
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			progressDialog = new ProgressDialog(context);
			progressDialog.setMessage("Creating Group...");
			progressDialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			try{
				if(!TextUtils.isEmpty(mCompressPath)){
					String typ = "grouppic";
						File f = new File(mCompressPath);
					String s = RestClient.uploadGroupMediaFile(typ, mGroupId,
							f, AppConstants.URLS.URL_POST_GROUP_UPLOAD);
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

			if (successValue) {
				finish();
			} else {
				Toast.makeText(CreateGroupActivity.this, errorValue, Toast.LENGTH_SHORT).show();
			}
		}
	}
}
