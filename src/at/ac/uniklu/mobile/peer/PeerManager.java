package at.ac.uniklu.mobile.peer;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Vector;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import at.ac.uniklu.mobile.db.Challenge;
import at.ac.uniklu.mobile.message.ObservableMessage;
import at.ac.uniklu.mobile.message.VectorTimestamp;
import at.ac.uniklu.mobile.message.ObservableMessage.MessageIntend;
import at.ac.uniklu.mobile.util.Constants;
import at.ac.uniklu.mobile.util.HelperUtil;

/**
 * handles peers
 *
 */
public class PeerManager {
	
	/** vector stores all online peers for a specific challenge **/
	private static Vector<Peer> peers;
	/** vector timestamp for message passing **/
	private static VectorTimestamp vectorTimestamp;
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
		
		vectorTimestamp = new VectorTimestamp(HelperUtil.getAndroidId(appContext));
		
		try {
			Log.d(Constants.LOG_TAG, "Creating local peer with android id " + HelperUtil.getAndroidId(context));
			myPeer =  new Peer(HelperUtil.getAndroidId(context), InetAddress.getByName(HelperUtil.getIpAddress()));
			Log.d(Constants.LOG_TAG, "Peer created with android id " + HelperUtil.getAndroidId(context));
			
			peerServerThread = new Thread(myPeer);
			
			peerServerThread.start();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		startRendezvous();
	}
	
	public static void closeConnections() {
		synchronized (peers) {
			for (Peer p : peers)
				p.closeSocket();
		}
	}
	
	public static void addPeer(Peer peer) {		
		synchronized (peers) {
			peers.add(peer);
			vectorTimestamp.getVector().put(peer.getAndroidId(), 0);
		}
	}
	
	public static void removePeer(Peer peer) {	
		synchronized (peers) {
			vectorTimestamp.getVector().remove(peer.getAndroidId());
			peers.remove(peer);
		}
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
		boolean exists = false;
		
		synchronized (peers) {
			peers = new Vector<Peer>();
			peers.addAll(currentPeers);
			
			vectorTimestamp = new VectorTimestamp(HelperUtil.getAndroidId(appContext));
			
			for (int i=0; i < peers.size(); i++) {
				if (!peers.get(i).isServer())
					exists = peers.get(i).connectToPeer(true);
				else
					exists = peers.get(i).connectToPeer(false);
				
				if (!exists) {
					PeerManager.sendReleaseMessage(peers.get(i).getAndroidId());
					PeerManager.removePeer(peers.get(i));

					if (i != peers.size())
						i--;
				} else
					vectorTimestamp.getVector().put(peers.get(i).getAndroidId(), 0);
			}
		}
	}
	
	public static VectorTimestamp getVectorTimestamp() {
		return vectorTimestamp;
	}
	
	public static void startRendezvous() {
		synchronized (peers) {
			rc = new PeerRendezvousClient(appContext, currentChallenge);
			rc.getPeers();
		}
	}
	
	public static void sendUncoverMessage(int x, int y) {
		synchronized (peers) {
			for (Peer p : peers)
				p.sendUncoverMessage(x, y);
		}
	}
	
	public static void removePeer(String androidId) {
		synchronized (peers) {
			Peer tmp = null;
			
			for (int i = 0; i < peers.size() && tmp == null; i++)
				if (peers.get(i).getAndroidId().equals(androidId)) {
					
					tmp = peers.get(i);
				}
			
			tmp.closeSocket();
			removePeer(tmp);
		}
	}
	
	public static void sendReleaseMessage(String androidId) 
	{
		synchronized (peers) {
			for (Peer p : peers)
				p.sendReleaseMessage(androidId);
		}
	}
	
	public static Context getContext() {
		return appContext;
	}
}
