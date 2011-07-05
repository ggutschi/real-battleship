package at.ac.uniklu.mobile.db;

import org.json.JSONException;
import org.json.JSONObject;

public class Score {
	
	private Participant user;
	private int score;
	/** json key field for user **/
	private static String FIELD_PARTICIPANT = "user";

	
	public Score(JSONObject jsonObject) throws JSONException {
		user = new Participant(jsonObject.getJSONObject(FIELD_PARTICIPANT));
	}
	public Participant getUser() {
		return user;
	}
	public void setUser(Participant user) {
		this.user = user;
	}
}
