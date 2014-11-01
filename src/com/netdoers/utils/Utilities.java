package com.netdoers.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.sql.Date;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.model.GraphUser;
import com.netdoers.tellus.R;

@SuppressLint("DefaultLocale")
public class Utilities {
	public static boolean isRTL = false;
	public static Point displaySize = new Point();
	private static int[] barPers = new int[2];
	private static final String TAG = Utilities.class.getSimpleName();

	public static void checkRTL() {
		Locale locale = Locale.getDefault();
		String lang = locale.getLanguage();
		if (lang == null) {
			lang = "en";
		}
		isRTL = lang.toLowerCase().equals("ar");
	}

	public static CharSequence highlight(String search, String originalText) {
		// ignore case and accents
		// the same thing should have been done for the search text
		search = search.toLowerCase();
		String normalizedText = Normalizer
				.normalize(originalText, Normalizer.Form.NFD)
				.replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
				.toLowerCase();

		int start = normalizedText.indexOf(search);
		if (start < 0) {
			// not found, nothing to to
			return originalText;
		} else {
			// highlight each appearance in the original text
			// while searching in normalized text
			Spannable highlighted = new SpannableString(originalText);
			while (start >= 0) {
				int spanStart = Math.min(start, originalText.length());
				int spanEnd = Math.min(start + search.length(),
						originalText.length());

				highlighted.setSpan(new BackgroundColorSpan(Color.YELLOW),
						spanStart, spanEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

				start = normalizedText.indexOf(search, spanEnd);
			}

			return highlighted;
		}
	}

	public static String getDeviceName() {
		try {
			String manufacturer = Build.MANUFACTURER;
			String model = Build.MODEL;
			if (model.startsWith(manufacturer)) {
				return capitalize(model);
			} else {
				return capitalize(manufacturer) + " " + model;
			}
		} catch (Exception e) {
			return "Device Unidentified";
		}
	}

	public static String capitalize(String s) {
		try {
			if (s == null || s.length() == 0) {
				return "";
			}
			char first = s.charAt(0);
			if (Character.isUpperCase(first)) {
				return s;
			} else {
				return Character.toUpperCase(first) + s.substring(1);
			}

		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	public static double checkDisplaySize() {
        try {
            WindowManager manager = (WindowManager)ApplicationLoader.applicationContext.getSystemService(Context.WINDOW_SERVICE);
            if (manager != null) {
                Display display = manager.getDefaultDisplay();
                if (display != null) {
                    if(android.os.Build.VERSION.SDK_INT < 13) {
                        displaySize.set(display.getWidth(), display.getHeight());
                    } else {
                        display.getSize(displaySize);
                    }
                    FileLog.e("tmessages", "display size = " + displaySize.x + " " + displaySize.y);
                }
            }
            return Math.sqrt((displaySize.x * displaySize.x) + (displaySize.y * displaySize.y));
        } catch (Exception e) {
            FileLog.e("tmessages", e);
            return 0;
        }
    }
	
	public static int getTrueFalsePer(int trueCount, int falseCount, int trueAdd, int falseAdd){
		int total = trueCount + falseCount + trueAdd + falseAdd;
		int truePer = 0;
		try {
			 truePer = ((trueCount + trueAdd) * 100) / total;
		} catch (ArithmeticException ae) {
			Log.e(TAG, ae.toString());
		}catch(Exception e){
			Log.e(TAG, e.toString());
		}
		return truePer;
	}
	
	
	public static Uri getImagePath() {
		File imageDirectory = null;
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			imageDirectory = new File(AppConstants.IMAGE_DIRECTORY_PATH);
		} else {
			imageDirectory = new File(AppConstants.IMAGE_DIRECTORY_PATH_DATA);
		}
		imageDirectory.mkdirs();
		File tempFile = new File(imageDirectory, getVideoName()+ AppConstants.EXTENSION);
		return Uri.fromFile(tempFile);
	}
	
	public static Uri getVideoPath() {
		File imageDirectory = null;
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			imageDirectory = new File(AppConstants.IMAGE_DIRECTORY_PATH);
		} else {
			imageDirectory = new File(AppConstants.IMAGE_DIRECTORY_PATH_DATA);
		}

		imageDirectory.mkdirs();
		File tempFile = new File(imageDirectory, getVideoName()
				+ AppConstants.VIDEO_EXTENSION);
		return Uri.fromFile(tempFile);
	}
	
	public static String getVideoName() {
		String name = "Utilities";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		try {
			name = sdf.format(new Date(System.currentTimeMillis()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return name;
	}
	
	public static void copy(File src, File dst) throws IOException {
		InputStream in = new FileInputStream(src);
		OutputStream out = new FileOutputStream(dst);

		// Transfer bytes from in to out
		byte[] buf = new byte[1024];
		int len;
		while ((len = in.read(buf)) > 0) {
			out.write(buf, 0, len);
		}
		in.close();
		out.close();
	}
	
	public static void galleryAddPic(Context mContext, Uri currentFileUri) {
	    Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
	    File f = new File(currentFileUri.getPath());
	    Uri contentUri = Uri.fromFile(f);
	    mediaScanIntent.setData(contentUri);
	    mContext.sendBroadcast(mediaScanIntent);
	}
	
	public static boolean isInternetConnected() {
	    ConnectivityManager connectivityManager = (ConnectivityManager) ApplicationLoader.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
	}
	
	public static Bitmap getBitmapFromUri(Uri uri) throws FileNotFoundException,
			IOException {
		InputStream input = ApplicationLoader.applicationContext.getContentResolver().openInputStream(uri);
		BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
		bitmapOptions.inDither = true;
		bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;
		input = ApplicationLoader.applicationContext.getContentResolver().openInputStream(uri);
		Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
		input.close();
		return bitmap;
	}
	
	public static String getDeviceId() {
		TelephonyManager telephonyManager = (TelephonyManager) ApplicationLoader.getApplication().getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
		return telephonyManager.getDeviceId();
	}
	
	public static String getSDKVersion(){
		return String.valueOf(Build.VERSION.SDK_INT);
	}
	
	public static String getApplicationVersion(){
		PackageInfo pInfo = null;
		try {
			pInfo = ApplicationLoader.getApplication().getApplicationContext().getPackageManager().getPackageInfo(ApplicationLoader.getApplication().getApplicationContext().getPackageName(), 0);
			return pInfo.versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "VERSION NAME NOT FOUND";
		}
	}
	
	
	public static String readFile(String s) {
		BufferedReader r;
		StringBuilder str = new StringBuilder();
		try {
			r = new BufferedReader(new InputStreamReader(ApplicationLoader
					.getApplication().getApplicationContext().getAssets()
					.open(s)));
			String line;
			while ((line = r.readLine()) != null) {
				str.append(line);
			}
		} catch (IOException e) { // TODO Auto-generated catch block
			e.printStackTrace();
		}

		return str.toString();
	}
	
	@SuppressWarnings("deprecation")
	public static int getDeviceWidth(){
		WindowManager wm = (WindowManager) ApplicationLoader.applicationContext.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		return display.getWidth();
	}
	
	@SuppressWarnings("deprecation")
	public static int getDeviceHeight(){
		WindowManager wm = (WindowManager) ApplicationLoader.applicationContext.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		return display.getHeight();
	}
	
	public static int getDeviceGalleryFitWidth(){
		return (int) (getDeviceWidth() * 1.47);
	}
	
	public static String toHtml(Object object) {
		StringBuilder stringBuilder = new StringBuilder(256);
		try {
			for (Field field : object.getClass().getDeclaredFields()) {
				field.setAccessible(true);
				Object val = field.get(object);
				stringBuilder.append("<b>");
				stringBuilder.append(field.getName().substring(1, field.getName().length()));
				stringBuilder.append(": ");
				stringBuilder.append("</b>");
				stringBuilder.append(val);
				stringBuilder.append("<br>");
			}
		} catch (Exception e) {
			// Do nothing
		}
		return stringBuilder.toString();
	}
	
	public static void hideSoftKeyboard(Activity activity) {
		InputMethodManager inputMethodManager = (InputMethodManager) activity
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
	}
	
	public static void logUserInfo(){
		try{
			Log.i(TAG, "Api key :"+ApplicationLoader.getPreferences().getApiKey());
			Log.i(TAG, "UserName:"+ApplicationLoader.getPreferences().getUserName());
			Log.i(TAG, "number  :"+ApplicationLoader.getPreferences().getUserNumber());
			Log.i(TAG, "gender  :"+ApplicationLoader.getPreferences().getUserGender());
			Log.i(TAG, "city_id :"+ApplicationLoader.getPreferences().getUserCityId());
			Log.i(TAG, "city    :"+ApplicationLoader.getPreferences().getUserCity());
			Log.i(TAG, "state_id:"+ApplicationLoader.getPreferences().getUserStateId());
			Log.i(TAG, "state   :"+ApplicationLoader.getPreferences().getUserState());
			Log.i(TAG, "country_:"+ApplicationLoader.getPreferences().getUserCountryId());
			Log.i(TAG, "country :"+ApplicationLoader.getPreferences().getUserCountry());
			Log.i(TAG, "pic     :"+ApplicationLoader.getPreferences().getProfilePicPath());
		}catch(Exception e){
			Log.i(TAG, e.toString());
		}
	}
	
	public static void logUserFacebookInfo(GraphUser user){
		Log.i(TAG,"User ID "+ user.getId());
        Log.i(TAG,"Username "+ user.asMap().get("username"));
        Log.i(TAG,"Email "+ user.asMap().get("email"));
        Log.i(TAG,"Name "+ user.asMap().get("name"));
        Log.i(TAG,"First Name "+ user.asMap().get("first_name"));
        Log.i(TAG,"Last Name "+ user.asMap().get("last_name"));
        Log.i(TAG,"Gender "+ user.asMap().get("gender"));
        Log.i(TAG,"Location "+ user.asMap().get("location"));
	}
	
	public static void errorApi(RelativeLayout mNoDataLayout, TextView mNoDataTxt){
		mNoDataLayout.setVisibility(View.VISIBLE);
 	    mNoDataTxt.setText(ApplicationLoader.getApplication().getResources().getString(R.string.error_api));
	}
	
	public static void errorNoData(RelativeLayout mNoDataLayout, TextView mNoDataTxt){
		mNoDataLayout.setVisibility(View.VISIBLE);
 	    mNoDataTxt.setText(ApplicationLoader.getApplication().getResources().getString(R.string.error_no_data));
	}
	
	public static void errorApiGone(RelativeLayout mNoDataLayout, TextView mNoDataTxt){
		mNoDataLayout.setVisibility(View.GONE);
 	    mNoDataTxt.setText(ApplicationLoader.getApplication().getResources().getString(R.string.error_no_data));
	}
	
	public static void errorNoDataGone(RelativeLayout mNoDataLayout, TextView mNoDataTxt){
		mNoDataLayout.setVisibility(View.GONE);
 	    mNoDataTxt.setText(ApplicationLoader.getApplication().getResources().getString(R.string.error_no_data));
	}
	
	public static int getNotificationCount(){
		return ApplicationLoader
				.getApplication()
				.getContentResolver()
				.query(DBConstant.Notification_Columns.CONTENT_URI,
						null,
						DBConstant.Notification_Columns.COLUMN_READ_STATUS+ "=?",  new String[]{"0"}, null).getCount();
	}
}
