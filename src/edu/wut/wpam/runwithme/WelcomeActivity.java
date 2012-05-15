package edu.wut.wpam.runwithme;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class WelcomeActivity extends Activity {
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);

        Button next = (Button) findViewById(R.id.btnIntervals);
        next.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	MediaPlayer mp = MediaPlayer.create(getBaseContext(), R.raw.beep);
                mp.start();
                mp.setOnCompletionListener(new OnCompletionListener() {
 
                    public void onCompletion(MediaPlayer mp) {
                        mp.release();
                    }
                });
                
                Intent myIntent = new Intent(view.getContext(), IntervalPickerActivity.class);
                startActivityForResult(myIntent, 0);
            }

        });
    }
}
