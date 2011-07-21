package at.ac.uniklu.mobile.message;

import java.util.HashMap;

import android.util.Log;
import at.ac.uniklu.mobile.peer.PeerManager;
import at.ac.uniklu.mobile.util.Constants;


/**
 * Classical vector timestamp
 */
public class VectorTimestamp {
	private HashMap<String, Integer> myVector = new HashMap<String, Integer>();
	private String androidId;
	
	
	public VectorTimestamp(String androidId) {
		this.androidId = androidId;
		
		myVector.put(androidId, 0);
		
		Log.d(Constants.LOG_TAG, "Puttet android id " + androidId + " to vector.");
	}
	
	public HashMap<String, Integer> getVector() {
		return myVector;
	}
	
	/**
	 * Adapts the local timestamp to the given one
	 * @param received
	 */
	public synchronized void adapt(VectorTimestamp received) {
		HashMap<String, Integer> receivedVector = received.getVector();
		
		for (String androidId : myVector.keySet()) {
			if (receivedVector.containsKey(androidId))
				if (receivedVector.get(androidId) > myVector.get(androidId))
					myVector.put(androidId, receivedVector.get(androidId));
		}
	}
	
	/**
	 * increases the local timestamp
	 */
	public synchronized void next()   {
		if (myVector.containsKey(androidId))
			myVector.put(androidId, myVector.get(androidId) + 1);
		else
			myVector.put(androidId, 0);
	}

	/**
	 * proofs if a causal error exists
	 * @param received
	 * @return
	 */
	public boolean causalError(VectorTimestamp received)   {
		return less(received.getVector(), myVector);
	}
	
	/**
	 * proofs if two timestamps are the same
	 * @param hm1
	 * @param hm2
	 * @return
	 */
	public static boolean equals (HashMap<String, Integer> hm1, HashMap<String, Integer> hm2) {
		for (String androidId : hm1.keySet())
			if (hm2.containsKey(androidId) && hm1.get(androidId) != hm2.get(androidId))
				return false;
		
		return true;
	}
	
	/**
	 * proofs if timestamp 1 is less than timestamp 2
	 * @param hm1
	 * @param hm2
	 * @return
	 */
	public static boolean less (HashMap<String, Integer> hm1, HashMap<String, Integer> hm2)   {
		if (equals(hm1, hm2))
			return false;

		for (String androidId : hm1.keySet())
			if (hm2.containsKey(androidId) && hm1.get(androidId) > hm2.get(androidId))
				return false;
				
		return true;
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		
		if (myVector.isEmpty())
			return "";
		
		for (String androidId : this.myVector.keySet()) {
			sb.append(androidId);
			sb.append(PeerManager.RENDEZVOUS_MESSAGE_SEP_CHAR);
			sb.append(this.myVector.get(androidId));
			sb.append(PeerManager.RENDEZVOUS_MESSAGE_SEP_CHAR);
		}
		
		return sb.substring(0, sb.length() - 1).toString();
	}
}
