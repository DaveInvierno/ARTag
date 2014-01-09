package com.gamejam.artag.gamestates;

import java.net.Socket;
import java.util.ArrayList;

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
import com.gamejam.artag.net.MultiThreadedServer;

public class GameRoomStateActvity extends Activity {

	public static String IS_SERVER_EXTRA = "is_server";
	public static String IP_ADDRESS_EXTRA = "ip_address";
	public static String PLAYER_NAME_EXTRA = "player_name";
	
	private String mPlayerName;
	
	private boolean mIsServer;
	private String mIpAddress;

	private Button mStartGameBtn;
	private Client mClient;
	private Handler mHandler = new Handler();

	private EditText mChatTxt;
	private TextView mChatBox;
	private Button mSendChat;

	private MultiThreadedServer mServer;
	private ArrayList<Socket> clients = new ArrayList<Socket>();
	private Handler handler = new Handler();

	protected void onCreate(Bundle savedInstanceState) {
		// Hide the window title.
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// Use full screen
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game_room_state);

		Intent data = getIntent();
		
		mIsServer = data.getBooleanExtra(IS_SERVER_EXTRA, false);
		mIpAddress = data.getStringExtra(IP_ADDRESS_EXTRA);
		mPlayerName = data.getStringExtra(PLAYER_NAME_EXTRA);
		
		mChatTxt = (EditText) findViewById(R.id.chat_box_edit_txt);
		mChatBox = (TextView) findViewById(R.id.chat_box_txt);

		mSendChat = (Button) findViewById(R.id.send_button);
		mSendChat.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (!mIsServer) {
					Log.d("Message From Client OnClick", mChatTxt.getText()
							.toString() + "");
					mClient.mWillSendMsg = true;
				} else {
					mServer.broadcastMessage(mChatTxt.getText().toString() + "");
					mChatBox.append('\n' + mPlayerName + " : " + mChatTxt.getText().toString() + "");
				}

			}
		});

		if (mIsServer) {
			mServer = MultiThreadedServer.getInstance(8080, mIpAddress, mHandler,
					mChatBox);
			new Thread(mServer).start();
		} else {
			mClient = Client.getInstance(mIpAddress, 8080, mHandler, mChatBox, mPlayerName);
			mClient.startThread(mChatTxt);
		}

		mStartGameBtn = (Button) findViewById(R.id.start_game_btn);
		mStartGameBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(GameRoomStateActvity.this,
						TrainFacesStateActivity.class);
				i.putExtra(IS_SERVER_EXTRA, mIsServer);
				i.putExtra(PLAYER_NAME_EXTRA, mPlayerName);
				startActivity(i);
			}
		});

	}

	@Override()
	public void onStop() {
		super.onStop();
		if (mIsServer) {
			mServer.stop();
		}

	}

}
