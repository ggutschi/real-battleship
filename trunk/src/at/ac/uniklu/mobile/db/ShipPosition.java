package at.ac.uniklu.mobile.db;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * describes each position of a ship or a part of a ship in an overlay of a google map (grid cells)
 */
public class ShipPosition {	
	/** the row of a ship or a part of a ship **/
	private int row;
	/** the column of a ship or a part of a ship **/
	private int column;
	
	/** json field label for row **/
	private static final String FIELD_ROW = "row";
	/** json field label for col **/
	private static final String FIELD_COL = "column";
	
	public ShipPosition(JSONObject jsonObject) throws JSONException {		
		row = jsonObject.getInt(FIELD_ROW);
		column = jsonObject.getInt(FIELD_COL);
	}

}
