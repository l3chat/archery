package de.psp24.alleinsgold;

import java.text.DateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorAdapter.ViewBinder;
import android.widget.TextView;
import de.psp24.alleinsgold.data.GoldDataProvider;
import de.psp24.alleinsgold.data.tables.Matches;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;

/**
 * Displays the list of all Matches 
 * @author D054746
 *
 */
public class MatchListFragment extends ListFragment implements
		LoaderCallbacks<Cursor> {
	
	private static final int MATCHES_LOADER = 0;

	// This is the Adapter being used to display the list's data.
	SimpleCursorAdapter mAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_match_list, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		mAdapter = new SimpleCursorAdapter(this.getActivity(), // Context.
				android.R.layout.two_line_list_item, // Specify the row template
				// to use (here, two
				// columns bound to the
				// two retrieved cursor
				// rows).
				null, // Pass in the cursor to bind to.
				// Array of cursor columns to bind to.
				new String[] { Matches.NAME, Matches.DATE },
				// Parallel array of which template objects to bind to those
				// columns.
				new int[] { android.R.id.text1, android.R.id.text2 },
				// Flags
				CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		mAdapter.setViewBinder(new ViewBinder() {

		    public boolean setViewValue(View aView, Cursor aCursor, int aColumnIndex) {
		        if (aColumnIndex == 1) {
		        		DateFormat df = DateFormat.getDateInstance();
		                String dateString = df.format( new Date(aCursor.getLong(aColumnIndex)*1000) );		                
		                TextView textView = (TextView) aView;
		                textView.setText( dateString );
		                return true;
		         }

		         return false;
		    }
		});
		
		setListAdapter(mAdapter);

		// Prepare the loader. Either re-connect with an existing one,
		// or start a new one.
		getLoaderManager().initLoader(MATCHES_LOADER, null, (LoaderCallbacks<Cursor>) this);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		// Find out the ID of the Match
		Cursor c = (Cursor)mAdapter.getItem(position);
		long matchId = c.getLong(c.getColumnIndex(Matches.ID));
		// Then start the Activity, passing the Match ID
		Activity activity = getActivity();
		Intent intent = new Intent(activity, MatchDetailsActivity.class);
		intent.putExtra(MatchDetailsActivity.MATCH_ID, matchId);
		activity.startActivity(intent);
	}

	// ---------------- LoaderManager.LoaderCallback Implementation

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		String[] projection = { Matches.ID, Matches.DATE };
		CursorLoader cursorLoader = new CursorLoader(getActivity(),
				GoldDataProvider.MATCHES_CONTENT_URI, projection, null, null, null);
		return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		// Swap the new cursor in. (The framework will take care of closing the
		// old cursor once we return.)
		mAdapter.swapCursor(cursor);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		// This is called when the last Cursor provided to onLoadFinished()
		// above is about to be closed. We need to make sure we are no
		// longer using it.
		mAdapter.swapCursor(null);
	}

}
