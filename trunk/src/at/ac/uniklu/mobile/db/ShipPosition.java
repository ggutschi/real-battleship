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
	/** is a cell already uncovered by another player **/
	private boolean uncovered;
	
	/** json field label for row **/
	private static final String FIELD_ROW = "row";
	/** json field label for col **/
	private static final String FIELD_COL = "column";
	/** json field label for uncovered **/
	private static final String FIELD_UNCOVERED = "uncovered";
	
	public ShipPosition(JSONObject jsonObject) throws JSONException {		
		row = jsonObject.getInt(FIELD_ROW);
		column = jsonObject.getInt(FIELD_COL);
		uncovered = jsonObject.getBoolean(FIELD_UNCOVERED);
	}
	
	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public int getColumn() {
		return column;
	}

	public void setColumn(int column) {
		this.column = column;
	}

	public boolean isUncovered() {
		return uncovered;
	}

	public void setUncovered(boolean uncovered) {
		this.uncovered = uncovered;
	}


}
