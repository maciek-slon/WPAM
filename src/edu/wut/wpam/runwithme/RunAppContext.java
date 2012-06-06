package edu.wut.wpam.runwithme;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import android.R.bool;
import android.app.Application;
import android.content.Context;
import android.view.View;

public class RunAppContext {
	private static RunAppContext runAppContext = null;

	private ActivityDataSource datasource;
	
	private boolean is_initialized = false;
	
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
		datasource.open();
		runActivity = datasource.createRunActivity(System.currentTimeMillis(), 0);
		distance = 0;
		speed = 0;
		is_initialized = true;
		datasource.close();
	}
	
	public void finish() {
		if (is_initialized) {
			// save everything to file
			datasource.open();
			runActivity.setSummary(11);
			datasource.updateActivity(runActivity);
			datasource.close();
		}
		
		is_initialized = false;
		context = null;
		runActivity = null;
		listeners = null;
		runAppContext = null;
	}
	
	public boolean initialized() {
		return this.is_initialized;
	}
	
	private float speed;

	private float distance;
	
	public void addTrackPoint(TrackPoint pt) {
		//track.add(pt);
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
	
	public void save() throws IOException {
		//String FILENAME = String.valueOf(timestamp);
		//String data = "";
		//FileOutputStream fos = context.openFileOutput(FILENAME, Context.MODE_PRIVATE);
		//fos.write(data.getBytes());
		//fos.close();
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
	
	
}
