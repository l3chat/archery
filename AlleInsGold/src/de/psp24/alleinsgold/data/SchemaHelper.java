package de.psp24.alleinsgold.data;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
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
	private static final int DATABASE_VERSION = 3;

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
				+ Matches.DISTANCE + " INTEGER, " 	// in meters
				+ Matches.N_ROUNDS + " INTEGER, " 	// 1 or 2
				+ Matches.N_PASSES + " INTEGER, " 	// 10 or 12
				+ Matches.N_ARROWS + " INTEGER, " 	// 3 or 6
				+ Matches.NAME     + " TEXT);" 			// match name
				);
		
		// create rounds table
		db.execSQL("CREATE TABLE " + Rounds.TABLE_NAME + " ("
				+ Rounds.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ Rounds.P_MATCH_ID + " INTEGER, " // parent match id
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
				+ Arrows.SCORE + " TEXT, " // arrow score: can be "X" (eye), so not an integer
				+ Arrows.MATCH_ID + " INTEGER);" // reference to the parent match
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

	
	private long addMatch(String matchName, Calendar datetime, int distance, int n_rounds, int n_passes, int n_arrows){
		ContentValues cv = new ContentValues();
		cv.put(Matches.DATE, datetime.getTimeInMillis() / 1000); // need seconds, not milliseconds
		cv.put(Matches.DISTANCE, distance);
		cv.put(Matches.N_ROUNDS, n_rounds);
		cv.put(Matches.N_PASSES, n_passes);
		cv.put(Matches.N_ARROWS, n_arrows);
		cv.put(Matches.NAME, matchName);
		
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
	private boolean assignArcher(int archerId, int matchId){
		ContentValues cv = new ContentValues();
		cv.put(ArcherToMatches.ARCHER_ID, archerId);
		cv.put(ArcherToMatches.MATCH_ID, matchId);
		
		SQLiteDatabase sd = getWritableDatabase();
		
		long result = sd.insert(ArcherToMatches.TABLE_NAME, null, cv);
		return (result >= 0);
	}
	
	
	private long addRound(int matchId, int roundNumber){
		ContentValues cv = new ContentValues();
		cv.put(Rounds.P_MATCH_ID, matchId);
		cv.put(Rounds.NUMBER, roundNumber);

		SQLiteDatabase sd = getWritableDatabase();
		
		long result = sd.insert(Rounds.TABLE_NAME, Rounds.NUMBER, cv);
		return result;
	}
	
	
	
	private long addPass(int pArcherId, int pRoundId, int passN){
		ContentValues cv = new ContentValues();
		cv.put(Passes.P_ARCHER_ID, pArcherId);
		cv.put(Passes.P_ROUND_ID, pRoundId);
		cv.put(Passes.NUMBER, passN);

		SQLiteDatabase sd = getWritableDatabase();
		
		long result = sd.insert(Passes.TABLE_NAME, Passes.NUMBER, cv);
		return result;
	}
	
	

	private long addArrow(int passId, int arrowN, String arrowScore, int matchId){
		ContentValues cv = new ContentValues();
		cv.put(Arrows.P_PASSE_ID, passId);
		cv.put(Arrows.NUMBER, arrowN);
		cv.put(Arrows.SCORE, arrowScore);
		cv.put(Arrows.MATCH_ID, matchId);

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
		
//		// create archers
//		ArrayList<Long> archers = new ArrayList<Long>();
//		archers.add(addArcher("Bill", "Gates"));
//		archers.add(addArcher("Peter", "Higgs"));
//		archers.add(addArcher("Master", "Yoda"));
//
//		long matchId = createEmptyMatch(archers, Calendar.getInstance(), distance, nRounds, nPasses, nArrows);

		long matchId = createEmptyMatch("Yoda wins", Calendar.getInstance(), distance, nRounds, nPasses, nArrows);

		// create and assign archers
		long archerId = addArcher("Bill", "Gates");
		boolean success = addArcherToMatch((int)archerId, (int)matchId);
		if(!success){
			removeMatch((int) matchId);
			return -1;
		}
		
		archerId = addArcher("Peter", "Higgs");
		success = addArcherToMatch((int)archerId, (int)matchId);
		if(!success){
			removeMatch((int) matchId);
			return -1;
		}
		
		archerId = addArcher("Master", "Yoda");
		success = addArcherToMatch((int)archerId, (int)matchId);
		if(!success){
			removeMatch((int) matchId);
			return -1;
		}

		return matchId;
	}
	

	//TODO make it transactional!!!!!
	public long createEmptyMatch(ArrayList<Long> archers, String matchName, Calendar datetime, int distance, int n_rounds, int n_passes, int n_arrows){
		long matchId = createEmptyMatch(matchName, datetime, distance, n_rounds, n_passes, n_arrows);

		boolean success = true;
		for(long archerId : archers){
			// assign archer
			success = addArcherToMatch((int) archerId, (int) matchId);
			if(!success){
				removeMatch((int)matchId);
				matchId = -1;
				break;
			}
		}
		
		return matchId;
	}

	
	
	public long createEmptyMatch(String matchName, Calendar datetime, int distance, int n_rounds, int n_passes, int n_arrows){
		long matchId = addMatch(matchName, datetime, distance, n_rounds, n_passes, n_arrows);
		return matchId;
	}
	
	
	public boolean addArcherToMatch(int archerId, int matchId){
		boolean result = true;
		
		int n_rounds=0, n_passes=0, n_arrows=0;
		Cursor c = getMatch(matchId);
		if(c.moveToFirst()){
			n_rounds = c.getInt(c.getColumnIndex(Matches.N_ROUNDS));
			n_passes = c.getInt(c.getColumnIndex(Matches.N_PASSES));
			n_arrows = c.getInt(c.getColumnIndex(Matches.N_ARROWS));
		} else {
			result = false;
			return result;
		}
		c.close();
		
		assignArcher(archerId, matchId);
		
		long roundId, passeId, arrowId;
		for(int i=0; i<n_rounds; i++){
			// create round
			roundId = addRound((int)matchId, i+1);
				

			// create passes
			for(int j=0; j<n_passes; j++){
				passeId = addPass((int) archerId, (int) roundId, j+1);
				
				// create arrows
				for(int k=0; k<n_arrows; k++){
					arrowId = addArrow((int)passeId, k+1, "0", (int) matchId);
					result &= (arrowId>0);
				}
			}
		}
		
		return result;
	}

	
//	public Long[] getAllArrowIds(long archerId, long matchId){
//		ArrayList<Long> arrows = new ArrayList<Long>();
//		return arrows.toArray(new Long[]{});
//	}
	
	
	public Cursor getArrows(long passeId){
		SQLiteDatabase sd = getReadableDatabase();
		
		String[] cols = new String[]{Arrows.ID, Arrows.NUMBER, Arrows.SCORE};
		String[] selectionArgs = new String[]{String.valueOf(passeId)};
		Cursor c = sd.query(Arrows.TABLE_NAME, cols, Arrows.P_PASSE_ID + " = ?",
				selectionArgs, null, null, Arrows.NUMBER);
		
		return c;
	}
	
	
	public Cursor getPasses(long roundId, long archerId){
		SQLiteDatabase sd = getReadableDatabase();
		
		String[] cols = new String[]{Passes.ID, Passes.NUMBER};
		String[] selectionArgs = new String[]{String.valueOf(archerId), String.valueOf(roundId)};
		Cursor c = sd.query(Passes.TABLE_NAME, cols, Passes.P_ARCHER_ID + " = ?"
				+ " AND " + Passes.P_ROUND_ID + " = ?",
				selectionArgs, null, null, Passes.NUMBER);
		
		return c;
	}
	
	
	public Cursor getRounds(long matchId){
		SQLiteDatabase sd = getReadableDatabase();
		
		String[] cols = new String[]{Rounds.ID, Rounds.NUMBER};
		String[] selectionArgs = new String[]{String.valueOf(matchId)};
		Cursor c = sd.query(Rounds.TABLE_NAME, cols, Rounds.P_MATCH_ID + " = ?",
				selectionArgs, null, null, Rounds.NUMBER);
		
		return c;
	}
	
	
	public Cursor getMatches(){
		SQLiteDatabase sd = getReadableDatabase();
		
		String[] cols = new String[]{Matches.ID, Matches.DATE, Matches.DISTANCE,
				Matches.N_ROUNDS, Matches.N_PASSES, Matches.N_ARROWS, Matches.NAME};
		Cursor c = sd.query(Matches.TABLE_NAME, cols, null,
				null, null, null, Matches.DATE);
		
		return c;
	}
	
	
	/**
	 * Gets a Cursor pointing to a match with specified matchId
	 * @param matchId
	 * @return
	 */
	public Cursor getMatch(long matchId){
		SQLiteDatabase sd = getReadableDatabase();
		
		String[] cols = new String[]{Matches.ID, Matches.DATE, Matches.DISTANCE,
				Matches.N_ROUNDS, Matches.N_PASSES, Matches.N_ARROWS, Matches.NAME};
		String[] selectionArgs = new String[]{String.valueOf(matchId)};
		Cursor c = sd.query(Matches.TABLE_NAME, cols, Matches.ID + " = ?",
				selectionArgs, null, null, Matches.DATE);
		
		return c;
	}
	
	
	public boolean removeMatch(int matchId){
		Set<Integer> roundIds = getRoundIds(matchId);
		Set<Integer> archerIds = getArcherIds(matchId);
		
		boolean resultOk = true;
		for(int roundId : roundIds){
			resultOk &= removeRound(roundId);
		}
		
		resultOk &= removeArcherAssignments(matchId);
		
		if(resultOk){
			SQLiteDatabase sd = getWritableDatabase();
			String[] whereArgs = new String[]{String.valueOf(matchId)};
			int result = sd.delete(Matches.TABLE_NAME, Matches.ID + " = ?", whereArgs);
			resultOk &= (result > 0);
		}
		
		if(resultOk){
			resultOk &= removeArchers(archerIds);
		}
		
		return resultOk;
	}
	
	
	private boolean removeRound(int roundId){
		Set<Integer> passesIds = getPasseIds(roundId);
		
		boolean resultOk = true;
		for(int passeId : passesIds){
			resultOk &= removePasse(passeId);
		}
		
		if(resultOk){
			SQLiteDatabase sd = getWritableDatabase();
			String[] whereArgs = new String[]{String.valueOf(roundId)};
			int result = sd.delete(Rounds.TABLE_NAME, Rounds.ID + " = ?", whereArgs);
			resultOk &= (result > 0);
		}
		
		return resultOk;
	}
	
	
	private boolean removeArcherAssignments(int matchId){
		SQLiteDatabase sd = getWritableDatabase();
		String[] whereArgs = new String[]{String.valueOf(matchId)};
		int result = sd.delete(ArcherToMatches.TABLE_NAME, ArcherToMatches.MATCH_ID + " = ?", whereArgs);
		return (result > 0);
	}
	
	
	private boolean removePasse(int passeId){
		Set<Integer> arrowIds = getArrowIds(passeId);
		
		boolean resultOk = true;
		for(int arrowId : arrowIds){
			resultOk &= removeArrow(arrowId);
		}
		
		if(resultOk){
			SQLiteDatabase sd = getWritableDatabase();
			String[] whereArgs = new String[]{String.valueOf(passeId)};
			int result = sd.delete(Passes.TABLE_NAME, Passes.ID + " = ?", whereArgs);
			resultOk &= (result > 0);
		}
		
		return resultOk;
	}
	
	
	private boolean removeArrow(int arrowId){
		SQLiteDatabase sd = getWritableDatabase();
		String[] whereArgs = new String[]{String.valueOf(arrowId)};
		int result = sd.delete(Arrows.TABLE_NAME, Arrows.ID + " = ?", whereArgs);
		
		return (result > 0);
	}
	
	
	private boolean removeArchers(Set<Integer> archerIds){
		boolean result = true;
		
		for(int archerId : archerIds){
			result &= removeArcher(archerId);
		}
		return result;
	}
	
	
	private boolean removeArcher(int archerId){
		SQLiteDatabase sd = getWritableDatabase();
		String[] whereArgs = new String[]{String.valueOf(archerId)};
		int result = sd.delete(Archers.TABLE_NAME, Archers.ID + " = ?", whereArgs);
		
		return (result > 0);
	}
	
	
	public Set<Integer> getArcherIds(int matchId){
		SQLiteDatabase sd = getReadableDatabase();
		
		String[] cols = new String[]{ArcherToMatches.ARCHER_ID};
		String[] selectionArgs = new String[]{String.valueOf(matchId)};
		Cursor c = sd.query(ArcherToMatches.TABLE_NAME, cols, ArcherToMatches.MATCH_ID + 
				" = ?", selectionArgs, null, null, null);
		
		Set<Integer> archerIds = new HashSet<Integer>();
		while(c.moveToNext()){
			int id = c.getInt(c.getColumnIndex(ArcherToMatches.ARCHER_ID));
			archerIds.add(id);
		}
		
		c.close();
		
		return archerIds;
	}
	
	
	public Set<Integer> getRoundIds(int matchId){
		SQLiteDatabase sd = getReadableDatabase();
		
		String[] cols = new String[]{Rounds.ID};
		String[] selectionArgs = new String[]{String.valueOf(matchId)};
		Cursor c = sd.query(Rounds.TABLE_NAME, cols, Rounds.P_MATCH_ID + 
				" = ?", selectionArgs, null, null, null);
		
		Set<Integer> archerIds = new HashSet<Integer>();
		while(c.moveToNext()){
			int id = c.getInt(c.getColumnIndex(Rounds.ID));
			archerIds.add(id);
		}
		
		c.close();
		
		return archerIds;
	}

	
	public Set<Integer> getPasseIds(int roundId){
		SQLiteDatabase sd = getReadableDatabase();
		
		String[] cols = new String[]{Passes.ID};
		Set<Integer> passeIds = new HashSet<Integer>();
		
		String[] selectionArgs = new String[]{String.valueOf(roundId)};
		Cursor c = sd.query(Passes.TABLE_NAME, cols, 
				Passes.P_ROUND_ID + " = ?", 
				selectionArgs, null, null, null);

		while(c.moveToNext()){
			int id = c.getInt(c.getColumnIndex(Passes.ID));
			passeIds.add(id);
		}
		
		c.close();
		
		return passeIds;
	}
	
	
	public Set<Integer> getArrowIds(int passeId){
		SQLiteDatabase sd = getReadableDatabase();
		
		String[] cols = new String[]{Arrows.ID};
		Set<Integer> arrowIds = new HashSet<Integer>();
		
		String[] selectionArgs = new String[]{String.valueOf(passeId)};
		Cursor c = sd.query(Arrows.TABLE_NAME, cols, 
				Arrows.P_PASSE_ID + " = ?", 
				selectionArgs, null, null, null);

		while(c.moveToNext()){
			int id = c.getInt(c.getColumnIndex(Arrows.ID));
			arrowIds.add(id);
		}
		
		c.close();
		
		return arrowIds;
	}
	
	
	public static Calendar getCalendar(int secondsSinceEpoc){
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(secondsSinceEpoc * 1000); // need milliseconds
		return cal;
	}
	
	
	public static String sprintCalendar(Calendar cal){
		SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
		return df.format(cal.getTime());
	}

}
