
package com.netdoers.ui.view;

import it.sephiroth.android.library.widget.HListView;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.netdoers.beans.Feed;
import com.netdoers.tellus.BuildConfig;
import com.netdoers.tellus.R;
import com.netdoers.utils.AppConstants;
import com.netdoers.utils.ApplicationLoader;
import com.netdoers.utils.NetworkVolley;
import com.netdoers.utils.NetworkVolley.VolleyPostJsonRequest;
import com.netdoers.utils.RequestBuilder;
import com.netdoers.utils.Utilities;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class FeedReportedAdapter extends BaseAdapter {

	Context mContext = null;
	ArrayList<Feed> mFeed = null;
	DisplayImageOptions options;
	ImageLoader imageLoader;
	private String TAG;
	private ProgressDialog mProgressDialog;

	public FeedReportedAdapter(Context mContext, String TAG, ArrayList<Feed> mFeed,
			ImageLoader imageLoader, DisplayImageOptions options) {
		this.mContext = mContext;
		this.mFeed = mFeed;
		this.options = options;
		this.imageLoader = imageLoader;
		this.TAG = TAG;
	}
	
	public void addFeed(ArrayList<Feed> mFeed){
		this.mFeed = mFeed;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mFeed.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		// return coupons.get(position).getTitle();
		return mFeed.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@SuppressWarnings("deprecation")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View v;
		ViewHolder holder = null;
		final Feed mFeedObj;

		if (convertView == null) {
			LayoutInflater li = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = li.inflate(R.layout.item_grid_feed_reported, null);
			holder = new ViewHolder();
			holder.mFeedPostedImage = (CircleImageView) v.findViewById(R.id.item_grid_feed_reported_profile_image);
			holder.mFeedPostedStatus = (TextView) v.findViewById(R.id.item_grid_feed_reported_title);
			holder.mFeedPostedContent = (TextView) v.findViewById(R.id.item_grid_feed_reported_content);
			holder.mFeedGallery = (HListView) v.findViewById(R.id.item_grid_feed_reported_gallery);
			holder.mFeedTime = (TextView) v.findViewById(R.id.item_grid_feed_reported_time);
			holder.mFeedReject = (TextView) v.findViewById(R.id.item_grid_feed_reported_reject);
			holder.mFeedRejectLayout = (RelativeLayout) v.findViewById(R.id.item_grid_feed_reported_reject_layout);
			holder.mFeedRejectImage = (ImageView) v.findViewById(R.id.item_grid_feed_reported_reject_image);			
			holder.mFeedAccept = (TextView) v.findViewById(R.id.item_grid_feed_reported_accept);
			holder.mFeedAcceptLayout = (RelativeLayout) v.findViewById(R.id.item_grid_feed_reported_accept_layout);
			holder.mFeedAcceptImage = (ImageView) v.findViewById(R.id.item_grid_feed_reported_accept_image);
			v.setTag(holder);
		} else {
			v = convertView;
			holder = (ViewHolder) v.getTag();
		}

		mFeedObj = mFeed.get(position);
		
		holder.mFeedPostedStatus.setText(Html.fromHtml("<b>"
				+ mFeedObj.getFeedPostedUser().toString() + "</b>"
				+ " posted in " + "<b>"
				+ mFeedObj.getFeedPostedInGroup() + "</b>"));
		holder.mFeedPostedContent.setText(mFeedObj.getFeedContent());
		holder.mFeedTime.setText(mFeedObj.getFeedTime());
		
		setFeedGallery(mContext, mFeed, position, holder.mFeedGallery, imageLoader,
				options);

		/*((Activity) mContext).runOnUiThread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				imageLoader.displayImage(mFeed.get(position)
						.getFeedPostedImage(),holder.mFeedPostedImage, options);
			}
		});*/
		
		imageLoader.displayImage(mFeed.get(position)
				.getFeedPostedImage(),holder.mFeedPostedImage, options);
		
		
		setAcceptEventListeners(holder.mFeedAcceptLayout, mFeedObj, position,
				mContext);
		
		setRejectEventListeners(holder.mFeedRejectLayout, mFeedObj, position,
				mContext);
		return v;
	}
	
	private void setFeedGallery(Context mContext, ArrayList<Feed> mFeed,
			int position, HListView mFeedGallery,ImageLoader imageLoader, DisplayImageOptions options) {
		if(mFeed.get(position).getFeedMedia().size() > 0){
			mFeedGallery.setVisibility(View.VISIBLE);
			FeedGalleryAdapter mFeedGalleryAdapter = new FeedGalleryAdapter(mContext, mFeed.get(position).getFeedMedia(), imageLoader, options);
			mFeedGallery.setAdapter(mFeedGalleryAdapter);	
		}else{
			mFeedGallery.setVisibility(View.GONE);
		}
	}
	
	private void setRejectEventListeners(
			final RelativeLayout mFeedRejectLayout, final Feed mFeedObj,
			final int position, final Context mContext) {
		
		mFeedRejectLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				volleyAcceptReject(mFeed,position,mFeedObj,mContext,"0");
				// TODO Auto-generated method stub
			}
		});
	}
	
	private void setAcceptEventListeners(
			final RelativeLayout mFeedAcceptLayout, final Feed mFeedObj,
			final int position, final Context mContext) {
		
		mFeedAcceptLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				volleyAcceptReject(mFeed,position,mFeedObj,mContext,"1");
				// TODO Auto-generated method stub
			}
		});
	}
	
	static class ViewHolder{
		CircleImageView mFeedPostedImage;
		TextView mFeedPostedStatus;
		TextView mFeedPostedContent;
		HListView mFeedGallery;
		ImageView mFeedRejectImage;
		ImageView mFeedAcceptImage;
		TextView mFeedTime;
		TextView mFeedContent;
		TextView mFeedReject;
		TextView mFeedAccept;
		RelativeLayout mFeedAcceptLayout, mFeedRejectLayout;
		}
	
	/*
	 * Network I/0 - VOLLEY
	 */
	
	private void volleyAcceptReject(final ArrayList<Feed> mFeed, final int position,
			final Feed mFeedObj, final Context mContext, final String acceptReject) {
		final String TAG = "volleyAcceptReject";
		if(Utilities.isInternetConnected()){
			showProgressDialog("Please wait..", mContext);
			NetworkVolley nVolley = new NetworkVolley();
			JSONObject mJSONObject = RequestBuilder.getPostReportActFeedData(mFeedObj
					.getFeedId(),acceptReject);
			Log.i(TAG, AppConstants.URLS.URL_POST_FEED_REPORT_ACT);
			Log.i(TAG, mJSONObject.toString());
			VolleyPostJsonRequest req = nVolley.new VolleyPostJsonRequest(AppConstants.URLS.URL_POST_FEED_REPORT_ACT, mJSONObject,
					new Listener<JSONObject>() {
						@Override
						public void onResponse(JSONObject response) {
							try {
								VolleyLog.v("Response:%n %s", response.toString(4));
								Log.i(TAG, response.toString());
								if (response.getBoolean("success")) {
									dismissProgressDialog();
									mFeed.remove(position);
									notifyDataSetChanged();
									if (BuildConfig.DEBUG) {
										VolleyLog.v("Response:%n %s",
												response.toString(4));
										Log.i(TAG, response.toString());
									}
								}
							} catch (JSONException e) {
								e.printStackTrace();
								dismissProgressDialog();
							}
						}
					}, new ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError error) {
							dismissProgressDialog();
							VolleyLog.e("Error: ", error.getMessage());
							if (BuildConfig.DEBUG) {
								Log.i(TAG, "Error : "
										+ mFeedObj.getFeedIsLike().toString());
								Log.i(TAG, "Volley Error : " + error.getMessage());
								VolleyLog.e("Error: ", error.getMessage());
							}
						}
					});
			ApplicationLoader.getInstance().addToRequestQueue(req, TAG);
		}
	}
	
	private void showProgressDialog(String str, Context mContext){
		mProgressDialog = new ProgressDialog(mContext);
		mProgressDialog.setCancelable(true);
		mProgressDialog.setMessage(str);
		mProgressDialog.show();
	}
	
	private void dismissProgressDialog(){
		if(mProgressDialog!=null)
			mProgressDialog.dismiss();
	}
}

