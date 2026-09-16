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

* undo()/redo()
Undo or redo last action

* playbackDrawing(duration)
Replays the strokes one by one over `duration` seconds, waiting at most one second between strokes.

* pausePlayback()/resumePlayback()
Pauses or resumes the playback.

* stopPlayback()
Stops the playback and shows all the strokes again.

* setPlaybackSpeed(speed)
Changes the speed of a running playback. `2` plays twice as fast, `0.5` half as fast.

* getPlaybackProgress()
Returns the playback progress, from 0 to 1.

* getStrokesData()
Returns the strokes as an array you can store with `JSON.stringify()`.

* loadStrokes(strokes)
Replaces the current strokes with an array returned by `getStrokesData()`.

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

### Stroke data

```javascript
[{
	strokeColor: '#ff0000',
	strokeWidth: 10,
	strokeAlpha: 255,
	eraseMode: false,
	points: [{ x: 100.5, y: 200.3 }, { x: 101.2, y: 201.8 }]
}]
```

On Android each stroke is saved with 5 to 20 points, so long strokes lose detail after `loadStrokes()`.

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
