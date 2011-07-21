package at.ac.uniklu.mobile.message;

import java.util.LinkedList;

import at.ac.uniklu.mobile.peer.PeerManager;

/**
 * Logger for peer messages to resolve message conflicts
 */
public class MessageLog {
	/**
	 * Message list
	 */
	private LinkedList<PeerMessage> ll = new LinkedList<PeerMessage>();
	
	/**
	 * Adds the given message to the list
	 * @param m message to add
	 */
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
	
	/**
	 * Clears the message list
	 */
	public void clearLog() {
		synchronized(ll) {
			ll.clear();
		}
	}
}
