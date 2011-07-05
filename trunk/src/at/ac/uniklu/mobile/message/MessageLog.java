package at.ac.uniklu.mobile.message;

import java.util.LinkedList;

import at.ac.uniklu.mobile.peer.PeerManager;

public class MessageLog {
	private LinkedList<PeerMessage> ll = new LinkedList<PeerMessage>();
	
	
	public void addMessage(PeerMessage m) {
		synchronized(ll) {
			ll.add(m);
		}
	}
	
	/**
	 * Checks for uncoverings in the MessageLog and applies them to the current challenge.
	 */
	public void checkForUncoveredCells() {
		synchronized(ll) {
			for (PeerMessage pm : ll) {
				if (pm instanceof UncoverMessage && ((UncoverMessage)pm).getChallengeId() == PeerManager.getCurrentChallenge().getId()) {			
					PeerManager.getCurrentChallenge().uncoverCellLocally(((UncoverMessage)pm).getX(), ((UncoverMessage)pm).getY(), PeerManager.getContext(), ((UncoverMessage)pm).androidId);
				}
			}
		}
	}
	
	public void clearLog() {
		synchronized(ll) {
			ll.clear();
		}
	}
}
