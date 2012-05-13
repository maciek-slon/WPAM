package edu.wut.wpam.runwithme;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import edu.wut.wpam.widgets.Plot;
import android.app.TabActivity;
import android.content.Context;
import android.graphics.PointF;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.widget.TabHost;
import android.widget.Toast;

public class RunWithMeActivity extends TabActivity {
	private TabHost mTabHost;
	
	private Timer myTimer;
	IntervalWorkout workout = new IntervalWorkout();

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// ---------------------------------------------
		// Setup tabs
		// ---------------------------------------------
		mTabHost = getTabHost();
		mTabHost.addTab(mTabHost
				.newTabSpec("tab_test1")
				.setIndicator("TAB 1", getResources().getDrawable(R.drawable.ic_tab_artist))
				.setContent(R.id.tab1));
		mTabHost.addTab(mTabHost
				.newTabSpec("tab_test2")
				.setIndicator("TAB 2", getResources().getDrawable(R.drawable.ic_tab_artist))
				.setContent(R.id.tab2));
		mTabHost.addTab(mTabHost
				.newTabSpec("tab_test3")
				.setIndicator("TAB 3", getResources().getDrawable(R.drawable.ic_tab_artist))
				.setContent(R.id.tab3));
		mTabHost.setCurrentTab(0);

		// ---------------------------------------------
		// Setup GPS
		// ---------------------------------------------
		LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new MyLocationListener());
		
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
		
		workout.setDay(5);
		workout.setStart(now);
		Plot plot = (Plot) findViewById(R.id.simplePlot);
		plot.setXEnd(workout.getLength());
		plot.setIntervals(workout.getIntervals());
	}

	public void onDestroy() {
		LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
	}
	
	
	
	
	
	private void TimerMethod()
	{
		//This method is called directly by the timer
		//and runs in the same thread as the timer.

		//We call the method that will work with the UI
		//through the runOnUiThread method.
		this.runOnUiThread(Timer_Tick);
	}

	private Runnable Timer_Tick = new Runnable() {
		public void run() {
		//This method runs in the same thread as the UI.  
			Time now = new Time();
			now.setToNow();  	       
			workout.incrementTime(now);
			Plot plot = (Plot) findViewById(R.id.simplePlot);
			plot.setMarker(workout.getTime());
			plot.invalidate();
		}
	};
	
	
	
	
	
	/* Class My Location Listener */
	public class MyLocationListener implements LocationListener
	{
		private static final String TAG = "MyLocationListener";
		Location last;
		long starttime;
		
		public void onLocationChanged(Location loc)
		{
			float dist = 0;
			int time = 0;
			float spd = 0;
			
			if (last != null) {
				dist = last.distanceTo(loc);
				time = (int) (loc.getTime() - last.getTime());
				spd = dist / time * 3.6f;
			} else {
				starttime = loc.getTime();
			}
			last = loc;

			String Text = "" + dist + "m/" + time * 0.001 + "s = " + spd + "km/h";
			Toast.makeText(getApplicationContext(),	Text, Toast.LENGTH_SHORT).show();
			
			Log.i(TAG, "onLocationChanged()");
			Date theTimeStamp = new Date(loc.getTime());
			Log.i(TAG, "Current  TimeStamp: " + new Date().toString());
			Log.i(TAG, "Location TimeStamp: " + theTimeStamp.toString());
			
			Plot plot = (Plot) findViewById(R.id.simplePlot);
			plot.add(new PointF((float)(loc.getTime() - starttime)/1000.0f, spd));
			plot.invalidate();
		}
		
		public void onProviderDisabled(String provider)
		{
			Toast.makeText(getApplicationContext(),
			"Gps Disabled",
			Toast.LENGTH_SHORT).show();
		}
		
		public void onProviderEnabled(String provider)
		{
			Toast.makeText(getApplicationContext(),
			"Gps Enabled",
			Toast.LENGTH_SHORT).show();
		}
		
		public void onStatusChanged(String provider, int status, Bundle extras)
		{
		}
	}/* End of Class MyLocationListener */
}