package de.psp24.alleinsgold.data;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

public class GoldDataProvider extends ContentProvider {
	
	private static final String AUTHORITY = "de.psp24.alleinsgold.data.GoldDataProvider";
	public static final int MATCHES   = 100;
	public static final int MATCH_ID  = 110;
	public static final int ARCHERS   = 100;
	public static final int ARCHER_ID = 110;
	
	
	
	private static final String MATCHES_BASE_PATH = "matches";
	public static final Uri MATCHES_CONTENT_URI   = Uri.parse("content://" + AUTHORITY + "/" + MATCHES_BASE_PATH);
	//public static final String CONTENT_ITEM_TYPE  = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/mt-tutorial";
	//public static final String CONTENT_TYPE       = ContentResolver.CURSOR_DIR_BASE_TYPE  + "/mt-tutorial";

	
	// Register the Content Provider
	private static final UriMatcher sURIMatcher = new UriMatcher( UriMatcher.NO_MATCH);
	static {
	    sURIMatcher.addURI(AUTHORITY, MATCHES_BASE_PATH,        MATCHES);
	    sURIMatcher.addURI(AUTHORITY, MATCHES_BASE_PATH + "/#", MATCH_ID);
	}
	
	private SchemaHelper mSchemaHelper;
	
	@Override
	public boolean onCreate() {
		mSchemaHelper = new SchemaHelper(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		Cursor cursor = null;
		int uriType = sURIMatcher.match(uri);
	    switch (uriType) {
	    case MATCH_ID:
	    	String matchId = uri.getLastPathSegment();
	        cursor =  mSchemaHelper.getMatch(Long.parseLong(matchId));
	        break;
	    case MATCHES:
	        cursor = mSchemaHelper.getMatches();
	        break;
	    default:
	        throw new IllegalArgumentException("Unknown URI");
	    }
	    return cursor;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

}
