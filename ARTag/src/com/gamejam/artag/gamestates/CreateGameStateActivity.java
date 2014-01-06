package com.gamejam.artag.gamestates;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.gamejam.artag.R;

public class CreateGameStateActivity extends Activity {

	public static final String EXTRA_NEW_GAME = "com.gamejam.artag.new_game";
	
	private Button mCreateGameBtn;
	private EditText mGameName;
	
	protected void onCreate(Bundle savedInstanceState) {
		// Hide the window title.
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // Use full screen
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_game_state);
		
		mGameName = (EditText) findViewById(R.id.game_name_txt);
		
		mCreateGameBtn = (Button) findViewById(R.id.create_game_btn);
		mCreateGameBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				//if(s.mServerSocket != null && c.mClientSocket != null) {
				if(mGameName.getText().toString().length() > 0) {
					Intent i = new Intent(CreateGameStateActivity.this, GameRoomStateActvity.class);
					i.putExtra(EXTRA_NEW_GAME, true);
					startActivity(i);
				} else {
					Toast.makeText(getApplicationContext(), "Game Name Cannot Be Empty", Toast.LENGTH_LONG).show();
				}
			}
		});
	}
	
}
