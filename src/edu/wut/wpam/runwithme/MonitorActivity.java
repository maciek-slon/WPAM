package edu.wut.wpam.runwithme;

import java.util.Timer;
import java.util.TimerTask;

import edu.wut.wpam.widgets.Plot;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PointF;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.format.Time;
import android.view.View;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

public class MonitorActivity extends TabActivity {
	private TabHost mTabHost;

	private Workout workout;
	
	private Timer myTimer;
	MyLocationListener myLocationListener;

	private RunAppContext context = RunAppContext.instance();
	
	private void setButtonActivity(int id, final Class<?> cls) {
		Button btn = (Button) findViewById(id);
        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {                
                Intent myIntent = new Intent(MonitorActivity.this, cls);
                startActivity(myIntent);
            }
        });
	}
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		loadBundle(savedInstanceState);

		// ---------------------------------------------
		// Setup context
		// ---------------------------------------------
		if (!context.initialized() || context.isStatic()) {
			context.init();
		}
		
		// ---------------------------------------------
		// Setup tabs
		// ---------------------------------------------
		mTabHost = getTabHost();
		mTabHost.addTab(mTabHost
				.newTabSpec("tab_test1")
				.setIndicator("Bieg",
						getResources().getDrawable(R.drawable.ic_run))
				.setContent(R.id.tab1));
		mTabHost.addTab(mTabHost
				.newTabSpec("tab_test2")
				.setIndicator("Treningi",
						getResources().getDrawable(R.drawable.ic_skate))
				.setContent(R.id.tab2));
		mTabHost.addTab(mTabHost
				.newTabSpec("tab_test3")
				.setIndicator("Rower",
						getResources().getDrawable(R.drawable.ic_bike))
				.setContent(R.id.tab3));
		mTabHost.setCurrentTab(0);

		// ---------------------------------------------
		// Setup GPS
		// ---------------------------------------------
		LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		myLocationListener = new MyLocationListener();
		mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
				myLocationListener);

		// ---------------------------------------------
		// Setup timer
		// ---------------------------------------------
		myTimer = new Timer();
		myTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				TimerMethod();
			}

		}, 0, 1000);

		// ---------------------------------------------
		// Setup workout
		// ---------------------------------------------
		Time now = new Time();
		now.setToNow();

		workout = context.getActiveWorkout();
		if (workout != null) {
		/*	workout.setDay(5);
			workout.setStart(now);
			Plot plot = (Plot) findViewById(R.id.simplePlot);
			plot.setXEnd(workout.getLength());
			plot.setIntervals(workout.getIntervals());*/
		}

		setButtonActivity(R.id.btnShowMap, RouteActivity.class);
		setButtonActivity(R.id.btnAddWorkout, IntervalPickerActivity.class);
	}

	@Override
	public void onDestroy() {
		LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		mlocManager.removeUpdates(myLocationListener);
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

	private void TimerMethod() {
		// This method is called directly by the timer
		// and runs in the same thread as the timer.

		// We call the method that will work with the UI
		// through the runOnUiThread method.
		this.runOnUiThread(Timer_Tick);
	}

	private Runnable Timer_Tick = new Runnable() {
		public void run() {
			// This method runs in the same thread as the UI.
//			workout.update(System.currentTimeMillis());
//			Plot plot = (Plot) findViewById(R.id.simplePlot);
//			plot.setMarker(workout.getTime());
//			plot.invalidate();
		}
	};

	/* Class My Location Listener */
	public class MyLocationListener implements LocationListener {
		Location last;
		long starttime;

		public void onLocationChanged(Location loc) {
			float dist = 0;
			float spd = 0;
			float dt;
			
			TrackPoint tp;

			long tm = System.currentTimeMillis();

			if (last == null) {
				last = loc;
				starttime = tm;
			} else if (tm - starttime > 5000) {
				dt = 0.001f * (tm - starttime);
				dist = last.distanceTo(loc);
				spd = 3.6f * dist / dt;
				starttime = tm;
				last = loc;

				tp = new TrackPoint(
						(int) (loc.getLatitude() * 1e6), 
						(int) (loc.getLongitude() * 1e6), 
						(int) (loc.getAltitude()), tm);
				
				if (workout != null)
					workout.update(tp);
				
				Plot plot = (Plot) findViewById(R.id.simplePlot);
				plot.add(new PointF(context.getTime(), spd));
				plot.invalidate();

				context.addTrackPoint(tp);
				context.addDistance(dist);
				context.setSpeed(spd);
			}

		}

		public void onProviderDisabled(String provider) {
			Toast.makeText(getApplicationContext(), "Gps Disabled",
					Toast.LENGTH_SHORT).show();
		}

		public void onProviderEnabled(String provider) {
			Toast.makeText(getApplicationContext(), "Gps Enabled",
					Toast.LENGTH_SHORT).show();
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	}/* End of Class MyLocationListener */
}