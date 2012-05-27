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
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

public class RouteActivity extends MapActivity {
	private List<Overlay> mapOverlays;

	private Projection projection;
	private RunAppContext context = RunAppContext.instance();
	private MapView mapView;
	
	@Override
	protected boolean isRouteDisplayed() {
	    return false;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.map);
	    
		mapView = (MapView) findViewById(R.id.mapview);
	    mapView.setBuiltInZoomControls(true);
	    
	    mapOverlays = mapView.getOverlays();        
	    projection = mapView.getProjection();
	    mapOverlays.add(new MyOverlay());
	    
	    context.addListener(mapView);
	}
	
	public void onDestroy() {
		context.removeListener(mapView);
		
		super.onDestroy();
	}
	
	class MyOverlay extends Overlay{

	    public MyOverlay(){

	    }   

	    public void draw(Canvas canvas, MapView mapv, boolean shadow){
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
	        
	        for (int i = 0; i < context.track.size(); ++i) {
	        	GeoPoint gP1 = new GeoPoint(context.track.get(i).lat, context.track.get(i).lon);
		        Point p1 = new Point();
		        projection.toPixels(gP1, p1);

		        if (i == 0)
		        	path.moveTo(p1.x, p1.y);
		        else
		        	path.lineTo(p1.x,p1.y);
	        }

	        canvas.drawPath(path, mPaint);
	    }
	}

}