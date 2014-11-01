package com.netdoers.beans;

import android.os.Parcel;
import android.os.Parcelable;

public class GroupFilter implements Parcelable {

	private String groupId;
	private String groupName;

	public GroupFilter() {
		super();
		// TODO Auto-generated constructor stub
	}

	public GroupFilter(String groupId, String groupName) {
		super();
		this.groupId = groupId;
		this.groupName = groupName;
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
	}

	public GroupFilter(Parcel source) {
		groupId = source.readString();
		groupName = source.readString();
	}

	public static final Parcelable.Creator<GroupFilter> CREATOR = new Parcelable.Creator<GroupFilter>() {
		@Override
		public GroupFilter createFromParcel(Parcel source) {
			return new GroupFilter(source);
		}

		@Override
		public GroupFilter[] newArray(int size) {
			return new GroupFilter[size];
		}
	};

}
