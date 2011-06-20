package at.ac.uniklu.mobile.peer;

import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;
import at.ac.uniklu.mobile.db.Challenge;
import at.ac.uniklu.mobile.util.Constants;
import at.ac.uniklu.mobile.util.HelperUtil;

public class PeerRendezvousServer extends Thread {
	
	private boolean finished = false;
	private Challenge challenge = null;
	private Context context = null;
	
	public PeerRendezvousServer(Context context, Challenge challenge) {
		this.challenge = challenge;
		this.context = context;
	}	
	
	public void stopPeerRendezvous() {
		finished = true;
	}
	
	private String getRendezvousMessage() {
		/*JSONObject jsonMsg = null;
		try {
			jsonMsg = new JSONObject();
			jsonMsg.put("msgtype", PeerManager.RENDEZVOUS_JOIN_MESSAGE);
			jsonMsg.put("challenge", challenge.getId());
			jsonMsg.put("androidId", HelperUtil.getAndroidId(context));
			jsonMsg.put("ipaddress", HelperUtil.getIpAddress());
		}
		catch(JSONException jsex) {
			Log.e(Constants.LOG_TAG, "cannot create JSON rendezvous message: " + jsex.getMessage(), jsex);
		}
		
		return jsonMsg.toString();
		*/
		
		return PeerManager.RENDEZVOUS_JOIN_MESSAGE + PeerManager.RENDEZVOUS_MESSAGE_SEP_CHAR 
				+ challenge.getId() + PeerManager.RENDEZVOUS_MESSAGE_SEP_CHAR 
				//+ HelperUtil.getAndroidId(context) + PeerManager.RENDEZVOUS_MESSAGE_SEP_CHAR + HelperUtil.getIpAddress(); 
				+ HelperUtil.getAndroidId(context) + PeerManager.RENDEZVOUS_MESSAGE_SEP_CHAR + "10.0.2.2";
				
	}
	
	@Override public void run() {
		
		try {
			InetAddress serverAddr = InetAddress.getByName(PeerManager.RENDEZVOUS_SERVER_IP);
			Log.d(Constants.LOG_TAG, "PeerRendezvousServer is connecting to rendezvous server...");
			Socket socket = new Socket(serverAddr, PeerManager.RENDEZVOUS_SERVER_PORT);

			while (!finished) {	
				Log.d(Constants.LOG_TAG , "PeerRendezvousServer try to send message...");
				PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
				String msg = getRendezvousMessage();
				out.println(msg);
				//out.flush();
				Log.d(Constants.LOG_TAG, "PeerRendezvousServer message " + msg + " was sent!");
				try {
					sleep(PeerManager.RENDEZVOUS_TIME_PERIOD);
				}
				catch(InterruptedException iex)
				{
			  
				}	
			}
			socket.close();
		}
		catch(Exception ex) {
			Log.e(Constants.LOG_TAG, "PeerRendezvousServer", ex);
			finished = true;
		}
	}
}
