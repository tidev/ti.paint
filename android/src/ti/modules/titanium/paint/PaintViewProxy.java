/**
 * Ti.Paint Module
 * Copyright (c) 2010-2013 by Appcelerator, Inc. All Rights Reserved.
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
	@Kroll.method
	public void setStrokeWidth(Object width) {
		paintView.setStrokeWidth(TiConvert.toFloat(width));
	}

	@Kroll.setProperty
	@Kroll.method
	public void setStrokeColor(String color) {
		paintView.setStrokeColor(color);
	}

	@Kroll.setProperty
	@Kroll.method
	public void setEraseMode(Boolean toggle) {
		paintView.setEraseMode(toggle);
	}

	@Kroll.setProperty
	@Kroll.method
	public void setStrokeAlpha(int alpha) {
		paintView.setStrokeAlpha(alpha);
	}

	@Kroll.setProperty
	@Kroll.method
	public void setImage(String imagePath) {
		paintView.setImage(imagePath);
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

	private static final int MSG_CLEAR = 60000;
	private final Handler handler = new Handler(TiMessenger.getMainMessenger().getLooper(), new Handler.Callback() {
		public boolean handleMessage(@NotNull Message msg) {
			switch (msg.what) {
				case MSG_CLEAR: {
					AsyncResult result = (AsyncResult) msg.obj;
					paintView.clear();
					result.setResult(null);
					return true;
				}
			}
			return false;
		}
	});
}
