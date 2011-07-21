package at.ac.uniklu.mobile;
	
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;

import at.ac.uniklu.mobile.db.Challenge;
import at.ac.uniklu.mobile.db.Participant;
import at.ac.uniklu.mobile.db.Ship;
import at.ac.uniklu.mobile.db.ShipPosition;
import at.ac.uniklu.mobile.util.Constants;
import at.ac.uniklu.mobile.util.GridOverlay;
import at.ac.uniklu.mobile.util.HttpUtil;
import at.ac.uniklu.mobile.util.PositionOverlay;

import com.flurry.android.FlurryAgent;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

public class HomeActivity extends MapActivity {
	
	private MapView mapView;
	private MapController mapController;
	
	private Challenge currentChallenge;
	private GeoPoint currentLocation;
	private String username;
	
	LocationManager locManager;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	View titleView = getWindow().findViewById(android.R.id.title);
    	if (titleView != null) {
    	  ViewParent parent = titleView.getParent();
    	  if (parent != null && (parent instanceof View)) {
    	    View parentView = (View)parent;
    	    parentView.setBackgroundColor(Color.rgb(0xff, 0x00, 0x00));
    	  }
    	}
    	
    	
    	
        super.onCreate(savedInstanceState);
        
        locManager = (LocationManager) getSystemService(LOCATION_SERVICE);

    	Log.d(Constants.LOG_TAG, "On create");
    	
        setContentView(R.layout.home);
        
        username = getIntent().getExtras().getString(Participant.FIELD_NICKNAME);
        Log.d(Constants.LOG_TAG, "got username " + username);
        
        TextView tv = (TextView) findViewById(R.id.no_challenge_selected);
        tv.setText(getResources().getString(R.string.no_challenge_selected, username));
        
        Log.d(Constants.LOG_TAG, "initialize mapview");
        configureMapView();
        initializeChallengeInfos();
        
        // set up button listener for menu selection
        final Button button = (Button) findViewById(R.id.button_change_challenge);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	Log.d(Constants.LOG_TAG, "change challenge button clicked");
            	
            	if (isOnline())
            		startActivityForResult(new Intent(HomeActivity.this, ChallengeListActivity.class).putExtra(Participant.FIELD_NICKNAME, username), 
            				Constants.CMD_CODE_CHANGE_CHALLENGE);
            	else {
            		new AlertDialog.Builder(HomeActivity.this)
            		.setTitle("No internet connection")
            		.setMessage("Please connect to the internet to be able to load challenges.")
            		.setNeutralButton("Ok",
            				new DialogInterface.OnClickListener() {
            					public void onClick(DialogInterface dialog, int which) { }
            		}).show();
            	}
            	
