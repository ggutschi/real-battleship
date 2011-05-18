package at.ac.uniklu.mobile;

import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import at.ac.uniklu.mobile.util.Constants;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

public class ChallengeStartActivity extends MapActivity {
	
	private MapView mapView;
	private MapController mapController;
	private GeoPoint currentLocation = new GeoPoint ((int)(Constants.DEFAULT_LATITUDE * 1E6), (int)(Constants.DEFAULT_LONGITUDE * 1E6));

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.start_challenge);
    	
    	mapView = (MapView) findViewById(R.id.challenge_map);
        mapView.setBuiltInZoomControls(true);
        mapView.setSatellite(true);

    	List<Overlay> overlays = mapView.getOverlays();
    	overlays.clear();
    	overlays.add(new CurrentPositionOverlay());

    	mapView.invalidate();
        
        mapController = mapView.getController();
        mapController.setZoom(10);
        mapController.animateTo(currentLocation);
    	
    	setupLocationManager();
    }
    
    public void setupLocationManager() {

    	LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
    	
    	LocationListener locationListener = new LocationListener() {

			@Override
			public void onLocationChanged(Location arg0) {

		    	Log.d(Constants.LOG_TAG, "location changed to " + arg0);
		    	
		    	currentLocation = new GeoPoint((int) (arg0.getLatitude() * 1E6), (int) (arg0.getLongitude() * 1E6));
		    	
		    	mapController.animateTo(currentLocation);

		    	mapView.invalidate();
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

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
	
	private class CurrentPositionOverlay extends com.google.android.maps.Overlay {

	    @Override
	    public void draw(Canvas canvas, MapView mapView, boolean shadow) {
	        super.draw(canvas, mapView, shadow);

	        if (!shadow) {

	            Point point = new Point();
	            mapView.getProjection().toPixels(currentLocation, point);

	            Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.fadenkreuz);

		    	Log.d(Constants.LOG_TAG, "bmp " + bmp);
		    	Log.d(Constants.LOG_TAG, "point " + point);
	            
	            int x = point.x - bmp.getWidth() / 2;
	            int y = point.y - bmp.getHeight();
	        
	            canvas.drawBitmap(bmp, x, y, null);
	        }
	    }
	}
	
	private class GridOverlay extends com.google.android.maps.Overlay {
		
		
		
		public GridOverlay() {
			super();
			
			
		}

	    @Override
	    public void draw(Canvas canvas, MapView mapView, boolean shadow) {
	        super.draw(canvas, mapView, shadow);

	        if (!shadow) {

	            Point point = new Point();
	            mapView.getProjection().toPixels(currentLocation, point);

	            Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.fadenkreuz);

		    	Log.d(Constants.LOG_TAG, "bmp " + bmp);
		    	Log.d(Constants.LOG_TAG, "point " + point);
	            
	            int x = point.x - bmp.getWidth() / 2;
	            int y = point.y - bmp.getHeight();
	        
	            canvas.drawBitmap(bmp, x, y, null);
	        }
	    }
	}
}
