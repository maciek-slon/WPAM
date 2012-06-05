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

        
        array_spinner=new String[7];
        array_spinner[0]="Od zera do bohatera 1";
        array_spinner[1]="Od zera do bohatera 2";
        array_spinner[2]="Od zera do bohatera 3";
        array_spinner[3]="Od zera do bohatera 4";
        array_spinner[4]="Od zera do bohatera 5";
        array_spinner[5]="Od zera do bohatera 6";
        array_spinner[6]="Od zera do bohatera 7";
        Spinner s = (Spinner) findViewById(R.id.cbIntervalType);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, array_spinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
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
