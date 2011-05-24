package at.ac.uniklu.mobile.db;

import org.json.JSONException;
import org.json.JSONObject;

public class Participant {
	/** the unique android identifier of a participant´s smartphone **/
	private String android_id;
	/** the inet address of a participant´s smartphone **/
	private String inet_addr;
	
	/** json field label of android id **/
	private static final String FIELD_ANDROID_ID = "android_id";
	
	/** json field label of ip address **/
	private static final String FIELD_INET_ADDR = "inet_addr";
	
	public Participant(JSONObject jsonObject) throws JSONException {
		android_id = jsonObject.getString(FIELD_ANDROID_ID);
		inet_addr = jsonObject.getString(FIELD_INET_ADDR);
	}

	public String getAndroid_id() {
		return android_id;
	}

	public void setAndroid_id(String androidId) {
		android_id = androidId;
	}

	public String getInet_addr() {
		return inet_addr;
	}

	public void setInet_addr(String inetAddr) {
		inet_addr = inetAddr;
	}

}
