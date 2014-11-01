package com.netdoers.beans;

import android.os.Parcel;
import android.os.Parcelable;

public class FeedGroupSuggestion implements Parcelable {

	private String groupId;
	private String groupName;
	private String groupFriends;
	private String groupImagePath;
	private String groupIsSubscribe;
	private String groupIsAdmin;

	public FeedGroupSuggestion() {
		super();
		// TODO Auto-generated constructor stub
	}

	public FeedGroupSuggestion(String groupId, String groupName, String groupFriends,
			String groupImagePath, String groupIsSubscribe, String groupIsAdmin) {
		super();
		this.groupId = groupId;
		this.groupName = groupName;
		this.groupFriends = groupFriends;
		this.groupImagePath = groupImagePath;
		this.groupIsSubscribe = groupIsSubscribe;
		this.groupIsAdmin = groupIsAdmin;
	}
	
	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getGroupFriends() {
		return groupFriends;
	}

	public void setGroupFriends(String groupFriends) {
		this.groupFriends = groupFriends;
	}

	public String getGroupImagePath() {
		return groupImagePath;
	}

	public void setGroupImagePath(String groupImagePath) {
		this.groupImagePath = groupImagePath;
	}

	public String getGroupIsSubscribe() {
		return groupIsSubscribe;
	}

	public void setGroupIsSubscribe(String groupIsSubscribe) {
		this.groupIsSubscribe = groupIsSubscribe;
	}

	public String getGroupIsAdmin() {
		return groupIsAdmin;
	}

	public void setGroupIsAdmin(String groupIsAdmin) {
		this.groupIsAdmin = groupIsAdmin;
	}
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeString(groupId);
		dest.writeString(groupName);
		dest.writeString(groupFriends);
		dest.writeString(groupImagePath);
		dest.writeString(groupIsSubscribe);
		dest.writeString(groupIsAdmin);
	}

	public FeedGroupSuggestion(Parcel source) {
		groupId = source.readString();
		groupName = source.readString();
		groupFriends = source.readString();
		groupImagePath = source.readString();
		groupIsSubscribe = source.readString();
		groupIsAdmin = source.readString();
	}

	public static final Parcelable.Creator<FeedGroupSuggestion> CREATOR = new Parcelable.Creator<FeedGroupSuggestion>() {
		@Override
		public FeedGroupSuggestion createFromParcel(Parcel source) {
			return new FeedGroupSuggestion(source);
		}

		@Override
		public FeedGroupSuggestion[] newArray(int size) {
			return new FeedGroupSuggestion[size];
		}
	};

}
