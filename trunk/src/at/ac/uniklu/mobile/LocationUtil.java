/**
 * Class for providing location information (GeoPoint)
 */
package at.ac.uniklu.mobile;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.google.android.maps.GeoPoint;

/**
 * Class provides services to retrieve the current location via GPS 
 *
 */
public class LocationUtil extends Activity implements LocationListener {
	
	/** instance of location manager **/
	private static LocationManager locationManager = null;
	
	/** the name of the provider **/
	private static String provider;
	
	/** the current GeoPoint describing latitude and longitude **/
	private static GeoPoint geoPoint = null;
	
	
	/**
	 * request location updates when constructing an object of this class
	 * @param context
	 */
	public LocationUtil(Context context) {
		
		Log.v(Constants.LOG_TAG, "create location util object");
		
		// get the location manager
		locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		// register the listener to receive location updates
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
	}
	
	/**
	 * returns the current GPS location
	 * @return a GeoPoint object containing current latitude and longitude coordinates
	 */
	public static GeoPoint getCurrentLocation() {		
		
		if (geoPoint == null)
		{	
			Log.v(Constants.LOG_TAG, "no geoPoint set -> get LastKnownLocation");
			Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			if (location != null)
				geoPoint = new GeoPoint((int)(location.getLatitude() * 1E6), (int)(location.getLongitude() * 1E6));
			else
				Log.v(Constants.LOG_TAG, "LastKnownLocation unknown");
		}	
		return geoPoint;
	}
	
	/**
	 *  Request updates at startup 
	 **/
	@Override
	protected void onResume() {
		super.onResume();		
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
	}

	/**
	 *  Remove the location updates when Activity is paused 
	 **/
	@Override
	protected void onPause() {
		super.onPause();
		locationManager.removeUpdates(this);
	}

	@Override
	public void onLocationChanged(Location location) {
		Log.v(Constants.LOG_TAG, "onLocationChanged() lat: " + location.getLatitude() + " lon: " + location.getLongitude());
		geoPoint = new GeoPoint((int)(location.getLatitude() * 1E6), (int)(location.getLongitude() * 1E6));
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {

	}

	@Override
	public void onProviderEnabled(String provider) {
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		/**
		 * Toast.makeText(this, "Disenabled provider " + provider,
				Toast.LENGTH_SHORT).show();
		 **/
	}
}
