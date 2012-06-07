package edu.wut.wpam.runwithme;

import java.util.List;

import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class TestDatabaseActivity extends ListActivity {
	private ActivityDataSource datasource;
	private LayoutInflater mInflater;
	List<RunActivity> values;
	AlertDialog.Builder builder;
	RunActivity act;
	ArrayAdapter<RunActivity> adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.database);

		builder = new AlertDialog.Builder(this);
		builder.setTitle("Zakończyć bieg?")
				.setMessage(
						"Wybranie tej opcji spowoduje zakończenie bieżącego biegu.")
				.setIcon(android.R.drawable.ic_dialog_alert);

		mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		datasource = new ActivityDataSource(this);
		datasource.open();
		values = datasource.getAllActivities();
		datasource.close();

		// hide all entries with negative length (i.e. current one)
		for (RunActivity act : values) {
			if (act.getSummary() < 0) {
				values.remove(act);
			}
		}

		// Use the SimpleCursorAdapter to show the elements in a ListView
		// ArrayAdapter<RunActivity> adapter = new
		// ArrayAdapter<RunActivity>(this, android.R.layout.simple_list_item_1,
		// values);
		// setListAdapter(adapter);
		adapter = new ArrayAdapter<RunActivity>(this, R.layout.list_item, values) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View row;

				if (null == convertView) {
					row = mInflater.inflate(R.layout.list_item, null);
				} else {
					row = convertView;
				}

				TextView tv = (TextView) row.findViewById(R.id.tvDate);
				tv.setText(getItem(position).toString());
				tv = (TextView) row.findViewById(R.id.tvDist);
				tv.setText("" + getItem(position).getSummary() * 0.001 + "km");

				return row;
			}
		};

		setListAdapter(adapter);

		ListView listView = (ListView) findViewById(android.R.id.list);
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				act = values.get(position);
				if (RunAppContext.instance().initialized()) {
					builder.setPositiveButton("Yes",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									// Yes button clicked, do something
									TestDatabaseActivity.this.showLog(true);
								}
							}).setNegativeButton("No", null);
					builder.show();
				} else {
					showLog(false);
				}
			}
		});
	}

	public void showLog(boolean dofinish) {
		if (dofinish) {
			RunAppContext.instance().saveCurrentActivity();
		}

		RunAppContext.instance().initFromFile(act);
		Intent myIntent = new Intent(this, MonitorActivity.class);
		startActivityForResult(myIntent, 0);
	}

	// Will be called via the onClick attribute
	// of the buttons in main.xml
	public void onClick(View view) {
		@SuppressWarnings("unchecked")
		ArrayAdapter<RunActivity> adapter = (ArrayAdapter<RunActivity>) getListAdapter();
		RunActivity act = null;
		switch (view.getId()) {
		case R.id.add:
			datasource.open();
			// Save the new comment to the database
			act = datasource.createRunActivity(System.currentTimeMillis(), 523);
			adapter.add(act);
			datasource.close();
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
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		if (v.getId() == android.R.id.list) {
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
			menu.setHeaderTitle("Test");
			String[] menuItems = getResources()
					.getStringArray(R.array.act_menu);
			for (int i = 0; i < menuItems.length; i++) {
				menu.add(Menu.NONE, i, i, menuItems[i]);
			}
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		int menuItemIndex = item.getItemId();
		String[] menuItems = getResources().getStringArray(R.array.act_menu);
		String menuItemName = menuItems[menuItemIndex];
		RunActivity act = values.get(info.position);

		if (menuItemName == "Usuń") {
			datasource.open();
			datasource.deleteActivity(act);
			adapter.remove(act);
			datasource.close();
		}

		return true;
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

}