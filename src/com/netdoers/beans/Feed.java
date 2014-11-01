package com.netdoers.beans;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

public class Feed implements Parcelable {
	private String feedId;
	private String feedPostedImage;
	private String feedPostedUser;
	private String feedPostedInGroup;
	private String feedTime;
	private String feedContent;
	private String feedIsTrue;
	private String feedIsFalse;
	private String feedTruePer;
	private String feedFalsePer;
	private String feedTrueCount;
	private String feedFalseCount;
	private String feedIsLike;
	private String feedLikeCount;
	private String feedPer;
	private String feedIsReport;
	private String feedCommentCount;
	private String feedShare;
	private ArrayList<FeedMedia> feedMedia;

	public Feed() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Feed(String feedId, String feedPostedImage, String feedPostedUser,
			String feedPostedInGroup, String feedTime, String feedContent,
			String feedIsTrue, String feedIsFalse, String feedTruePer,
			String feedFalsePer, String feedTrueCount, String feedFalseCount,
			String feedIsLike, String feedLikeCount, String feedPer,
			String feedIsReport, String feedCommentCount, String feedShare,
			ArrayList<FeedMedia> feedMedia) {
		super();
		this.feedId = feedId;
		this.feedPostedImage = feedPostedImage;
		this.feedPostedUser = feedPostedUser;
		this.feedPostedInGroup = feedPostedInGroup;
		this.feedTime = feedTime;
		this.feedContent = feedContent;
		this.feedIsTrue = feedIsTrue;
		this.feedIsFalse = feedIsFalse;
		this.feedTruePer = feedTruePer;
		this.feedFalsePer = feedFalsePer;
		this.feedTrueCount = feedTrueCount;
		this.feedFalseCount = feedFalseCount;
		this.feedIsLike = feedIsLike;
		this.feedLikeCount = feedLikeCount;
		this.feedPer = feedPer;
		this.feedIsReport = feedIsReport;
		this.feedCommentCount = feedCommentCount;
		this.feedShare = feedShare;
		this.feedMedia = feedMedia;
	}

	public String getFeedId() {
		return feedId;
	}

	public void setFeedId(String feedId) {
		this.feedId = feedId;
	}

	public String getFeedPostedImage() {
		return feedPostedImage;
	}

	public void setFeedPostedImage(String feedPostedImage) {
		this.feedPostedImage = feedPostedImage;
	}

	public String getFeedPostedUser() {
		return feedPostedUser;
	}

	public void setFeedPostedUser(String feedPostedUser) {
		this.feedPostedUser = feedPostedUser;
	}

	public String getFeedPostedInGroup() {
		return feedPostedInGroup;
	}

	public void setFeedPostedInGroup(String feedPostedInGroup) {
		this.feedPostedInGroup = feedPostedInGroup;
	}

	public String getFeedTime() {
		return feedTime;
	}

	public void setFeedTime(String feedTime) {
		this.feedTime = feedTime;
	}

	public String getFeedContent() {
		return feedContent;
	}

	public void setFeedContent(String feedContent) {
		this.feedContent = feedContent;
	}

	public String getFeedIsTrue() {
		return feedIsTrue;
	}

	public void setFeedIsTrue(String feedIsTrue) {
		this.feedIsTrue = feedIsTrue;
	}

	public String getFeedIsFalse() {
		return feedIsFalse;
	}

	public void setFeedIsFalse(String feedIsFalse) {
		this.feedIsFalse = feedIsFalse;
	}

	public String getFeedTruePer() {
		return feedTruePer;
	}

	public void setFeedTruePer(String feedTruePer) {
		this.feedTruePer = feedTruePer;
	}

	public String getFeedFalsePer() {
		return feedFalsePer;
	}

	public void setFeedFalsePer(String feedFalsePer) {
		this.feedFalsePer = feedFalsePer;
	}

	public String getFeedTrueCount() {
		return feedTrueCount;
	}

	public void setFeedTrueCount(String feedTrueCount) {
		this.feedTrueCount = feedTrueCount;
	}

	public String getFeedFalseCount() {
		return feedFalseCount;
	}

	public void setFeedFalseCount(String feedFalseCount) {
		this.feedFalseCount = feedFalseCount;
	}

	public String getFeedIsLike() {
		return feedIsLike;
	}

	public void setFeedIsLike(String feedIsLike) {
		this.feedIsLike = feedIsLike;
	}

	public String getFeedLikeCount() {
		return feedLikeCount;
	}

	public void setFeedLikeCount(String feedLikeCount) {
		this.feedLikeCount = feedLikeCount;
	}

	public String getFeedPer() {
		return feedPer;
	}

	public void setFeedPer(String feedPer) {
		this.feedPer = feedPer;
	}

	public String getFeedIsReport() {
		return feedIsReport;
	}

	public void setFeedIsReport(String feedIsReport) {
		this.feedIsReport = feedIsReport;
	}

	public String getFeedCommentCount() {
		return feedCommentCount;
	}

	public void setFeedCommentCount(String feedCommentCount) {
		this.feedCommentCount = feedCommentCount;
	}

	public String getFeedShare() {
		return feedShare;
	}

	public void setFeedShare(String feedShare) {
		this.feedShare = feedShare;
	}

	public ArrayList<FeedMedia> getFeedMedia() {
		return feedMedia;
	}

	public void setFeedMedia(ArrayList<FeedMedia> feedMedia) {
		this.feedMedia = feedMedia;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(feedId);
		dest.writeString(feedPostedImage);
		dest.writeString(feedPostedUser);
		dest.writeString(feedPostedInGroup);
		dest.writeString(feedTime);
		dest.writeString(feedContent);
		dest.writeString(feedIsTrue);
		dest.writeString(feedIsFalse);
		dest.writeString(feedTruePer);
		dest.writeString(feedFalsePer);
		dest.writeString(feedTrueCount);
		dest.writeString(feedFalseCount);
		dest.writeString(feedIsLike);
		dest.writeString(feedLikeCount);
		dest.writeString(feedPer);
		dest.writeString(feedIsReport);
		dest.writeString(feedCommentCount);
		dest.writeString(feedShare);
		if (feedMedia == null) {
			dest.writeByte((byte) (0x00));
		} else {
			dest.writeByte((byte) (0x01));
			dest.writeList(feedMedia);
		}
	}

	protected Feed(Parcel in) {
		feedId = in.readString();
		feedPostedImage = in.readString();
		feedPostedUser = in.readString();
		feedPostedInGroup = in.readString();
		feedTime = in.readString();
		feedContent = in.readString();
		feedIsTrue = in.readString();
		feedIsFalse = in.readString();
		feedTruePer = in.readString();
		feedFalsePer = in.readString();
		feedTrueCount = in.readString();
		feedFalseCount = in.readString();
		feedIsLike = in.readString();
		feedLikeCount = in.readString();
		feedPer = in.readString();
		feedIsReport = in.readString();
		feedCommentCount = in.readString();
		feedShare = in.readString();
		if (in.readByte() == 0x01) {
			feedMedia = new ArrayList<FeedMedia>();
			in.readList(feedMedia, FeedMedia.class.getClassLoader());
		} else {
			feedMedia = null;
		}
	}

	public static final Parcelable.Creator<Feed> CREATOR = new Parcelable.Creator<Feed>() {
		@Override
		public Feed createFromParcel(Parcel source) {
			return new Feed(source);
		}

		@Override
		public Feed[] newArray(int size) {
			return new Feed[size];
		}

	};
}
