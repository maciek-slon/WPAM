package edu.wut.wpam.runwithme;

import java.util.List;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

public class AchievedActivity extends Activity{
	
	private void setText(int id, String txt) {
		TextView view = (TextView)findViewById(id);
		view.setText(txt);
	}
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.achieved);
        
        SharedPreferences settings = getSharedPreferences("RUNWITHME_ACHIEVEMENTS", 0);
        //setText(R.id.tvTopSpeed, String.format("%.1f km/h", settings.getFloat("TopSpeed", 0)));
        setText(R.id.tvTopDist, String.format("%.2f km", settings.getFloat("TopDist", 0)));
        setText(R.id.tvTopAvg, String.format("%.1f km/h", settings.getFloat("TopAvg", 0)));
        setText(R.id.tvTopTime, Helper.formatTime(settings.getFloat("TopTime", 0)));
	}
	
}
