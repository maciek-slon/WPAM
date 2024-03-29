package edu.wut.wpam.runwithme;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteHelper extends SQLiteOpenHelper {

	public static final String TABLE_ACTIVITIES = "activities";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_DATE = "date";
	public static final String COLUMN_SUMMARY = "summary";

	public static final String TABLE_WORKOUTS = "workouts";
	public static final String COLUMN_LENGTH = "length";
	public static final String COLUMN_PROGRESS = "progress";
	
	
	private static final String DATABASE_NAME = "runwithme.db";
	private static final int DATABASE_VERSION = 1;

	// Database creation sql statement
	private static final String DATABASE_CREATE = "create table "
			+ TABLE_ACTIVITIES + "( " 
			+ COLUMN_ID + " integer primary key autoincrement, " 
			+ COLUMN_DATE + " integer not null, " 
			+ COLUMN_SUMMARY + " integer not null " 
			+ ");";

	public MySQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(MySQLiteHelper.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACTIVITIES);
		onCreate(db);
	}

}
