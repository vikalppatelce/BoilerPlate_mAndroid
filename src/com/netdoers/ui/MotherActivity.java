/* HISTORY
 * CATEGORY			 :- BASE ACTIVITY 
 * DEVELOPER		 :- VIKALP PATEL
 * AIM      		 :- PROVIDE BASE FOR TABS AND DRAWER ON TOP OF IT
 * NOTE: HANDLE WITH CARE 
 * 
 * S - START E- END  C- COMMENTED  U -EDITED A -ADDED
 * --------------------------------------------------------------------------------------------------------------------
 * INDEX       DEVELOPER		DATE			FUNCTION		DESCRIPTION
 * --------------------------------------------------------------------------------------------------------------------
 * TU001      VIKALP PATEL     29/07/2014                       CREATED
 * --------------------------------------------------------------------------------------------------------------------
 * 
 * *****************************************METHODS INFORMATION******************************************************** 
 * ********************************************************************************************************************
 * DEVELOPER		  METHOD								DESCRIPTION
 * ********************************************************************************************************************
 * VIKALP PATEL                          			
 * ********************************************************************************************************************
 *
 */

package com.netdoers.ui;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.OnNavigationListener;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.netdoers.beans.Feed;
import com.netdoers.tellus.BuildConfig;
import com.netdoers.tellus.R;
import com.netdoers.ui.view.CircleImageView;
import com.netdoers.ui.view.PagerSlidingTabStrip;
import com.netdoers.ui.view.ViewPagerAdapter;
import com.netdoers.utils.AppConstants;
import com.netdoers.utils.ApplicationLoader;
import com.netdoers.utils.DBConstant;
import com.netdoers.utils.NetworkVolley;
import com.netdoers.utils.Utilities;
import com.netdoers.utils.NetworkVolley.VolleyPostJsonRequest;
import com.netdoers.utils.RequestBuilder;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class MotherActivity extends ActionBarActivity implements IActivityCommunicator{

	public static final String TAG = MotherActivity.class.getSimpleName();

	private ViewPager mPager;
	private PagerSlidingTabStrip mPagerSlidingTabStrp;
	private ViewPager.SimpleOnPageChangeListener ViewPagerListener;

	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;

	private CharSequence mDrawerTitle;
	private CharSequence mTitle;
	private String[] mDrawerTitles;
	private String[] mDrawerDetailTitles;

	DrawerArrayAdapter mDrawerAdapter;

	private ImageLoader imageLoader;
	private DisplayImageOptions options;
	
	private static Button notifCount;
	private static int mNotifCount = 0;
	private static boolean mActionBarMenuShouldGoInvisible = false;
	
	private SpinnerAdapter mSpinnerAdapter;
	
	public IFragmentCommunicator mFragmentCommunicator;
	public IFragmentPopularCommunicator mFragmentPopularCommunicator;
	public IFragmentFriendsCommunicator mFragmentFriendsCommunicator;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mother);
		initUi();
		setUniversalImageLoader();
		setPagerSlidingTabStrip();
		setDrawerLayout();
		volleyProfilePicture();
		checkRegisteredGCMToServer();
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_mother, menu);
		MenuItem item = menu.findItem(R.id.action_notification);
		MenuItemCompat.setActionView(item, R.layout.menu_actionbar_badge_layout);
