package edu.wut.wpam.runwithme;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.exception.DropboxServerException;
import com.dropbox.client2.exception.DropboxUnlinkedException;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session.AccessType;

import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class TestDatabaseActivity extends ListActivity {
	private ActivityDataSource datasource;
	private LayoutInflater mInflater;
	List<RunActivity> values;
	AlertDialog.Builder builder;
	RunActivity act;
	ArrayAdapter<RunActivity> adapter;
	SimpleDateFormat formatter = new SimpleDateFormat("d MMMM yyyy, H:mm");

	
	
	final static private String APP_KEY = "e7szaohaboil8tz";
	final static private String APP_SECRET = "3dd4zibca09t6ih";
	final static private AccessType ACCESS_TYPE = AccessType.DROPBOX;
	
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.database);

		builder = new AlertDialog.Builder(this);
		builder.setTitle("Zakończyć bieg?")
				.setMessage("Wybranie tej opcji spowoduje zakończenie bieżącego biegu.")
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								// Yes button clicked, do something
								TestDatabaseActivity.this.showLog(true);
							}
						}).setNegativeButton("No", null);

		mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		datasource = new ActivityDataSource(this);
		datasource.open();
		values = datasource.getAllActivities();
		datasource.close();

		// hide all entries with negative length (i.e. current one)
		for (RunActivity act : values) {
			if (act.getSummary() < 0) {
				//values.remove(act);
			}
		}

		// Use the SimpleCursorAdapter to show the elements in a ListView
		// ArrayAdapter<RunActivity> adapter = new
		// ArrayAdapter<RunActivity>(this, android.R.layout.simple_list_item_1,
		// values);
		// setListAdapter(adapter);
		adapter = new ArrayAdapter<RunActivity>(this, R.layout.list_item, values) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View row;

				if (null == convertView) {
					row = mInflater.inflate(R.layout.list_item, null);
				} else {
					row = convertView;
				}

				Date date = new Date(getItem(position).getDate());
				
				TextView tv = (TextView) row.findViewById(R.id.tvDate);
				tv.setText(formatter.format(date));
				tv = (TextView) row.findViewById(R.id.tvDist);
				tv.setText(String.format("%.2fkm", getItem(position).getSummary() * 0.001));

				return row;
			}
		};

		setListAdapter(adapter);

		ListView listView = (ListView) findViewById(android.R.id.list);
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				act = values.get(position);
				System.out.println("Init: " + RunAppContext.instance().initialized() +
						" Static: " + RunAppContext.instance().isStatic());
				if (RunAppContext.instance().initialized() && !RunAppContext.instance().isStatic()) {
					builder.show();
				} else {
					showLog(false);
				}
			}
		});
		

	    registerForContextMenu(listView);
	}

	public void showLog(boolean dofinish) {
		if (dofinish) {
			RunAppContext.instance().saveCurrentActivity();
		}

		RunAppContext.instance().initFromFile(act);
		Intent myIntent = new Intent(this, LogActivity.class);
		startActivityForResult(myIntent, 0);
	}

	// Will be called via the onClick attribute
	// of the buttons in main.xml
