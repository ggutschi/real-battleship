package at.ac.uniklu.mobile.message;

import java.io.Serializable;

import at.ac.uniklu.mobile.db.Challenge;
import at.ac.uniklu.mobile.peer.PeerManager;
import at.ac.uniklu.mobile.util.Constants;

public class UncoverMessage implements Serializable {
	
	private Challenge challenge;
	private VectorTimestamp vt;
	private int x;
	private int y;
	
	public UncoverMessage(Challenge challenge, VectorTimestamp myTimestamp, int x, int y) {
		this.challenge = challenge;
		this.vt = myTimestamp;
		this.x = x;
		this.y = y;
	}
	
	public VectorTimestamp getVectorTimestamp() {
		return vt;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}
	
	@Override
	public String toString() {		
		return Constants.UNCOVERED_MSG + Constants.MESSAGE_SEP_CHAR 
				+ this.challenge.getId() + Constants.MESSAGE_SEP_CHAR 
				+ this.x + Constants.MESSAGE_SEP_CHAR 
				+ this.y + Constants.MESSAGE_SEP_CHAR 
				+ this.vt;
				//+ HelperUtil.getAndroidId(context) + PeerManager.RENDEZVOUS_MESSAGE_SEP_CHAR + "10.0.2.2";
				
	}
}
