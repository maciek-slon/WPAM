package edu.wut.wpam.runwithme;

import java.util.ArrayList;
import java.util.Date;

import android.view.View;

public class RunActivity {
	private long id;
	private long date;
	private long summary;

	public ArrayList<TrackPoint> track;

	public ArrayList<TrackPoint> getTrack() {
		return track;
	}

	public void setTrack(ArrayList<TrackPoint> track) {
		this.track = track;
	}

	public long getSummary() {
		return summary;
	}

	public void setSummary(long summary) {
		this.summary = summary;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getDate() {
		return date;
	}

	public void setDate(long date) {
		this.date = date;
	}

	// Will be used by the ArrayAdapter in the ListView
	@Override
	public String toString() {
		Date dt = new Date(date);
		return dt.toString() + " : " + 0.001*summary + "km";
	}
}
