/**
 * 
 */
package at.ac.uniklu.mobile;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.util.Log;
import android.widget.BaseAdapter;
import at.ac.uniklu.mobile.db.Challenge;
import at.ac.uniklu.mobile.util.Constants;

import com.google.android.maps.GeoPoint;

/**
 *
 */
public class ChallengeListModel {

	/** list of all available challenges **/
	private ArrayList<Challenge> challengeList;
	
	 /**
     * The application context within which we operate.
     */
    private Context context;
    
    /**
     * The singleton challenge list model.
     */
    private static ChallengeListModel instance;
	
    /**
     * The list adapter that cares when we make changes to this model.
     */
    private BaseAdapter dependentAdapter;
    
	 /**
     * Constructor
     * @param context
     */
    private ChallengeListModel (Context pContext) {
        context = pContext;
        challengeList = new ArrayList<Challenge>();
        //initializeDefaultChallenges();
        loadChallenges();
    }
    
    private void loadChallenges() {
    	
    	HttpClient httpClient = new DefaultHttpClient();  
    	HttpGet httpGet = new HttpGet(Constants.URL_WEBSERVICE_GETCHALLENGES);  
    	HttpResponse response;  
    	
    	try {  
    		response = httpClient.execute(httpGet);  
    		
    		if (response.getStatusLine().getStatusCode() == Constants.URL_STATUSCODE_OK) {
    		
    			HttpEntity entity = response.getEntity();
    			
    			if (entity != null)
    			{
    				InputStream instream = entity.getContent();  
            		BufferedReader reader = new BufferedReader(new InputStreamReader(instream));  
            		StringBuilder sb = new StringBuilder();  
            		  
            		String line = null;  
            		while ((line = reader.readLine()) != null)  
            		sb.append(line + "n");
            		
            		String result=sb.toString();
            		
            		Log.d(Constants.LOG_TAG, "json response: " + result);
            		instream.close();
            		
            		JSONArray challenges = new JSONArray(new String(result));
            		for (int i = 0; i < challenges.length(); i++) {
            			challengeList.add(new Challenge(challenges.getJSONObject(i)));
            	    } // for
    			} // if
    			else
    				Log.e(Constants.LOG_TAG, "no response content from webservice received");
        		
        		        		
    		}
    		else {
    			// server call was not successfull
    			Log.e(Constants.LOG_TAG, "could not load challenges from server, wrong response code: " 
    					+ response.getStatusLine().getStatusCode());
    		}
    		
    	}
		catch (ClientProtocolException e) {  
		// TODO Auto-generated catch block  
		e.printStackTrace();  
		} catch (IOException e) {  
		// TODO Auto-generated catch block  
		e.printStackTrace();  
		}  catch (Exception e){  
		e.printStackTrace();  
		}finally{  
		httpGet.abort();  
		}  
    		  
    		  
    	
    }
    
    /**
     * initializes the list of challenges with default challenges
     */
    private void initializeDefaultChallenges() {
    	challengeList.add(new Challenge(1, "Klagenfurt City Challenge", true, new GeoPoint(43, 12), "Klagenfurt"));
    	challengeList.add(new Challenge(2, "Klagenfurt Uni Challenge", true, new GeoPoint(44, 13), "Klagenfurt"));
    	challengeList.add(new Challenge(2, "Villach McDonalds Challenge", true, new GeoPoint(45, 14), "Villach"));    	
    }    
    
    /**
     * @return the mAnimals
     */
    public ArrayList<Challenge> getChallenges() {
        return challengeList;
    }

    /**
     * Get the singleton instance of this class.
     * @return The (singleton) ChallengeListModel
     */
    public static ChallengeListModel getInstance(Context context) {
        if (instance == null) {
            // If the singleton doesn't exist, create it
            instance = new ChallengeListModel(context);
        }
        return instance;
    }
    
    /**
     * Register an adapter to listen for changes to this model.
     * 
     * @param adapter The adapter
     */
    public void registerDependentAdapter(BaseAdapter adapter) {
        dependentAdapter = adapter;
    }
}
