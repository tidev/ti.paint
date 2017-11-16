# FindJavaScriptCore
# Author: Chris Williams
#
# Copyright (c) 2017 by Axway All Rights Reserved.
# Licensed under the terms of the Apache Public License.
# Please see the LICENSE included with this distribution for details.

# Author: Chris Williams
# Created: 2014.12.02

if (${CMAKE_SYSTEM_VERSION} MATCHES "^10.0")
  set(PLATFORM win10)
elseif(${CMAKE_SYSTEM_NAME} STREQUAL "WindowsPhone")
  set(PLATFORM phone)
elseif(${CMAKE_SYSTEM_NAME} STREQUAL "WindowsStore")
  set(PLATFORM store)
else()
  message(FATAL_ERROR "This app supports Store / Phone only.")
endif()

set(JavaScriptCore_ARCH "x86")
if(CMAKE_GENERATOR MATCHES "^Visual Studio .+ ARM$")
  set(JavaScriptCore_ARCH "arm")
endif()

# Taken and slightly modified from build's JavaScriptCore_Targets.cmake file
# INTERFACE_INCLUDE_DIRECTORIES is modified to point to our pre-packaged include dir for module

# Create imported target JavaScriptCore
add_library(JavaScriptCore SHARED IMPORTED)

set_target_properties(JavaScriptCore PROPERTIES
  COMPATIBLE_INTERFACE_STRING "JavaScriptCore_MAJOR_VERSION"
  INTERFACE_JavaScriptCore_MAJOR_VERSION "0"
)

set_target_properties(JavaScriptCore PROPERTIES
  IMPORTED_IMPLIB "${WINDOWS_SOURCE_DIR}/lib/JavaScriptCore/${PLATFORM}/${JavaScriptCore_ARCH}/JavaScriptCore.lib"
  IMPORTED_LOCATION "${WINDOWS_SOURCE_DIR}/lib/JavaScriptCore/${PLATFORM}/${JavaScriptCore_ARCH}/JavaScriptCore.dll"
  )
