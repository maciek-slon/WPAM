package edu.wut.wpam.runwithme;

import java.io.FileNotFoundException;
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

	private RunAppContext() {
		track = new ArrayList<TrackPoint>(30);
		timestamp = System.currentTimeMillis();
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
	
}
