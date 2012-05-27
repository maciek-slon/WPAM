package edu.wut.wpam.runwithme;

import java.util.ArrayList;

import android.view.View;

public class RunAppContext {
	private static RunAppContext runAppContext = null;
	
	private static ArrayList<View> listeners = new ArrayList<View>();

	private RunAppContext() {
		track = new ArrayList<TrackPoint>(30);
	}

	public static RunAppContext instance() {
		if (runAppContext == null) { 
			runAppContext = new RunAppContext(); 
		} 
		return runAppContext;
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
	
}
