package com.netdoers.ui;

import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.netdoers.tellus.R;
import com.netdoers.utils.AppConstants;
import com.netdoers.utils.ApplicationLoader;
import com.netdoers.utils.RequestBuilder;
import com.netdoers.utils.RestClient;
import com.netdoers.utils.Utilities;

public class SignInActivity extends ActionBarActivity {

	private EditText mUserName, mPassword;
	private Button mLoginButton;
	private boolean isValid = false;

	private static String TAG = SignInActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_in);
		initUi();
		setEventListeners();
		setFacebookData();
	}

	private void initUi() {
		mUserName = (EditText) findViewById(R.id.activity_sign_in_username);
		mPassword = (EditText) findViewById(R.id.activity_sign_in_password);
		mLoginButton = (Button) findViewById(R.id.activity_sign_in_submit);
	}

	private void setEventListeners() {
		mLoginButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onLogin();
			}
		});
	}

	private void setFacebookData() {
		try {
			mUserName.setText(ApplicationLoader.getPreferences()
					.getFacebookEmail());
			Toast.makeText(SignInActivity.this, "Already signed up with Facebook. Use your Tell Us credentials to Login.", Toast.LENGTH_LONG).show();
		} catch (Exception e) {
			Log.i(TAG, " " + e.toString());
		}
	}

	private void onLogin() {
		validate();
		if (isValid) {
			new LoginTask(SignInActivity.this).execute();
		}
	}

	private void validate() {
		if (!TextUtils.isEmpty(mPassword.getText().toString())) {
			isValid = true;
		} else {
			isValid = false;
			mPassword.requestFocus();
			mPassword.setError("Please enter password");
		}

		if (!TextUtils.isEmpty(mUserName.getText().toString())) {
			isValid = true;
		} else {
			isValid = false;
			mUserName.requestFocus();
			mUserName.setError("Please enter username");
		}
	}

	public class LoginTask extends AsyncTask<Void, Void, Void> {
		Context context;
		ProgressDialog progressDialog;
		String errorValue;
		boolean successValue = false;

		public LoginTask(Context context) {
			this.context = context;
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			progressDialog = new ProgressDialog(context);
			progressDialog.setMessage("Logging in...");
			progressDialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			try {
				JSONObject dataJSON = RequestBuilder.getPostSignInData(
						mUserName.getText().toString(), mPassword.getText()
								.toString());
				Log.i(TAG, dataJSON.toString());
				String str = RestClient.postJSON(
						AppConstants.URLS.URL_POST_SIGN_IN, dataJSON);
				JSONObject object = new JSONObject(str);
				if ((successValue = object.getBoolean("success"))) {
					ApplicationLoader.getPreferences().setApiKey(
							object.getString("api_key"));
					ApplicationLoader.getPreferences().setUserName(
							object.getString("username"));
					ApplicationLoader.getPreferences().setUserNumber(
							object.getString("mobile_no"));
					ApplicationLoader.getPreferences().setUserGender(
							object.getString("gender"));
					ApplicationLoader.getPreferences().setUserCityId(
							object.getString("city_id"));
					ApplicationLoader.getPreferences().setUserCity(
							object.getString("city_name"));
					ApplicationLoader.getPreferences().setUserStateId(
							object.getString("state_id"));
					ApplicationLoader.getPreferences().setUserState(
							object.getString("state_name"));
					ApplicationLoader.getPreferences().setUserCountryId(
							object.getString("country_id"));
					ApplicationLoader.getPreferences().setUserCountry(
							object.getString("country_name"));
					ApplicationLoader.postInitApplication();
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
				Intent i = new Intent(SignInActivity.this, MotherActivity.class);
				// Intent i = new Intent(SignInActivity.this,
				// GroupActivity.class);
				startActivity(i);
				finish();
			} else {
				Toast.makeText(SignInActivity.this, errorValue,
						Toast.LENGTH_SHORT).show();
			}

		}
	}

}
