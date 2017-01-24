/**
* Titanium Windows - ti.paint
*
* Copyright (c) 2017 by Axway All Rights Reserved.
* Licensed under the terms of the Apache Public License.
* Please see the LICENSE included with this distribution for details.
*/
#include "TiPaintView.hpp"
#include "TitaniumWindows/UI/WindowsViewLayoutDelegate.hpp"

namespace Ti
{
		using namespace Windows::Devices::Input;
		using namespace Windows::UI::Core;

		PaintView::PaintView(const JSContext& js_context) TITANIUM_NOEXCEPT
			: Titanium::UI::View(js_context),
			strokeWidth__(6),
			strokeColor__("black"),
			strokeAlpha__(255),
			eraseMode__(false),
			image__("")
		{
			TITANIUM_LOG_DEBUG("Ti::Paint::View::ctor Initialize");
		}

		void PaintView::postCallAsConstructor(const JSContext& js_context, const std::vector<JSValue>& arguments)
		{
			Titanium::UI::View::postCallAsConstructor(js_context, arguments);

			canvas__ = ref new Windows::UI::Xaml::Controls::Canvas();
			canvas__->Background = ref new Windows::UI::Xaml::Media::SolidColorBrush(Windows::UI::Colors::Transparent);

			inputId__ = 0;
			strokeBrush__ = ref new Windows::UI::Xaml::Media::SolidColorBrush(TitaniumWindows::UI::WindowsViewLayoutDelegate::ColorForName(strokeColor__));

			canvas__->PointerPressed += ref new Windows::UI::Xaml::Input::PointerEventHandler(
				[=](Platform::Object ^sender, Windows::UI::Xaml::Input::PointerRoutedEventArgs^ e) {
				const auto p = e->GetCurrentPoint(canvas__);
				inputStart__ = p->Position;

				const auto type = e->Pointer->PointerDeviceType;
				if (type == PointerDeviceType::Touch || type == PointerDeviceType::Pen || type == PointerDeviceType::Mouse && p->Properties->IsLeftButtonPressed) {
					inputId__ = p->PointerId;
					e->Handled = true;
				}
			}
			);
			canvas__->PointerMoved += ref new Windows::UI::Xaml::Input::PointerEventHandler(
				[=](Platform::Object ^sender, Windows::UI::Xaml::Input::PointerRoutedEventArgs^ e) {

				if (e->Pointer->PointerId == inputId__) {
					const auto p = e->GetCurrentPoint(canvas__);

					inputEnd__ = p->Position;

					if (std::sqrt(std::pow((inputEnd__.X - inputStart__.X), 2) + std::pow((inputEnd__.Y - inputStart__.Y), 2)) > 1.0) {
						auto line = ref new Windows::UI::Xaml::Shapes::Line();
						line->X1 = inputEnd__.X;
						line->Y1 = inputEnd__.Y;
						line->X2 = inputStart__.X;
						line->Y2 = inputStart__.Y;
						line->StrokeThickness = strokeWidth__;
						line->StrokeStartLineCap = PenLineCap::Round;
						line->StrokeEndLineCap = PenLineCap::Round;
						if (get_eraseMode()) {
							line->Stroke = canvas__->Background;
						} else {
							line->Stroke = strokeBrush__;
						}
						canvas__->Children->Append(line);

						inputStart__ = inputEnd__;
					}
				}
			}
			);
			canvas__->PointerReleased += ref new Windows::UI::Xaml::Input::PointerEventHandler(
				[=](Platform::Object ^sender, Windows::UI::Xaml::Input::PointerRoutedEventArgs^ e) {
				inputId__ = 0;
				e->Handled = true;
			}
			);

			Titanium::UI::View::setLayoutDelegate<TitaniumWindows::UI::WindowsViewLayoutDelegate>();

			layoutDelegate__->set_defaultHeight(Titanium::UI::LAYOUT::FILL);
			layoutDelegate__->set_defaultWidth(Titanium::UI::LAYOUT::FILL);
			layoutDelegate__->set_autoLayoutForHeight(Titanium::UI::LAYOUT::FILL);
			layoutDelegate__->set_autoLayoutForWidth(Titanium::UI::LAYOUT::FILL);

			getViewLayoutDelegate<TitaniumWindows::UI::WindowsViewLayoutDelegate>()->setComponent(canvas__, nullptr, false);
		}

