package at.ac.uniklu.mobile.message;

import java.io.Serializable;

import at.ac.uniklu.mobile.db.Challenge;
import at.ac.uniklu.mobile.util.Constants;

public class UncoverMessage implements Serializable {
	
	private String androidId;
	private Challenge challenge;
	private VectorTimestamp vt;
	private int x;
	private int y;
	
	public UncoverMessage(String androidId, Challenge challenge, VectorTimestamp myTimestamp, int x, int y) {
		this.androidId = androidId;
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

	
	public static VectorTimestamp getVectorTimestamp(String[] msgSplitted) {
		if (msgSplitted.length > 1) {
			VectorTimestamp vt = new VectorTimestamp(msgSplitted[1]);
			
			for (int i = 5; i < msgSplitted.length; i += 2) {
				vt.getVector().put(msgSplitted[i], Integer.parseInt(msgSplitted[i + 1]));
			}
			
			return vt;
		}
		
		return null;
	}
	
	@Override
	public String toString() {		
		return Constants.UNCOVERED_MSG + Constants.MESSAGE_SEP_CHAR 
				+ this.androidId + Constants.MESSAGE_SEP_CHAR 
				+ this.challenge.getId() + Constants.MESSAGE_SEP_CHAR 
				+ this.x + Constants.MESSAGE_SEP_CHAR 
				+ this.y + Constants.MESSAGE_SEP_CHAR 
				+ this.vt;
				//+ HelperUtil.getAndroidId(context) + PeerManager.RENDEZVOUS_MESSAGE_SEP_CHAR + "10.0.2.2";
				
	}
}
