package com.netdoers.ui;

import java.util.ArrayList;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.widget.FacebookDialog;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;
import com.netdoers.beans.ContactPicker;
import com.netdoers.beans.Feed;
import com.netdoers.tellus.BuildConfig;
import com.netdoers.tellus.R;
import com.netdoers.utils.AppConstants;
import com.netdoers.utils.ApplicationLoader;
import com.netdoers.utils.DBConstant;
import com.netdoers.utils.NetworkVolley;
import com.netdoers.utils.RequestBuilder;
import com.netdoers.utils.NetworkVolley.VolleyPostJsonRequest;

public class InviteActivity extends ActionBarActivity {

	private LinearLayout mFacebookInvite;
	private LinearLayout mWhatsappInvite;
	private LinearLayout mContactInvite;
	private LinearLayout mEmailInvite;
	
	private ProgressDialog mProgressDialog;
	
	private static String TAG = InviteActivity.class.getSimpleName();
	private final static int CONTACT_INVITE = 10001;
	private final static int EMAIL_INVITE = 10002;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_invite);
		initUi();
		setActionBar();
		setEventListener();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	private void initUi() {
		mFacebookInvite = (LinearLayout) findViewById(R.id.activity_invite_facebook);
		mWhatsappInvite = (LinearLayout) findViewById(R.id.activity_invite_whatsapp);
		mContactInvite = (LinearLayout) findViewById(R.id.activity_invite_contact);
		mEmailInvite = (LinearLayout) findViewById(R.id.activity_invite_email);
	}

	private void setActionBar() {
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		getSupportActionBar().setTitle("Invite Friends");
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
	private void setEventListener() {
		mFacebookInvite.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// sendFacebookRequestDialog();
				sendFacebookMessageDialog();
			}
		});

		mWhatsappInvite.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				/*Intent mIntent = new Intent(InviteActivity.this,
						InviteWhatsappActivity.class);
				startActivity(mIntent);*/

				Intent mIntent = new Intent(Intent.ACTION_SEND);
				mIntent.setType("text/plain");
				mIntent.putExtra(Intent.EXTRA_TEXT,getResources().getString(R.string.invite_share_text));
