package at.ac.uniklu.mobile.message;

import java.io.Serializable;

import at.ac.uniklu.mobile.db.Challenge;
import at.ac.uniklu.mobile.util.Constants;

public class ReleasedMessage implements Serializable {
	
	private String androidId;
	
	public ReleasedMessage(String androidId) {
		this.androidId = androidId;
	}
	
	
	@Override
	public String toString() {		
		return Constants.RELEASED_MSG + Constants.MESSAGE_SEP_CHAR 
				+ this.androidId;
				
	}
}
