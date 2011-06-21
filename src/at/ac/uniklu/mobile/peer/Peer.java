package at.ac.uniklu.mobile.peer;

import java.net.InetAddress;

import at.ac.uniklu.mobile.message.VectorTimestamp;

/**
 * describes one peer in network
 */
public class Peer {
	
	private String androidId;
	private InetAddress ipAddress;
	private long timeStamp;
	private VectorTimestamp vectorTimestamp;
	
	public Peer(String androidId, InetAddress ipAddress, long timeStamp) {
		this.androidId = androidId;
		this.ipAddress = ipAddress;
		this.timeStamp = timeStamp;
		this.vectorTimestamp = new VectorTimestamp(androidId);
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
	
	
	

}
