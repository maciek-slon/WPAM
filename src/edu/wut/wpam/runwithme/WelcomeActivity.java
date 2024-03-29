package edu.wut.wpam.runwithme;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
        setButtonActivity(R.id.btnAchieve, AchievedActivity.class);
        setButtonActivity(R.id.btnShare, ShareActivity.class);
        
        Button btn = (Button) findViewById(R.id.btnInfo);
        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	AlertDialog alertDialog;
        		alertDialog = new AlertDialog.Builder(WelcomeActivity.this).create();
        		alertDialog.setTitle("Autorzy");
        		alertDialog.setMessage("Sylwia Szymczyk\nMaciej Stefańczyk");
        		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
        		    public void onClick(DialogInterface dialog, int which) {  
        		        dialog.dismiss();                      
        		    }  
        		});  
        		alertDialog.show();
            }
        });
        
        
        RunAppContext.instance().setContext(getApplicationContext());
    }
    
    protected void onDestroy() {
    	System.out.println("Destroy!");
    	RunAppContext.instance().finish();
    	super.onDestroy();
    }
}
