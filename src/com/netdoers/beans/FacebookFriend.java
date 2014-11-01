package com.netdoers.beans;

import android.os.Parcel;
import android.os.Parcelable;

public class FacebookFriend implements Parcelable {

	private String facebookId;
	private String facebookName;

	public FacebookFriend() {
		super();
		// TODO Auto-generated constructor stub
	}

	public FacebookFriend(String facebookId, String facebookName) {
		super();
		this.facebookId = facebookId;
		this.facebookName = facebookName;
	}

	public String getFacebookId() {
		return facebookId;
	}

	public void setFacebookId(String facebookId) {
		this.facebookId = facebookId;
	}

	public String getFacebookName() {
		return facebookName;
	}

	public void setFacebookName(String facebookName) {
		this.facebookName = facebookName;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeString(facebookId);
		dest.writeString(facebookName);
	}

	public FacebookFriend(Parcel source) {
		facebookId = source.readString();
		facebookName = source.readString();
	}

	public static final Parcelable.Creator<FacebookFriend> CREATOR = new Parcelable.Creator<FacebookFriend>() {
		@Override
		public FacebookFriend createFromParcel(Parcel source) {
			return new FacebookFriend(source);
		}

		@Override
		public FacebookFriend[] newArray(int size) {
			return new FacebookFriend[size];
		}
	};

}
