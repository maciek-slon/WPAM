package edu.wut.wpam.runwithme;

import edu.wut.wpam.widgets.Plot;
import edu.wut.wpam.widgets.StopWatch;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PointF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StopwatchActivity extends Activity implements SensorEventListener {
	/** Called when the activity is first created. */

	Button stop;
	Button restart;

	Plot plot;
	
	StopWatch sw;

	private SensorManager mSensorManager;
	private Sensor mAccelerometer;
	private final float NOISE = (float) 2.0;
	private float ax, ay, az;
	
	// is accelerometer initialized
	private boolean mInitialized;

	private int cnt = 0;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.stopwatch);

		//plot = (Plot) findViewById(R.id.simplePlot); 
		
		mInitialized = false;
		
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_FASTEST);

		sw = (StopWatch) findViewById(R.id.stopWatch);

		stop = (Button) findViewById(R.id.btnSwStop);
		stop.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				sw.stop();
			}
		});

		restart = (Button) findViewById(R.id.btnSwRestart);
		restart.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				sw.restart();
			}
		});
	}

	protected void onResume() {
		super.onResume();
		mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
	}

	protected void onPause() {
		mSensorManager.unregisterListener(this);
		super.onPause();
	}

	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// can be safely ignored for this demo
	}

	public void onSensorChanged(SensorEvent event) {
		float x = event.values[0];
		float y = event.values[1];
		float z = event.values[2];
		
		//System.out.println(">>>>>>>>>>>>>>>:" + x + ", " + y + ", " + z);
		
		if (!mInitialized) {
			ax = x;
			ay = y;
			az = z;
			mInitialized = true;
		} else {
			float dx = ax - x;
			float dy = ay - y;
			float dz = az - z;
			ax = x;
			ay = y;
			az = z;
			float len = Math.abs(dx*dx+dy*dy+dz*dz);
			cnt++;
			//plot.add(new PointF(cnt, len));
			//plot.invalidate();
			if (len > 200)
				sw.stop();
		}
	}
}
