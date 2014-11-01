package com.netdoers.utils;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;
import android.util.Log;

import com.netdoers.beans.Contact;
import com.netdoers.beans.ContactPicker;
import com.netdoers.beans.FacebookFriend;

public class RequestBuilder {

	public static JSONObject getPostStatusData(String mGroupId,
			String mPost) {
		JSONObject stringBuffer = new JSONObject();
		try {
			stringBuffer.put("api_key", ApplicationLoader.getPreferences().getApiKey());
			stringBuffer.put("group_id", mGroupId);
			stringBuffer.put("post", mPost);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return stringBuffer;
	}

	public static JSONObject getPostStatusMedia(String mPath,
			String mPathIsVideo) {
		try {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("is_video", mPathIsVideo);
			jsonObject.put("file_name", mPath);
			return jsonObject;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return null;
	}

	public static JSONObject getPostCountryData(String mCountryId) {
		JSONObject stringBuffer = new JSONObject();
		try {
			stringBuffer.put("country_id", mCountryId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return stringBuffer;
	}

	public static JSONObject getPostStateData(String mStateId) {
		JSONObject stringBuffer = new JSONObject();
		try {
			stringBuffer.put("state_id", mStateId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return stringBuffer;
	}
	
	public static JSONObject getPostVerification(String mStateId) {
		JSONObject stringBuffer = new JSONObject();
		try {
			stringBuffer.put("api_key", ApplicationLoader.getPreferences().getApiKey());
			stringBuffer.put("code", mStateId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return stringBuffer;
	}
	
	public static JSONObject getPostSignUpData(String mUserName,
			String mPassword, String mConfirmPassword, String mEmail,
			String mDeviceId, String mDeviceName, String mSdkVersion,
			String mAppVersion, String mGender, String mCountryId,
			String mStateId, String mCityId, String mMobileNo) {
		JSONObject stringBuffer = new JSONObject();
		try {
			stringBuffer.put("username", mUserName);
			stringBuffer.put("password", mPassword);
			stringBuffer.put("password_confirmation", mConfirmPassword);
			stringBuffer.put("email", mEmail);
			stringBuffer.put("device_id", mDeviceId);
			stringBuffer.put("device_name", mDeviceName);
			stringBuffer.put("sdk_version", mSdkVersion);
			stringBuffer.put("app_version", mAppVersion);
			stringBuffer.put("gender", mGender);
			stringBuffer.put("country_id", mCountryId);
			stringBuffer.put("state_id", mStateId);
			stringBuffer.put("city_id", mCityId);
			stringBuffer.put("mobile_no", mMobileNo);
			stringBuffer.put("fb_user_id", ApplicationLoader.getPreferences().getFacebookUserId());
			stringBuffer.put("fb_access_token", ApplicationLoader.getPreferences().getFacebookUserAccessToken());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return stringBuffer;
	}
	
	public static JSONObject getPostSignInFacebookData(String mUserName,
			String mPassword, String mConfirmPassword, 
			String mDeviceId, String mDeviceName, String mSdkVersion,
			String mAppVersion, String mCountryId,
			String mStateId, String mCityId, String mMobileNo) {
		JSONObject stringBuffer = new JSONObject();
		try {
			stringBuffer.put("username", mUserName);
			stringBuffer.put("password", mPassword);
			stringBuffer.put("password_confirmation", mConfirmPassword);
			stringBuffer.put("device_id", mDeviceId);
			stringBuffer.put("device_name", mDeviceName);
			stringBuffer.put("sdk_version", mSdkVersion);
			stringBuffer.put("app_version", mAppVersion);
			stringBuffer.put("country_id", mCountryId);
			stringBuffer.put("state_id", mStateId);
			stringBuffer.put("city_id", mCityId);
			stringBuffer.put("mobile_no", mMobileNo);
			stringBuffer.put("user_id", ApplicationLoader.getPreferences().getFacebookUserId());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return stringBuffer;
	}
	
	public static JSONObject getPostProfileUpdateData(String mUserName,
			String mGender, String mCountryId, String mStateId, String mCityId,
			String mMobileNo) {
		JSONObject stringBuffer = new JSONObject();
		try {
			stringBuffer.put("username", mUserName);
			stringBuffer.put("gender", mGender);
			stringBuffer.put("country_id", mCountryId);
			stringBuffer.put("state_id", mStateId);
			stringBuffer.put("city_id", mCityId);
			stringBuffer.put("mobile_no", mMobileNo);
			stringBuffer.put("api_key", ApplicationLoader.getPreferences().getApiKey());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return stringBuffer;
	}
	
	public static JSONObject getPostSignInData(String mUserName, String mPassword) {
		JSONObject stringBuffer = new JSONObject();
		try {
			stringBuffer.put("email", mUserName);
			stringBuffer.put("password", mPassword);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return stringBuffer;
	}
	
	public static JSONObject getPostAddGroupData(String mApiKey, String mGroupName, String mGroupDescription) {
		JSONObject stringBuffer = new JSONObject();
		try {
			stringBuffer.put("api_key", mApiKey);
			stringBuffer.put("name", mGroupName);
			stringBuffer.put("description", mGroupDescription);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return stringBuffer;
	}
	
	public static JSONObject getPostStatusGroupIdData(String mGroupName) {
		JSONObject stringBuffer = new JSONObject();
		try {
			stringBuffer.put("name", mGroupName);
			stringBuffer.put("api_key", ApplicationLoader.getPreferences().getApiKey());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return stringBuffer;
	}
	
	public static JSONObject getPostGroupFeedData(String mGroupId, String mPaginationCount) {
		JSONObject stringBuffer = new JSONObject();
		try {
			stringBuffer.put("api_key", ApplicationLoader.getPreferences().getApiKey());
			stringBuffer.put("group_id", mGroupId);
			stringBuffer.put("count", mPaginationCount);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return stringBuffer;
	}
	
	public static JSONObject getPostFeedData(String mPaginationCount) {
		JSONObject stringBuffer = new JSONObject();
		try {
			stringBuffer.put("api_key", ApplicationLoader.getPreferences().getApiKey());
			stringBuffer.put("count", mPaginationCount);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return stringBuffer;
	}
	
	public static JSONObject getPostFeedDetailData(String mPostId) {
		JSONObject stringBuffer = new JSONObject();
		try {
			stringBuffer.put("api_key", ApplicationLoader.getPreferences().getApiKey());
			stringBuffer.put("post_id", mPostId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return stringBuffer;
	}
	
	public static JSONObject getPostGroupSearchData(String mGroupName) {
		JSONObject stringBuffer = new JSONObject();
		try {
			stringBuffer.put("name", mGroupName);
			stringBuffer.put("api_key", ApplicationLoader.getPreferences().getApiKey());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return stringBuffer;
	}
	
	public static JSONObject getPostLikeFeedData(String mPostId, String mIsLike) {
		JSONObject stringBuffer = new JSONObject();
		try {
			stringBuffer.put("api_key", ApplicationLoader.getPreferences().getApiKey());
			stringBuffer.put("post_id", mPostId);
			stringBuffer.put("is_like", mIsLike);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return stringBuffer;
	}
	
	public static JSONObject getPostNotifSeenData(String mPostId, String mNotificationId) {
		JSONObject stringBuffer = new JSONObject();
		try {
			stringBuffer.put("api_key", ApplicationLoader.getPreferences().getApiKey());
			stringBuffer.put("post_id", mPostId);
			stringBuffer.put("comment_id", mNotificationId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return stringBuffer;
	}
	
	public static JSONObject getPostReportActFeedData(String mPostId, String mIsAccept) {
		JSONObject stringBuffer = new JSONObject();
		try {
			stringBuffer.put("api_key", ApplicationLoader.getPreferences().getApiKey());
			stringBuffer.put("post_id", mPostId);
			stringBuffer.put("is_accepted", mIsAccept);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return stringBuffer;
	}
	
	public static JSONObject getPostReportFeedData(String mPostId, String mIsReport) {
		JSONObject stringBuffer = new JSONObject();
		try {
			stringBuffer.put("post_id", mPostId);
			stringBuffer.put("is_spam", mIsReport);
			stringBuffer.put("api_key", ApplicationLoader.getPreferences().getApiKey());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return stringBuffer;
	}
	
	public static JSONObject getPostReportDeleteFeedData(String mPostId) {
		JSONObject stringBuffer = new JSONObject();
		try {
			stringBuffer.put("post_id", mPostId);
			stringBuffer.put("api_key", ApplicationLoader.getPreferences().getApiKey());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return stringBuffer;
	}
	
	
	public static JSONObject getPostUserProfilePicData() {
		JSONObject stringBuffer = new JSONObject();
		try {
			stringBuffer.put("api_key", ApplicationLoader.getPreferences().getApiKey());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return stringBuffer;
	}
	
	public static JSONObject getPostGroupSubscribeData(String mGroupId, String mIsSubscribe) {
		JSONObject stringBuffer = new JSONObject();
		try {
			stringBuffer.put("api_key", ApplicationLoader.getPreferences().getApiKey());
			stringBuffer.put("group_id", mGroupId);
			stringBuffer.put("is_subscribe", mIsSubscribe);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return stringBuffer;
	}
	
	public static JSONObject getPostTrueFalseFeedData(String mPostId, String mIsTrue) {
		JSONObject stringBuffer = new JSONObject();
		try {
			stringBuffer.put("api_key", ApplicationLoader.getPreferences().getApiKey());
			stringBuffer.put("post_id", mPostId);
			stringBuffer.put("is_true_false", mIsTrue);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return stringBuffer;
	}
	
	public static JSONObject getPostFeedCommentData(String mPostId, String mComment) {
		JSONObject stringBuffer = new JSONObject();
		try {
			stringBuffer.put("api_key", ApplicationLoader.getPreferences().getApiKey());
			stringBuffer.put("post_id", mPostId);
			stringBuffer.put("comment", mComment);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return stringBuffer;
	}
	
	public static JSONObject getPostApiData() {
		JSONObject stringBuffer = new JSONObject();
		try {
			stringBuffer.put("api_key", ApplicationLoader.getPreferences().getApiKey());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return stringBuffer;
	}
	
	public static JSONObject getPostFacebookUserData(String mUserId,
			String mUserName, String mEmail, String mName, String mFirstName,
			String mLastName, String mGender, String mLocation, String mAccessToken) {
		JSONObject stringBuffer = new JSONObject();
		try {
			stringBuffer.put("user_id", mUserId);
			stringBuffer.put("username", mUserName);
			stringBuffer.put("email", mEmail);
			stringBuffer.put("name", mName);
			stringBuffer.put("first_name", mFirstName);
			stringBuffer.put("last_name", mLastName);
			stringBuffer.put("gender", mGender);
			stringBuffer.put("location", mLocation);
			stringBuffer.put("access_token", mAccessToken);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return stringBuffer;
	}
	
	public static JSONObject getPostFacebookUserIdData() {
		JSONObject stringBuffer = new JSONObject();
		try {
			stringBuffer.put("fb_user_id", ApplicationLoader.getPreferences().getFacebookUserId());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return stringBuffer;
	}
	
	public static JSONObject getPostFacebookUserFriendData(ArrayList<FacebookFriend> mFacebookFriend) {
		JSONArray jsonArray = new JSONArray();
		JSONObject mJSONObject = new JSONObject();
		if (mFacebookFriend != null && mFacebookFriend.size() > 0) {
			for (int i = 0; i < mFacebookFriend.size(); i++) {
				JSONObject jsonObject = new JSONObject();
				try {
					jsonObject.put("facebook_id", mFacebookFriend.get(i).getFacebookId());
					jsonObject.put("facebook_name", mFacebookFriend.get(i).getFacebookName());
					jsonArray.put(jsonObject);
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
		}
		try {
			mJSONObject.put("facebook_friends", jsonArray);
			mJSONObject.put("facebook_user_id", ApplicationLoader.getPreferences().getFacebookUserId());
			mJSONObject.put("api_key", ApplicationLoader.getPreferences().getApiKey());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return mJSONObject;
	}
	
	public static JSONObject getContactsData(ArrayList<Contact> contact) {
		JSONArray jsonArray = new JSONArray();
		JSONObject mJSONObject = new JSONObject();
		if (contact != null && contact.size() > 0) {
			for (int i = 0; i < contact.size(); i++) {
				JSONObject jsonObject = new JSONObject();
				try {
					jsonObject.put("contact_id", contact.get(i).getContactId());
					jsonObject.put("contact_name", contact.get(i).getContactName());
					jsonObject.put("contact_number", contact.get(i).getContactNumber());
					if(!TextUtils.isEmpty(contact.get(i).getContactEmail())){
						jsonObject.put("contact_email", contact.get(i).getContactEmail());
					}else{
						jsonObject.put("contact_email", "");
					}
					jsonArray.put(jsonObject);
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
		}
		try {
			mJSONObject.put("contacts", jsonArray);
			mJSONObject.put("api_key", ApplicationLoader.getPreferences().getApiKey());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return mJSONObject;
	}

	
	public static JSONObject getInviteSMSData(ArrayList<ContactPicker> contact) {
		JSONArray jsonArray = new JSONArray();
		JSONObject mJSONObject = new JSONObject();
		if (contact != null && contact.size() > 0) {
			for (int i = 0; i < contact.size(); i++) {
				JSONObject jsonObject = new JSONObject();
				try {
					jsonObject.put("contact_id", contact.get(i).getContactId());
					jsonObject.put("contact_name", contact.get(i).getContactName());
					jsonObject.put("contact_number", contact.get(i).getContactNumber());
					jsonArray.put(jsonObject);
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
		}
		try {
			mJSONObject.put("invite_contacts", jsonArray);
			mJSONObject.put("api_key", ApplicationLoader.getPreferences().getApiKey());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return mJSONObject;
	}

	public static JSONObject getInviteEmailData(ArrayList<ContactPicker> contact) {
		JSONArray jsonArray = new JSONArray();
		JSONObject mJSONObject = new JSONObject();
		if (contact != null && contact.size() > 0) {
			for (int i = 0; i < contact.size(); i++) {
				JSONObject jsonObject = new JSONObject();
				try {
					jsonObject.put("contact_id", contact.get(i).getContactId());
					jsonObject.put("contact_name", contact.get(i).getContactName());
					jsonObject.put("contact_email", contact.get(i).getContactEmail());
					jsonArray.put(jsonObject);
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
		}
		try {
			mJSONObject.put("invite_email", jsonArray);
			mJSONObject.put("api_key", ApplicationLoader.getPreferences().getApiKey());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return mJSONObject;
	}
}

