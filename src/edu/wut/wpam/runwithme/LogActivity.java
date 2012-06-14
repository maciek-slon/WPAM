package edu.wut.wpam.runwithme;


import java.text.SimpleDateFormat;
import java.util.Date;

import edu.wut.wpam.widgets.Plot;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class LogActivity extends Activity {
	private RunAppContext context = RunAppContext.instance();
	
	private void setText(int id, String txt) {
		TextView view = (TextView)findViewById(id);
		view.setText(txt);
	}
	
	public void onClick(View v) {
		Intent myIntent = new Intent(this, RouteActivity.class);
		startActivityForResult(myIntent, 0);
	}
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.log);

		loadBundle(savedInstanceState);

		Button map = (Button) findViewById(R.id.btnShowMap);
		map.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				Intent myIntent = new Intent(view.getContext(),	RouteActivity.class);
				startActivityForResult(myIntent, 0);
			}
		});
		
		
		RunActivity act = context.getActivity();
		Date date = new Date(act.getDate());
		SimpleDateFormat formatter = new SimpleDateFormat("d MMMM yyyy");
		setText(R.id.logDate, formatter.format(date));
		setText(R.id.logTime, Helper.formatTime(context.getTime()));
		setText(R.id.logDist, String.format("%.2fkm", 0.001f * context.getDistance()));
		
		Plot plot = (Plot)findViewById(R.id.logSpeedPlot);
		int skip = (int) Math.ceil(0.005 * act.getTrack().size());
		plot.setElems(Helper.calculateSpeed(act.getTrack(), skip));
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		// Save UI state changes to the savedInstanceState.
		// This bundle will be passed to onCreate if the process is
		// killed and restarted.
		saveBundle(savedInstanceState);
		super.onSaveInstanceState(savedInstanceState);
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		// Restore UI state from the savedInstanceState.
		// This bundle has also been passed to onCreate.
		loadBundle(savedInstanceState);
	}

	private void saveBundle(Bundle bundle) {
	}

	private void loadBundle(Bundle bundle) {
	}
}