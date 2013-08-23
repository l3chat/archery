package de.psp24.alleinsgold;

import java.text.DateFormat;
import java.util.Date;

import de.psp24.alleinsgold.data.SchemaHelper;
import de.psp24.alleinsgold.data.tables.Matches;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MatchHeaderFragment extends Fragment {
	
	private long mCurrentMatchId = 0;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_match_header, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		Intent intent = getActivity().getIntent();
		mCurrentMatchId = intent.getLongExtra(MatchDetailsActivity.MATCH_ID,0);
		
		Cursor c = new SchemaHelper(getActivity()).getMatch(mCurrentMatchId);
		c.moveToFirst();
		
		TextView tv = (TextView)getView().findViewById(R.id.tv_matchName);
		tv.setText(c.getString(c.getColumnIndex(Matches.NAME))); 
		tv = (TextView)getView().findViewById(R.id.tv_matchDate);
		DateFormat df = DateFormat.getDateInstance();
		String dateString = df.format( new Date( c.getLong(c.getColumnIndexOrThrow(Matches.DATE))*1000 ));
		tv.setText( dateString );
		tv = (TextView)getView().findViewById(R.id.tv_distance_value);
		tv.setText(c.getString(c.getColumnIndex(Matches.DISTANCE)));
		tv = (TextView)getView().findViewById(R.id.tv_numberOfRounds_value);
		tv.setText(c.getString(c.getColumnIndex(Matches.N_ROUNDS)));
	}
	
}
