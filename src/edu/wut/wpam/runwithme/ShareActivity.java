package edu.wut.wpam.runwithme;


import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session.AccessType;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

public class ShareActivity extends Activity{
	
	final static private String APP_KEY = "e7szaohaboil8tz";
	final static private String APP_SECRET = "3dd4zibca09t6ih";
	final static private AccessType ACCESS_TYPE = AccessType.DROPBOX;
	
	private boolean authenticating = false;
	
    private DropboxAPI<AndroidAuthSession> mDBApi;
	
    protected void showMessage(String title, String msg) {
		AlertDialog alertDialog;
		alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.setTitle(title);
		alertDialog.setMessage(msg);
		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int which) {  
		        dialog.dismiss();                      
		    }  
		});  
		alertDialog.show();
	}
    
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.achieved);

        AccessTokenPair tokens;
        
        AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);
        AndroidAuthSession session = new AndroidAuthSession(appKeys, ACCESS_TYPE);
        mDBApi = new DropboxAPI<AndroidAuthSession>(session);
   
        SharedPreferences settings = getSharedPreferences("RUNWITHME_DROPBOX", 0);
        boolean connected = settings.getBoolean("connected", false);
        if (connected) {
        	String key = settings.getString("key", "");
        	String secret = settings.getString("secret", "");
        	tokens = new AccessTokenPair(key, secret);
            mDBApi.getSession().setAccessTokenPair(tokens);
        } else {
        	authenticating = true;
        	mDBApi.getSession().startAuthentication(ShareActivity.this);
        }
	}
	
	protected void onResume() {
	    super.onResume();

	    if (authenticating && mDBApi.getSession().authenticationSuccessful()) {
	    	authenticating = false;
	        try {
	            // Sets the access token on the session
	            mDBApi.getSession().finishAuthentication();

	            AccessTokenPair tokens = mDBApi.getSession().getAccessTokenPair();

	            // Provide your own storeKeys to persist the access token pair
	            // A typical way to store tokens is using SharedPreferences
	            //storeKeys(tokens.key, tokens.secret);
	            SharedPreferences settings = getSharedPreferences("RUNWITHME_DROPBOX", 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean("connected", true);
                editor.putString("key", tokens.key);
                editor.putString("secret", tokens.secret);
                editor.commit();
	        } catch (IllegalStateException e) {
	            Log.i("DbAuthLog", "Error authenticating", e);
	        }
	    }
	    
	    finish();
	}
}
