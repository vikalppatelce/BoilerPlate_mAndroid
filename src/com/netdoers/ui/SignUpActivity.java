package com.netdoers.ui;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import org.json.JSONException;
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
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.netdoers.beans.FacebookFriend;
import com.netdoers.tellus.BuildConfig;
import com.netdoers.tellus.R;
import com.netdoers.ui.view.CircleImageView;
import com.netdoers.ui.view.Switch;
import com.netdoers.utils.AppConstants;
import com.netdoers.utils.ApplicationLoader;
import com.netdoers.utils.ImageCompression;
import com.netdoers.utils.NetworkVolley;
import com.netdoers.utils.NetworkVolley.VolleyGetJsonRequest;
import com.netdoers.utils.NetworkVolley.VolleyPostJsonRequest;
import com.netdoers.utils.RequestBuilder;
import com.netdoers.utils.RestClient;
import com.netdoers.utils.Utilities;

public class SignUpActivity extends ActionBarActivity {

	private CircleImageView mCircleImageView;
	private ImageView mImageView;
	private Switch mGender;
	private EditText mUserName, mPassword, mConfirmPassword, mEmail, mMobile;
	private AutoCompleteTextView mCountry, mState, mCity;
	private Button mSubmit;

	private static final String TAG = SignUpActivity.class.getSimpleName();
	private static final int IMPORT_PICTURE = 10001;

	private String mPicturePath, mCompressPath, mProfilePicPath;
	private Uri mUri;
	private List<String> mStrCountry, mStrState, mStrCity, mStrCountryId,
			mStrStateId, mStrCityId;
	private CustomArrayAdapter<String> mCountryAdapter, mCityAdapter,
			mStateAdapter;
	private String mSelectedCountry, mSelectedState, mSelectedCity,
			mSelectedCountryId, mSelectedStateId, mSelectedCityId;
	private String mStrGender = "male";
	private boolean isValid = false;

