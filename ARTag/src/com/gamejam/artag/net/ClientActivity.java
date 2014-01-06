package com.gamejam.artag.net;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.gamejam.artag.R;

/*public class Client extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_game_state);
		
		Thread client = new Thread(new ClientThread());
		client.start();
	}
	
	public class ClientThread implements Runnable {

		@Override
		public void run() {
			try {
		        Socket s = new Socket("192.168.1.207",12345);
		       
		        //outgoing stream redirect to socket
		        OutputStream out = s.getOutputStream();
		       
		        PrintWriter output = new PrintWriter(out);
		        output.println("Hello Android!");
		        BufferedReader input = new BufferedReader(new InputStreamReader(s.getInputStream()));
		       
		        //read line(s)
		        String st = input.readLine();
		        //Close connection
		        s.close();
		       
		       
			} catch (UnknownHostException e) {
			        // TODO Auto-generated catch block
			        e.printStackTrace();
			} catch (IOException e) {
			        // TODO Auto-generated catch block
			        e.printStackTrace();
			}
		}
	}
}
*/

public class ClientActivity extends Activity {
	 
    private EditText serverIp;
    private EditText chatMsg;
    private Button connectPhones;
    private Button sendMsg;
    private String serverIpAddress = "";
 
    private boolean connected = false;
    private boolean willSendMsg = false;
 
    private Socket clientSocket;
    private Handler handler = new Handler();
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.client);
 
        serverIp = (EditText) findViewById(R.id.server_ip);
        connectPhones = (Button) findViewById(R.id.connect_phones);
        connectPhones.setOnClickListener(connectListener);
        
        chatMsg = (EditText) findViewById(R.id.chat_msg);
        sendMsg = (Button) findViewById(R.id.send_msg);
        sendMsg.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				willSendMsg = true;
			}
		});
    }
 
    private OnClickListener connectListener = new OnClickListener() {
 
        @Override
        public void onClick(View v) {
            if (!connected) {
                serverIpAddress = serverIp.getText().toString();
                if (!serverIpAddress.equals("")) {
                    Thread cThread = new Thread(new ClientThread());
                    cThread.start();
                }
            }
        }
    };
 
    public class ClientThread implements Runnable {
 
        public void run() {
            try {
                InetAddress serverAddr = InetAddress.getByName(serverIpAddress);
                Log.d("ClientActivity", "C: Connecting...");
                clientSocket = new Socket(serverAddr, ServerActivity.SERVERPORT);
                Log.d("ClientActivity", "C: Connected");
                connected = true;
                while (connected) {
                    if(willSendMsg) {
                    	willSendMsg = false;
	                	try {
	                        Log.d("ClientActivity", "C: Sending command.");
	                        PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(clientSocket
	                                    .getOutputStream())), true);
	                            // WHERE YOU ISSUE THE COMMANDS
	                            out.println(chatMsg.getText().toString());
	                            Log.d("ClientActivity", "C: Sent.");
	                    } catch (Exception e) {
	                        Log.e("ClientActivity", "S: Error", e);
	                    }
                    }
                }
                clientSocket.close();
                Log.d("ClientActivity", "C: Closed.");
            } catch (Exception e) {
                Log.e("ClientActivity", "C: Error", e);
                connected = false;
            }
        }
    }
    
    @Override
    public void onStop() {
    	connected = false;
    	try {
			clientSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	super.onStop();
    }
}