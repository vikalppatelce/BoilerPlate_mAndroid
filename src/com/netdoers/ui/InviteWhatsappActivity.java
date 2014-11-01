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
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.netdoers.beans.Contact;
import com.netdoers.beans.ContactPicker;
import com.netdoers.tellus.R;
import com.netdoers.utils.AppConstants;
import com.netdoers.utils.SyncContact;
import com.netdoers.utils.Utilities;

public final class InviteWhatsappActivity extends ActionBarActivity implements SearchView.OnQueryTextListener {

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
		setContentView(R.layout.activity_invite_whatsapp);
		initUi();
		setUpActionBar();
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
	
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_group_search, menu);
		MenuItem  searchMenuItem = menu.findItem(R.id.action_search);
	    SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchMenuItem);
	    searchView.setOnQueryTextListener(this);
		return super.onCreateOptionsMenu(menu);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
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

		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				String mWhatsappId = view.getTag(R.id.TAG_WHATSAPP_ID).toString();
				Uri uri = Uri.parse("smsto:" + mWhatsappId);
				Intent mIntent = new Intent(Intent.ACTION_SENDTO,uri);
//				mIntent.putExtra("sms_body", "Tell Us");
				mIntent.putExtra(Intent.EXTRA_TEXT, "tell us");
				mIntent.putExtra(Intent.EXTRA_SUBJECT, "Sub : tell us");
			    mIntent.setPackage("com.whatsapp");
			    if (mIntent != null) 
			        startActivity(mIntent);
			    
			}
		});
	}
	
	public void setUpActionBar() {
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setTitle(getResources().getString(R.string.invite_whatsapp_title));
	}

	public void onClear(View v) {
		mFilter.setText("");
	}

	private class AsyncLoadContacts extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			mProgressLayout.setVisibility(View.VISIBLE);
		}

		@Override
		protected Void doInBackground(Void... params) {
			contacts = SyncContact.getWhatsappNewApi(new LinkedHashMap<String, ContactPicker>(),new ArrayList<ContactPicker>());
			if(contacts!=null && contacts.size() > 0){
				mAdapter = new ContactAdapter(InviteWhatsappActivity.this, R.id.contactList, contacts);
				mAdapter.notifyDataSetChanged();
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
				view = vi.inflate(R.layout.item_invite_whatsapp, null);
			}
			final ContactPicker contact = contactList.get(position);
			if (contact != null) {
				TextView name = (TextView) view.findViewById(R.id.name);
				ImageView thumb = (ImageView) view.findViewById(R.id.thumb);
				TextView number = (TextView) view.findViewById(R.id.number);

				thumb.setImageURI(contact.getContactPhotoUri());

				if (thumb.getDrawable() == null)
					thumb.setImageResource(R.drawable._def_contact);

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
			}

			view.setTag(R.id.TAG_WHATSAPP_ID, contact.getContactNumber());
			
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

	@Override
	public boolean onQueryTextChange(String str) {
		// TODO Auto-generated method stub
		mAdapter.getFilter().filter(str);
		return true;
	}

	@Override
	public boolean onQueryTextSubmit(String str) {
		// TODO Auto-generated method stub
		mAdapter.getFilter().filter(str);
		return true;
	}

}
