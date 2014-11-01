package com.netdoers.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;
import android.widget.MediaController;
import android.widget.VideoView;

import com.netdoers.tellus.R;

public class VideoActivity extends Activity {

	private ProgressDialog progressDialog;
	private String videourl;
	private VideoView videoView;

	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_video);

		videoView = (VideoView) findViewById(R.id.videoview);
		Intent intent = getIntent();
		String Path = intent.getStringExtra("videourl");

		progressDialog = ProgressDialog.show(VideoActivity.this, "",
				"Buffering video...", true);
		progressDialog.setCancelable(true);

		PlayVideo(Path);

	}

	private void PlayVideo(String videoPath) {
		try {
			getWindow().setFormat(PixelFormat.TRANSLUCENT);
			MediaController mediaController = new MediaController(this);
			mediaController.setAnchorView(videoView);

			Uri video = Uri.parse(videoPath);
			videoView.setMediaController(mediaController);
			videoView.setVideoURI(video);
			videoView.requestFocus();
			videoView.setOnPreparedListener(new OnPreparedListener() {

				public void onPrepared(MediaPlayer mp) {
					progressDialog.dismiss();
					videoView.start();
				}
			});

		} catch (Exception e) {
			progressDialog.dismiss();
			System.out.println("Video Play Error :" + e.toString());
			finish();
		}

	}
}
