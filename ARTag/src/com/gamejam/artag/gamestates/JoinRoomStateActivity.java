package com.gamejam.artag.gamestates;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

public class JoinRoomStateActivity extends Activity {

	private static final String IPV4_REGEX = "^(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})$";
	
	private Button mJoinBtn;
	private EditText mIpAddressTxt;
	private EditText mPlayerName;
	
	protected void onCreate(Bundle savedInstanceState) {
		// Hide the window title.
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // Use full screen
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_join_room_state);
		
		mIpAddressTxt = (EditText) findViewById(R.id.room_ip_txt);
		mPlayerName = (EditText) findViewById(R.id.player_name_in_join_txt);
		
		mJoinBtn = (Button) findViewById(R.id.join_game_btn);
		mJoinBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(mPlayerName.getText().toString().length() == 0) {
					Toast.makeText(getApplicationContext(), "Player Name Cannot Be Empty", Toast.LENGTH_LONG).show();
					return;
				} 
				
				if(isValidInet4Address(mIpAddressTxt.getText().toString())) {
					Intent i = new Intent(JoinRoomStateActivity.this, GameRoomStateActvity.class);
					i.putExtra(GameRoomStateActvity.IS_SERVER_EXTRA, false);
					i.putExtra(GameRoomStateActvity.IP_ADDRESS_EXTRA, mIpAddressTxt.getText().toString());
					i.putExtra(GameRoomStateActvity.PLAYER_NAME_EXTRA, mPlayerName.getText().toString());
					startActivity(i);
				} else {
					Toast.makeText(getApplicationContext(), "Invalid IP Address", Toast.LENGTH_LONG).show();
				}
			}
		});
		
	}
	
    public boolean isValidInet4Address(String inet4Address) {
    	Pattern p = Pattern.compile(IPV4_REGEX);
    	Matcher m = p.matcher(inet4Address);
    	return m.matches();
    }
}
