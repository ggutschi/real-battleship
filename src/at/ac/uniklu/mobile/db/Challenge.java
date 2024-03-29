package at.ac.uniklu.mobile.db;

import java.util.ArrayList;
import java.util.Observable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import at.ac.uniklu.mobile.R;
import at.ac.uniklu.mobile.message.ObservableMessage;
import at.ac.uniklu.mobile.message.ObservableMessage.MessageIntend;
import at.ac.uniklu.mobile.peer.PeerManager;
import at.ac.uniklu.mobile.util.Constants;

import com.google.android.maps.GeoPoint;

/**
 * Class describing the data structure of a challenge
 */
public class Challenge extends Observable {
	
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
	
	/** challenge ships field **/
	public static final String FIELD_SHIPS = "ships";
	
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
	private ArrayList<Ship> ships;
	
	
	private ArrayList<ArrayList<Boolean>> uncoveredCells;
	
	
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
		  JSONArray jsonShipPositions = jsonChallenge.getJSONArray(FIELD_SHIPS);
		  ships = new ArrayList<Ship>();
		  
		  for (int i = 0; i < jsonShipPositions.length(); i++) 
		  {
			  Ship s = new Ship(jsonShipPositions.getJSONObject(i));
			  ships.add(s);
		  }
		  Log.d(Constants.LOG_TAG, "challenge object constructed with lat: " + locationLeftTop.getLatitudeE6());
		  
		  initializeUncoveredCells();
	}
	
	private void initializeUncoveredCells() {
		  uncoveredCells = new ArrayList<ArrayList<Boolean>>();
		  
		  for (int i = 0; i < this.getCellsY(); i++) {
			  uncoveredCells.add(new ArrayList<Boolean>());
			  
			  for (int j = 0; j < this.getCellsX(); j++)
				  uncoveredCells.get(i).add(false);
		  }
		  
		  for (Ship s : ships)
			  for (ShipPosition sp : s.getShipPositions())
				  if (sp.isUncovered())
					  uncoveredCells.get(sp.getRow()).set(sp.getColumn(), true);
	}
	
	public void setChanged() {
		super.setChanged();
	}
	
	/**
	 * Uncovers the shipposition at position x, y at the local peer
	 * @param x x coordinate of cell
	 * @param y y coordinate of cell
	 * @param c Context
	 * @param androidId uncovering android device id
	 */
	private void uncoverShipPositionLocallyAt(int x, int y, Context c, String androidId) {
		for (Ship s : this.ships)
			for (ShipPosition sp : s.getShipPositions())
				if (sp.getRow() == y && sp.getColumn() == x) {
					
					if (!sp.isUncovered()) {

		        		Log.d(Constants.LOG_TAG, "ShipPosition SETTED AS UNCOVERED challenge = " + this);
						sp.setUncovered(true);
	            		
	            		if (androidId.equals(PeerManager.myPeer.getAndroidId())) {
		            		setChanged();
		            		notifyObservers(new ObservableMessage(MessageIntend.SCORE_INCREMENT, new Integer(Constants.SHIPCELL_SCORE)));
	            		}
						
		            	if (s.getNumberOfUncoveredShipPositions() == s.getShipPositions().size()) {
		            		if (androidId.equals(PeerManager.myPeer.getAndroidId()))
		            			Toast.makeText(c, R.string.ship_uncovered, Toast.LENGTH_SHORT).show();
		            		// notify activity view that score has to be changed
		            		s.setDestroyed(true);

		                	Log.d(Constants.LOG_TAG, "ship destroyed.");
		            	} else {
		            		if (androidId.equals(PeerManager.myPeer.getAndroidId())) {
		            			Toast.makeText(c, R.string.ship_position_uncovered, Toast.LENGTH_SHORT).show();
		            		}
		            	}
					}
		    		
		        	Log.d(Constants.LOG_TAG, "ShipPosition uncovered");
				}
	}
	
	/**
	 * Proofs if all ships are destroyed and game is over
	 * @return true if all ships are destroyed, false otherwise
	 */
	private boolean allShipsDestroyed() {
    	Log.d(Constants.LOG_TAG, "all ships destroyed...");
		for (Ship s : ships)
			if (!s.isDestroyed()) {

		    	Log.d(Constants.LOG_TAG, "ship not destroyed");
				return false;
			}
		
		return true;
	}
	
	/**
	 * Uncovers the cell at position x, y at the local peer
	 * @param x x coordinate of cell
	 * @param y y coordinate of cell
	 * @param c Context
	 * @param androidId uncovering android device id
	 */
	public void uncoverCellLocally(int x, int y, Context c, String androidId) {
		if (y < uncoveredCells.size() && uncoveredCells.size() > 0 && x < uncoveredCells.get(0).size()) {

        	Log.d(Constants.LOG_TAG, "Uncovering cell (" + x + ", " + y + ")");
        	
        	if (uncoveredCells.get(y).get(x)) {

        		if (androidId.equals(PeerManager.myPeer.getAndroidId()))
        			Toast.makeText(c, R.string.already_uncovered, Toast.LENGTH_SHORT).show();
        	}
        	
        	uncoveredCells.get(y).set(x, true);

			this.uncoverShipPositionLocallyAt(x, y, c, androidId);
        	
        	String msg = "";
        	
        	if (allShipsDestroyed()) {
        		msg = "gameover";
            	Log.d(Constants.LOG_TAG, "msg = gameover");
        	}
        	
    		setChanged();
    		notifyObservers(new ObservableMessage(MessageIntend.UPDATE_MAP, msg));
			
		} else
        	Log.d(Constants.LOG_TAG, "Uncovering cells failed");
		
	}
	
	/**
	 * Proofs if cell at position x, y is a shipcell
	 * @param x
	 * @param y
	 * @return true if cell is shipcell, false otherwise
	 */
	public boolean isShipCell(int x, int y) {
		for (Ship s : this.ships)
			for (ShipPosition sp : s.getShipPositions())
				if (sp.getRow() == y && sp.getColumn() == x)
					return true;
		
		return false;
	}
	
	
	/**
	 * Resets the challenge at the local peer
	 */
	public void resetChallengeLocally() {
		for (Ship s : ships)
			for (ShipPosition sp : s.getShipPositions())
				sp.setUncovered(false);
		
		for (ArrayList<Boolean> row : uncoveredCells)
			for (int i = 0; i < row.size(); i++)
				row.set(i, false);
	}
	
	public boolean isUncovered(int x, int y) {
		if (y < uncoveredCells.size() && uncoveredCells.size() > 0 && x < uncoveredCells.get(0).size())
			return uncoveredCells.get(y).get(x);
			
		return false;
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
	public ArrayList<Ship> getShips() {
		return ships;
	}
	public void setShips(ArrayList<Ship> ships) {
		this.ships = ships;
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
