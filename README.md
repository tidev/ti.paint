ti.paint
=======

This is the Paint Module for Titanium.

## Usage
```javascript
var Paint = require('ti.paint');
var paintView = Paint.createPaintView({
    strokeWidth: 5,
    strokeColor: 'blue',
    strokeAlpha: 255
});

// Basic functionality
paintView.clear();
paintView.undo();
paintView.redo();

// New playback functionality
paintView.playbackDrawing(3.0); // Play back all strokes over 3 seconds
paintView.pausePlayback();
paintView.resumePlayback();
paintView.setPlaybackSpeed(0.5); // Half speed
var progress = paintView.getPlaybackProgress(); // Get current progress

// Save and load stroke data
var strokesData = paintView.getStrokesData(); // Save current strokes
var file = Ti.Filesystem.getFile(Ti.Filesystem.applicationDataDirectory, 'drawing.json');
file.write(JSON.stringify(strokesData));

// Later: Load saved strokes
var savedData = JSON.parse(file.read().text);
paintView.loadStrokes(savedData); // Restore drawing
paintView.playbackDrawing(5.0); // Play back loaded drawing
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

* undo()/redo() [Cross-platform]
Undo or redo last action. Now available on both iOS and Android.

* playbackDrawing(duration) [Cross-platform]
Plays back all strokes chronologically over the specified duration (in seconds). Creates a "movie mode" effect.

* pausePlayback() [Cross-platform]
Pauses the current playback animation.

* resumePlayback() [Cross-platform]
Resumes a paused playback animation.

* stopPlayback() [Cross-platform]
Stops the current playback and returns to normal drawing mode.

* setPlaybackSpeed(speed) [Cross-platform]
Sets the playback speed multiplier (e.g., 0.5 for half speed, 2.0 for double speed).

* getPlaybackProgress() [Cross-platform]
Returns the current playback progress as a number between 0.0 and 1.0.

* getStrokesData() [Cross-platform]
Returns an array containing all stroke data for saving/loading sessions.

* loadStrokes(strokesData) [Cross-platform]
Loads previously saved stroke data to recreate a drawing session.


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
