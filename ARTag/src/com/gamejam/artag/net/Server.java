package com.gamejam.artag.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

public class Server {

	public static final int SERVER_PORT = 8080;
	public String SERVER_IP;
	
	private static Server mServer;
	public static ServerSocket mServerSocket;
	private Handler mHandler;
	private String mGameState;
	private boolean isServerRunning = true;
	private String mMsgFromClient;
	private TextView mChatTxt;
	
	private int mPort;
	
	private Server(int port) {
		mPort = port;
	}
	
	public static Server getInstance(int port) {
		if(mServer == null) {
			mServer = new Server(port);
		}
		return mServer;
	}
	
	public boolean setSocket() {
		boolean isSuccessful;
		
		try {
			mServerSocket = new ServerSocket(mPort);
			isSuccessful = true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			isSuccessful = false;
		}
		
		return isSuccessful;
	}
	
	public void closeSocket() {
		try {
			mServerSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void startThread(String gameState, Handler mHandler) {
		mGameState = gameState;
		mHandler = mHandler;
		Thread t = new Thread(new ServerThread());
		t.start();
	}
	
	public void startThread(String gameState, Handler handler, TextView tv) {
		mGameState = gameState;
		mHandler = handler;
		mChatTxt = tv;
		Thread t = new Thread(new ServerThread());
		t.start();
	}
	
	public class ServerThread implements Runnable {

		@Override
		public void run() {
			if(!setSocket()) return;
			
			while(isServerRunning) {
				if(mGameState.equals("ROOM")) {
					
                    // LISTEN FOR INCOMING CLIENTS
                    Socket client;
					try {
						client = mServerSocket.accept();
                    
	                    /*mHandler.post(new Runnable() {
	                        @Override
	                        public void run() {
	                            mChatTxt.setText(mChatTxt.getText().toString() + "\n" + "Connected.");
	                        }
	                    });*/
	 
	                    try {
	                        BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
	                        mMsgFromClient = null;
	                        while ((mMsgFromClient = in.readLine()) != null) {
	                            Log.d("ServerActivity", mMsgFromClient);
	                            /*mHandler.post(new Runnable() {
	                                @Override
	                                public void run() {
	                                    mChatTxt.setText(mChatTxt.getText().toString() + "\n" + mMsgFromClient);
	                                }
	                            });*/
	                        }
	                    } catch (Exception e) {
	                        /*mHandler.post(new Runnable() {
	                            @Override
	                            public void run() {
	                                mChatTxt.setText(mChatTxt.getText().toString() + "\n" + "Oops. Connection interrupted. Please reconnect your phones.");
	                            }
	                        });*/
	                        e.printStackTrace();
	                    }
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
		            
				} else if(mGameState.equals("GAME")) {
					
				} else if(mGameState.equals("GAME_OVER")) {
					
				} else if(mGameState.equals("QUIT")) {
					isServerRunning = false;
				}
			}
			closeSocket();
		}
		
	}
}
