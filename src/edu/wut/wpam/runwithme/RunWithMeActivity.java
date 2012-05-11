package edu.wut.wpam.runwithme;

import android.app.TabActivity;
import android.content.Context;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.TabHost;

@SuppressWarnings({ "deprecation" })
public class RunWithMeActivity extends TabActivity  {
    private TabHost mTabHost;

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        //---------------------------------------------
        // setup tabs
        //---------------------------------------------
        mTabHost = getTabHost();
        mTabHost.addTab(mTabHost.newTabSpec("tab_test1").setIndicator("TAB 1", getResources().getDrawable(R.drawable.ic_tab_artist)).setContent(R.id.tab1));
        mTabHost.addTab(mTabHost.newTabSpec("tab_test2").setIndicator("TAB 2", getResources().getDrawable(R.drawable.ic_tab_artist)).setContent(R.id.tab2));
        mTabHost.addTab(mTabHost.newTabSpec("tab_test3").setIndicator("TAB 3", getResources().getDrawable(R.drawable.ic_tab_artist)).setContent(R.id.tab3));
        mTabHost.setCurrentTab(0);
        
        //---------------------------------------------
        // Setup GPS
        //---------------------------------------------
        LocationManager mlocManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE); 
        GPSListener gpsListener = GPSListener.instance(); 
        mlocManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 0, 0, gpsListener);
    }
}