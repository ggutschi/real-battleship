package at.ac.uniklu.mobile.message;

import java.io.Serializable;

import at.ac.uniklu.mobile.util.Constants;

/**
 * Message sent when a peer uncovers a field
 */
public class UncoverMessage extends PeerMessage implements Serializable {
	
	/**
	 * id of challenge
	 */
	private int challengeId;
	
	/**
	 * vector timestamp for recognizing conflicts
	 */
	private VectorTimestamp vt;
	
	/**
	 * x coordinate of uncovered cell
	 */
	private int x;
	
	/**
	 * y coordinate of uncovered cell
	 */
	private int y;
	
	public UncoverMessage(String androidId, int challengeId, VectorTimestamp myTimestamp, int x, int y) {
		this.androidId = androidId;
		this.challengeId = challengeId;
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
	
	public int getChallengeId() {
		return challengeId;
	}

	/**
	 * Extracts the vector timestamp from the given uncover message
	 * @param msgSplitted uncover message splitted by separation character
	 * @return extracted vector timestamp
	 */
	public static VectorTimestamp extractVectorTimestamp(String[] msgSplitted) {
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
				+ this.challengeId + Constants.MESSAGE_SEP_CHAR 
				+ this.x + Constants.MESSAGE_SEP_CHAR 
				+ this.y + Constants.MESSAGE_SEP_CHAR 
				+ this.vt;
				//+ HelperUtil.getAndroidId(context) + PeerManager.RENDEZVOUS_MESSAGE_SEP_CHAR + "10.0.2.2";
				
	}
}