		void PaintView::JSExportInitialize()
		{
			JSExport<PaintView>::SetClassVersion(1);
			JSExport<PaintView>::SetParent(JSExport<Titanium::UI::View>::Class());

			TITANIUM_ADD_PROPERTY(PaintView, strokeWidth);
			TITANIUM_ADD_PROPERTY(PaintView, strokeColor);
			TITANIUM_ADD_PROPERTY(PaintView, strokeAlpha);
			TITANIUM_ADD_PROPERTY(PaintView, eraseMode);
			TITANIUM_ADD_PROPERTY(PaintView, image);

			TITANIUM_ADD_FUNCTION(PaintView, clear);
		}

		TITANIUM_PROPERTY_READWRITE(PaintView, double, strokeWidth);

		TITANIUM_PROPERTY_GETTER(PaintView, strokeWidth)
		{
			return get_context().CreateNumber(get_strokeWidth());
		}

		TITANIUM_PROPERTY_SETTER(PaintView, strokeWidth)
		{
			TITANIUM_ASSERT(argument.IsNumber());
			set_strokeWidth(static_cast<double>(argument));
			return true;
		}

		TITANIUM_PROPERTY_READ(PaintView, std::string, strokeColor);

		TITANIUM_PROPERTY_GETTER(PaintView, strokeColor)
		{
			return get_context().CreateString(get_strokeColor());
		}

		TITANIUM_PROPERTY_SETTER(PaintView, strokeColor)
		{
			TITANIUM_ASSERT(argument.IsString());
			set_strokeColor(static_cast<std::string>(argument));
			return true;
		}

		void PaintView::set_strokeColor(const std::string& color) TITANIUM_NOEXCEPT
		{
			strokeColor__ = color;
			auto brushColor = TitaniumWindows::UI::WindowsViewLayoutDelegate::ColorForName(strokeColor__);
			brushColor.A = static_cast<char>(strokeAlpha__);
			strokeBrush__ = ref new Windows::UI::Xaml::Media::SolidColorBrush(brushColor);
		}

		TITANIUM_PROPERTY_READ(PaintView, int, strokeAlpha);

		TITANIUM_PROPERTY_GETTER(PaintView, strokeAlpha)
		{
			return get_context().CreateNumber(get_strokeAlpha());
		}

		TITANIUM_PROPERTY_SETTER(PaintView, strokeAlpha)
		{
			TITANIUM_ASSERT(argument.IsNumber());
			set_strokeAlpha(static_cast<int>(argument));
			return true;
		}

		void PaintView::set_strokeAlpha(const int& alpha) TITANIUM_NOEXCEPT
		{
			strokeAlpha__ = alpha;
			set_strokeColor(strokeColor__);
		}

		TITANIUM_PROPERTY_READWRITE(PaintView, bool, eraseMode);

		TITANIUM_PROPERTY_GETTER(PaintView, eraseMode)
		{
			return get_context().CreateBoolean(get_eraseMode());
		}

		TITANIUM_PROPERTY_SETTER(PaintView, eraseMode)
		{
			TITANIUM_ASSERT(argument.IsBoolean());
			set_eraseMode(static_cast<bool>(argument));
			return true;
		}

		TITANIUM_PROPERTY_READWRITE(PaintView, std::string, image);

		TITANIUM_PROPERTY_GETTER(PaintView, image)
		{
			return get_context().CreateString(get_image());
		}

		TITANIUM_PROPERTY_SETTER(PaintView, image)
		{
			TITANIUM_ASSERT(argument.IsString());
			set_image(static_cast<std::string>(argument));
			layoutDelegate__->set_backgroundImage(get_image());
			return true;
		}

		TITANIUM_FUNCTION(PaintView, clear)
		{
			canvas__->Children->Clear();
			return get_context().CreateUndefined();
		}
}
