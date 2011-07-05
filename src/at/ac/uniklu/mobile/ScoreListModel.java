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
import java.util.Vector;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;

import android.content.Context;
import android.util.Log;
import android.widget.BaseAdapter;
import at.ac.uniklu.mobile.db.Score;
import at.ac.uniklu.mobile.util.Constants;


public class ScoreListModel {

	/** list of all available challenges **/
	private ArrayList<Score> scoreList;
	
	 /**
     * The application context within which we operate.
     */
    private Context context;
    
    /**
     * The singleton challenge list model.
     */
    private static ScoreListModel instance;
	
    /**
     * The list adapter that cares when we make changes to this model.
     */
    private BaseAdapter dependentAdapter;
    
	 /**
     * Constructor
     * @param context
     */
    private ScoreListModel (Context pContext) {
        context = pContext;
        scoreList = new ArrayList<Score>();
    }
    
    public void loadScores(int challenge_id) {
    	
    	Log.d(Constants.LOG_TAG, "load scores from challenge with id " + challenge_id);
    	
    	HttpClient httpClient = new DefaultHttpClient();  
    	HttpPost httpPost = new HttpPost(Constants.URL_WEBSERVICE_GETSCORES);  
    	HttpResponse response;  
    	
    	try {  
        	
       	 	List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("challenge_id", Integer.toString(challenge_id)));
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
            		
            		scoreList = new ArrayList<Score>();
            		
            		JSONArray scores = new JSONArray(new String(result));
            		for (int i = 0; i < scores.length(); i++) {
            			scoreList.add(new Score(scores.getJSONObject(i)));
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
			httpPost.abort();  
		}
    }
    
    /**
     * @return scores of a specific challenge
     */
    public ArrayList<Score> getScores(int challenge_id) {
    	this.loadScores(challenge_id);
        return scoreList;
    }

    /**
     * Get the singleton instance of this class.
     * @return The (singleton) ScoreListModel
     */
    public static ScoreListModel getInstance(Context context) {
        if (instance == null) {
            // If the singleton doesn't exist, create it
            instance = new ScoreListModel(context);
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
