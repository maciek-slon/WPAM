package edu.wut.wpam.runwithme;

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
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

public class MonitorActivity extends TabActivity {
	private TabHost mTabHost;
	
	private Timer myTimer;
	IntervalWorkout workout = new IntervalWorkout();
	MyLocationListener myLocationListener;

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
				.setIndicator("Bieg", getResources().getDrawable(R.drawable.ic_run))
				.setContent(R.id.tab1));
		mTabHost.addTab(mTabHost
				.newTabSpec("tab_test2")
				.setIndicator("Rolki", getResources().getDrawable(R.drawable.ic_skate))
				.setContent(R.id.tab2));
		mTabHost.addTab(mTabHost
				.newTabSpec("tab_test3")
				.setIndicator("Rower", getResources().getDrawable(R.drawable.ic_bike))
				.setContent(R.id.tab3));
		mTabHost.setCurrentTab(0);

		// ---------------------------------------------
		// Setup GPS
		// ---------------------------------------------
		LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		myLocationListener = new MyLocationListener();
		mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, myLocationListener);
		
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
		
		
		TextView tv = (TextView)findViewById(R.id.textview3);
		tv.setText("READY!");
	}
	
	@Override
	public void onDestroy() {
		LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		mlocManager.removeUpdates(myLocationListener);
		super.onDestroy();
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
		Location last;
		long starttime;
		
		public void onLocationChanged(Location loc)
		{
			float dist = 0;
			float spd = 0;
			float dt;
			
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
				String Text = "" + dist + "m/" + dt + "s = " + spd + "km/h";
				TextView tv = (TextView)findViewById(R.id.textview3);
				tv.setText(Text);
								
				Plot plot = (Plot) findViewById(R.id.simplePlot);
				plot.add(new PointF(dt, spd));
				plot.invalidate();
			}

		}
		
		public void onProviderDisabled(String provider)
		{
			Toast.makeText(getApplicationContext(),"Gps Disabled", Toast.LENGTH_SHORT).show();
		}
		
		public void onProviderEnabled(String provider)
		{
			Toast.makeText(getApplicationContext(), "Gps Enabled", Toast.LENGTH_SHORT).show();
		}
		
		public void onStatusChanged(String provider, int status, Bundle extras)
		{
		}
	}/* End of Class MyLocationListener */
}