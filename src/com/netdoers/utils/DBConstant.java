package com.netdoers.utils;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class DBConstant {

	public static final String DB_NAME                          = "ApplicationDB";
	public static final String TABLE_NOTIFICATIONS 			    = "notification";
	
	
//	public static final Uri DISTINCT_CONTENT_URI = Uri.parse("content://"+ ZnameDB.AUTHORITY + "/contacts");
	public static class Notification_Columns implements BaseColumns
	{
		public static final Uri CONTENT_URI = Uri.parse("content://"+ ApplicationDB.AUTHORITY + "/notification");
		public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/notification";
		
		public static final String COLUMN_ID 							= "_id";
		public static final String COLUMN_NOTIF_ID 					    = "_notif_id";
		public static final String COLUMN_NOTIF_FROM_USER				= "_user_id";
		public static final String COLUMN_NOTIF_FROM_USER_PATH			= "_user_path";
		public static final String COLUMN_NOTIF_POST_ID                 = "post_id";
		public static final String COLUMN_NOTIF_WHAT      				= "_notif_what";
		public static final String COLUMN_NOTIF_TO 				        = "_notif_to";
		public static final String COLUMN_NOTIF_PAGE 				    = "_notif_page";
		public static final String COLUMN_READ_STATUS 					= "_read_status";
	}
}