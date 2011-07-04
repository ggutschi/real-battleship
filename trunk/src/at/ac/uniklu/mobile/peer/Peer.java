package at.ac.uniklu.mobile.peer;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
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
import at.ac.uniklu.mobile.message.VectorTimestamp;
import at.ac.uniklu.mobile.message.ObservableMessage.MessageIntend;
import at.ac.uniklu.mobile.util.Constants;

/**
 * describes one peer in network
 */
public class Peer implements Runnable {
	
	private String androidId;
	private InetAddress ipAddress;
	private Socket socket;
	private PrintWriter out;
	private Socket sSocket;
	private ServerSocket ss;
	private Vector<Thread> peerThreads;
	
	


	private boolean stop = false;
	
	public Peer(String androidId, InetAddress ipAddress) {
		Log.d(Constants.LOG_TAG, "Creating local peer (constructor)...");
		this.androidId = androidId;
		this.ipAddress = ipAddress;
		Log.d(Constants.LOG_TAG, "Local peer created (constructor).");
	}
	
	public Vector<Thread> getPeerThreads() {
		return peerThreads;
	}
	
	public boolean connectToPeer(boolean rendezvous) {
		try {
			Log.d(Constants.LOG_TAG, "Connecting to peer " + androidId + " with IP " + ipAddress + " (rendezvous = " + rendezvous + ") ...");
			InetSocketAddress sa = new InetSocketAddress(ipAddress, Constants.PEER_TO_PEER_PORT);
			
			socket = new Socket();
			
			socket.connect(sa, 4000);
			
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
	
	public boolean isServer() {
		return this.ipAddress.getHostAddress().equals(PeerManager.RENDEZVOUS_SERVER_IP);
	}
	
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
	
	public void sendUncoverMessage(int x, int y) {
		if (socket != null && socket.isConnected()) {
			Log.d(Constants.LOG_TAG , "Peer try to send message to android id " + this.androidId);
			PeerManager.getVectorTimestamp().next();
			String msg = (new UncoverMessage(PeerManager.myPeer.androidId, PeerManager.getCurrentChallenge(), PeerManager.getVectorTimestamp(), x, y)).toString();
			out.println(msg);
			//out.flush();
			Log.d(Constants.LOG_TAG, "Peer message " + msg + " was sent!");
	
			PeerManager.getCurrentChallenge().setChanged();
			PeerManager.getCurrentChallenge().notifyObservers(new ObservableMessage(MessageIntend.DEBUG_MESSAGE, "Sent message: " + msg));
		} else {
			PeerManager.sendReleaseMessage(androidId);
			Log.d(Constants.LOG_TAG , "Socket to " + this.androidId + " with IP " + this.ipAddress + " null or not connected.");
		}
	}
	
	
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
			Log.d(Constants.LOG_TAG , "Socket to " + this.androidId + " with IP " + this.ipAddress + " null or not connected.");
	}
	
	public void run() {
		try {
			ss = new ServerSocket(Constants.PEER_TO_PEER_PORT);
			
			while (!stop) {
				sSocket = ss.accept();
				Log.d(Constants.LOG_TAG, "Socket connection accepted.");
				PeerCommunication peerComm = new PeerCommunication(sSocket);
				Thread t = new Thread(peerComm);
				peerThreads.add(t);
				t.start();
			}			

		} catch (IOException e) {
			Log.e(Constants.LOG_TAG, "IO Exception", e);
		}
	}
	
	public void stopp() {
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
		
		stop = true;
	}
	
	
	/**
	 * class implementing a thread for handling peer messages
	 *
	 */
	private class PeerCommunication implements Runnable {
	    private Socket server;
	    private String line,input;

	    public PeerCommunication(Socket server) {
	      this.server=server;
	    }

	    public void run () {

	      try {
	        // Get input from the client
	        DataInputStream in = new DataInputStream (server.getInputStream());
	        PrintStream out = new PrintStream(server.getOutputStream());

	        while((line = in.readLine()) != null) {
	        	Log.d(Constants.LOG_TAG, "handle peer message: " +  line);
	        	handleClientMessage(line);
	        }
	        
	        server.close();
	      } catch (IOException ioe) {
	    	  Log.e(Constants.LOG_TAG, "IOException on socket listen: ", ioe);
	      }
	    }
	    
	    private void handleClientMessage(String input) {
	    	// released;android_id
			String[] msgSplitted = input.split(Constants.MESSAGE_SEP_CHAR + "");
			
			if (msgSplitted[0].equalsIgnoreCase(Constants.UNCOVERED_MSG)) {
				VectorTimestamp receivedVectorTimestamp = UncoverMessage.getVectorTimestamp(msgSplitted);
				
				if (PeerManager.getVectorTimestamp().causalError(receivedVectorTimestamp)) {
					PeerManager.getCurrentChallenge().notifyObservers(new ObservableMessage(MessageIntend.DEBUG_MESSAGE, "CAUSAL ERROR"));
				}
				
				PeerManager.getVectorTimestamp().adapt(receivedVectorTimestamp);
				PeerManager.getVectorTimestamp().next();
				
				PeerManager.getCurrentChallenge().uncoverCellLocally(Integer.parseInt(msgSplitted[3]), Integer.parseInt(msgSplitted[4]), PeerManager.getContext());
			} else if (msgSplitted[0].equalsIgnoreCase(Constants.RELEASED_MSG)) {
				if (msgSplitted.length > 1) {
					String androidId = msgSplitted[1];
					
					PeerManager.removePeer(androidId);
				}
			} else if (msgSplitted[0].equalsIgnoreCase(Constants.JOINED_MSG)) {

				if (msgSplitted.length > 1) {
					String androidId = msgSplitted[2];

					Log.d(Constants.LOG_TAG, "Adding peer " + androidId + " with IP " + sSocket.getInetAddress());
					PeerManager.addPeer(new Peer(androidId, sSocket.getInetAddress()));
					Log.d(Constants.LOG_TAG, "Peer added.");
				}
			}
	    }
	}
}

