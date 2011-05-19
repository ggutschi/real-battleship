package at.ac.uniklu.mobile;
	
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import at.ac.uniklu.mobile.db.Challenge;
import at.ac.uniklu.mobile.util.Constants;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;

public class HomeActivity extends MapActivity {
	
	
	private int current_challenge_id;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        
        Log.d(Constants.LOG_TAG, "initialize mapview");
        configureMapView(null);
        initializeChallengeInfos();
        
        // set up button listener for menu selection
        final Button button = (Button) findViewById(R.id.button_change_challenge);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	Log.d(Constants.LOG_TAG, "change challenge button clicked");
            	startActivityForResult(new Intent(HomeActivity.this, ChallengeListActivity.class), 
            			Constants.CMD_CODE_CHANGE_CHALLENGE);
            	
                //startActivity(new Intent(HomeActivity.this, ChallengeListActivity.class));
            }
        });
        
        // set up button listener for menu selection
        final Button button_start = (Button) findViewById(R.id.button_start_challenge);
        button_start.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	Log.d(Constants.LOG_TAG, "start challenge button clicked");
                startActivity(new Intent(HomeActivity.this, ChallengeStartActivity.class).putExtra(Challenge.FIELD_ID, current_challenge_id));
            }
        });

        
    }
    
    
    /* (non-Javadoc)
     * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        Log.d(Constants.LOG_TAG, "on activity result, request code: " + requestCode + " , resultCode: " + resultCode);
        
        if (requestCode == Constants.CMD_CODE_CHANGE_CHALLENGE)
        {
        	if (resultCode == RESULT_OK) {
                // new challenge selected
        		// error occured
                Toast.makeText(HomeActivity.this, R.string.challenge_changed, Toast.LENGTH_SHORT).show();
            	ChallengeListModel listModel = ChallengeListModel.getInstance(this);
            	current_challenge_id = data.getExtras().getInt(Challenge.FIELD_ID);
            	Challenge challenge = listModel.getChallengeById(current_challenge_id);
            	Log.d(Constants.LOG_TAG, "new challenge selected with id: " + challenge.getId()
            			+ " name: " + challenge.getName() 
            			+ " location: " + challenge.getLocation()
            			+ " lat: " + challenge.getLocationLeftTop().getLatitudeE6()
            			+ " lon: " + challenge.getLocationLeftTop().getLongitudeE6());
            	configureMapView(challenge); 
            	initializeChallengeInfos(challenge);
        	}
        	else if (resultCode == Constants.RETURN_CODE_CHANGE_CHALLENGE_ERROR) {
        		// error occured
                Toast.makeText(HomeActivity.this, R.string.challenge_not_changed, Toast.LENGTH_SHORT).show();
        	}
        }
    }
    
    /**
     * configures the google map view by setting default values
     */
    private void configureMapView(Challenge c) {
    	GeoPoint geoPoint = null;
    	MapView mapView = (MapView) findViewById(R.id.challenge_map);
        mapView.setBuiltInZoomControls(true);
        MapController mapController = mapView.getController();
        mapController.setZoom(15);
        if (c != null)
        	geoPoint = c.getLocationLeftTop();
        else
        	geoPoint = new GeoPoint ((int)(Constants.DEFAULT_LATITUDE * 1E6), ((int)(Constants.DEFAULT_LONGITUDE * 1E6)));
        Log.d(Constants.LOG_TAG, "set map center to geo point with lat: " + geoPoint.getLatitudeE6() 
        		+ " lon: " + geoPoint.getLongitudeE6());
        
        mapController.animateTo(geoPoint);
    }
    
    /**
     * sets the challenge information in the UI
     * @param c
     */
    private void initializeChallengeInfos(Challenge... c) {
    	
    	TextView no_challenge = (TextView)findViewById(R.id.no_challenge_selected);
    	
    	if (c!= null && c.length > 0) {
    		// display challenge info
    		TextView challengeName = (TextView)findViewById(R.id.challenge_name);
    		challengeName.setText(c[0].getName());
    		TextView challengeLocation = (TextView)findViewById(R.id.challenge_location);
    		challengeLocation.setText(c[0].getLocation());
    		no_challenge.setVisibility(View.GONE);
    		findViewById(R.id.no_challenge_selected).setVisibility(View.GONE);
    		findViewById(R.id.button_start_challenge).setEnabled(true);
    		findViewById(R.id.button_start_challenge).setVisibility(View.VISIBLE);
    		findViewById(R.id.linearLayoutChallengeInfo).setVisibility(View.VISIBLE);
    	}
    	else {
    		// dont display challenge info and disable start challenge button when there is no info available
    		findViewById(R.id.linearLayoutChallengeInfo).setVisibility(View.GONE);
    		findViewById(R.id.button_start_challenge).setEnabled(false);
    		no_challenge.setVisibility(View.VISIBLE);
    		
    	}
    }
    
    /**
     * override required method
     */
    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }
    
    
}