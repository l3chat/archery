package de.psp24.alleinsgold.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import de.psp24.alleinsgold.data.tables.ArcherToMatches;
import de.psp24.alleinsgold.data.tables.Archers;
import de.psp24.alleinsgold.data.tables.Arrows;
import de.psp24.alleinsgold.data.tables.Matches;
import de.psp24.alleinsgold.data.tables.Passes;
import de.psp24.alleinsgold.data.tables.Rounds;

public class SchemaHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "archery_data.db";

	// TOGGLE THIS NUMBER FOR UPDATING TABLES AND DATABASE
	private static final int DATABASE_VERSION = 1;

	public SchemaHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w("LOG_TAG", "Upgrading database from version " + oldVersion
				+ " to " + newVersion + ", which will destroy all old data");
		
		// KILL PREVIOUS TABLES IF UPGRADED
		db.execSQL("DROP TABLE IF EXISTS " + Arrows.TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + Passes.TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + Rounds.TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + ArcherToMatches.TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + Matches.TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + Archers.TABLE_NAME);

		// CREATE NEW INSTANCE OF SCHEMA
		onCreate(db);
	}
	
	public void createArcher(String lastName, String firstName){
	}

	//TODO get the parameters for match creation:
//	public void createMatch(){
//	}

}
