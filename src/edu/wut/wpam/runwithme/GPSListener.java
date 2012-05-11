package edu.wut.wpam.runwithme;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

public class GPSListener implements LocationListener {

	public void onLocationChanged(Location loc) 
	{ 
		loc.getLatitude(); 
		loc.getLongitude(); 
		String Text = "My current location is: " + 
				"Latitud = " + loc.getLatitude() + 
				"Longitud = " + loc.getLongitude();  
		System.out.println(Text);
	} 
 
	public void onProviderDisabled(String provider) 
	{  
	} 
 
	public void onProviderEnabled(String provider) 
	{  
	} 
 
	public void onStatusChanged(String provider, int status, Bundle extras) 
	{ 
	}
	
	
	
	private static GPSListener gpsListener = null;

	private GPSListener() {
	}

	public static GPSListener instance() {
		if (gpsListener == null) { 
			gpsListener = new GPSListener(); 
		} 
		return gpsListener;
	}
}
