package com.netdoers.beans;

import android.os.Parcel;
import android.os.Parcelable;

public class Notification implements Parcelable {

	private String id;
	private String notifId;
	private String notifFromUser;
	private String notifFromUserImage;
	private String notifWhat;
	private String notifTo;
	private String notifPostId;
	private String notifPage;
	private String notifReadStatus;

	public Notification() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Notification(String id, String notifId, String notifFromUser,
			String notifFromUserImage, String notifWhat, String notifTo,
			String notifPostId, String notifPage, String notifReadStatus) {
		super();
		this.id = id;
		this.notifId = notifId;
		this.notifFromUser = notifFromUser;
		this.notifFromUserImage = notifFromUserImage;
		this.notifWhat = notifWhat;
		this.notifTo = notifTo;
		this.notifPostId = notifPostId;
		this.notifPage = notifPage;
		this.notifReadStatus = notifReadStatus;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getNotifId() {
		return notifId;
	}

	public void setNotifId(String notifId) {
		this.notifId = notifId;
	}

	public String getNotifFromUser() {
		return notifFromUser;
	}

	public void setNotifFromUser(String notifFromUser) {
		this.notifFromUser = notifFromUser;
	}

	public String getNotifFromUserImage() {
		return notifFromUserImage;
	}

	public void setNotifFromUserImage(String notifFromUserImage) {
		this.notifFromUserImage = notifFromUserImage;
	}

	public String getNotifWhat() {
		return notifWhat;
	}

	public void setNotifWhat(String notifWhat) {
		this.notifWhat = notifWhat;
	}

	public String getNotifTo() {
		return notifTo;
	}

	public void setNotifTo(String notifTo) {
		this.notifTo = notifTo;
	}

	public String getNotifPostId() {
		return notifPostId;
	}

	public void setNotifPostId(String notifPostId) {
		this.notifPostId = notifPostId;
	}

	public String getNotifPage() {
		return notifPage;
	}

	public void setNotifPage(String notifPage) {
		this.notifPage = notifPage;
	}

	public String getNotifReadStatus() {
		return notifReadStatus;
	}

	public void setNotifReadStatus(String notifReadStatus) {
		this.notifReadStatus = notifReadStatus;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeString(id);
		dest.writeString(notifId);
		dest.writeString(notifFromUser);
		dest.writeString(notifFromUserImage);
		dest.writeString(notifWhat);
		dest.writeString(notifTo);
		dest.writeString(notifPostId);
		dest.writeString(notifPage);
		dest.writeString(notifReadStatus);
	}

	public Notification(Parcel source) {
		id = source.readString();
		notifId = source.readString();
		notifFromUser = source.readString();
		notifFromUserImage = source.readString();
		notifWhat = source.readString();
		notifTo = source.readString();
		notifPostId = source.readString();
		notifPage = source.readString();
		notifReadStatus = source.readString();

	}

	public static final Parcelable.Creator<Notification> CREATOR = new Parcelable.Creator<Notification>() {
		@Override
		public Notification createFromParcel(Parcel source) {
			return new Notification(source);
		}

		@Override
		public Notification[] newArray(int size) {
			return new Notification[size];
		}
	};

}
