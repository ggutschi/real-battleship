package at.ac.uniklu.mobile.peer;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

import android.util.Log;
import at.ac.uniklu.mobile.message.ObservableMessage;
import at.ac.uniklu.mobile.message.UncoverMessage;
import at.ac.uniklu.mobile.message.VectorTimestamp;
import at.ac.uniklu.mobile.message.ObservableMessage.MessageIntend;
import at.ac.uniklu.mobile.util.Constants;

/**
 * class implementing a thread for handling peer messages
 *
 */
public class PeerCommunication extends Thread {
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

	  Log.e(Constants.LOG_TAG, "After catch.");
    }
    
    public void stopp() {
    	try {
			server.close();
		} catch (IOException e) {
	    	  Log.e(Constants.LOG_TAG, "IOException on server close: ", e);
		}
    }
    
    private void handleClientMessage(String input) {
    	// released;android_id
		String[] msgSplitted = input.split(Constants.MESSAGE_SEP_CHAR + "");
		
		if (msgSplitted[0].equalsIgnoreCase(Constants.UNCOVERED_MSG)) {

			String androidId = msgSplitted[1];

			// Adds the peer if not already in peer vector
			Log.d(Constants.LOG_TAG, "Adding peer " + androidId + " with IP " + server.getInetAddress());
			PeerManager.addPeer(new Peer(androidId, server.getInetAddress()));
			Log.d(Constants.LOG_TAG, "Peer added.");
			
			UncoverMessage um = new UncoverMessage(androidId, Integer.parseInt(msgSplitted[2]), UncoverMessage.extractVectorTimestamp(msgSplitted), Integer.parseInt(msgSplitted[3]), Integer.parseInt(msgSplitted[4]));
			
			if (PeerManager.getVectorTimestamp().causalError(um.getVectorTimestamp())) {
				Log.e(Constants.LOG_TAG, "Causal error arrised. myVT = " + PeerManager.getVectorTimestamp() + ", receivedVT = " + um.getVectorTimestamp());
			}
			
			PeerManager.getVectorTimestamp().adapt(um.getVectorTimestamp());
			PeerManager.getVectorTimestamp().next();
			
			PeerManager.getCurrentChallenge().uncoverCellLocally(Integer.parseInt(msgSplitted[3]), Integer.parseInt(msgSplitted[4]), PeerManager.getContext(), msgSplitted[1]);
		} else if (msgSplitted[0].equalsIgnoreCase(Constants.RELEASED_MSG)) {
			if (msgSplitted.length > 1) {
				String androidId = msgSplitted[1];
				
				PeerManager.removePeer(androidId);
			}
		} else if (msgSplitted[0].equalsIgnoreCase(Constants.JOINED_MSG)) {

			if (msgSplitted.length > 1) {
				String androidId = msgSplitted[2];

				Log.d(Constants.LOG_TAG, "Adding peer " + androidId + " with IP " + server.getInetAddress());
				PeerManager.addPeer(new Peer(androidId, server.getInetAddress()));
				Log.d(Constants.LOG_TAG, "Peer added.");
			}
		} else if (msgSplitted[0].equalsIgnoreCase(Constants.ALREADY_UNCOVERED_MSG)) {
			PeerManager.getCurrentChallenge().hasChanged();
			PeerManager.getCurrentChallenge().notifyObservers(new ObservableMessage(MessageIntend.SCORE_DECREMENT, new Integer(Constants.SHIPCELL_SCORE)));
		}
    }
}
