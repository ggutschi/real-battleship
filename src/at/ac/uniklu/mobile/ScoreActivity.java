package at.ac.uniklu.mobile;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.DataSetObserver;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import at.ac.uniklu.mobile.db.Challenge;
import at.ac.uniklu.mobile.db.Participant;
import at.ac.uniklu.mobile.db.Score;
import at.ac.uniklu.mobile.util.Constants;

	/**
	 * Activity for display user scores for one challenge
	 *
	 */
	public class ScoreActivity extends ListActivity {
		
		private int challenge_id;
		private String nickname;
		private ArrayList<Score> scoreList;
	    
		private ProgressDialog progressDialog;
		private ScoreListModel listModel = null; 
		
		/** Called when the activity is first created. */
	    @Override
	    public void onCreate(Bundle savedInstanceState) {    	 
	    	super.onCreate(savedInstanceState);
	    	Log.d(Constants.LOG_TAG, "initialize list view");
	        setContentView(R.layout.score_list);
	        
	        challenge_id = getIntent().getExtras().getInt(Challenge.FIELD_ID);
	        Log.d(Constants.LOG_TAG, "got challenge id " + challenge_id);
	        
	        listModel = ScoreListModel.getInstance(getApplicationContext()	);
	        Log.d(Constants.LOG_TAG, "display progress dialog");
	        
	        ProgressDialog progress = new ProgressDialog(this);
	        progress.setMessage("Loading scores");
	        new MyTask(progress, challenge_id).execute();
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
			// ignore this case
		}
	    
		/**
	     * (Simple) list adapter for a list of animals.
	     */
	    private class ScoreListAdapter extends ArrayAdapter<Score> {
	        private List<Score> scoreList;
	                
	        public ScoreListAdapter(Context context, int textViewResourceId, ArrayList<Score> items) {
	        	super(context, textViewResourceId, items);
	            scoreList = items;
	        }

	        /*
	         * (non-Javadoc)
	         * 
	         * @see android.widget.Adapter#getCount()
	         */
	        @Override
	        public int getCount() {
	            return scoreList.size();
	        }

	        /*
	         * (non-Javadoc)
	         * 
	         * @see android.widget.Adapter#getItem(int)
	         */
	        @Override
	        public Score getItem(int position) {
	            return scoreList.get(position);
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
	        	View v = convertView;
	            if (v == null) {
                    LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = vi.inflate(R.layout.score_row, null);
                }
	            Score score = scoreList.get(position);
                if (score != null) {
                        TextView tt = (TextView) v.findViewById(R.id.nickname);
                        TextView bt = (TextView) v.findViewById(R.id.score);
                        if (tt != null) {
                              tt.setText(score.getUser().getNickname());                            
                        }
                        if(bt != null){
                              bt.setText(score.getUser().getScore());
                        }
                }
                return v;
	        }
	    }
	    
	    public class MyTask extends AsyncTask<Void, Void, Void> {
	    	private int challenge_id;
	    	  public MyTask(ProgressDialog progress, int challenge) {
	    	    progressDialog = progress;
	    	    challenge_id = challenge;
	    	  }

	    	  public void onPreExecute() {
	    		  progressDialog.show();
	    	  }

	    	  public Void doInBackground(Void... unused) {
	    		 ScoreListModel listModel = ScoreListModel.getInstance(getApplicationContext()	);
	     		 scoreList = listModel.getScores(challenge_id);    		
	     		 return null;
	    	  }

	    	  public void onPostExecute(Void unused) {
	    		  progressDialog.dismiss();
	    		  ScoreActivity.this.populateList(scoreList);
	    	  }
	    	}
	    
	    public void populateList(ArrayList<Score> scoreList) {
	    	ScoreListAdapter challengeAdapter = new ScoreListAdapter(ScoreActivity.this, R.layout.score_row, scoreList);
	        ScoreActivity.this.setListAdapter(challengeAdapter);
	        
	        // Register an observer to handle the case where the list contents change
	        ScoreActivity.this.getListAdapter().registerDataSetObserver(new DataSetObserver() {
	            @Override
	            public void onChanged() {
	                configureView();
	            }
	        });
	        
	        // Register our adapter to be notified of model changes
	        ChallengeListModel listModel = ChallengeListModel.getInstance(getApplicationContext()	);
	        listModel.registerDependentAdapter(challengeAdapter);

	        ScoreActivity.this.registerForContextMenu(getListView());
	    }

	}


