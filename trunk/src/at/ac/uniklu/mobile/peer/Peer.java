package at.ac.uniklu.mobile.peer;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

import android.util.Log;
import at.ac.uniklu.mobile.message.UncoverMessage;
import at.ac.uniklu.mobile.message.VectorTimestamp;
import at.ac.uniklu.mobile.util.Constants;

/**
 * describes one peer in network
 */
public class Peer {
	
	private String androidId;
	private InetAddress ipAddress;
	private long timeStamp;
	private VectorTimestamp vectorTimestamp;
	private Socket socket;
	private PrintWriter out;
	
	public Peer(String androidId, InetAddress ipAddress, long timeStamp) {
		this.androidId = androidId;
		this.ipAddress = ipAddress;
		this.timeStamp = timeStamp;
		this.vectorTimestamp = new VectorTimestamp(androidId);
	}
	
	public void connectToPeer() {
		try {
			Log.d(Constants.LOG_TAG, "Connecting to peer " + androidId + " with IP " + ipAddress + " ...");
			socket = new Socket(ipAddress, PeerManager.RENDEZVOUS_SERVER_PORT);

			Log.d(Constants.LOG_TAG , "Peer try to send message...");
			out = new PrintWriter(socket.getOutputStream(), true);
			String msg = PeerManager.rc.getRendezvousMessage();
			out.println(msg);
			//out.flush();
			Log.d(Constants.LOG_TAG, "Peer message " + msg + " was sent!");
			try {
				Thread.sleep(PeerManager.RENDEZVOUS_TIME_PERIOD);
			}
			catch(InterruptedException iex)
			{
		  
			}	
			
			Log.d(Constants.LOG_TAG, "Socket opened: Peer " + androidId);
		}
		catch(Exception ex) {
			Log.e(Constants.LOG_TAG, "Peer" + androidId, ex);
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
	public long getTimeStamp() {
		return timeStamp;
	}
	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}
	
	public void sendUncoverMessage(int x, int y) {
		Log.d(Constants.LOG_TAG , "Peer try to send message...");
		vectorTimestamp.next();
		String msg = (new UncoverMessage(vectorTimestamp, x, y)).toString();
		out.println(msg);
		//out.flush();
		Log.d(Constants.LOG_TAG, "Peer message " + msg + " was sent!");
	}
}