	private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
			+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	private static final String USERNAME_PATTERN = "^[a-zA-Z0-9]{3,15}$";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_up);
		initUi();
		setEventListeners();
		setAutoSuggest();
		setAutoSuggestFilter();
		setFacebookData();
		// setForceToSelectFromAdapter();
	}

	private void initUi() {
		mCircleImageView = (CircleImageView) findViewById(R.id.activity_sign_up_user_picture);
		mImageView = (ImageView) findViewById(R.id.activity_sign_up_user_picture_upload);
		mUserName = (EditText) findViewById(R.id.activity_sign_up_username);
		mConfirmPassword = (EditText) findViewById(R.id.activity_sign_up_password_confirmation);
		mPassword = (EditText) findViewById(R.id.activity_sign_up_password);
		mGender = (Switch) findViewById(R.id.activity_sign_up_gender);
		mEmail = (EditText) findViewById(R.id.activity_sign_up_email);
		mMobile = (EditText) findViewById(R.id.activity_sign_up_phone);
		mCountry = (AutoCompleteTextView) findViewById(R.id.activity_sign_up_country);
		mState = (AutoCompleteTextView) findViewById(R.id.activity_sign_up_state);
		mCity = (AutoCompleteTextView) findViewById(R.id.activity_sign_up_city);
		mSubmit = (Button) findViewById(R.id.activity_sign_up_submit);
	}

	private void setEventListeners() {
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

		mSubmit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onSubmit();
			}
		});

		mGender.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if (mGender.isChecked()) {
					mStrGender = "female";
				} else {
					mStrGender = "male";
				}

			}
		});
	}

	private void onUploadPicture() {
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(Intent.createChooser(intent, "Select Picture"),
				IMPORT_PICTURE);
	}

	private void onSubmit() {
		validate();
		if (isValid) {

			if (Utilities.isInternetConnected()) {
				new RegistrationTask(SignUpActivity.this).execute();
			} else {
				Toast.makeText(SignUpActivity.this,
						getString(R.string.noconnection), Toast.LENGTH_SHORT)
						.show();
			}
		}
	}

	private void validate() {

		if (!TextUtils.isEmpty(mMobile.getText().toString())) {
			isValid = true;
		} else {
			isValid = false;
			mMobile.setError("Please enter Phone");
			mMobile.requestFocus();
			return;
		}

		if (!TextUtils.isEmpty(mSelectedCity)) {
			isValid = true;
		} else {
			isValid = false;
			mCity.setError("Please select city from autosuggest only");
			mCity.requestFocus();
			return;
		}

		if (!TextUtils.isEmpty(mSelectedState)) {
			isValid = true;
		} else {
			isValid = false;
			mState.setError("Please select state from autosuggest only");
			mState.requestFocus();
			return;
		}

		if (!TextUtils.isEmpty(mSelectedCountry)) {
			isValid = true;
		} else {
			isValid = false;
			mCountry.setError("Please select country from autosuggest only");
			mCountry.requestFocus();
			return;
		}

		if (!TextUtils.isEmpty(mEmail.getText().toString())) {
			isValid = true;
		} else {
			isValid = false;
			mEmail.setError("Please enter Email");
			mEmail.requestFocus();
			return;
		}

		if (!TextUtils.isEmpty(mConfirmPassword.getText().toString())) {
			isValid = true;
		} else {
			isValid = false;
			mConfirmPassword.setError("Please enter Confirm Password");
			mConfirmPassword.requestFocus();
			return;
		}

		if (!TextUtils.isEmpty(mPassword.getText().toString())) {
			isValid = true;
		} else {
			isValid = false;
			mPassword.setError("Please enter password");
			mPassword.requestFocus();
			return;
		}

		if (!TextUtils.isEmpty(mUserName.getText().toString())) {
			isValid = true;
		} else {
			isValid = false;
			mUserName.setError("Please enter username");
			mUserName.requestFocus();
			return;
		}

		if (!TextUtils.isEmpty(mUserName.getText().toString())) {
			isValid = Pattern.compile(USERNAME_PATTERN)
					.matcher(mUserName.getText().toString()).matches();
			if (!isValid){
				mUserName.setError("Please enter valid username with a-zA-Z0-9");
				mUserName.requestFocus();
				return;
			}
		}

		if (!TextUtils.isEmpty(mMobile.getText().toString())) {
			isValid = Patterns.PHONE.matcher(mMobile.getText().toString())
					.matches();
			if (!isValid){
				mMobile.setError("Please enter valid Phone");
				mMobile.requestFocus();
				return;
			}
		}

		if (!TextUtils.isEmpty(mMobile.getText().toString())
				&& mMobile.getText().toString().length() != 9) {
			isValid = Patterns.PHONE.matcher(mMobile.getText().toString())
					.matches();
			if (!isValid){
				mMobile.setError("Please enter valid 10 digit Phone");
				mMobile.requestFocus();
				return;
			}
		}else{
			mMobile.setError("Please enter valid 10 digit Phone");
			mMobile.requestFocus();
			return;
		}

		if (!TextUtils.isEmpty(mEmail.getText().toString())) {
			isValid = Patterns.EMAIL_ADDRESS.matcher(
					mEmail.getText().toString()).matches();
			if (!isValid){
				mEmail.setError("Please enter valid email");
				mEmail.requestFocus();
				return;
			}
		}

		if (!TextUtils.isEmpty(mEmail.getText().toString())) {
			isValid = Pattern.compile(EMAIL_PATTERN)
					.matcher(mEmail.getText().toString()).matches();
			if (!isValid){
				mEmail.setError("Please enter valid email");
				mEmail.requestFocus();
				return;
			}
		}

		if (mConfirmPassword.getText().toString()
				.equals(mPassword.getText().toString())) {
			isValid = true;
		} else {
			isValid = false;
			mConfirmPassword.setError("Please enter same password");
			mConfirmPassword.requestFocus();
			return;
		}
	}

	private void setAutoSuggest() {
		NetworkVolley nVolley = new NetworkVolley();
		if (Utilities.isInternetConnected()) {
			VolleyGetJsonRequest req = nVolley.new VolleyGetJsonRequest(
					AppConstants.URLS.URL_GET_COUNTRY, null,
					new Listener<JSONObject>() {
						@Override
						public void onResponse(JSONObject response) {
							try {
								VolleyLog.v("Response:%n %s",
										response.toString(4));
								parseCountry(response);
								mCountryAdapter = new CustomArrayAdapter<String>(
										SignUpActivity.this,
										mStrCountry
												.toArray(new String[mStrCountry
														.size()]));
								mCountry.setAdapter(mCountryAdapter);
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
			ApplicationLoader.getInstance().addToRequestQueue(req, TAG);
		} else {
			Toast.makeText(SignUpActivity.this,
					getString(R.string.noconnection), Toast.LENGTH_SHORT)
					.show();
		}
	}

	private void setFacebookData() {
		try {
			mEmail.setText(ApplicationLoader.getPreferences()
					.getFacebookEmail());
			mUserName.setText(ApplicationLoader.getPreferences()
					.getFacebookUserName());
			mCity.setText(ApplicationLoader.getPreferences()
					.getFacebookUserLocation());
		} catch (Exception e) {
			Log.i(TAG, " setFacebookData " + e.toString());
		}
	}

	private void setAutoSuggestFilter() {
		mCountry.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				mSelectedCountry = mCountryAdapter.getItem(position).toString();
				mSelectedCountryId = mStrCountryId.get(
						mStrCountry.indexOf(mSelectedCountry)).toString();
				mState.setText("");
				mSelectedState = null;
				mSelectedStateId = null;
				mCity.setText("");
				mSelectedCity = null;
				mSelectedCityId = null;
				getFilterState();
			}
		});

		mState.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				mSelectedState = mStateAdapter.getItem(position).toString();
				mSelectedStateId = mStrStateId.get(
						mStrState.indexOf(mSelectedState)).toString();
				mCity.setText("");
				mSelectedCity = null;
				mSelectedCityId = null;
				getFilterCity();
			}
		});

		mCity.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				mSelectedCity = mCityAdapter.getItem(position).toString();
				mSelectedCityId = mStrCityId.get(
						mStrCity.indexOf(mSelectedCity)).toString();
			}
		});
	}

	private void getFilterState() {
		if (Utilities.isInternetConnected()) {
			NetworkVolley nVolley = new NetworkVolley();
			JSONObject dataJSON = RequestBuilder
					.getPostCountryData(mSelectedCountryId);
			Log.i(TAG, dataJSON.toString());
			VolleyPostJsonRequest req = nVolley.new VolleyPostJsonRequest(
					AppConstants.URLS.URL_POST_STATE, dataJSON,
					new Listener<JSONObject>() {
						@Override
						public void onResponse(JSONObject response) {
							try {
								VolleyLog.v("Response:%n %s",
										response.toString(4));
								parseState(response);
								mStateAdapter = new CustomArrayAdapter<String>(
										SignUpActivity.this,
										mStrState.toArray(new String[mStrState
												.size()]));
								mState.setAdapter(mStateAdapter);
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
			ApplicationLoader.getInstance().addToRequestQueue(req, TAG);
		} else {
			Toast.makeText(SignUpActivity.this,
					getString(R.string.noconnection), Toast.LENGTH_SHORT)
					.show();
		}
	}

	private void getFilterCity() {
		if (Utilities.isInternetConnected()) {
			NetworkVolley nVolley = new NetworkVolley();
			JSONObject dataJSON = RequestBuilder
					.getPostStateData(mSelectedStateId);
			Log.i(TAG, dataJSON.toString());
			VolleyPostJsonRequest req = nVolley.new VolleyPostJsonRequest(
					AppConstants.URLS.URL_POST_CITY, dataJSON,
					new Listener<JSONObject>() {
						@Override
						public void onResponse(JSONObject response) {
							try {
								VolleyLog.v("Response:%n %s",
										response.toString(4));
								parseCity(response);
								mCityAdapter = new CustomArrayAdapter<String>(
										SignUpActivity.this,
										mStrCity.toArray(new String[mStrCity
												.size()]));
								mCity.setAdapter(mCityAdapter);
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
			ApplicationLoader.getInstance().addToRequestQueue(req, TAG);
		} else {
			Toast.makeText(SignUpActivity.this,
					getString(R.string.noconnection), Toast.LENGTH_SHORT)
					.show();
		}
	}

	private void setForceToSelectFromAdapter() {
		mCountry.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				mSelectedCountry = null;
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});

		mState.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				mSelectedState = null;
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});

		mCity.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				mSelectedCity = null;
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
	}

	private void parseCountry(JSONObject jsonCountry) {
		mStrCountry = new ArrayList<String>();
		mStrCountryId = new ArrayList<String>();

		Iterator<?> keys = jsonCountry.keys();

		while (keys.hasNext()) {
			String key = (String) keys.next();
			try {
				mStrCountry.add(jsonCountry.get(key).toString());
				mStrCountryId.add(key);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	private void parseState(JSONObject jsonState) {
		mStrState = new ArrayList<String>();
		mStrStateId = new ArrayList<String>();

		Iterator<?> keys = jsonState.keys();

		while (keys.hasNext()) {
			String key = (String) keys.next();
			try {
				mStrState.add(jsonState.get(key).toString());
				mStrStateId.add(key);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	private void parseCity(JSONObject jsonCity) {
		mStrCity = new ArrayList<String>();
		mStrCityId = new ArrayList<String>();
		Iterator<?> keys = jsonCity.keys();

		while (keys.hasNext()) {
			String key = (String) keys.next();
			try {
				mStrCity.add(jsonCity.get(key).toString());
				mStrCityId.add(key);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	static class CustomArrayAdapter<T> extends ArrayAdapter<T> {
		public CustomArrayAdapter(Context ctx, T[] objects) {
			super(ctx, android.R.layout.simple_list_item_1, objects);
		}

		@Override
		public TextView getView(int position, View convertView, ViewGroup parent) {
			TextView v = (TextView) super
					.getView(position, convertView, parent);
			v.setPadding(15, 15, 0, 15);
			return v;
		}

		/*
		 * @Override public View getDropDownView(int position, View convertView,
		 * ViewGroup parent) { View view = super.getView(position, convertView,
		 * parent); TextView text = (TextView)
		 * view.findViewById(android.R.id.text1); text.setPadding(15, 15, 0,
		 * 15); return view; }
		 */
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (requestCode == IMPORT_PICTURE) {
				Uri selectedImage = data.getData();
				String[] filePathColumn = { MediaColumns.DATA };
				Cursor cursor = getContentResolver().query(selectedImage,
						filePathColumn, null, null, null);
				cursor.moveToFirst();
				int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
				mPicturePath = cursor.getString(columnIndex);
				cursor.close();

				mUri = Utilities.getImagePath();
				try {
					Utilities.copy(new File(mPicturePath),
							new File(mUri.getPath()));
					Utilities.galleryAddPic(SignUpActivity.this, mUri);
					mProfilePicPath = mPicturePath.toString().substring(
							mPicturePath.toString().lastIndexOf("/") + 1);
					mCompressPath = ImageCompression
							.compressImage(mPicturePath);
					;
					mCircleImageView.setImageBitmap(Utilities
							.getBitmapFromUri(Uri.parse("file:///"
									+ mPicturePath)));
				} catch (IOException e) {
					Log.e(TAG, e.toString());
				}
			}
		}
	}

	public class RegistrationTask extends AsyncTask<Void, Void, Void> {
		Context context;
		ProgressDialog progressDialog;
		String errorValue;
		boolean successValue = false;

		public RegistrationTask(Context context) {
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
			try {
				JSONObject dataJSON = RequestBuilder.getPostSignUpData(
						mUserName.getText().toString(), mPassword.getText()
								.toString(), mConfirmPassword.getText()
								.toString(), mEmail.getText().toString(),
						Utilities.getDeviceId(), Utilities.getDeviceName(),
						Utilities.getSDKVersion(), Utilities
								.getApplicationVersion(), mStrGender,
						mSelectedCountryId, mSelectedStateId, mSelectedCityId,
						mMobile.getText().toString());
				Log.i(TAG, dataJSON.toString());
				String str = RestClient.postJSON(
						AppConstants.URLS.URL_POST_REGISTRATION, dataJSON);
				JSONObject object = new JSONObject(str);
				if ((successValue = object.getBoolean("success"))) {
					ApplicationLoader.getPreferences().setApiKey(
							object.getString("api_key"));
					if (ApplicationLoader.getPreferences()
							.getFacebookUserFriends() != null) {
						try {
							Gson gson = new Gson();
							Type type = new TypeToken<ArrayList<FacebookFriend>>() {
							}.getType();
							ArrayList<FacebookFriend> mFacebookFriends = gson
									.fromJson(ApplicationLoader
											.getPreferences()
											.getFacebookUserFriends(), type);
							volleyFacebookFriend(RequestBuilder
									.getPostFacebookUserFriendData(mFacebookFriends));
						} catch (Exception e) {
							Log.e(TAG, e.toString());
						}
					}

				} else {
					errorValue = object.getString("errors");
				}
			} catch (Exception e) {
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
				if (!TextUtils.isEmpty(mCompressPath)) {
					new RegistrationUploadTask(SignUpActivity.this).execute();
				} else {
					Intent i = new Intent(SignUpActivity.this,
							SignInActivity.class);
					startActivity(i);
					finish();
				}
			} else {
				 JSONObject mJSONObject;
				try {
					mJSONObject = new JSONObject(errorValue);
					try{
						String usernameError = mJSONObject.getString("username").replace("[", "").replace("]", "").replace("\"", "");
						 if(usernameError!=null)
							 Toast.makeText(SignUpActivity.this, usernameError, Toast.LENGTH_SHORT).show();
					}catch(Exception e){
						Log.i(TAG, e.toString());
					}
					try{
						String usernameError = mJSONObject.getString("email").replace("[", "").replace("]", "").replace("\"", "");
						 if(usernameError!=null)
							 Toast.makeText(SignUpActivity.this, usernameError, Toast.LENGTH_SHORT).show();
					}catch(Exception e){
						Log.i(TAG, e.toString());
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					Toast.makeText(SignUpActivity.this, errorValue,
							Toast.LENGTH_SHORT).show();
					e.printStackTrace();
				}
			}

		}
	}

	public class RegistrationUploadTask extends AsyncTask<Void, Void, Void> {
		Context context;
		ProgressDialog progressDialog;
		String errorValue;
		boolean successValue = false;

		public RegistrationUploadTask(Context context) {
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
			try {
				if (!TextUtils.isEmpty(mCompressPath)) {
					String typ = "profile_pic";
					File f = new File(mCompressPath);
					String s = RestClient.uploadFile(typ, f,
							AppConstants.URLS.URL_POST_PROFILE_PIC);
					JSONObject object = new JSONObject(s);
					if ((successValue = object.getBoolean("status"))) {
					} else {
						errorValue = object.getString("errors");
					}
					Log.i("MediaUpload", s.toString());
				}
			} catch (Exception e) {
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
				Intent i = new Intent(SignUpActivity.this, SignInActivity.class);
				ApplicationLoader.getPreferences().setApiKey("");
				startActivity(i);
				finish();
			} else {
				Toast.makeText(SignUpActivity.this, errorValue,
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	private void volleyFacebookFriend(JSONObject mJSONObject) {
		final String TAG = "volleyFacebookFriend";
		NetworkVolley nVolley = new NetworkVolley();
		Log.i(TAG, AppConstants.URLS.URL_POST_FB_FRIENDS);
		Log.i(TAG, mJSONObject.toString());
		VolleyPostJsonRequest req = nVolley.new VolleyPostJsonRequest(
				AppConstants.URLS.URL_POST_FB_FRIENDS, mJSONObject,
				new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						try {
							VolleyLog.v("Response:%n %s", response.toString(4));
							Log.i(TAG, response.toString());
							if (response.getBoolean("success")) {
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
}
