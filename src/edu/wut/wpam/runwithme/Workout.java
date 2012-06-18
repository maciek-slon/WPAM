package edu.wut.wpam.runwithme;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;

public class Workout {
	protected int type;
	protected long starttime;
	protected long length;
	
	public Workout() {
		starttime = 0;
		length = 0;
	}
	
	public boolean finished() {
		return length > 0;
	}
	
	public void update() {
		
	}
	
	public void update(TrackPoint tp) {
		
	}
	
	// get progress of workout
	public float getProgress() {
		// check if workout started
		if (starttime == 0)
			return 0;
		
		// check if workout finished
		if (length > 0) 
			return 1;
		
		return 1;
	}
	
	// start current workout
	public void start() {
		starttime = System.currentTimeMillis();
	}
	
	// stop workout, save length etc.
	public void stop() {
		length = System.currentTimeMillis() - starttime;
	}
	
	// return workout name
	public String getName() {
		return "Trening";
	}
	
	// return workout summary (like target distance or time)
	public String getSummary() {
		return "";
	}

	public int getSound() {
		return 0;
	}
}
