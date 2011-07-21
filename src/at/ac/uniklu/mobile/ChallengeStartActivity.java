package at.ac.uniklu.mobile;

import java.io.IOException;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewParent;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import at.ac.uniklu.mobile.ChallengeListActivity.MyTask;
import at.ac.uniklu.mobile.db.Challenge;
import at.ac.uniklu.mobile.db.Participant;
import at.ac.uniklu.mobile.message.MessageLog;
import at.ac.uniklu.mobile.message.ObservableMessage;
import at.ac.uniklu.mobile.peer.PeerCommunication;
import at.ac.uniklu.mobile.peer.PeerManager;
import at.ac.uniklu.mobile.util.Constants;
import at.ac.uniklu.mobile.util.GridOverlay;
import at.ac.uniklu.mobile.util.PositionOverlay;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

public class ChallengeStartActivity extends MapActivity implements Observer {
	
	private MapView 		mapView;
	private MapController 	mapController;
	//private GeoPoint 		currentLocation 	= new GeoPoint ((int)(Constants.DEFAULT_LATITUDE * 1E6), (int)(Constants.DEFAULT_LONGITUDE * 1E6));
	private GeoPoint		currentLocation;
	private Challenge 		currentChallenge;
	/** current game score of player **/
	private int				score;
	Handler 				handler; 
	private ProgressDialog 	progressDialog;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {

    	super.onCreate(savedInstanceState);
    	
    	
    	final boolean customTitleSupported = requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);

    	setContentView(R.layout.start_challenge);


