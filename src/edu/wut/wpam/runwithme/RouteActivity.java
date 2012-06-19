package edu.wut.wpam.runwithme;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.google.android.maps.Projection;

import edu.wut.wpam.widgets.ITRMapView;

enum CenterMode {
	FIT,
	FOLLOW
}

public class RouteActivity extends MapActivity {
	private List<Overlay> mapOverlays;

	private Projection projection;
	private MapController myMapController;
	private RunAppContext context = RunAppContext.instance();
	private ITRMapView mapView;
	MyItemizedOverlay itemizedOverlay;

	private boolean auto_center;
	private CenterMode mode;

	public CenterMode getMode() {
		return mode;
	}

	public void setMode(CenterMode mode) {
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

		projection = mapView.getProjection();
		
		myMapController = mapView.getController();
		myMapController.setZoom(17); // Fixed Zoom Level
		myMapController.setCenter(new GeoPoint(52000000, 21000000));
		
		context.addListener(mapView);
		mode = CenterMode.FOLLOW; // Fixed Center point
		
		Drawable start = this.getResources().getDrawable(R.drawable.marker_start);
		itemizedOverlay = new MyItemizedOverlay(start, this); 
		
		mapOverlays = mapView.getOverlays();
		mapOverlays.add(new MyOverlay());
		mapOverlays.add(itemizedOverlay);
		mapOverlays.add(new SummaryOverlay());
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
		case R.id.cmFit:
			mode = CenterMode.FIT;
			Toast.makeText(this, "Wyświetlam całą trasę", Toast.LENGTH_LONG).show();
			mapView.setLast_touched(0);
			mapView.invalidate();
			break;
		case R.id.cmFollow:
			mode = CenterMode.FOLLOW;
			Toast.makeText(this, "Śledzę bieżący punkt", Toast.LENGTH_LONG).show();
			mapView.setLast_touched(0);
			mapView.invalidate();
			break;
		case R.id.cmViewMap:
			mapView.setSatellite(false);
			mapView.invalidate();
			break;
		case R.id.cmViewSatelite:
			mapView.setSatellite(true);
			mapView.invalidate();
			break;
		}
		return true;
	}

	public void onDestroy() {
		context.removeListener(mapView);
		super.onDestroy();
	}

	class SummaryOverlay extends Overlay {
		private Paint tPaint;
		private Paint rPaint;
		private float bl;

		public SummaryOverlay() {
			preparePaints();
		}
		
		public void draw(Canvas canvas, MapView mapv, boolean shadow) {
			super.draw(canvas, mapv, shadow);
			
			String speed = String.format("%.2f km/h", context.getSpeed());
			String distance = String.format("%.2f km", 0.001f * context.getDistance());
			float seconds = context.getTime();
			String time = Helper.formatTime(seconds);
			
			tPaint.setTextAlign(Align.LEFT);
			canvas.drawRect(new RectF(0, 0, mapView.getWidth(), 5*bl), rPaint);
			canvas.drawText(speed, bl, 2*bl, tPaint);
			canvas.drawText(time, bl, 4*bl, tPaint);
			tPaint.setTextAlign(Align.RIGHT);
			canvas.drawText(distance, mapView.getWidth() - bl, 2*bl, tPaint);
		}
		
		private void preparePaints() {
			DisplayMetrics metrics = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(metrics);
			
			bl = 0.1f * metrics.densityDpi;
			
			tPaint = new Paint();
			tPaint.setAntiAlias(true);
			tPaint.setColor(Color.WHITE);
			tPaint.setStyle(Paint.Style.FILL_AND_STROKE);
			tPaint.setStrokeJoin(Paint.Join.ROUND);
			tPaint.setStrokeCap(Paint.Cap.ROUND);
			tPaint.setTextSize(metrics.densityDpi / 6.0f);
			tPaint.setStrokeWidth(metrics.densityDpi / 120.0f);
			tPaint.setShadowLayer(1, 1, 1, Color.rgb(60, 60, 60));

			rPaint = new Paint();
			rPaint.setColor(Color.BLACK);
			rPaint.setStyle(Paint.Style.FILL_AND_STROKE);
			rPaint.setAlpha(80);
		}
	}
	
	class MyOverlay extends Overlay {

		private Paint mPaint;

		private Path path = new Path();
				
		public MyOverlay() {
			preparePaints();
		}

		private void preparePaints() {
			mPaint = new Paint();
			mPaint.setAntiAlias(true);
			mPaint.setColor(Color.RED);
			mPaint.setAlpha(192);
			mPaint.setStyle(Paint.Style.STROKE);
			mPaint.setStrokeJoin(Paint.Join.ROUND);
			mPaint.setStrokeCap(Paint.Cap.ROUND);
			mPaint.setStrokeWidth(3);
		}
		
		public void draw(Canvas canvas, MapView mapv, boolean shadow) {
			super.draw(canvas, mapv, shadow);

			path.reset();
			
			ArrayList<TrackPoint> track = context.getTrack();
			
			if (track == null || track.size() == 0) {
				return;
			}
			
			int min_lat = 0, min_lon = 0, max_lat = 0, max_lon = 0;

			long lasttime = track.get(0).tim;

			Point p1 = new Point();
			Point p2 = p1;

			Rect r =  new Rect(0, 0, mapView.getWidth(), mapView.getHeight());
			
			for (int i = 0; i < track.size(); ++i) {
				
				if (track.get(i).tim - lasttime < 0) break;
				
				int lat = track.get(i).lat;
				int lon = track.get(i).lon;
				GeoPoint gP1 = new GeoPoint(lat, lon);
				projection.toPixels(gP1, p1);
				
				if (i == 0) {
					path.moveTo(p1.x, p1.y);
					min_lat = max_lat = lat;
					min_lon = max_lat = lon;
					p2 = p1;
				} else {
					// draw only visible part of path
					if (r.contains(p1.x, p1.y) || r.contains(p2.x, p2.y) )
						path.lineTo(p1.x, p1.y);
					else 
						path.moveTo(p1.x, p1.y);
					
					
					p2 = p1;
					
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
				switch (mode) {
				case FIT:
					myMapController.zoomToSpan(max_lat - min_lat, max_lon - min_lon);
					myMapController.setCenter(new GeoPoint(mean_lat, mean_lon));
					break;
				case FOLLOW:
					myMapController.setCenter(new GeoPoint(last_lat, last_lon));
					break;
				}	
			}
			itemizedOverlay.clear();
			itemizedOverlay.addOverlayItem(track.get(0).lat, track.get(0).lon, "I'm here", R.drawable.marker_start);
			itemizedOverlay.addOverlayItem(last_lat, last_lon, "I'm here", R.drawable.marker_end);
			canvas.drawPath(path, mPaint);
		}

	}

	public class MyItemizedOverlay extends ItemizedOverlay<OverlayItem> {

		private ArrayList<OverlayItem> myOverlays;
		private Context mContext;

		public MyItemizedOverlay(Drawable defaultMarker) {
			super(boundCenterBottom(defaultMarker));
			myOverlays = new ArrayList<OverlayItem>();
			populate();
		}

		public void clear() {
			myOverlays.clear();
			populate();
		}

		public MyItemizedOverlay(Drawable defaultMarker, Context context) {
			super(boundCenterBottom(defaultMarker));
			myOverlays = new ArrayList<OverlayItem>();
			mContext = context;
			populate();
		}

		public void addOverlay(OverlayItem overlay) {
			myOverlays.add(overlay);
			populate();
		}

		@Override
		protected OverlayItem createItem(int i) {
			return myOverlays.get(i);
		}

		// Removes overlay item i
		public void removeItem(int i) {
			myOverlays.remove(i);
			populate();
		}

		// Returns present number of items in list
		@Override
		public int size() {
			return myOverlays.size();
		}

		public void addOverlayItem(OverlayItem overlayItem) {
			myOverlays.add(overlayItem);
			populate();
		}

		public void addOverlayItem(int lat, int lon, String title, int id) {
			try {
				GeoPoint point = new GeoPoint(lat, lon);
				OverlayItem overlayItem = new OverlayItem(point, title, null);
				Drawable icon = getResources().getDrawable(id);
				icon.setBounds(-icon.getIntrinsicWidth() / 2, -icon.getIntrinsicHeight(), icon.getIntrinsicWidth() / 2, 0);
				overlayItem.setMarker(icon);
				addOverlayItem(overlayItem);
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
	}
}