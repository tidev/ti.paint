const Paint = require('ti.paint');

let win = Ti.UI.createWindow({
  backgroundColor: '#ffffff',
  title: 'Ti.Paint Example - All Features'
});

// Utility functions
function createRowView() {
  return Ti.UI.createView({
    top: 5,
    bottom: 5,
    width: Ti.UI.SIZE,
    height: Ti.UI.SIZE,
    layout: 'horizontal'
  });
}

function createStyledButton(title, backgroundColor = "#f8fafc") {
  return Ti.UI.createButton({
    left: 3,
    right: 3,
    height: 40,
    borderRadius: 6,
    backgroundColor,
    width: Ti.UI.SIZE,
    color: '#333333',
    title: `   ${title}   `,
    font: { fontSize: 12, fontWeight: 'bold' }
  });
}


// Main canvas view - will automatically occupy all the space
let paintView = Paint.createPaintView({
  strokeWidth: 10,
  strokeAlpha: 255,
  eraseMode: false,
  width: Ti.UI.FILL,
  height: Ti.UI.FILL,
  strokeColor: '#00ff00',
  backgroundColor: '#ffffff',
});

// Contenedor principal de todos los controles
let controlsContainer = Ti.UI.createView({
  bottom: 0,
  width: Ti.UI.FILL,
  height: Ti.UI.SIZE,
  layout: 'vertical',
  backgroundColor: '#f1f5f9',
});

// Row 1: Controles básicos
let row1 = createRowView();

let buttonStrokeWidth = createStyledButton('Width: 10', '#74b9ff');
let buttonRed = createStyledButton('Red', '#fd79a8');
let buttonGreen = createStyledButton('Green', '#00b894');
let buttonBlue = createStyledButton('Blue', '#0984e3');
let buttonClear = createStyledButton('Clear', '#e17055');

// Row 2: Undo/Redo and Alpha/Erase controls
let row2 = createRowView();

let buttonUndo = createStyledButton('Undo', '#a29bfe');
let buttonRedo = createStyledButton('Redo', '#6c5ce7');
let buttonAlpha = createStyledButton('Alpha: 100%', '#fdcb6e');
let buttonErase = createStyledButton('Erase: Off', '#e84393');

// Row 3: Playback Controls
let row3 = createRowView();

let buttonPlayback = createStyledButton('Play Animation', '#00cec9');
let buttonPause = createStyledButton('Pause', '#fab1a0');
let buttonResume = createStyledButton('Resume', '#55a3ff');
let buttonStop = createStyledButton('Stop', '#ff7675');
let buttonSaveLoad = createStyledButton('Save', '#81ecec');

// Event listeners and state management
let isPlaybackRunning = false;
let strokesFile = Ti.Filesystem.getFile(Ti.Filesystem.applicationDataDirectory, 'strokes.json');
let hasSavedStrokes = strokesFile.exists();

// Update Save/Load button title based on state
buttonSaveLoad.applyProperties({ title: hasSavedStrokes ? '   Load   ' : '   Save   ' });

// Row 1 listeners
buttonStrokeWidth.addEventListener('click', function (e) {
  paintView.applyProperties({ strokeWidth: (paintView.strokeWidth === 50) ? 10 : 50 });
  e.source.applyProperties({ title: `Width: ${paintView.strokeWidth}` });
});

buttonRed.addEventListener('click', function () {
  paintView.applyProperties({ strokeColor: '#ff0000' });
});

buttonGreen.addEventListener('click', function () {
  paintView.applyProperties({ strokeColor: '#00ff00' });
});

buttonBlue.addEventListener('click', function () {
  paintView.applyProperties({ strokeColor: '#0000ff' });
});

buttonClear.addEventListener('click', function () {
  paintView.clear();
});

// Row 2 listeners
buttonUndo.addEventListener('click', function () {
  paintView.undo();
});

buttonRedo.addEventListener('click', function () {
  paintView.redo();
});

