package at.ac.uniklu.mobile.message;

import java.util.HashMap;

public class VectorTimestamp {
	private HashMap<String, Integer> myVector = new HashMap<String, Integer>();
	private String androidId;
	
	
	public VectorTimestamp(String androidId) {
		this.androidId = androidId;
	}
	
	public HashMap<String, Integer> getVector() {
		return myVector;
	}
	
	public synchronized void adapt(VectorTimestamp received) {
		HashMap<String, Integer> receivedVector = received.getVector();
		
		for (String androidId : myVector.keySet()) {
			if (receivedVector.containsKey(androidId))
				if (receivedVector.get(androidId) > myVector.get(androidId))
					myVector.put(androidId, receivedVector.get(androidId));
		}
	}
	
	public synchronized void next()   {
		myVector.put(androidId, myVector.get(androidId) + 1);
	}

	public boolean causalError(VectorTimestamp received)   {
		return less(received.getVector(), myVector);
	}
	
	public static boolean equals (HashMap<String, Integer> hm1, HashMap<String, Integer> hm2) {
		for (String androidId : hm1.keySet())
			if (hm1.get(androidId) != hm2.get(androidId))
				return false;
		
		return true;
	}
	
	public static boolean less (HashMap<String, Integer> hm1, HashMap<String, Integer> hm2)   {
		if (equals(hm1, hm2))
			return false;

		for (String androidId : hm1.keySet())
			if (hm1.get(androidId) > hm2.get(androidId))
				return false;
				
		return true;
	}
}
