ti.paint
=======

A **cross-platform Titanium SDK module** providing a paint surface user interface for creating drawing and painting applications.

## Platform Support

- **iOS**: v3.0.0 (iOS 12.0+)
- **Android**: v6.0.0 (API 21+, Android 5.0+)
- **Minimum Titanium SDK**: 12.0.0

## Installation

```bash
# Install for iOS
ti module install ti.paint-iphone-3.0.0.zip

# Install for Android  
ti module install ti.paint-android-6.0.0.zip
```

## Quick Start
```javascript
var Paint = require('ti.paint');
var paintView = Paint.createPaintView({
    strokeWidth: 10,
    strokeColor: '#ff0000',
    strokeAlpha: 255,
    eraseMode: false
});

// Basic drawing operations
paintView.clear();

// Universal Undo/Redo (now works on both iOS and Android)
paintView.undo();
paintView.redo();

// Movie Mode: Playback functionality
paintView.playbackDrawing(5.0); // Replay all strokes over 5 seconds
paintView.pausePlayback();
paintView.resumePlayback();
paintView.stopPlayback();
paintView.setPlaybackSpeed(2.0); // Double speed
var progress = paintView.getPlaybackProgress(); // Returns 0.0-1.0

// Save and Load: Cross-platform stroke persistence
var strokesData = paintView.getStrokesData();
var file = Ti.Filesystem.getFile(Ti.Filesystem.applicationDataDirectory, 'drawing.json');
file.write(JSON.stringify(strokesData));

// Later: Load and replay saved drawing
var savedData = JSON.parse(file.read().text);
paintView.loadStrokes(savedData);
paintView.playbackDrawing(8.0); // Replay loaded session
```

## API Reference

### Core Methods

**`clear()`**  
Clears the entire paint view and removes all strokes.

**`undo()` [Cross-platform]**  
Undoes the last drawing action. Uses efficient stroke-based system (no memory-intensive snapshots).

**`redo()` [Cross-platform]**  
Redoes the last undone action.

### Playback System ("Movie Mode")

**`playbackDrawing(duration)` [Cross-platform]**  
Replays all strokes chronologically over the specified duration in seconds. Maximum interval between strokes is 1 second for smooth playback.

**`pausePlayback()` [Cross-platform]**  
Pauses the current playback animation.

**`resumePlayback()` [Cross-platform]**  
Resumes a paused playback animation.

**`stopPlayback()` [Cross-platform]**  
Stops playback and returns to normal drawing mode.

**`setPlaybackSpeed(speed)` [Cross-platform]**  
Sets playback speed multiplier (0.1x to 10x+). Example: `0.5` for half speed, `2.0` for double speed.

**`getPlaybackProgress()` [Cross-platform]**  
Returns current playback progress as a float between 0.0 and 1.0.

### Save/Load System

**`getStrokesData()` [Cross-platform]**  
Returns stroke data array for persistence. Data format is consistent between iOS and Android.

**`loadStrokes(strokesData)` [Cross-platform]**  
Loads previously saved stroke data to recreate a drawing session. Clears current strokes first.

### Android-Only Methods

**`moveTo(x, y)`**  
Move drawing cursor to position x/y.

**`lineTo(x, y)`**  
Draw line to position x/y.

**`enable(boolean)`**  
Enable/disable drawing interaction.


### Properties

**`strokeWidth` (Number)**  
Controls the width of strokes in pixels. Default: varies by platform.

**`strokeColor` (String)**  
Controls stroke color. Accepts hex (`#ff0000`), named colors (`red`), or RGB values.

**`strokeAlpha` (Number)**  
Controls stroke opacity. Range: 0-255 (0 = transparent, 255 = opaque).

**`eraseMode` (Boolean)**  
Enable/disable erase mode. When `true`, strokes clear existing pixels using blend mode (real pixel erasure, not white drawing).

**`image` (String)**  
Loads a background image by URL/path for drawing over.

### Touch Events

**`touchstart`**  
Fired when a touch gesture begins.

**`touchmove`**  
Fired during touch movement/dragging.

**`touchend`**  
Fired when a touch gesture completes.

**`touchcancel`**  
Fired when a touch is interrupted by the system.

## Data Format

The stroke data returned by `getStrokesData()` uses this consistent format across platforms:

```javascript
[
  {
    "strokeColor": "#ff0000",    // Hex color string
    "strokeWidth": 10.0,         // Stroke width in pixels
    "strokeAlpha": 255,          // Alpha value (0-255)
    "eraseMode": false,          // Boolean erase mode flag
    "points": [                  // Array of path coordinates
      {"x": 100.5, "y": 200.3},
      {"x": 101.2, "y": 201.8}
    ]
  }
]
```

## Key Features

- ✅ **Cross-platform compatibility** - Identical API on iOS and Android
- ✅ **Efficient undo/redo** - Stroke-based system (95% less memory than snapshots)
- ✅ **Movie Mode playback** - Replay drawings as animations with speed control
- ✅ **Persistent sessions** - Save/load drawings with full stroke data
- ✅ **Real pixel erasing** - True erasure using blend modes, not white drawing
- ✅ **Modern SDK support** - Requires Titanium SDK 12.0.0+

## Contributors

* Please see https://github.com/tidev/ti.paint/graphs/contributors
* Interested in contributing? Read the [contributors/committer's](https://github.com/tidev/organization-docs/blob/main/BECOMING_A_COMMITTER.md) guide.

## License

This module is Copyright (c) 2010-present by Tidev, Inc. All Rights Reserved. Usage of this module is subject to the Terms of Service agreement with Tidev, Inc.
