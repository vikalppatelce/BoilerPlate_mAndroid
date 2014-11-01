package com.netdoers.beans;

import android.os.Parcel;
import android.os.Parcelable;

public class FeedComment implements Parcelable {
	private String feedUserProfile;
	private String feedUserName;
	private String feedUserComment;

	public FeedComment() {
		super();
		// TODO Auto-generated constructor stub
	}

	public FeedComment(String feedUserProfile, String feedUserName,
			String feedUserComment) {
		super();
		this.feedUserProfile = feedUserProfile;
		this.feedUserName = feedUserName;
		this.feedUserComment = feedUserComment;
	}

	public String getFeedUserProfile() {
		return feedUserProfile;
	}

	public void setFeedUserProfile(String feedUserProfile) {
		this.feedUserProfile = feedUserProfile;
	}

	public String getFeedUserName() {
		return feedUserName;
	}

	public void setFeedUserName(String feedUserName) {
		this.feedUserName = feedUserName;
	}

	public String getFeedUserComment() {
		return feedUserComment;
	}

	public void setFeedUserComment(String feedUserComment) {
		this.feedUserComment = feedUserComment;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeString(feedUserProfile);
		dest.writeString(feedUserName);
		dest.writeString(feedUserComment);
	}

	public FeedComment(Parcel source) {
		feedUserProfile = source.readString();
		feedUserName = source.readString();
		feedUserComment = source.readString();
	}

	public static final Parcelable.Creator<FeedComment> CREATOR = new Parcelable.Creator<FeedComment>() {
		@Override
		public FeedComment createFromParcel(Parcel source) {
			return new FeedComment(source);
		}

		@Override
		public FeedComment[] newArray(int size) {
			return new FeedComment[size];
		}
	};
}
