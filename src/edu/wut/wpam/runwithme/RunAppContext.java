package edu.wut.wpam.runwithme;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.content.Context;
import android.view.View;

public class RunAppContext {
	private static RunAppContext runAppContext = null;

	private ActivityDataSource datasource;
	
	private boolean is_initialized = false;
	private boolean is_static = false;
	
	private static ArrayList<View> listeners = new ArrayList<View>();
	
	private Context context;

	private RunActivity runActivity;
	
	private RunAppContext() {
	} 

	public static RunAppContext instance() {
		if (runAppContext == null) { 
			runAppContext = new RunAppContext(); 
		} 
		return runAppContext;
	}
	
	public void setContext(Context ctx) {
		context = ctx;
		datasource = new ActivityDataSource(context);
	}
	
	public void init() {
		listeners = new ArrayList<View>();
		datasource.open();
		runActivity = datasource.createRunActivity(System.currentTimeMillis(), -1);
		distance = 0;
		speed = 0;
		is_initialized = true;
		is_static = false;
		datasource.close();
	}
	
	public void initFromFile(RunActivity act) {
		listeners = new ArrayList<View>();
		datasource.open();
		runActivity = act;
		distance = act.getSummary();
		speed = 0;
		is_initialized = true;
		datasource.close();
		is_static = true;
		try {
			loadTrack(act.getDate());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void finish() {
		if (is_initialized) {
			saveCurrentActivity();
		}
		
		is_initialized = false;
		is_static = false;
		context = null;
		runActivity = null;
		listeners = null;
		runAppContext = null;
	}

	public void saveCurrentActivity() {
		// save updated activity to database
		datasource.open();
		runActivity.setSummary((long) distance);
		datasource.updateActivity(runActivity);
		datasource.close();
		
		// save track to file
		try {
			saveTrack(runActivity.getDate());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public boolean initialized() {
		return this.is_initialized;
	}
	
	private float speed;

	private float distance;
	
	public void addTrackPoint(TrackPoint pt) {
		runActivity.getTrack().add(pt);
		notifyUI();
	}
	
	private void notifyUI() {
		for(View view : listeners) {
			view.invalidate();
		}
	}
	
	public void addListener(View view) {
		listeners.add(view);
	}
	
	public void removeListener(View view) {
		listeners.remove(view);
	}
	
	public void saveTrack(long timestamp) throws IOException {
		String FILENAME = "" + timestamp;
		String data = "";

		if (getTrack() == null)
			return;
		
		FileOutputStream fos = context.openFileOutput(FILENAME, Context.MODE_PRIVATE);
		for (TrackPoint pt : getTrack()) {
			data = "" + pt.tim + ";" + pt.lat + ";" + pt.lon + ";" + pt.alt + "\n";
			fos.write(data.getBytes());
		}
		fos.close();
	}
	
	public void loadTrack(long timestamp) throws IOException {
		String FILENAME = "" + timestamp;
		String data = "";

		FileInputStream fis = context.openFileInput(FILENAME);
		BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
		
		do {
			data = reader.readLine();
			if (data == null) break;
			//// TODO: sprawdzanie poprawności danych
			String[] nums = data.split(";");
			TrackPoint tp = new TrackPoint(0, 0, 0, 0);
			tp.tim = Long.parseLong(nums[0]);
			tp.lat = Integer.parseInt(nums[1]);
			tp.lon = Integer.parseInt(nums[2]);
			tp.alt = Integer.parseInt(nums[3]);
			addTrackPoint(tp);
		} while(true);

		fis.close();
	}
	
	public float getSpeed() {
		return speed;
	}
	
	public void setSpeed(float sp) {
		speed = sp;
	}
	
	public float getDistance() {
		return distance;
	}
	
	public void addDistance(float dd) {
		distance += dd;
	}
	
	
	public float getTime() {
		return 0.001f * (System.currentTimeMillis() - runActivity.getDate());
	}
	
	public Workout getActiveWorkout() {
		return null;
	}
	
	public ArrayList<TrackPoint> getTrack() {
		return runActivity.getTrack();
	}

	public boolean isStatic() {
		return is_static;
	}
	
}
