package com.netdoers.ui;

import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources.NotFoundException;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.widget.TextView;

import com.netdoers.tellus.R;

public class AboutUsActivity extends ActionBarActivity {

	private TextView mTextView;
	private ActionBar mActionBar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		initUi();
		setActionBar();
		setAboutUs();
	}
	
	private void initUi(){
		mTextView = (TextView)findViewById(R.id.activity_about_txt);
	}
	
	private void setActionBar(){
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		mActionBar = getSupportActionBar();
		try {
			mActionBar.setTitle(getResources().getString(R.string.about) + " v" + getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
		} catch (NotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void setAboutUs(){
		mTextView.setText(Html.fromHtml(getResources().getString(R.string.about_us)));
	}
}
