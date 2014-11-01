/*
 * This is the source code of Zname for Android v. 0.1.1.
 * Copyright Vikalp Patel, 2014.
 */

package com.netdoers.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.facebook.FacebookException;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.facebook.widget.LoginButton.OnErrorListener;
import com.google.gson.Gson;
import com.netdoers.beans.FacebookFriend;
import com.netdoers.tellus.BuildConfig;
import com.netdoers.tellus.R;
import com.netdoers.utils.AppConstants;
import com.netdoers.utils.ApplicationLoader;
import com.netdoers.utils.NetworkVolley;
import com.netdoers.utils.NetworkVolley.VolleyPostJsonRequest;
import com.netdoers.utils.RequestBuilder;
import com.netdoers.utils.Utilities;

public class IntroActivity extends FragmentActivity {
    private ViewPager viewPager;
    private ViewGroup bottomPages;
    private int lastPage = 0;
    private boolean justCreated = false;
    private boolean startPressed = false;
    private int[] titles;
    private int[] messages;
    
    private ArrayList<FacebookFriend> mFacebookFriend;
    
    private ProgressDialog mProgressDialog;
    
    private TextView mTxtSignIn, mTxtSignUp, mButtonLogin; /*SIMPLE FACEBOOK*/
    private LoginButton mAuthFacebook;
    
//    private SimpleFacebook  mSimpleFacebook;/*SIMPLE FACEBOOK*/
    
    private static final String TAG = IntroActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.intro_layout);

        if(ApplicationLoader.getPreferences().getApiKey()!=null){
        	Intent intent = new Intent(IntroActivity.this, MotherActivity.class);
        	startActivity(intent);
        	finish();
        }
        
        if (Utilities.isRTL) {
            titles = new int[] {
            		R.string.Page5Title,
            		R.string.Page4Title,
                    R.string.Page3Title,
                    R.string.Page2Title,
                    R.string.Page1Title
            };
            messages = new int[] {
            		R.string.Page5Message,
            		R.string.Page4Message,
                    R.string.Page3Message,
                    R.string.Page2Message,
                    R.string.Page1Message
            };
        } else {
            titles = new int[] {
                    R.string.Page1Title,
                    R.string.Page2Title,
                    R.string.Page3Title,
                    R.string.Page4Title,
                    R.string.Page5Title
            };
            messages = new int[] {
                    R.string.Page1Message,
                    R.string.Page2Message,
                    R.string.Page3Message,
                    R.string.Page4Message,
                    R.string.Page5Message
            };
        }
        viewPager = (ViewPager)findViewById(R.id.intro_view_pager);
        mTxtSignIn = (TextView) findViewById(R.id.intro_txtview_signin);
        mTxtSignUp = (TextView) findViewById(R.id.intro_txtview_signup);
//        mButtonLogin = (TextView) findViewById(R.id.intro_txtview_signup_facebook); /*SIMPLE FACEBOOK*/
        mAuthFacebook = (LoginButton) findViewById(R.id.intro_txtview_signup_facebook); /*NATIVE FACEBOOK*/
        bottomPages = (ViewGroup)findViewById(R.id.bottom_pages);
        
        viewPager.setAdapter(new IntroAdapter());
        viewPager.setPageMargin(0);
        viewPager.setOffscreenPageLimit(1);
        justCreated = true;
        
        setEventListeners();
//        setLogin(); /*SIMPLE FACEBOOK*/
        wireFacebookLogin(); /*NATIVE FACEBOOK*/
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        if (justCreated) {
            if (Utilities.isRTL) {
                viewPager.setCurrentItem(2);
                lastPage = 4;
            } else {
                viewPager.setCurrentItem(0);
                lastPage = 0;
            }
            justCreated = false;
        }
