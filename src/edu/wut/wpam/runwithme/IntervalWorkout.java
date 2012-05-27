package edu.wut.wpam.runwithme;

import java.util.ArrayList;

import android.text.format.Time;

public class IntervalWorkout {
	
	private ArrayList<Float> intervals;
	int day;
	float workoutLength;
	float totalTime;
	long startTime;
	
	// defines intervals (in seconds) for this type of workout
	// from left: days, repeats, run, walk 
	private int[][] times = {
			{ 4, 5,   60,  300 },
			{ 4, 5,  120,  240 },
			{ 4, 5,  180,  180 },
			{ 4, 4,  300,  150 },
			{ 4, 3,  420,  180 },
			{ 4, 3,  480,  120 },
			{ 4, 3,  540,   60 },
			{ 4, 2,  780,  120 },
			{ 4, 2,  840,   60 },
			{ 4, 1, 1800,    0 }
	};
	
	public IntervalWorkout() {
		
	}
	
	public void setDay(int d) {
		day = d;
		prepareIntervals();
	}
	
	public float getLength() {
		return workoutLength;
	}
	
	private void prepareInterval(int count, float i1, float i2) {
		intervals.clear();
		for (int i = 0; i < count; ++i) {
			intervals.add(i1);
			intervals.add(i2);
		}
		workoutLength = count * (i1+i2);
	}
	
	private void prepareIntervals() {
		intervals = new ArrayList<Float>();
		int accum = 0;
		for (int i = 0; i < times.length; ++i) {
			accum += times[i][0];
			if (day <= accum) {
				prepareInterval(times[i][1], times[i][2], times[i][3]);
				return;
			}
		}
		
		totalTime = 0;
	}
	
	public ArrayList<Float> getIntervals() {
		return intervals;
	}
	
	public void update(long tm) {
		totalTime = 0.001f * (tm - startTime);
	}
	
	public float getTime() {
		return totalTime;
	}

	public void setStart(Time now) {
		startTime = System.currentTimeMillis();
	}
}
