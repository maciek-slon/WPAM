package edu.wut.wpam.widgets;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.format.Time;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public final class StopWatch extends View {

	private static final String TAG = StopWatch.class.getSimpleName();
	
	// drawing tools
	private RectF rimRect;
	private Paint rimPaint;
	private Paint rimCirclePaint;
	
	private RectF faceRect;
	private Paint facePaint;
	private Paint rimShadowPaint;
	
	private Paint scalePaint;
	private RectF scaleRect;
	
	private Paint titlePaint;	
	private Path titlePath;
	
	private Paint handPaint;
	private Path handPath;
	private Paint handScrewPaint;
	
	private Paint backgroundPaint;
	
	private Paint smallPaint;
	private Paint smallBlackPaint;
	private RectF smallDial;
	private Path smallHandPath;
	
	private Paint logoPaint;
	// end drawing tools
	
	private Bitmap background; // holds the cached static part
	
	// scale configuration
	private static final int totalNicks = 300;
	private static final float degreesPerNick = 360.0f / totalNicks;	
	private static final int centerDegree = 40; // the one in the top center (12 o'clock)
	
	// hand dynamics -- all are angular expressed in F degrees
	private boolean handInitialized = false;
	private float handPosition = centerDegree;
	private float handTarget = centerDegree;
	private float handVelocity = 0.0f;
	private float handAcceleration = 0.0f;
	private long lastHandMoveTime = -1L;

	private boolean running;

	private float mins;
	private float secs;
	private long starttime;
	private long curtime;
	
	
	public StopWatch(Context context) {
		super(context);
		init();
	}

	public StopWatch(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public StopWatch(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	
	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
	}
	
	@Override
	protected void onRestoreInstanceState(Parcelable state) {
		Bundle bundle = (Bundle) state;
		Parcelable superState = bundle.getParcelable("superState");
		super.onRestoreInstanceState(superState);
		
		handInitialized = bundle.getBoolean("handInitialized");
		handPosition = bundle.getFloat("handPosition");
		handTarget = bundle.getFloat("handTarget");
		handVelocity = bundle.getFloat("handVelocity");
		handAcceleration = bundle.getFloat("handAcceleration");
		lastHandMoveTime = bundle.getLong("lastHandMoveTime");
	}

	@Override
	protected Parcelable onSaveInstanceState() {
		Parcelable superState = super.onSaveInstanceState();
		
		Bundle state = new Bundle();
		state.putParcelable("superState", superState);
		state.putBoolean("handInitialized", handInitialized);
		state.putFloat("handPosition", handPosition);
		state.putFloat("handTarget", handTarget);
		state.putFloat("handVelocity", handVelocity);
		state.putFloat("handAcceleration", handAcceleration);
		state.putLong("lastHandMoveTime", lastHandMoveTime);
		return state;
	}

	private void init() {
		initDrawingTools();
		
		running = true;
		starttime = System.currentTimeMillis();
	}

	private void setTime(float s) {
		mins = s / 60;
		secs = s % 60;
		
	}

	private String getTitle() {
		return "StopWatch";
	}

	private void initDrawingTools() {
		rimRect = new RectF(0.05f, 0.05f, 0.95f, 0.95f);

		// the linear gradient is a bit skewed for realism
		rimPaint = new Paint();
		rimPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
		rimPaint.setShader(new LinearGradient(0.40f, 0.0f, 0.60f, 1.0f, 
										   Color.rgb(0xf0, 0xf5, 0xf0),
										   Color.rgb(0x30, 0x31, 0x30),
										   Shader.TileMode.CLAMP));		

		facePaint = new Paint();
		facePaint.setColor(Color.rgb(200, 200, 200));
		
		rimCirclePaint = new Paint();
		rimCirclePaint.setAntiAlias(true);
		rimCirclePaint.setStyle(Paint.Style.STROKE);
		rimCirclePaint.setColor(Color.argb(0x4f, 0x33, 0x36, 0x33));
		rimCirclePaint.setStrokeWidth(0.005f);

		float rimSize = 0.02f;
		faceRect = new RectF();
		faceRect.set(rimRect.left + rimSize, rimRect.top + rimSize, 
			     rimRect.right - rimSize, rimRect.bottom - rimSize);		

		

		rimShadowPaint = new Paint();
		rimShadowPaint.setShader(new RadialGradient(0.5f, 0.5f, faceRect.width() / 2.0f, 
				   new int[] { 0x00000000, 0x00000500, 0x50000500 },
				   new float[] { 0.96f, 0.96f, 0.99f },
				   Shader.TileMode.MIRROR));
		rimShadowPaint.setStyle(Paint.Style.FILL);

		scalePaint = new Paint();
		scalePaint.setStyle(Paint.Style.STROKE);
		scalePaint.setColor(Color.BLACK);
		scalePaint.setStrokeWidth(0.005f);
		scalePaint.setAntiAlias(true);
		
		scalePaint.setTextSize(0.045f);
		scalePaint.setTypeface(Typeface.SANS_SERIF);
		scalePaint.setTextScaleX(0.7f);
		scalePaint.setTextAlign(Paint.Align.CENTER);		
		
		float scalePosition = 0.01f;
		scaleRect = new RectF();
		scaleRect.set(faceRect.left + scalePosition, faceRect.top + scalePosition,
					  faceRect.right - scalePosition, faceRect.bottom - scalePosition);

		titlePaint = new Paint();
		titlePaint.setColor(Color.rgb(20, 10, 10));
		titlePaint.setAntiAlias(true);
		titlePaint.setTextAlign(Paint.Align.CENTER);
		titlePaint.setTextSize(0.05f);
		titlePaint.setTextScaleX(0.7f);

		titlePath = new Path();
		titlePath.addArc(new RectF(0.24f, 0.24f, 0.76f, 0.76f), -180.0f, -180.0f);

		

		handPaint = new Paint();
		handPaint.setAntiAlias(true);
		handPaint.setColor(0xff392f2c);		
		handPaint.setShadowLayer(0.01f, -0.005f, -0.005f, 0x7f000000);
		handPaint.setStyle(Paint.Style.FILL);	
		
		handPath = new Path();
		handPath.moveTo(0.5f, 0.5f + 0.2f);
		handPath.lineTo(0.5f - 0.010f, 0.5f + 0.2f - 0.007f);
		handPath.lineTo(0.5f - 0.001f, 0.5f - 0.41f);
		handPath.lineTo(0.5f + 0.001f, 0.5f - 0.41f);
		handPath.lineTo(0.5f + 0.010f, 0.5f + 0.2f - 0.007f);
		handPath.lineTo(0.5f, 0.5f + 0.2f);
		handPath.addCircle(0.5f, 0.5f, 0.025f, Path.Direction.CW);
		
		handScrewPaint = new Paint();
		handScrewPaint.setAntiAlias(true);
		handScrewPaint.setColor(0xff493f3c);
		handScrewPaint.setStyle(Paint.Style.FILL);
		
		backgroundPaint = new Paint();
		backgroundPaint.setFilterBitmap(true);
		
		smallPaint = new Paint();
		smallPaint.setAntiAlias(true);
		smallPaint.setStrokeCap(Cap.BUTT);
		smallPaint.setStrokeWidth(0.007f);
		smallPaint.setStyle(Style.STROKE);
		smallPaint.setColor(Color.rgb(164, 0, 0));
		
		smallBlackPaint = new Paint();
		smallBlackPaint.setAntiAlias(true);
		smallBlackPaint.setColor(Color.BLACK);
		smallPaint.setStyle(Style.STROKE);
		
		float smallDialSize = 0.12f;
		smallDial = new RectF(-smallDialSize, -smallDialSize, smallDialSize, smallDialSize);
		
		float sh = 0.07f;
		float lo = 0.13f;
		smallHandPath = new Path();
		smallHandPath.moveTo(      0,  sh);
		smallHandPath.lineTo(-0.010f,  sh - 0.007f);
		smallHandPath.lineTo(-0.001f, -lo);
		smallHandPath.lineTo( 0.001f, -lo);
		smallHandPath.lineTo( 0.010f,  sh - 0.007f);
		smallHandPath.lineTo(      0,  sh);
		smallHandPath.addCircle(0, 0, 0.025f, Path.Direction.CW);
		
		logoPaint = new Paint();
		logoPaint.setAntiAlias(true);
		logoPaint.setColor(Color.rgb(20, 10, 10));
		logoPaint.setStyle(Style.STROKE);
		logoPaint.setStrokeWidth(0.008f);
		logoPaint.setStrokeCap(Cap.ROUND);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {		
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);
		
		int chosenWidth = chooseDimension(widthMode, widthSize);
		int chosenHeight = chooseDimension(heightMode, heightSize);
		
		int chosenDimension = Math.min(chosenWidth, chosenHeight);
		
		setMeasuredDimension(chosenDimension, chosenDimension);
	}
	
	private int chooseDimension(int mode, int size) {
		if (mode == MeasureSpec.AT_MOST || mode == MeasureSpec.EXACTLY) {
			return size;
		} else { // (mode == MeasureSpec.UNSPECIFIED)
			return getPreferredSize();
		} 
	}
	
	// in case there is no size specified
	private int getPreferredSize() {
		return 300;
	}

	private void drawRim(Canvas canvas) {
		// first, draw the metallic body
		canvas.drawOval(rimRect, rimPaint);
		// now the outer rim circle
		canvas.drawOval(rimRect, rimCirclePaint);
	}
	
	private void drawFace(Canvas canvas) {		
		canvas.drawOval(faceRect, facePaint);
		// draw the inner rim circle
		canvas.drawOval(faceRect, rimCirclePaint);
		// draw the rim shadow inside the face
		canvas.drawOval(faceRect, rimShadowPaint);
	}

	private void drawScale(Canvas canvas) {
		canvas.drawOval(scaleRect, scalePaint);

		canvas.save(Canvas.MATRIX_SAVE_FLAG);
		
		scalePaint.setStrokeWidth(0.0015f);
		scalePaint.setColor(Color.BLACK);
		for (int i = 0; i < totalNicks; ++i) {
			float y1 = scaleRect.top;
			float y2 = y1 + 0.014f;

			scalePaint.setStrokeWidth(0.0015f);
			if (i % 10 == 0) {
				scalePaint.setStrokeWidth(0.003f);
				y2 = y1 + 0.036f;
				
			} else if (i % 5 == 0) {
				y2 = y1 + 0.024f;
			}
			
			
			canvas.drawLine(0.5f, y1, 0.5f, y2, scalePaint);
			
			canvas.rotate(degreesPerNick, 0.5f, 0.5f);
		}
		canvas.restore();		
	}
	
	private void drawNumbers(Canvas canvas) {
		canvas.save(Canvas.MATRIX_SAVE_FLAG);
		canvas.translate(0.5f, 0.5f);

		scalePaint.setStyle(Style.FILL_AND_STROKE);
		
		
		float scale = (float) getWidth();
		Rect bounds = new Rect();
		
		scalePaint.setColor(Color.BLACK);
		scalePaint.setTextScaleX(0.7f);
		
		float fontSize = 0.063f;
		scalePaint.setTextSize(fontSize * scale);
		scalePaint.getTextBounds("0", 0, 1, bounds);
		scalePaint.setTextSize(fontSize);
		float offy = bounds.height() / scale / 2;
		
		float x, y;
		float r = 0.35f;
		for (int i = 2; i <= 30; i+=2) {
			x = (float) (r * Math.cos(Math.toRadians(i * 12 - 90)));
			y = (float) (r * Math.sin(Math.toRadians(i * 12 - 90)));
						
			String text = Integer.toString(i);
			

			
			
			y = y + offy;
			canvas.drawText(text, x, y, scalePaint);
		}
		
		scalePaint.setColor(Color.rgb(128, 0, 0));
		scalePaint.setTextScaleX(1.0f);
		
		r = 0.33f;
		fontSize = 0.046f;
		scalePaint.setTextSize(fontSize * scale);
		scalePaint.getTextBounds("0", 0, 1, bounds);
		scalePaint.setTextSize(fontSize);
		offy = bounds.height() / scale / 2;
		
		for (int i = 1; i <= 30; i+=2) {
			x = (float) (r * Math.cos(Math.toRadians(i * 12 - 90)));
			y = (float) (r * Math.sin(Math.toRadians(i * 12 - 90)));
						
			String valueString = Integer.toString(i+30);
			y = y + offy;
			canvas.drawText(valueString, x, y, scalePaint);
		}
		
		canvas.restore();
	}
	
	private void drawSmallDial(Canvas canvas) { 
		canvas.save(Canvas.MATRIX_SAVE_FLAG);
		canvas.translate(0.5f, 0.33f);
		canvas.rotate(-90);


		smallBlackPaint.setStrokeWidth(0.002f);
		smallBlackPaint.setStyle(Style.STROKE);
		
		canvas.drawCircle(0, 0, -smallDial.top+0.0035f, smallBlackPaint);
		canvas.drawCircle(0, 0, -smallDial.top-0.0035f, smallBlackPaint);
		
		smallBlackPaint.setStrokeWidth(0.004f);
		
		for (int i = 0; i < 15; ++i) {
			canvas.drawArc(smallDial, 12, 12, false, smallPaint);
			canvas.drawLine(-smallDial.top-0.0035f, 0, -smallDial.top+0.015f, 0, smallBlackPaint);

			canvas.rotate(24);
		}
		
		canvas.restore();
	}
	
	private void drawSmallNumbers(Canvas canvas) {
		canvas.save(Canvas.MATRIX_SAVE_FLAG);
		canvas.translate(0.5f, 0.33f);

		scalePaint.setStyle(Style.FILL_AND_STROKE);
		
		
		float scale = (float) getWidth();
		Rect bounds = new Rect();
		
		scalePaint.setColor(Color.BLACK);
		scalePaint.setTextScaleX(1.0f);
		
		float fontSize = 0.028f;
		scalePaint.setTextSize(fontSize * scale);
		scalePaint.getTextBounds("0", 0, 1, bounds);
		scalePaint.setTextSize(fontSize);
		float offy = bounds.height() / scale / 2;
		
		float x, y;
		float r = 0.095f;
		for (int i = 1; i <= 15; i++) {
			x = (float) (r * Math.cos(Math.toRadians(i * 24 - 90)));
			y = (float) (r * Math.sin(Math.toRadians(i * 24 - 90)));
						
			String text = Integer.toString(i);

			y = y + offy;
			canvas.drawText(text, x, y, scalePaint);
		}
				
		canvas.restore();
	}
	
	private void drawLogo(Canvas canvas) {
		canvas.save(Canvas.MATRIX_SAVE_FLAG);
	
		float f = 0.05f;
		float ff = f * 0.3f;
		
		canvas.translate(0.5f, 0.65f);
		canvas.drawCircle(0, 0, f, logoPaint);
		canvas.drawCircle(0, 0, f*0.08f, logoPaint);
		canvas.drawLine(0, f*0.25f, 0, -f*0.7f, logoPaint);
		
		canvas.translate(0, -f*1.5f);
		canvas.drawArc(new RectF(-ff, -ff, ff, ff), 120, 300, false, logoPaint);
		
		canvas.translate(0, f*1.5f);
		canvas.rotate(-45);
		canvas.drawLine(0, -f, 0, -f*1.2f, logoPaint);
		
		canvas.restore();
	}
	
	private void drawTitle(Canvas canvas) {
		String title = getTitle();
		title = "Сделано в СССР";
		canvas.drawTextOnPath(title, titlePath, 0.0f,0.0f, titlePaint);				
	}

	private void drawHand(Canvas canvas) {
		float handAngle = secs * 12;
		canvas.save(Canvas.MATRIX_SAVE_FLAG);
		canvas.rotate(handAngle, 0.5f, 0.5f);
		canvas.drawPath(handPath, handPaint);
		canvas.restore();
		
		canvas.drawCircle(0.5f, 0.5f, 0.01f, handScrewPaint);
	}
	
	private void drawSmallHand(Canvas canvas) {
		float handAngle = mins * 24;
		canvas.save(Canvas.MATRIX_SAVE_FLAG);
		canvas.translate(0.5f, 0.33f);
		canvas.rotate(handAngle);
		canvas.drawPath(smallHandPath, handPaint);
		
		canvas.drawCircle(0, 0, 0.01f, handScrewPaint);
		canvas.restore();
	}

	private void drawBackground(Canvas canvas) {
		if (background == null) {
			Log.w(TAG, "Background not created");
		} else {
			canvas.drawBitmap(background, 0, 0, backgroundPaint);
		}
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		drawBackground(canvas);

		float scale = (float) getWidth();		
		canvas.save(Canvas.MATRIX_SAVE_FLAG);
		canvas.scale(scale, scale);

		drawSmallHand(canvas);
		drawHand(canvas);
		
		canvas.restore();
		
		if (running) {
			float timediff = 0.001f * (System.currentTimeMillis() - starttime);
			setTime(timediff);
			invalidate();
		}
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		Log.d(TAG, "Size changed to " + w + "x" + h);
		
		regenerateBackground();
	}
	
	private void regenerateBackground() {
		// free the old bitmap
		if (background != null) {
			background.recycle();
		}
		
		background = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
		Canvas backgroundCanvas = new Canvas(background);
		float scale = (float) getWidth();		
		backgroundCanvas.scale(scale, scale);
		
		drawRim(backgroundCanvas);
		drawFace(backgroundCanvas);
		drawScale(backgroundCanvas);
		drawNumbers(backgroundCanvas);
		drawSmallDial(backgroundCanvas);
		drawSmallNumbers(backgroundCanvas);
		drawTitle(backgroundCanvas);	
		drawLogo(backgroundCanvas);
	}
}
