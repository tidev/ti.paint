# Titanium Windows Native Module - ti.paint
#
# Copyright (c) 2017 Axway All Rights Reserved.
# Licensed under the terms of the Apache Public License.
# Please see the LICENSE included with this distribution for details.

include(CMakeFindDependencyMacro)
find_dependency(HAL)
find_dependency(TitaniumKit)

include("${CMAKE_BINARY_DIR}/TitaniumWindows_TiPaint_Targets.cmake")