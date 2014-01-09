package com.gamejam.artag.gamestates;

import java.util.UUID;

import android.app.Activity;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.gamejam.artag.R;
import com.gamejam.artag.model.Player;
import com.gamejam.artag.model.PlayerList;

public class CreateGameStateActivity extends Activity {

	public static final String EXTRA_NEW_GAME = "com.gamejam.artag.new_game";
	public static final String EXTRA_GAME_NAME = "com.gamejam.artag.game_name";
	
	private Button mCreateGameBtn;
	private EditText mGameName;
	private EditText mPlayerName;
	
	protected void onCreate(Bundle savedInstanceState) {
		// Hide the window title.
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // Use full screen
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_game_state);
		
		mGameName = (EditText) findViewById(R.id.game_name_txt);
		mPlayerName = (EditText) findViewById(R.id.player_name_in_create_txt);
		
		mCreateGameBtn = (Button) findViewById(R.id.create_game_btn);
		mCreateGameBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if(mPlayerName.getText().toString().length() == 0) {
					Toast.makeText(getApplicationContext(), "Player Name Cannot Be Empty", Toast.LENGTH_LONG).show();
					return;
				}
				
				//if(s.mServerSocket != null && c.mClientSocket != null) {
				if(mGameName.getText().toString().length() == 0) {
					Toast.makeText(getApplicationContext(), "Game Name Cannot Be Empty", Toast.LENGTH_LONG).show();
					return;
				}
				
				Player p = new Player(UUID.randomUUID(), mPlayerName.getText().toString());
				PlayerList pl = PlayerList.getInstance();
				pl.addPlayer(p);
				
				WifiManager wifiMgr = (WifiManager) getSystemService(WIFI_SERVICE);
				WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
				int ip = wifiInfo.getIpAddress();
				String ipAddress = Formatter.formatIpAddress(ip);
				
				Intent i = new Intent(CreateGameStateActivity.this, GameRoomStateActvity.class);
				i.putExtra(GameRoomStateActvity.IS_SERVER_EXTRA, true);
				i.putExtra(GameRoomStateActvity.IP_ADDRESS_EXTRA, ipAddress);
				i.putExtra(EXTRA_NEW_GAME, true);
				i.putExtra(EXTRA_GAME_NAME, mGameName.getText().toString());
				i.putExtra(GameRoomStateActvity.PLAYER_NAME_EXTRA, mPlayerName.getText().toString());
				startActivity(i);
				
				CreateGameStateActivity.this.finish();
			}
		});
	}
	
}
