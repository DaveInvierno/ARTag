package com.gamejam.artag.gamestates;

import android.app.Activity;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.gamejam.artag.R;
import com.gamejam.artag.net.Client;
import com.gamejam.artag.net.Server;

public class GameRoomStateActvity extends Activity {

	private boolean mIsServer;
	private Button mStartGameBtn;
	private Server mServer;
	private Client mClient;
	private Handler mHandler = new Handler();
	
	private EditText mChatTxt;
	private TextView mChatBox;
	private Button mSendChat;
	
	protected void onCreate(Bundle savedInstanceState) {
		// Hide the window title.
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // Use full screen
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game_room_state);
		
		mStartGameBtn = (Button) findViewById(R.id.start_game_btn);
		mStartGameBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(GameRoomStateActvity.this, TrainFacesStateActivity.class);
				startActivity(i);
			}
		});
		
		mChatTxt = (EditText) findViewById(R.id.chat_box_edit_txt);
		mChatBox = (TextView) findViewById(R.id.chat_box_txt);
		
		mSendChat = (Button) findViewById(R.id.send_button);
		mSendChat.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				//mClient.mWillSendMsg = true;
			}
		});
		
		/*mIsServer = getIntent().getBooleanExtra(CreateGameStateActivity.EXTRA_NEW_GAME, false);
		
		if(mIsServer) {
			WifiManager wifiMgr = (WifiManager) getSystemService(WIFI_SERVICE);
			WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
			int ip = wifiInfo.getIpAddress();
			String ipAddress = Formatter.formatIpAddress(ip);
			Log.d("CreateGameStateActivity", ipAddress);
			
			mServer = Server.getInstance(8080);
			mServer.SERVER_IP = ipAddress;
			mServer.startThread("ROOM", mHandler);
		}
		
		mClient = Client.getInstance(mServer.SERVER_IP, 8080);
		mClient.startThread(mChatTxt);*/
	}
	
	@Override()
	public void onStop() {
		super.onStop();
		
		/*if(mIsServer)
			mServer.closeSocket();
		mClient.closeSocket();*/
	}
	
	
}
