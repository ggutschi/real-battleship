/**
 * 
 */
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
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.BaseAdapter;
import at.ac.uniklu.mobile.db.Challenge;
import at.ac.uniklu.mobile.util.Constants;

import com.google.android.maps.GeoPoint;

/**
 * List model holding all challenges
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
        //loadChallenges();
    }
    
    /**
     * Loads challenge with given id from webserver.
     * @param id id of challenge to load
     */
    public void loadChallenge(int id) {
    	
    	Log.d(Constants.LOG_TAG, "load challenge with id " + id);
    	
    	HttpClient httpClient = new DefaultHttpClient();  
    	HttpPost httpPost = new HttpPost(Constants.URL_WEBSERVICE_GETCHALLENGE);  
    	HttpResponse response;  
    	
    	try {  
        	
       	 	List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("challenge_id", Integer.toString(id)));
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            
    		response = httpClient.execute(httpPost);  
    		
    		if (response.getStatusLine().getStatusCode() == Constants.WEBSERVICE_STATUSCODE_OK) {
    		
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
            		
            		JSONObject challenge = new JSONObject(new String(result));
            		
            		challengeList.remove(getChallengeById(id));
            		
            		challengeList.add(new Challenge(challenge));
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
		e.printStackTrace();  
		Log.e(Constants.LOG_TAG, "", e);
		} catch (IOException e) {    
		e.printStackTrace();  
		Log.e(Constants.LOG_TAG, "", e);
		}  catch (Exception e){  
		e.printStackTrace();  
		Log.e(Constants.LOG_TAG, "", e);
		}finally{  
			httpPost.abort();  
		}
    }
    
    /**
     * Loads all challenges from webserver
     */
    public void loadChallenges() {
    	
    	Log.d(Constants.LOG_TAG, "load challenges");
    	challengeList.clear();
    	
    	HttpClient httpClient = new DefaultHttpClient();  
    	HttpGet httpGet = new HttpGet(Constants.URL_WEBSERVICE_GETCHALLENGES);  
    	HttpResponse response;  
    	
    	try {  
    		response = httpClient.execute(httpGet);  
    		
    		if (response.getStatusLine().getStatusCode() == Constants.WEBSERVICE_STATUSCODE_OK) {
    		
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
		e.printStackTrace();  
		Log.e(Constants.LOG_TAG, "", e);
		} catch (IOException e) {    
		e.printStackTrace();  
		Log.e(Constants.LOG_TAG, "", e);
		}  catch (Exception e){  
		e.printStackTrace();  
		Log.e(Constants.LOG_TAG, "", e);
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
     * @return the list of challenges
     */
    public ArrayList<Challenge> getChallenges() {
    	this.loadChallenges();
    	
        return challengeList;
    }
    
    /**
     * get challenge by the unique challenge id
     * @param id challenge id
     * @return challenge with the given id
     */
    public Challenge getChallengeById(int id) {
    	for (Challenge c: challengeList) {
    		if (c.getId() == id)
    			return c;
    	}
    	return null;
    }
    
    /**
     * adds an new participant to the challenge by sending an HTTP POST request to the challenge server
     * @param android_id the unique android id of the new participants´ smartphone
     * @param ip_address the current ip address of the new participants´ smartphone
     * @param challenge_id the id of the challenge a new participant wants to join with
     * @param nickname the nickname of the player
     */
    public boolean addParticipant(String android_id, String ip_address, int challenge_id, String nickname) {
    	boolean bTransactionPerformed = false;
    	HttpClient httpClient = new DefaultHttpClient(); 
    	HttpResponse response; 
    	HttpPost postMethod = new HttpPost(Constants.URL_WEBSERVICE_ADDPARTICIPANTS);  
    	  
    	try {  
	    	
	    	 List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
	         nameValuePairs.add(new BasicNameValuePair("android_id", android_id));
	         nameValuePairs.add(new BasicNameValuePair("inet_address", ip_address));
	         nameValuePairs.add(new BasicNameValuePair("challenge_id", Integer.toString(challenge_id)));
	         nameValuePairs.add(new BasicNameValuePair("nickname", nickname));
	         postMethod.setEntity(new UrlEncodedFormEntity(nameValuePairs));

	         //Log.d(Constants.LOG_TAG, "call webservice to add participant with params: " + params.toString());
	    	
	    	response = httpClient.execute(postMethod);
	    	
	    	if (response.getStatusLine().getStatusCode() == Constants.WEBSERVICE_STATUSCODE_OK) {
	    		Log.d(Constants.LOG_TAG, "webservice response code ok");
	    		HttpEntity entity = response.getEntity();    			
    			if (entity != null)
    			{
    				InputStream instream = entity.getContent();  
            		BufferedReader reader = new BufferedReader(new InputStreamReader(instream));
            		String line = reader.readLine();
            		
            		if (line.equals(Constants.WEBSERVICE_TRANSACTION_OK)) {
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
