package at.ac.uniklu.mobile;

import java.util.List;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import at.ac.uniklu.mobile.db.Challenge;
import at.ac.uniklu.mobile.util.Constants;
import at.ac.uniklu.mobile.util.GridOverlay;
import at.ac.uniklu.mobile.util.PositionOverlay;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

public class ChallengeStartActivity extends MapActivity {
	
	public final static float GRID_LINE_WIDTH = 5;
	
	public final static int GRID_COLOR = Color.RED;
	
	private MapView 		mapView;
	private MapController 	mapController;
	private GeoPoint 		currentLocation 	= new GeoPoint ((int)(Constants.DEFAULT_LATITUDE * 1E6), (int)(Constants.DEFAULT_LONGITUDE * 1E6));
	private Challenge 		currentChallenge;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.start_challenge);
    	
    	this.currentChallenge = ChallengeListModel.getInstance(this).getChallengeById(getIntent().getExtras().getInt(Challenge.FIELD_ID));
    	
    	Log.d(Constants.LOG_TAG, "Challenge: " + currentChallenge);
    	
    	mapView = (MapView) findViewById(R.id.challenge_map);
        mapView.setBuiltInZoomControls(true);
        mapView.setSatellite(true);

    	List<Overlay> overlays = mapView.getOverlays();
    	overlays.clear();
    	overlays.add(new PositionOverlay(currentLocation, this.getApplicationContext()));
    	overlays.add(new GridOverlay(mapView, currentChallenge));

    	mapView.invalidate();
        
        mapController = mapView.getController();
        mapController.setZoom(18);
        mapController.animateTo(currentLocation);
        
    	setupLocationManager();

        
        // set up button listener for uncover button
        final Button button_start = (Button) findViewById(R.id.button_uncover);
        button_start.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	Log.d(Constants.LOG_TAG, "uncover cell button clicked");
            	
            	int diffX = (currentChallenge.getLocationRightBottom().getLongitudeE6() - currentChallenge.getLocationLeftTop().getLongitudeE6()) / currentChallenge.getCellsX();
            	int diffY = (currentChallenge.getLocationRightBottom().getLatitudeE6()  - currentChallenge.getLocationLeftTop().getLatitudeE6())  / currentChallenge.getCellsY();
            	
            	int i = 0;
            	
            	if (diffX > 0)
            		for (i = 0; (currentLocation.getLongitudeE6() + diffX) < (i * diffX + currentChallenge.getLocationLeftTop().getLongitudeE6()) && (i * diffX + currentChallenge.getLocationLeftTop().getLongitudeE6()) <= currentChallenge.getLocationRightBottom().getLongitudeE6(); i++)
            			;
            	else
            		for (i = 0; (currentLocation.getLongitudeE6() + diffX) > (i * diffX + currentChallenge.getLocationLeftTop().getLongitudeE6()) && (i * diffX + currentChallenge.getLocationLeftTop().getLongitudeE6()) >= currentChallenge.getLocationRightBottom().getLongitudeE6(); i++)
                		;
            	
            	Log.d(Constants.LOG_TAG, "Cell x = " + i);

            	int j = 0;
            	
            	if (diffY > 0)
            		for (j = 0; (currentLocation.getLatitudeE6() + diffY) < (j * diffY + currentChallenge.getLocationLeftTop().getLatitudeE6()) && (j * diffY + currentChallenge.getLocationLeftTop().getLatitudeE6()) <= currentChallenge.getLocationRightBottom().getLatitudeE6(); i++)
            			;
            	else
            		for (j = 0; (currentLocation.getLatitudeE6() + diffY) > (j * diffY + currentChallenge.getLocationLeftTop().getLatitudeE6()) && (j * diffY + currentChallenge.getLocationLeftTop().getLatitudeE6()) >= currentChallenge.getLocationRightBottom().getLatitudeE6(); i++)
                			;
            	
            	Log.d(Constants.LOG_TAG, "Cell y = " + j);
            	
            	uncoverCell(i, j);
            }
        });
    }
    
    private void uncoverCell(int x, int y) {
    	
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
}
