package edu.wut.wpam.runwithme;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class IntervalPickerActivity extends Activity {
	
	private String array_spinner[];
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intervals);

        
        array_spinner=new String[5];
        array_spinner[0]="Od zera do bohatera";
        Spinner s = (Spinner) findViewById(R.id.cbIntervalType);
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, array_spinner);
        s.setAdapter(adapter);
        
        
        
        Button next = (Button) findViewById(R.id.btnGo);
        next.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {                
                Intent myIntent = new Intent(view.getContext(), MonitorActivity.class);
                startActivityForResult(myIntent, 0);
            }

        });
    }
}
