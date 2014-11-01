package com.netdoers.utils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.ListIterator;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Data;
import android.text.TextUtils;

import com.netdoers.beans.Contact;
import com.netdoers.beans.ContactPicker;

public class SyncContact {

	private static String TAG = SyncContact.class.getSimpleName();
	
	public static ArrayList<Contact> getContactsNewApi(
			LinkedHashMap<String, Contact> allContacts,
			ArrayList<Contact> contacts) {
		ContentResolver cr = ApplicationLoader.getApplication()
				.getContentResolver();

		String selection = Data.HAS_PHONE_NUMBER + " > '" + ("0") + "'";

		Cursor cur = cr.query(Data.CONTENT_URI, new String[] { Data.CONTACT_ID,
				Data.MIMETYPE, Contacts.DISPLAY_NAME, Phone.NUMBER ,Email.ADDRESS},
				selection, null, Contacts.DISPLAY_NAME);

		Contact contact;
		if (cur.getCount() > 0) {
			while (cur.moveToNext()) {
				String id = cur.getString(cur.getColumnIndex(Data.CONTACT_ID));
				String mimeType = cur.getString(cur
						.getColumnIndex(Data.MIMETYPE));

				if (allContacts.containsKey(id)) {
					// update contact
					contact = allContacts.get(id);
				} else {
					contact = new Contact();
					allContacts.put(id, contact);
					// set contactId
					contact.setContactId(id);
				}

				if (mimeType.equals(StructuredName.CONTENT_ITEM_TYPE)) {
					// set name
					contact.setContactName(cur.getString(cur
							.getColumnIndex(Contacts.DISPLAY_NAME)));
				}

				if (mimeType.equals(Phone.CONTENT_ITEM_TYPE)) {
					String s = cur.getString(cur.getColumnIndex(Phone.NUMBER))
							.replaceAll("\\D", "");
					if (contact.getContactNumber().toString().length() == 0) {
						if (s.length() == 12 || s.length() == 11) {
							contact.setContactNumber(
									s.substring(s.length() - 10, s.length()));
						} else {
							contact.setContactNumber("\"" + s + "\"");
						}
					} else {
						if (s.length() == 12 || s.length() == 11) {
							contact.setContactNumber(contact
									.getContactNumber()
									.toString()
									.concat(", ")
									.concat(s.substring(s.length() - 10,
											s.length())));
						} else {
							contact.setContactNumber(contact.getContactNumber()
									.toString().concat(", ")
									.concat(s));
						}
					}
				}
				
				if (mimeType.equals(Email.CONTENT_ITEM_TYPE))
					contact.setContactEmail(cur.getString(cur.getColumnIndex(Email.ADDRESS)));
			}
		}

		cur.close();
		// get contacts from hashmap
		contacts.clear();
		contacts.addAll(allContacts.values());

		// remove self contact
		for (Contact _contact : contacts) {
			if (_contact.getContactName() == null
					&& _contact.getContactNumber() == null) {
				contacts.remove(_contact);
				break;
			}
		}

		return contacts;
	}

