package edu.wut.wpam.runwithme;

import java.util.List;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.os.Bundle;

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

			Path path = new Path();

			if (context.track == null || context.track.size() == 0) {
				return;
			}
			int min_lat = 0, min_lon = 0, max_lat = 0, max_lon = 0;

			for (int i = 0; i < context.track.size(); ++i) {
				int lat = context.track.get(i).lat;
				int lon = context.track.get(i).lon;
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
			int mean_lat = (min_lat + max_lat) / 2;
			int mean_lon = (min_lon + max_lon) / 2;
			if (System.currentTimeMillis() - mapView.getLast_touched() > 10000) {
				myMapController.setCenter(new GeoPoint(mean_lat, mean_lon));
			}

			canvas.drawPath(path, mPaint);
		}
	}

}