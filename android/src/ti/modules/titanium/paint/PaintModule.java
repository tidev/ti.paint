/**
 * Copyright (c) 2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Apache Public License
 * Please see the LICENSE included with this distribution for details.
 * Author: Fred Spencer (fspencer@appcelerator.com)
 */

package ti.modules.titanium.paint;

import org.appcelerator.kroll.KrollModule;
import org.appcelerator.kroll.annotations.Kroll;

import org.appcelerator.titanium.TiContext;

@Kroll.module(name="Paint", id="ti.paint")
public class PaintModule extends KrollModule {
	public PaintModule(TiContext tiContext) {
		super(tiContext);
	}
}