	/**
	 * @see Get Contacts for 2.2+ DATA.HAS_PHONE_NUMBER was not added in < 3.0
	 */
	public static ArrayList<Contact> getContactsOldApi(
			LinkedHashMap<String, Contact> allContacts,
			ArrayList<Contact> contacts) {

		Uri uri = ContactsContract.Contacts.CONTENT_URI;
		String[] projection = new String[] { ContactsContract.Contacts._ID,
				ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.Contacts.Data.MIMETYPE,
				Email.ADDRESS };
		
		String selection = ContactsContract.Contacts.HAS_PHONE_NUMBER + " > '"
				+ ("0") + "'";
		String[] selectionArgs = null;
		String sortOrder = ContactsContract.Contacts.DISPLAY_NAME
				+ " COLLATE LOCALIZED ASC";

		ContentResolver contectResolver = ApplicationLoader.getApplication()
				.getContentResolver();

		Cursor cursor = contectResolver.query(uri, projection, selection,
				selectionArgs, sortOrder);
		Contact contact;
		// Load contacts one by one
		if (cursor.moveToFirst()) {
			while (!cursor.isAfterLast()) {
				contact = new Contact();
				String id = cursor.getString(cursor
						.getColumnIndex(ContactsContract.Contacts._ID));
				String mimeType = cursor.getString(cursor
						.getColumnIndex(Data.MIMETYPE));
				// set contactId
				contact.setContactId(id);

				String[] phoneProj = new String[] {
						ContactsContract.CommonDataKinds.Phone.NUMBER,
						ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME };
				Cursor cursorPhone = contectResolver.query(
						ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
						phoneProj,
						ContactsContract.CommonDataKinds.Phone.CONTACT_ID
								+ " = ?", new String[] { id }, null);
				if (cursorPhone.moveToFirst()) {
					do {
						String s = cursorPhone
								.getString(
										cursorPhone
												.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
								.replaceAll("\\D", "");
						if (contact.getContactNumber().toString().length() == 0) {
							if (s.length() == 12 || s.length() == 11) {
								contact.setContactNumber("\""
										+ s.substring(s.length() - 10,
												s.length()) + "\"");
							} else {
								contact.setContactNumber("\"" + s + "\"");
							}
						} else {
							if (s.length() == 12 || s.length() == 11) {
								contact.setContactNumber(contact
										.getContactNumber()
										.toString()
										.concat(", ")
										.concat("\"")
										.concat(s.substring(s.length() - 10,
												s.length())).concat("\""));
							} else {
								contact.setContactNumber(contact
										.getContactNumber().toString()
										.concat(", ").concat("\"").concat(s)
										.concat("\""));
							}
						}
					} while (cursorPhone.moveToNext());
				}

				if (cursorPhone != null) {
					cursorPhone.close();
				}
				// EU ZM002

				contact.setContactName(cursor.getString(cursor
						.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)));

				if (mimeType.equals(Email.CONTENT_ITEM_TYPE))
					contact.setContactEmail(cursor.getString(cursor
							.getColumnIndex(Email.ADDRESS)));

				allContacts.put(id, contact);
				cursor.moveToNext();
			}
		}
		cursor.close();
		contacts.clear();
		contacts.addAll(allContacts.values());

		for (Contact _contact : contacts) {

			if (_contact.getContactName() == null
					&& _contact.getContactNumber() == null) {
				contacts.remove(_contact);
				break;
			}
		}
		return contacts;
	}

	
	public static ArrayList<Contact> getContactsOldApiSlow(
			LinkedHashMap<String, Contact> allContacts,
			ArrayList<Contact> contacts) {
		ContentResolver cr =  ApplicationLoader.getApplication()
				.getContentResolver();;

		Cursor cur = cr.query(Data.CONTENT_URI, new String[] { Data.CONTACT_ID,
				Data.MIMETYPE, Email.ADDRESS, Contacts.DISPLAY_NAME,
				Phone.NUMBER }, null, null, Contacts.DISPLAY_NAME);

		Contact contact;

		if (cur.getCount() > 0) {

			while (cur.moveToNext()) {

				String id = cur.getString(cur.getColumnIndex(Data.CONTACT_ID));

				String mimeType = cur.getString(cur
						.getColumnIndex(Data.MIMETYPE));

				if (allContacts.containsKey(id)) {
					// update contact
					contact = allContacts.get(id);
				} else {
					contact = new Contact();
					allContacts.put(id, contact);
				}

				if (mimeType.equals(StructuredName.CONTENT_ITEM_TYPE))
					// set name
					contact.setContactName(cur.getString(cur
							.getColumnIndex(Contacts.DISPLAY_NAME)));

				if (mimeType.equals(Phone.CONTENT_ITEM_TYPE)){
					String s = cur.getString(cur
							.getColumnIndex(Phone.NUMBER))
							.replaceAll("\\D", "");
					if (contact.getContactNumber().toString().length() == 0) {
						if (s.length() == 12 || s.length() == 11) {
							contact.setContactNumber(
									 s.substring(s.length() - 10,
											s.length()));
						} else {
							contact.setContactNumber(s);
						}
					} else {
						if (s.length() == 12 || s.length() == 11) {
							contact.setContactNumber(contact
									.getContactNumber()
									.toString()
									.concat(", ")
									.concat(s.substring(s.length() - 10,
											s.length())));
						} else {
							contact.setContactNumber(contact
									.getContactNumber().toString()
									.concat(", ").concat(s));
						}
					}
				}

				if (mimeType.equals(Email.CONTENT_ITEM_TYPE))
					// set email
					contact.setContactEmail(cur.getString(cur
							.getColumnIndex(Email.ADDRESS)));

			}
		}

		cur.close();
		contacts.clear();
		contacts.addAll(allContacts.values());

		ListIterator<Contact> iterContacts = contacts.listIterator();
		while (iterContacts.hasNext()) {
			final Contact _contact = iterContacts.next();
			if (TextUtils.isEmpty(_contact.getContactNumber())) {
				contacts.remove(_contact);
				iterContacts = contacts.listIterator();
			}
		}
		return contacts;
	}
	
