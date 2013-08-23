package de.psp24.alleinsgold;

import java.text.DateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorAdapter.ViewBinder;
import android.widget.TextView;
import de.psp24.alleinsgold.data.GoldDataProvider;
import de.psp24.alleinsgold.data.SchemaHelper;
import de.psp24.alleinsgold.data.tables.Matches;

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
	private SchemaHelper schemaHelper;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_match_list, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		schemaHelper = new SchemaHelper(getActivity());

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
		
		// create context menu for matches
		registerForContextMenu(getListView());
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
	
	
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
    	if (v.getId() == android.R.id.list) {
    		menu.add(Menu.NONE, 0, 0, "remove match"); //TODO : load from resources, like below:
//    		String[] menuItems = getResources().getStringArray(R.array.menu); 
//    		for (int i = 0; i<menuItems.length; i++) {
//    			menu.add(Menu.NONE, i, i, menuItems[i]);
//			}
    	}
    }
    
    @Override
    public boolean onContextItemSelected(MenuItem item) {
    	boolean result = true;
    	AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
    	int menuItemIndex = item.getItemId();
    	if(menuItemIndex == 0){
    		Cursor c = (Cursor) mAdapter.getItem(info.position);
    		long matchId = c.getLong(c.getColumnIndex(Matches.ID));
    		
    		//TODO: delete using GoldDataProvider!!!!
    		// and call there inside delete() the following line:
    		// getContext().getContentResolver().notifyChange(GoldDataProvider.MATCHES_CONTENT_URI, null);
    		// this will update the list
    		result = schemaHelper.removeMatch((int)matchId);
    		
    		// this doesn't update the list, see above
    		// commented out
//    		getActivity().getContentResolver().notifyChange(GoldDataProvider.MATCHES_CONTENT_URI, null);
    	}
    	return result;
    }


}
