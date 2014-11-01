/* HISTORY
 * CATEGORY 		:- ACTIVITY
 * DEVELOPER		:- VIKALP PATEL
 * AIM			    :- ADD IPD ACTIVITY
 * DESCRIPTION 		:- SAVE IPD
 * 
 * S - START E- END  C- COMMENTED  U -EDITED A -ADDED
 * --------------------------------------------------------------------------------------------------------------------
 * INDEX       DEVELOPER		DATE			FUNCTION		DESCRIPTION
 * --------------------------------------------------------------------------------------------------------------------
 * 1000B      VIKALP PATEL    07/02/2014        RELEASE         ADD VIDEO EXTENSION
 * 1000E      VIKALP PATEL    15/02/2014        RELEASE         ADDED PASS HASH IN JSON
 * --------------------------------------------------------------------------------------------------------------------
 */
package com.netdoers.utils;

import android.graphics.Color;
import android.os.Environment;

public class AppConstants {

	public interface URLS{
		//API V1
		public static final String URL_GET_COUNTRY           = "http://www.netdoers.com/projects/tellus/api/v1/autosuggest/countries";
		public static final String URL_POST_STATE            = "http://www.netdoers.com/projects/tellus/api/v1/autosuggest/states/country";
		public static final String URL_POST_CITY             = "http://www.netdoers.com/projects/tellus/api/v1/autosuggest/cities/states";
		public static final String URL_POST_REGISTRATION     = "http://www.netdoers.com/projects/tellus/api/v1/user/registration";
		public static final String URL_POST_PROFILE_PIC      = "http://www.netdoers.com/projects/tellus/api/v1/user/profilepic/update";
		public static final String URL_POST_GET_PROFILE_PIC  = "http://www.netdoers.com/projects/tellus/api/v1/user/getprofilepic";
		public static final String URL_POST_SIGN_IN          = "http://www.netdoers.com/projects/tellus/api/v1/user/login";
		public static final String URL_POST_PROFILE_UPDATE   = "http://www.netdoers.com/projects/tellus/api/v1/user/profile/update";
		public static final String URL_POST_PROFILE_VERIFY   = "http://www.netdoers.com/projects/tellus/api/v1/user/profile/update";
		
		public static final String URL_POST_CREATE_GROUP     = "http://www.netdoers.com/projects/tellus/api/v1/user/group/store";
		public static final String URL_POST_GROUP_UPLOAD     = "http://www.netdoers.com/projects/tellus/api/v1/user/group/grouppic/update";
		public static final String URL_POST_GROUPS           = "http://www.netdoers.com/projects/tellus/api/v1/user/group/suggest";
		public static final String URL_POST_GROUP_SUBSCRIBE  = "http://www.netdoers.com/projects/tellus/api/v1/user/groupsubscription/store";
		public static final String URL_POST_GROUP_FEED       = "http://www.netdoers.com/projects/tellus/api/v1/user/group/getfeeds";
		public static final String URL_POST_GROUP_SEARCH     = "http://www.netdoers.com/projects/tellus/api/v1/user/group/search";
		public static final String URL_POST_GROUP_WHILE_POST = "http://www.netdoers.com/projects/tellus/api/v1/user/group/search/subscribe";
		
		public static final String URL_POST_GROUP_ADMIN      = "http://www.netdoers.com/projects/tellus/api/v1/user/admingroups";
		public static final String URL_POST_GROUP_REPORT_FEED= "http://www.netdoers.com/projects/tellus/api/v1/user/group/post/getreportedfeeds";
		public static final String URL_POST_FEED_REPORT_ACT  = "http://www.netdoers.com/projects/tellus/api/v1/user/group/postacceptreport";
		public static final String URL_POST_FEED_REPORT_DEL  = "http://www.netdoers.com/projects/tellus/api/v1/user/group/postdelete";
		
			
		public static final String URL_POST_MEDIA_UPLOAD     = "http://www.netdoers.com/projects/tellus/api/v1/user/group/postresource";
		public static final String URL_POST_STATUS           = "http://www.netdoers.com/projects/tellus/api/v1/user/group/post";
		
		
		public static final String URL_POST_FEED_ALL         = "http://www.netdoers.com/projects/tellus/api/v1/user/feeds";
		public static final String URL_POST_FEED_FRIENDS     = "http://www.netdoers.com/projects/tellus/api/v1/user/friendfeeds";
		public static final String URL_POST_FEED_POPULAR     = "http://www.netdoers.com/projects/tellus/api/v1/user/popularfeeds";
		