	public static ArrayList<ContactPicker> getEmailNewApi(
			LinkedHashMap<String, ContactPicker> allContacts,
			ArrayList<ContactPicker> contacts) {
		ContentResolver cr = ApplicationLoader.getApplication()
				.getContentResolver();

		String selection = null;//Email.ADDRESS + " NOT NULL";

		Cursor cur = cr.query(Data.CONTENT_URI, new String[] { Data.CONTACT_ID,
				Data.MIMETYPE, Contacts.DISPLAY_NAME, Email.ADDRESS },
				selection, null, Contacts.DISPLAY_NAME);

		ContactPicker contact;
		if (cur.getCount() > 0) {
			while (cur.moveToNext()) {
				String id = cur.getString(cur.getColumnIndex(Data.CONTACT_ID));
				String mimeType = cur.getString(cur
						.getColumnIndex(Data.MIMETYPE));
				
				if (allContacts.containsKey(id)) {
					// update contact
					contact = allContacts.get(id);
				} else {
					contact = new ContactPicker();
					allContacts.put(id, contact);
					// set contactId
					contact.setContactId(id);
					contact.setContactPhotoUri(getContactPhotoUri(Long
							.parseLong(id)));
				}

				if (mimeType.equals(StructuredName.CONTENT_ITEM_TYPE)) {
					// set name
					contact.setContactName(cur.getString(cur
							.getColumnIndex(Contacts.DISPLAY_NAME)));
				}
								
				if (mimeType.equals(Email.CONTENT_ITEM_TYPE))
					contact.setContactEmail(cur.getString(cur.getColumnIndex(Email.ADDRESS)));
			}
		}

		cur.close();
		// get contacts from hashmap
		contacts.clear();
		contacts.addAll(allContacts.values());
		// remove self contact

		ListIterator<ContactPicker> iterContacts = contacts.listIterator();
		while (iterContacts.hasNext()) {
			final ContactPicker _contact = iterContacts.next();
			if (TextUtils.isEmpty(_contact.getContactName())
					|| TextUtils.isEmpty(_contact.getContactEmail())) {
				contacts.remove(_contact);
				iterContacts = contacts.listIterator();
			}
		}

		return contacts;
	}

	
	public static ArrayList<ContactPicker> getEmailOldApi(
			LinkedHashMap<String, ContactPicker> allContacts,
			ArrayList<ContactPicker> contacts) {

		Uri uri = ContactsContract.Contacts.CONTENT_URI;
		String[] projection = new String[] { ContactsContract.Contacts._ID,
				ContactsContract.Contacts.DISPLAY_NAME, Data.MIMETYPE,
				Email.ADDRESS };
		
		String selection = null;//Email.ADDRESS + " NOT NULL";
		String[] selectionArgs = null;
		String sortOrder = ContactsContract.Contacts.DISPLAY_NAME
				+ " COLLATE LOCALIZED ASC";

		ContentResolver contectResolver = ApplicationLoader.getApplication()
				.getContentResolver();

		Cursor cursor = contectResolver.query(uri, projection, selection,
				selectionArgs, sortOrder);
		ContactPicker contact;
		// Load contacts one by one
		if (cursor.moveToFirst()) {
			while (!cursor.isAfterLast()) {
				contact = new ContactPicker();
				String id = cursor.getString(cursor
						.getColumnIndex(ContactsContract.Contacts._ID));
				String mimeType = cursor.getString(cursor
						.getColumnIndex(Data.MIMETYPE));
				// set contactId
				contact.setContactId(id);

				contact.setContactPhotoUri(getContactPhotoUri(Long
						.parseLong(id)));
				
				contact.setContactName(cursor.getString(cursor
						.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)));

				if (mimeType.equals(Email.CONTENT_ITEM_TYPE))
					contact.setContactEmail(cursor.getString(cursor
							.getColumnIndex(Email.ADDRESS)));

				allContacts.put(id, contact);
				cursor.moveToNext();
			}
		}
		cursor.close();
		contacts.clear();
		contacts.addAll(allContacts.values());

