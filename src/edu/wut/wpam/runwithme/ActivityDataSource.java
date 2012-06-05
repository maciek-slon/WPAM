package edu.wut.wpam.runwithme;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class ActivityDataSource {

	// Database fields
	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;
	private String[] allColumns = { MySQLiteHelper.COLUMN_ID,
			MySQLiteHelper.COLUMN_DATE, MySQLiteHelper.COLUMN_SUMMARY };

	public ActivityDataSource(Context context) {
		dbHelper = new MySQLiteHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public RunActivity createRunActivity(long date, long summary) {
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.COLUMN_DATE, date);
		values.put(MySQLiteHelper.COLUMN_SUMMARY, summary);
		long insertId = database.insert(MySQLiteHelper.TABLE_ACTIVITIES, null, values);
		Cursor cursor = database.query(MySQLiteHelper.TABLE_ACTIVITIES,
				allColumns, MySQLiteHelper.COLUMN_ID + " = " + insertId, null,
				null, null, null);
		cursor.moveToFirst();
		RunActivity newActivity = cursorToActivity(cursor);
		cursor.close();
		return newActivity;
	}

	public void deleteActivity(RunActivity act) {
		long id = act.getId();
		//System.out.println("Comment deleted with id: " + id);
		database.delete(MySQLiteHelper.TABLE_ACTIVITIES, MySQLiteHelper.COLUMN_ID
				+ " = " + id, null);
	}

	public List<RunActivity> getAllComments() {
		List<RunActivity> acts = new ArrayList<RunActivity>();

		Cursor cursor = database.query(MySQLiteHelper.TABLE_ACTIVITIES,
				allColumns, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			RunActivity act = cursorToActivity(cursor);
			acts.add(act);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return acts;
	}

	private RunActivity cursorToActivity(Cursor cursor) {
		RunActivity act = new RunActivity();
		act.setId(cursor.getLong(0));
		act.setDate(cursor.getLong(1));
		act.setSummary(cursor.getLong(2));
		return act;
	}
}
