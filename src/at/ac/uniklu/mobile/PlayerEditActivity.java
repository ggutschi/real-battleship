package at.ac.uniklu.mobile;


import com.flurry.android.FlurryAgent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import at.ac.uniklu.mobile.db.Participant;
import at.ac.uniklu.mobile.util.Constants;


/**
 * Start screen of application asking for the name of the user
 */
public class PlayerEditActivity extends Activity {
	
	/**
	 * Text field for username
	 */
	private EditText user_nickname;
	
	/**
	 * Button for confirming username
	 */
	private Button button_continue;
	
	 /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Log.d(Constants.LOG_TAG, "on Create player edit");
        setContentView(R.layout.edit_user); 
        
        user_nickname = (EditText) findViewById(R.id.username);
        button_continue = (Button) findViewById(R.id.button_continue);
        Log.d(Constants.LOG_TAG, "setup change listener");
        setupChangeListener();
        Log.d(Constants.LOG_TAG, "setup change listener after");
        
        // set up button listener for menu selection
        final Button button_continue = (Button) findViewById(R.id.button_continue);
        button_continue.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	Log.d(Constants.LOG_TAG, "continue button clicked");
            	EditText et = (EditText)findViewById(R.id.username);
                startActivity(new Intent(PlayerEditActivity.this, HomeActivity.class).putExtra(Participant.FIELD_NICKNAME, user_nickname.getText().toString()));
            }
        });
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
    
    /**
     * Create the change listeners we'll need to react to user input.
     */
    private void setupChangeListener() {
        user_nickname.addTextChangedListener(new TextChangedWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            	Log.d(Constants.LOG_TAG, "user nickname changed");
            	if (user_nickname!= null && user_nickname.length() > 0) {
            		Log.d(Constants.LOG_TAG, "user nickname valid");
                    // enable continue button if user has entered some text for nickname
                	button_continue.setEnabled(true);
                }
            	else
            		button_continue.setEnabled(false);
            }
        });
    }

	@Override
	protected void onResume() {
		super.onResume();
	}
    
	/**
     * Utility class that implements two of the three TextWatcher methods. Extend this class to implement a text changed
     * listener.
     */
    private class TextChangedWatcher implements TextWatcher {
        /*
         * (non-Javadoc)
         * 
         * @see android.text.TextWatcher#afterTextChanged(android.text.Editable)
         */
        @Override
        public void afterTextChanged(Editable s) {
            throw new RuntimeException("afterTextChanged not implemented");
        }

        /*
         * (non-Javadoc)
         * 
         * @see android.text.TextWatcher#beforeTextChanged(java.lang.CharSequence, int, int, int)
         */
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // Ignore
        }

        /*
         * (non-Javadoc)
         * 
         * @see android.text.TextWatcher#onTextChanged(java.lang.CharSequence, int, int, int)
         */
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // Ignore.
        }
    }
    

}

    
    