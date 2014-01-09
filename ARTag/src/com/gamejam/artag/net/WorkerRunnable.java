package com.gamejam.artag.net;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.UUID;

import android.util.Log;

public class WorkerRunnable implements Runnable {

	private MultiThreadedServer server = null;

	protected Socket clientSocket = null;
	protected String mMsgFromClient = null;

	private UUID id;
	private ArrayList<Socket> clients = new ArrayList<Socket>();

	public WorkerRunnable(Socket clientSocket, MultiThreadedServer server) {
		this.clientSocket = clientSocket;
		this.server = server;
		id = UUID.randomUUID();
	}

	public void run() {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(
					clientSocket.getInputStream()));

			Log.d("SERVERTEXT", "Proceed to broadcast");
			while ((mMsgFromClient = in.readLine()) != null) {
				Log.d("Message From Client Worker", mMsgFromClient + "");
				server.printMessage(mMsgFromClient);
				
				clients = server.getClients();
				for(int i = 0, j = clients.size() - 1; i <= j; i++){
					Socket socket = clients.get(i);
					PrintWriter out = null;
					try {
						Log.d("SERVER - CLIENT IS ALIVE b4 pw :", socket.isConnected() + "");
						out = new PrintWriter(new BufferedWriter(
								new OutputStreamWriter(socket.getOutputStream())),
								true);
						Log.d("SERVER - CLIENT IS ALIVE b4 pw :", socket.isConnected() + "");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					// WHERE YOU ISSUE THE COMMANDS
					out.println(mMsgFromClient);
				}
				
			}
			
		} catch (IOException e) {
			server.printMessage('\n' + "Oops. Connection interrupted. Please reconnect your phones.");
		}

	}

	private String getID() {
		return id.toString();
	}
}