//		TextView notifCount = (TextView) MenuItemCompat.getActionView(item);
		FrameLayout notifCountLayout = (FrameLayout) MenuItemCompat.getActionView(item);
		TextView notifCount = (TextView) notifCountLayout.findViewById(R.id.notif_count);
		notifCount.setText(String.valueOf(mNotifCount));
		
		/*if (mNotifCount == 0) {
			notifCount.setText("");
			notifCount.setBackgroundResource(R.drawable.shape_notification_red);
		} else {
			notifCount.setBackgroundResource(R.drawable.shape_notification_blue);			
		}*/
		
		notifCount.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent mNotifIntent = new Intent(MotherActivity.this, NotificationActivity.class);
				startActivity(mNotifIntent);
			}
		});
		
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			try {
				if (mDrawerLayout.isDrawerOpen(mDrawerList)) {
					mDrawerLayout.closeDrawer(mDrawerList);
				} else {
					mDrawerLayout.openDrawer(mDrawerList);
				}
			} catch (Exception e) {
				Log.e(TAG, e.toString());
			}
			return true;
			
		case R.id.action_status:
			Intent mIntent = new Intent(MotherActivity.this, PostActivity.class);
			startActivity(mIntent);
			return true;
		case R.id.action_notification:
			Intent mNotifIntent = new Intent(MotherActivity.this, NotificationActivity.class);
			startActivity(mNotifIntent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
	    // If the nav drawer is open, hide action items related to the content view
	    boolean drawerOpen = mActionBarMenuShouldGoInvisible;
	    hideMenuItems(menu, !drawerOpen);
	    return super.onPrepareOptionsMenu(menu);
	}
	
	private void hideMenuItems(Menu menu, boolean visible){
	    for(int i = 0; i < menu.size(); i++){
	        menu.getItem(i).setVisible(visible);
	    }
	    
	    if(visible)
	    	loadActionBarTitle();
	}
	
	private void checkRegisteredGCMToServer(){
		if(!ApplicationLoader.getPreferences().getRegisteredGCMToServer())
			ApplicationLoader.postInitApplication();
	}
	
	private void loadActionBarTitle(){
		switch(mPager.getCurrentItem()){
		case 0:
			setActionBarTitle(getResources().getString(R.string.str_actionbar_fragment_feed_all), true);
			break;
		case 1:
			setActionBarTitle(getResources().getString(R.string.str_actionbar_fragment_feed_friends), false);
			break;
		case 2:
			setActionBarTitle(getResources().getString(R.string.str_actionbar_fragment_feed_recent), false);
			break;
		case 3:
			setActionBarTitle(getResources().getString(R.string.str_actionbar_fragment_feed_popular), false);
			break;
		}
	}
	private void setNotifCount(int count){
	    mNotifCount = count;
	    supportInvalidateOptionsMenu();
	}

	private void initUi() {
		mPager = (ViewPager) findViewById(R.id.pager);
		mPagerSlidingTabStrp = (PagerSlidingTabStrip) findViewById(R.id.pager_sliding_tab_strip);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);
	}

	private void setPagerSlidingTabStrip() {
		ViewPagerAdapter viewpageradapter = new ViewPagerAdapter(
				getSupportFragmentManager());
//		mPager.setOffscreenPageLimit(4);
		mPager.setOffscreenPageLimit(3);
		mPager.setAdapter(viewpageradapter);
		mPagerSlidingTabStrp.setShouldExpand(true);
		mPagerSlidingTabStrp.setViewPager(mPager);
		setViewPagerListener();
		mPagerSlidingTabStrp.setOnPageChangeListener(ViewPagerListener);
		setActionBarTitle(getResources().getString(R.string.str_actionbar_fragment_feed_all), true);
	}

	private void setDrawerLayout() {
		mTitle = mDrawerTitle = getTitle();
		mDrawerTitles = getResources()
				.getStringArray(R.array.drawer_menu_array);
		mDrawerDetailTitles = getResources().getStringArray(
				R.array.drawer_menu_detail_array);
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);
		mDrawerAdapter = new DrawerArrayAdapter(this, mDrawerTitles,
				mDrawerDetailTitles, imageLoader, options);
		mDrawerList.setAdapter(mDrawerAdapter);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_drawer, R.string.drawer_open,
				R.string.drawer_close) {
			@Override
			public void onDrawerClosed(View view) {
				getSupportActionBar().setTitle(mTitle);
				mActionBarMenuShouldGoInvisible = false;
				supportInvalidateOptionsMenu();
			}

			@Override
			public void onDrawerOpened(View drawerView) {
//				getSupportActionBar().setDisplayShowTitleEnabled(true);
//				getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
				getSupportActionBar().setTitle(mDrawerTitle);
				mActionBarMenuShouldGoInvisible = true;
				supportInvalidateOptionsMenu();
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
	}

	private void setUniversalImageLoader() {
		imageLoader = ImageLoader.getInstance();
		imageLoader.init(ImageLoaderConfiguration.createDefault(this));
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable._def_contact)
				.showImageForEmptyUri(R.drawable._def_contact)
				.showImageOnFail(R.drawable._def_contact).cacheInMemory()
				.cacheOnDisc().build();
	}

	private void setViewPagerListener(){
		ViewPagerListener = new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				super.onPageSelected(position);
				// Find the ViewPager Position
				switch (position) {
				case 0:
					setActionBarTitle(getString(R.string.str_actionbar_fragment_feed_all), true);
					break;
				case 1:
					setActionBarTitle(getString(R.string.str_actionbar_fragment_feed_friends), false);
					break;
				case 2:
					setActionBarTitle(getString(R.string.str_actionbar_fragment_feed_popular), false);
					break;
				/*case 2:
					setActionBarTitle(getString(R.string.str_actionbar_fragment_feed_recent), false);
					break;
				case 3:
					setActionBarTitle(getString(R.string.str_actionbar_fragment_feed_popular), false);
					break;*/
				}
			}
		};
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (requestCode == AppConstants.INTENT.FEED_COMMENT) {
				Feed mFeedRefreshed = data.getExtras().getParcelable(
						AppConstants.INTENT.COMMENT_DATA);
				int mPosition = Integer.parseInt(data.getExtras().getString(AppConstants.TAGS.FEED_POSITION));
				Log.i(TAG, "onActivityResult");
				Log.i(TAG, "mPager:Current" +mPager.getCurrentItem());
				Log.i(TAG, "mFragmentCommunicator" +mFragmentCommunicator);
				switch (mPager.getCurrentItem()) {
				case 0:
					mFragmentCommunicator.passFeedDataToFragment(mFeedRefreshed, mPosition,"ALL");
					break;
				case 1:
					mFragmentFriendsCommunicator.passFeedDataToFragment(mFeedRefreshed, mPosition,"FRIENDS");
					break;
				case 2:
					mFragmentPopularCommunicator.passFeedDataToFragment(mFeedRefreshed, mPosition, "POPULAR");
					break;
				}
			}
		}
	}
	
	private void setActionBarTitle(String str, boolean isList){
			getSupportActionBar().setTitle(str);
		/*if(isList){
			setActionBarNavigationModeList();
		}else{
			getSupportActionBar().setDisplayShowTitleEnabled(true);
			getSupportActionBar().setTitle(str);
			getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		}*/
	}
	
	private void setActionBarNavigationModeList(){
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		mSpinnerAdapter = ArrayAdapter.createFromResource(this,R.array.actionbar_list_array, android.R.layout.simple_dropdown_item_1line);
		getSupportActionBar().setListNavigationCallbacks(mSpinnerAdapter, new OnNavigationListener() {
			@Override
			public boolean onNavigationItemSelected(int position, long itemId) {
				// TODO Auto-generated method stub
//				mFragmentCommunicator.passDataToFragment(String.valueOf(position));
				return true;
			}
		});
	}

	/* The click listner for ListView in the navigation drawer */
	private class DrawerItemClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			selectItem(position);
		}
	}

	private void selectItem(int position) {
		Fragment fragment = null;
		Bundle args = new Bundle();
		Intent drawerIntent = null;
		switch (position) {
		case 0:
			drawerIntent = new Intent(MotherActivity.this, ProfileActivity.class);
			break;
		case 1:
			drawerIntent = new Intent(MotherActivity.this, ProfileActivity.class);
			break;
		case 2:
			drawerIntent = new Intent(MotherActivity.this, GroupActivity.class);
			break;
		case 3:
			drawerIntent = new Intent(MotherActivity.this, CreateGroupActivity.class);
			break;
		case 4:
			drawerIntent = new Intent(MotherActivity.this, ManageGroupsActivity.class);
			break;
		case 5:
			drawerIntent = new Intent(MotherActivity.this, SubscribedGroupsActivity.class);
			break;
		case 6:
			drawerIntent = new Intent(MotherActivity.this, PrefsActivity.class);
			break;
		case 7:
			/*drawerIntent = new Intent(Intent.ACTION_SEND);
			drawerIntent.setType("text/plain");
			drawerIntent
					.putExtra(
							Intent.EXTRA_TEXT,
							"Catch person call status using Z:name \n\n"
									+ "https://play.google.com/store/apps/details?id=com.netdoers.zname");*/
			
			drawerIntent = new Intent(MotherActivity.this, InviteActivity.class);
			break;
		case 8:
			drawerIntent = new Intent(MotherActivity.this, FeedBackActivity.class);
			break;
		default:
			break;
		}
		mDrawerLayout.closeDrawer(mDrawerList);
		if (drawerIntent != null)
			startActivity(drawerIntent);
	}

	public class DrawerArrayAdapter extends ArrayAdapter<String> {
		private final Context context;
		private final String[] values;
		private final String[] values2;
		private ImageLoader imageLoader;
		private DisplayImageOptions options;

		public DrawerArrayAdapter(Context context, String[] values,
				String[] values2, ImageLoader imageLoader,
				DisplayImageOptions options) {
			super(context, R.layout.item_list_drawer, values);
			this.context = context;
			this.values = values;
			this.values2 = values2;
			this.imageLoader = imageLoader;
			this.options = options;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View rowView = inflater.inflate(R.layout.item_list_drawer, parent,
					false);

			FrameLayout mDrawerProfileLayout = (FrameLayout) rowView
					.findViewById(R.id.drawer_layout_profile);
			LinearLayout mDrawerMenuLayout = (LinearLayout) rowView
					.findViewById(R.id.drawer_layout_menu);

			final CircleImageView mDrawerProfileImageView = (CircleImageView) rowView
					.findViewById(R.id.drawer_profile_image);

			TextView mDrawerProfileFullNameView = (TextView) rowView
					.findViewById(R.id.drawer_profile_full_name);
			TextView mDrawerProfilePostCountView = (TextView) rowView
					.findViewById(R.id.drawer_profile_post_count);
			TextView mDrawerProfileGroupCountView = (TextView) rowView
					.findViewById(R.id.drawer_profile_groups_count);
			TextView mDrawerTitleView = (TextView) rowView
					.findViewById(R.id.drawer_layout_menu_title);
			TextView mDrawerTitleSubView = (TextView) rowView
					.findViewById(R.id.drawer_layout_menu_subtitle);
			ImageView mDrawerIconView = (ImageView) rowView
					.findViewById(R.id.drawer_layout_menu_icon);

			switch (position) {
			case 0:
				mDrawerMenuLayout.setVisibility(View.GONE);
				mDrawerProfileFullNameView.setText(ApplicationLoader.getPreferences().getUserName());
				mDrawerProfileGroupCountView.setText(ApplicationLoader.getPreferences().getProfileGroupCount());
				mDrawerProfilePostCountView.setText(ApplicationLoader.getPreferences().getProfilePostCount());
				if (!TextUtils.isEmpty(ApplicationLoader.getPreferences()
						.getProfilePicPath())) {
					((Activity) context).runOnUiThread(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							imageLoader.displayImage(ApplicationLoader.getPreferences()
									.getProfilePicPath(),
									mDrawerProfileImageView, options);
						}
					});
				}
				break;
			case 1:
				mDrawerProfileLayout.setVisibility(View.GONE);
				mDrawerMenuLayout.setVisibility(View.VISIBLE);
				mDrawerIconView
						.setImageResource(R.drawable.drawer_profile);
				mDrawerTitleView.setText(values[position]);
				mDrawerTitleSubView.setText(values2[position]);
				break;
			case 2:
				mDrawerProfileLayout.setVisibility(View.GONE);
				mDrawerMenuLayout.setVisibility(View.VISIBLE);
				mDrawerIconView
						.setImageResource(R.drawable.drawer_groups);
				mDrawerTitleView.setText(values[position]);
				mDrawerTitleSubView.setText(values2[position]);
				break;
			case 3:
				mDrawerProfileLayout.setVisibility(View.GONE);
				mDrawerMenuLayout.setVisibility(View.VISIBLE);
				mDrawerIconView
						.setImageResource(R.drawable.drawer_group_create);
				mDrawerTitleView.setText(values[position]);
				mDrawerTitleSubView.setText(values2[position]);
				break;
			case 4:
				mDrawerProfileLayout.setVisibility(View.GONE);
				mDrawerMenuLayout.setVisibility(View.VISIBLE);
				mDrawerIconView
						.setImageResource(R.drawable.drawer_groups_manage);
				mDrawerTitleView.setText(values[position]);
				mDrawerTitleSubView.setText(values2[position]);
				break;
			case 5:
				mDrawerProfileLayout.setVisibility(View.GONE);
				mDrawerMenuLayout.setVisibility(View.VISIBLE);
				mDrawerIconView
						.setImageResource(R.drawable.drawer_group_subscribed);
				mDrawerTitleView.setText(values[position]);
				mDrawerTitleSubView.setText(values2[position]);
				break;
			case 6:
				mDrawerProfileLayout.setVisibility(View.GONE);
				mDrawerMenuLayout.setVisibility(View.VISIBLE);
				mDrawerIconView
						.setImageResource(R.drawable.drawer_setting);
				mDrawerTitleView.setText(values[position]);
				mDrawerTitleSubView.setText(values2[position]);
				break;
			case 7:
				mDrawerProfileLayout.setVisibility(View.GONE);
				mDrawerMenuLayout.setVisibility(View.VISIBLE);
				mDrawerIconView
						.setImageResource(R.drawable.drawer_invite_friends);
				mDrawerTitleView.setText(values[position]);
				mDrawerTitleSubView.setText(values2[position]);
				break;
			case 8:
				mDrawerProfileLayout.setVisibility(View.GONE);
				mDrawerMenuLayout.setVisibility(View.VISIBLE);
				mDrawerIconView
						.setImageResource(R.drawable.drawer_feedback);
				mDrawerTitleView.setText(values[position]);
				mDrawerTitleSubView.setText(values2[position]);
				break;
			default:
				break;
			}
			return rowView;
		}
	}