        if ( customTitleSupported ) {
            getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar);
        }
        
        
    	Log.d(Constants.LOG_TAG, "OnCreate called.");
    	
    
        
        ProgressDialog progress = new ProgressDialog(this);
        progress.setMessage("Connecting to peers");
        new MyTask(progress).execute();
        
        

        
        // set up button listener for uncover button
        final Button button_start = (Button) findViewById(R.id.button_uncover);
        button_start.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	Log.d(Constants.LOG_TAG, "uncover cell button clicked");
            	
            	int x = getCurrentCellX();
            	int y = getCurrentCellY();
            	
            	if (x != -1 && y != -1)
            		uncoverCell(x, y);
            }
        });
        
        handler = new Handler() {
        	public void handleMessage(Message msg) {

        		if (msg.arg1 == 1) {
					Toast.makeText(ChallengeStartActivity.this, "The game is over!", Toast.LENGTH_LONG).show();
	
	                Intent myIntent = new Intent(ChallengeStartActivity.this, ScoreActivity.class).putExtra(Challenge.FIELD_ID, currentChallenge.getId());
	                startActivityForResult(myIntent, 0);
        		}
        		
        		addOverlays();
        	}
        };        
    }
    
    
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
      super.onConfigurationChanged(newConfig);
      

  		Log.d(Constants.LOG_TAG, "Configuration changed.");
    }
    
    
    @Override
	protected void onResume() {
		final Button bUncover = (Button) findViewById(R.id.button_uncover);
		
		super.onResume();
		
		if (currentLocation == null) {
	    	Log.d(Constants.LOG_TAG, "Location is null.");
	    	bUncover.setEnabled(false);
        	Toast.makeText(this.getApplicationContext(), R.string.no_gps, Toast.LENGTH_LONG).show();
		} else
			Log.d(Constants.LOG_TAG, "Location is not null.");
	}



	@Override
	protected void onDestroy() {
    	Log.d(Constants.LOG_TAG, "super.onDestroy()...");
		super.onDestroy();

		
		Vector<PeerCommunication> peerCommunications = PeerManager.myPeer.getPeerCommunication();
		
		for (PeerCommunication peerCommunication : peerCommunications) {
			try {

				Log.e(Constants.LOG_TAG, "Joining peerThread...");
				peerCommunication.stopp();
				peerCommunication.join(500);
				
				if (peerCommunication.isAlive()) {
					peerCommunication.interrupt();
					peerCommunication = null;
				}
				
				Log.e(Constants.LOG_TAG, "peerThread joined.");
			}
			catch(Exception ex) {
				Log.e(Constants.LOG_TAG, "peer join exception", ex);
			}
		}

    	Log.d(Constants.LOG_TAG, "closeConnections()...");
		PeerManager.closeConnections();

    	Log.d(Constants.LOG_TAG, "myPeer.stopp()...");
		PeerManager.myPeer.stopp();
    	Log.d(Constants.LOG_TAG, "myPeer.stopped");
		
		try {
	    	Log.d(Constants.LOG_TAG, "Thread.join()...");
			PeerManager.peerServerThread.join();
	    	Log.d(Constants.LOG_TAG, "destroyed.");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}



	private void addOverlays() {

    	Log.d(Constants.LOG_TAG, "currentChallenge = " + ChallengeStartActivity.this.currentChallenge + " id = " + ChallengeStartActivity.this.getIntent().getExtras().getInt(Challenge.FIELD_ID));
    	
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
    	
    	if (i * diffX + left > right)
    		return -1;
    	
    	return i;
    }
    
    private int getCurrentCellY() {
    	// TODO: Make calculations for other quarters of earth
    	
    	int bottom  = currentChallenge.getLocationRightBottom().getLatitudeE6();
    	int top     = currentChallenge.getLocationLeftTop().getLatitudeE6();
    	int current = currentLocation.getLatitudeE6();

    	int diffY = (top - bottom) / currentChallenge.getCellsY();
    	
    	int i = 0;
    	
    	for (i = 0; current > (i * diffY + bottom) && (i * diffY + bottom) <= top; i++)
    		;
    	
    	Log.d(Constants.LOG_TAG, "Cell y = " + (currentChallenge.getCellsY() - i));
    	
    	if ((i * diffY + bottom) > top)
    		return -1;
    	
    	return currentChallenge.getCellsY() - i;
    }
    
    private void uncoverCell(int x, int y) {
    	// only send uncover message if shipcell uncovered
    	
		currentChallenge.uncoverCellLocally(x, y, this, PeerManager.myPeer.getAndroidId());
		
		if (currentChallenge.isShipCell(x, y))
			PeerManager.sendUncoverMessage(x, y);
		
    	Log.d(Constants.LOG_TAG, "Cell uncovered");
    }
    
    
    /*
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
    */
    
    public boolean isInGrid(GeoPoint gp) {
    	return gp.getLatitudeE6() <= currentChallenge.getLocationLeftTop().getLatitudeE6()
    	 	&& gp.getLatitudeE6() >= currentChallenge.getLocationRightBottom().getLatitudeE6()
    	 	&& gp.getLongitudeE6() >= currentChallenge.getLocationLeftTop().getLongitudeE6()
    	 	&& gp.getLongitudeE6() <= currentChallenge.getLocationRightBottom().getLongitudeE6();
    }
    
    /**
     * increase current game score after uncovering a ship 
     * @param increment how much should the score be incremented
     */
    public void increaseScore(int increment) {
    	score+=increment;
    	TextView t=(TextView)findViewById(R.id.myTitle); 
        t.setText("Score: " + Integer.toString(score));
    }
    /**
     * decrease current game score after another player 
     * prior hit the ship but score of current player already got incremented  
     * @param decrement how much should the score be decremented
     */
    public void decreaseScore(int decrement) {
    	score -= decrement;
    	TextView t=(TextView)findViewById(R.id.myTitle); 
        t.setText("Score: " + Integer.toString(score));
    }
    
    /**
     * 
     * @return current game score
     */
    public int getScore() {
    	return score;
    }
    
    public void setupLocationManager() {

		final Button bUncover = (Button) findViewById(R.id.button_uncover);

    	LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
    	
    	LocationListener locationListener = new LocationListener() {

			@Override
			public void onLocationChanged(Location arg0) {

		    	Log.d(Constants.LOG_TAG, "location changed to " + arg0);
		    	
		    	currentLocation = new GeoPoint((int) (arg0.getLatitude() * 1E6), (int) (arg0.getLongitude() * 1E6));
		    	
		    	if (isInGrid(currentLocation))
		    		bUncover.setEnabled(true);
		    	else
		    		bUncover.setEnabled(false);
		    	
		    	Log.d(Constants.LOG_TAG, "Location setted.");
		        
		        addOverlays();
		    	
		    	mapController.animateTo(currentLocation);

		    	mapView.invalidate();
			}

			@Override
			public void onProviderDisabled(String provider) {
				if (provider.equals("gps"))
					Toast.makeText(ChallengeStartActivity.this, "Please turn on GPS.", Toast.LENGTH_LONG).show();
				
			}

			@Override
			public void onProviderEnabled(String provider) {
				if (provider.equals("gps"))
					Toast.makeText(ChallengeStartActivity.this, "GPS on. Waiting for location update...", Toast.LENGTH_LONG).show();
			}

			@Override
			public void onStatusChanged(String provider, int status,
					Bundle extras) {
				// TODO Auto-generated method stub
				
			}
    		
    	};
    	
    	locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 3, locationListener);
    }
    


	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
	
	/**
	 * implements the observer part of the observer pattern
	 * used for changing the current game score (increase/decrease score)
	 * @param o the observable object
	 * @param arg the observable message consisting of an message intend and message content 
	 */
	public void update(Observable o, Object arg) {
		Log.d(Constants.LOG_TAG, "update activity by observable object");
		try {
			switch (((ObservableMessage)arg).getMessageIntend()) {
			case SCORE_INCREMENT:
				int increaseScore = (Integer)((ObservableMessage)arg).getMessageContent();
				increaseScore(increaseScore);
				break;
			case SCORE_DECREMENT:
				int decreaseScore = (Integer)((ObservableMessage)arg).getMessageContent();
				decreaseScore(decreaseScore);
				break;
			case DEBUG_MESSAGE:					
				//Toast.makeText(this, ((ObservableMessage)arg).getMessageContent().toString(), Toast.LENGTH_LONG).show();
				break;
			case UPDATE_MAP:
				Message m = new Message();
				
				m.arg1 = 0;

				if (((ObservableMessage)arg).getMessageContent().toString().equals("gameover")) {
					m.arg1 = 1;
				}
    	    	
				Log.d(Constants.LOG_TAG, "UPDATE_MAP");
				handler.sendMessage(m);
	    		break;
			default:
				Log.e(Constants.LOG_TAG, "unkown observable message intend");
		}
		}
		catch(Exception ex) {
			Log.e(Constants.LOG_TAG, "observable message not interpretable", ex);
		}
	}
	

    
    public class MyTask extends AsyncTask<Void, Void, Void> {
    	  public MyTask(ProgressDialog progress) {
    	    progressDialog = progress;
    	  }

    	  public void onPreExecute() {
    		  progressDialog.show();
    	  }

    	  public Void doInBackground(Void... unused) {
    		  ChallengeStartActivity.this.currentChallenge = ChallengeListModel.getInstance(ChallengeStartActivity.this).getChallengeById(ChallengeStartActivity.this.getIntent().getExtras().getInt(Challenge.FIELD_ID));
	    	
    	    	Log.d(Constants.LOG_TAG, "PeerManager.init(...)...");
      	    	PeerManager.init(ChallengeStartActivity.this.currentChallenge, ChallengeStartActivity.this.getBaseContext());
        	    Log.d(Constants.LOG_TAG, "PeerManager.init(...) finished.");

    	    	PeerManager.log.clearLog();
    	    	
        	    ChallengeListModel.getInstance(ChallengeStartActivity.this).loadChallenge(getIntent().getExtras().getInt(Challenge.FIELD_ID));
    	    	ChallengeStartActivity.this.currentChallenge = ChallengeListModel.getInstance(ChallengeStartActivity.this).getChallengeById(ChallengeStartActivity.this.getIntent().getExtras().getInt(Challenge.FIELD_ID));
    	    	
    	    	PeerManager.refreshCurrentChallenge(currentChallenge);
    	    	
    	    	// check for arrived uncover messages    	    	
    	    	PeerManager.log.checkForUncoveredCells();
    	    	Log.d(Constants.LOG_TAG, "before add observer currentChallenge = " + ChallengeStartActivity.this.currentChallenge + " id = " + ChallengeStartActivity.this.getIntent().getExtras().getInt(Challenge.FIELD_ID));
    	    	

      	    	ChallengeStartActivity.this.currentChallenge.addObserver(ChallengeStartActivity.this);
      	    	
    	    	Log.d(Constants.LOG_TAG, "after add observer currentChallenge = " + ChallengeStartActivity.this.currentChallenge + " id = " + ChallengeStartActivity.this.getIntent().getExtras().getInt(Challenge.FIELD_ID));
    	    	
    	    	// register activity as an observer object (e.g. for observing ship uncover actions to adjust current score in activity)
    	    	


    	    	
    	    	return null;
    	  }

    	  public void onPostExecute(Void unused) {
  	    	
  	    	Log.d(Constants.LOG_TAG, "currentChallenge: " + currentChallenge);
  	    	
  	    	mapView = (MapView) findViewById(R.id.challenge_map);
  	        mapView.setBuiltInZoomControls(true);
  	        mapView.setSatellite(true);
  	        
  	        mapController = mapView.getController();
  	        
  	        if (currentLocation != null) {
  		        mapController.setZoom(18);
  		        mapController.animateTo(currentLocation);
  	        }
  	        
  	    	setupLocationManager();
  	    	
	    	Log.d(Constants.LOG_TAG, "before add overlays Challenge: " + ChallengeStartActivity.this.currentChallenge);
  	    	
  	        addOverlays();
	    	Log.d(Constants.LOG_TAG, "after add overlays Challenge: " + ChallengeStartActivity.this.currentChallenge);

  	        // read existing score of user
  	        for (Participant p : currentChallenge.getParticipants())
  	        	if (p.getAndroid_id().equals(PeerManager.myPeer.getAndroidId())) {
  	        		if (!p.getScore().equals("null"))
  	        			ChallengeStartActivity.this.increaseScore(Integer.parseInt(p.getScore()));
  	        	}
  	        		
  	        	
    		  progressDialog.dismiss();
    	  }
    	}
}
