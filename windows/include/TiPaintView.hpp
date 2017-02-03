/**
 * Titanium Windows - ti.paint
 *
 * Copyright (c) 2017 by Axway All Rights Reserved.
 * Licensed under the terms of the Apache Public License.
 * Please see the LICENSE included with this distribution for details.
 */
#ifndef _TIPAINTVIEW_HPP_
#define _TIPAINTVIEW_HPP_

#include "TiPaint_EXPORT.h"
#include "Titanium/UI/View.hpp"
#include "Titanium/detail/TiBase.hpp"

namespace Ti
{
	using namespace HAL;

	using namespace Windows::Foundation;
	using namespace Windows::UI::Xaml::Controls;
	using namespace Windows::UI::Xaml::Media;

	class TIPAINT_EXPORT PaintView : public Titanium::UI::View, public JSExport<PaintView>
	{
	public:
		PaintView(const JSContext&) TITANIUM_NOEXCEPT;

		virtual void postCallAsConstructor(const JSContext& js_context, const std::vector<JSValue>& arguments) override;

		virtual ~PaintView() = default;
		PaintView(const PaintView&) = default;
		PaintView& operator=(const PaintView&) = default;
#ifdef TITANIUM_MOVE_CTOR_AND_ASSIGN_DEFAULT_ENABLE
		PaintView(PaintView&&) = default;
		PaintView& operator=(PaintView&&) = default;
#endif

		static void JSExportInitialize();

		TITANIUM_PROPERTY_DEF(strokeWidth);
		TITANIUM_PROPERTY_IMPL_DEF(double, strokeWidth);

		TITANIUM_PROPERTY_DEF(strokeColor);
		TITANIUM_PROPERTY_IMPL_READONLY_DEF(std::string, strokeColor);
		virtual void set_strokeColor(const std::string& color) TITANIUM_NOEXCEPT;

		TITANIUM_PROPERTY_DEF(strokeAlpha);
		TITANIUM_PROPERTY_IMPL_READONLY_DEF(int, strokeAlpha);
		virtual void set_strokeAlpha(const int& color) TITANIUM_NOEXCEPT;

		TITANIUM_PROPERTY_DEF(eraseMode);
		TITANIUM_PROPERTY_IMPL_DEF(bool, eraseMode);

		TITANIUM_PROPERTY_DEF(image);
		TITANIUM_PROPERTY_IMPL_DEF(std::string, image);

		TITANIUM_FUNCTION_DEF(clear);

	private:
#pragma warning(push)
#pragma warning(disable : 4251)
		double strokeWidth__;
		std::string strokeColor__;
		int strokeAlpha__;
		bool eraseMode__;
		std::string image__;

		std::uint32_t inputId__;
		Point inputStart__;
		Point inputEnd__;
		Brush^ strokeBrush__{ nullptr };
		Canvas^ canvas__{ nullptr };
#pragma warning(pop)
	};
}
#endif // _TIPAINTVIEW_HPP_
