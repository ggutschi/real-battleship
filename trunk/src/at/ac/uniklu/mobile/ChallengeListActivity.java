/**
 * 
 */
package at.ac.uniklu.mobile;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import at.ac.uniklu.mobile.db.Challenge;
import at.ac.uniklu.mobile.util.Constants;

/**
 * Activity for display all available challenges.
 *
 */
public class ChallengeListActivity extends ListActivity {
	
	private ArrayList<Challenge> challengeList;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {    	 
    	super.onCreate(savedInstanceState);
    	Log.d(Constants.LOG_TAG, "initialize list view");
        setContentView(R.layout.challenge_list);
        
        ChallengeListModel listModel = ChallengeListModel.getInstance(getApplicationContext());
        challengeList = listModel.getChallenges();
               
        ChallengeListAdapter challengeAdapter = new ChallengeListAdapter(this, challengeList, android.R.layout.simple_list_item_2);
        setListAdapter(challengeAdapter);
        
        // Register an observer to handle the case where the list contents change
        getListAdapter().registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                configureView();
            }
        });
        
        // Register our adapter to be notified of model changes
        listModel.registerDependentAdapter(challengeAdapter);

        registerForContextMenu(getListView());

        
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
		Intent intent = new Intent(ChallengeListActivity.this, HomeActivity.class);
		intent.putExtra(Challenge.FIELD_ID, c.getId());
		setResult(RESULT_OK, intent);
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
}
