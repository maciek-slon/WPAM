package edu.wut.wpam.runwithme;

public class DistWorkout extends Workout {
	float distance;
	float accum;
	TrackPoint last_tp;
	private int snd_id;
	private boolean prog_quater = false, prog_half = false, prog_three = false;
	
	public DistWorkout() {
		type = 2;
		distance = 0;
		accum = 0;
		
		last_tp = null;
	}
	
	public DistWorkout(float dist) {
		type = 2;
		distance = dist;
		accum = 0;
		
		last_tp = null;
	}
	
	// set workout distance in meters
	public void setDistance(float dist) {
		distance = dist;
	}
	
	@Override
	public float getProgress() {
		if (starttime == 0)
			return 0;
		
		if (distance == 0) 
			return 1;
		
		return accum / distance;
	}
	
	public void update(TrackPoint tp) {
		if (last_tp != null)
			accum += tp.distanceTo(last_tp);
		
		if (accum > 3 * distance / 4 && !prog_three) {
			snd_id = 1;
			prog_three = true;
		} else		
		if (accum > distance / 2 && !prog_half) {
			snd_id = 1;
			prog_half = true;
		} else
		if (accum > distance / 4 && !prog_quater) {
			snd_id = 1;
			prog_quater = true;
		}
		
		last_tp = tp;	
	}
	
	@Override
	public String getName() {
		return "Dystans";
	}
	
	@Override
	public String getSummary() {
		return String.format("%.0fm", distance);
	}
	
	@Override
	public int getSound() {
		int tmp = snd_id;
		snd_id = 0;
		return tmp;
	}
}
