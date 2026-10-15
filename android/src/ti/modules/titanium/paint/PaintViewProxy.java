/**
 * Ti.Paint Module
 * Copyright (c) 2010-present by TiDev, Inc. All Rights Reserved.
 * Please see the LICENSE included with this distribution for details.
 */

package ti.modules.titanium.paint;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;

import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.kroll.common.AsyncResult;
import org.appcelerator.kroll.common.TiMessenger;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.util.TiConvert;
import org.appcelerator.titanium.view.TiUIView;
import org.jetbrains.annotations.NotNull;

@Kroll.proxy(creatableInModule = PaintModule.class)
public class PaintViewProxy extends TiViewProxy {
    private static final int MSG_CLEAR = 60000;
    private static final int MSG_LOAD = 60001;
    private UIPaintView paintView;
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
            }
            return false;
        }
    });

    public PaintViewProxy() {
        super();
    }

    @Override
    public TiUIView createView(Activity activity) {
        paintView = new UIPaintView(this);
        return paintView;
    }

    @Override
    public void releaseViews() {
        super.releaseViews();
        // Drop our own reference as well. The proxy can outlive the window (JS still holds it),
        // and the view keeps the full-size drawing bitmap and the destroyed activity alive.
        paintView = null;
    }

    @Kroll.setProperty
    public void setStrokeWidth(Object width) {
        if (paintView == null) {
            return;
        }
        paintView.setStrokeWidth(TiConvert.toFloat(width));
    }

    @Kroll.setProperty
    public void setStrokeColor(String color) {
        if (paintView == null) {
            return;
        }
        paintView.setStrokeColor(color);
    }

    @Kroll.setProperty
    public void setEraseMode(Boolean toggle) {
        if (paintView == null) {
            return;
        }
        paintView.setEraseMode(toggle);
    }

    @Kroll.setProperty
    public void setStrokeAlpha(int alpha) {
        if (paintView == null) {
            return;
        }
        paintView.setStrokeAlpha(alpha);
    }

    @Kroll.setProperty
    public void setImage(String imagePath) {
        if (paintView == null) {
            return;
        }
        if (!TiApplication.isUIThread()) {
            TiMessenger.sendBlockingMainMessage(handler.obtainMessage(MSG_LOAD));
        } else {
            paintView.setImage(imagePath);
        }
    }

    @Kroll.method
    public void fill(String color) {
        if (paintView == null) {
            return;
        }
        paintView.fill(TiConvert.toColor(color, TiApplication.getAppCurrentActivity()));
    }

    @Kroll.method
    public void lineTo(int x, int y) {
        if (paintView == null) {
            return;
        }
        paintView.lineTo(x, y);
    }

    @Kroll.method
    public void moveTo(int x, int y) {
        if (paintView == null) {
            return;
        }
        paintView.moveTo(x, y);
    }

    @Kroll.method
    public void undo() {
        if (paintView == null) {
            return;
        }
        paintView.undo();
    }

    @Kroll.method
    public void redo() {
        if (paintView == null) {
            return;
        }
        paintView.redo();
    }

    @Kroll.method
    public void enable(boolean enable) {
        if (paintView == null) {
            return;
        }
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
}
