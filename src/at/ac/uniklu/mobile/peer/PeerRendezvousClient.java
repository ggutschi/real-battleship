package at.ac.uniklu.mobile.peer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;
import at.ac.uniklu.mobile.db.Challenge;
import at.ac.uniklu.mobile.util.Constants;
import at.ac.uniklu.mobile.util.HelperUtil;

public class PeerRendezvousClient extends Thread {	

	private Challenge challenge = null;
	private Context context = null;
	
	public PeerRendezvousClient(Context context, Challenge challenge) {
		this.challenge = challenge;
		this.context = context;
	}
	
	public void run()
	{		
		try {
			InetAddress serverAddr = InetAddress.getByName(PeerManager.RENDEZVOUS_SERVER_IP);
			Log.d(Constants.LOG_TAG, "PeerRendezvousServer is connecting to rendezvous server...");
			Socket socket = new Socket(serverAddr, PeerManager.RENDEZVOUS_SERVER_PORT);

			Log.d(Constants.LOG_TAG , "PeerRendezvousServer try to send message...");
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			String msg = getRendezvousMessage();
			out.println(msg);
			//out.flush();
			Log.d(Constants.LOG_TAG, "PeerRendezvousServer message " + msg + " was sent!");
				
			socket.close();
		}
		catch(Exception ex) {
			Log.e(Constants.LOG_TAG, "PeerRendezvousServer", ex);
		}
		
		try {
			//InetAddress serverAddr = InetAddress.getLocalHost();
			Log.d(Constants.LOG_TAG, "PeerRendezvousClient is connecting to rendezvous server...");
			//Socket socket = new Socket(serverAddr, 19423);
			ServerSocket ss = new ServerSocket(19423);
			//Socket socket = ss.accept();
			String line = null;
			BufferedReader in = null;
			Socket socket = null;
			
			//while(true) {
			Log.d(Constants.LOG_TAG, "PeerRendezvousClient vor accept ");
			socket = ss.accept();
			
			Log.d(Constants.LOG_TAG, "PeerRendezvousClient wait for server message: ");
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			line = in.readLine();
			
		    /*String str = null, msg = null;
		    while ((str = in.readLine()) != null) {
		    	Log.d(Constants.LOG_TAG, "PeerRendezvousClient got message line: " + str);
		        msg = new String(str);
		    }*/
		    socket.close();
			Log.d(Constants.LOG_TAG, "PeerRendezvousClient got server message: " + line);
			
			if (line != null && !line.equals(PeerManager.RENDEZVOUS_FIRST_PEER_MESSAGE) && !line.equals("")) {
				Vector<Peer> peers = extractServerMessage(line);
				if (peers != null)
					PeerManager.initPeers(peers);
			}
			
			//}
			
		}
		catch(IOException ioex) {
			Log.e(Constants.LOG_TAG, "IOException occured while receiving rendezvous server messages: " 
					+ ioex.getMessage(), ioex);
			
		}		
	}
	
	
	public String getRendezvousMessage() {		
		return PeerManager.RENDEZVOUS_JOIN_MESSAGE + PeerManager.RENDEZVOUS_MESSAGE_SEP_CHAR 
				+ challenge.getId() + PeerManager.RENDEZVOUS_MESSAGE_SEP_CHAR 
				+ HelperUtil.getAndroidId(context); 
				//+ HelperUtil.getAndroidId(context) + PeerManager.RENDEZVOUS_MESSAGE_SEP_CHAR + "10.0.2.2";
				
	}
	
	/**
	 * extracts the server message
	 * @param b server message represented as byte array
	 * @return if extraction successful: an instance of NodeInfo describing the server network node
	 * 		   otherwise: null
	 */
	public Vector<Peer> extractServerMessage(String serverMessage)
	{
		Vector<Peer> peers = null;
		
		Log.d(Constants.LOG_TAG, "extract rendezvous message: " + serverMessage);
		
		try {
			
			JSONArray peerArr = new JSONArray(serverMessage);
			peers = new Vector<Peer>();
			for (int i = 0; i < peerArr.length(); i++) {
				JSONObject obj = peerArr.getJSONObject(0);
				Peer peer = new Peer(obj.getString("android_id"), InetAddress.getByName(obj.getString("ipaddy")), 0L);
				peers.add(peer);
			}
		}
		catch(Exception ex) {
			Log.e(Constants.LOG_TAG, "error while extracting peer objects from server message" 
					+ ex.getMessage(), ex);
		}
		return peers;		
	}
}
