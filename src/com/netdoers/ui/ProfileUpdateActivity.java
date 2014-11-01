package com.netdoers.ui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
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
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.netdoers.tellus.R;
import com.netdoers.ui.view.Switch;
import com.netdoers.utils.AppConstants;
import com.netdoers.utils.ApplicationLoader;
import com.netdoers.utils.NetworkVolley;
import com.netdoers.utils.NetworkVolley.VolleyGetJsonRequest;
import com.netdoers.utils.NetworkVolley.VolleyPostJsonRequest;
import com.netdoers.utils.RequestBuilder;
import com.netdoers.utils.RestClient;
import com.netdoers.utils.Utilities;

public class ProfileUpdateActivity extends ActionBarActivity {

	private Switch mGender;
	private EditText mUserName, mMobile;
	private AutoCompleteTextView mCountry, mState, mCity;
	private Button mSubmit;
	
	private static final String TAG = ProfileUpdateActivity.class.getSimpleName();
	
	private List<String> mStrCountry, mStrState, mStrCity, mStrCountryId, mStrStateId, mStrCityId;
	private CustomArrayAdapter<String> mCountryAdapter, mCityAdapter, mStateAdapter;
	private String mSelectedCountry, mSelectedState, mSelectedCity, mSelectedCountryId, mSelectedStateId, mSelectedCityId;
	private String mStrGender = "male";
	private boolean isValid = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile_update);
		initUi();
		setActionBar();
		setProfileData();
		setEventListeners();
		setAutoSuggest();
		setAutoSuggestFilter();
