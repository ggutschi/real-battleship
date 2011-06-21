package at.ac.uniklu.mobile.peer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import android.util.Log;
import at.ac.uniklu.mobile.message.UncoverMessage;
import at.ac.uniklu.mobile.message.VectorTimestamp;
import at.ac.uniklu.mobile.util.Constants;

/**
 * describes one peer in network
 */
public class Peer implements Runnable {
	
	private String androidId;
	private InetAddress ipAddress;
	private Socket socket;
	private PrintWriter out;
	
	private boolean stop = false;
	
	public Peer(String androidId, InetAddress ipAddress) {
		Log.d(Constants.LOG_TAG, "Creating local peer (constructor)...");
		this.androidId = androidId;
		this.ipAddress = ipAddress;
		Log.d(Constants.LOG_TAG, "Local peer created (constructor).");
	}
	
	public void connectToPeer(boolean rendezvous) {
		try {
			Log.d(Constants.LOG_TAG, "Connecting to peer " + androidId + " with IP " + ipAddress + " (rendezvous = " + rendezvous + ") ...");
			socket = new Socket(ipAddress, Constants.PEER_TO_PEER_PORT);
			out = new PrintWriter(socket.getOutputStream(), true);

			if (rendezvous) {
				Log.d(Constants.LOG_TAG , "Peer try to send message...");
			
				String msg = PeerManager.rc.getRendezvousMessage();
				out.println(msg);
				//out.flush();
				Log.d(Constants.LOG_TAG, "Peer message " + msg + " was sent!");
			}
			
			Log.d(Constants.LOG_TAG, "Socket opened: Peer " + androidId);
		}
		catch(Exception ex) {
			Log.e(Constants.LOG_TAG, "Peer " + androidId + " hasn't answered. Tell your peers!", ex);
		}
	}
	
	public boolean isServer() {
		return this.ipAddress.getHostAddress().equals(PeerManager.RENDEZVOUS_SERVER_IP);
	}
	
	public void closeSocket() {
		try {
			if (socket != null)
				socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String getAndroidId() {
		return androidId;
	}
	public void setAndroidId(String androidId) {
		this.androidId = androidId;
	}
	public InetAddress getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(InetAddress ipAddress) {
		this.ipAddress = ipAddress;
	}
	
	public void sendUncoverMessage(int x, int y) {
		Log.d(Constants.LOG_TAG , "Peer try to send message...");
		PeerManager.getVectorTimestamp().next();
		String msg = (new UncoverMessage(PeerManager.getCurrentChallenge(), PeerManager.getVectorTimestamp(), x, y)).toString();
		out.println(msg);
		//out.flush();
		Log.d(Constants.LOG_TAG, "Peer message " + msg + " was sent!");
	}
	
	private void releasePeer(String androidId) {
		PeerManager.removePeer(androidId);
	}
	
	public void run() {
		try {
			ServerSocket ss = new ServerSocket(Constants.PEER_TO_PEER_PORT);
			socket = ss.accept();
			
			Log.d(Constants.LOG_TAG, "Socket connection accepted.");

			String line = null;
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));;
		
			while (!stop) {
				line = in.readLine();
				
				Log.d(Constants.LOG_TAG, "Message received: " + line);
				
				// released;android_id
				String[] msgSplitted = line.split(Constants.MESSAGE_SEP_CHAR + "");
				
				if (msgSplitted[0].equalsIgnoreCase(Constants.UNCOVERED_MSG)) {
					// CHECK VECTORTIMESTAMP
					// UNCOVER FIELD LOCALLY
				} else if (msgSplitted[0].equalsIgnoreCase(Constants.RELEASED_MSG)) {
					if (msgSplitted.length > 1) {
						String androidId = msgSplitted[1];
						
						releasePeer(androidId);
					}
				}
			}

			socket.close();
			ss.close();

		} catch (IOException e) {
		}
	}
	
	public void stopp() {
		stop = true;
	}
}
