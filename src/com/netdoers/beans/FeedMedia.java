package com.netdoers.beans;

import android.os.Parcel;
import android.os.Parcelable;

public class FeedMedia implements Parcelable {
	private String feedId;
	private String feedMediaId;
	private String feedMediaPath;
	private String feedIsVideo;
	private String feedVideoThumbail;

	public FeedMedia() {
		super();
		// TODO Auto-generated constructor stub
	}

	public FeedMedia(String feedId, String feedMediaId, String feedMediaPath,
			String feedIsVideo, String feedVideoThumbail) {
		super();
		this.feedId = feedId;
		this.feedMediaId = feedMediaId;
		this.feedMediaPath = feedMediaPath;
		this.feedIsVideo = feedIsVideo;
		this.feedVideoThumbail = feedVideoThumbail;
	}

	public String getFeedId() {
		return feedId;
	}

	public void setFeedId(String feedId) {
		this.feedId = feedId;
	}

	public String getFeedMediaId() {
		return feedMediaId;
	}

	public void setFeedMediaId(String feedMediaId) {
		this.feedMediaId = feedMediaId;
	}

	public String getFeedMediaPath() {
		return feedMediaPath;
	}

	public void setFeedMediaPath(String feedMediaPath) {
		this.feedMediaPath = feedMediaPath;
	}

	public String getFeedIsVideo() {
		return feedIsVideo;
	}

	public void setFeedIsVideo(String feedIsVideo) {
		this.feedIsVideo = feedIsVideo;
	}

	public String getFeedVideoThumbail() {
		return feedVideoThumbail;
	}

	public void setFeedVideoThumbail(String feedVideoThumbail) {
		this.feedVideoThumbail = feedVideoThumbail;
	}
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeString(feedId);
		dest.writeString(feedMediaId);
		dest.writeString(feedMediaPath);
		dest.writeString(feedIsVideo);
		dest.writeString(feedVideoThumbail);
	}

	public FeedMedia(Parcel source) {
		feedId = source.readString();
		feedMediaId = source.readString();
		feedMediaPath = source.readString();
		feedIsVideo = source.readString();
		feedVideoThumbail = source.readString();
	}

	public static final Parcelable.Creator<FeedMedia> CREATOR = new Parcelable.Creator<FeedMedia>() {
		@Override
		public FeedMedia createFromParcel(Parcel source) {
			return new FeedMedia(source);
		}

		@Override
		public FeedMedia[] newArray(int size) {
			return new FeedMedia[size];
		}
	};
}