		ListIterator<ContactPicker> iterContacts = contacts.listIterator();
		while (iterContacts.hasNext()) {
			final ContactPicker _contact = iterContacts.next();
			if (TextUtils.isEmpty(_contact.getContactName())
					|| TextUtils.isEmpty(_contact.getContactEmail())) {
				contacts.remove(_contact);
				iterContacts = contacts.listIterator();
			}
		}
		
		return contacts;
	}
	
	public static ArrayList<ContactPicker> getEmailOldApiSlow(
			LinkedHashMap<String, ContactPicker> allContacts,
			ArrayList<ContactPicker> contacts) {

		ContentResolver cr =  ApplicationLoader.getApplication()
				.getContentResolver();;

		Cursor cur = cr.query(Data.CONTENT_URI, new String[] { Data.CONTACT_ID,
				Data.MIMETYPE, Email.ADDRESS, Contacts.DISPLAY_NAME,
				Phone.NUMBER }, null, null, Contacts.DISPLAY_NAME);

		ContactPicker contact;

		if (cur.getCount() > 0) {

			while (cur.moveToNext()) {

				String id = cur.getString(cur.getColumnIndex(Data.CONTACT_ID));

				String mimeType = cur.getString(cur
						.getColumnIndex(Data.MIMETYPE));

				if (allContacts.containsKey(id)) {
					// update contact
					contact = allContacts.get(id);
				} else {
					contact = new ContactPicker();
					allContacts.put(id, contact);
				}

				if (mimeType.equals(StructuredName.CONTENT_ITEM_TYPE))
					// set name
					contact.setContactName(cur.getString(cur
							.getColumnIndex(Contacts.DISPLAY_NAME)));
				
				if (mimeType.equals(Email.CONTENT_ITEM_TYPE))
					// set email
					contact.setContactEmail(cur.getString(cur
							.getColumnIndex(Email.ADDRESS)));

			}
		}

		cur.close();
		contacts.clear();
		contacts.addAll(allContacts.values());

		ListIterator<ContactPicker> iterContacts = contacts.listIterator();
		while (iterContacts.hasNext()) {
			final ContactPicker _contact = iterContacts.next();
			if (TextUtils.isEmpty(_contact.getContactEmail())) {
				contacts.remove(_contact);
				iterContacts = contacts.listIterator();
			}
		}
		return contacts;
		
	}
	
	public static Uri getContactPhotoUri(long contactId) {
		Uri photoUri = ContentUris.withAppendedId(Contacts.CONTENT_URI,
				contactId);
		photoUri = Uri.withAppendedPath(photoUri,
				Contacts.Photo.CONTENT_DIRECTORY);
		return photoUri;
	}
	
