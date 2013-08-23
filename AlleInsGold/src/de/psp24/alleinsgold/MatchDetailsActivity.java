package de.psp24.alleinsgold;

import java.util.Currency;

import de.psp24.alleinsgold.data.SchemaHelper;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;

public class MatchDetailsActivity extends FragmentActivity {
	
	public final static String MATCH_ID = "de.psp24.alleinsgold.data.tables.Matches.id";
	private long mCurrentMatchId = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_match_details);
		// Show the Up button in the action bar.
		setupActionBar();
		
		Intent intent = getIntent();
		mCurrentMatchId = intent.getLongExtra(MATCH_ID,0);
		
		Cursor c = new SchemaHelper(this).getMatch(mCurrentMatchId);
		
		
		TextView tv = (TextView)findViewById(R.id.txtview_match_id);
		tv.setText("Selected Match: "+mCurrentMatchId);
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.competition_details, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