                //startActivity(new Intent(HomeActivity.this, ChallengeListActivity.class));
            }
        });
        
        // set up button listener for menu selection
        final Button button_start = (Button) findViewById(R.id.button_start_challenge);
        button_start.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	Log.d(Constants.LOG_TAG, "start challenge button clicked");

            	if (locManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
                    startActivity(new Intent(HomeActivity.this, ChallengeStartActivity.class).putExtra(Challenge.FIELD_ID, currentChallenge.getId()));
            	else {
            		new AlertDialog.Builder(HomeActivity.this)
            		.setTitle("No GPS")
            		.setMessage("Please turn on your GPS to play.")
            		.setNeutralButton("Ok",
            				new DialogInterface.OnClickListener() {
            					public void onClick(DialogInterface dialog, int which) { }
            		}).show();
            	}
            }
        });
        
        // set up button listener for menu selection
        final Button button_score = (Button) findViewById(R.id.button_view_score);
        button_score.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	Log.d(Constants.LOG_TAG, "start challenge button clicked");
                startActivity(new Intent(HomeActivity.this, ScoreActivity.class).putExtra(Challenge.FIELD_ID, currentChallenge.getId()));
            }
        });
        
        
        // set up button listener for menu selection
        final Button button_reset = (Button) findViewById(R.id.button_reset_challenge);
        if (username!=null && username.equalsIgnoreCase("chris")) {
        	Log.d(Constants.LOG_TAG, "enable reset button");
        	button_reset.setVisibility(0);
        	button_reset.setEnabled(true);
        	
        }
        	        	
        else {
        	Log.d(Constants.LOG_TAG, "disable reset button");
        	button_reset.setVisibility(4);
        	button_reset.setEnabled(false);
        	
        }
        
        button_reset.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	Log.d(Constants.LOG_TAG, "reset challenge button clicked");
            	String result = null;
            	try {
            		result = HttpUtil.doGetRequest(Constants.URL_WEBSERVICE_CLEARCHALLENGE);
            		Log.d(Constants.LOG_TAG, "response: " + result); 
            	}
            	catch(Exception ex) {
            		Log.e(Constants.LOG_TAG, ex.getMessage(), ex);
            	}
            	
            	if (result.equalsIgnoreCase(Constants.WEBSERVICE_TRANSACTION_OK)) {
            		Log.d(Constants.LOG_TAG, "reset challenge successful");
            		addOverlays();
            		
            		currentChallenge.resetChallengeLocally();
            		Toast.makeText(HomeActivity.this, R.string.challenge_reset, Toast.LENGTH_SHORT).show();
            	}
            		
            }
        });
    }
    
    
    public boolean isOnline() {
    	 ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    	 NetworkInfo ni = cm.getActiveNetworkInfo();
    	 
    	 if (ni == null)
    		 return false;
    	 
    	 return ni.isConnectedOrConnecting();
    }
    
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
      super.onConfigurationChanged(newConfig);
    }
    


	@Override
	protected void onStart() {
		super.onStart();
		
		FlurryAgent.onStartSession(this, "RWETYFSLDYJSHJQG8S3B");
	}

	@Override
	protected void onStop() {
		super.onStop();
		
		FlurryAgent.onEndSession(this);
	}
    
    
    @Override
	protected void onResume() {

    	Log.d(Constants.LOG_TAG, "On resume");
    	
		super.onResume();
		
		Button button_score = (Button) findViewById(R.id.button_view_score);
		
		if (currentChallenge != null)
		{	
			button_score.setEnabled(true);
			zoomOut();			
		}
		else
			button_score.setEnabled(false);
			
	}
    
    private void zoomOut() {
    	GeoPoint lt = currentChallenge.getLocationLeftTop();
    	GeoPoint rb = currentChallenge.getLocationRightBottom();
    	
    	int latDiff = rb.getLatitudeE6()  - lt.getLatitudeE6();
    	int lonDiff = rb.getLongitudeE6() - lt.getLongitudeE6();
        
    	GeoPoint gp = new GeoPoint(lt.getLatitudeE6() + latDiff / 2, lt.getLongitudeE6() + lonDiff / 2);
        
    	mapController.animateTo(gp);
    	
        mapController.zoomToSpan(latDiff, lonDiff);
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
            	int current_challenge_id = data.getExtras().getInt(Challenge.FIELD_ID);
            	currentChallenge = listModel.getChallengeById(current_challenge_id);
            	Log.d(Constants.LOG_TAG, "new challenge selected with id: " + currentChallenge.getId()
            			+ " name: " + currentChallenge.getName() 
            			+ " location: " + currentChallenge.getLocation()
            			+ " lat: " + currentChallenge.getLocationLeftTop().getLatitudeE6()
            			+ " lon: " + currentChallenge.getLocationLeftTop().getLongitudeE6());
            	configureMapView(); 
            	initializeChallengeInfos(currentChallenge);
        	}
        	else if (resultCode == Constants.RETURN_CODE_CHANGE_CHALLENGE_ERROR) {
        		// error occured
                Toast.makeText(HomeActivity.this, R.string.challenge_not_changed, Toast.LENGTH_SHORT).show();
        	}
        }
    }

	private void addOverlays() {
    	List<Overlay> overlays = mapView.getOverlays();
    	overlays.clear();
    	overlays.add(new GridOverlay(mapView, currentChallenge));
    	//overlays.add(new PositionOverlay(currentLocation, this.getApplicationContext()));

    	mapView.invalidate();
    	

    	Log.d(Constants.LOG_TAG, "Overlays added.");
    }
    
    /**
     * configures the google map view by setting default values
     */
    private void configureMapView() {
    	mapView = (MapView) findViewById(R.id.challenge_map);
        mapView.setBuiltInZoomControls(true);
        mapController = mapView.getController();
        
        mapView.setBuiltInZoomControls(true);
        mapView.setSatellite(true);

        if (currentChallenge != null) {
	    	addOverlays();
	    	
	    	zoomOut();
    	} else {
        	currentLocation = new GeoPoint ((int)(Constants.DEFAULT_LATITUDE * 1E6), ((int)(Constants.DEFAULT_LONGITUDE * 1E6)));
        	
        	mapController.setZoom(3);
    	}
        
        Log.d(Constants.LOG_TAG, "set map center to geo point with lat: " + currentLocation.getLatitudeE6() 
        		+ " lon: " + currentLocation.getLongitudeE6());
        
        mapController.animateTo(currentLocation);
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
    
    /**
     * 
     * @return current username of player
     */
    public String getUsername() { 
    	return username;
    }
    
    
}