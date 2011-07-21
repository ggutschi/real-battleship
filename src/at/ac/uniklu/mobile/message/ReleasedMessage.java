package at.ac.uniklu.mobile.message;

import java.io.Serializable;

import at.ac.uniklu.mobile.util.Constants;

/**
 * Message sent if peer is not available any more
 */
public class ReleasedMessage extends PeerMessage implements Serializable {
	
	private static final long serialVersionUID = 1L;


	/**
	 * @param androidId id of released peer
	 */
	public ReleasedMessage(String androidId) {
		this.androidId = androidId;
	}
	
	
	@Override
	public String toString() {		
		return Constants.RELEASED_MSG + Constants.MESSAGE_SEP_CHAR 
				+ this.androidId;
				
	}
}
