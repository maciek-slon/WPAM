package edu.wut.wpam.widgets;

import android.graphics.Canvas;
import android.view.MotionEvent;

import com.google.android.maps.MapView;

public class ITRMapView extends MapView {
	private Boolean touched = false;
	private long last_touched = 0;
	public Boolean getTouched() {
		return touched;
	}

	public void setTouched(Boolean touched) {
		this.touched = touched;
	}

	public ITRMapView(android.content.Context context,
			android.util.AttributeSet attrs) {
		super(context, attrs);
	}

	public ITRMapView(android.content.Context context,
			android.util.AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public ITRMapView(android.content.Context context, java.lang.String apiKey) {
		super(context, apiKey);
	}

	public boolean onTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_UP) {
			touched = true;
			last_touched = System.currentTimeMillis();
		}
		return super.onTouchEvent(ev);
	}

	public long getLast_touched() {
		return last_touched;
	}

	public void setLast_touched(long last_touched) {
		this.last_touched = last_touched;
	}

	int oldZoomLevel = -1;

	public void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		if (getZoomLevel() != oldZoomLevel) {
			touched = true;
			last_touched = System.currentTimeMillis();
			oldZoomLevel = getZoomLevel();
		}
	}
}