package com.gamejam.artag.gamestates;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.gamejam.artag.R;
import com.gamejam.artag.imageproc.FaceRecognition;

public class MainMenuStateActivity extends Activity {

	private Button mCreateBtn;
	private Button mJoinBtn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// Hide the window title.
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // Use full screen
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_menu_state);
		
		mCreateBtn = (Button) findViewById(R.id.create_btn);
		mCreateBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View view) {
				Intent i = new Intent(MainMenuStateActivity.this, CreateGameStateActivity.class);
				startActivity(i);
			}
		});
		
		mJoinBtn = (Button) findViewById(R.id.join_btn);
		mJoinBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View view) {
				Intent i = new Intent(MainMenuStateActivity.this, JoinRoomStateActivity.class);
				startActivity(i);
			}
		});
		
	}

}
