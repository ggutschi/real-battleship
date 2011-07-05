package at.ac.uniklu.mobile.db;

import org.json.JSONException;
import org.json.JSONObject;

public class Score {
	
	private Participant user;
	private int score;
	/** json key field for user **/
	private static String FIELD_PARTICIPANT = "user";
	/** json key field for score **/
	private static String FIELD_SCORE = "score";
	
	public Score(JSONObject jsonObject) throws JSONException {
		user = new Participant(jsonObject.getJSONObject(FIELD_PARTICIPANT));
		score = jsonObject.getInt(FIELD_SCORE);
	}
	public Participant getUser() {
		return user;
	}
	public void setUser(Participant user) {
		this.user = user;
	}
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}
}
