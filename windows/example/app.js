var win = Ti.UI.createWindow({
		backgroundColor: 'red',
		layout: 'vertical'
	}),
	control = Ti.UI.createView({
		backgroundColor: 'gray',
		layout: 'horizontal',
		height: Ti.UI.SIZE
	}),
	btn_red = Ti.UI.createButton({title: 'RED', backgroundColor: 'red'}),
	btn_erase = Ti.UI.createButton({title: 'ERASE'}),
	btn_clear = Ti.UI.createButton({title: 'CLEAR'}),
	btn_save = Ti.UI.createButton({title: 'SAVE'}),
	
	paintView = require('ti.paint').createPaintView({
		top: 10, bottom: 10,
		left: 10, right: 10,
		backgroundColor: 'white',
		strokeColor: 'black', strokeWidth: 6,
		width: Ti.UI.FILL, height: '30%'
	});
	imageView = Ti.UI.createImageView({
		top: 10, bottom: 10,
		left: 10, right: 10,
		width: Ti.UI.FILL, height: '30%'
	});
	
btn_red.addEventListener('click', function() {
	paintView.strokeColor = 'red';
});

btn_erase.addEventListener('click', function() {
	paintView.eraseMode = !paintView.eraseMode;
});

btn_clear.addEventListener('click', function() {
	paintView.clear();
});

btn_save.addEventListener('click', function() {
	paintView.toImage(function(blob) {
		imageView.image = blob;
	});
});
	
control.add(btn_red);
control.add(btn_erase);
control.add(btn_clear);
control.add(btn_save);

win.add(control);
win.add(paintView);
win.add(imageView);

win.open();