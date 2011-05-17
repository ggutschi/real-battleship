/**
 * Class for providing location information (GeoPoint)
 */
package at.ac.uniklu.mobile.util;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

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
	 * finds out the location name to a given geo point. 
	 * this method is using the process of geocoding which is build-in to google maps in android.
	 * @param context the current application context
	 * @param geoPoint the GeoPoint object to find the location name
	 * @return
	 */
	public static String getLocationName(Context context, GeoPoint geoPoint) {
		String locationName = new String();
		Geocoder geoCoder = new Geocoder(context, Locale.getDefault());
		try {
			List<Address> addresses = geoCoder.getFromLocation(
	        		geoPoint.getLatitudeE6()  / 1E6, 
	        		geoPoint.getLongitudeE6() / 1E6, 1);	        
	        
	        if (addresses.size() > 0) 
	        {
	            for (int i=0; i<addresses.get(0).getMaxAddressLineIndex() && i < 3;i++)
	            	locationName += addresses.get(0).getAddressLine(i) + "";
	        }
		}
		catch(IOException ioex) {
			Log.e(Constants.LOG_TAG, "could not retrieve location name from geo point: " + ioex.getMessage());
		}		
		return locationName;                    
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
