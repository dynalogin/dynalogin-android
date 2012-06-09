package org.dynalogin.android;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ProfileStore {
	
	public static final String  KEY_ROWID        = "_id";
	public static final String  KEY_PROF_NAME    = "name";
	public static final String  KEY_SECRET       = "secret";
	public static final String  KEY_SEQ          = "seq";
	
	private static final String DB_NAME          = "dynalogin";
	private static final int DB_VERSION          = 1;
	private static final String DB_TABLE_PROFILE = "profile";
	private static final String DB_TABLE_PROFILE_DDL =
		"create table profile ("
			+ KEY_ROWID + " integer primary key autoincrement, "
			+ KEY_PROF_NAME + " text not null, "
			+ KEY_SECRET + " text not null, "
			+ KEY_SEQ + " integer not null)";
	private final Context context;
	private DatabaseHelper DBHelper;
	private SQLiteDatabase db;

	public ProfileStore(Context ctx) {
		this.context = ctx;
		DBHelper = new DatabaseHelper(context);
	}

	private static class DatabaseHelper extends SQLiteOpenHelper
	{
		DatabaseHelper(Context context) {
			super(context, DB_NAME, null, DB_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(DB_TABLE_PROFILE_DDL);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		}
	}

	public void reCreate() {
		db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_PROFILE);
		db.execSQL(DB_TABLE_PROFILE_DDL);
	}

	// ---opens the database---
	public ProfileStore open() throws SQLException {
		db = DBHelper.getWritableDatabase();
		return this;
	}

	// ---closes the database---
	public void close() {
		DBHelper.close();
	}

	// ---insert a title into the database---
	public long insertProfile(String prof_name, String secret) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_PROF_NAME, prof_name);
		initialValues.put(KEY_SECRET, secret);
		initialValues.put(KEY_SEQ, 0);
		return db.insert(DB_TABLE_PROFILE, null, initialValues);
	}

	// ---deletes a particular title---
	public boolean deleteProfile(int rowId)	{
		return db.delete(DB_TABLE_PROFILE, KEY_ROWID + "=" + rowId, null) > 0;
	}

	public void deleteAllProfiles()	{
		db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_PROFILE);
		db.execSQL(DB_TABLE_PROFILE_DDL);
	}

	public Cursor getAllProfiles() {
		return db.query(DB_TABLE_PROFILE,
				new String[] { KEY_ROWID,
				KEY_PROF_NAME, KEY_SECRET, KEY_SEQ },
				null, null,	null, null, null);
	}

	// ---retrieves a particular title---
	public Cursor getProfile(int rowId) throws SQLException	{
		Cursor mCursor = db.query(true, DB_TABLE_PROFILE,
				new String[] { KEY_ROWID, KEY_PROF_NAME, KEY_SECRET, KEY_SEQ },
				KEY_ROWID + "=" + rowId, null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	// ---updates a title---
	public boolean updateProfile(int rowId, String prof_name, String secret, int seq) {
		ContentValues args = new ContentValues();
		args.put(KEY_PROF_NAME, prof_name);
		args.put(KEY_SECRET, secret);
		args.put(KEY_SEQ, seq);
		return db.update(DB_TABLE_PROFILE, args, KEY_ROWID + "=" + rowId, null) > 0;
	}

	public boolean updateCount(int rowId, int seq) {
		ContentValues args = new ContentValues();
		args.put(KEY_SEQ, seq);
		return db.update(DB_TABLE_PROFILE, args, KEY_ROWID + "=" + rowId, null) > 0;
	}
}
