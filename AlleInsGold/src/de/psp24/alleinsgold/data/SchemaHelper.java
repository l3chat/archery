package de.psp24.alleinsgold.data;

import java.util.Calendar;

import android.content.ContentValues;
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
		// create matches table
		db.execSQL("CREATE TABLE " + Matches.TABLE_NAME + " ("
				+ Matches.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ Matches.DATE + " INTEGER, "	// seconds since epoc; 
									// see http://www.sqlite.org/datatype3.html, 
									// http://www.sqlite.org/lang_datefunc.html
				+ Matches.DISTANCE + " INTEGER, " // in meters
				+ Matches.N_ROUNDS + " INTEGER, " // 1 or 2
				+ Matches.N_PASSES + " INTEGER, " // 10 or 12
				+ Matches.N_ARROWS + " INTEGER);" // 3 or 6
				);
		
		// create rounds table
		db.execSQL("CREATE TABLE " + Rounds.TABLE_NAME + " ("
				+ Rounds.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ Rounds.P_MATCH_id + " INTEGER, " // parent match id
				+ Rounds.NUMBER + " INTEGER);" // round number
				);
		
		// create passes table
		db.execSQL("CREATE TABLE " + Passes.TABLE_NAME + " ("
				+ Passes.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ Passes.P_ROUND_ID + " INTEGER, " // parent round id
				+ Passes.P_ARCHER_ID + " INTEGER, " // archer id
				+ Passes.NUMBER + " INTEGER);" // pass number
				);
		
		// create arrows table
		db.execSQL("CREATE TABLE "  + Arrows.TABLE_NAME + " ("
				+ Arrows.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ Arrows.P_PASSE_ID + " INTEGER, " // parent pass id
				+ Arrows.NUMBER + " INTEGER, " // arrow number
				+ Arrows.SCORE + " TEXT);" // arrow score: can be "X" (eye), so not an integer
				);
		
		// create archers table
		db.execSQL("CREATE TABLE " + Archers.TABLE_NAME + " ("
				+ Archers.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ Archers.FIRST_NAME + " TEXT, "
				+ Archers.LAST_NAME + " TEXT);"
				);
		
		// create assignment table
		db.execSQL("CREATE TABLE " + ArcherToMatches.TABLE_NAME + " ("
				+ ArcherToMatches.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ ArcherToMatches.ARCHER_ID + " INTEGER, " // archer assigned to..
				+ ArcherToMatches.MATCH_ID + " INTEGER);" // ...the match
				);
		
