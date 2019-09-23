/**
 * Ti.Paint Module
 * Copyright (c) 2010-2013 by Appcelerator, Inc. All Rights Reserved.
 * Please see the LICENSE included with this distribution for details.
 */

package ti.modules.titanium.paint;

import org.appcelerator.kroll.KrollDict;

import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.util.TiConvert;
import org.appcelerator.titanium.view.TiDrawableReference;
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

	public UIPaintView(TiViewProxy proxy) {
		super(proxy);

		props = proxy.getProperties();

		setPaintOptions(); // set initial paint options

		setNativeView(tiPaintView = new PaintView(proxy.getActivity()));

		if (props.containsKeyAndNotNull("image")) {
			tiPaintView.setImage(props.getString("image"));
		}
	}

	private void setPaintOptions() {
		tiPaint = new Paint();
		tiPaint.setAntiAlias(true);
		tiPaint.setDither(true);
		tiPaint.setColor((props.containsKeyAndNotNull("strokeColor")) ? TiConvert.toColor(props, "strokeColor") : TiConvert.toColor("black"));
		tiPaint.setStyle(Paint.Style.STROKE);
		tiPaint.setStrokeJoin(Paint.Join.ROUND);
		tiPaint.setStrokeCap(Paint.Cap.ROUND);
		tiPaint.setStrokeWidth((props.containsKeyAndNotNull("strokeWidth")) ? TiConvert.toInt(props.get("strokeWidth")) : 12);
		tiPaint.setAlpha((props.containsKeyAndNotNull("strokeAlpha")) ? TiConvert.toInt(props.get("strokeAlpha")) : 255);
		alphaState = (props.containsKeyAndNotNull("strokeAlpha")) ? TiConvert.toInt(props.get("strokeAlpha")) : 255;
	}

	public void setStrokeWidth(int width) {
		Log.d(LCAT, "Changing stroke width.");
		tiPaintView.finalizePaths();
		tiPaint.setStrokeWidth(TiConvert.toInt(width));
		tiPaint.setAlpha(alphaState);
	}


	public void setEraseMode(Boolean toggle) {
		eraseState = toggle;
		tiPaintView.finalizePaths();

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
		tiPaintView.finalizePaths();
		tiPaint.setColor(TiConvert.toColor(color));
		tiPaint.setAlpha(alphaState);
	}

	public void setStrokeAlpha(int alpha) {
		Log.d(LCAT, "Changing stroke alpha.");
		tiPaintView.finalizePaths();
		tiPaint.setAlpha(alpha);
		alphaState = alpha;
	}

	public void setImage(String imagePath) {
		Log.d(LCAT, "Changing image.");
		tiPaintView.setImage(imagePath);
	}

	public void clear() {
		Log.d(LCAT, "Clearing.");
		tiPaintView.clear();
	}

	public class PaintView extends View {

		private static final int maxTouchPoints = 20;

		private float[] tiX;
		private float[] tiY;

		private Path[] tiPaths;
		private Bitmap tiBitmap;
		private String tiImage;
		private Canvas tiCanvas;
		private Paint tiBitmapPaint;

		public PaintView(Context c) {
			super(c);
			tiBitmapPaint = new Paint(Paint.DITHER_FLAG);
			tiPaths = new Path[maxTouchPoints];
			tiX = new float[maxTouchPoints];
			tiY = new float[maxTouchPoints];
		}

		@Override
		protected void onSizeChanged(int w, int h, int oldw, int oldh) {
			super.onSizeChanged(w, h, oldw, oldh);

			if(tiBitmap == null){
				if (tiImage != null) {
					TiDrawableReference ref = TiDrawableReference.fromUrl(proxy, tiImage);
					tiBitmap = Bitmap.createScaledBitmap(ref.getBitmap(), w, h, true);
				} else {
					tiBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
				}
				tiCanvas = new Canvas(tiBitmap);
			}
			else {		
				tiBitmap = Bitmap.createScaledBitmap(tiBitmap, w, h, true);
				tiCanvas = new Canvas(tiBitmap);			
			}

		}


		@Override
		protected void onDraw(Canvas canvas) {
			boolean containsBG = props.containsKeyAndNotNull(TiC.PROPERTY_BACKGROUND_COLOR);
			canvas.drawColor(containsBG ? TiConvert.toColor(props, TiC.PROPERTY_BACKGROUND_COLOR) : TiConvert.toColor("transparent"));
			canvas.drawBitmap(tiBitmap, 0, 0, tiBitmapPaint);

			for (int i = 0; i < maxTouchPoints; i++) {
				if (tiPaths[i] != null) {
					canvas.drawPath(tiPaths[i], tiPaint);
				}
			}
		}

		private void touch_start(int id, float x, float y) {
			tiPaths[id] = new Path();
			tiPaths[id].moveTo(x, y);
			tiX[id] = x;
			tiY[id] = y;
		}

		private void touch_move(int id, float x, float y) {
			if (tiPaths[id] == null) {
				tiPaths[id] = new Path();
				tiPaths[id].moveTo(tiX[id], tiY[id]);
			}
			tiPaths[id].quadTo(tiX[id], tiY[id], (x + tiX[id]) / 2, (y + tiY[id]) / 2);
			tiX[id] = x;
			tiY[id] = y;
		}

		@Override
		public boolean onTouchEvent(MotionEvent mainEvent) {
			for (int i = 0; i < mainEvent.getPointerCount(); i++) {
				int id = mainEvent.getPointerId(i);
				float x = mainEvent.getX(i);
				float y = mainEvent.getY(i);
				int action = mainEvent.getAction();
				if (action > 6) {
					action = (action % 256) - 5;
				}
				switch (action) {
					case MotionEvent.ACTION_DOWN:
						finalizePath(id);
						touch_start(id, x, y);
						invalidate();
						break;
					case MotionEvent.ACTION_MOVE:
						touch_move(id, x, y);
						invalidate();
						break;
					case MotionEvent.ACTION_UP:
						finalizePath(id);
						invalidate();
						break;
				}
			}

			return true;
		}

		public void finalizePath(int id) {
			if (tiPaths[id] != null) {
				tiCanvas.drawPath(tiPaths[id], tiPaint);
				tiPaths[id].reset();
				tiPaths[id] = null;
			}
		}
		
		public void finalizePaths() {
			for (int i = 0; i < maxTouchPoints; i++) {
				if (tiPaths[i] != null) {
					tiCanvas.drawPath(tiPaths[i], tiPaint);
					tiPaths[i].reset();
					tiPaths[i] = null;
				}
			}
		}

		public void setImage(String imagePath) {
			Log.i(LCAT, "setImage called");
			tiImage = imagePath;
			if (tiImage == null) {
				clear();
			} else {
				finalizePaths();
				TiDrawableReference ref = TiDrawableReference.fromUrl(proxy, tiImage);
				tiBitmap = ref.getBitmap().copy(Bitmap.Config.ARGB_8888, true);
				tiCanvas = new Canvas(tiBitmap);
				invalidate();
			}
		}

		public void clear() {
			finalizePaths();
			tiBitmap.eraseColor(Color.TRANSPARENT);

			invalidate();
		}
	}

}