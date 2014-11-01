package com.netdoers.ui;

import com.netdoers.beans.Feed;

public interface IFragmentPopularCommunicator {
//	public void passDataToFragment(String someValue);
	public void passFeedDataToFragment(Feed mFeed, int mPosition, String someValue);
}
