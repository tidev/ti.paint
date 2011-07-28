/**
 * Copyright (c) 2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Apache Public License
 * Please see the LICENSE included with this distribution for details.
 * Author: Fred Spencer (fspencer@appcelerator.com)
 */

package ti.modules.titanium.paint;

import org.appcelerator.kroll.annotations.Kroll;

import org.appcelerator.titanium.TiContext;
import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.view.TiUIView;

import android.app.Activity;

import ti.modules.titanium.paint.UIPaintView;

@Kroll.proxy(creatableInModule=PaintModule.class)
public class PaintViewProxy extends TiViewProxy {
	private UIPaintView paintView;

	public PaintViewProxy(TiContext tiContext) {
		super(tiContext);
	}

	@Override
	public TiUIView createView(Activity activity) {
		paintView = new UIPaintView(this);
		return paintView;
	}

	@Kroll.setProperty @Kroll.method
	public void setStrokeWidth(Float width) {		
		paintView.setStrokeWidth(width);
	}

	@Kroll.setProperty @Kroll.method
	public void setStrokeColor(String color) {		
		paintView.setStrokeColor(color);
	}

	@Kroll.setProperty @Kroll.method
	public void setEraseMode(Boolean toggle) {
		paintView.setEraseMode(toggle);
	}

	@Kroll.setProperty @Kroll.method
	public void setStrokeAlpha(int alpha) {
		paintView.setStrokeAlpha(alpha);
	}

	@Kroll.method(runOnUiThread=true)
	public void clear() {
	    paintView.clear();
	}
}