/**
* Titanium Windows - ti.paint
*
* Copyright (c) 2017 by Axway All Rights Reserved.
* Licensed under the terms of the Apache Public License.
* Please see the LICENSE included with this distribution for details.
*/
#include "TiPaint.hpp"
#include "TiPaintView.hpp"

namespace Ti
{
	Paint::Paint(const JSContext& js_context) TITANIUM_NOEXCEPT
		: Titanium::Module(js_context, "ti.paint"),
		paintView__(JSExport<Ti::PaintView>::Class())
	{
		TITANIUM_LOG_DEBUG("Ti::Paint::ctor Initialize");
	}

	void Paint::JSExportInitialize()
	{
		JSExport<Paint>::SetClassVersion(1);
		JSExport<Paint>::SetParent(JSExport<Titanium::Module>::Class());

		TITANIUM_ADD_FUNCTION(Paint, createPaintView);
	}

	TITANIUM_FUNCTION(Paint, createPaintView)
	{
		ENSURE_OPTIONAL_OBJECT_AT_INDEX(parameters, 0);
		auto paintView_obj = get_context().CreateObject(paintView__).CallAsConstructor(parameters);
		Titanium::Module::applyProperties(parameters, paintView_obj);
		return paintView_obj;
	}

}
