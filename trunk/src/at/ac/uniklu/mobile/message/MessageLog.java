package at.ac.uniklu.mobile.message;

import java.util.LinkedList;

public class MessageLog {
	private LinkedList<PeerMessage> ll = new LinkedList<PeerMessage>();
	
	
	public void addMessage(PeerMessage m) {
		synchronized(ll) {
			ll.add(m);
		}
	}
	
	public void searchForConflict(PeerMessage m) {
		synchronized(ll) {
			for (PeerMessage pm : ll) {
				// SEARCH AND RESOLVE CONFLICT
			}
		}
	}
	
	public void clearLog() {
		synchronized(ll) {
			ll.clear();
		}
	}
}
