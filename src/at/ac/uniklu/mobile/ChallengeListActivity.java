/**
 * 
 */
package at.ac.uniklu.mobile;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

/**
 * Activity for display all available challenges.
 *
 */
public class ChallengeListActivity extends Activity{
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.challenge_list);
        
        Log.v(Constants.LOG_TAG, "initialize list view");        
        
    }

}
