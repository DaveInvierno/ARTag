package com.gamejam.artag.gamestates;

import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ProgressBar;

import com.gamejam.artag.imageproc.FaceView;
import com.gamejam.artag.imageproc.Preview;

public class InGameStateActivity extends Activity {
	private FrameLayout layout;
    private FaceView mFaceView;
    private Preview mPreview;

    private ProgressBar mHealthBar;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Hide the window title.
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        super.onCreate(savedInstanceState);

        //mHealthBar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        
        // Create our Preview view and set it as the content of our activity.
        try {
            layout = new FrameLayout(this);
            mFaceView = new FaceView(this);
            mPreview = new Preview(this, mFaceView);
            layout.addView(mPreview);
            layout.addView(mFaceView);
            //layout.addView(mHealthBar);
            setContentView(layout);
        } catch (IOException e) {
            e.printStackTrace();
            new AlertDialog.Builder(this).setMessage(e.getMessage()).create().show();
        }
        
    }
    
    @Override
    public void onDestroy() {
    	super.onDestroy();
    	
    	mFaceView.cleanUp();
    }

}
