let Paint = require('ti.paint');

let win = Ti.UI.createWindow({
  backgroundColor: '#fff',
  title: 'Ti.Paint Example - All Features'
});

let paintView = Paint.createPaintView({
  top: 0,
  left: 0,
  right: 0,
  bottom: 120,
  strokeWidth: 10,
  eraseMode: false,
  strokeAlpha: 255,
  strokeColor: '#0f0'
});

win.add(paintView);

// Row 1: Basic Controls
let buttonStrokeWidth = Ti.UI.createButton({
  left: 5,
  width: 80,
  height: 35,
  bottom: 80,
  title: 'Width: 10'
});

buttonStrokeWidth.addEventListener('click', function (e) {
  paintView.strokeWidth = (paintView.strokeWidth === 50) ? 10 : 50;
  e.source.title = 'Width: ' + paintView.strokeWidth;
});

let buttonRed = Ti.UI.createButton({
  left: 90,
  width: 50,
  height: 35,
  bottom: 80,
  title: 'Red'
});

buttonRed.addEventListener('click', function () {
  paintView.strokeColor = 'red';
});

let buttonGreen = Ti.UI.createButton({
  left: 145,
  width: 50,
  height: 35,
  bottom: 80,
  title: 'Green'
});

buttonGreen.addEventListener('click', function () {
  paintView.strokeColor = '#0f0';
});

let buttonBlue = Ti.UI.createButton({
  left: 200,
  width: 50,
  height: 35,
  bottom: 80,
  title: 'Blue'
});

buttonBlue.addEventListener('click', function () {
  paintView.strokeColor = '#0000ff';
});

let buttonClear = Ti.UI.createButton({
  left: 255,
  width: 50,
  height: 35,
  bottom: 80,
  title: 'Clear'
});

buttonClear.addEventListener('click', function () {
  paintView.clear();
});

// Row 2: Universal Undo/Redo (works on both iOS and Android now!)
let buttonUndo = Ti.UI.createButton({
  left: 5,
  width: 60,
  height: 35,
  bottom: 40,
  title: 'Undo'
});

buttonUndo.addEventListener('click', function () {
  paintView.undo();
});

let buttonRedo = Ti.UI.createButton({
  left: 70,
  width: 60,
  height: 35,
  bottom: 40,
  title: 'Redo'
});

buttonRedo.addEventListener('click', function () {
  paintView.redo();
});

// Alpha and Erase controls
let buttonAlpha = Ti.UI.createButton({
  left: 135,
  width: 80,
  height: 35,
  bottom: 40,
  title: 'Alpha: 100%'
});

buttonAlpha.addEventListener('click', function (e) {
  paintView.strokeAlpha = (paintView.strokeAlpha === 255) ? 127 : 255;
  e.source.title = (paintView.strokeAlpha === 255) ? 'Alpha: 100%' : 'Alpha: 50%';
});

let buttonErase = Ti.UI.createButton({
  left: 220,
  width: 80,
  height: 35,
  bottom: 40,
  title: 'Erase: Off'
});

buttonErase.addEventListener('click', function (e) {
  paintView.eraseMode = !paintView.eraseMode;
  e.source.title = paintView.eraseMode ? 'Erase: On' : 'Erase: Off';
});

// Row 3: NEW Playback Controls (Cross-Platform Movie Mode!)
let buttonPlayback = Ti.UI.createButton({
  left: 5,
  bottom: 5,
  width: 80,
  height: 35,
  title: 'Play 5s'
});

let isPlaybackRunning = false;

buttonPlayback.addEventListener('click', function (e) {
  if (!isPlaybackRunning) {
    paintView.playbackDrawing(5.0); // 5 second playback
    isPlaybackRunning = true;
    e.source.title = 'Playing...';

    // Reset button after 5 seconds
    setTimeout(function () {
      isPlaybackRunning = false;
      e.source.title = 'Play 5s';
    }, 5000);
  }
});

let buttonPause = Ti.UI.createButton({
  left: 90,
  bottom: 5,
  width: 50,
  height: 35,
  title: 'Pause'
});

buttonPause.addEventListener('click', function () {
  paintView.pausePlayback();
});

let buttonResume = Ti.UI.createButton({
  left: 145,
  bottom: 5,
  width: 55,
  height: 35,
  title: 'Resume'
});

buttonResume.addEventListener('click', function () {
  paintView.resumePlayback();
});

let buttonStop = Ti.UI.createButton({
  left: 205,
  bottom: 5,
  width: 50,
  height: 35,
  title: 'Stop'
});

buttonStop.addEventListener('click', function () {
  paintView.stopPlayback();
  isPlaybackRunning = false;
  buttonPlayback.title = 'Play 5s';
});

// NEW: Save/Load Strokes (Cross-Platform Persistence!)
let strokesFile = Ti.Filesystem.getFile(Ti.Filesystem.applicationDataDirectory, 'strokes.json');
let hasSavedStrokes = strokesFile.exists();

let buttonSaveLoad = Ti.UI.createButton({
  right: 5,
  bottom: 5,
  width: 65,
  height: 35,
  title: hasSavedStrokes ? 'Load' : 'Save'
});

buttonSaveLoad.addEventListener('click', function (e) {
  if (!hasSavedStrokes) {
    // Save current strokes
    let strokesData = paintView.getStrokesData();
    strokesFile.write(JSON.stringify(strokesData));
    hasSavedStrokes = true;
    e.source.title = 'Load';
    Ti.API.info('Strokes saved! Draw something new, then press Load to restore.');
  } else {
    // Load saved strokes
    if (strokesFile.exists()) {
      let savedData = JSON.parse(strokesFile.read().text);
      paintView.loadStrokes(savedData);
      hasSavedStrokes = false;
      e.source.title = 'Save';
      Ti.API.info('Strokes loaded! Press Play to see the drawing replay.');
    }
  }
});

// Add all buttons to window
win.add(buttonStrokeWidth);
win.add(buttonRed);
win.add(buttonGreen);
win.add(buttonBlue);
win.add(buttonClear);
win.add(buttonUndo);
win.add(buttonRedo);
win.add(buttonAlpha);
win.add(buttonErase);
win.add(buttonPlayback);
win.add(buttonPause);
win.add(buttonResume);
win.add(buttonStop);
win.add(buttonSaveLoad);

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
Ti.API.info('4. Press "Play 5s" to replay your drawing as a movie!');
Ti.API.info('5. Use Pause/Resume/Stop to control playback');

win.open();
