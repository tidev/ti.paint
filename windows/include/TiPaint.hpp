/**
* Titanium Windows - ti.paint
*
* Copyright (c) 2017 by Axway All Rights Reserved.
* Licensed under the terms of the Apache Public License.
* Please see the LICENSE included with this distribution for details.
*/
#ifndef _TIPAINT_HPP_
#define _TIPAINT_HPP_

#include "TiPaint_EXPORT.h"
#include "Titanium/Module.hpp"
#include "Titanium/detail/TiBase.hpp"

namespace Ti
{
	using namespace HAL;

	class TIPAINT_EXPORT Paint : public Titanium::Module, public JSExport<Paint>
	{
		public:
			Paint(const JSContext&) TITANIUM_NOEXCEPT;

			virtual ~Paint()               = default;
			Paint(const Paint&)            = default;
			Paint& operator=(const Paint&) = default;
#ifdef TITANIUM_MOVE_CTOR_AND_ASSIGN_DEFAULT_ENABLE
			Paint(Paint&&)                 = default;
			Paint& operator=(Paint&&)      = default;
#endif

			static void JSExportInitialize();

			TITANIUM_FUNCTION_DEF(createPaintView);

		private:
			JSClass paintView__;
	};
}
#endif // _TIPAINT_HPP_
