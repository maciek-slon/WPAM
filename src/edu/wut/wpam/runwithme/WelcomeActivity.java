package edu.wut.wpam.runwithme;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class WelcomeActivity extends Activity {
	
	private void setButtonActivity(int id, final Class<?> cls) {
		Button btn = (Button) findViewById(id);
        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {                
                Intent myIntent = new Intent(WelcomeActivity.this, cls);
                startActivity(myIntent);
            }
        });
	}
	
	/** Called when the activity is first created. */	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);

        setButtonActivity(R.id.btnIntervals, MonitorActivity.class);
        setButtonActivity(R.id.btnStopwatch, StopwatchActivity.class);
        setButtonActivity(R.id.btnDatabase, TestDatabaseActivity.class);
        
        RunAppContext.instance().setContext(getApplicationContext());
    }
    
    protected void onDestroy() {
    	System.out.println("Destroy!");
    	RunAppContext.instance().finish();
    	super.onDestroy();
    }
}
