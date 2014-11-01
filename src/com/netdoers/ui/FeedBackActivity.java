package com.netdoers.ui;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.netdoers.errorreporting.G;
import com.netdoers.tellus.R;
import com.netdoers.utils.ApplicationLoader;
import com.netdoers.utils.Mail;
import com.netdoers.utils.Utilities;

public class FeedBackActivity extends ActionBarActivity {

	private Button mSend;
	private EditText mDescription;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feedback);
		initUi();
		setActionBar();
		setEventListener();
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
		mDescription = (EditText)findViewById(R.id.activity_feedback_description);
		mSend = (Button)findViewById(R.id.activity_feedback_submit);
	}
	
	private void setActionBar(){
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		getSupportActionBar().setTitle(getString(R.string.feedback));
	}

	private void setEventListener(){
		mSend.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				sendFeedBack();
			}
		});
	}
	
	private void sendFeedBack(){
	
		if(!TextUtils.isEmpty(mDescription.getText().toString())){
			asyncSendFeedBack();
		}
	}
	
	private void asyncSendFeedBack() {
		if (Utilities.isInternetConnected()) {
			new MailTask().execute();
		} else {
			Toast.makeText(FeedBackActivity.this,
					getResources().getString(R.string.noconnection),
					Toast.LENGTH_SHORT).show();
		}
	}
	
	public class MailTask extends AsyncTask<String,Void,String>{
		private ProgressDialog mProgressDialog;

		@Override
		protected void onPreExecute() {
        mProgressDialog = new ProgressDialog(FeedBackActivity.this);
        mProgressDialog.setMessage("Sending...");
        mProgressDialog.setCancelable(true);
        mProgressDialog.show();
        }
		
		@Override
		protected String doInBackground(String... params) {
			/*
			 * MAIL SENDING
			 */
			Calendar c = Calendar.getInstance();
	    	SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
	    	String time = df.format(c.getTime());
	    	
			 Mail m = new Mail("androidbugtrace@gmail.com", "android1"); 
			String[] toArr = { "androidbugnetdoers@gmail.com",
					"androidbugtrace@gmail.com", "rajan.rwl@gmail.com",
					"dhaval_dms@yahoo.com"}; 
		      m.setTo(toArr);
		      m.setFrom("androidbugnetdoers@gmail.com"); 
		      m.setSubject("Tell Us | FeedBack ");
		      m.setBody("\n"
		    		+" Android OS   :" + G.ANDROID_VERSION +  "\n"
					+ "Phone Model  : " + G.PHONE_MODEL+ "\n" 
		    		+ "Phone Size   : "+ G.PHONE_SIZE + "\n" 
					+ "Version      : " + G.APP_VERSION+ "\n" 
					+ "Version Code : " + G.APP_VERSION_CODE + "\n"
					+ "Package      : " + G.APP_PACKAGE + "\n"
					+ "Time         : " + time + "\n"
					+ "Username     : " + ApplicationLoader.getPreferences().getUserName() + "\n"
					+ "api_key      : " + ApplicationLoader.getPreferences().getApiKey() + "\n"
					+ "--------------------------------------------------------------" + "\n"
					+ "Description  : " + mDescription.getText().toString() + "\n"
					+ "--------------------------------------------------------------" + "\n");
            
		      try { 
		        if(m.send()) { 
		        	Log.i("MailApp", "Mail sent successfully!"); 
		        } else { 
		        	Log.e("MailApp", "Could not send email"); 
		        } 
		      } catch(Exception e) { 
		        //Toast.makeText(MailApp.this, "There was a problem sending the email.", Toast.LENGTH_LONG).show(); 
		        Log.e("MailApp", "Could not send email", e); 
		      } 
			return "MailSent";
		}
		@Override
		protected void onPostExecute(String result) {
			if(mProgressDialog!=null){
				mProgressDialog.dismiss();
				Toast.makeText(FeedBackActivity.this, "Sent", Toast.LENGTH_SHORT).show();
				finish();
			}
        }
	}
}
