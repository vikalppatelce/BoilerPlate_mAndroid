package com.netdoers.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Data;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.netdoers.beans.ContactPicker;
import com.netdoers.tellus.R;
import com.netdoers.utils.Utilities;

public final class InviteContactActivity extends ActionBarActivity {

	private static ArrayList<ContactPicker> contacts = null;
	private LinkedHashMap<String, ContactPicker> allContacts = new LinkedHashMap<String, ContactPicker>();
	private ContactAdapter mAdapter = null;
	private ListView mListView;
	Button mBtnDone, mBtnCancel;
	RelativeLayout mProgressLayout;
	EditText mFilter;
	HashMap<String, Integer> alphaIndexer;
	String[] sections;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_invite_contact);
		initUi();
		setUpCustomActionBar();
		setEventListener();


		contacts = null;
		if (contacts == null) {
			contacts = new ArrayList<ContactPicker>();
			new AsyncLoadContacts().execute();
		} else {
			mAdapter = new ContactAdapter(this, R.id.contactList, contacts);
			mListView.setAdapter(mAdapter);
		}

	}
	
	private void initUi(){
		mProgressLayout = (RelativeLayout) findViewById(R.id.progress_layout);
		mListView = (ListView) findViewById(R.id.contactList);
		mFilter = (EditText) findViewById(R.id.search_txt);
		mProgressLayout.setVisibility(View.GONE);
		mFilter.setEnabled(false);
	}

	private void setEventListener(){
		mProgressLayout.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return true;
			}
		});
		
		mFilter.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// call the filter with the current text on the editbox
				mAdapter.getFilter().filter(s.toString());
			}
		});
	}
	
	public void setUpCustomActionBar() {
		ActionBar bar = getSupportActionBar();
		View actionBarView = getLayoutInflater().inflate(
				R.layout.actionbar_invite_contact, null);
		bar.setCustomView(actionBarView);
		bar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

		bar.setHomeButtonEnabled(false);
		bar.setDisplayShowHomeEnabled(false);
		bar.setDisplayHomeAsUpEnabled(false);
	}

	public void onCancel(View v) {
		Utilities.hideSoftKeyboard(InviteContactActivity.this);
		Intent intent = new Intent();
		setResult(RESULT_CANCELED, intent);
		finish();
	}

	public void onDone(View v) {
		Utilities.hideSoftKeyboard(InviteContactActivity.this);
		setSelctedcontacts();
	}

	public void onClear(View v) {
		mFilter.setText("");
	}

	private void setSelctedcontacts() {
		ArrayList<ContactPicker> selectedList = new ArrayList<ContactPicker>();
		Intent intent = new Intent();
		ArrayList<ContactPicker> contactList = mAdapter.originalList;
		for (int i = 0; i < contactList.size(); i++) {
			ContactPicker contact = contactList.get(i);
			if (contact.isSelected()) {
				selectedList.add(contact);
			}
			if (selectedList.size() > 0) {
				intent.putParcelableArrayListExtra(ContactPicker.CONTACTS_DATA,
						selectedList);
				setResult(RESULT_OK, intent);
			} else {
				setResult(RESULT_CANCELED, intent);
			}
		}
		finish();
	}

	@Override
	public void onBackPressed() {
		ArrayList<ContactPicker> selectedList = new ArrayList<ContactPicker>();
		Intent intent = new Intent();
		ArrayList<ContactPicker> contactList = mAdapter.originalList;
		for (int i = 0; i < contactList.size(); i++) {
			ContactPicker contact = contactList.get(i);
			if (contact.isSelected()) {
				selectedList.add(contact);
			}
			if (selectedList.size() > 0) {
				intent.putParcelableArrayListExtra(ContactPicker.CONTACTS_DATA,
						selectedList);
				setResult(RESULT_OK, intent);
			} else {
				setResult(RESULT_CANCELED, intent);
			}
		}

		finish();

	};

	/**
	 * @see Get Contacts for 2.2+ DATA.HAS_PHONE_NUMBER was not added in < 3.0
	 */
	@SuppressLint("InlinedApi")
	private void getContactsOldApi() {
		Uri uri = ContactsContract.Contacts.CONTENT_URI;
		String[] projection = new String[] { ContactsContract.Contacts._ID,
				ContactsContract.Contacts.DISPLAY_NAME };
		String selection = ContactsContract.Contacts.HAS_PHONE_NUMBER + " > '"
				+ ("0") + "'";
		String[] selectionArgs = null;
		String sortOrder = ContactsContract.Contacts.DISPLAY_NAME
				+ " COLLATE LOCALIZED ASC";

		ContentResolver contectResolver = getContentResolver();

		Cursor cursor = contectResolver.query(uri, projection, selection,
				selectionArgs, sortOrder);
		ContactPicker contact;
		// Load contacts one by one
		if (cursor.moveToFirst()) {
			while (!cursor.isAfterLast()) {
				contact = new ContactPicker();
				String id = cursor.getString(cursor
						.getColumnIndex(ContactsContract.Contacts._ID));

				contact.setContactPhotoUri(getContactPhotoUri(Long
						.parseLong(id)));
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
						if (contact.getContactNumber().toString().length() == 0) {
							contact.setContactNumber(cursorPhone
									.getString(
											cursorPhone
													.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
									.replaceAll("\\D", ""));
						} else {
							contact.setContactNumber(contact
									.getContactNumber()
									.toString()
									.concat(", ")
									.concat(cursorPhone
											.getString(
													cursorPhone
															.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
											.replaceAll("\\D", "")));
						}
					} while (cursorPhone.moveToNext());
				}
				if (cursorPhone != null) {
					cursorPhone.close();
				}
				contact.setContactName(cursor.getString(cursor
						.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)));
				allContacts.put(id, contact);
				cursor.moveToNext();
			}
		}
		cursor.close();
		contacts.clear();
		contacts.addAll(allContacts.values());

		for (ContactPicker _contact : contacts) {

			if (_contact.getContactName() == null
					&& _contact.getContactNumber() == null) {
				// && _contact.getContactEmail() == null
				contacts.remove(_contact);
				break;
			}
		}
		mAdapter = new ContactAdapter(this, R.id.contactList, contacts);
		mAdapter.notifyDataSetChanged();
	}

	/**
	 * @see Get Contacts for 3.0+
	 * 
	 */
	@SuppressLint("InlinedApi")
	private void getContactsNewApi() {
		ContentResolver cr = getContentResolver();
		String selection = Data.HAS_PHONE_NUMBER + " > '" + ("0") + "'";
		Cursor cur = cr.query(Data.CONTENT_URI, new String[] { Data.CONTACT_ID,
				Data.MIMETYPE, Email.ADDRESS, Contacts.DISPLAY_NAME,
				Phone.NUMBER }, selection, null, Contacts.DISPLAY_NAME);
		ContactPicker contact;
		if (cur.getCount() > 0) {
			while (cur.moveToNext()) {
				String id = cur.getString(cur.getColumnIndex(Data.CONTACT_ID));
				String mimeType = cur.getString(cur
						.getColumnIndex(Data.MIMETYPE));
				if (allContacts.containsKey(id)) {
					contact = allContacts.get(id);
				} else {
					contact = new ContactPicker();
					allContacts.put(id, contact);
					contact.setContactPhotoUri(getContactPhotoUri(Long
							.parseLong(id)));
					contact.setContactId(id);
				}
				if (mimeType.equals(StructuredName.CONTENT_ITEM_TYPE))
					contact.setContactName(cur.getString(cur
							.getColumnIndex(Contacts.DISPLAY_NAME)));

				if (mimeType.equals(Phone.CONTENT_ITEM_TYPE)) {
					if (contact.getContactNumber().toString().length() == 0) {
						contact.setContactNumber(cur.getString(cur.getColumnIndex(Phone.NUMBER)).replaceAll("\\D", ""));
					} else {
						contact.setContactNumber(contact.getContactNumber().toString().concat(", ").concat(cur.getString(cur.getColumnIndex(Phone.NUMBER)).replaceAll("\\D", "")));
					}
				}
				// if (mimeType.equals(Email.CONTENT_ITEM_TYPE))
				// // set email
				// contact.setContactEmail(cur.getString(cur.getColumnIndex(Email.ADDRESS)));
			}
		}

		cur.close();
		contacts.clear();
		contacts.addAll(allContacts.values());

		for (ContactPicker _contact : contacts) {
			if (_contact.getContactName() == null
					&& _contact.getContactNumber() == null) {
				// && _contact.getContactEmail() == null
				contacts.remove(_contact);
				break;
			}
		}

		mAdapter = new ContactAdapter(this, R.id.contactList, contacts);
		mAdapter.notifyDataSetChanged();
	}

	public Uri getContactPhotoUri(long contactId) {
		Uri photoUri = ContentUris.withAppendedId(Contacts.CONTENT_URI,
				contactId);
		photoUri = Uri.withAppendedPath(photoUri,
				Contacts.Photo.CONTENT_DIRECTORY);
		return photoUri;
	}

	private class AsyncLoadContacts extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			mProgressLayout.setVisibility(View.VISIBLE);
		}

		@Override
		protected Void doInBackground(Void... params) {
			if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
				getContactsNewApi();
			} else {
				getContactsOldApi();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			mListView.setAdapter(mAdapter);
			mProgressLayout.setVisibility(View.GONE);
			mFilter.setEnabled(true);
		}
	}

	// Contact adapter
	public class ContactAdapter extends ArrayAdapter<ContactPicker> implements
			SectionIndexer {

		private ArrayList<ContactPicker> contactList;
		private ArrayList<ContactPicker> originalList;
		private ContactFilter filter;

		public ContactAdapter(Context context, int textViewResourceId,
				ArrayList<ContactPicker> items) {
			super(context, textViewResourceId, items);
			this.contactList = new ArrayList<ContactPicker>();
			this.originalList = new ArrayList<ContactPicker>();
			this.contactList.addAll(items);
			this.originalList.addAll(items);
			// indexing
			alphaIndexer = new HashMap<String, Integer>();
			int size = contactList.size();
			for (int x = 0; x < size; x++) {
				String s = contactList.get(x).getContactName();
				if (s != null && !TextUtils.isEmpty(s)) {
					String ch = s.substring(0, 1);
					ch = ch.toUpperCase();
					alphaIndexer.put(ch, x);
				}
			}

			Set<String> sectionLetters = alphaIndexer.keySet();
			// create a list from the set to sort
			ArrayList<String> sectionList = new ArrayList<String>(
					sectionLetters);
			Collections.sort(sectionList);
			sections = new String[sectionList.size()];
			sectionList.toArray(sections);
		}

		@Override
		public Filter getFilter() {
			if (filter == null) {
				filter = new ContactFilter();
			}
			return filter;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;

			if (view == null) {
				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = vi.inflate(R.layout.item_invite_contact, null);
			}
			final ContactPicker contact = contactList.get(position);
			if (contact != null) {
				TextView name = (TextView) view.findViewById(R.id.name);
				ImageView thumb = (ImageView) view.findViewById(R.id.thumb);
				TextView number = (TextView) view.findViewById(R.id.number);

				thumb.setImageURI(contact.getContactPhotoUri());

				if (thumb.getDrawable() == null)
					thumb.setImageResource(R.drawable._def_contact);

				final CheckBox nameCheckBox = (CheckBox) view
						.findViewById(R.id.checkBox);

				if (!TextUtils.isEmpty(mFilter.getText().toString())) {
					name.setText(Utilities.highlight(mFilter.getText()
							.toString(), contact.getContactName()));
				} else {
					name.setText(contact.getContactName());
				}
				if (StringUtils.isNumeric(mFilter.getText().toString())) {
					number.setText(Utilities.highlight(mFilter.getText()
							.toString(), contact.getContactNumber()));
				} else {
					number.setText(contact.getContactNumber());
				}
				nameCheckBox
						.setOnCheckedChangeListener(new OnCheckedChangeListener() {
							@Override
							public void onCheckedChanged(
									CompoundButton buttonView, boolean isChecked) {

								contact.setSelected(nameCheckBox.isChecked());
							}
						});
				nameCheckBox.setChecked(contact.isSelected());
			}

			return view;
		}

		@Override
		public int getPositionForSection(int section) {
			return alphaIndexer.get(sections[section]);
		}

		@Override
		public int getSectionForPosition(int position) {
			return 0;
		}

		@Override
		public Object[] getSections() {
			return sections;
		}

		// Contacts filter
		private class ContactFilter extends Filter {

			@Override
			protected FilterResults performFiltering(CharSequence constraint) {

				constraint = constraint.toString().toLowerCase();
				FilterResults result = new FilterResults();
				if (constraint != null && constraint.toString().length() > 0) {
					ArrayList<ContactPicker> filteredItems = new ArrayList<ContactPicker>();

					for (int i = 0, l = originalList.size(); i < l; i++) {
						ContactPicker contact = originalList.get(i);
						if (contact.toString().toLowerCase()
								.contains(constraint)) {
							filteredItems.add(contact);
						}
					}
					result.count = filteredItems.size();
					result.values = filteredItems;
				} else {
					synchronized (this) {
						result.values = originalList;
						result.count = originalList.size();
					}
				}
				return result;
			}

			@SuppressWarnings("unchecked")
			@Override
			protected void publishResults(CharSequence constraint,
					FilterResults results) {

				contactList = (ArrayList<ContactPicker>) results.values;
				notifyDataSetChanged();
				clear();
				for (int i = 0, l = contactList.size(); i < l; i++)
					add(contactList.get(i));
				notifyDataSetInvalidated();
			}
		}

	}

}
