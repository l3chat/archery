package de.psp24.alleinsgold;

import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.Menu;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class ShooterResultsTableActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shooter_results_table);
		fillTable();
	}

	private ArrayList<Passe> getMockPasses()
	{
		ArrayList<Passe> passes = new ArrayList<Passe>();
		
		// Passe 1
		Passe passe = new Passe();
		passe.arrow1 = 10;
		passe.arrow2 = 8;
		passe.arrow3 = 7;
		passes.add(passe);
		
		// Passe 2
		passe = new Passe();
		passe.arrow1 = 10;
		passe.arrow2 = 8;
		passe.arrow3 = 8;
		passes.add(passe);
		
		// Passe 3
		passe = new Passe();
		passe.arrow1 = 10;
		passe.arrow2 = 9;
		passe.arrow3 = 9;
		passes.add(passe);
		
		// Passe 4
		passe = new Passe();
		passe.arrow1 = 10;
		passe.arrow2 = 10;
		passe.arrow3 = 7;
		passes.add(passe);
		
		// Passe 5
		passe = new Passe();
		passe.arrow1 = 10;
		passe.arrow2 = 9;
		passe.arrow3 = 7;
		passes.add(passe);
		
		// Passe 6
		passe = new Passe();
		passe.arrow1 = 9;
		passe.arrow2 = 7;
		passe.arrow3 = 7;
		passes.add(passe);
		
		// Passe 7
		passe = new Passe();
		passe.arrow1 = 9;
		passe.arrow2 = 9;
		passe.arrow3 = 8;
		passes.add(passe);
		
		// Passe 8
		passe = new Passe();
		passe.arrow1 = 9;
		passe.arrow2 = 9;
		passe.arrow3 = 8;
		passes.add(passe);
		
		// Passe 9
		passe = new Passe();
		passe.arrow1 = 9;
		passe.arrow2 = 8;
		passe.arrow3 = 7;
		passes.add(passe);
		
		// Passe 10
		passe = new Passe();
		passe.arrow1 = 9;
		passe.arrow2 = 8;
		passe.arrow3 = 7;
		passes.add(passe);
		
		// Passe 11
		passe = new Passe();
		passe.arrow1 = 9;
		passe.arrow2 = 8;
		passe.arrow3 = 7;
		passes.add(passe);
		
		// Passe 12
		passe = new Passe();
		passe.arrow1 = 9;
		passe.arrow2 = 9;
		passe.arrow3 = 8;
		passes.add(passe);
		
		return passes;
	}
	
	private void fillTable()
	{
		// Setup of Mock-Data
		String shooterName = "Hans Mustermann";
		String shooterInfo = "SV Schießmichtot";
		String distance    = "40m";
		int numberOfPasses = 12;	
		ArrayList<Passe> passes = getMockPasses();		
		
		// Set Header data
		TextView shooterNameView   = (TextView)findViewById(R.id.shooter_name);
		shooterNameView.setText(shooterName);
		TextView shooterInfoView   = (TextView)findViewById(R.id.shooter_info);
		shooterInfoView.setText(shooterInfo);
		TextView distanceValueView = (TextView)findViewById(R.id.distance_value);
		distanceValueView.setText(distance);
		
		// Populate the table
		TableLayout table = (TableLayout)findViewById(R.id.shooting_result_table);
		// Remove the example Passe
		table.removeView(findViewById(R.id.include_example_passe));
		TableRow footerRow = (TableRow)findViewById(R.id.tableRow_result_table_footer);
		table.removeView(footerRow);
		LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		int lastRowSum = 0;
		int aggregatedSum = 0;
		for( int i=0; i< numberOfPasses; i++ )
		{	
			int rowSum = 0;
			
			// Create  a new Passe Row from the layout
			TableRow passeRow = (TableRow)inflater.inflate(R.layout.table_row_passe, null);
			TextView tv = (TextView)passeRow.findViewById(R.id.passeRow_rowNumber);
			// Insert the line number
			tv.setText( String.valueOf(i+1) );
	
			if( i < passes.size() )
			{
				// Get the Passe for this iteration
				Passe passe = passes.get(i);
				rowSum = passe.getRowSum(); // Calculate the sum for this passe
				
				// Print out the values
				tv = (TextView)passeRow.findViewById(R.id.passeRow_arrow1_value);
				tv.setText( String.valueOf( passe.arrow1) );
				tv = (TextView)passeRow.findViewById(R.id.passeRow_arrow2_value);
				tv.setText( String.valueOf( passe.arrow2) );
				tv = (TextView)passeRow.findViewById(R.id.passeRow_arrow3_value);
				tv.setText( String.valueOf( passe.arrow3) );	
			}
			else
			{
				// In case this passe was not shot yet, leave the fields empty
				tv = (TextView)passeRow.findViewById(R.id.passeRow_arrow1_value);
				tv.setText( "" );
				tv = (TextView)passeRow.findViewById(R.id.passeRow_arrow2_value);
				tv.setText( "" );
				tv = (TextView)passeRow.findViewById(R.id.passeRow_arrow3_value);
				tv.setText( "" );
			}
			
			// Print out the Sums
			tv = (TextView)passeRow.findViewById(R.id.passeRow_sum1_value);
			tv.setText( String.valueOf( rowSum ) );
			
			tv = (TextView)passeRow.findViewById(R.id.passeRow_sum2_value);
			if( i % 2 == 0)
			{
				tv.setText("");
			}
			else
			{
				tv.setText( String.valueOf(lastRowSum + rowSum) );
			}
			
			lastRowSum = rowSum; // Remember the sum for the next iteration
			aggregatedSum += rowSum; // Add to the total
			
			// Now show the carry value
			tv = (TextView)passeRow.findViewById(R.id.passeRow_carry_value);
			if( i%2 != 0 && i > 2 ) // Skip the first Line as there is nothing to show in it
			{				
				tv.setText( String.valueOf( aggregatedSum ) );
			}
			else {
				tv.setText("");
			}
			
			// Finally add the Row to the table
			table.addView(passeRow);
		}
		
		TextView totalScore   = (TextView)footerRow.findViewById(R.id.tableRow_result_table_footer_totalSum);
		totalScore.setText( String.valueOf( aggregatedSum ) );
		table.addView(footerRow);
		
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.shooter_results_table, menu);
		return true;
	}

	private class Passe {
		public int arrow1 = 0;
		public int arrow2 = 0;
		public int arrow3 = 0;
		
		public int getRowSum() {
			return arrow1 + arrow2 + arrow3;
		}
		
	};
	
}
