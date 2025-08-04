/**
 * Ti.Paint Module
 * Copyright (c) 2010-present by TiDev, Inc. All Rights Reserved.
 * Please see the LICENSE included with this distribution for details.
 */

package ti.modules.titanium.paint;

import org.appcelerator.kroll.KrollDict;

import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.io.TiBaseFile;
 import org.appcelerator.titanium.io.TiFileFactory;
import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.util.TiConvert;
import org.appcelerator.titanium.view.TiDrawableReference;
import org.appcelerator.titanium.view.TiUIView;

import android.content.Context;
import android.graphics.*;
import android.view.MotionEvent;
import android.view.View;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class UIPaintView extends TiUIView {
	private static final String LCAT = "UIPaintView";

	public Paint tiPaint;
	public PaintView tiPaintView;
	private KrollDict props;
	private Boolean eraseState = false;
	private int currentColor = -999999999;
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
		if (currentColor == -999999999) {
			currentColor = (props.containsKeyAndNotNull("strokeColor")) ? TiConvert.toColor(props, "strokeColor", TiApplication.getAppCurrentActivity()) : TiConvert.toColor("black",TiApplication.getAppCurrentActivity());
		}

		if (oldWidth == -1.0f) {
			oldWidth = (props.containsKeyAndNotNull("strokeWidth")) ? TiConvert.toFloat(props.get("strokeWidth")) : 12.0f;
		}

		if (alphaState == -1 ){
			alphaState = (props.containsKeyAndNotNull("strokeAlpha")) ? TiConvert.toInt(props.get("strokeAlpha")) : 255;
		}

		tiPaint = new Paint();

		tiPaint.setDither(true);
		tiPaint.setAntiAlias(true);
		tiPaint.setColor(currentColor);
		tiPaint.setStyle(Paint.Style.STROKE);
		// tiPaint.setBlendMode(BlendMode.MULTIPLY);
		tiPaint.setStrokeCap(Paint.Cap.ROUND);
		tiPaint.setStrokeJoin(Paint.Join.ROUND);

		tiPaint.setAlpha(alphaState);
		tiPaint.setStrokeWidth(oldWidth);
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
			tiPaint.setColor(TiConvert.toColor("black", TiApplication.getAppCurrentActivity()));
			tiPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
		} else {
			tiPaint.setXfermode(null);
		}
		tiPaintView.newPath();
	}

	public void setStrokeColor(String color) {
		Log.d(LCAT, "Changing stroke color.");
		currentColor = TiConvert.toColor(color, TiApplication.getAppCurrentActivity());
		tiPaint.setColor(currentColor);
		tiPaint.setAlpha(alphaState);
	}

	public void setStrokeAlpha(int alpha) {
		Log.d(LCAT, "Changing stroke alpha.");
		tiPaint.setAlpha(alpha);
		alphaState = alpha;
	}

	public void setImage(String _imagePath) {
		Log.d(LCAT, "Changing image.");
		tiPaintView.setImage(_imagePath);
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

		private static final int maxTouchPoints = 1;

		private float currentX, currentY;

		private Path mPath;
		private String tiImage;
		private Canvas tiCanvas;
		private Bitmap tiBitmap;
		private Paint tiBitmapPaint;
		private PathPaint pathPaint;
		private boolean enabled = true;
		private ArrayList<PathPaint> tiPaths = new ArrayList<PathPaint>();
		private ArrayList<PathPaint> undoPaths = new ArrayList<PathPaint>();

		public PaintView(Context c) {
			super(c);
			tiBitmapPaint = new Paint(Paint.DITHER_FLAG);
			mPath = new Path();

			pathPaint = new PathPaint();
			pathPaint.setPath(mPath);
			pathPaint.setPaint(tiPaint);
			pathPaint.setEarase(eraseState);

			setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		}

		@Override
		protected void onSizeChanged(int _width, int _height, int _oldWidth, int _oldHeight) {
			super.onSizeChanged(_width, _height, _oldWidth, _oldHeight);

			if(tiBitmap == null) {
				if (tiImage != null) {
					TiDrawableReference ref = TiDrawableReference.fromUrl(proxy, proxy.resolveUrl(null, tiImage));
 					if (ref.getBitmap() != null) {
 						tiBitmap = Bitmap.createScaledBitmap(ref.getBitmap(), _width, _height, true);
 					} else {
 						tiBitmap = Bitmap.createBitmap(_width, _height, Bitmap.Config.ARGB_8888);
 					}
				} else {
					tiBitmap = Bitmap.createBitmap(_width, _height, Bitmap.Config.ARGB_8888);
				}
				tiCanvas = new Canvas(tiBitmap);
			} else {
				tiBitmap = Bitmap.createScaledBitmap(tiBitmap, _width, _height, true);
				tiCanvas = new Canvas(tiBitmap);
			}
		}


		@Override
		protected void onDraw(Canvas canvas) {
			if (tiBitmap != null) {
 				canvas.drawBitmap(tiBitmap, 0, 0, null);
 			}

			for (PathPaint p : tiPaths) {
				canvas.drawPath(p.getPath(), p.getPaint());
			}

			canvas.drawPath(mPath, tiPaint);
		}

		public void touch_start(float _x, float _y) {
			setPaintOptions();
			undoPaths.clear();
			mPath.reset();
			mPath.moveTo(_x, _y);
			currentX = _x;
			currentY = _y;
		}

		public void enable(boolean enable) {
			enabled = enable;
		}

		public void touch_move(float _x, float _y) {
			mPath.quadTo(currentX, currentY, (_x + currentX) / 2, (_y + currentY) / 2);

			currentX = _x;
			currentY = _y;
		}

		public void touch_up() {
			mPath.lineTo(currentX, currentY);
			tiPaths.add(pathPaint);

			mPath = new Path();
			pathPaint = new PathPaint();
			pathPaint.setPath(mPath);
			pathPaint.setPaint(tiPaint);
			pathPaint.setEarase(eraseState);
		}

		public void newPath(){
			mPath = new Path();
			pathPaint = new PathPaint();
			pathPaint.setPath(mPath);
			pathPaint.setPaint(tiPaint);
			pathPaint.setEarase(eraseState);
		}

		public void undo() {
			if (tiPaths.size()>0) {
				 undoPaths.add(tiPaths.remove(tiPaths.size() - 1));
				 invalidate();
			}
		}

		public void redo() {
			if (undoPaths.size() > 0) {
				tiPaths.add(undoPaths.remove(undoPaths.size() - 1));
				invalidate();
			}
		}

		@Override
		public boolean onTouchEvent(MotionEvent _mainEvent) {
			if (enabled && _mainEvent.getPointerCount() <= maxTouchPoints) {
				for (int i = 0; i < _mainEvent.getPointerCount(); i++) {
					float x = _mainEvent.getX(i);
					float y = _mainEvent.getY(i);
					int action = _mainEvent.getAction();
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


		public void setImage(String _imagePath) {
			Log.d(LCAT, "setImage called");
			tiImage = _imagePath;
			if (tiImage == null) {
				clear();
			} else {
				TiDrawableReference ref = TiDrawableReference.fromUrl(proxy, proxy.resolveUrl(null, tiImage));
 				if (ref.getBitmap() != null) {
 					tiBitmap = ref.getBitmap().copy(Bitmap.Config.ARGB_8888, true);
 					tiCanvas = new Canvas(tiBitmap);
 					invalidate();
 				}
			}
		}

		public void clear() {
			tiBitmap.eraseColor(Color.TRANSPARENT);
			tiPaths.clear();
			invalidate();
		}
	}

}
