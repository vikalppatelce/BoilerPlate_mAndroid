package com.netdoers.ui.view;

import java.util.Collections;
import java.util.List;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.netdoers.tellus.R;

public class ChooserView {
	private AppAdapter mAdapter = null;
	private Intent mIntent = new Intent(Intent.ACTION_SEND);
	private GridView mGridView;

	public void initUi(final Context mContext, String strShare) {
		// TODO Auto-generated method stub
		final Dialog dialog = new Dialog(mContext);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		WindowManager.LayoutParams WMLP = dialog.getWindow().getAttributes();
		WMLP.gravity = Gravity.CENTER;
		dialog.getWindow().setAttributes(WMLP);
		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		dialog.setCanceledOnTouchOutside(true);
		dialog.setContentView(R.layout.activity_chooser);
		dialog.setCancelable(true);
		mGridView = (GridView) dialog.findViewById(R.id.activity_chooser_gridview);
		PackageManager pm = mContext.getPackageManager();
//		mIntent.putExtra(Intent.EXTRA_EMAIL,new String[] { "velmurugan@androidtoppers.com" });
//		mIntent.putExtra(Intent.EXTRA_SUBJECT, "Hi");
		mIntent.putExtra(Intent.EXTRA_TEXT, strShare);
		mIntent.setType("text/plain");
		List<ResolveInfo> launchables = pm.queryIntentActivities(mIntent, 0);
		Collections.sort(launchables, new ResolveInfo.DisplayNameComparator(pm));
		mAdapter = new AppAdapter(mContext, pm, launchables);
		mGridView.setAdapter(mAdapter);
		mGridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				// TODO Auto-generated method stub
				ResolveInfo launchable = mAdapter.getItem(position);
				ActivityInfo activity = launchable.activityInfo;
				ComponentName name = new ComponentName(
						activity.applicationInfo.packageName, activity.name);
				mIntent.addCategory(Intent.CATEGORY_LAUNCHER);
				mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
						| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
				mIntent.setComponent(name);
				mContext.startActivity(mIntent);
				dialog.dismiss();
			}
		});
		dialog.show();
	}

	class AppAdapter extends ArrayAdapter<ResolveInfo> {
		private PackageManager pm = null;
		private Context mContext;

		AppAdapter(Context mContext, PackageManager pm, List<ResolveInfo> apps) {
			super(mContext, R.layout.item_chooser, apps);
			this.pm = pm;
			this.mContext = mContext;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = newView(parent);
			}
			bindView(position, convertView);
			return (convertView);
		}

		private View newView(ViewGroup parent) {
			LayoutInflater li = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			return (li.inflate(R.layout.item_chooser, parent,
					false));
		}

		private void bindView(int position, View row) {
			TextView label = (TextView) row
					.findViewById(R.id.item_chooser_title);
			label.setText(getItem(position).loadLabel(pm));
			ImageView icon = (ImageView) row
					.findViewById(R.id.item_chooser_icon);
			icon.setImageDrawable(getItem(position).loadIcon(pm));
		}
	}
}