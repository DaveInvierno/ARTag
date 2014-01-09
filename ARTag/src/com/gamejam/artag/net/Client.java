package com.gamejam.artag.net;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

public class Client {

	private static Client mClient;
	public Socket mClientSocket;

	private String mGameState;
	private boolean isServerRunning = true;
	public boolean mWillSendMsg = false;
	public boolean mConnected = false;
	private EditText mChatMsg;
	private TextView mChatBox;

	private String mIP;
	private int mPort;
	private String mPlayerName;

	Socket socket = null;
	private Handler handler = null;

	private static Client me;

	private Client(String IP, int port, Handler handler, TextView chatBox,
			String mPlayerName) {
		mIP = IP;
		mPort = port;
		this.handler = handler;
		mChatBox = chatBox;
		this.mPlayerName = mPlayerName;
	}

	public static Client getInstance(String IP, int port, Handler handler,
			TextView chatBox, String mPlayerName) {
		if (me == null) {
			me = new Client(IP, port, handler, chatBox, mPlayerName);
		}
		return me;
	}

	public static Client getInstance() {
		return me;
	}

	public void startThread(EditText et) {
		mChatMsg = et;

		Thread listener = new Thread(new ClientThreadListener());
		listener.start();

		Thread t = new Thread(new ClientThread());
		t.start();

	}

	public class ClientThread implements Runnable {

		public void run() {
			try {
				InetAddress serverAddr = InetAddress.getByName(mIP);

				Log.d("ClientActivity", "C: Connecting...");

				socket = new Socket(serverAddr, mPort);
				mConnected = true;

				while (mConnected) {
					if (mWillSendMsg) {
						mWillSendMsg = false;
						try {
							Log.d("ClientActivity", "C: Sending command.");
							PrintWriter out = new PrintWriter(
									new BufferedWriter(new OutputStreamWriter(
											socket.getOutputStream())), true);
							// WHERE YOU ISSUE THE COMMANDS
							Log.d("Message From Client Client Thread", mChatMsg
									.getText().toString());
							out.println(mChatMsg.getText().toString());

						} catch (Exception e) {
							Log.e("ClientActivity", "S: Error", e);
						}
					}
				}
				Log.d("ClientActivity", "C: Closed.");
			} catch (Exception e) {
				Log.e("ClientActivity", "C: Error", e);
				mConnected = false;
			}
		}
	}

	public class ClientThreadListener implements Runnable {

		protected Socket serverSocket = null;
		protected String mMsgFromServer;

		public void run() {
			try {
				InetAddress serverAddr = InetAddress.getByName(mIP);
				serverSocket = new Socket(serverAddr, mPort);

				mConnected = true;
				Log.d("CLIENT LISTENER ", "LISTENING TO SERVER");
				Log.d("CLIENT LISTENER before getInput",
						serverSocket.isConnected() + "");
				BufferedReader in = new BufferedReader(new InputStreamReader(
						serverSocket.getInputStream()));

				boolean isXML = false;
				
				Log.d("CLIENT LISTENER after getInput",
						serverSocket.isConnected() + "");
				while ((mMsgFromServer = in.readLine()) != null) {
					Log.d("MESSAGE FROM SERVER: ", mMsgFromServer);
					if (mMsgFromServer.contains("xml")) {
						isXML = true;
						break;
					} else {
						handler.post(new Runnable() {
							@Override
							public void run() {
								mChatBox.append('\n' + mPlayerName + " : "
										+ mMsgFromServer);
							}
						});
					}
				}
				
				if(isXML) {
					// Create a filename
					String filename = "facedata.xml";
					String txtFiledDir = Environment.getExternalStorageDirectory() + "/artag/data/";
					// Save the jpeg data to disk
					FileOutputStream os = null;
					File dir= new File (txtFiledDir);
					dir.mkdirs();
					File file = new File(dir, filename);
					
					byte[] mybytearray = new byte[Integer.MAX_VALUE];
				    InputStream is = socket.getInputStream();
				    FileOutputStream fos = new FileOutputStream(file);
				    BufferedOutputStream bos = new BufferedOutputStream(fos);
				    int bytesRead = is.read(mybytearray, 0, mybytearray.length);
				    bos.write(mybytearray, 0, bytesRead);
				    bos.close();
				}
			} catch (Exception e) {
				Log.e("ClientListener", "C: Error", e);
				mConnected = false;
			}
		}

	}
}
