package edu.wut.wpam.runwithme;

public class TimeWorkout extends Workout {
	private long total_length;
	private int snd_id;
	private boolean played = false;
	
	public TimeWorkout() {		
		type = 1;
		total_length = 0;
	}
	
	public TimeWorkout(long len) {		
		type = 1;
		total_length = 1000 * len;
	}
	
	// set workout length in seconds
	public void setLength(long len) {
		total_length = 1000 * len;
	}
	
	@Override
	public float getProgress() {
		// check if workout started
		if (starttime == 0)
			return 0;

		// check if workout finished
		if (length > 0) 
			return 1;
		
		if (total_length == 0)
			return 1;
		
		return 1.0f * (System.currentTimeMillis() - starttime) / total_length;
	}
	
	@Override
	public String getName() {
		return "Czas";
	}
	
	@Override
	public String getSummary() {
		return Helper.formatTimeNice(0.001f * total_length);
	}
	
	@Override 
	public void update() {
		long elapsed = System.currentTimeMillis() - starttime;
		if (total_length - elapsed < 6500 && !played) {
			snd_id = 3;
			played = true;
		}
	}
	
	@Override
	public int getSound() {
		int tmp = snd_id;
		snd_id = 0;
		return tmp;
	}
}