//		setForceToSelectFromAdapter();
	}
	
	private void initUi(){
		mUserName = (EditText) findViewById(R.id.activity_profile_update_username);
		mGender = (Switch)findViewById(R.id.activity_profile_update_gender);
		mMobile = (EditText) findViewById(R.id.activity_profile_update_phone);
		mGender = (Switch) findViewById(R.id.activity_profile_update_gender);
		mCountry = (AutoCompleteTextView) findViewById(R.id.activity_profile_update_country);
		mState = (AutoCompleteTextView) findViewById(R.id.activity_profile_update_state);
		mCity = (AutoCompleteTextView) findViewById(R.id.activity_profile_update_city);
		mSubmit = (Button) findViewById(R.id.activity_profile_update_submit);
	}
	
	private void setEventListeners(){
		mSubmit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onSubmit();
			}
		});
		
		mGender.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if(mGender.isChecked()){
					mStrGender = "female";
				}else{
					mStrGender = "male";
				}
					
			}
		});
	}
	
	private void setActionBar() {
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		getSupportActionBar().setTitle("Update Profile");
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
	
	private void setProfileData(){
		try{
			mUserName.setText(ApplicationLoader.getPreferences().getUserName());
			mMobile.setText(ApplicationLoader.getPreferences().getUserNumber());
			mCity.setText(ApplicationLoader.getPreferences().getUserCity());
			mState.setText(ApplicationLoader.getPreferences().getUserState());
			mCountry.setText(ApplicationLoader.getPreferences().getUserCountry());
			
			mSelectedCityId = ApplicationLoader.getPreferences().getUserCityId();
			mSelectedStateId = ApplicationLoader.getPreferences().getUserStateId();
			mSelectedCountryId = ApplicationLoader.getPreferences().getUserCountryId();
			
			mSelectedCity = ApplicationLoader.getPreferences().getUserCity();
			mSelectedState = ApplicationLoader.getPreferences().getUserState();
			mSelectedCountry = ApplicationLoader.getPreferences().getUserCountry();
			
			if(ApplicationLoader.getPreferences().getUserGender().equalsIgnoreCase("female")){
				mGender.setChecked(true);
				mStrGender = "female";
			}
		}catch(Exception e){
			Log.i(TAG, " setProfileData "+e.toString());
		}
		
	}
	
	private void onSubmit(){
		validate();
		if(isValid){
			
			if(Utilities.isInternetConnected()){
				new RegistrationTask(ProfileUpdateActivity.this).execute();
			}else{
				Toast.makeText(ProfileUpdateActivity.this, getString(R.string.noconnection), Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	private void validate(){
		
		if(!TextUtils.isEmpty(mMobile.getText().toString())){
			isValid = true;
		}else{
			isValid = false;
			mMobile.setError("Please enter Phone");
			mMobile.requestFocus();
		}
		
		if(!TextUtils.isEmpty(mSelectedCity)){
			isValid = true;
		}else{
			isValid = false;
			mCity.setError("Please select city from autosuggest only");
			mCity.requestFocus();
		}
		
		if(!TextUtils.isEmpty(mSelectedState)){
			isValid = true;
		}else{
			isValid = false;
			mState.setError("Please select state from autosuggest only");
			mState.requestFocus();
		}
		
		if(!TextUtils.isEmpty(mSelectedCountry)){
			isValid = true;
		}else{
			isValid = false;
			mCountry.setError("Please select country from autosuggest only");
			mCountry.requestFocus();
		}
		
		
		if(!TextUtils.isEmpty(mUserName.getText().toString())){
			isValid = true;
		}else{
			isValid = false;
			mUserName.setError("Please enter username");
			mUserName.requestFocus();
		}
		
		
		if(!TextUtils.isEmpty(mMobile.getText().toString())){
			isValid = Patterns.PHONE.matcher(mMobile.getText().toString()).matches();
		}else{
			isValid = false;
			mMobile.setError("Please enter valid Phone");
			mMobile.requestFocus();
		}
		
	}
	
	private void setAutoSuggest(){
		NetworkVolley nVolley = new NetworkVolley();
		if(Utilities.isInternetConnected()){
			VolleyGetJsonRequest req = nVolley.new VolleyGetJsonRequest(AppConstants.URLS.URL_GET_COUNTRY, null,
				       new Listener<JSONObject>() {
				           @Override
				           public void onResponse(JSONObject response) {
				               try {
				                   VolleyLog.v("Response:%n %s", response.toString(4));
				                   parseCountry(response);
				                   mCountryAdapter = new CustomArrayAdapter<String>(ProfileUpdateActivity.this, mStrCountry.toArray(new String[mStrCountry.size()]));
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
			ApplicationLoader.getInstance().addToRequestQueue(req,TAG);
		}else{
			Toast.makeText(ProfileUpdateActivity.this, getString(R.string.noconnection), Toast.LENGTH_SHORT).show();
		}
	}
	
	private void setAutoSuggestFilter() {
		mCountry.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				mSelectedCountry = mCountryAdapter.getItem(position).toString();
				mSelectedCountryId = mStrCountryId.get(mStrCountry.indexOf(mSelectedCountry)).toString();
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
				mSelectedStateId = mStrStateId.get(mStrState.indexOf(mSelectedState)).toString();
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
				mSelectedCityId = mStrCityId.get(mStrCity.indexOf(mSelectedCity)).toString();
			}
		});
	}
	
	private void getFilterState(){
		if(Utilities.isInternetConnected()){
			NetworkVolley nVolley = new NetworkVolley();
			JSONObject  dataJSON = RequestBuilder.getPostCountryData(mSelectedCountryId);
			Log.i(TAG, dataJSON.toString());
			VolleyPostJsonRequest req = nVolley.new VolleyPostJsonRequest(AppConstants.URLS.URL_POST_STATE, dataJSON,
				       new Listener<JSONObject>() {
				           @Override
				           public void onResponse(JSONObject response) {
				               try {
				                   VolleyLog.v("Response:%n %s", response.toString(4));
				                   parseState(response);
				                   mStateAdapter = new CustomArrayAdapter<String>(ProfileUpdateActivity.this, mStrState.toArray(new String[mStrState.size()]));
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
			ApplicationLoader.getInstance().addToRequestQueue(req,TAG);
		}else{
			Toast.makeText(ProfileUpdateActivity.this, getString(R.string.noconnection), Toast.LENGTH_SHORT).show();
		}
	}
	
	private void getFilterCity(){
		if(Utilities.isInternetConnected()){
			NetworkVolley nVolley = new NetworkVolley();
			JSONObject dataJSON = RequestBuilder.getPostStateData(mSelectedStateId);
			Log.i(TAG, dataJSON.toString());
			VolleyPostJsonRequest req = nVolley.new VolleyPostJsonRequest(AppConstants.URLS.URL_POST_CITY, dataJSON,
				       new Listener<JSONObject>() {
				           @Override
				           public void onResponse(JSONObject response) {
				               try {
				                   VolleyLog.v("Response:%n %s", response.toString(4));
				                   parseCity(response);
				                   mCityAdapter = new CustomArrayAdapter<String>(ProfileUpdateActivity.this, mStrCity.toArray(new String[mStrCity.size()]));
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
			ApplicationLoader.getInstance().addToRequestQueue(req,TAG);
		}else{
			Toast.makeText(ProfileUpdateActivity.this, getString(R.string.noconnection), Toast.LENGTH_SHORT).show();
		}
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
		mStrState  = new ArrayList<String>();
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
			TextView v = (TextView) super.getView(position, convertView, parent);
			v.setPadding(15, 15, 0, 15);
			return v;
		}
	}
	

	public class RegistrationTask extends AsyncTask<Void, Void, Void>
	{
		Context context;
		ProgressDialog progressDialog;
		String errorValue;
		boolean successValue = false;

		public RegistrationTask(Context context){
			this.context = context;
		}
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			progressDialog = new ProgressDialog(context);
			progressDialog.setMessage("Updating...");
			progressDialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			try{
				JSONObject dataJSON = RequestBuilder.getPostProfileUpdateData(
						mUserName.getText().toString(), mStrGender,
						mSelectedCountryId, mSelectedStateId, mSelectedCityId,
						mMobile.getText().toString());
				Log.i(TAG, dataJSON.toString());
				Log.i(TAG, AppConstants.URLS.URL_POST_PROFILE_UPDATE);
				String str = RestClient.postJSON(AppConstants.URLS.URL_POST_PROFILE_UPDATE, dataJSON);
				JSONObject object = new JSONObject(str);
				if((successValue = object.getBoolean("success"))){
					ApplicationLoader.getPreferences().setUserName(mUserName.getText().toString());
					ApplicationLoader.getPreferences().setUserNumber(mMobile.getText().toString());
					ApplicationLoader.getPreferences().setUserGender(mStrGender);
					ApplicationLoader.getPreferences().setUserCity(mSelectedCity);
					ApplicationLoader.getPreferences().setUserState(mSelectedState);
					ApplicationLoader.getPreferences().setUserCountry(mSelectedCountry);
					ApplicationLoader.getPreferences().setUserCityId(mSelectedCountryId);
					ApplicationLoader.getPreferences().setUserStateId(mSelectedStateId);
					ApplicationLoader.getPreferences().setUserCountryId(mSelectedCountryId);
				}else{
					errorValue = object.getString("errors");
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
			
			if (successValue) {
				finish();
			} else {
				Toast.makeText(ProfileUpdateActivity.this, errorValue,
						Toast.LENGTH_SHORT).show();
			}
				
		}
	}

}