//        mSimpleFacebook = SimpleFacebook.getInstance(this); /*SIMPLE FACEBOOK*/
    }
    

    private void setEventListeners(){
    	mTxtSignIn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(IntroActivity.this, SignInActivity.class);
				startActivity(intent);
				/*Intent intent = new Intent(IntroActivity.this, MotherActivity.class);
				startActivity(intent);*/
			}
		});
    	
    	/*NATIVE FACEBOOK*/
    	/*mAuthFacebook.setOnClickListener(new View.OnClickListener() { 
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			}
		});*/
    	
    	mTxtSignUp.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(IntroActivity.this, SignUpActivity.class);
				startActivity(intent);
			}
		});
    }
    
    private void wireFacebookLogin(){
    
    	mAuthFacebook.setOnErrorListener(new OnErrorListener() {
			@Override
			public void onError(FacebookException error) {
				// TODO Auto-generated method stub
				Log.i(TAG, error.toString());
			}
		});
    	
    	mAuthFacebook.setReadPermissions(Arrays.asList("public_profile","email","user_friends"));
    	
    	mAuthFacebook.setSessionStatusCallback(new Session.StatusCallback() {
			@SuppressWarnings("deprecation")
			@Override
			public void call(final Session session, SessionState state, Exception exception) {
				// TODO Auto-generated method stub
				if (session.isOpened()) {
		              Log.i(TAG,"Access Token"+ session.getAccessToken());
		              Request.executeMeRequestAsync(session,
                      new Request.GraphUserCallback() {
                      @Override
                      public void onCompleted(GraphUser user,Response response) {
                              if (user != null) {
                            	   showProgressDialog();
								   JSONObject mJSONObject = RequestBuilder.getPostFacebookUserData(
												String.valueOf(user.getId()),
												String.valueOf(user.asMap().get("username")),
												String.valueOf(user.asMap().get("email")), String.valueOf(user
														.asMap().get("name")),
												String.valueOf(user.asMap().get("first_name")),
												String.valueOf(user.asMap().get("last_name")),
												String.valueOf(user.asMap().get("gender")),
												String.valueOf(user.asMap().get("location")), String.valueOf(session.getAccessToken()));
								   Utilities.logUserFacebookInfo(user);
								   ApplicationLoader.getPreferences().setFacebookUserId(String.valueOf(user.getId()));
								   ApplicationLoader.getPreferences().setFacebookUserAccessToken(String.valueOf(session.getAccessToken()));
								   try{
									   ApplicationLoader.getPreferences().setFacebookUserName(String.valueOf(user.asMap().get("username")));
									   ApplicationLoader.getPreferences().setFacebookUserFirstName(String.valueOf(user.asMap().get("first_name")));
									   ApplicationLoader.getPreferences().setFacebookUserLastName(String.valueOf(user.asMap().get("last_name")));
									   ApplicationLoader.getPreferences().setFacebookUserGender(String.valueOf(user.asMap().get("gender")));
									   ApplicationLoader.getPreferences().setFacebookUserLocation(String.valueOf(user.asMap().get("location")));
									   ApplicationLoader.getPreferences().setFacebookName(String.valueOf(user
												.asMap().get("name")));
									   ApplicationLoader.getPreferences().setFacebookEmail(String.valueOf(user.asMap().get("email")));
								   }catch(Exception e){
									   Log.i(TAG, " saving Facebook Data "+e.toString());
								   }
	                               requestFacebookFriends(session);
	                               volleyFacebook(RequestBuilder.getPostFacebookUserIdData());
	                              }
                  	}
	              });
				}
			}
		});
    }
    
    
    
    @SuppressWarnings("deprecation")
	private void requestFacebookFriends(Session session) {
    	Log.i(TAG, "requestFacebookFriends");
        Request.executeMyFriendsRequestAsync(session,
                new Request.GraphUserListCallback() {
                    @Override
                    public void onCompleted(List<GraphUser> users,
                            Response response) {
                        // TODO: your code for friends here!
                    	Log.i(TAG, "requestFacebookFriends : onCompleted");
                    	Log.i(TAG, users.toString());
                    	mFacebookFriend = new ArrayList<FacebookFriend>();
                    	for(int i = 0 ; i < users.size() ; i++){
                    		FacebookFriend mFacebookFriendObject = new FacebookFriend();
                    		mFacebookFriendObject.setFacebookId(users.get(i).getId());
                    		mFacebookFriendObject.setFacebookName(users.get(i).getName());
                    		mFacebookFriend.add(mFacebookFriendObject);
                    	}
                    	
                    	if(mFacebookFriend!=null && mFacebookFriend.size() > 0){
                    		Gson gson = new Gson();
                    		String json = gson.toJson(mFacebookFriend);
                    		ApplicationLoader.getPreferences().setFacebookUserFriends(json);
                    	}
                    }
                });
    }
    
	/**
	 * Login example. //SIMPLE FACEBOOK
	 */
