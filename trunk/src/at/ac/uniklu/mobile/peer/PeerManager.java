package at.ac.uniklu.mobile.peer;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Vector;

import android.content.Context;
import android.util.Log;
import at.ac.uniklu.mobile.db.Challenge;
import at.ac.uniklu.mobile.util.Constants;
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
	public static String RENDEZVOUS_SERVER_IP = "93.104.210.214";
	//public static String RENDEZVOUS_SERVER_IP = "192.168.1.101";
	public static String RENDEZVOUS_JOIN_MESSAGE = "joined";
	public static String RENDEZVOUS_MESSAGE_SEP_CHAR = ";";
	public static String RENDEZVOUS_FIRST_PEER_MESSAGE = "OK";
	
	public static Peer myPeer;
	public static Thread peerServerThread;
	
	public static PeerRendezvousClient rc;
	
	public static void init(Challenge challenge, Context context) {
		peers = new Vector<Peer>();
		currentChallenge = challenge; 
		appContext = context;
		
		try {
			Log.d(Constants.LOG_TAG, "Creating local peer...");
			myPeer =  new Peer(HelperUtil.getAndroidId(context), InetAddress.getByName(HelperUtil.getIpAddress()));
			Log.d(Constants.LOG_TAG, "Peer created with vectortimestamp " + myPeer.getVectorTimestamp());
			
			peerServerThread = new Thread(myPeer);
			
			peerServerThread.start();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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
			
			for (Peer p : peers)
				if (!p.isServer())
					p.connectToPeer(true);
				else
					p.connectToPeer(false);
		}
	}
	
	public static  void startRendezvous() {
		synchronized (peers) {
			rc = new PeerRendezvousClient(appContext, currentChallenge);
			rc.getPeers();
		}
	}
	
	public static void sendUncoverMessage(int x, int y) {
		for (Peer p : peers)
			p.sendUncoverMessage(x, y);
	}
}