buttonAlpha.addEventListener('click', function (e) {
  paintView.applyProperties({ strokeAlpha: (paintView.strokeAlpha === 255) ? 127 : 255 });
  e.source.applyProperties({ title: (paintView.strokeAlpha === 255) ? 'Alpha: 100%' : 'Alpha: 50%' });
});

buttonErase.addEventListener('click', function (e) {
  paintView.applyProperties({ eraseMode: !paintView.eraseMode });
  e.source.applyProperties({
    title: paintView.eraseMode ? 'Erase: On' : 'Erase: Off',
    backgroundColor: paintView.eraseMode ? '#ff4757' : '#e84393'
  });
});

// Row 3 listeners
buttonPlayback.addEventListener('click', function (e) {
  if (!isPlaybackRunning) {
    isPlaybackRunning = true;
    paintView.playbackDrawing(paintView.getStrokesData().length / 10); // number of strokes divided by 10
    e.source.applyProperties({ title: 'Playing...' });

    // Reset button after 5 seconds
    setTimeout(function () {
      isPlaybackRunning = false;
      e.source.applyProperties({ title: 'Play Animation' });
    }, 5000);
  }
});

buttonPause.addEventListener('click', function () {
  paintView.pausePlayback();
});

buttonResume.addEventListener('click', function () {
  paintView.resumePlayback();
});

buttonStop.addEventListener('click', function () {
  isPlaybackRunning = false;
  paintView.stopPlayback();
  buttonPlayback.applyProperties({ title: 'Play Animation' });
});

buttonSaveLoad.addEventListener('click', function (e) {
  if (!hasSavedStrokes) {
    // Save current strokes
    let strokesData = paintView.getStrokesData();
    strokesFile.write(JSON.stringify(strokesData));
    hasSavedStrokes = true;
    e.source.applyProperties({ title: 'Load' });
    Ti.API.info('Strokes saved! Draw something new, then press Load to restore.');
  } else {
    // Load saved strokes
    if (strokesFile.exists()) {
      let savedData = JSON.parse(strokesFile.read().text);
      console.warn('Loaded strokes data:', savedData, savedData.length);
      paintView.loadStrokes(savedData);
      hasSavedStrokes = false;
      e.source.applyProperties({ title: 'Save' });
      Ti.API.info('Strokes loaded! Press Play to see the drawing replay.');
    }
  }
});

// Add buttons to their respective rows
row1.add(buttonStrokeWidth);
row1.add(buttonRed);
row1.add(buttonGreen);
row1.add(buttonBlue);
row1.add(buttonClear);

row2.add(buttonUndo);
row2.add(buttonRedo);
row2.add(buttonAlpha);
row2.add(buttonErase);

row3.add(buttonPlayback);
row3.add(buttonPause);
row3.add(buttonResume);
row3.add(buttonStop);
row3.add(buttonSaveLoad);

// Add rows to the controls container
controlsContainer.add(row1);
controlsContainer.add(row2);
controlsContainer.add(row3);

// Build the main interface responsively
win.add(paintView);
win.add(controlsContainer);

// Touch event listeners (for debugging/monitoring)
paintView.addEventListener('touchcancel', function (e) {
  Ti.API.info('Paint: touch cancel');
});
paintView.addEventListener('touchend', function (e) {
  Ti.API.info('Paint: touch end');
});
paintView.addEventListener('touchmove', function (e) {
  // Ti.API.info('Paint: touch move'); // Too verbose, uncomment if needed
});
paintView.addEventListener('touchstart', function (e) {
  Ti.API.info('Paint: touch start');
});

// Instructions
Ti.API.info('=== Ti.Paint v6.0.0/3.0.0 - All Features Demo ===');
Ti.API.info('1. Draw something with your finger');
Ti.API.info('2. Use Undo/Redo (now works on both iOS & Android!)');
Ti.API.info('3. Save your drawing, clear canvas, then Load to restore');
Ti.API.info('4. Press "Play Animation" to replay your drawing as a movie!');
Ti.API.info('5. Use Pause/Resume/Stop to control playback');

win.open();