/*	private void setLogin() {
		// Login listener
		final OnLoginListener onLoginListener = new OnLoginListener() {

			@Override
			public void onFail(String reason) {
				Log.w(TAG, "Failed to login");
			}

			@Override
			public void onException(Throwable throwable) {
				Log.e(TAG, "Bad thing happened", throwable);
			}

			@Override
			public void onThinking() {
				// show progress bar or something to the user while login is
				// happening
			}

			@Override
			public void onLogin() {
				// change the state of the button or do whatever you want
				getProfile();
			}

			@Override
			public void onNotAcceptingPermissions(Permission.Type type) {
//				toast(String.format("You didn't accept %s permissions", type.name()));
			}
		};

		mButtonLogin.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				mSimpleFacebook.login(onLoginListener);
			}
		});
	}
	
	private void getProfile(){
		mSimpleFacebook.getProfile(new OnProfileListener() {

			@Override
			public void onThinking() {
			}

			@Override
			public void onException(Throwable throwable) {
			}

			@Override
			public void onFail(String reason) {
			}

			@Override
			public void onComplete(Profile response) {
				String str = Utilities.toHtml(response);
				Log.i(TAG,String.valueOf(Html.fromHtml(str)));
			}
		});
	}*/
	 
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Session.getActiveSession().onActivityResult(this, requestCode,resultCode, data); /*NATIVE FACEBOOK*/
		ApplicationLoader.getPreferences().setFacebookSession(Session.getActiveSession().toString());
//		mSimpleFacebook.onActivityResult(this, requestCode, resultCode, data); /*SIMPLE FACEBOOK*/
	}

    private class IntroAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return 5;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = View.inflate(container.getContext(), R.layout.intro_view_layout, null);
            TextView headerTextView = (TextView)view.findViewById(R.id.intro_header_text);
            TextView messageTextView = (TextView)view.findViewById(R.id.intro_message_text);
            container.addView(view, 0);

            headerTextView.setText(getString(titles[position]));
            messageTextView.setText(Html.fromHtml(getString(messages[position])));
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
            int count = bottomPages.getChildCount();
            for (int a = 0; a < count; a++) {
                View child = bottomPages.getChildAt(a);
                if (a == position) {
//                    child.setBackgroundColor(0xffffde00);
                    child.setBackgroundResource(R.drawable.shape_intro_fill);
                } else {
//                    child.setBackgroundColor(0xffffffff);
                    child.setBackgroundResource(R.drawable.shape_intro);
                }
            }
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override
        public void finishUpdate(View arg0) {
        }

        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {
        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public void startUpdate(View arg0) {
        }
    }
    
	private void volleyFacebook(JSONObject mJSONObject) {
		final String TAG = "volleyFacebook";
		NetworkVolley nVolley = new NetworkVolley();
		Log.i(TAG, AppConstants.URLS.URL_POST_FB_EXISTS);
		Log.i(TAG, mJSONObject.toString());
		VolleyPostJsonRequest req = nVolley.new VolleyPostJsonRequest(AppConstants.URLS.URL_POST_FB_EXISTS, mJSONObject,
				new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						try {
							VolleyLog.v("Response:%n %s", response.toString(4));
							Log.i(TAG, response.toString());
							if (response.getBoolean("success") && !response.getBoolean("is_exists")) {
								dismissDialog();
								Intent mIntent = new Intent(IntroActivity.this, SignUpActivity.class);
								startActivity(mIntent);
								if (BuildConfig.DEBUG) {
									VolleyLog.v("Response:%n %s",
											response.toString(4));
									Log.i(TAG, response.toString());
								}
							}else{
								Intent mIntent = new Intent(IntroActivity.this, SignInActivity.class);
								startActivity(mIntent);
							}
						} catch (JSONException e) {
							dismissDialog();
							e.printStackTrace();
						}
					}
				}, new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						VolleyLog.e("Error: ", error.getMessage());
						dismissDialog();
						if (BuildConfig.DEBUG) {
							Log.i(TAG, "Volley Error : " + error.getMessage());
							VolleyLog.e("Error: ", error.getMessage());
						}
					}
				});
		ApplicationLoader.getInstance().addToRequestQueue(req, TAG);
	}
	
	private void showProgressDialog(){
		mProgressDialog = new ProgressDialog(IntroActivity.this);
		mProgressDialog.setMessage("Please wait...");
		mProgressDialog.show();
	}
	
	private void dismissDialog(){
		if(mProgressDialog!=null)
			mProgressDialog.dismiss();
	}
}
