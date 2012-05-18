package edu.wut.wpam.runwithme;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class IntervalPickerActivity extends Activity {
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intervals);

        Button next = (Button) findViewById(R.id.btnGo);
        next.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {                
                Intent myIntent = new Intent(view.getContext(), MonitorActivity.class);
                startActivityForResult(myIntent, 0);
            }

        });
    }
}
