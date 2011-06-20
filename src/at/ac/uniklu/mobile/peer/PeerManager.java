package at.ac.uniklu.mobile.peer;

import java.util.Vector;

import android.content.Context;
import at.ac.uniklu.mobile.db.Challenge;
import at.ac.uniklu.mobile.util.HelperUtil;

/**
 * handles peers
 *
 */
public class PeerManager {
	
	/** vector stores all online peers for a specific challenge **/
	private static Vector<Peer> peers;
	/** the current challenge object **/
	private static Challenge currentChallenge;
	/** the current application **/
	private static Context appContext; 
	
	/** the server ip which is responsible for initial peer registration **/
	//public static String RENDEZVOUS_SERVER_IP = "93.104.210.214";
	public static String RENDEZVOUS_SERVER_IP = "10.0.2.2";
	/** server port for intial peer registration **/
	public static int RENDEZVOUS_SERVER_PORT = 19423;
	public static String RENDEZVOUS_JOIN_MESSAGE = "joined";
	public static String RENDEZVOUS_MESSAGE_SEP_CHAR = ";";
	public static int RENDEZVOUS_TIME_PERIOD = 5000;
	public static String RENDEZVOUS_FIRST_PEER_MESSAGE = "OK";
	
	public static void init(Challenge challenge, Context context) {
		peers = new Vector<Peer>();
		currentChallenge = challenge; 
		appContext = context;
		startRendezvous();
	}
	
	public static synchronized void addPeer(Peer peer) {		
		peers.add(peer);
	}
	
	public static synchronized void removePeer(Peer peer) {
		peers.remove(peer);
	}
	
	public Vector<Peer> getPeers() {
		return peers;
	}
	
	public static Challenge getCurrentChallenge() {
		return currentChallenge;
	}

	/***
	 * Checks if the passed peer is already contained in the vector of peers 
	 * @param newPeer peer to check for 
	 * @return true if passed peer in contained, otherwise false
	 */
	public static void initPeers(Vector<Peer> currentPeers) {
		synchronized (peers) {
			peers = new Vector<Peer>();
			peers.addAll(currentPeers);
		}
	}
	
	public static void startRendezvous() {
		PeerRendezvousClient rc = new PeerRendezvousClient();
		PeerRendezvousServer rs = new PeerRendezvousServer(appContext, currentChallenge);
		rc.start();
		rs.start();
	}
}
