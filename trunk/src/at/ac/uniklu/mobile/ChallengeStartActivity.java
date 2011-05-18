package at.ac.uniklu.mobile;

import android.app.Activity;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import at.ac.uniklu.mobile.util.Constants;

public class ChallengeStartActivity extends Activity {

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	setupLocationManager();
    }
    
    public void setupLocationManager() {

    	LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
    	
    	LocationListener locationListener = new LocationListener() {

			@Override
			public void onLocationChanged(Location arg0) {

		    	Log.d(Constants.LOG_TAG, "location changed to " + arg0);
				
			}

			@Override
			public void onProviderDisabled(String provider) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onProviderEnabled(String provider) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onStatusChanged(String provider, int status,
					Bundle extras) {
				// TODO Auto-generated method stub
				
			}
    		
    	};
    	
    	locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
    }
}
