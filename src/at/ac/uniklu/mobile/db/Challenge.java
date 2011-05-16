package at.ac.uniklu.mobile.db;

import java.util.ArrayList;

import com.google.android.maps.GeoPoint;

/**
 * Class describing the data structure of a challenge
 */
public class Challenge {
	
	/** id of a challenge **/
	private int id;
	/** name of a challenge **/
	private String name;
	/** boolean indicating the state of a challenge (active or not) **/
	private boolean active;
	
	/** list of challenge participants **/
	private ArrayList<Participant> participants;
	/** the top left geo point of the challenge area **/
	private GeoPoint locationLeftTop;
	/** the right bottom geo point of the challenge area **/
	private GeoPoint locationRightBottom;
	/** list of all ship positions (row, col) **/
	private ArrayList<ShipPosition> shipPositions;

}
