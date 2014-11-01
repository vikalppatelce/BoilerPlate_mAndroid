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

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.netdoers.beans.Feed;
import com.netdoers.tellus.R;
import com.netdoers.ui.view.GroupManagementViewPagerAdapter;
import com.netdoers.ui.view.PagerSlidingTabStrip;
import com.netdoers.utils.AppConstants;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class GroupManagementActivity extends ActionBarActivity implements
		IActivityCommunicator {

	public static final String TAG = GroupManagementActivity.class
			.getSimpleName();

	private ViewPager mPager;
	private PagerSlidingTabStrip mPagerSlidingTabStrp;
	private ViewPager.SimpleOnPageChangeListener ViewPagerListener;

	private ImageLoader imageLoader;
	private DisplayImageOptions options;
	private String mGroupId;
	private String mGroupName;
	public IGroupFragmentAllCommunicator mGroupFragmentAllCommunicator;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_group_management);
		initUi();
		getIntentData();
		setActionBar();
		setUniversalImageLoader();
		setPagerSlidingTabStrip();
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_group_feed_admin, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		case R.id.action_add_group_post:
			Intent mIntent = new Intent(GroupManagementActivity.this,
					PostActivity.class);
			mIntent.putExtra(AppConstants.TAGS.GROUP_ID, mGroupId);
			mIntent.putExtra(AppConstants.TAGS.GROUP_NAME, mGroupName);
			startActivity(mIntent);
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void initUi() {
		mPager = (ViewPager) findViewById(R.id.activity_group_management_pager);
		mPagerSlidingTabStrp = (PagerSlidingTabStrip) findViewById(R.id.activity_group_management_pager_sliding_tab_strip);
	}

	private void getIntentData() {
		mGroupId = getIntent().getStringExtra(AppConstants.TAGS.GROUP_ID);
		mGroupName = getIntent().getStringExtra(AppConstants.TAGS.GROUP_NAME)
				.toUpperCase();
	}

	private void setActionBar() {
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	private void setPagerSlidingTabStrip() {
		GroupManagementViewPagerAdapter viewpageradapter = new GroupManagementViewPagerAdapter(
				getSupportFragmentManager());
		mPager.setOffscreenPageLimit(2);
		mPager.setAdapter(viewpageradapter);
		mPagerSlidingTabStrp.setShouldExpand(true);
		mPagerSlidingTabStrp.setViewPager(mPager);
		setViewPagerListener();
		mPagerSlidingTabStrp.setOnPageChangeListener(ViewPagerListener);
		setActionBarTitle(mGroupName);
		setActionBarSubTitle(getString(R.string.str_actionbar_fragment_group_feed_all));
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

	private void setViewPagerListener() {
		ViewPagerListener = new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				super.onPageSelected(position);
				// Find the ViewPager Position
				switch (position) {
				case 0:
					setActionBarSubTitle(getString(R.string.str_actionbar_fragment_group_feed_all));
					break;
				case 1:
					setActionBarSubTitle(getString(R.string.str_actionbar_fragment_group_feed_report));
					break;
				}
			}
		};
	}

	private void setActionBarTitle(String str) {
		getSupportActionBar().setTitle(str);
	}

	private void setActionBarSubTitle(String str) {
		getSupportActionBar().setSubtitle(str);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (requestCode == AppConstants.INTENT.FEED_COMMENT) {
				Feed mFeedRefreshed = data.getExtras().getParcelable(
						AppConstants.INTENT.COMMENT_DATA);
				int mPosition = Integer.parseInt(data.getExtras().getString(
						AppConstants.TAGS.FEED_POSITION));
				switch (mPager.getCurrentItem()) {
				case 0:
					mGroupFragmentAllCommunicator.passFeedDataToFragment(
							mFeedRefreshed, mPosition, "ALLREPORTED");
					break;
				}
			}
		}
	}

	@Override
	public void passFeedDataToActivity(Feed mFeed, int mPosition,
			String someValue) {
		// TODO Auto-generated method stub

	}
}
