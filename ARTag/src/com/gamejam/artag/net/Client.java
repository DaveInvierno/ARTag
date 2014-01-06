package com.gamejam.artag.net;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import com.googlecode.javacv.cpp.opencv_features2d.MSER;

import android.util.Log;
import android.widget.EditText;

public class Client {

	private static Client mClient;
	public Socket mClientSocket;
	
	private String mGameState;
	private boolean isServerRunning = true;
	public boolean mWillSendMsg = false;
	public boolean mConnected = false;
	private EditText mChatMsg;
	
	private String mIP;
	private int mPort;
	
	private Client(String IP, int port) {
		mIP = IP;
		mPort = port;
	}
	
	public static Client getInstance(String IP, int port) {
		if(mClient == null) {
			mClient = new Client(IP, port);
		}
		return mClient;
	}
	
	public boolean setSocket() {
		boolean isSuccessful;
		
		try {
			mClientSocket = new Socket(mIP, mPort);
			isSuccessful = true;
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			isSuccessful = false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			isSuccessful = false;
		}
		
		return isSuccessful;
	}
	
	public void closeSocket() {
		try {
			mClientSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void startThread(EditText et) {
		mChatMsg = et;
		Thread t = new Thread(new ClientThread());
		t.start();
	}
	
	public class ClientThread implements Runnable {

		@Override
		public void run() {
			if(!setSocket()) return;
			
			while(isServerRunning) {
				if(mGameState.equals("ROOM")) {
					try {
		                mConnected = true;
		                while (mConnected) {
		                    if(mWillSendMsg) {
		                    	mWillSendMsg = false;
			                	try {
			                        Log.d("ClientActivity", "C: Sending command.");
			                        PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(mClientSocket
			                                    .getOutputStream())), true);
			                            // WHERE YOU ISSUE THE COMMANDS
			                            out.println(mChatMsg.getText().toString());
			                            Log.d("ClientActivity", "C: Sent.");
			                    } catch (Exception e) {
			                        Log.e("ClientActivity", "S: Error", e);
			                    }
		                    }
		                }
		                closeSocket();
		                Log.d("ClientActivity", "C: Closed.");
		            } catch (Exception e) {
		                Log.e("ClientActivity", "C: Error", e);
		                mConnected = false;
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
