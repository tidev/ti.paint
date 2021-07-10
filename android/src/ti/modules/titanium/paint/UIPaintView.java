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

import java.util.ArrayList;

public class UIPaintView extends TiUIView {
	private static final String LCAT = "UIPaintView";

	public Paint tiPaint;
	public PaintView tiPaintView;
	private KrollDict props;
	private Boolean eraseState = false;
	private int currentColor = -1;
	private int alphaState = -1;
	private Float oldWidth = -1.0f;

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
		if (currentColor == -1) {
			currentColor = (props.containsKeyAndNotNull("strokeColor")) ? TiConvert.toColor(props, "strokeColor") : TiConvert.toColor("black");
		}

		if (oldWidth == -1.0f) {
			oldWidth = (props.containsKeyAndNotNull("strokeWidth")) ? TiConvert.toFloat(props.get("strokeWidth")) : 12.0f;
		}

		if (alphaState == -1 ){
			alphaState = (props.containsKeyAndNotNull("strokeAlpha")) ? TiConvert.toInt(props.get("strokeAlpha")) : 255;
		}
		tiPaint = new Paint();
		tiPaint.setAntiAlias(true);
		tiPaint.setDither(true);
		tiPaint.setColor(currentColor);
		tiPaint.setStyle(Paint.Style.STROKE);
		tiPaint.setStrokeJoin(Paint.Join.ROUND);
		tiPaint.setStrokeCap(Paint.Cap.ROUND);

		tiPaint.setStrokeWidth(oldWidth);
		tiPaint.setAlpha(alphaState);

	}

	public void setStrokeWidth(Float width) {
		Log.d(LCAT, "Changing stroke width.");
		tiPaint.setStrokeWidth(width);
		tiPaint.setAlpha(alphaState);
		oldWidth = width;
	}


	public void setEraseMode(Boolean toggle) {
		eraseState = toggle;
		if (eraseState) {
			tiPaint.setColor(TiConvert.toColor("black"));
            tiPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
		} else {
			tiPaint.setXfermode(null);
		}
		tiPaintView.newPath();
	}

	public void setStrokeColor(String color) {
		Log.d(LCAT, "Changing stroke color.");
		currentColor = TiConvert.toColor(color);
		tiPaint.setColor(currentColor);
		tiPaint.setAlpha(alphaState);
	}

	public void setStrokeAlpha(int alpha) {
		Log.d(LCAT, "Changing stroke alpha.");
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

	public void moveTo(int x, int y) {
		tiPaintView.touch_up();
		tiPaintView.touch_start( x, y);
		tiPaintView.invalidate();
	}

	public void lineTo(int x, int y) {
		tiPaintView.touch_move( x, y);
		tiPaintView.invalidate();
	}

	public void enable(boolean enable) {
		tiPaintView.enable(enable);
	}

	public void undo() {
		tiPaintView.undo();
	}

	public void redo() {
		tiPaintView.redo();
	}

	public class PaintView extends View {

		private static final int maxTouchPoints = 20;

		private float mX, mY;

		private ArrayList<PathPaint> tiPaths = new ArrayList<PathPaint>();
		private ArrayList<PathPaint> undoPaths = new ArrayList<PathPaint>();
		private Path    mPath;
		private Bitmap tiBitmap;
		private String tiImage;
		private Canvas tiCanvas;
		private Paint tiBitmapPaint;
		private boolean enabled = true;
		private PathPaint pp;

		public PaintView(Context c) {
			super(c);
			tiBitmapPaint = new Paint(Paint.DITHER_FLAG);
			mPath = new Path();

			pp = new PathPaint();
			pp.setPath(mPath);
			pp.setPaint(tiPaint);
			pp.setEarase(eraseState);

			setLayerType(View.LAYER_TYPE_SOFTWARE, null);
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
			// boolean containsBG = props.containsKeyAndNotNull(TiC.PROPERTY_BACKGROUND_COLOR);
			// canvas.drawColor(containsBG ? TiConvert.toColor(props, TiC.PROPERTY_BACKGROUND_COLOR) : TiConvert.toColor("transparent"));
			// canvas.drawBitmap(tiBitmap, 0, 0, tiBitmapPaint);

			for (PathPaint p : tiPaths) {
				canvas.drawPath(p.getPath(), p.getPaint());
			}
			canvas.drawPath(mPath, tiPaint);

		}

		public void touch_start(float x, float y) {
			setPaintOptions();
			undoPaths.clear();
            mPath.reset();
            mPath.moveTo(x, y);
			mX = x;
            mY = y;
		}

		public void enable(boolean enable) {
			enabled = enable;
		}

		public void touch_move(float x, float y) {
			mPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);

			mX = x;
			mY = y;
		}

		public void touch_up() {
			mPath.lineTo(mX, mY);
			tiCanvas.drawPath(mPath, tiPaint);
			tiPaths.add(pp);

			mPath = new Path();
			pp = new PathPaint();
			pp.setPath(mPath);
			pp.setPaint(tiPaint);
			pp.setEarase(eraseState);
		}

		public void newPath(){
			mPath = new Path();
			pp = new PathPaint();
			pp.setPath(mPath);
			pp.setPaint(tiPaint);
			pp.setEarase(eraseState);
		}

		public void undo() {
            if (tiPaths.size()>0) {
               undoPaths.add(tiPaths.remove(tiPaths.size()-1));
               invalidate();
            }
        }

		public void redo() {
			if (undoPaths.size()>0) {
				tiPaths.add(undoPaths.remove(undoPaths.size()-1));
				invalidate();
			}
        }

		@Override
		public boolean onTouchEvent(MotionEvent mainEvent) {
			if (enabled) {
				for (int i = 0; i < mainEvent.getPointerCount(); i++) {
					float x = mainEvent.getX(i);
					float y = mainEvent.getY(i);
					int action = mainEvent.getAction();
					if (action > 6) {
						action = (action % 256) - 5;
					}
					switch (action) {
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
				}
			}
			return true;
		}


		public void setImage(String imagePath) {
			Log.i(LCAT, "setImage called");
			tiImage = imagePath;
			if (tiImage == null) {
				clear();
			} else {
				TiDrawableReference ref = TiDrawableReference.fromUrl(proxy, tiImage);
				tiBitmap = ref.getBitmap().copy(Bitmap.Config.ARGB_8888, true);
				tiCanvas = new Canvas(tiBitmap);
				invalidate();
			}
		}

		public void clear() {
			tiBitmap.eraseColor(Color.TRANSPARENT);
			tiPaths.clear();
			invalidate();
		}
	}

}
