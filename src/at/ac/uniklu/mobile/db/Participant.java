package at.ac.uniklu.mobile.db;

import org.json.JSONException;
import org.json.JSONObject;

public class Participant {
	/** the unique android identifier of a participantīs smartphone **/
	private String android_id;
	/** the inet address of a participantīs smartphone **/
	private String inet_addr;
	
	/** json field label of android id **/
	private static final String FIELD_ANDROID_ID = "android_id";
	
	/** json field label of ip address **/
	private static final String FIELD_INET_ADDR = "inet_addr";
	
	public Participant(JSONObject jsonObject) throws JSONException {
		android_id = jsonObject.getString(FIELD_ANDROID_ID);
		inet_addr = jsonObject.getString(FIELD_INET_ADDR);
	}

}
