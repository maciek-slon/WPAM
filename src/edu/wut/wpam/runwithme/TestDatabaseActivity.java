package edu.wut.wpam.runwithme;

import java.util.List;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import android.app.ListActivity;
import android.content.Context;
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
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.database);

        mInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		datasource = new ActivityDataSource(this);
		datasource.open();

		values = datasource.getAllActivities();
		
		// hide all entries with negative length (i.e. current one)
		for (RunActivity act : values) {
			if (act.getSummary() < 0) {
				values.remove(act);
			}
		}

		// Use the SimpleCursorAdapter to show the elements in a ListView
		//ArrayAdapter<RunActivity> adapter = new ArrayAdapter<RunActivity>(this, android.R.layout.simple_list_item_1, values);
		//setListAdapter(adapter);
		setListAdapter(new ArrayAdapter<RunActivity>(this, R.layout.list_item, values) {
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
		});
		
		ListView listView = (ListView)findViewById(android.R.id.list);
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
				int position, long id) {
				RunAppContext.instance().initFromFile(values.get(position));
				Intent myIntent = new Intent(view.getContext(), MonitorActivity.class);
                startActivityForResult(myIntent, 0);
			}
		});
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