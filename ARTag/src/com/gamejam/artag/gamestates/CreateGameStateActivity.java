package com.gamejam.artag.gamestates;

import android.app.Activity;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.telephony.gsm.GsmCellLocation;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.gamejam.artag.R;
import com.gamejam.artag.net.Client;
import com.gamejam.artag.net.Server;

public class CreateGameStateActivity extends Activity {

	public static final String EXTRA_NEW_GAME = "com.gamejam.artag.new_game";
	
	private Button mCreateGameBtn;
	
	protected void onCreate(Bundle savedInstanceState) {
		// Hide the window title.
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // Use full screen
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_game_state);
		
		mCreateGameBtn = (Button) findViewById(R.id.create_game_btn);
		mCreateGameBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				//if(s.mServerSocket != null && c.mClientSocket != null) {
					Intent i = new Intent(CreateGameStateActivity.this, GameRoomStateActvity.class);
					i.putExtra(EXTRA_NEW_GAME, true);
					startActivity(i);
				//} else {
					//Toast.makeText(getApplicationContext(), "Failed to create game. Please try again.", Toast.LENGTH_LONG).show();
				//}
			}
		});
	}
	
}
