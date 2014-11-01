package com.netdoers.ui;

import org.json.JSONObject;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.netdoers.tellus.R;
import com.netdoers.utils.DBConstant;

public class GcmIntentService extends IntentService {
    
	public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;
    
    static final String TAG = "GCMService";
    static final String MESSAGE_TYPE_NOTIFICATION = "notification_comment";
    String notificationContent = null;
    String notificationTitle = null;

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

//        sendNotification("Received: " + extras.toString());
        Log.i(TAG, "Received: " + extras.toString());
        
        
        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM
             * will be extended in the future with new message types, just ignore
             * any message types you're not interested in, or that you don't
             * recognize.
             */
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) 
            {
                sendNotification("Send error: " + extras.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) 
            {
                sendNotification("Deleted messages on server: " +extras.toString());
            // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) 
            {
                // This loop represents the service doing some work.
                for (int i=0; i<5; i++) {
                    Log.i(TAG, "Working... " + (i+1)+ "/5 @ " + SystemClock.elapsedRealtime());
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                    }
                }
                Log.i(TAG, "Completed work @ " + SystemClock.elapsedRealtime());
                // Post notification of received message.
                sendNotification("Received: " + extras.toString());
                Log.i(TAG, "Received: " + extras.toString());
            }
            else if (MESSAGE_TYPE_NOTIFICATION.equalsIgnoreCase(messageType)) 
            {
                // This loop represents the service doing some work.
                
                Log.i(TAG, "Working..." + SystemClock.elapsedRealtime());
				try {
//					sendNotification("Received: " + extras.toString());
					String message = extras.getString("message");
					JSONObject msgJsonObject = new JSONObject(message);
					ContentValues values = new ContentValues();
					values.put(DBConstant.Notification_Columns.COLUMN_ID, System.currentTimeMillis());
					values.put(DBConstant.Notification_Columns.COLUMN_NOTIF_ID, msgJsonObject.getString("comment_id")); //notfication_id
					values.put(DBConstant.Notification_Columns.COLUMN_NOTIF_FROM_USER, msgJsonObject.getString("username"));//notifying_user
					values.put(DBConstant.Notification_Columns.COLUMN_NOTIF_FROM_USER_PATH, msgJsonObject.getString("post"));//notifying_user_image
//					values.put(DBConstant.Notification_Columns.COLUMN_NOTIF_TO, msgJsonObject.getString("notify_to"));
					values.put(DBConstant.Notification_Columns.COLUMN_NOTIF_PAGE, msgJsonObject.getString("post_id"));//notify_post_id
					values.put(DBConstant.Notification_Columns.COLUMN_NOTIF_WHAT, msgJsonObject.getString("action"));//notify_activity
					values.put(DBConstant.Notification_Columns.COLUMN_READ_STATUS, "0");
					getContentResolver().insert(DBConstant.Notification_Columns.CONTENT_URI, values);
					sendNotification(
							getResources().getString(R.string.app_name),
							msgJsonObject.getString("username")
									+ " "
									+ msgJsonObject
											.getString("action") + " "
									+ msgJsonObject.getString("post"),
							messageType);
				} 
				catch (Exception e){
				}
                Log.i(TAG, "Completed work @ " + SystemClock.elapsedRealtime());
                
                // Post notification of received message.
//                sendNotification("New Notification", notificationContent, "notification");
                Log.i(TAG, "Received: " + extras.toString());
            }
            
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        WakefulBroadcastReceiver.completeWakefulIntent(intent);
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(String title , String content , String where ) {
        mNotificationManager = (NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent contentIntent;
    	contentIntent = PendingIntent.getActivity(this, 0,new Intent(this, NotificationActivity.class), 0);	

        NotificationCompat.Builder mBuilder =new NotificationCompat.Builder(this)
        .setSmallIcon(R.drawable.ic_launcher)
//        .setContentTitle("GCM Notification")
        .setContentTitle(title)
        .setAutoCancel(true)
        .setStyle(new NotificationCompat.BigTextStyle()
        .bigText(title))
        .setContentText(content);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
    
    private void sendNotification(String msg) {
        mNotificationManager = (NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,new Intent(this, NotificationActivity.class), 0);	
        

        NotificationCompat.Builder mBuilder =new NotificationCompat.Builder(this)
        .setSmallIcon(R.drawable.ic_launcher)
//        .setContentTitle("GCM Notification")
        .setContentTitle("TellUs")
        .setAutoCancel(true)
        .setStyle(new NotificationCompat.BigTextStyle()
        .bigText(msg))
        .setContentText(msg);
        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

}