//		createInitialContent();
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w("LOG_TAG", "Upgrading database from version " + oldVersion
				+ " to " + newVersion + ", which will destroy all old data");
		
		// KILL PREVIOUS TABLES IF UPGRADED
		dropTables(db);

		// CREATE NEW INSTANCE OF SCHEMA
		onCreate(db);
	}
	
	private void dropTables(SQLiteDatabase db){
		db.execSQL("DROP TABLE IF EXISTS " + Arrows.TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + Passes.TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + Rounds.TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + ArcherToMatches.TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + Matches.TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + Archers.TABLE_NAME);
	}
	
	
	// create an entry for an archer
	public long addArcher(String firstName, String lastName){
		ContentValues cv = new ContentValues();
		cv.put(Archers.FIRST_NAME, firstName);
		cv.put(Archers.LAST_NAME, lastName);
		
		SQLiteDatabase sd = getWritableDatabase();
		
		// if cv is empty, Archers.LAST_NAME will be explicitly set to "NULL"
		long result = sd.insert(Archers.TABLE_NAME, Archers.LAST_NAME, cv);
		return result;
	}

	
	public long addMatch(Calendar datetime, int distance, int n_rounds, int n_passes, int n_arrows){
		ContentValues cv = new ContentValues();
		cv.put(Matches.DATE, datetime.getTimeInMillis() / 1000); // need seconds, not milliseconds
		cv.put(Matches.DISTANCE, distance);
		cv.put(Matches.N_ROUNDS, n_rounds);
		cv.put(Matches.N_PASSES, n_passes);
		cv.put(Matches.N_ARROWS, n_arrows);
		
		SQLiteDatabase sd = getWritableDatabase();
		
		long result = sd.insert(Matches.TABLE_NAME, Matches.DATE, cv);
		return result;
	}
	
	
	/**
	 * Assigns an archer with archerId
	 * to the match with matchId.
	 * 
	 * Returns true if successfully assigned.
	 */
	public boolean assignArcher(int archerId, int matchId){
		ContentValues cv = new ContentValues();
		cv.put(ArcherToMatches.ARCHER_ID, archerId);
		cv.put(ArcherToMatches.MATCH_ID, matchId);
		
		SQLiteDatabase sd = getWritableDatabase();
		
		long result = sd.insert(ArcherToMatches.TABLE_NAME, null, cv);
		return (result >= 0);
	}
	
	
	public long addRound(int matchId, int roundNumber){
		ContentValues cv = new ContentValues();
		cv.put(Rounds.P_MATCH_id, matchId);
		cv.put(Rounds.NUMBER, roundNumber);

		SQLiteDatabase sd = getWritableDatabase();
		
		long result = sd.insert(Rounds.TABLE_NAME, Rounds.NUMBER, cv);
		return result;
	}
	
	
	
	public long addPass(int pArcherId, int pRoundId, int passN){
		ContentValues cv = new ContentValues();
		cv.put(Passes.P_ARCHER_ID, pArcherId);
		cv.put(Passes.P_ROUND_ID, pRoundId);
		cv.put(Passes.NUMBER, passN);

		SQLiteDatabase sd = getWritableDatabase();
		
		long result = sd.insert(Passes.TABLE_NAME, Passes.NUMBER, cv);
		return result;
	}
	
	

	public long addArrow(int passId, int arrowN, String arrowScore){
		ContentValues cv = new ContentValues();
		cv.put(Arrows.P_PASSE_ID, passId);
		cv.put(Arrows.NUMBER, arrowN);
		cv.put(Arrows.SCORE, arrowScore);

		SQLiteDatabase sd = getWritableDatabase();
		
		long result = sd.insert(Arrows.TABLE_NAME, Arrows.NUMBER, cv);
		return result;
	}
	
	
	/*
	 * TODO: create a transaction
	 */
	public long createMockData(){
		// create a match
		// 50 meters, 1 round, 12 passes, 3 arrows
		int distance = 50;
		int nRounds = 1;
		int nPasses = 12;
		int nArrows = 3;
		long matchId = addMatch(Calendar.getInstance(), distance, nRounds, nPasses, nArrows);
		
		// create archers
		long a1 = addArcher("Bill", "Gates");
		long a2 = addArcher("Peter", "Higgs");
		long a3 = addArcher("Master", "Yoda");
		
		// assign archers
		assignArcher((int) a1, (int) matchId);
		assignArcher((int) a2, (int) matchId);
		assignArcher((int) a3, (int) matchId);
		
		// create rounds
		long r, p1, p2, p3, ar1, ar2, ar3;
		for(int i=0; i<nRounds; i++){
			r = addRound((int)matchId, i+1);
			
			// create passes
			for(int j=0; j<nPasses; j++){
				p1 = addPass((int) a1, (int) r, j+1);
				p2 = addPass((int) a2, (int) r, j+1);
				p3 = addPass((int) a3, (int) r, j+1);
				
				// create arrows
				for(int k=0; k<nArrows; k++){
					ar1 = addArrow((int)p1, k+1, Integer.toString((int)Math.floor(Math.random()*11)));
					ar2 = addArrow((int)p2, k+1, Integer.toString((int)Math.floor(Math.random()*11)));
					ar3 = addArrow((int)p3, k+1, "X"); // Master Yoda
				}
			}
		}
		
		return matchId;
	}

}
