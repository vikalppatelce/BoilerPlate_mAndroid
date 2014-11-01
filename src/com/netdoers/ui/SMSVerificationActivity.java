package com.netdoers.ui;

import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.netdoers.tellus.R;
import com.netdoers.utils.AppConstants;
import com.netdoers.utils.RequestBuilder;
import com.netdoers.utils.RestClient;
import com.netdoers.utils.Utilities;

public class SMSVerificationActivity extends ActionBarActivity {
	
	//DECLARE VIEW
	private TextView codeTxt, codeTxtVerify;
	private TextView mTitle;
	private Button proceed, mSkip;
	private ActionBar mActionBar;
	private VerificationSMSReceiver messageReceiver;
	
	//CONSTANT
	public static final String TAG 							= SMSVerificationActivity.class.getSimpleName();
	
	String intentCode;
	
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_verification_layout);
		initUi();
		setCustomActionBar();		
		setEventListeners();
		messageReceiver = new VerificationSMSReceiver();
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		unregisterReceiver(messageReceiver);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		registerReceiver(messageReceiver, new IntentFilter("android.provider.Telephony.SMS_RECEIVED"));
	}

	private void initUi(){
		//GET VIEW FROM LAYOUT
		codeTxt = (TextView)findViewById(R.id.verification_txt_zname);
		codeTxtVerify = (TextView)findViewById(R.id.verification_txt_verify);
		proceed = (Button)findViewById(R.id.verification_btn);
	}
	
	private void setCustomActionBar(){
		mActionBar = getSupportActionBar();
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		mActionBar.setDisplayShowTitleEnabled(false);
		LayoutInflater mInflater = LayoutInflater.from(this);
		View mCustomView = mInflater.inflate(R.layout.actionbar_verification, null);
		mActionBar.setCustomView(mCustomView);
		mActionBar.setDisplayShowCustomEnabled(true);
		mSkip = (Button)mCustomView.findViewById(R.id.actionbar_verification_skip);
		mTitle = (TextView)mCustomView.findViewById(R.id.actionbar_verification_title);
	}
	
	private void setEventListeners(){
		//LISTENERS
		proceed.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onVerification(v);
			}
		});
		
		mSkip.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent mIntent = new Intent(SMSVerificationActivity.this, SignInActivity.class);
				startActivity(mIntent);
				finish();
			}
		});
	}
	
	public void onVerification(View v)
	{
		try {
				if(Utilities.isInternetConnected() && !TextUtils.isEmpty(codeTxt.getText().toString())){
					new VerificationTask(codeTxt.getText().toString(), this).execute();	
				}
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}
	}
	
	
	public class VerificationSMSReceiver extends BroadcastReceiver {
        // Get the object of SmsManager
        final SmsManager sms = SmsManager.getDefault();
        public void onReceive(Context context, Intent intent) {
            // Retrieves a map of extended data from the intent.
            final Bundle bundle = intent.getExtras();
            try {
                if (bundle != null) {
                    final Object[] pdusObj = (Object[]) bundle.get("pdus");
                    for (int i = 0; i < pdusObj.length; i++) {
                        SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                        String phoneNumber = currentMessage.getDisplayOriginatingAddress();
                        String senderNum = phoneNumber;
                        String message = currentMessage.getDisplayMessageBody();
                        Log.i("SmsReceiver", "senderNum: "+ senderNum + "; message: " + message);
                       // Show Alert
                        int duration = Toast.LENGTH_LONG;
                        Toast toast = Toast.makeText(context, 
                                     "senderNum: "+ senderNum + ", message: " + message, duration);
                        toast.show();
                    } // end for loop
                  } // bundle is null
    
            } catch (Exception e) {
                Log.e("SmsReceiver", "Exception smsReceiver" +e);
            }
        }    
    }

	public class VerificationTask extends AsyncTask<Void, Void, Void>{
		String code;
		String apiKey;
		String znumber;
		String fullName;
		boolean successvalue = false;
		String errorvalue;
		private Context context;
		private ProgressDialog progressDialog;
		
		public VerificationTask(String code, Context context){
			this.code=code;
			this.context = context;
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
		JSONObject dataToSend = RequestBuilder.getPostVerification(code);
		Log.i(TAG, AppConstants.URLS.URL_POST_PROFILE_VERIFY);
		Log.i(TAG, dataToSend.toString());
				try {

					String str = RestClient.postJSON(AppConstants.URLS.URL_POST_PROFILE_VERIFY,dataToSend);
					JSONObject object = new JSONObject(str);
				} catch (Exception e) {
				Log.e(TAG, e.toString());
				}
			return null;
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			progressDialog = new ProgressDialog(context);
			progressDialog.setMessage("Verification...");
			progressDialog.show();
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if(progressDialog!=null)
				progressDialog.dismiss();
			
			if(TextUtils.isEmpty(apiKey)){
				Toast.makeText(context, "Invalid Verification Code", Toast.LENGTH_SHORT).show();
			}
			else{
				Intent signIn = new Intent(SMSVerificationActivity.this,SignInActivity.class);
				startActivity(signIn);
				finish();
			}
		}
	}
}

