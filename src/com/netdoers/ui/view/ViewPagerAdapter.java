/* HISTORY
 * CATEGORY			 :- VIEW | HELPER 
 * DEVELOPER		 :- VIKALP PATEL
 * AIM      		 :- PROVIDE FRAGMENT FOR TABS
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

package com.netdoers.ui.view;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.netdoers.tellus.R;
import com.netdoers.ui.FragmentFeedAll;
import com.netdoers.ui.FragmentFeedFriends;
import com.netdoers.ui.FragmentFeedPopular;
import com.netdoers.ui.view.PagerSlidingTabStrip.IconTabProvider;

/**
 * @author Vikalp Patel (vikalppatelce@yahoo.com)
 * @category Ui Helper
 * 
 */
public class ViewPagerAdapter extends FragmentPagerAdapter implements
		IconTabProvider {
//	final int PAGE_COUNT = 4;
	final int PAGE_COUNT = 3;
	private final int[] ICONS = { R.drawable.tab_icon_feed_home_selector,
			R.drawable.tab_icon_feed_friends_selector, R.drawable.tab_icon_feed_popular_selector/*,
			R.drawable.tab_icon_selector*/ };

	public ViewPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int item) {
		switch (item) {
		case 0:
			FragmentFeedAll fragmentFeedAll = new FragmentFeedAll();
			return fragmentFeedAll;
		case 1:
			FragmentFeedFriends fragmentFeedAll1 = new FragmentFeedFriends();
			return fragmentFeedAll1;
		case 2:
			FragmentFeedPopular fragmentFeedAll3 = new FragmentFeedPopular();
			return fragmentFeedAll3;
		/*case 2:
			FragmentFeedRecent fragmentFeedAll2 = new FragmentFeedRecent();
			return fragmentFeedAll2;
		case 3:
			FragmentFeedPopular fragmentFeedAll3 = new FragmentFeedPopular();
			return fragmentFeedAll3;*/
		}
		return null;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return PAGE_COUNT;
	}

	@Override
	public int getPageIconResId(int position) {
		// TODO Auto-generated method stub
		return ICONS[position];
	}
}
