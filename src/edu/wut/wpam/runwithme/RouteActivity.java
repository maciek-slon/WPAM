package edu.wut.wpam.runwithme;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.InputFilter.LengthFilter;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

import edu.wut.wpam.widgets.ITRMapView;

public class RouteActivity extends MapActivity {
	private List<Overlay> mapOverlays;

	private Projection projection;
	private MapController myMapController;
	private RunAppContext context = RunAppContext.instance();
	private ITRMapView mapView;

	private boolean auto_center;
	private int mode;

	public int getMode() {
		return mode;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}

	public boolean isAuto_center() {
		return auto_center;
	}

	public void setAuto_center(boolean auto_center) {
		this.auto_center = auto_center;
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);

		mapView = (ITRMapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);

		mapOverlays = mapView.getOverlays();
		projection = mapView.getProjection();
		mapOverlays.add(new MyOverlay());
		myMapController = mapView.getController();
		myMapController.setZoom(17); // Fixed Zoom Level
		myMapController.setCenter(new GeoPoint(52000000, 21000000));
		context.addListener(mapView);
		mode = 1; // Fixed Center point
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.centr:
			if (mode == 1) {
				mode = 2;
				item.setIcon(R.drawable.ic_crosshair);
			} else {
				mode = 1;
				item.setIcon(R.drawable.ic_bike);
			}
			Toast.makeText(this, "Zmieniles tryb centrowania!",	Toast.LENGTH_LONG).show();
			mapView.setLast_touched(0);
			mapView.invalidate();
			break;
		case R.id.text:
			Toast.makeText(this, "You pressed the text!", Toast.LENGTH_LONG)
					.show();
			break;
		case R.id.icontext:
			Toast.makeText(this, "You pressed the icon and text!",
					Toast.LENGTH_LONG).show();
			break;
		}
		return true;
	}

	public void onDestroy() {
		context.removeListener(mapView);

		super.onDestroy();
	}

	class MyOverlay extends Overlay {

		public MyOverlay() {
		}

		public void draw(Canvas canvas, MapView mapv, boolean shadow) {
			super.draw(canvas, mapv, shadow);

			Paint mPaint = new Paint();
			mPaint.setDither(true);
			mPaint.setColor(Color.RED);
			mPaint.setStyle(Paint.Style.STROKE);
			mPaint.setStrokeJoin(Paint.Join.ROUND);
			mPaint.setStrokeCap(Paint.Cap.ROUND);
			mPaint.setStrokeWidth(3);

			Paint tPaint = new Paint();
			tPaint.setAntiAlias(true);
			tPaint.setColor(Color.WHITE);
			tPaint.setStyle(Paint.Style.FILL_AND_STROKE);
			tPaint.setStrokeJoin(Paint.Join.ROUND);
			tPaint.setStrokeCap(Paint.Cap.ROUND);
			tPaint.setStrokeWidth(2);

			int density = canvas.getDensity();
			int txtSize = density / 6;
			tPaint.setTextSize(txtSize);

			Paint rPaint = new Paint();
			rPaint.setColor(Color.BLACK);
			rPaint.setStyle(Paint.Style.FILL_AND_STROKE);
			rPaint.setAlpha(80);

			String speed = String.valueOf(context.getSpeed()) + " km/h";
			float dist = context.getDistance() / 1000;
			String time = String.valueOf(context.getTime());
			float seconds = context.getTime();
			int hour = (int) Math.floor(seconds / 3600);
			seconds = seconds - hour * 3600;
			int minutes = (int) Math.floor(seconds / 60);
			seconds = seconds - minutes * 60;
			int sec = (int) Math.floor(seconds);

			String distance = String.format("%.2f", dist) + " km";

			String disHour = (hour < 10 ? "0" : "") + hour, disMinu = (minutes < 10 ? "0"
					: "")
					+ minutes, disSec = (sec < 10 ? "0" : "") + sec;

			time = "" + disHour + ":" + disMinu + ":" + disSec;

			canvas.drawRect(new Rect(0, 0, mapView.getWidth(),
					(int) (0.58 * density)), rPaint);
			canvas.drawText(speed, 20, (float) (0.25 * density), tPaint);
			canvas.drawText(time, 20, (float) (0.5 * density), tPaint);
			tPaint.setTextAlign(Align.RIGHT);
			canvas.drawText(distance, mapView.getWidth() - 20,
					(float) (0.25 * density), tPaint);

			Path path = new Path();

			ArrayList<TrackPoint> track = context.getTrack();
			if (track == null || track.size() == 0) {
				return;
			}
			int min_lat = 0, min_lon = 0, max_lat = 0, max_lon = 0;

			for (int i = 0; i < track.size(); ++i) {
				int lat = track.get(i).lat;
				int lon = track.get(i).lon;
				GeoPoint gP1 = new GeoPoint(lat, lon);
				Point p1 = new Point();
				projection.toPixels(gP1, p1);

				if (i == 0) {
					path.moveTo(p1.x, p1.y);
					min_lat = max_lat = lat;
					min_lon = max_lat = lon;
				} else {
					path.lineTo(p1.x, p1.y);
					if (lat < min_lat)
						min_lat = lat;
					if (lon < min_lon)
						min_lon = lon;
					if (lon > max_lon)
						max_lon = lon;
					if (lat > max_lat)
						max_lat = lat;
				}
			}
			int last_lon = track.get(track.size() - 1).lon;
			int last_lat = track.get(track.size() - 1).lat;
			int mean_lat = (min_lat + max_lat) / 2;
			int mean_lon = (min_lon + max_lon) / 2;
			if (System.currentTimeMillis() - mapView.getLast_touched() > 10000) {
				if (mode == 1) {
					myMapController.zoomToSpan(max_lat - min_lat, max_lon
							- min_lon);
					myMapController.setCenter(new GeoPoint(mean_lat, mean_lon));
				} else
					myMapController.setCenter(new GeoPoint(last_lat, last_lon));
			}

			canvas.drawPath(path, mPaint);
		}
	}

}