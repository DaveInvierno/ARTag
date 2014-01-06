package com.gamejam.artag.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import com.gamejam.artag.R;
import com.googlecode.javacv.cpp.opencv_features2d.MSER;

/*public class Server extends Activity {

	private boolean mStopThread = false;
	private int mBackCount = 0;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_game_state);
		
		
		
		try {
			Thread server = new Thread(new ServerThread());
			server.start();
			
			Thread.sleep(2000);
			
			Thread client = new Thread(new ClientThread());
			client.start();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	public class ServerThread implements Runnable {

		@Override
		public void run() {
			try {
		        Boolean end = false;
		        ServerSocket ss = new ServerSocket(12345);
		        Log.d("Tcp Example", "Server Created");
		        while(!end){
		        	Log.d("Tcp Example", "waiting . . .");
	                //Server is waiting for client here, if needed
	                Socket s = ss.accept();
	                BufferedReader input = new BufferedReader(new InputStreamReader(s.getInputStream()));
	                PrintWriter output = new PrintWriter(s.getOutputStream(),true); //Autoflush
	                String st = input.readLine();
	                Log.d("Tcp Example", "From client: "+st);
	                output.println("Good bye and thanks for all the fish :)");
	                s.close();
	                Log.d("Tcp Example", "waiting . . .");
	                if (mStopThread){ end = true; }
		        }
				ss.close();
				Log.d("Tcp Example", "Server Thread Closed");
				       
				} catch (UnknownHostException e) {
				        // TODO Auto-generated catch block
				        e.printStackTrace();
				} catch (IOException e) {
				        // TODO Auto-generated catch block
				        e.printStackTrace();
				}
				
			}
		
	}
	
	public class ClientThread implements Runnable {

		@Override
		public void run() {
			try {
				Log.d("Tcp Example", "Client Created");
		        Socket s = new Socket("192.168.1.207", 12345);
		       
		        //outgoing stream redirect to socket
		        OutputStream out = s.getOutputStream();
		       
		        PrintWriter output = new PrintWriter(out);
		        output.println("Hello Android!");
		        BufferedReader input = new BufferedReader(new InputStreamReader(s.getInputStream()));
		       
		        //read line(s)
		        String st = input.readLine();
		        //Close connection
		        s.close();
		        Log.d("Tcp Example", "Client Thread Closed");
		       
			} catch (UnknownHostException e) {
			        // TODO Auto-generated catch block
			        e.printStackTrace();
			} catch (IOException e) {
			        // TODO Auto-generated catch block
			        e.printStackTrace();
			}
		}
	}
	
	@Override
	public void onBackPressed() {
		mStopThread = true;
		if(mBackCount == 1) super.onBackPressed();
		mBackCount++;
	}
}*/

public class ServerActivity extends Activity {
	 
    private TextView serverStatus;
 
    // DEFAULT IP
    public static String SERVERIP = "10.0.2.15";
 
    // DESIGNATE A PORT
    public static final int SERVERPORT = 8080;
 
    private Handler handler = new Handler();
 
    private ServerSocket serverSocket;
    
    private String mMsgFromClient;
    
    private boolean isRunning = true;
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.server);
        serverStatus = (TextView) findViewById(R.id.server_status);
 
        SERVERIP = getLocalIpAddress();
 
        Thread fst = new Thread(new ServerThread());
        fst.start();
    }
 
    public class ServerThread implements Runnable {
 
        public void run() {
            try {
                if (SERVERIP != null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            serverStatus.setText("Listening on IP: " + SERVERIP);
                        }
                    });
                    serverSocket = new ServerSocket(SERVERPORT);
                    while (isRunning) {
                        // LISTEN FOR INCOMING CLIENTS
                        Socket client = serverSocket.accept();
                        
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                serverStatus.setText(serverStatus.getText().toString() + "\n" + "Connected.");
                            }
                        });
 
                        try {
                            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                            mMsgFromClient = null;
                            while ((mMsgFromClient = in.readLine()) != null) {
                                Log.d("ServerActivity", mMsgFromClient);
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        serverStatus.setText(serverStatus.getText().toString() + "\n" + mMsgFromClient);
                                    }
                                });
                            }
                            break;
                        } catch (Exception e) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    serverStatus.setText("Oops. Connection interrupted. Please reconnect your phones.");
                                }
                            });
                            e.printStackTrace();
                        }
                    }
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            serverStatus.setText("Couldn't detect internet connection.");
                        }
                    });
                }
            } catch (Exception e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        serverStatus.setText("Error");
                    }
                });
                e.printStackTrace();
            }
        }
    }
 
    // GETS THE IP ADDRESS OF YOUR PHONE'S NETWORK
    private String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) { return inetAddress.getHostAddress().toString(); }
                }
            }
        } catch (SocketException ex) {
            Log.e("ServerActivity", ex.toString());
        }
        return null;
    }
 
    @Override
    protected void onStop() {
        isRunning = false;
    	super.onStop();
        try {
             // MAKE SURE YOU CLOSE THE SOCKET UPON EXITING
             serverSocket.close();
         } catch (IOException e) {
             e.printStackTrace();
         }
    }
 
}
