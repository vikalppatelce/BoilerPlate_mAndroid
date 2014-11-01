package com.netdoers.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.netdoers.beans.Contact;
import com.netdoers.tellus.R;
import com.netdoers.utils.AppConstants;
import com.netdoers.utils.AppPreferences;
import com.netdoers.utils.ApplicationLoader;
import com.netdoers.utils.NetworkVolley;
import com.netdoers.utils.NetworkVolley.VolleyPostJsonRequest;
import com.netdoers.utils.RequestBuilder;
import com.netdoers.utils.SyncContact;
import com.netdoers.utils.Utilities;

@SuppressLint("NewApi")
public class PrefsActivity extends PreferenceActivity implements
		OnSharedPreferenceChangeListener {
	private Preference prefsContactSync;
	private Preference prefsDev;
	private static String TAG = PrefsActivity.class.getSimpleName();
	private ProgressDialog mProgressDialog;
	private ArrayList<Contact> mContact;

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		PreferenceManager prefMgr = getPreferenceManager();
		addPreferencesFromResource(R.xml.activity_setting);

		prefsContactSync = prefMgr.findPreference("prefContactSync");
		if (prefsContactSync != null) {
			prefsContactSync
					.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

						@Override
						public boolean onPreferenceClick(Preference preference) {
							// TODO Auto-generated method stub
							volleyContactSync();
							return false;
						}
					});
		}

		prefsDev = prefMgr.findPreference("prefDev");
		if (prefsDev != null) {
			prefsDev.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

				@Override
				public boolean onPreferenceClick(Preference preference) {
					// TODO Auto-generated method stub
					copyDatabase();
					return false;
				}
			});
		}
	}

	private void copyDatabase() {
		try {
			File sd = Environment.getExternalStorageDirectory();
			File data = Environment.getDataDirectory();

			if (sd.canWrite()) {
				String currentDBPath = "/data/data/" + getPackageName()
						+ "/databases/ApplicationDB";
				String backupDBPath = "ApplicationDB_Dev.db";
				File currentDB = new File(currentDBPath);
				File backupDB = new File(sd, backupDBPath);

				if (currentDB.exists()) {
					FileChannel src = new FileInputStream(currentDB)
							.getChannel();
					FileChannel dst = new FileOutputStream(backupDB)
							.getChannel();
					dst.transferFrom(src, 0, src.size());
					src.close();
					dst.close();
					Toast.makeText(PrefsActivity.this, "Database Transferred!",
							Toast.LENGTH_SHORT).show();
				}
			}
		} catch (Exception e) {
			Toast.makeText(PrefsActivity.this, e.toString(), Toast.LENGTH_SHORT)
					.show();
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		// Set up a listener whenever a key changes
		getPreferenceScreen().getSharedPreferences()
				.registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		// Unregister the listener whenever a key changes
		getPreferenceScreen().getSharedPreferences()
				.unregisterOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
	}

	private void showProgressDialog() {
		mProgressDialog = new ProgressDialog(PrefsActivity.this);
		mProgressDialog.setMessage("Synchornizing...");
		mProgressDialog.setCancelable(true);
		mProgressDialog.show();
	}

	private void dismissProgressDialog() {
		if (mProgressDialog != null)
			mProgressDialog.dismiss();
	}

	private void volleyContactSync() {
		if (Utilities.isInternetConnected()) {
			Log.i(TAG, "volleyContactSync");
			showProgressDialog();
			getContactsData();
			if(mContact==null && mContact.size() < 0)
				return;
			NetworkVolley nVolley = new NetworkVolley();
			JSONObject mDataJSON = RequestBuilder.getContactsData(mContact);
			Log.i(TAG, mDataJSON.toString());
			Log.i(TAG, AppConstants.URLS.URL_POST_SYNC_CONTACT);
			VolleyPostJsonRequest req = nVolley.new VolleyPostJsonRequest(
					AppConstants.URLS.URL_POST_SYNC_CONTACT, mDataJSON,
					new Listener<JSONObject>() {
						@Override
						public void onResponse(JSONObject response) {
							try {
								if(response.getBoolean("success")){
									VolleyLog.v("Response:%n %s",
											response.toString(4));
									Log.i(TAG, response.toString());
									dismissProgressDialog();
								}
							} catch (JSONException e) {
								e.printStackTrace();
								dismissProgressDialog();
							}
						}
					}, new ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError error) {
							VolleyLog.e("Error: ", error.getMessage());
							dismissProgressDialog();
						}
					});
			ApplicationLoader.getInstance().addToRequestQueue(req, TAG);
		} else {
			Toast.makeText(PrefsActivity.this, R.string.noconnection,
					Toast.LENGTH_SHORT).show();
		}
	}

	private void getContactsData() {
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
			mContact = SyncContact.getContactsNewApi(new LinkedHashMap<String, Contact>(),new ArrayList<Contact>());
		} else {
			mContact = SyncContact.getContactsOldApiSlow(new LinkedHashMap<String, Contact>(),new ArrayList<Contact>());
		}
	}
}
