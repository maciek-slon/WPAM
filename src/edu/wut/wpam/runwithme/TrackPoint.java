package edu.wut.wpam.runwithme;

import android.location.Location;

public class TrackPoint {
	public int lat;
	public int lon;
	public int alt;
	public long tim;
	
	public TrackPoint(int la, int lo, int al, long tm) {
		lat = la;
		lon = lo;
		alt = al;
		tim = tm;
	}
	
	public float distanceTo(TrackPoint end) {
		float[] res = new float[1];
		Location.distanceBetween(1.0 * lat / 1e6, 1.0 * lon / 1e6, 1.0 * end.lat / 1e6, 1.0 * end.lon / 1e6, res);
		return res[0];
	}
}
