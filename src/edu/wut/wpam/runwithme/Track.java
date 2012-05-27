package edu.wut.wpam.runwithme;

import java.util.ArrayList;

public class Track {
	public ArrayList<TrackPoint> points = new ArrayList<TrackPoint>();
	
	public void addPoint(TrackPoint pt) {
		points.add(pt);
	}
}
