package edu.wut.wpam.runwithme;

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
}
