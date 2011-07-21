package at.ac.uniklu.mobile.peer;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

import android.util.Log;
import at.ac.uniklu.mobile.message.ObservableMessage;
import at.ac.uniklu.mobile.message.ReleasedMessage;
import at.ac.uniklu.mobile.message.UncoverMessage;
import at.ac.uniklu.mobile.message.ObservableMessage.MessageIntend;
import at.ac.uniklu.mobile.util.Constants;

/**
 * Describes a single peer in the network
 */
public class Peer implements Runnable {
	
	/**
	 * android device id
	 */
	private String androidId;
	
	/**
	 * ip address of peer
	 */
	private InetAddress ipAddress;
	
	/**
	 * socket for connecting to peer
	 */
	private Socket socket;
	
	/**
	 * print writer for writing to socket
	 */
	private PrintWriter out;
	
	/**
	 * socket for incoming socket connection
	 */
	private Socket sSocket;
	
	/**
	 * server socket for incoming connection
	 */
	private ServerSocket ss;
	
	/**
	 * vector of threads handling communications to all other peers
	 */
	private Vector<PeerCommunication> peerCommunications = new Vector<PeerCommunication>();
	
	
	private boolean stop = false;
	
	public Peer(String androidId, InetAddress ipAddress) {
		Log.d(Constants.LOG_TAG, "Creating local peer (constructor)...");
		this.androidId = androidId;
		this.ipAddress = ipAddress;
		Log.d(Constants.LOG_TAG, "Local peer created (constructor).");
	}
	
	public Vector<PeerCommunication> getPeerCommunication() {
		return peerCommunications;
	}
	
	
	/**
	 * connects to the current peer
	 * @param rendezvous if true, a rendezvous message will be send to the peer
	 * @return true if successfully connected, false otherwise
	 */
	public boolean connectToPeer(boolean rendezvous) {
		try {
			Log.d(Constants.LOG_TAG, "Connecting to peer " + androidId + " with IP " + ipAddress + " (rendezvous = " + rendezvous + ") ...");
			InetSocketAddress sa = new InetSocketAddress(ipAddress, Constants.PEER_TO_PEER_PORT);
			
			socket = new Socket();
			
			socket.connect(sa, 8000);
			
			out = new PrintWriter(socket.getOutputStream(), true);

			if (rendezvous) {
				Log.d(Constants.LOG_TAG , "Peer try to send message...");
			
				String msg = PeerManager.rc.getRendezvousMessage();
				out.println(msg);
				//out.flush();
				Log.d(Constants.LOG_TAG, "Peer message " + msg + " was sent!");
			}
			
			Log.d(Constants.LOG_TAG, "Socket opened: Peer " + androidId);
			
			return true;
		}
		catch(Exception ex) {
			Log.d(Constants.LOG_TAG, "Peer " + androidId + " hasn't answered. Tell your peers!");
			
			PeerManager.getCurrentChallenge().setChanged();
			PeerManager.getCurrentChallenge().notifyObservers(new ObservableMessage(MessageIntend.DEBUG_MESSAGE, "No answer from peer " + androidId + ". Tell your peers!"));
			
			return false;
		}
	}
	
	
	/**
	 * checks if peer is the server
	 * @return true if peer is server, false otherwise
	 */
	public boolean isServer() {
		return this.ipAddress.getHostAddress().equals(PeerManager.RENDEZVOUS_SERVER_IP);
	}
	
	/**
	 * closes the connection to this peer
	 */
	public void closeSocket() {
		try {
			if (out != null)
				out.close();
			
			if (socket != null)
				socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String getAndroidId() {
		return androidId;
	}
	public void setAndroidId(String androidId) {
		this.androidId = androidId;
	}
	public InetAddress getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(InetAddress ipAddress) {
		this.ipAddress = ipAddress;
	}
	
	/**
	 * sends an uncover message to this peer
	 * @param x x coordinate of uncovered cell
	 * @param y y coordinate of uncovered cell
	 */
	public void sendUncoverMessage(int x, int y) {
		if (socket != null && socket.isConnected()) {
			Log.d(Constants.LOG_TAG , "Peer try to send message to android id " + this.androidId);
			PeerManager.getVectorTimestamp().next();
			
			UncoverMessage um = new UncoverMessage(PeerManager.myPeer.androidId, PeerManager.getCurrentChallenge().getId(), PeerManager.getVectorTimestamp(), x, y);			
			String msg = um.toString();
			out.println(msg);
			
			// log message for resolving conflicts
			PeerManager.log.addMessage(um);
			
			//out.flush();
			Log.d(Constants.LOG_TAG, "Peer message " + msg + " was sent!");
	
			PeerManager.getCurrentChallenge().setChanged();
			PeerManager.getCurrentChallenge().notifyObservers(new ObservableMessage(MessageIntend.DEBUG_MESSAGE, "Sent message: " + msg));
		} else {
			PeerManager.sendReleaseMessage(androidId);
			Log.d(Constants.LOG_TAG , "Socket to " + this.androidId + " with IP " + this.ipAddress + " null or not connected. (socket = " + socket + ", ss = " + ss + ", sSocket = " + sSocket + ")");
		}
	}
	
	/**
	 * sends a release message to the current peer
	 * @param androidId android id of sending peer
	 */
	public void sendReleaseMessage(String androidId) {
		if (socket != null && socket.isConnected()) {
			Log.d(Constants.LOG_TAG , "Peer try to send message...");
			String msg = (new ReleasedMessage(androidId)).toString();
			out.println(msg);
			//out.flush();
			Log.d(Constants.LOG_TAG, "Peer message " + msg + " was sent!");
	
			PeerManager.getCurrentChallenge().setChanged();
			PeerManager.getCurrentChallenge().notifyObservers(new ObservableMessage(MessageIntend.DEBUG_MESSAGE, "Sent message: " + msg));
		} else
			Log.d(Constants.LOG_TAG , "Socket to " + this.androidId + " with IP " + this.ipAddress + " null or not connected. (socket = " + socket + ", ss = " + ss + ", sSocket = " + sSocket + ")");
	}
	
	public void run() {
		try {
			ss = new ServerSocket(Constants.PEER_TO_PEER_PORT);
			
			while (!stop) {
				sSocket = ss.accept();
				Log.d(Constants.LOG_TAG, "Socket connection accepted.");

				PeerCommunication peerComm = new PeerCommunication(sSocket);
				peerCommunications.add(peerComm);
				peerComm.start();
			}			

		} catch (IOException e) {
			Log.e(Constants.LOG_TAG, "IO Exception (stop = "+ stop + ")", e);
		}
	}
	
	public void stopp() {
		
		stop = true;
		
		try {
			
			if (sSocket != null)
				sSocket.close();
			if (socket != null)
				socket.close();
			if (ss != null)
				ss.close();
			
		} catch (IOException e) {
			Log.e(Constants.LOG_TAG, "Socket close exception.", e);
		}
	}
	
	

}