//	@Override
//	public void passDataToActivity(String someValue) {
//		// TODO Auto-generated method stub
//	}
	
	@Override
	public void passFeedDataToActivity(Feed mFeed, int mPosition,String someValue) {
		// TODO Auto-generated method stub
	}

	
	private void volleyProfilePicture() {
		final String TAG = "volleyProfilePicture";
		NetworkVolley nVolley = new NetworkVolley();
		JSONObject mJSONObject = RequestBuilder.getPostApiData();
		Log.i(TAG, AppConstants.URLS.URL_POST_GET_PROFILE_PIC);
		Log.i(TAG, mJSONObject.toString());
		VolleyPostJsonRequest req = nVolley.new VolleyPostJsonRequest(AppConstants.URLS.URL_POST_GET_PROFILE_PIC, mJSONObject,
				new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						try {
							VolleyLog.v("Response:%n %s", response.toString(4));
							Log.i(TAG, response.toString());
							if (response.getBoolean("success")) {
								if (BuildConfig.DEBUG) {
									ApplicationLoader.getPreferences().setProfilePicPath(response.getString("profile_pic"));
									ApplicationLoader.getPreferences().setProfilePostCount(response.getString("post_count"));
									ApplicationLoader.getPreferences().setProfileGroupCount(response.getString("subscribe_count"));
									try{
										setNotifCount(Utilities.getNotificationCount());
									}catch(Exception e){
										Log.e(TAG, e.toString());
									}
									VolleyLog.v("Response:%n %s",
											response.toString(4));
									Log.i(TAG, response.toString());
								}
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}, new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						VolleyLog.e("Error: ", error.getMessage());
						if (BuildConfig.DEBUG) {
							Log.i(TAG, "Volley Error : " + error.getMessage());
							VolleyLog.e("Error: ", error.getMessage());
						}
					}
				});
		ApplicationLoader.getInstance().addToRequestQueue(req, TAG);
	}
}
