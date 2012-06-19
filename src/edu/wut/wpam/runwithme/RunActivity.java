package edu.wut.wpam.runwithme;

import java.util.ArrayList;
import java.util.Date;

public class RunActivity {
	private long id;
	private long date;
	private long summary;
	private int length;
	
	private Workout current_workout;
	private int current_id = 0;
	private int last_id = 0;

	public ArrayList<TrackPoint> track = new ArrayList<TrackPoint>();
	
	public ArrayList<Workout> workouts = new ArrayList<Workout>();

	public long getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

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

	public void update() {
		if (current_workout == null) {
			current_workout = workouts.get(0);
			current_workout.start();
			current_id = 0;
		}
		
		current_workout.update();
		
		if (current_workout.getProgress() >= 1) {
			synchronized(this) {
				if (workouts.size() > current_id+1) {
					current_id += 1;
					current_workout.stop();
					current_workout = workouts.get(current_id);
					current_workout.start();
				} else {
					if (current_workout.getType() != 0) {
						workouts.add(new Workout());
					}
				}
			}
		}
	}
	
	public int getCurrentWorkoutId() {
		return current_id;
	}
	
	public Workout getCurrentWorkout() {
		return current_workout;
	}
	
	// Will be used by the ArrayAdapter in the ListView
	@Override
	public String toString() {
		Date dt = new Date(date);
		return dt.toString() + " : " + 0.001*summary + "km";
	}

	public void add(TrackPoint pt) {
		track.add(pt);
		if (current_workout != null)
			current_workout.update(pt);
	}

	public int getSound() {
		if (last_id != current_id) {
			last_id = current_id;
			return 2;	
		} else
			return current_workout.getSound();
	}
}
