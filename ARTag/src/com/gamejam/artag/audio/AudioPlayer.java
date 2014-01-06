package com.gamejam.artag.audio;

import com.gamejam.artag.R;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;

public class AudioPlayer {

	private MediaPlayer mPlayer;

	private int pausedPos = 0;
	
	public void stop() {
		if(mPlayer != null) {
			mPlayer.release();
			mPlayer = null;
			pausedPos = 0;
		}
	}
	
	public void play(Context c) {
		stop();
		
		mPlayer = MediaPlayer.create(c, R.raw.gunshot);
		
		mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
			
			@Override
			public void onCompletion(MediaPlayer mp) {
				stop();
			}
		});
		
		mPlayer.start();
	}
	
	public int pause() {
		if(mPlayer != null) {
			if(mPlayer.isPlaying()) {
				pausedPos = mPlayer.getCurrentPosition();
				Log.d("pausedPos", "" + pausedPos);
				mPlayer.pause();
				return 1;
			} else {
				mPlayer.seekTo(pausedPos);
				mPlayer.start();
				return 2;
			}
		}
		
		return 0;
	}
}
