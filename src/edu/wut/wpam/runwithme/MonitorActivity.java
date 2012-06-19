package edu.wut.wpam.runwithme;

import java.util.ArrayList;
import java.util.Date;
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
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

public class MonitorActivity extends TabActivity {
	private TabHost mTabHost;

	private Workout workout;

	MediaPlayer mp = null;
	private ArrayAdapter<Workout> adapter;
	
	private Timer myTimer;
	MyLocationListener myLocationListener;
	

	private LayoutInflater mInflater;
	ListView list;

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
		
		mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

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
 
		
		ArrayList<Workout> workouts = context.getActivity().workouts;
		//Set up the results list, see BNYAdapter
        list = (ListView)findViewById(R.id.lvWorkouts);
        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, napisy);
        adapter = new ArrayAdapter<Workout>(this, R.layout.list_item, workouts) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View row;

				if (null == convertView) {
					row = mInflater.inflate(R.layout.list_item_workout, null);
				} else {
					row = convertView;
				}

				TextView tv = (TextView) row.findViewById(R.id.tvWorkout);
				tv.setText(getItem(position).getName());
				tv = (TextView) row.findViewById(R.id.tvSummary);
				tv.setText(getItem(position).getSummary());
				ProgressBar pb = (ProgressBar) row.findViewById(R.id.pbProgress);
				

				if (getItem(position).finished()) {
					pb.setProgress(100);
				} else {
					pb.setProgress((int) (100 * getItem(position).getProgress()));
				}

				
				return row;
			}
		};
        list.setAdapter(adapter);
		
		
		
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

		}, 0, 250);

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
		myTimer.cancel();
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
			if (context != null) {
				context.getActivity().update();
				adapter.notifyDataSetChanged();
				
				int snd_id = context.getActivity().getSound(); 
				if (snd_id > 0) {
					System.out.println(snd_id);
					// play sounds according to progress of workout
					// 1 - intermediate
					// 2 - finished
					// 3 - countdown
					switch (snd_id) {
					case 1: 
						mp = MediaPlayer.create(MonitorActivity.this, R.raw.beep);
						break;
					case 2:
						mp = MediaPlayer.create(MonitorActivity.this, R.raw.checkpoint);
						break;
					case 3:
						mp = MediaPlayer.create(MonitorActivity.this, R.raw.beeps);
						break;
					}
					
			        mp.start();
			        mp.setOnCompletionListener(new OnCompletionListener() {
			            public void onCompletion(MediaPlayer mp) {
			                // TODO Auto-generated method stub
			                mp.release();
			            }
			        });   
				}
			}
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
				
				plot = (Plot) findViewById(R.id.elevationPlot);
				plot.add(new PointF(context.getTime(), (float)loc.getAltitude()));
				plot.invalidate();
				
				TextView tv = (TextView) findViewById(R.id.tvCurSpeed);
				tv.setText(String.format("%.2f km/h", spd));
				
				tv = (TextView) findViewById(R.id.tvCurElev);
				tv.setText(String.format("%.0f mnpm", (float)loc.getAltitude()));

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