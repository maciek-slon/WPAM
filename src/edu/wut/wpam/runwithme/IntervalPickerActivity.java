package edu.wut.wpam.runwithme;

import edu.wut.wpam.widgets.NumberPicker;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class IntervalPickerActivity extends Activity {
		
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intervals);

        final NumberPicker pickTime = (NumberPicker)findViewById(R.id.pickTime);
        pickTime.setRange(1, 60);
        pickTime.setValue(5);
        pickTime.setIncrement(1);
        
        final NumberPicker pickDist = (NumberPicker)findViewById(R.id.pickDist);
        pickDist.setRange(100, 10000);
        pickDist.setValue(500);
        pickDist.setIncrement(100);
        
        
        Button addDist = (Button) findViewById(R.id.btnAddDist);
        addDist.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {                
                RunAppContext.instance().getActivity().workouts.add(new DistWorkout(pickDist.getValue()));
                finish();
            }
        });
        
        Button addTime = (Button) findViewById(R.id.btnAddTime);
        addTime.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {                
                RunAppContext.instance().getActivity().workouts.add(new TimeWorkout(pickTime.getValue() * 60));
                finish();
            }
        });
    }
}
