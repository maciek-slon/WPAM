package edu.wut.wpam.runwithme;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import android.app.Application;
import android.content.Context;
import android.view.View;

public class RunAppContext {
	private static RunAppContext runAppContext = null;
	
	private static ArrayList<View> listeners = new ArrayList<View>();
	
	private long timestamp = 0;

	private Context context;
	
	private ArrayList<Workout> workouts;
	
	private RunAppContext() {
		track = new ArrayList<TrackPoint>(30);
		workouts = new ArrayList<Workout>();
		timestamp = System.currentTimeMillis();
		distance = 0;
		speed = 0;
	}

	public static RunAppContext instance() {
		if (runAppContext == null) { 
			runAppContext = new RunAppContext(); 
		} 
		return runAppContext;
	}
	
	public void setContext(Context ctx) {
		context = ctx;
	}
	
	public ArrayList<TrackPoint> track;

	private float speed;

	private float distance;
	
	public void addTrackPoint(TrackPoint pt) {
		track.add(pt);
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
		String FILENAME = String.valueOf(timestamp);
		String data = "";
		FileOutputStream fos = context.openFileOutput(FILENAME, Context.MODE_PRIVATE);
		fos.write(data.getBytes());
		fos.close();
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
		return 0.001f * (System.currentTimeMillis() - timestamp);
	}
	
	public Workout getActiveWorkout() {
		return null;
	}
}
