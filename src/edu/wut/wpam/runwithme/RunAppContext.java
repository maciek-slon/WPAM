package edu.wut.wpam.runwithme;

import java.util.ArrayList;

public class RunAppContext {
	private static RunAppContext runAppContext = null;

	private RunAppContext() {
		track = new ArrayList<TrackPoint>(30);
	}

	public static RunAppContext instance() {
		if (runAppContext == null) { 
			runAppContext = new RunAppContext(); 
		} 
		return runAppContext;
	}
	
	private ArrayList<TrackPoint> track;
	
	public void addTrackPoint(TrackPoint pt) {
		track.add(pt);
		if (track.size() > 30)
			track.remove(0);
		
		notifyUI();
	}
	
	private void notifyUI() {
		
	}
}