		public static final String URL_POST_FEED_TRUE_FALSE  = "http://www.netdoers.com/projects/tellus/api/v1/user/group/posttruefalse";
		public static final String URL_POST_FEED_LIKE        = "http://www.netdoers.com/projects/tellus/api/v1/user/group/postlike";
		public static final String URL_POST_FEED_DETAIL      = "http://www.netdoers.com/projects/tellus/api/v1/user/group/post/getfeeds";
		public static final String URL_POST_FEED_COMMENT     = "http://www.netdoers.com/projects/tellus/api/v1/user/group/post/addcomment";
		public static final String URL_POST_FEED_REPORT      = "http://www.netdoers.com/projects/tellus/api/v1/user/group/postreport";
		
		public static final String URL_GCM			         = "http://www.netdoers.com/projects/tellus/api/v1/user/pushnotification/store";
		public static final String URL_POST_NOTI_SEEN        = "http://www.netdoers.com/projects/tellus/api/v1/user/postnotification/delete";
		
		public static final String URL_POST_SYNC_EMAIL       = "http://www.netdoers.com/projects/tellus/api/v1/user/inviteemails/store";
		public static final String URL_POST_SYNC_CONTACT     = "http://www.netdoers.com/projects/tellus/api/v1/user/invitecontacts/store";
		public static final String URL_POST_SYNC_SMS         = "http://www.netdoers.com/projects/tellus/api/v1/user/invitesms/store";
		
		public static final String URL_POST_FB_EXISTS        = "http://www.netdoers.com/projects/tellus/api/v1/user/facebook/exists";
		public static final String URL_POST_FB_FRIENDS       = "http://www.netdoers.com/projects/tellus/api/v1/user/facebook/addfriends";
	}
	
	public interface HEADERS{
		public static final String USER 	= "netdoersadmin";
		public static final String PASSWORD = "538f25fc32727";
	}
	
	public interface RESPONSES
	{
		public interface LoginResponse {
			public static String STATUS = "success";
			public static String VID = "user_id";
			public static String PASSHASH = "hash";
			public static String USERNAME = "email";
			public static String INFO = "info";
		}

		public interface ProjectsResponse {
			public static String PID = "pid";
			public static String PROJECT = "project";
			public static String COMPANY = "company";
		}

		public interface VendorProjectsResponse {
			public static String PID = "pid";
			public static String PROJECT = "project_name";
			public static String COMPANY = "client_name";
		}

		public interface PreviousImagesResponse {
			public static String PROJECT = "project";
			public static String WORK_TITLE = "work_title";
			public static String IMAGE = "image";
			public static String ADDRESS = "address";
			public static String CITY = "city";
			public static String STATE = "state";
			public static String SIZE = "size";
		}
	}
	
	public interface TAGS{
		public static final String GROUP_ID          = "group_id";
		public static final String GROUP_NAME        = "group_name";
		public static final String POST_ID           = "post_id";
		public static final String FEED_POSITION     = "feed_position";
		public static final String GROUP_IMAGE       = "group_image";
		public static final String GROUP_SUBSCRIBE   = "is_subscribe";
		public static final String GROUP_ADMIN       = "is_admin";
		public static final String WHATSAPP_ID       = "whatsapp_id";
	}
	
	public interface INTENT{
		public static final int FEED_COMMENT         = 10001;
		public static final String COMMENT_DATA      = "refresh_post";
	}
	
	public interface BROADCAST_ACTION{
		public static final String POST_SERVICE_MEDIA = "post_service_media";
	}
	
	public static final String NETWORK_NOT_AVAILABLE = "Network not available";
	public static final String IMAGE_DIRECTORY_PATH = Environment.getExternalStorageDirectory().getPath()+"/Tellus";
	public static final String IMAGE_DIRECTORY_PATH_DATA = ApplicationLoader.getApplication().getApplicationContext().getFilesDir().getAbsolutePath();
	public static final String EXTENSION = ".jpg";
	public static final String VIDEO_EXTENSION = ".mp4";
	public static final boolean DEBUG = false;
	public static final String WHATSAPP_DOMAIN = "@s.whatsapp.net";
	
	public static final String GREEN = "#006400";
	public static final String BLUE  = "#01AFD2";
	
}
