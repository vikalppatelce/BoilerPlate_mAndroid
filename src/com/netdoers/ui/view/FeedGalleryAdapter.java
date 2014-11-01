package com.netdoers.ui.view;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.netdoers.beans.FeedMedia;
import com.netdoers.tellus.R;
import com.netdoers.ui.MediaActivity;
import com.netdoers.utils.Utilities;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class FeedGalleryAdapter extends BaseAdapter {
	ArrayList<FeedMedia> mFeedMedia;
	Context mContext;
	LayoutInflater mInflater;
	DisplayImageOptions options;
	ImageLoader imageLoader;
	private String TAG = FeedGalleryAdapter.class.getSimpleName();

	public FeedGalleryAdapter(Context mContext, ArrayList<FeedMedia> mFeedMedia,
			ImageLoader imageLoader, DisplayImageOptions options) {
		// TODO Auto-generated constructor stub
		this.mFeedMedia = mFeedMedia;
		this.mContext = mContext;
		this.options = options;
		this.imageLoader = imageLoader;
		mInflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mFeedMedia.size();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View v = null;
		if (convertView == null) {
			v = mInflater.inflate(R.layout.item_grid_feed_gallery, null);
		} else {
			v = convertView;
		}
		final ImageView mFeedGalleryImage = (ImageView) v
				.findViewById(R.id.item_grid_feed_gallery_image);
		final ImageView mFeedGalleryVideoImage = (ImageView) v
				.findViewById(R.id.item_grid_feed_gallery_image_video);
		
		if(mFeedMedia.get(position).getFeedIsVideo().equalsIgnoreCase("image")){
			mFeedGalleryVideoImage.setVisibility(View.GONE);
			((Activity) mContext).runOnUiThread(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					imageLoader.displayImage(mFeedMedia.get(position)
							.getFeedMediaPath(), mFeedGalleryImage, options);
					if(mFeedMedia.size() == 1)
						scaleImage(mFeedGalleryImage);
				}
			});
		}else{
			mFeedGalleryVideoImage.setVisibility(View.VISIBLE);
			((Activity) mContext).runOnUiThread(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					imageLoader.displayImage(mFeedMedia.get(position)
							.getFeedVideoThumbail(), mFeedGalleryImage, options);
									
					if(mFeedMedia.size() == 1)
						scaleImage(mFeedGalleryImage);
				}
			});
		}
		
		mFeedGalleryImage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ArrayList<String> path = new ArrayList<String>();
				ArrayList<String> type = new ArrayList<String>();
				for (int i = 0; i < mFeedMedia.size(); i++) {
					path.add(mFeedMedia.get(i).getFeedMediaPath());
					type.add(mFeedMedia.get(i).getFeedIsVideo());
				}
				Intent mIntent = new Intent(mContext, MediaActivity.class);
				mIntent.putStringArrayListExtra("url", path);
				mIntent.putStringArrayListExtra("type", type);
				mIntent.putExtra("current_image", position);
				mContext.startActivity(mIntent);
			}
		});
		return v;
	}

	@Override
	public FeedMedia getItem(int position) {
		// TODO Auto-generated method stub
		return mFeedMedia.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	private void scaleImage(ImageView view) {
		int deviceWidth = Utilities.getDeviceWidth();
		Drawable drawing = view.getDrawable();
		if (drawing == null) {
			return;
		}
		Bitmap bitmap = ((BitmapDrawable) drawing).getBitmap();

		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		int bounding_x = Utilities.getDeviceGalleryFitWidth();
		int bounding_y = 360;

		view.setScaleType(ScaleType.MATRIX);

		float xScale = ((float) bounding_x) / width;
		float yScale = ((float) bounding_y) / height;

		Matrix matrix = new Matrix();
		matrix.postScale(xScale, yScale);

		Bitmap scaledBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height,matrix, true);
		width = scaledBitmap.getWidth();
		height = scaledBitmap.getHeight();
		BitmapDrawable result = new BitmapDrawable(scaledBitmap);

		view.setImageDrawable(result);

		FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
		params.width = deviceWidth;
		params.height = height;

		view.setLayoutParams(params);
	}
}