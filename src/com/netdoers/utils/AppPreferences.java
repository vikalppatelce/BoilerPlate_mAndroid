package com.netdoers.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.facebook.Session;

public class AppPreferences {

	SharedPreferences sharedPreferences;
	Editor editor;

	public AppPreferences(Context context) {
		// TODO Auto-generated constructor stub
		sharedPreferences = context.getSharedPreferences("Cache",
				Context.MODE_PRIVATE);
	}

	/*
	 * PREFERENCES : API DETAILS
	 */

	public void setApiKey(String str) {
		editor = sharedPreferences.edit();
		editor.putString("API_KEY", str);
		editor.commit();
	}

	public String getApiKey() {
		String flag = sharedPreferences.getString("API_KEY", null);
		return flag;
	}

	/*
	 * PUSH NOTIFICATION : GCM
	 */
	public void setRegisteredGCMToServer(boolean value) {
		editor = sharedPreferences.edit();
		editor.putBoolean("setRegisteredGCMToServer", value);
		editor.commit();
	}

	public boolean getRegisteredGCMToServer() {
		return sharedPreferences.getBoolean("setRegisteredGCMToServer", false);
	}

	/*
	 * PREFERENCES : APPLICATION DETAILS
	 */

	public void setFirstTime(boolean flag) {
		editor = sharedPreferences.edit();
		editor.putBoolean("isFirstTime", flag);
		editor.commit();
	}

	public boolean getFirstTime() {
		return sharedPreferences.getBoolean("isFirstTime", false);
	}

	/*
	 * PREFERENCES : FACEBOOK USER DETAILS
	 */

	public void setFacebookUserId(String str) {
		editor = sharedPreferences.edit();
		editor.putString("facebookUserId", str);
		editor.commit();
	}

	public String getFacebookUserId() {
		String flag = sharedPreferences.getString("facebookUserId", null);
		return flag;
	}

	public void setFacebookUserAccessToken(String str) {
		editor = sharedPreferences.edit();
		editor.putString("facebookUserAccessToken", str);
		editor.commit();
	}

	public String getFacebookUserAccessToken() {
		String flag = sharedPreferences.getString("facebookUserAccessToken",
				null);
		return flag;
	}

	public void setFacebookUserFriends(String str) {
		editor = sharedPreferences.edit();
		editor.putString("facebookUserFriends", str);
		editor.commit();
	}

	public String getFacebookUserFriends() {
		String flag = sharedPreferences.getString("facebookUserFriends", null);
		return flag;
	}

	public void setFacebookUserName(String str) {
		editor = sharedPreferences.edit();
		editor.putString("facebookUserName", str);
		editor.commit();
	}

	public String getFacebookUserName() {
		String flag = sharedPreferences.getString("facebookUserName", null);
		return flag;
	}

	public void setFacebookEmail(String str) {
		editor = sharedPreferences.edit();
		editor.putString("facebookUserEmail", str);
		editor.commit();
	}

	public String getFacebookEmail() {
		String flag = sharedPreferences.getString("facebookUserEmail", null);
		return flag;
	}

	public void setFacebookName(String str) {
		editor = sharedPreferences.edit();
		editor.putString("facebookName", str);
		editor.commit();
	}

	public String getFacebookName() {
		String flag = sharedPreferences.getString("facebookName", null);
		return flag;
	}

	public void setFacebookUserFirstName(String str) {
		editor = sharedPreferences.edit();
		editor.putString("facebookUserFirstName", str);
		editor.commit();
	}

	public String getFacebookUserFirstName() {
		String flag = sharedPreferences
				.getString("facebookUserFirstName", null);
		return flag;
	}

	public void setFacebookUserLastName(String str) {
		editor = sharedPreferences.edit();
		editor.putString("facebookUserLastName", str);
		editor.commit();
	}

	public String getFacebookUserLastName() {
		String flag = sharedPreferences.getString("facebookUserLastName", null);
		return flag;
	}

	public void setFacebookUserGender(String str) {
		editor = sharedPreferences.edit();
		editor.putString("facebookUserGender", str);
		editor.commit();
	}

	public String getFacebookUserGender() {
		String flag = sharedPreferences.getString("facebookUserGender", null);
		return flag;
	}

	public void setFacebookUserLocation(String str) {
		editor = sharedPreferences.edit();
		editor.putString("facebookUserLocation", str);
		editor.commit();
	}

	public String getFacebookUserLocation() {
		String flag = sharedPreferences.getString("facebookUserLocation", null);
		return flag;
	}

	/*
	 * PREFERENCES : USER DETAILS
	 */

