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
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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

    // Create PaintView with proper initialization order
    tiPaintView = new PaintView(proxy.getActivity());

    // Defer initialization to avoid this-escape warning
    // This will be called after constructor completes
    proxy.getActivity().runOnUiThread(new Runnable() {
      @Override
      public void run() {
        initializeNativeView();
        
        if (props.containsKeyAndNotNull("image")) {
          tiPaintView.setImage(props.getString("image"));
        }
      }
    });
  }

  private void setPaintOptions() {
    if (currentColor == -999999999) {
      currentColor = (props.containsKeyAndNotNull("strokeColor"))
          ? TiConvert.toColor(props, "strokeColor", TiApplication.getAppCurrentActivity())
          : TiConvert.toColor("black", TiApplication.getAppCurrentActivity());
    }

    if (oldWidth == -1.0f) {
      oldWidth = (props.containsKeyAndNotNull("strokeWidth")) ? TiConvert.toFloat(props.get("strokeWidth")) : 12.0f;
    }

    if (alphaState == -1) {
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

  private void initializeNativeView() {
    setNativeView(tiPaintView);
    tiPaintView.initializeView();
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
    tiPaintView.touch_start(x, y);
    tiPaintView.invalidate();
  }

  public void lineTo(int x, int y) {
    tiPaintView.touch_move(x, y);
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

  // Playback wrapper methods
  public void playbackDrawing(float durationSeconds) {
    tiPaintView.playbackDrawing(durationSeconds);
  }

  public void pausePlayback() {
    tiPaintView.pausePlayback();
  }

  public void resumePlayback() {
    tiPaintView.resumePlayback();
  }

  public void stopPlayback() {
    tiPaintView.stopPlayback();
  }

  public void setPlaybackSpeed(float speed) {
    tiPaintView.setPlaybackSpeed(speed);
  }

  public float getPlaybackProgress() {
    return tiPaintView.getPlaybackProgress();
  }

  public Object[] getStrokesData() {
    return tiPaintView.getStrokesData();
  }

  public void loadStrokes(Object[] strokesData) {
    tiPaintView.loadStrokes(strokesData);
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

    // Playback state
    private ArrayList<PathPaint> playbackPaths = new ArrayList<PathPaint>();
    private Handler playbackHandler = new Handler(Looper.getMainLooper());
    private Runnable playbackRunnable;
    private int currentPlaybackIndex = 0;
    private boolean isPlayingBack = false;
    private boolean isPaused = false;
    private long playbackInterval = 100;

    public PaintView(Context c) {
      super(c);
      tiBitmapPaint = new Paint(Paint.DITHER_FLAG);
      mPath = new Path();

      pathPaint = new PathPaint();
      pathPaint.setPath(mPath);
      pathPaint.setPaint(tiPaint);
      pathPaint.setEarase(eraseState);
    }

    void initializeView() {
      setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    @Override
    protected void onSizeChanged(int _width, int _height, int _oldWidth, int _oldHeight) {
      super.onSizeChanged(_width, _height, _oldWidth, _oldHeight);

      if (tiBitmap == null) {
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

      // Choose which paths to draw based on playback state
      ArrayList<PathPaint> pathsToDraw = isPlayingBack ? playbackPaths : tiPaths;

      for (PathPaint p : pathsToDraw) {
        canvas.drawPath(p.getPath(), p.getPaint());
      }

      // Draw current active path only if not in playback mode
      if (!isPlayingBack) {
        canvas.drawPath(mPath, tiPaint);
      }
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

    public void newPath() {
      mPath = new Path();
      pathPaint = new PathPaint();
      pathPaint.setPath(mPath);
      pathPaint.setPaint(tiPaint);
      pathPaint.setEarase(eraseState);
    }

    public void undo() {
      if (tiPaths.size() > 0) {
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

    // Playback methods
    public void playbackDrawing(float durationSeconds) {
      if (tiPaths.size() == 0)
        return;

      stopPlayback(); // Stop any existing playback

      isPlayingBack = true;
      isPaused = false;
      currentPlaybackIndex = 0;
      playbackPaths.clear();

      playbackInterval = (long) ((durationSeconds * 1000) / tiPaths.size());
      // Ensure reasonable maximum interval (no more than 1 second between strokes)
      if (playbackInterval > 1000) {
        playbackInterval = 1000;
      }

      playbackRunnable = new Runnable() {
        @Override
        public void run() {
          if (currentPlaybackIndex < tiPaths.size() && isPlayingBack && !isPaused) {
            playbackPaths.add(tiPaths.get(currentPlaybackIndex));
            currentPlaybackIndex++;
            invalidate();
            playbackHandler.postDelayed(this, playbackInterval);
          } else if (currentPlaybackIndex >= tiPaths.size()) {
            stopPlayback();
          }
        }
      };

      playbackHandler.postDelayed(playbackRunnable, playbackInterval);
    }

    public void pausePlayback() {
      isPaused = true;
    }

    public void resumePlayback() {
      isPaused = false;
      if (playbackRunnable != null && isPlayingBack) {
        playbackHandler.postDelayed(playbackRunnable, playbackInterval);
      }
    }

    public void stopPlayback() {
      if (playbackHandler != null && playbackRunnable != null) {
        playbackHandler.removeCallbacks(playbackRunnable);
      }
      isPlayingBack = false;
      isPaused = false;
      currentPlaybackIndex = 0;
      playbackPaths.clear();
      invalidate();
    }

    public void setPlaybackSpeed(float speed) {
      if (isPlayingBack && speed > 0) {
        playbackInterval = (long) (playbackInterval / speed);
        if (playbackRunnable != null && !isPaused) {
          playbackHandler.removeCallbacks(playbackRunnable);
          playbackHandler.postDelayed(playbackRunnable, playbackInterval);
        }
      }
    }

    public float getPlaybackProgress() {
      if (tiPaths.size() == 0)
        return 0.0f;
      return (float) currentPlaybackIndex / (float) tiPaths.size();
    }

    public Object[] getStrokesData() {
      ArrayList<Map<String, Object>> strokesList = new ArrayList<Map<String, Object>>();

      for (PathPaint pathPaint : tiPaths) {
        Map<String, Object> strokeData = new HashMap<String, Object>();

        // Get paint properties
        Paint paint = pathPaint.getPaint();

        // Convert color to hex string (use Titanium API naming)
        int color = paint.getColor();
        String hexColor = String.format("#%06x", color & 0xFFFFFF);
        strokeData.put("strokeColor", hexColor);

        strokeData.put("strokeWidth", (double) paint.getStrokeWidth());
        strokeData.put("strokeAlpha", paint.getAlpha());
        strokeData.put("eraseMode", pathPaint.getEarase());

        // Extract real points using PathMeasure and convert to proper array
        ArrayList<Map<String, Object>> pointsList = createSimplePointsFromBounds(pathPaint.getPath());
        Object[] pointsArray = pointsList.toArray();
        strokeData.put("points", pointsArray);
        
        // Keep pathData for debugging
        strokeData.put("pathData", "points:" + pointsList.size());

        strokesList.add(strokeData);
      }

      return strokesList.toArray();
    }

    public void loadStrokes(Object[] strokesData) {
      try {
        // Clear current strokes
        tiPaths.clear();
        undoPaths.clear();

        // Load each stroke
        for (Object strokeObj : strokesData) {
          // Handle both Map and Object[] cases (JavaScript serialization differences)
          Map<String, Object> strokeData = null;
          if (strokeObj instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> tempMap = (Map<String, Object>) strokeObj;
            strokeData = tempMap;
          } else if (strokeObj instanceof Object[]) {
            // The strokeObj is actually an array of individual stroke HashMaps
            Object[] strokeArray = (Object[]) strokeObj;
            
            // Process each element in the array as a separate stroke
            for (Object individualStroke : strokeArray) {
              if (individualStroke instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> individualStrokeData = (Map<String, Object>) individualStroke;
                processStroke(individualStrokeData);
              }
            }
            continue; // Continue to next strokeObj
          } else {
            continue; // Skip unknown types
          }
          
          if (strokeData != null) {
            processStroke(strokeData);
          }
        }

        // Refresh the view
        invalidate();
        
      } catch (Exception e) {
        Log.e("UIPaintView", "ERROR in loadStrokes: " + e.getMessage(), e);
      }
    }

    private int parseHexColor(String hexColor) {
      if (hexColor == null || !hexColor.startsWith("#")) {
        return android.graphics.Color.BLACK;
      }
      try {
        return android.graphics.Color.parseColor(hexColor);
      } catch (IllegalArgumentException e) {
        return android.graphics.Color.BLACK;
      }
    }

    private Path parsePathFromString(String pathString) {
      if (pathString == null || pathString.isEmpty()) {
        return new Path();
      }

      try {
        // Simple approach: try to parse basic path commands
        // Path.toString() format varies, so we'll create a simple parser
        return parseSimplePathString(pathString);
      } catch (Exception e) {
        // If parsing fails, return empty path
        return new Path();
      }
    }

    private Path parseSimplePathString(String pathString) {
      Path path = new Path();
      
      try {
        // Debug log to see what we're trying to parse
        Log.d("UIPaintView", "Parsing path: " + pathString);
        
        // More robust parsing using regex to extract coordinates
        java.util.regex.Pattern coordPattern = java.util.regex.Pattern.compile("([+-]?\\d*\\.?\\d+)");
        java.util.regex.Matcher matcher = coordPattern.matcher(pathString);
        
        java.util.List<Float> coords = new java.util.ArrayList<Float>();
        while (matcher.find()) {
          try {
            coords.add(Float.parseFloat(matcher.group(1)));
          } catch (NumberFormatException ignored) {
            // Skip invalid numbers
          }
        }
        
        // If we have at least 2 coordinates, create a simple path
        if (coords.size() >= 2) {
          path.moveTo(coords.get(0), coords.get(1));
          
          // Add remaining coordinates as line segments
          for (int i = 2; i < coords.size() - 1; i += 2) {
            if (i + 1 < coords.size()) {
              path.lineTo(coords.get(i), coords.get(i + 1));
            }
          }
        }
        
        Log.d("UIPaintView", "Parsed " + coords.size() + " coordinates");
        
      } catch (Exception e) {
        Log.e("UIPaintView", "Path parsing failed: " + e.getMessage());
        // Return empty path on any error
      }

      return path;
    }

    private ArrayList<Map<String, Object>> createSimplePointsFromBounds(Path path) {
      ArrayList<Map<String, Object>> pointsArray = new ArrayList<Map<String, Object>>();
      
      try {
        // Use PathMeasure to extract actual points from the path!
        PathMeasure pathMeasure = new PathMeasure(path, false);
        float pathLength = pathMeasure.getLength();
        
        if (pathLength > 0) {
          // Extract points along the path at regular intervals
          int numPoints = Math.min(20, Math.max(5, (int)(pathLength / 10))); // 5-20 points based on length
          float[] coords = new float[2];
          
          for (int i = 0; i < numPoints; i++) {
            float distance = (pathLength * i) / (numPoints - 1);
            if (pathMeasure.getPosTan(distance, coords, null)) {
              Map<String, Object> point = new HashMap<String, Object>();
              point.put("x", (double)coords[0]);
              point.put("y", (double)coords[1]);
              pointsArray.add(point);
            }
          }
          
          }
      } catch (Exception e) {
        Log.e("UIPaintView", "Error extracting points from path: " + e.getMessage());
      }
      
      return pointsArray;
    }
    
    private Path reconstructPathFromPoints(ArrayList<Map<String, Object>> pointsArray) {
      Path path = new Path();

      if (pointsArray.size() > 0) {
        // Start path with first point
        Map<String, Object> firstPoint = pointsArray.get(0);
        float startX = getFloatValue(firstPoint.get("x"));
        float startY = getFloatValue(firstPoint.get("y"));
        path.moveTo(startX, startY);

        // Add remaining points using quadTo for smooth curves
        float prevX = startX, prevY = startY;
        for (int i = 1; i < pointsArray.size(); i++) {
          Map<String, Object> point = pointsArray.get(i);
          float x = getFloatValue(point.get("x"));
          float y = getFloatValue(point.get("y"));

          // Use quadTo for smooth curves (matching touch_move logic)
          path.quadTo(prevX, prevY, (x + prevX) / 2, (y + prevY) / 2);
          prevX = x;
          prevY = y;
        }
        // Final lineTo (matching touch_up logic)
        path.lineTo(prevX, prevY);
      }

      return path;
    }
    
    private void processStroke(Map<String, Object> strokeData) {
      // Create new PathPaint
      PathPaint pathPaint = new PathPaint();

      // Set paint properties (use Titanium API naming)
      Paint paint = pathPaint.getPaint();
      if (strokeData.containsKey("strokeColor")) {
        String hexColor = (String) strokeData.get("strokeColor");
        int color = parseHexColor(hexColor);
        paint.setColor(color);
      }
      if (strokeData.containsKey("strokeWidth")) {
        Object widthObj = strokeData.get("strokeWidth");
        float width;
        if (widthObj instanceof Integer) {
          width = ((Integer) widthObj).floatValue();
        } else if (widthObj instanceof Double) {
          width = ((Double) widthObj).floatValue();
        } else {
          width = 10.0f; // default
        }
        paint.setStrokeWidth(width);
      }
      if (strokeData.containsKey("strokeAlpha")) {
        Object alphaObj = strokeData.get("strokeAlpha");
        int alpha;
        if (alphaObj instanceof Integer) {
          alpha = (Integer) alphaObj;
        } else if (alphaObj instanceof Double) {
          alpha = ((Double) alphaObj).intValue();
        } else {
          alpha = 255; // default
        }
        paint.setAlpha(alpha);
      }
      if (strokeData.containsKey("eraseMode")) {
        pathPaint.setEarase((Boolean) strokeData.get("eraseMode"));
      }

      // Reconstruct path from points data (both Android and iOS compatible)
      Path path = new Path();
      
      if (strokeData.containsKey("points")) {
        Object pointsObj = strokeData.get("points");
        ArrayList<Map<String, Object>> pointsArray = new ArrayList<Map<String, Object>>();
        
        // Handle both Object[] (from Android) and ArrayList (from iOS)
        if (pointsObj instanceof Object[]) {
          Object[] pointsObjArray = (Object[]) pointsObj;
          
          for (Object pointObj : pointsObjArray) {
            if (pointObj instanceof Map) {
              @SuppressWarnings("unchecked")
              Map<String, Object> pointMap = (Map<String, Object>) pointObj;
              pointsArray.add(pointMap);
            }
          }
        } else if (pointsObj instanceof ArrayList) {
          @SuppressWarnings("unchecked")
          ArrayList<Map<String, Object>> tempArray = (ArrayList<Map<String, Object>>) pointsObj;
          pointsArray = tempArray;
        }
        
        path = reconstructPathFromPoints(pointsArray);
      }

      pathPaint.setPath(path);
      tiPaths.add(pathPaint);
    }
    
    private float getFloatValue(Object value) {
      if (value instanceof Integer) {
        return ((Integer) value).floatValue();
      } else if (value instanceof Double) {
        return ((Double) value).floatValue();
      } else if (value instanceof Float) {
        return (Float) value;
      } else {
        return 0.0f; // default
      }
    }
  }

}