/*	public static ArrayList<ContactPicker> getWhatsappNewApi(
			LinkedHashMap<String, ContactPicker> allContacts,
			ArrayList<ContactPicker> contacts) {
		final String WHATSAPP_MIME_TYPE ="vnd.android.cursor.item/vnd.com.whatsapp.profile";
		ContentResolver cr = ApplicationLoader.getApplication()
				.getContentResolver();

		String selection = Data.HAS_PHONE_NUMBER + " > '" + ("0") + "'" + " AND "+ Data.MIMETYPE + " = '" + WHATSAPP_MIME_TYPE + "'";;

		Cursor cur = cr.query(Data.CONTENT_URI, new String[] { Data.CONTACT_ID,
				Data.MIMETYPE, Contacts.DISPLAY_NAME, Phone.NUMBER ,Email.ADDRESS},
				selection, null, Contacts.DISPLAY_NAME);

		ContactPicker contact;
		if (cur.getCount() > 0) {
			Log.i(TAG , "Whatsapp Count :"+ cur.getCount());
			while (cur.moveToNext()) {
				String id = cur.getString(cur.getColumnIndex(Data.CONTACT_ID));
				String mimeType = cur.getString(cur
						.getColumnIndex(Data.MIMETYPE));

				if (allContacts.containsKey(id)) {
					// update contact
					contact = allContacts.get(id);
				} else {
					contact = new ContactPicker();
					allContacts.put(id, contact);
					// set contactId
					contact.setContactId(id);
				}

				if (mimeType.equals(StructuredName.CONTENT_ITEM_TYPE)) {
					// set name
					contact.setContactName(cur.getString(cur
							.getColumnIndex(Contacts.DISPLAY_NAME)));
				}

				if (mimeType.equals(Phone.CONTENT_ITEM_TYPE)) {
					String s = cur.getString(cur.getColumnIndex(Phone.NUMBER))
							.replaceAll("\\D", "");
					if (contact.getContactNumber().toString().length() == 0) {
						if (s.length() == 12 || s.length() == 11) {
							contact.setContactNumber("\""
									+ s.substring(s.length() - 10, s.length())
									+ "\"");
						} else {
							contact.setContactNumber("\"" + s + "\"");
						}
					} else {
						if (s.length() == 12 || s.length() == 11) {
							contact.setContactNumber(contact
									.getContactNumber()
									.toString()
									.concat(", ")
									.concat("\"")
									.concat(s.substring(s.length() - 10,
											s.length())).concat("\""));
						} else {
							contact.setContactNumber(contact.getContactNumber()
									.toString().concat(", ").concat("\"")
									.concat(s).concat("\""));
						}
					}
				}
			}
		}

		cur.close();
		// get contacts from hashmap
		contacts.clear();
		contacts.addAll(allContacts.values());

		// remove self contact
		for (ContactPicker _contact : contacts) {
			if (_contact.getContactName() == null
					&& _contact.getContactNumber() == null) {
				contacts.remove(_contact);
				break;
			}
		}

		return contacts;
	}*/
	
	public static ArrayList<ContactPicker> getWhatsappNewApi(
			LinkedHashMap<String, ContactPicker> allContacts,
			ArrayList<ContactPicker> contacts) {

		ContentResolver cr = ApplicationLoader.getApplication()
				.getContentResolver();
		
		final String[] projection={
                ContactsContract.Data.CONTACT_ID,
                ContactsContract.Data.MIMETYPE,
                "account_type",
                ContactsContract.Data.DATA3,
                };
        final String selection= ContactsContract.Data.MIMETYPE+" =? and account_type=?";
        final String[] selectionArgs = {
                "vnd.android.cursor.item/vnd.com.whatsapp.profile",
                "com.whatsapp"
                };
       
        Cursor c = cr.query(
                ContactsContract.Data.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                Contacts.DISPLAY_NAME);
        ContactPicker contact;
        if(c.getCount() > 0){
        	while(c.moveToNext()){
                String id=c.getString(c.getColumnIndex(ContactsContract.Data.CONTACT_ID));
                String number=c.getString(c.getColumnIndex(ContactsContract.Data.DATA3));
                
                if (allContacts.containsKey(id)) {
    				contact = allContacts.get(id);
    			} else {
    				contact = new ContactPicker();
    				allContacts.put(id, contact);
    				contact.setContactPhotoUri(getContactPhotoUri(Long
    						.parseLong(id)));
    				contact.setContactId(id);
    				contact.setContactNumber(number);
    			}
                
                Cursor mCursor=cr.query(
                        ContactsContract.Contacts.CONTENT_URI,
                        new String[]{ContactsContract.Contacts.DISPLAY_NAME},
                        ContactsContract.Contacts._ID+" =?",
                        new String[]{id},
                        null);
                while(mCursor.moveToNext()){
                    contact.setContactName(mCursor.getString(mCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)));
                }
                mCursor.close();
            }	
        }

		contacts.clear();
		contacts.addAll(allContacts.values());

		for (ContactPicker _contact : contacts) {
			if (_contact.getContactName() == null
					&& _contact.getContactNumber() == null) {
				contacts.remove(_contact);
				break;
			}
		}
		return contacts;
	}
}
