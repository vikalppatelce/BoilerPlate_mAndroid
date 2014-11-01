package com.netdoers.ui;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

import com.netdoers.tellus.R;
import com.netdoers.utils.GalleryMedia;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;


public class MediaActivity extends ActionBarActivity {

	ImageView imageView;
	Uri uri;
	public ViewPager mPager;
	public int NUM_PAGES = 0;
	public ArrayList<String> picPaths;
	public ArrayList<String> fileType;
	public TextView serviceName,serviceLocation,pageTotal,pageCurrent;
	String strServiceName,strServiceLocation,strCurrPage;
	public int currentPage = 0;
	DisplayImageOptions options;
	ImageLoader imageLoader;
	private ActionBar mActionBar;
	private TextView mCurrentPage, mTotalPage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_media);
		mPager = (ViewPager)findViewById(R.id.viewPager);
	
		setUniversalImageLoader();
		setCustomActionBar();
		picPaths = getIntent().getStringArrayListExtra("url");
		fileType = getIntent().getStringArrayListExtra("type");
		currentPage = getIntent().getIntExtra("current_image",0);
		NUM_PAGES = picPaths.size();

		mTotalPage.setText(" of " +NUM_PAGES);
		mCurrentPage.setText(" " +currentPage);
		mPager.setAdapter(new MyPagerAdapter());
		mPager.setPageTransformer(true, new DepthPageTransformer());
		mPager.setCurrentItem(currentPage);
	}
	
	private void setUniversalImageLoader() {
		imageLoader = ImageLoader.getInstance();
		imageLoader.init(ImageLoaderConfiguration.createDefault(this));
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable._def_contact)
				.showImageForEmptyUri(R.drawable._def_contact)
				.showImageOnFail(R.drawable._def_contact).cacheInMemory()
				.cacheOnDisc().build();
	}
	
	private void setCustomActionBar(){
		mActionBar = getSupportActionBar();
		mActionBar.setDisplayShowHomeEnabled(false);
		mActionBar.setDisplayShowTitleEnabled(false);
		LayoutInflater mInflater = LayoutInflater.from(this);
		View mCustomView = mInflater.inflate(R.layout.actionbar_media, null);
		mActionBar.setCustomView(mCustomView);
		mActionBar.setDisplayShowCustomEnabled(true);
		mCurrentPage = (TextView)mCustomView.findViewById(R.id.actionbar_media_current);
		mTotalPage = (TextView)mCustomView.findViewById(R.id.actionbar_media_total);
	}
	/**
     * A simple pager adapter that represents ImageView objects, in
     * sequence.
     */
    private class MyPagerAdapter extends PagerAdapter {
        public int getCount() {
            return NUM_PAGES;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
        	final ImageView imageView = new ImageView(MediaActivity.this);
        	imageView.setScaleType(ScaleType.FIT_CENTER);
        	Bitmap myImg = null;
        	String extension = "";
        	int j = picPaths.get(position).lastIndexOf('.');
			if (j >= 0) {
				extension = picPaths.get(position).substring(j + 1);
			}
			if (extension.equalsIgnoreCase("mp4")) {
				try {
					((MediaActivity.this)).runOnUiThread(new Runnable() {
		    			@Override
		    			public void run() {
		    				// TODO Auto-generated method stub
		    				imageLoader.displayImage(picPaths.get(position), imageView, options);
		    			}
		    		});
					
					myImg = GalleryMedia.drawableToBitmap(imageView.getDrawable());
					Resources r = getResources();
					Drawable[] layers = new Drawable[2];
					layers[0] = new BitmapDrawable(myImg);
					layers[1] = r.getDrawable(R.drawable.play_icon);
					LayerDrawable layerDrawable = new LayerDrawable(layers);
					myImg=GalleryMedia.drawableToBitmap(GalleryMedia.geSingleDrawable(layerDrawable));
					
					imageView.setImageBitmap(myImg);
					
					imageView.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							Intent mIntent = new Intent(MediaActivity.this, VideoActivity.class);
							mIntent.putExtra("videourl", picPaths.get(position));
							startActivity(mIntent);
						}
					});
					}
				catch (Exception e) {
					Log.e("LoadPathCursor", e.toString());
				}
			 } else {
				((MediaActivity.this)).runOnUiThread(new Runnable() {
	    			@Override
	    			public void run() {
	    				// TODO Auto-generated method stub
	    				imageLoader.displayImage(picPaths.get(position), imageView, options);
	    			}
	    		});
			}
        	container.addView(imageView);
			return imageView;
        	}

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
            mCurrentPage.setText(String.valueOf(position+1));
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override
        public void finishUpdate(View arg0) {
        }

        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {
        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public void startUpdate(View arg0) {
        }
    }
	public class DepthPageTransformer implements ViewPager.PageTransformer {
        private static final float MIN_SCALE = 0.75f;

        @SuppressLint("NewApi")
		public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();

            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setAlpha(0);

            } else if (position <= 0) { // [-1,0]
                // Use the default slide transition when moving to the left page
                view.setAlpha(1);
                view.setTranslationX(0);
                view.setScaleX(1);
                view.setScaleY(1);

            } else if (position <= 1) { // (0,1]
                // Fade the page out.
                view.setAlpha(1 - position);

                // Counteract the default slide transition
                view.setTranslationX(pageWidth * -position);

                // Scale the page down (between MIN_SCALE and 1)
                float scaleFactor = MIN_SCALE
                        + (1 - MIN_SCALE) * (1 - Math.abs(position));
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);

            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                view.setAlpha(0);
            }
        }
    }
//    EA P3A01
}
