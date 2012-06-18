package edu.wut.wpam.runwithme;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.exception.DropboxUnlinkedException;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session.AccessType;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

public class AchievedActivity extends Activity{
	
	final static private String APP_KEY = "e7szaohaboil8tz";
	final static private String APP_SECRET = "3dd4zibca09t6ih";
	final static private AccessType ACCESS_TYPE = AccessType.DROPBOX;
	

    private DropboxAPI<AndroidAuthSession> mDBApi;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.achieved);

        AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);
        AndroidAuthSession session = new AndroidAuthSession(appKeys, ACCESS_TYPE);
        mDBApi = new DropboxAPI<AndroidAuthSession>(session);
        
        mDBApi.getSession().startAuthentication(AchievedActivity.this);
        
    
       
	}
	
	protected void onResume() {
	    super.onResume();

	    // ...

	    if (mDBApi.getSession().authenticationSuccessful()) {
	        try {
	            // MANDATORY call to complete auth.
	            // Sets the access token on the session
	            mDBApi.getSession().finishAuthentication();

	            AccessTokenPair tokens = mDBApi.getSession().getAccessTokenPair();

	            // Provide your own storeKeys to persist the access token pair
	            // A typical way to store tokens is using SharedPreferences
	            //storeKeys(tokens.key, tokens.secret);
	            SharedPreferences settings = getSharedPreferences("RUNWITHME_DROPBOX", 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("key", tokens.key);
                editor.putString("secret", tokens.secret);
                editor.commit();
	            
	            // Uploading content.
	            FileInputStream inputStream = null;
	            try {
	            	FileOutputStream fos = openFileOutput("Test.txt", MODE_WORLD_READABLE);
	                String data = "TTTeeee";
	                fos.write(data.getBytes());
	                fos.close();
	            	
	                inputStream = openFileInput("Test.txt");
	                Entry newEntry = mDBApi.putFile("/testing.txt", inputStream, 7, null, null);
	                Log.i("DbExampleLog", "The uploaded file's rev is: " + newEntry.rev);
	            } catch (DropboxUnlinkedException e) {
	                // User has unlinked, ask them to link again here.
	                Log.e("DbExampleLog", "User has unlinked.");
	            } catch (DropboxException e) {
	                Log.e("DbExampleLog", "Something went wrong while uploading.");
	            } catch (FileNotFoundException e) {
	                Log.e("DbExampleLog", "File not found.");
	            } catch (IOException e) {
	            	Log.e("DbExampleLog", "Other error");
	    		} finally {
	                if (inputStream != null) {
	                    try {
	                        inputStream.close();
	                    } catch (IOException e) {}
	                }
	            }
	        } catch (IllegalStateException e) {
	            Log.i("DbAuthLog", "Error authenticating", e);
	        }
	    }

	    // ...
	}
}