//			    mIntent.setPackage("com.whatsapp");
			    if (mIntent != null) 
			        startActivity(mIntent);
			}
		});

		mContactInvite.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent mIntent = new Intent(InviteActivity.this,
						InviteContactActivity.class);
				startActivityForResult(mIntent, CONTACT_INVITE);
			}
		});

		mEmailInvite.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent mIntent = new Intent(InviteActivity.this,
						InviteEmailActivity.class);
				startActivityForResult(mIntent, EMAIL_INVITE);
			}
		});
	}

	private void sendFacebookRequestDialog() {
		Bundle params = new Bundle();
		params.putString("message",
				"Learn how to make your Android apps social");
		Log.i(TAG, ApplicationLoader.getPreferences().getFacebookSession());
		WebDialog requestsDialog = (new WebDialog.RequestsDialogBuilder(
				InviteActivity.this, ApplicationLoader.getPreferences()
						.getFacebookSession(), params)).setOnCompleteListener(
				new OnCompleteListener() {
					@Override
					public void onComplete(Bundle values,
							FacebookException error) {
						if (error != null) {
							if (error instanceof FacebookOperationCanceledException) {
								Toast.makeText(InviteActivity.this,
										"Request cancelled", Toast.LENGTH_SHORT)
										.show();
							} else {
								Toast.makeText(InviteActivity.this,
										"Network Error", Toast.LENGTH_SHORT)
										.show();
							}
						} else {
							final String requestId = values
									.getString("request");
							if (requestId != null) {
								Toast.makeText(InviteActivity.this,
										"Request sent", Toast.LENGTH_SHORT)
										.show();
							} else {
								Toast.makeText(InviteActivity.this,
										"Request cancelled", Toast.LENGTH_SHORT)
										.show();
							}
						}
					}
				}).build();
		requestsDialog.show();
	}

	private void sendFacebookMessageDialog() {
		// Check if the Facebook app is installed and we can present the share
		// dialog
		FacebookDialog.MessageDialogBuilder builder = new FacebookDialog.MessageDialogBuilder(
				InviteActivity.this)
				.setLink("https://developers.facebook.com/docs/android/share/")
				.setName("Message Dialog Tutorial")
				.setCaption("Build great social apps that engage your friends.")
				.setPicture("http://i.imgur.com/g3Qc1HN.png")
				.setDescription(
						"Allow your users to message links from your app using the Android SDK.");

		// If the Facebook app is installed and we can present the share dialog
		if (builder.canPresent()) {
			// Enable button or other UI to initiate launch of the Message
			// Dialog
			// Present message dialog
			FacebookDialog dialog = builder.build();
			dialog.present();
		} else {
			// Disable button or other UI for Message Dialog
		}
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == CONTACT_INVITE) {
			if (resultCode == Activity.RESULT_OK) {
				if (data.hasExtra(ContactPicker.CONTACTS_DATA)) {
					ArrayList<ContactPicker> contacts = data
							.getParcelableArrayListExtra(ContactPicker.CONTACTS_DATA);
					if (contacts != null) {
						JSONObject mJSONObject = RequestBuilder
								.getInviteSMSData(contacts);
						Log.i(TAG, mJSONObject.toString());
						Log.i(TAG, "CONTACT INVITE : " + contacts.size());
						volleyPostData(mJSONObject, AppConstants.URLS.URL_POST_SYNC_SMS);
					}
				}
			}
		}

		if (requestCode == EMAIL_INVITE) {
			if (resultCode == Activity.RESULT_OK) {
				if (data.hasExtra(ContactPicker.CONTACTS_DATA)) {
					ArrayList<ContactPicker> contacts = data
							.getParcelableArrayListExtra(ContactPicker.CONTACTS_DATA);
					if (contacts != null) {
						JSONObject mJSONObject = RequestBuilder
								.getInviteEmailData(contacts);
						Log.i(TAG, mJSONObject.toString());
						Log.i(TAG, "EMAIL INVITE : " + contacts.size());
						volleyPostData(mJSONObject, AppConstants.URLS.URL_POST_SYNC_EMAIL);
					}
				}
			}
		}
	}

	private void showProgressDialog() {
		mProgressDialog = new ProgressDialog(InviteActivity.this);
		mProgressDialog.setMessage("Inviting...");
		mProgressDialog.setCancelable(true);
		mProgressDialog.show();
	}

	private void dismissProgressDialog() {
		if (mProgressDialog != null)
			mProgressDialog.dismiss();
	}
	
	private void volleyPostData(JSONObject mJSONObject, String URL) {
		final String TAG = "volleyPostData";
		NetworkVolley nVolley = new NetworkVolley();
		showProgressDialog();
		Log.i(TAG, URL);
		Log.i(TAG, mJSONObject.toString());
		VolleyPostJsonRequest req = nVolley.new VolleyPostJsonRequest(URL, mJSONObject,
				new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						try {
							VolleyLog.v("Response:%n %s", response.toString(4));
							Log.i(TAG, response.toString());
							if (response.getBoolean("success")) {
								dismissProgressDialog();
								Toast.makeText(ApplicationLoader.getApplication().getApplicationContext(), "Invited Successfully", Toast.LENGTH_SHORT).show();	
							}
						} catch (JSONException e) {
							dismissProgressDialog();
							e.printStackTrace();
						}
					}
				}, new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						VolleyLog.e("Error: ", error.getMessage());
						dismissProgressDialog();
						if (BuildConfig.DEBUG) {
							Log.i(TAG, "Volley Error : " + error.getMessage());
							VolleyLog.e("Error: ", error.getMessage());
						}
					}
				});
		ApplicationLoader.getInstance().addToRequestQueue(req, TAG);
	}
}
