package com.gamejam.artag.net;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

public class MultiThreadedServer implements Runnable {

	protected int serverPort = 12345;
	protected boolean isStopped = false;
	protected Thread runningThread = null;

	private TextView serverStatus;

	// DEFAULT IP
	public static String SERVER_IP = "10.0.2.15";

	// DESIGNATE A PORT
	public static final int SERVERPORT = 8080;
	private Handler handler = null;
	private ServerSocket serverSocket;
	private String mMsgFromClient;
	private ArrayList<Socket> clients = new ArrayList<Socket>();

	private static MultiThreadedServer me;

	private MultiThreadedServer(int port, String ip_address, Handler handler,
			TextView serverStatus) {
		this.serverPort = port;
		SERVER_IP = ip_address;
		this.handler = handler;
		this.serverStatus = serverStatus;
	}

	public static MultiThreadedServer getInstance(int port, String ip_address,
			Handler handler, TextView serverStatus) {
		if (me == null) {
			me = new MultiThreadedServer(port, ip_address, handler,
					serverStatus);
		}
		return me;
	}

	public static MultiThreadedServer getInstance() {
		return me;
	}

	public void run() {
		synchronized (this) {
			this.runningThread = Thread.currentThread();
		}
		openServerSocket();
		while (!isStopped()) {
			Socket clientSocket = null;
			try {
				clientSocket = this.serverSocket.accept();
				clients.add(clientSocket);
			} catch (IOException e) {
				if (isStopped()) {
					Log.d("SERVER TEXT", "Server Stopped.");
					return;
				}
				throw new RuntimeException("Error accepting client connection",
						e);
			}
			new Thread(new WorkerRunnable(clientSocket, this)).start();

		}
		Log.d("SERVER TEXT", "Server Stopped.");
	}

	private synchronized boolean isStopped() {
		return this.isStopped;
	}

	public synchronized void stop() {
		this.isStopped = true;
		try {
			this.serverSocket.close();
		} catch (IOException e) {
			throw new RuntimeException("Error closing server", e);
		}
	}

	private void openServerSocket() {
		try {
			this.serverSocket = new ServerSocket(this.serverPort);
		} catch (IOException e) {
			throw new RuntimeException("Cannot open port 8080", e);
		}
	}

	public void printMessage(String message) {
		mMsgFromClient = message;
		handler.post(new Runnable() {
			@Override
			public void run() {
				Log.d("Message From Client Server", mMsgFromClient + "");
				serverStatus.append('\n' + mMsgFromClient);
			}
		});
	}

	public ArrayList<Socket> getClients() {
		return clients;
	}

	public void broadcastMessage(String message) {
		clients = getClients();
		for (int i = 0, j = clients.size() - 1; i <= j; i++) {
			Socket socket = clients.get(i);
			PrintWriter out = null;
			try {
				Log.d("SERVER - CLIENT IS ALIVE b4 pw :", socket.isConnected()
						+ "");
				out = new PrintWriter(new BufferedWriter(
						new OutputStreamWriter(socket.getOutputStream())), true);
				Log.d("SERVER - CLIENT IS ALIVE b4 pw :", socket.isConnected()
						+ "");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// WHERE YOU ISSUE THE COMMANDS
			out.println(message);
		}
	}

	public void broadcastMessage(File myFile) throws IOException {
		clients = getClients();
		for (int i = 0, j = clients.size() - 1; i <= j; i++) {
			Socket socket = clients.get(i);
			byte[] mybytearray = new byte[(int) myFile.length()];
			BufferedInputStream bis;
			try {
				bis = new BufferedInputStream(
						new FileInputStream(myFile));
				bis.read(mybytearray, 0, mybytearray.length);
				OutputStream os = socket.getOutputStream();
				os.write(mybytearray, 0, mybytearray.length);
				os.flush();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}

	}

}
