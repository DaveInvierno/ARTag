package com.gamejam.artag.gamestates;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.gamejam.artag.R;

public class MainMenuStateActivity extends Activity {

	private Button mPlayBtn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_menu_state);
		
		mPlayBtn = (Button) findViewById(R.id.play_btn);
		mPlayBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(MainMenuStateActivity.this, TrainFacesStateActivity.class);
				startActivity(i);
			}
		});
		
	}

}
