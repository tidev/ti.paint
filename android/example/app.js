Titanium.Paint = require('ti.paint');
Ti.Paint = Titanium.Paint;

// strokeWidth (float), strokeColor (string), strokeAlpha (int, 0-255)

var win = Ti.UI.createWindow();
var paintView = Ti.Paint.createPaintView({ top:0, right:0, bottom:80, left:0, strokeColor:'#0f0', strokeAlpha:255, strokeWidth:10, eraseMode:false });
var buttonStrokeWidth = Ti.UI.createButton({ left:10, bottom:10, right:10, height:30, title:'Decrease Stroke Width' });

var buttonStrokeColorRed = Ti.UI.createButton({ bottom:100, left:10, width:75, height:30, title:'Red' });
var buttonStrokeColorGreen = Ti.UI.createButton({ bottom:70, left:10, width:75, height:30, title:'Green' });
var buttonStrokeColorBlue = Ti.UI.createButton({ bottom:40, left:10, width:75, height:30, title:'Blue' });
var buttonStrokeAlpha = Ti.UI.createButton({ bottom:70, right:10, width:100, height:30, title:'Alpha : 100' });
var buttonStrokeColorEraser = Ti.UI.createButton({ bottom:40, right:10, width:100, height:30, title:'Erase : Off' });

Ti.API.info(paintView.strokeWidth);

buttonStrokeWidth.addEventListener('click', function(e) {
	paintView.strokeWidth = (paintView.strokeWidth === 10) ? 5 : 10;
	e.source.title = (paintView.strokeWidth === 10) ? 'Decrease Stroke Width' : 'Increase Stroke Width';
});

buttonStrokeColorRed.addEventListener('click', function(e) {
	paintView.strokeColor = 'red';
});

buttonStrokeColorGreen.addEventListener('click', function(e) {
	paintView.strokeColor = '#0f0';
});

buttonStrokeColorBlue.addEventListener('click', function(e) {
	paintView.strokeColor = '#0000ff';
});

buttonStrokeAlpha.addEventListener('click', function(e) {
	paintView.strokeAlpha = (paintView.strokeAlpha === 255) ? 100 : 255;
	e.source.title = (paintView.strokeAlpha === 255) ? 'Alpha : 100' : 'Alpha : 255';
});

buttonStrokeColorEraser.addEventListener('click', function(e) {
	paintView.eraseMode = (paintView.eraseMode) ? false : true;
	e.source.title = (paintView.eraseMode) ? 'Erase : On' : 'Erase : Off';
});

Ti.API.info(paintView.strokeAlpha);

win.add(paintView);

win.add(buttonStrokeWidth);
win.add(buttonStrokeColorRed);
win.add(buttonStrokeColorGreen);
win.add(buttonStrokeColorBlue);
win.add(buttonStrokeAlpha);
win.add(buttonStrokeColorEraser);

win.open();

