package com.netdoers.utils;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.netdoers.beans.Feed;
import com.netdoers.beans.FeedMedia;
import com.netdoers.tellus.BuildConfig;

public class ParseJSON {

	private static final String TAG = ParseJSON.class.getSimpleName();
	public static ArrayList<Feed> parseFeedFromJSON(ArrayList<Feed> mFeed, JSONObject mJSONObject){
		try{
			JSONArray feedArray = mJSONObject.getJSONArray("feeds");
			for (int i = 0; i < feedArray.length(); i++) {
				Feed feed = new Feed();
				JSONObject mFeedObj = (JSONObject) feedArray.get(i);
				JSONArray feedMediaArray = null;
				JSONArray feedCommentArray = null;
				
				try{
					feedMediaArray = mFeedObj.getJSONArray("resources");
				}catch(JSONException e){
					Log.e(TAG, e.toString());
				}
				try{
					feedCommentArray = mFeedObj.getJSONArray("comments");
				}catch(JSONException e){
					Log.e(TAG, e.toString());
				}
				
				feed.setFeedId(mFeedObj.getString("id"));//feed_id
				feed.setFeedContent(mFeedObj.getString("posted_status"));//posted text
				feed.setFeedPostedUser(mFeedObj.getString("username"));//posted user
				feed.setFeedPostedImage(mFeedObj.getString("profile_pic"));//posted user image
				feed.setFeedPostedInGroup(mFeedObj.getString("posted_in_group"));//posted in group
				feed.setFeedIsLike(mFeedObj.getString("is_like"));//is user like
				feed.setFeedIsTrue(mFeedObj.getString("is_false"));//is user false
				feed.setFeedIsFalse(mFeedObj.getString("is_true"));// is user true
				feed.setFeedTime(mFeedObj.getString("created_time"));// posted time
				feed.setFeedTrueCount(mFeedObj.getString("true_count"));//post true count
				feed.setFeedFalseCount(mFeedObj.getString("false_count"));//post false count
				
				ArrayList<FeedMedia> arrayFeedMedia = new ArrayList<FeedMedia>();
				if(feedMediaArray!=null){
					for (int j = 0; j < feedMediaArray.length(); j++) {
						JSONObject mFeedMediaObj = (JSONObject) feedMediaArray.get(j);
						FeedMedia feedMedia = new FeedMedia();
						feedMedia.setFeedId(mFeedObj.getString("id")); // feed_id
						feedMedia.setFeedMediaPath(mFeedMediaObj.getString("path"));//feed_media_path
						feedMedia.setFeedIsVideo(mFeedMediaObj.getString("type"));//feed_media_is_video
//						feedMedia.setFeedMediaId(mFeedMediaObj.getString(""));//feed_media_id
						
						arrayFeedMedia.add(feedMedia);
						if(BuildConfig.DEBUG){
							Log.i(TAG, "id : "+mFeedObj.getString("id")); // feed_id
							Log.i(TAG, "source : "+mFeedMediaObj.getString("path"));//feed_media_path
							Log.i(TAG, "isVideo : "+mFeedMediaObj.getString("type"));//feed_media_is_video
						}
					}
				}
				feed.setFeedMedia(arrayFeedMedia);
				mFeed.add(feed);
				
				if (BuildConfig.DEBUG) {
					Log.i(TAG,"data : length : "+ feedArray.length());
					if(feedMediaArray!=null){
						Log.i(TAG,"feed_media : length : "+feedMediaArray.length());
					}
					if(feedCommentArray!=null){
						Log.i(TAG,"comments : length : "+feedCommentArray.length());	
					}
					
					Log.i(TAG,"_id : "+mFeedObj.getString("id"));//feed_id
					Log.i(TAG,"posted_status : "+mFeedObj.getString("posted_status"));//posted text
					Log.i(TAG,"posted_user : "+mFeedObj.getString("username"));//posted user
					Log.i(TAG,"posted_user_pic : "+mFeedObj.getString("profile_pic"));//posted user image
					Log.i(TAG,"posted_in_group : " + mFeedObj.getString("posted_in_group"));//posted in group
					Log.i(TAG,"user_is_like : "+mFeedObj.getString("is_like"));//is user like
					Log.i(TAG,"user_is_false : "+mFeedObj.getString("is_false"));//is user false
					Log.i(TAG,"user_is_true : "+mFeedObj.getString("is_true"));// is user true
					Log.i(TAG,"created_time : "+ mFeedObj.getString("created_time"));// posted time
					Log.i(TAG,"status_true_count : " + mFeedObj.getString("true_count"));//post true count
					Log.i(TAG,"status_false_count : " +mFeedObj.getString("false_count"));//post false count
					Log.i(TAG,"Feed Obj : length : "+mFeed.size());//feed obj length
				}
			}
		}catch(JSONException e){
			Log.i(TAG, e.toString());
		}
		catch(Exception e){
			Log.i(TAG, e.toString());
		}
		return mFeed;
	}
}
