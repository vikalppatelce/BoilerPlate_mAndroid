package com.netdoers.ui.view;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.netdoers.beans.Feed;
import com.netdoers.beans.FeedComment;
import com.netdoers.beans.FeedMedia;
import com.netdoers.tellus.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class FeedCommentAdapter extends BaseAdapter {
	ArrayList<FeedComment> mFeedComment;
	Context mContext;
	LayoutInflater mInflater;
	DisplayImageOptions options;
	ImageLoader imageLoader;

	public FeedCommentAdapter(Context mContext, ArrayList<FeedComment> mFeedComment,
			ImageLoader imageLoader, DisplayImageOptions options) {
		// TODO Auto-generated constructor stub
		this.mFeedComment = mFeedComment;
		this.mContext = mContext;
		this.options = options;
		this.imageLoader = imageLoader;
		mInflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public void addFeed(ArrayList<FeedComment> mFeedComment){
		this.mFeedComment = mFeedComment;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mFeedComment.size();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View v = null;
		final CircleImageView mFeedUserImage;
		final TextView mFeedCommentText;
		if (convertView == null) {
			v = mInflater.inflate(R.layout.item_list_comment, null);
		} else {
			v = convertView;
		}
		mFeedUserImage = (CircleImageView) v
				.findViewById(R.id.item_list_comment_user_image);
		mFeedCommentText = (TextView)v.findViewById(R.id.item_list_comment_content);
		
		mFeedCommentText.setText(mFeedComment.get(position).getFeedUserComment());
		((Activity) mContext).runOnUiThread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				imageLoader.displayImage(mFeedComment.get(position)
						.getFeedUserProfile(), mFeedUserImage, options);
			}
		});
		
		return v;
	}

	@Override
	public FeedComment getItem(int position) {
		// TODO Auto-generated method stub
		return mFeedComment.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}
}