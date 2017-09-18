# Titanium Windows Native Module - ti.paint
#
# Copyright (c) 2017 Axway All Rights Reserved.
# Licensed under the terms of the Apache Public License.
# Please see the LICENSE included with this distribution for details.

cmake_minimum_required(VERSION 3.0.0)

if(${CMAKE_SYSTEM_NAME} STREQUAL "Windows10")
  set(PLATFORM win10)
elseif(${CMAKE_SYSTEM_NAME} STREQUAL "WindowsPhone")
  set(PLATFORM phone)
  add_definitions("-DPHONE")
elseif(${CMAKE_SYSTEM_NAME} STREQUAL "WindowsStore")
  set(PLATFORM store)
else()
  message(FATAL_ERROR "This app supports Store / Phone only.")
endif()

project(TiPaint)

set(TiPaint_VERSION 0.1.0)

set(WINDOWS_SOURCE_DIR "C:/ProgramData/Titanium/mobilesdk/win32/6.2.0.GA/windows")

SET(CMAKE_FIND_LIBRARY_PREFIXES "")
SET(CMAKE_FIND_LIBRARY_SUFFIXES ".lib" ".dll")

set(CMAKE_INCLUDE_CURRENT_DIR ON)
set(CMAKE_INCLUDE_CURRENT_DIR_IN_INTERFACE ON)

option(TiPaint_DISABLE_TESTS "Disable compiling the tests" OFF)

set_property(GLOBAL PROPERTY USE_FOLDERS ON)

get_filename_component(APPCELERATOR_CMAKE_MODULE_PATH ${PROJECT_SOURCE_DIR}/cmake ABSOLUTE)
list(INSERT CMAKE_MODULE_PATH 0 ${APPCELERATOR_CMAKE_MODULE_PATH})

find_package(HAL REQUIRED)
find_package(TitaniumKit REQUIRED)
find_package(LayoutEngine REQUIRED)
find_package(TitaniumWindows_UI REQUIRED)

enable_testing()

set(SOURCE_TiPaint
  include/TiPaint.hpp
  src/TiPaint.cpp
  include/TiPaintView.hpp
  src/TiPaintView.cpp
  )

source_group(TiPaint FILES ${SOURCE_TiPaint})

set(CMAKE_CXX_VISIBILITY_PRESET hidden)
set(CMAKE_VISIBILITY_INLINES_HIDDEN 1)

add_library(TiPaint SHARED
  ${SOURCE_TiPaint}
  )

include(GenerateExportHeader)
generate_export_header(TiPaint)
target_compile_definitions(TiPaint PRIVATE TiPaint_EXPORTS)

target_include_directories(TiPaint PUBLIC
  ${PROJECT_SOURCE_DIR}/include
  $<TARGET_PROPERTY:HAL,INTERFACE_INCLUDE_DIRECTORIES>
  $<TARGET_PROPERTY:TitaniumKit,INTERFACE_INCLUDE_DIRECTORIES>
  $<TARGET_PROPERTY:LayoutEngine,INTERFACE_INCLUDE_DIRECTORIES>
  $<TARGET_PROPERTY:TitaniumWindows_UI,INTERFACE_INCLUDE_DIRECTORIES>
  )

target_link_libraries(TiPaint
  HAL
  TitaniumKit
  LayoutEngine
  TitaniumWindows_UI
  )

set_target_properties(TiPaint PROPERTIES VS_WINRT_COMPONENT TRUE)

set_property(TARGET TiPaint APPEND_STRING PROPERTY LINK_FLAGS_DEBUG "/OPT:NOREF /OPT:NOICF")

if (NOT TiPaint_DISABLE_TESTS)
  add_subdirectory(test)
endif()

set_property(TARGET TiPaint PROPERTY VERSION ${TiPaint_VERSION})
set_property(TARGET TiPaint PROPERTY SOVERSION 0)
set_property(TARGET TiPaint PROPERTY INTERFACE_TiPaint_MAJOR_VERSION 0)
set_property(TARGET TiPaint APPEND PROPERTY
  COMPATIBLE_INTERFACE_STRING TiPaint_MAJOR_VERSION
  )

install(TARGETS TiPaint EXPORT TiPaint_Targets
  LIBRARY DESTINATION lib
  ARCHIVE DESTINATION lib
  RUNTIME DESTINATION bin
  INCLUDES DESTINATION include
  )

export(EXPORT TiPaint_Targets
  FILE "${CMAKE_BINARY_DIR}/TiPaint_Targets.cmake"
)

configure_file(cmake/TiPaint_Config.cmake
  "${CMAKE_BINARY_DIR}/TiPaint_Config.cmake"
  COPYONLY
  )

include(CMakePackageConfigHelpers)
write_basic_package_version_file(
  "${CMAKE_BINARY_DIR}/TiPaint_ConfigVersion.cmake"
  VERSION ${TiPaint_VERSION}
  COMPATIBILITY AnyNewerVersion
  )

export(PACKAGE TiPaint)
