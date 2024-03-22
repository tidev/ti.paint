var Paint = require('ti.paint');

var win = Ti.UI.createWindow({
	backgroundColor: '#fff'
});
var paintView = Paint.createPaintView({
	top: 0,
	right: 0,
	bottom: 80,
	left: 0,
	// strokeWidth (float), strokeColor (string), strokeAlpha (int, 0-255)
	strokeColor: '#0f0',
	strokeAlpha: 255,
	strokeWidth: 10,
	eraseMode: false
});
win.add(paintView);

var buttonStrokeWidth = Ti.UI.createButton({
	height: 40,
	left: 10,
	bottom: 10,
	right: 10,
	title: 'Decrease Stroke Width'
});
buttonStrokeWidth.addEventListener('click', function(e) {
	paintView.strokeWidth = (paintView.strokeWidth === 10) ? 5 : 10;
	e.source.title = (paintView.strokeWidth === 10) ? 'Decrease Stroke Width' : 'Increase Stroke Width';
});
win.add(buttonStrokeWidth);

var buttonStrokeColorRed = Ti.UI.createButton({
	height: 40,
	bottom: 100,
	left: 10,
	title: 'Red'
});
buttonStrokeColorRed.addEventListener('click', function() {
	paintView.strokeColor = 'red';
});
var buttonStrokeColorGreen = Ti.UI.createButton({
	height: 40,
	bottom: 70,
	left: 10,
	title: 'Green'
});
buttonStrokeColorGreen.addEventListener('click', function() {
	paintView.strokeColor = '#0f0';
});
var buttonStrokeColorBlue = Ti.UI.createButton({
	height: 40,
	bottom: 40,
	left: 10,
	title: 'Blue'
});
buttonStrokeColorBlue.addEventListener('click', function() {
	paintView.strokeColor = '#0000ff';
});
win.add(buttonStrokeColorRed);
win.add(buttonStrokeColorGreen);
win.add(buttonStrokeColorBlue);

var clear = Ti.UI.createButton({
	height: 40,
	bottom: 40,
	left: 100,
	title: 'Clear'
});
clear.addEventListener('click', function() {
	paintView.clear();
});
win.add(clear);

if (OS_ANDROID) {
	var undo = Ti.UI.createButton({
		height: 40,
		bottom: 70,
		left: 100,
		title: 'undo'
	});
	undo.addEventListener('click', function() {
		paintView.undo();
	});
	win.add(undo);
	var redo = Ti.UI.createButton({
		height: 40,
		bottom: 100,
		left: 100,
		title: 'redo'
	});
	var fill = Ti.UI.createButton({
		height: 40,
		bottom: 130,
		left: 10,
		title: 'fill'
	});
	fill.addEventListener('click', function() {
		paintView.fill("#ff0000");
	});
	win.add(fill);
}

var isSaved = false;
var buttonLoadSave = Ti.UI.createButton({
	height: 40,
	bottom: 100,
	right: 10,
	title: 'Save/Load'
});
buttonLoadSave.addEventListener('click', function(e) {
	var file = Ti.Filesystem.getFile(Ti.Filesystem.applicationDataDirectory, "tmp.jpg");
	if (!isSaved) {
		paintView.toImage(function(blob) {
			file.write(blob);
			alert("done");
		})
	} else {
		paintView.image = file.nativePath
	}
	isSaved = !isSaved;
});
var buttonStrokeAlpha = Ti.UI.createButton({
	height: 40,
	bottom: 70,
	right: 10,
	title: 'Alpha : 100%'
});
buttonStrokeAlpha.addEventListener('click', function(e) {
	paintView.strokeAlpha = (paintView.strokeAlpha === 255) ? 127 : 255;
	e.source.title = (paintView.strokeAlpha === 255) ? 'Alpha : 100%' : 'Alpha : 50%';
});
win.add(buttonStrokeAlpha);

var buttonStrokeColorEraser = Ti.UI.createButton({
	height: 40,
	bottom: 40,
	right: 10,
	title: 'Erase : Off'
});
buttonStrokeColorEraser.addEventListener('click', function(e) {
	paintView.eraseMode = (paintView.eraseMode) ? false : true;
	e.source.title = (paintView.eraseMode) ? 'Erase : On' : 'Erase : Off';
});
win.add(buttonStrokeColorEraser);
win.add(buttonLoadSave);

paintView.addEventListener('touchcancel', function(e) {});
paintView.addEventListener('touchend', function(e) {});
paintView.addEventListener('touchmove', function(e) {});
paintView.addEventListener('touchstart', function(e) {});

win.open();