//	public void onClick(View view) {
//		@SuppressWarnings("unchecked")
//		ArrayAdapter<RunActivity> adapter = (ArrayAdapter<RunActivity>) getListAdapter();
//		RunActivity act = null;
//		switch (view.getId()) {
//		case R.id.add:
//			datasource.open();
//			// Save the new comment to the database
//			act = datasource.createRunActivity(System.currentTimeMillis(), 523);
//			adapter.add(act);
//			datasource.close();
//			break;
//		case R.id.delete:
//			if (getListAdapter().getCount() > 0) {
//				act = (RunActivity) getListAdapter().getItem(0);
//				datasource.deleteActivity(act);
//				adapter.remove(act);
//			}
//			break;
//		}
//		adapter.notifyDataSetChanged();
//	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		if (v.getId() == android.R.id.list) {
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
			RunActivity act = values.get(info.position);
			Date date = new Date(act.getDate());
			SimpleDateFormat formatter = new SimpleDateFormat("d MMMM yyyy");
			menu.setHeaderTitle(formatter.format(date));
			String[] menuItems = getResources()
					.getStringArray(R.array.act_menu);
			for (int i = 0; i < menuItems.length; i++) {
				MenuItem m = menu.add(Menu.NONE, i, i, menuItems[i]);
				m.setIcon(R.drawable.ic_bike_nor);
			}
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		int menuItemIndex = item.getItemId();
		//String[] menuItems = getResources().getStringArray(R.array.act_menu);
		//String menuItemName = menuItems[];
		RunActivity act = values.get(info.position);

		if (menuItemIndex == 0) {
			System.out.println("SHARE!");

			AccessTokenPair tokens;
	        
	        AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);
	        AndroidAuthSession session = new AndroidAuthSession(appKeys, ACCESS_TYPE);
	        DropboxAPI<AndroidAuthSession> mDBApi = new DropboxAPI<AndroidAuthSession>(session);

    		int size = 0;
			SharedPreferences settings = getSharedPreferences("RUNWITHME_DROPBOX", 0);
	        boolean connected = settings.getBoolean("connected", false);
	        if (connected) {
	        	String key = settings.getString("key", "");
	        	String secret = settings.getString("secret", "");
	        	tokens = new AccessTokenPair(key, secret);
	            mDBApi.getSession().setAccessTokenPair(tokens);
	            
	            String FILENAME = "" + act.getDate();
	    		String data = "";

	    		
	    		try {
		    		FileInputStream fis = openFileInput(FILENAME);
		    		FileOutputStream fos = openFileOutput("tmp.kml", MODE_WORLD_READABLE);
		    		BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
		    		
		    		
		    		String line = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" 
		    				+ "<kml xmlns=\"http://earth.google.com/kml/2.0\">\n"
		    				+ "\n"
		    				+ "<Document>\n"
		    				+ "  <name>RunWithMe</name>\n"
		    				+ "  <description>Zapis trasy</description>\n"
		    				+ "  <Placemark>\n"
		    				+ "    <name>" + formatter.format(new Date(act.getDate())) + "</name>\n"
		    				+ "    <description>" + String.format("%.2fkm", 0.001f * act.getSummary()) + "</description>\n"
		    				+ "\n"
		    			    + "    <Style>\n"
		    			    + "      <LineStyle>\n"
		    			    + "        <color>80800000</color>\n"
		    			    + "        <width>3</width>\n"
		    			    + "      </LineStyle>\n"
		    			    + "    </Style>\n"
		    			    + "\n"
		    			    + "    <LineString>\n"
		    			    + "      <coordinates>\n";
		    		
		    		size += line.getBytes().length;
		  
		    		fos.write(line.getBytes());
		    		
		    		do {
		    			data = reader.readLine();
		    			if (data == null) break;
		    			String[] nums = data.split(";");
		    			TrackPoint tp = new TrackPoint(0, 0, 0, 0);
		    			
		    			tp.tim = Long.parseLong(nums[0]);
		    			tp.lat = Integer.parseInt(nums[1]);
		    			tp.lon = Integer.parseInt(nums[2]);
		    			tp.alt = Integer.parseInt(nums[3]);
		    					    			
		    			
		    			line = " " + 0.000001 * tp.lon + "," + 0.000001 * tp.lat + "," + tp.alt + " ";
		    			size += line.getBytes().length;
		                fos.write(line.getBytes());
		    			
		    		} while(true);
		    		
		    		line = "\n" +
		    				"      </coordinates>\n" +
		    				"    </LineString>\n" +
		    				"  </Placemark>\n" +
		    				"</Document>\n" +
		    				"\n" +
		    				"</kml>";
	                fos.write(line.getBytes());
		    		size += line.getBytes().length;

		    		
		    		fis.close();
	                fos.close();
	    		} catch(IOException e) {
	    			
	    		}

	    		// Metadata -- check, if file already exists and get it's rev (necessary for uploading deleted file)
	    		Entry existingEntry = null;
	    		String rev = null;
	    		try {
	    		    existingEntry = mDBApi.metadata("/Public/" + act.getDate() + ".kml", 1, null, false, null);
	    		    Log.i("DbExampleLog", "The file's rev is now: " + existingEntry.rev);
	    		    rev = existingEntry.rev;
	    		} catch  (DropboxServerException e) {
	    			Log.e("DbExampleLog", "Something went wrong while getting metadata. Server: " + e.reason);
	    		} catch (DropboxException e) {
	    		    Log.e("DbExampleLog", "Something went wrong while getting metadata.");
	    		}
	    		
	    		if (existingEntry == null || existingEntry.isDeleted) {
		            // Uploading content.
		            FileInputStream inputStream = null;
		            try {
		                inputStream = openFileInput("tmp.kml");
		                Log.i("DbExampleLog", "Sending file. Size: " + size);
		                Entry newEntry = mDBApi.putFile("/Public/" + act.getDate() + ".kml", inputStream, size, rev, null);
		                Log.i("DbExampleLog", "The uploaded file's rev is: " + newEntry.rev);
		            } catch (DropboxUnlinkedException e) {
		                // User has unlinked, ask them to link again here.
		                Log.e("DbExampleLog", "User has unlinked.");
		                showMessage("Błąd", "Nie jesteś połączony z usługą Dropbox");
		            } catch  (DropboxServerException e) {
		    			Log.e("DbExampleLog", "Something went wrong while uploading. Server: " + e.reason);
		                showMessage("Błąd", "Wysłanie pliku nie powiodło się. " + e.reason);
		    		} catch (DropboxException e) {
		                Log.e("DbExampleLog", "Something went wrong while uploading.");
		                showMessage("Błąd", "Wysłanie pliku nie powiodło się.");
		            } catch (FileNotFoundException e) {
		                Log.e("DbExampleLog", "File not found.");
		    		} finally {
		                if (inputStream != null) {
		                    try {
		                        inputStream.close();
		                    } catch (IOException e) {}
		                }
		            }
	    		} else {
	    			 showMessage("Błąd", "Nie można wysłać pliku");
	    		}
	        } else {
	        	showMessage("Błąd", "Nie jesteś połączony z usługą Dropbox");
	        }
		}
		
		if (menuItemIndex == 1) {
			System.out.println("DELETE!");
			datasource.open();
			datasource.deleteActivity(act);
			adapter.remove(act);
			datasource.close();
		}

		return true;
	}

	protected void showMessage(String title, String msg) {
		AlertDialog alertDialog;
		alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.setTitle(title);
		alertDialog.setMessage(msg);
		alertDialog.setCancelable(false);
		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int which) {  
		        dialog.dismiss();                      
		    }  
		});  
		alertDialog.show();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

}