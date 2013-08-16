package de.psp24.alleinsgold;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class ShooterResultsTableActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shooter_results_table);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.shooter_results_table, menu);
		return true;
	}

}
