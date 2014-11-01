/* HISTORY
 * CATEGORY 		:- BEAN | DATA TRANSFER OBJECT
 * DEVELOPER		:- VIKALP PATEL
 * AIM			    :- USES AS BEANS IN CONTACTS FRAGMENTS [FAMILY, WORK, FRIENDS & CONTACTS] 
 * DESCRIPTION 		:- USE TO STORE CONTACT IN FORM OF BEANS
 * 
 * S - START E- END  C- COMMENTED  U -EDITED A -ADDED
 * --------------------------------------------------------------------------------------------------------------------
 * INDEX       DEVELOPER		DATE			FUNCTION		DESCRIPTION
 * --------------------------------------------------------------------------------------------------------------------
 * 10001      VIKALP PATEL    16/05/2014                        CREATED
 * --------------------------------------------------------------------------------------------------------------------
 */
package com.netdoers.beans;

import android.os.Parcel;
import android.os.Parcelable;

public class Contact implements Parcelable {

	private String contactId;
	private String contactName;
	private String contactNumber = "";
	private String contactEmail;
	public static final String CONTACTS_DATA = "CONTACTS_DATA";

	public Contact() {
	}

	public String getContactId() {
		return contactId;
	}

	public void setContactId(String contactId) {
		this.contactId = contactId;
	}

	public String getContactName() {
		return contactName;
	}

	public String getContactNumber() {
		return contactNumber;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}

	public String getContactEmail() {
		return contactEmail;
	}

	public void setContactEmail(String contactEmail) {
		this.contactEmail = contactEmail;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(contactId);
		dest.writeString(contactName);
		dest.writeString(contactNumber);
		dest.writeString(contactEmail);
	}

	public Contact(Parcel source) {
		contactId = source.readString();
		contactName = source.readString();
		contactNumber = source.readString();
		contactEmail = source.readString();
	}

	public static final Parcelable.Creator<Contact> CREATOR = new Parcelable.Creator<Contact>() {
		@Override
		public Contact createFromParcel(Parcel source) {
			return new Contact(source);
		}

		@Override
		public Contact[] newArray(int size) {
			return new Contact[size];
		}
	};
}
