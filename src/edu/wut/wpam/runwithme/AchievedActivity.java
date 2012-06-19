package edu.wut.wpam.runwithme;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class AchievedActivity extends Activity{
	
	private AchievementDataSource datasource;
	
	List<Achievement> values;
	
	private void setText(int id, String txt) {
		TextView view = (TextView)findViewById(id);
		view.setText(txt);
	}
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.achieved);
        
        datasource = new AchievementDataSource(this);
		datasource.open();
		values = datasource.getAllAchievements();
		datasource.close();
		
		for (Achievement a : values) {
			if (a.name == "TopSpeed") {
				setText(R.id.tvTopSpeed, String.format("%.1f km/h", a.value));
			}
		}
	}
	
}
