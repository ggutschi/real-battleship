/**
 * 
 */
package at.ac.uniklu.mobile.db;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Ship {
	
	/** id of the ship **/
	private int id;
	/** destroyed-state of a ship **/
	private boolean destroyed;
	
	/** list of ship positions **/
	private ArrayList<ShipPosition> shipPositions;
	
	/** json field label for id **/
	private static final String FIELD_ID = "id";
	/** json field label for destroyed-flag **/
	private static final String FIELD_DESTROYED = "destroyed";
	
	private static final String FIELD_SHIPPOSITIONS = "shippositions";
	
	public Ship(JSONObject jsonObject) throws JSONException {		
		id = jsonObject.getInt(FIELD_ID);
		destroyed = jsonObject.getBoolean(FIELD_DESTROYED);
		
		JSONArray jsonShipPositions = jsonObject.getJSONArray(FIELD_SHIPPOSITIONS);
		shipPositions = new ArrayList<ShipPosition>();
		  for (int i = 0; i < jsonShipPositions.length(); i++) 
		  {
			  ShipPosition sp = new ShipPosition(jsonShipPositions.getJSONObject(i));
			  shipPositions.add(sp);
		  }		
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public boolean isDestroyed() {
		return destroyed;
	}

	public void setDestroyed(boolean destroyed) {
		this.destroyed = destroyed;
	}

	public ArrayList<ShipPosition> getShipPositions() {
		return shipPositions;
	}

	public void setShipPositions(ArrayList<ShipPosition> shipPositions) {
		this.shipPositions = shipPositions;
	}

	public int getNumberOfUncoveredShipPositions() {
		int num = 0;
		
		for (ShipPosition sp : this.getShipPositions())
			if (sp.isUncovered())
				num++;
		
		return num;
	}
}
