package de.psp24.alleinsgold;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Set;

import org.apache.http.message.BasicNameValuePair;

import de.psp24.alleinsgold.data.SchemaHelper;
import de.psp24.alleinsgold.data.tables.Archers;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.SimpleCursorAdapter.ViewBinder;
import android.widget.TwoLineListItem;

public class RoundFragment extends Fragment {
	
	public static final String ROUND_NUMBER = "ROUND_NUMBER";
	public static final String ROUND_ID     = "ROUND_ID";
	public static final String MATCH_ID     = "MATCH_ID";

	
	long mMatchId = 0;
	int mRoundNo = 0;
	int mRoundId = 0;
	
	ArrayList<ArcherRoundScore> archerScores;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_round, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Bundle arguments = getArguments();
		mMatchId = arguments.getLong(MATCH_ID, 0);
		mRoundNo = arguments.getInt(ROUND_NUMBER, 0);
		mRoundId = arguments.getInt(ROUND_ID, 0);
		
		changeHeaderText();
		
		SchemaHelper schemaHelper = new SchemaHelper(getActivity());
		
		Set<Integer> archerIds = schemaHelper.getArcherIds(mMatchId);
		
		archerScores = new ArrayList<RoundFragment.ArcherRoundScore>();
		for( int archerId : archerIds ) {
			Cursor archerCursor = schemaHelper.getArcher(archerId);
			ArcherRoundScore ars = new ArcherRoundScore();
			ars.archerId   = archerId;
			ars.archerName = archerCursor.getString(archerCursor.getColumnIndex(Archers.FIRST_NAME)) + " " +
							 archerCursor.getString(archerCursor.getColumnIndex(Archers.LAST_NAME));
			ars.score      = schemaHelper.getScoreForArcherInRound(archerId, mRoundId);
			archerScores.add(ars);
		}
		 ArrayAdapter<ArcherRoundScore> adapter = new ArrayAdapter<ArcherRoundScore>(getActivity(),
			        android.R.layout.simple_list_item_2, archerScores){
			            @Override
			            public View getView(int position, View convertView, ViewGroup parent){
			                TwoLineListItem row;            
			                if(convertView == null){
			                    LayoutInflater inflater = (LayoutInflater)getActivity().getApplicationContext().getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
			                    row = (TwoLineListItem)inflater.inflate(android.R.layout.simple_list_item_2, null);                    
			                }else{
			                    row = (TwoLineListItem)convertView;
			                }
			                ArcherRoundScore data = archerScores.get(position);
			                row.getText1().setText(data.archerName);
			                row.getText2().setText(data.score);
			     
			                return row;
			            }
			        };;
			    
		
		 
		ListView lv = (ListView)getView().findViewById(android.R.id.list);
		lv.setAdapter(adapter);
		
	}

	
	public void changeHeaderText(){
		TextView tv = (TextView)getView().findViewById(R.id.round_number);
		tv.setText(getString(R.string.round_number, mRoundNo));
	}
	
	
	private class ArcherRoundScore
	{
		public int    archerId = 0;
		public String archerName = "";
		public int    score;
		
		public String toString() {
			return archerName;
		}
	}
}