	public void setUserName(String str) {
		editor = sharedPreferences.edit();
		editor.putString("username", str);
		editor.commit();
	}

	public String getUserName() {
		String flag = sharedPreferences.getString("username", null);
		return flag;
	}

	public void setUserNumber(String str) {
		editor = sharedPreferences.edit();
		editor.putString("contactnumber", str);
		editor.commit();
	}

	public String getUserNumber() {
		String flag = sharedPreferences.getString("contactnumber", null);
		return flag;
	}

	public void setUserGender(String str) {
		editor = sharedPreferences.edit();
		editor.putString("gender", str);
		editor.commit();
	}

	public String getUserGender() {
		String flag = sharedPreferences.getString("gender", null);
		return flag;
	}

	public void setProfilePicPath(String str) {
		editor = sharedPreferences.edit();
		editor.putString("profile_pic", str);
		editor.commit();
	}

	public String getProfilePicPath() {
		String flag = sharedPreferences.getString("profile_pic", null);
		return flag;
	}

	public String getProfilePostCount() {
		String flag = sharedPreferences.getString("post_count", null);
		return flag;
	}

	public void setProfilePostCount(String str) {
		editor = sharedPreferences.edit();
		editor.putString("post_count", str);
		editor.commit();
	}

	public String getProfileGroupCount() {
		String flag = sharedPreferences.getString("group_count", null);
		return flag;
	}

	public void setProfileGroupCount(String str) {
		editor = sharedPreferences.edit();
		editor.putString("group_count", str);
		editor.commit();
	}

	public void setUserCity(String str) {
		editor = sharedPreferences.edit();
		editor.putString("user_city", str);
		editor.commit();
	}

	public String getUserCity() {
		String flag = sharedPreferences.getString("user_city", null);
		return flag;
	}

	public void setUserCityId(String str) {
		editor = sharedPreferences.edit();
		editor.putString("user_city_id", str);
		editor.commit();
	}

	public String getUserCityId() {
		String flag = sharedPreferences.getString("user_city_id", null);
		return flag;
	}

	public void setUserState(String str) {
		editor = sharedPreferences.edit();
		editor.putString("user_state", str);
		editor.commit();
	}

	public String getUserState() {
		String flag = sharedPreferences.getString("user_state", null);
		return flag;
	}

	public void setUserStateId(String str) {
		editor = sharedPreferences.edit();
		editor.putString("user_state_id", str);
		editor.commit();
	}

	public String getUserStateId() {
		String flag = sharedPreferences.getString("user_state_id", null);
		return flag;
	}

	public void setUserCountry(String str) {
		editor = sharedPreferences.edit();
		editor.putString("user_country", str);
		editor.commit();
	}

	public String getUserCountry() {
		String flag = sharedPreferences.getString("user_country", null);
		return flag;
	}

	public void setUserCountryId(String str) {
		editor = sharedPreferences.edit();
		editor.putString("user_country_id", str);
		editor.commit();
	}

	public String getUserCountryId() {
		String flag = sharedPreferences.getString("user_country_id", null);
		return flag;
	}

	/*
	 * PREFERENCES : DEVICE DETAILS
	 */

	public void setScreenWidth(String str) {
		editor = sharedPreferences.edit();
		editor.putString("screenWidth", str);
		editor.commit();
	}

	public String getScreenWidth() {
		String flag = sharedPreferences.getString("screenWidth", null);
		return flag;
	}

	public void setDeviceId(String id) {
		editor = sharedPreferences.edit();
		editor.putString("deviceId", id);
		editor.commit();
	}

	public String getDeviceId() {
		String deviceId = sharedPreferences.getString("deviceId",
				"Device Id Not Found");
		return deviceId;
	}

	public void setDeviceSize(String id) {
		editor = sharedPreferences.edit();
		editor.putString("deviceId", id);
		editor.commit();
	}

	public String getDeviceSize() {
		String deviceId = sharedPreferences.getString("deviceSize",
				"Device Size Not Found");
		return deviceId;
	}

	public void setFacebookSession(String id) {
		editor = sharedPreferences.edit();
		editor.putString("facebookSession", id);
		editor.commit();
	}

	public String getFacebookSession() {
		String deviceId = sharedPreferences.getString("facebookSession", null);
		return deviceId;
	}

	public void setIMEINo(String id) {
		editor = sharedPreferences.edit();
		editor.putString("iMEINo", id);
		editor.commit();
	}

	public String getIMEINo() {
		String phoneId = sharedPreferences
				.getString("iMEINo", "IMEI Not Found");
		return phoneId;
	}
}
