ti.paint
=======

This is the Paint Module for Titanium.

## Usage
```javascript
var Paint = require('ti.paint');
var paintView = Paint.createPaintView({});
```

### Functions

* clear()
Clears the paint view.

* moveTo(x,y) [Android only]
Move to position x/y

* lineTo(x,y) [Android only]
Draw line to position x/y

* enable(true/false) [Android only]
Disable drawing

* undo()/redo() [Android only]
Undo or redo last action

* fill(color) [Android only]
Fills the whole paint view with a solid color


### Properties

* strokeWidth[double]
Controls the width of the strokes.

* strokeColor[string]
Controls the color of the strokes.

* strokeAlpha[int]
Controls the opacity of the strokes.

* eraseMode[boolean]
Controls if the strokes are in "erase mode" -- that is, any existing paint will be erased.

* image[string]
Loads an image (by its URL) directly in to the paint view so that it can be drawn on and erased.

### Events

* touchcancel
Fired when a touch event is interrupted by the device.

* touchend
Fired when a touch event is completed.

* touchmove
Fired as soon as the device detects movement of a touch.

* touchstart
Fired as soon as the device detects a touch gesture.

## Contributors

* Please see https://github.com/tidev/ti.paint/graphs/contributors
* Interested in contributing? Read the [contributors/committer's](https://github.com/tidev/organization-docs/blob/main/BECOMING_A_COMMITTER.md) guide.

## Legal

This module is Copyright (c) 2010-present by Tidev, Inc. All Rights Reserved. Usage of this module is subject to
the Terms of Service agreement with Tidev, Inc.  
nc.
