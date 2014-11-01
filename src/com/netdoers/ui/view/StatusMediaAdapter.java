package com.netdoers.ui.view;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.netdoers.tellus.R;

public class StatusMediaAdapter extends BaseAdapter {
	List<Bitmap> myData;
	Context context;
	LayoutInflater inflater;

	public StatusMediaAdapter(Context context, List<Bitmap> objects) {
		// TODO Auto-generated constructor stub
		this.myData = objects;
		this.context = context;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return myData.size();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View v = null;
		if (convertView == null) {
			v = inflater.inflate(R.layout.item_list_post_gallery, null);
		} else {
			v = convertView;
		}
		ImageView mImageView = (ImageView) v.findViewById(R.id.item_list_post_gallery_image);
		mImageView.setImageBitmap(getItem(position));
		return v;
	}

	@Override
	public Bitmap getItem(int position) {
		// TODO Auto-generated method stub
		return myData.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}
}