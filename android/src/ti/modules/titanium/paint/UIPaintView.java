/**
 * Copyright (c) 2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Apache Public License
 * Please see the LICENSE included with this distribution for details.
 * Portions copyright (C) 2007 The Android Open Source Project
 * Author: Fred Spencer (fspencer@appcelerator.com)
 */

package ti.modules.titanium.paint;

import org.appcelerator.kroll.KrollDict;

import org.appcelerator.titanium.util.Log;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.util.TiConvert;
import org.appcelerator.titanium.view.TiUIView;

import android.content.Context;
import android.graphics.*;
import android.view.MotionEvent;
import android.view.View;

public class UIPaintView extends TiUIView {
	private static final String LCAT = "UIPaintView";

	public Paint tiPaint;
	public PaintView tiPaintView;
	private KrollDict props;
	private Boolean eraseState = false;
	private int alphaState = 255; // alpha resets on changes, so store

	private void setPaintOptions() {
		tiPaint = new Paint();
		tiPaint.setAntiAlias(true);
		tiPaint.setDither(true);
		tiPaint.setColor((props.containsKeyAndNotNull("strokeColor")) ? TiConvert.toColor(props, "strokeColor") : TiConvert.toColor("black"));
		tiPaint.setStyle(Paint.Style.STROKE);
		tiPaint.setStrokeJoin(Paint.Join.ROUND);
		tiPaint.setStrokeCap(Paint.Cap.ROUND);
		tiPaint.setStrokeWidth((props.containsKeyAndNotNull("strokeWidth")) ? TiConvert.toFloat(props.get("strokeWidth")) : 12);
		tiPaint.setAlpha((props.containsKeyAndNotNull("strokeAlpha")) ? TiConvert.toInt(props.get("strokeAlpha")) : 255);
		alphaState = (props.containsKeyAndNotNull("strokeAlpha")) ? TiConvert.toInt(props.get("strokeAlpha")) : 255;
	}

	public class PaintView extends View {
		private Bitmap  tiBitmap;
		private Canvas  tiCanvas;
		private Path    tiPath;
		private Paint   tiBitmapPaint;

		public PaintView(Context c) {
			super(c);

			tiPath = new Path();
			tiBitmapPaint = new Paint(Paint.DITHER_FLAG);
		}

		@Override
		protected void onSizeChanged(int w, int h, int oldw, int oldh) {
			super.onSizeChanged(w, h, oldw, oldh);
			tiBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
			tiCanvas = new Canvas(tiBitmap);
		}

		@Override
		protected void onDraw(Canvas canvas) {
			canvas.drawColor((props.containsKeyAndNotNull(TiC.PROPERTY_BACKGROUND_COLOR)) ? TiConvert.toColor(props, TiC.PROPERTY_BACKGROUND_COLOR) : TiConvert.toColor("transparent") );

			canvas.drawBitmap(tiBitmap, 0, 0, tiBitmapPaint);

			canvas.drawPath(tiPath, tiPaint);
		}

		private float tiX, tiY;
		private static final float TOUCH_TOLERANCE = 4;

		private void touch_start(float x, float y) {
			tiPath.reset();
			tiPath.moveTo(x, y);
			tiX = x;
			tiY = y;
		}
		private void touch_move(float x, float y) {
			float dx = Math.abs(x - tiX);
			float dy = Math.abs(y - tiY);
			if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
				tiPath.quadTo(tiX, tiY, (x + tiX)/2, (y + tiY)/2);
				tiX = x;
				tiY = y;
			}
		}
		private void touch_up() {
			tiPath.lineTo(tiX, tiY);
			tiCanvas.drawPath(tiPath, tiPaint);
			tiPath.reset();
		}

		@Override
		public boolean onTouchEvent(MotionEvent event) {
			float x = event.getX();
			float y = event.getY();

			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				touch_start(x, y);
				invalidate();
				break;
			case MotionEvent.ACTION_MOVE:
				touch_move(x, y);
				invalidate();
				break;
			case MotionEvent.ACTION_UP:
				touch_up();
				invalidate();
				break;
			}
			return true;
		}

		public void clear() {
		    tiBitmap.eraseColor(Color.TRANSPARENT);
			invalidate();
		}
	}

	public void setStrokeWidth(Float width) {
		Log.d(LCAT, "Changing stroke width.");
		tiPaint.setStrokeWidth(width);
		tiPaint.setAlpha(alphaState);
	}

	public void setEraseMode(Boolean toggle) {		
		eraseState = toggle;

		if (eraseState) {
			Log.d(LCAT, "Setting Erase Mode to True.");
			tiPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
		} else {
			Log.d(LCAT, "Setting Erase Mode to False.");
			tiPaint.setXfermode(null);
		}

		tiPaint.setAlpha(alphaState);
	}

	public void setStrokeColor(String color) {
		Log.d(LCAT, "Changing stroke color.");
		tiPaint.setColor(TiConvert.toColor(color));
		tiPaint.setAlpha(alphaState);
	}

	public void setStrokeAlpha(int alpha) {
		Log.d(LCAT, "Changing stroke alpha.");
		tiPaint.setAlpha(alpha);
		alphaState = alpha;
	}

	public void clear() {
		Log.d(LCAT, "Clearing.");
		tiPaintView.clear();
	}

	public UIPaintView(TiViewProxy proxy) {
		super(proxy);

		props = proxy.getProperties();

		setPaintOptions(); // set initial paint options

		setNativeView(tiPaintView = new PaintView(proxy.getContext()));
	}

}