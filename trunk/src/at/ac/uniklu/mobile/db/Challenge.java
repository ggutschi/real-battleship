package at.ac.uniklu.mobile.db;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import at.ac.uniklu.mobile.util.Constants;

import com.google.android.maps.GeoPoint;

/**
 * Class describing the data structure of a challenge
 */
public class Challenge {
	
	/** challenge name field **/
	public static final String FIELD_ID = "id";
	
	/** challenge name field **/
	public static final String FIELD_NAME = "name";
	
	/** challenge active field **/
	public static final String FIELD_ACTIVE = "active";
	
	/** challenge cellsX field **/
	public static final String FIELD_CELLS_X = "cellsX";
	
	/** challenge cellsY field **/
	public static final String FIELD_CELLS_Y = "cellsY";
	
	/** challenge participants field **/
	public static final String FIELD_PARTICIPANTS = "participants";
	
	/** challenge ship positions field **/
	public static final String FIELD_SHIP_POSITIONS = "shippositions";
	
	/** challenge location left top field **/
	public static final String FIELD_GEOLOCATION = "geolocation";
	
	/** challenge location left top field **/
	public static final String FIELD_GEOLOCATION_LEFT_TOP = "locationLeftTop";
	
	/** challenge location right bottom field **/
	public static final String FIELD_GEOLOCATION_RIGHT_BOTTOM = "locationRightBottom";
	
	/** challenge lattitude for left top/right bottom **/
	public static final String FIELD_GEOLOCATION_LAT = "lat";
	/** challenge longitude for left top/right bottom **/
	public static final String FIELD_GEOLOCATION_LON = "lon";
	
	
	/** challenge name field **/
	public static final String FIELD_LOCATION = "location";
	
	/** id of a challenge **/
	private int id;	
	/** name of a challenge **/
	private String name;
	/** name of the location of the challenge **/
	private String location;
	/** boolean indicating the state of a challenge (active or not) **/
	private boolean active;
	
	/** list of challenge participants **/
	private ArrayList<Participant> participants;
	/** the top left geo point of the challenge area **/
	private GeoPoint locationLeftTop;
	/** the right bottom geo point of the challenge area **/
	private GeoPoint locationRightBottom;
	
	/** how many cells between the two geo points should be placed on X-coordinate **/
	private int cellsX;
	
	/** how many cells between the two geo points should be placed on Y-coordinate **/
	private int cellsY;
	
	/** list of all ship positions (row, col) **/
	private ArrayList<ShipPosition> shipPositions;
	
	public Challenge(int id, String name, boolean active, GeoPoint locLeftTop, String location) {
		this.id = id;
		this.name = name;		
		this.active = active;
		this.location = location;
		this.locationLeftTop = locLeftTop;
	}
	
	/**
	 * build challenge from a JSON object
	 * @param jsonChallenge
	 * @throws JSONException
	 */
	public Challenge(JSONObject jsonChallenge) throws JSONException {
		  id = jsonChallenge.getInt(FIELD_ID);
		  name = jsonChallenge.getString(FIELD_NAME);
		  active = jsonChallenge.getBoolean(FIELD_ACTIVE);
		  location = jsonChallenge.getString(FIELD_LOCATION);
		  cellsX = jsonChallenge.getInt(FIELD_CELLS_X);
		  cellsY = jsonChallenge.getInt(FIELD_CELLS_Y);		  
		  JSONObject geoLocation_leftTop = jsonChallenge.getJSONObject(FIELD_GEOLOCATION_LEFT_TOP);		  
		  locationLeftTop = new GeoPoint((int)(geoLocation_leftTop.getDouble(FIELD_GEOLOCATION_LAT)*1E6), (int)(geoLocation_leftTop.getDouble(FIELD_GEOLOCATION_LON)*1E6));
		  JSONObject geoLocation_rightBottom = jsonChallenge.getJSONObject(FIELD_GEOLOCATION_RIGHT_BOTTOM);
		  locationRightBottom = new GeoPoint((int)(geoLocation_rightBottom.getDouble(FIELD_GEOLOCATION_LAT)*1E6), (int)(geoLocation_rightBottom.getDouble(FIELD_GEOLOCATION_LON)*1E6));
		  
		  
		  JSONArray jsonParticipants = jsonChallenge.getJSONArray(FIELD_PARTICIPANTS);
		  participants = new ArrayList<Participant>();
		  for (int i = 0; i < jsonParticipants.length(); i++) 
		  {
			  Participant p = new Participant(jsonParticipants.getJSONObject(i));
			  participants.add(p);
		  }
		  JSONArray jsonShipPositions = jsonChallenge.getJSONArray(FIELD_SHIP_POSITIONS);
		  shipPositions = new ArrayList<ShipPosition>();
		  
		  for (int i = 0; i < jsonShipPositions.length(); i++) 
		  {
			  ShipPosition sp = new ShipPosition(jsonShipPositions.getJSONObject(i));
			  shipPositions.add(sp);
		  }
		  Log.d(Constants.LOG_TAG, "challenge object constructed with lat: " + locationLeftTop.getLatitudeE6());
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public ArrayList<Participant> getParticipants() {
		return participants;
	}
	public void setParticipants(ArrayList<Participant> participants) {
		this.participants = participants;
	}
	public GeoPoint getLocationLeftTop() {
		return locationLeftTop;
	}
	public void setLocationLeftTop(GeoPoint locationLeftTop) {
		this.locationLeftTop = locationLeftTop;
	}
	public GeoPoint getLocationRightBottom() {
		return locationRightBottom;
	}
	public void setLocationRightBottom(GeoPoint locationRightBottom) {
		this.locationRightBottom = locationRightBottom;
	}
	public ArrayList<ShipPosition> getShipPositions() {
		return shipPositions;
	}
	public void setShipPositions(ArrayList<ShipPosition> shipPositions) {
		this.shipPositions = shipPositions;
	};
	
	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}
	
	public int getCellsX() {
		return cellsX;
	}

	public void setCellsX(int cellsX) {
		this.cellsX = cellsX;
	}

	public int getCellsY() {
		return cellsY;
	}

	public void setCellsY(int cellsY) {
		this.cellsY = cellsY;
	}

}
