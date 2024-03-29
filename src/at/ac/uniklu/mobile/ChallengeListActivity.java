/**
 * 
 */
package at.ac.uniklu.mobile;

import java.util.ArrayList;
import java.util.List;

import com.flurry.android.FlurryAgent;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import at.ac.uniklu.mobile.db.Challenge;
import at.ac.uniklu.mobile.db.Participant;
import at.ac.uniklu.mobile.util.Constants;
import at.ac.uniklu.mobile.util.HelperUtil;

/**
 * Activity for displaying all available challenges.
 *
 */
public class ChallengeListActivity extends ListActivity {
	
	private ArrayList<Challenge> challengeList;
    
	/**
	 * Progress dialog displayed during challenge retrieval from webserver
	 */
	private ProgressDialog progressDialog;
	
	/**
	 * List model holding challenges
	 */
	private ChallengeListModel listModel = null;
	
	/**
	 * Nickname of user
	 */
	private String nickname;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {    	 
    	super.onCreate(savedInstanceState);

    	// Set title and background color of title bar
    	View titleView = getWindow().findViewById(android.R.id.title);
    	if (titleView != null) {
    	  ViewParent parent = titleView.getParent();
    	  if (parent != null && (parent instanceof View)) {
    	    View parentView = (View)parent;
    	    parentView.setBackgroundColor(Color.rgb(0xff, 0x00, 0x00));
    	  }
    	}
    	
    	Log.d(Constants.LOG_TAG, "initialize list view");
        setContentView(R.layout.challenge_list);
        nickname = getIntent().getExtras().getString(Participant.FIELD_NICKNAME);
        Log.d(Constants.LOG_TAG, "got nickname " + nickname);
        
        listModel = ChallengeListModel.getInstance(getApplicationContext()	);
        Log.d(Constants.LOG_TAG, "display progress dialog");
        
        ProgressDialog progress = new ProgressDialog(this);
        progress.setMessage("Loading challenges");
        new MyTask(progress).execute();
    }
    
    /**
     * Configure the view according to whether we have items in the list.
     */
    private void configureView() {
        View list = getListView();
        View text = findViewById(android.R.id.empty);

        if (getListAdapter().getCount() <= 0) {
            // No animals in list -- show text view
            list.setVisibility(View.GONE);
            text.setVisibility(View.VISIBLE);
        } else {
            // Animals in list -- show list view
            list.setVisibility(View.VISIBLE);
            text.setVisibility(View.GONE);
        }
    }
    
    
    
    @Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		Log.d(Constants.LOG_TAG, "list item click, id: " + id);
		ChallengeListModel listModel = ChallengeListModel.getInstance(ChallengeListActivity.this);
		Challenge c = listModel.getChallenges().get(position);
		
		// notify server
		String android_id = HelperUtil.getAndroidId(this);
		String ip_address = HelperUtil.getIpAddress();
		
		Log.d(Constants.LOG_TAG, "notify server because of new participant with android_id: " + android_id
				+ " ip address: " + ip_address + " challenge id: " + c.getId() + " nickname : " + nickname);
		
		boolean bOK = listModel.addParticipant(android_id, ip_address, c.getId(), nickname);		
		
		Intent intent = new Intent(ChallengeListActivity.this, HomeActivity.class);
		intent.putExtra(Challenge.FIELD_ID, c.getId());
		if (bOK)
			setResult(RESULT_OK, intent);
		else
			setResult(Constants.RETURN_CODE_CHANGE_CHALLENGE_ERROR, intent);
		finish();
	}
    



	/**
     * (Simple) list adapter for a list of animals.
     */
    private class ChallengeListAdapter extends BaseAdapter {
        private List<Challenge> challengeList;
        private int viewId;
       

        /**
         * Constructor for our animal list adapter.
         * 
         * @param context
         *            The application context in which we operate
         * @param data
         *            The model we need to map
         * @param resource
         *            Layout resource ID for animal view to display in list
         */
        public ChallengeListAdapter(Activity context, List<Challenge> data, int resource) {
            super();
            challengeList = data;
            viewId = resource;            
        }

        /*
         * (non-Javadoc)
         * 
         * @see android.widget.Adapter#getCount()
         */
        @Override
        public int getCount() {
            return challengeList.size();
        }

        /*
         * (non-Javadoc)
         * 
         * @see android.widget.Adapter#getItem(int)
         */
        @Override
        public Object getItem(int position) {
            return challengeList.get(position);
        }

        /*
         * (non-Javadoc)
         * 
         * @see android.widget.Adapter#getItemId(int)
         */
        @Override
        public long getItemId(int position) {
            return position;
        }

        /*
         * (non-Javadoc)
         * 
         * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView;

            if (convertView == null) {
                // Make a new view to display the given animal
                itemView =  getLayoutInflater().inflate(viewId, parent, false);
            } else {
                // Recycle convertView, which should be a TwoLineListItem
                itemView =  convertView;
            }

            TextView text1 = (TextView) itemView.findViewById(android.R.id.text1);
            TextView text2 = (TextView) itemView.findViewById(android.R.id.text2);
            Challenge challenge = challengeList.get(position);
            text1.setText(challenge.getName());
            text2.setText(challenge.getLocation());
           
            return itemView;
        }
    }
    
    /**
     * Task for progress dialog
     */
    public class MyTask extends AsyncTask<Void, Void, Void> {
    	  public MyTask(ProgressDialog progress) {
    	    progressDialog = progress;
    	  }

    	  public void onPreExecute() {
    		  progressDialog.show();
    	  }

    	  public Void doInBackground(Void... unused) {
    		 ChallengeListModel listModel = ChallengeListModel.getInstance(getApplicationContext()	);
     		 challengeList = listModel.getChallenges();     		
     		 return null;
    	  }

    	  public void onPostExecute(Void unused) {
    		  progressDialog.dismiss();
    		  ChallengeListActivity.this.populateList(challengeList);
    	  }
    	}
    
    public void populateList(ArrayList<Challenge> challengeList) {
    	ChallengeListAdapter challengeAdapter = new ChallengeListAdapter(ChallengeListActivity.this, challengeList, android.R.layout.simple_list_item_2);
        ChallengeListActivity.this.setListAdapter(challengeAdapter);
        
        // Register an observer to handle the case where the list contents change
        ChallengeListActivity.this.getListAdapter().registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                configureView();
            }
        });
        
        // Register our adapter to be notified of model changes
        ChallengeListModel listModel = ChallengeListModel.getInstance(getApplicationContext()	);
        listModel.registerDependentAdapter(challengeAdapter);

        ChallengeListActivity.this.registerForContextMenu(getListView());
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

}
