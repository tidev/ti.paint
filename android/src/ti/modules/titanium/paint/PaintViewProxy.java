/**
 * Ti.Paint Module
 * Copyright (c) 2010-present by TiDev, Inc. All Rights Reserved.
 * Please see the LICENSE included with this distribution for details.
 */

package ti.modules.titanium.paint;

import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.kroll.common.AsyncResult;
import org.appcelerator.kroll.common.TiMessenger;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.util.TiConvert;
import org.appcelerator.titanium.view.TiUIView;
import org.jetbrains.annotations.NotNull;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;

@Kroll.proxy(creatableInModule = PaintModule.class)
public class PaintViewProxy extends TiViewProxy {
  private UIPaintView paintView;

  public PaintViewProxy() {
    super();
  }

  @Override
  public TiUIView createView(Activity activity) {
    paintView = new UIPaintView(this);
    return paintView;
  }

  @Kroll.setProperty
  public void setStrokeWidth(Object width) {
    paintView.setStrokeWidth(TiConvert.toFloat(width));
  }

  @Kroll.setProperty
  public void setStrokeColor(String color) {
    paintView.setStrokeColor(color);
  }

  @Kroll.setProperty
  public void setEraseMode(Boolean toggle) {
    paintView.setEraseMode(toggle);
  }

  @Kroll.setProperty
  public void setStrokeAlpha(int alpha) {
    paintView.setStrokeAlpha(alpha);
  }

  @Kroll.setProperty
  public void setImage(String imagePath) {
    if (!TiApplication.isUIThread()) {
      TiMessenger.sendBlockingMainMessage(handler.obtainMessage(MSG_LOAD));
    } else {
      paintView.setImage(imagePath);
    }
  }

  @Kroll.method
  public void lineTo(int x, int y) {
    paintView.lineTo(x, y);
  }

  @Kroll.method
  public void moveTo(int x, int y) {
    paintView.moveTo(x, y);
  }

  @Kroll.method
  public void undo() {
    paintView.undo();
  }

  @Kroll.method
  public void redo() {
    paintView.redo();
  }

  @Kroll.method
  public void enable(boolean enable) {
    paintView.enable(enable);
  }

  @Kroll.method
  public void clear() {
    if (paintView != null) {
      if (!TiApplication.isUIThread()) {
        TiMessenger.sendBlockingMainMessage(handler.obtainMessage(MSG_CLEAR));
      } else {
        paintView.clear();
      }
    }
  }

  // Playback methods
  @Kroll.method
  public void playbackDrawing(float durationSeconds) {
    if (paintView != null) {
      paintView.playbackDrawing(durationSeconds);
    }
  }

  @Kroll.method
  public void pausePlayback() {
    if (paintView != null) {
      paintView.pausePlayback();
    }
  }

  @Kroll.method
  public void resumePlayback() {
    if (paintView != null) {
      paintView.resumePlayback();
    }
  }

  @Kroll.method
  public void stopPlayback() {
    if (paintView != null) {
      paintView.stopPlayback();
    }
  }

  @Kroll.method
  public void setPlaybackSpeed(float speed) {
    if (paintView != null) {
      paintView.setPlaybackSpeed(speed);
    }
  }

  @Kroll.method
  public float getPlaybackProgress() {
    if (paintView != null) {
      return paintView.getPlaybackProgress();
    }
    return 0.0f;
  }

  @Kroll.method
  public Object[] getStrokesData() {
    if (paintView != null) {
      return paintView.getStrokesData();
    }
    return new Object[0];
  }

  @Kroll.method
  public void loadStrokes(Object[] strokesData) {
    if (paintView != null) {
      if (!TiApplication.isUIThread()) {
        TiMessenger.sendBlockingMainMessage(handler.obtainMessage(MSG_LOAD_STROKES, strokesData));
      } else {
        paintView.loadStrokes(strokesData);
      }
    }
  }

  private static final int MSG_LOAD = 60001;
  private static final int MSG_CLEAR = 60000;
  private static final int MSG_LOAD_STROKES = 60002;

  private final Handler handler = new Handler(TiMessenger.getMainMessenger().getLooper(), new Handler.Callback() {
    public boolean handleMessage(@NotNull Message msg) {
      switch (msg.what) {
        case MSG_CLEAR: {
          AsyncResult result = (AsyncResult) msg.obj;
          paintView.clear();
          result.setResult(null);
          return true;
        }
        case MSG_LOAD: {
          AsyncResult result = (AsyncResult) msg.obj;
          paintView.setImage(result.getResult().toString());
          result.setResult(null);
          return true;
        }
        case MSG_LOAD_STROKES: {
          AsyncResult result = (AsyncResult) msg.obj;
          Object[] strokesData = (Object[]) result.getResult();
          paintView.loadStrokes(strokesData);
          result.setResult(null);
          return true;
        }
      }
      return false;
    }
  });
}
