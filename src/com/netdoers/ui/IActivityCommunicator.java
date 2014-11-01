package com.netdoers.ui;

import com.netdoers.beans.Feed;

public interface IActivityCommunicator {
//	public void passDataToActivity(String someValue);
	public void passFeedDataToActivity(Feed mFeed,int mPosition, String someValue);
}
