package edu.wut.wpam.runwithme;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class AchievementDataSource {

	// Database fields
	private SQLiteDatabase database;
	private SQLiteHelperAchievements dbHelper;
	private String[] allColumns = { 
			SQLiteHelperAchievements.COLUMN_ID,
			SQLiteHelperAchievements.COLUMN_NAME,
			SQLiteHelperAchievements.COLUMN_DESC, 
			SQLiteHelperAchievements.COLUMN_VALUE };

	public AchievementDataSource(Context context) {
		dbHelper = new SQLiteHelperAchievements(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public Achievement createAchievement(String name, String desc, long value) {
		ContentValues values = new ContentValues();
		values.put(SQLiteHelperAchievements.COLUMN_NAME, name);
		values.put(SQLiteHelperAchievements.COLUMN_DESC, desc);
		values.put(SQLiteHelperAchievements.COLUMN_VALUE, value);
		
		long insertId = database.insert(SQLiteHelperAchievements.TABLE, null, values);
		Cursor cursor = database.query(SQLiteHelperAchievements.TABLE,
				allColumns, SQLiteHelperAchievements.COLUMN_ID + " = " + insertId, null,
				null, null, null);
		cursor.moveToFirst();
		Achievement newAch = cursorToAchievement(cursor);
		cursor.close();
		return newAch;
	}

	public void deleteAchievement(Achievement act) {
		long id = act.id;
		//System.out.println("Comment deleted with id: " + id);
		database.delete(SQLiteHelperAchievements.TABLE, 
				SQLiteHelperAchievements.COLUMN_ID + " = " + id, null);
	}
	
	public void updateAhievement(Achievement act) {
		long id = act.id;
		ContentValues values = new ContentValues();
		values.put(SQLiteHelperAchievements.COLUMN_NAME, act.name);
		values.put(SQLiteHelperAchievements.COLUMN_DESC, act.desc);
		values.put(SQLiteHelperAchievements.COLUMN_VALUE, act.value);
		database.update(SQLiteHelperAchievements.TABLE, values, SQLiteHelperAchievements.COLUMN_ID + " = " + id, null);
	}

	public List<Achievement> getAllAchievements() {
		List<Achievement> acts = new ArrayList<Achievement>();

		Cursor cursor = database.query(SQLiteHelperAchievements.TABLE,
				allColumns, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Achievement act = cursorToAchievement(cursor);
			acts.add(act);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return acts;
	}

	private Achievement cursorToAchievement(Cursor cursor) {
		Achievement act = new Achievement();
		act.id = cursor.getLong(0);
		act.name = cursor.getString(1);
		act.desc = cursor.getString(2);
		act.value = cursor.getLong(3);
		return act;
	}
}
