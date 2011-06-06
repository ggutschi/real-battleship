package at.ac.uniklu.mobile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;

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
        
        addOverlays();
        
        mapController = mapView.getController();
        mapController.setZoom(18);
        mapController.animateTo(currentLocation);
        
    	setupLocationManager();

        
        // set up button listener for uncover button
        final Button button_start = (Button) findViewById(R.id.button_uncover);
        button_start.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	Log.d(Constants.LOG_TAG, "uncover cell button clicked");
            	
            	int x = getCurrentCellX();
            	int y = getCurrentCellY();
            	
            	uncoverCell(x, y);
            }
        });
    }
    
    
    
    @Override
	protected void onResume() {
		super.onResume();
	}



	private void addOverlays() {
    	List<Overlay> overlays = mapView.getOverlays();
    	overlays.clear();
    	overlays.add(new GridOverlay(mapView, currentChallenge));
    	overlays.add(new PositionOverlay(currentLocation, this.getApplicationContext()));

    	mapView.invalidate();

    	Log.d(Constants.LOG_TAG, "Overlays added.");
    }
    
    private int getCurrentCellX() {
    	// TODO: Make calculations for other quarters of earth
    	
    	
    	int right   = currentChallenge.getLocationRightBottom().getLongitudeE6();
    	int left    = currentChallenge.getLocationLeftTop().getLongitudeE6();
    	int current = currentLocation.getLongitudeE6();

    	int diffX = (right - left) / currentChallenge.getCellsX();
    	
    	int i = 0;
    	
    	for (i = 0; (current - diffX) > (i * diffX + left) && (i * diffX + left) <= right; i++)
    		;
    	
    	Log.d(Constants.LOG_TAG, "Cell x = " + i);
    	
    	return i;
    }
    
    private int getCurrentCellY() {
    	// TODO: Make calculations for other quarters of earth
    	
    	int bottom  = currentChallenge.getLocationRightBottom().getLatitudeE6();
    	int top     = currentChallenge.getLocationLeftTop().getLatitudeE6();
    	int current = currentLocation.getLatitudeE6();

    	int diffY = (top - bottom) / currentChallenge.getCellsY();
    	
    	int i = 0;
    	
    	for (i = currentChallenge.getCellsY() - 1; (current + diffY) < (i * diffY + bottom) && (i * diffY + bottom) >= bottom; i--)
    		;
    	
    	Log.d(Constants.LOG_TAG, "Cell y = " + (currentChallenge.getCellsY() - 1 - i));
    	
    	return currentChallenge.getCellsY() - 1 - i;
    }
    
    private void uncoverCell(int x, int y) {
    	if (sendUncoverRequest(x, y)) {
    		ChallengeListModel.getInstance(this).loadChallenges();
    		
    		addOverlays();
    	}
    }
    
    private boolean sendUncoverRequest(int x, int y) {
    	boolean bTransactionPerformed = false;
    	HttpClient httpClient = new DefaultHttpClient(); 
    	HttpResponse response; 
    	HttpPost postMethod = new HttpPost(Constants.URL_WEBSERVICE_UNCOVERSHIPPOSITION);  
    	  
    	try {  
	    	 List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
	         nameValuePairs.add(new BasicNameValuePair("challenge_id", String.valueOf(currentChallenge.getId())));
	         nameValuePairs.add(new BasicNameValuePair("row", String.valueOf(y)));
	         nameValuePairs.add(new BasicNameValuePair("col", String.valueOf(x)));
	         postMethod.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			Log.d(Constants.LOG_TAG, "challenge id: " + currentChallenge.getId());

		    Log.d(Constants.LOG_TAG, "challenge_id: " + nameValuePairs.get(0).toString());
		    Log.d(Constants.LOG_TAG, "row: " + nameValuePairs.get(1).toString());
		    Log.d(Constants.LOG_TAG, "col: " + nameValuePairs.get(2).toString());
	    	
	    	response = httpClient.execute(postMethod);
	    	
	    	if (response.getStatusLine().getStatusCode() == Constants.WEBSERVICE_STATUSCODE_OK) {
	    		Log.d(Constants.LOG_TAG, "webservice response code ok");
	    		HttpEntity entity = response.getEntity();    			
    			if (entity != null)
    			{
    				InputStream instream = entity.getContent();  
            		BufferedReader reader = new BufferedReader(new InputStreamReader(instream));
            		String line = reader.readLine();
            		
            		if (line.equals(Constants.WEBSERVICE_STATUSCODE_UNCOVERED)) {
            			bTransactionPerformed = true; // add participant successful
            			Log.d(Constants.LOG_TAG, "webservice transaction successful");
            		}
            		else
            			Log.e(Constants.LOG_TAG, "webservice transaction was not successful, response: " + line);
    			}
    			else
    				Log.e(Constants.LOG_TAG, "webservice response entity is empty");
	    	}
	    	else
	    		Log.e(Constants.LOG_TAG, "webservice response code not ok: " + response.getStatusLine().getStatusCode());
	    	
    	} catch (ClientProtocolException e) {    
    		e.printStackTrace();  
    		Log.e(Constants.LOG_TAG, "", e);
    	} catch (IOException e) {
    		e.printStackTrace();  
    		Log.e(Constants.LOG_TAG, "", e);
    	} catch (Exception e) {    
    		e.printStackTrace();  
    		Log.e(Constants.LOG_TAG, "", e);
    	} finally {  
    		postMethod.abort();  
    	}
    	
    	return bTransactionPerformed;
    }
    
    public void setupLocationManager() {

    	LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
    	
    	LocationListener locationListener = new LocationListener() {

			@Override
			public void onLocationChanged(Location arg0) {

		    	Log.d(Constants.LOG_TAG, "location changed to " + arg0);
		    	
		    	currentLocation = new GeoPoint((int) (arg0.getLatitude() * 1E6), (int) (arg0.getLongitude() * 1E6));
		        
		        addOverlays();
		    	
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
