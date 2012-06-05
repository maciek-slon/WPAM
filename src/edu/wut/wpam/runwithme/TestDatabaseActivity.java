package edu.wut.wpam.runwithme;

import java.util.List;
import java.util.Random;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;

public class TestDatabaseActivity extends ListActivity {
	private ActivityDataSource datasource;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.database);

		datasource = new ActivityDataSource(this);
		datasource.open();

		List<RunActivity> values = datasource.getAllComments();

		// Use the SimpleCursorAdapter to show the
		// elements in a ListView
		ArrayAdapter<RunActivity> adapter = new ArrayAdapter<RunActivity>(this,
				android.R.layout.simple_list_item_1, values);
		setListAdapter(adapter);
	}

	// Will be called via the onClick attribute
	// of the buttons in main.xml
	public void onClick(View view) {
		@SuppressWarnings("unchecked")
		ArrayAdapter<RunActivity> adapter = (ArrayAdapter<RunActivity>) getListAdapter();
		RunActivity act = null;
		switch (view.getId()) {
		case R.id.add:
			// Save the new comment to the database
			act = datasource.createRunActivity(System.currentTimeMillis(), 523);
			adapter.add(act);
			break;
		case R.id.delete:
			if (getListAdapter().getCount() > 0) {
				act = (RunActivity) getListAdapter().getItem(0);
				datasource.deleteActivity(act);
				adapter.remove(act);
			}
			break;
		}
		adapter.notifyDataSetChanged();
	}

	@Override
	protected void onResume() {
		datasource.open();
		super.onResume();
	}

	@Override
	protected void onPause() {
		datasource.close();
		super.onPause();
	}

}