package com.netdoers.utils;

import java.util.HashMap;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.media.audiofx.NoiseSuppressor;
import android.net.Uri;
import android.util.Log;

import com.netdoers.tellus.BuildConfig;

public class ApplicationDB extends ContentProvider{
	
	public static final String AUTHORITY = "com.netdoers.utils.ApplicationDB";
	
	private static final UriMatcher sUriMatcher;
	private static final String TAG = ApplicationDB.class.getSimpleName();
	
	private static final int NOTIFICATION = 1;
	
	private static HashMap<String, String> notificationProjectionMap;
	
	private static class OpenHelper extends SQLiteOpenHelper {

		OpenHelper(Context context) {
			super(context, DBConstant.DB_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			
			//allcontacts
			StringBuilder strBuilder = new StringBuilder();
			strBuilder.append("CREATE TABLE ");
			strBuilder.append(DBConstant.TABLE_NOTIFICATIONS);
			strBuilder.append('(');
			strBuilder.append(DBConstant.Notification_Columns.COLUMN_ID +" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," );
			strBuilder.append(DBConstant.Notification_Columns.COLUMN_NOTIF_ID +" TEXT UNIQUE," );
			strBuilder.append(DBConstant.Notification_Columns.COLUMN_NOTIF_PAGE +" TEXT ," );
			strBuilder.append(DBConstant.Notification_Columns.COLUMN_NOTIF_TO +" TEXT ," );
			strBuilder.append(DBConstant.Notification_Columns.COLUMN_NOTIF_POST_ID +" TEXT ," );
			strBuilder.append(DBConstant.Notification_Columns.COLUMN_NOTIF_WHAT +" TEXT ," );
			strBuilder.append(DBConstant.Notification_Columns.COLUMN_NOTIF_FROM_USER +" TEXT ," );
			strBuilder.append(DBConstant.Notification_Columns.COLUMN_NOTIF_FROM_USER_PATH +" TEXT ," );
			strBuilder.append(DBConstant.Notification_Columns.COLUMN_READ_STATUS +" NUMBER" );
			strBuilder.append(')');
			db.execSQL(strBuilder.toString());
			if (BuildConfig.DEBUG) {
				Log.i(TAG, strBuilder.toString());
			}
		}

		
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + DBConstant.TABLE_NOTIFICATIONS);
			
			onCreate(db);
		}
	}

	/* VERSION      DATABASE_VERSION      MODIFIED            BY
	 * ----------------------------------------------------------------
	 * V 0.0.1             1              16/05/14        VIKALP PATEL
	 * -----------------------------------------------------------------
	 */
	private static final int DATABASE_VERSION = 1;
		
	OpenHelper openHelper;


	@Override
	public int delete(Uri uri, String where, String[] whereArgs) {
		// TODO Auto-generated method stub
		SQLiteDatabase db = openHelper.getWritableDatabase();
		int count;
		switch (sUriMatcher.match(uri)) {
		case NOTIFICATION:
			count = db.delete(DBConstant.TABLE_NOTIFICATIONS, where, whereArgs);
			break;
		
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}


	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		switch (sUriMatcher.match(uri)) {
		case NOTIFICATION:
			return DBConstant.Notification_Columns.CONTENT_TYPE;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}


	@Override
	public Uri insert(Uri uri, ContentValues initialValues) {
		// TODO Auto-generated method stub
		if (sUriMatcher.match(uri) != NOTIFICATION) 
		{ 
			throw new IllegalArgumentException("Unknown URI " + uri); 
		}
		
		ContentValues values;
		if (initialValues != null) {
			values = new ContentValues(initialValues);
		} 
		else {
			values = new ContentValues();
		}
		
		SQLiteDatabase db = openHelper.getWritableDatabase();
		long rowId = 0;
		
		switch (sUriMatcher.match(uri)) 
		{
			case NOTIFICATION:
				 rowId = db.insertWithOnConflict(DBConstant.TABLE_NOTIFICATIONS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
				if (rowId > 0) 
				{
					Uri noteUri = ContentUris.withAppendedId(DBConstant.Notification_Columns.CONTENT_URI, rowId);
					getContext().getContentResolver().notifyChange(noteUri, null);
					return noteUri;
				}
				break;
				
			default:
				throw new IllegalArgumentException("Unknown URI " + uri);
				
		}
		throw new SQLException("Failed to insert row into " + uri);
	}


	@Override
	public boolean onCreate() {
		// TODO Auto-generated method stub
		openHelper 		= new OpenHelper(getContext());
		return true;
	}


	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		// TODO Auto-generated method stub
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		switch (sUriMatcher.match(uri)) {
		case NOTIFICATION:
			qb.setTables(DBConstant.TABLE_NOTIFICATIONS);
			qb.setProjectionMap(notificationProjectionMap);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
//		SQLiteDatabase db = openHelper.getReadableDatabase();
		SQLiteDatabase db = openHelper.getWritableDatabase();
		Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}


	@Override
	public int update(Uri uri, ContentValues values, String where, String[] whereArgs) {
		// TODO Auto-generated method stub
		SQLiteDatabase db = openHelper.getWritableDatabase();
		int count = -1;
		switch (sUriMatcher.match(uri)) {
		case NOTIFICATION:
			count = db.update(DBConstant.TABLE_NOTIFICATIONS, values, where, whereArgs);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}
	
	static {
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sUriMatcher.addURI(AUTHORITY, DBConstant.TABLE_NOTIFICATIONS, NOTIFICATION);

		notificationProjectionMap = new HashMap<String, String>();
		notificationProjectionMap.put(DBConstant.Notification_Columns.COLUMN_ID, DBConstant.Notification_Columns.COLUMN_ID);
		notificationProjectionMap.put(DBConstant.Notification_Columns.COLUMN_NOTIF_ID, DBConstant.Notification_Columns.COLUMN_NOTIF_ID);
		notificationProjectionMap.put(DBConstant.Notification_Columns.COLUMN_NOTIF_PAGE, DBConstant.Notification_Columns.COLUMN_NOTIF_PAGE);
		notificationProjectionMap.put(DBConstant.Notification_Columns.COLUMN_NOTIF_TO, DBConstant.Notification_Columns.COLUMN_NOTIF_TO);
		notificationProjectionMap.put(DBConstant.Notification_Columns.COLUMN_NOTIF_POST_ID, DBConstant.Notification_Columns.COLUMN_NOTIF_POST_ID);
		notificationProjectionMap.put(DBConstant.Notification_Columns.COLUMN_NOTIF_WHAT, DBConstant.Notification_Columns.COLUMN_NOTIF_WHAT);
		notificationProjectionMap.put(DBConstant.Notification_Columns.COLUMN_NOTIF_FROM_USER, DBConstant.Notification_Columns.COLUMN_NOTIF_FROM_USER);
		notificationProjectionMap.put(DBConstant.Notification_Columns.COLUMN_NOTIF_FROM_USER_PATH, DBConstant.Notification_Columns.COLUMN_NOTIF_FROM_USER_PATH);
		notificationProjectionMap.put(DBConstant.Notification_Columns.COLUMN_READ_STATUS, DBConstant.Notification_Columns.COLUMN_READ_STATUS);
		}	
}
