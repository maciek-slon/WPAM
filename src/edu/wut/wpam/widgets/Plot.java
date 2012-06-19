package edu.wut.wpam.widgets;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class Plot extends View {

	private static final String TAG = Plot.class.getSimpleName();

	private ArrayList<PointF> elems;
	private float elem_max;
	private float elem_min;

	private float x_start;
	private float x_end;
	private float y_start;
	private float y_end;

	private float x_range;
	private float y_range;
	private float x_scale;
	private float y_scale;

	private float x_origin;
	private float y_origin;

	private ArrayList<Float> intervals;

	private float marker;

	public void setXStart(float start) {
		this.x_start = start;
	}

	public void setXEnd(float range) {
		this.x_end = range;
	}

	public void seYStart(float start) {
		this.y_start = start;
	}

	public void setYEnd(float range) {
		this.y_end = range;
	}

	public void setMarker(float m) {
		marker = m;
	}

	public Plot(Context context) {
		super(context);
		init();
	}

	public Plot(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		elem_max = 0.0f;
		elem_min = 1000f;

		x_start = 0.0f;
		x_end = 1800.0f;
		y_start = 0.0f;
		y_end = 5.0f;

		x_origin = 30.0f;
		y_origin = 10.0f;

		recalculateScales();

		marker = Float.NaN;

		elems = new ArrayList<PointF>();

		intervals = new ArrayList<Float>();
	}

	public void setIntervals(ArrayList<Float> i) {
		intervals = i;
	}
	
	public void setElems(ArrayList<PointF> points) {
		if (points == null)
			return;
		
		elems = points;
		
		x_end = 10;
		for (PointF elem : points) {
			if (elem.y > elem_max) elem_max = elem.y;
			if (elem.y < elem_min) elem_min = elem.y;
			if (elem.x > x_end)	x_end = elem.x;
		}
		
		recalculateScales();
	}

	public void add(PointF elem) {
		elems.add(elem);

		if (elem.y > elem_max) {
			elem_max = elem.y;
			recalculateScales();
		}
		
		if (elem.y < elem_min) {
			elem_min = elem.y;
			recalculateScales();
		}

		if (elem.x > x_end) {
			x_end = elem.x;
			recalculateScales();
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
		int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);

		int widthMeasure = MeasureSpec.getSize(widthMeasureSpec);
		int heightMeasure = MeasureSpec.getSize(heightMeasureSpec);

		int newWidth, newHeight;

		Log.d(TAG, "Width spec: " + MeasureSpec.toString(widthMeasureSpec));
		Log.d(TAG, "Height spec: " + MeasureSpec.toString(heightMeasureSpec));

		if (widthSpecMode == MeasureSpec.AT_MOST)
			newWidth = 3000;
		else
			newWidth = widthMeasure;

		if (heightSpecMode == MeasureSpec.AT_MOST)
			newHeight = 3000;
		else
			newHeight = heightMeasure;

		setMeasuredDimension(newWidth, newHeight);

		recalculateScales();
	}

	private void recalculateScales() {
		if (elem_max > y_end) {
			y_end = (float) (Math.floor(elem_max / 5) * 5 + 5);
		}
		
//		if (elem_min < y_start) {
			y_start = (float) (Math.floor(elem_min / 5) * 5);
//}

		x_range = x_end - x_start;
		y_range = y_end - y_start;

		x_scale = (getWidth() - x_origin) / x_range;
		y_scale = (getHeight() - y_origin - 10) / y_range;
	}

	private void drawAxes(Canvas canvas) {
		Paint paint = new Paint();
		paint.setColor(Color.WHITE);
		canvas.drawLine(x_origin, getHeight() - y_origin, x_origin, 10, paint);

		/*
		 * paint.setColor(Color.RED); canvas.drawLine(0, y_range, x_range,
		 * y_range, paint); canvas.drawLine(x_range, 0, x_range, y_range,
		 * paint);
		 */

		paint.setTextAlign(Align.RIGHT);
		float y_tick = y_range / 5;
		for (int i = 0; i <= 5; ++i) {
			float y = Math.round(getHeight() - y_origin - i * y_tick * y_scale);
			canvas.drawText(String.format("%.0f", y_start + i * y_tick), x_origin - 2, y + 5, paint);
			canvas.drawLine(x_origin, y, getWidth(), y, paint);
		}
	}

	private void drawIntervals(Canvas canvas) {
		if (intervals.isEmpty())
			return;

		Paint paint = new Paint();
		paint.setColor(Color.WHITE);
		float accum = 0;
		for (int i = 0; i < intervals.size(); ++i) {
			paint.setAlpha(40 + (i % 2) * 40);
			canvas.drawRect(accum, 0, accum + intervals.get(i), y_range, paint);
			accum += intervals.get(i);
		}
	}

	private void drawPlot(Canvas canvas) {
		if (elems.isEmpty())
			return;

		Paint paint = new Paint();
		paint.setColor(Color.rgb(255, 200, 0));
		PointF s = elems.get(0);
		PointF e = s;
		Path path = new Path();
		path.moveTo(x_start, y_start);
		path.lineTo(s.x, s.y);
		for (int i = 1; i < elems.size(); ++i) {
			s = elems.get(i - 1);
			e = elems.get(i);
			path.lineTo(e.x, e.y);
			canvas.drawLine(s.x, s.y, e.x, e.y, paint);
			if (s.x > x_end)
				break;
		}
		paint.setAlpha(128);
		path.lineTo(e.x, y_start);
		path.close();
		canvas.drawPath(path, paint);
	}

	public void drawMarker(Canvas canvas) {
		if (marker == Float.NaN)
			return;

		Paint paint = new Paint();
		paint.setColor(Color.WHITE);
		Log.d("Plot", "x_scale: " + x_scale);
		paint.setStrokeWidth(2 / Math.abs(x_scale));
		paint.setPathEffect(new DashPathEffect(new float[] {
				2 / Math.abs(y_scale), 4 / Math.abs(y_scale) }, 0));
		canvas.drawLine(marker, y_start, marker, y_end, paint);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		recalculateScales();
		Rect rect = new Rect(0, 0, getWidth(), getHeight());
		Paint paint = new Paint();
		paint.setColor(Color.BLACK);
		canvas.drawRect(rect, paint);

		drawAxes(canvas);

		canvas.translate(x_origin, getHeight() - y_origin);
		canvas.scale(x_scale, -y_scale);
		canvas.translate(-x_start, -y_start);

		drawIntervals(canvas);
		drawPlot(canvas);
		drawMarker(canvas);
	}
}
