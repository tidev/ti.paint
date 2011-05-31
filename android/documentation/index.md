# paint Module

## Description

Provides a paint surface user interface view.

## Accessing the paint Module

To access this module from JavaScript, you would do the following (recommended):

	Titanium.Paint = require('ti.paint');
	Ti.Paint = Titanium.Paint;
	
The paint variable is a reference to the Module object.	

## Reference

## Functions

### Ti.Paint.createPaintView({...})

Returns a paint view:
 	
- strokeWidth*[float]*
- strokeColor*[string]*
- strokeAlpha*[int]*
- eraseMode*[boolean]*

## Module Installation and Use

- Put the module zip file into the root folder of your Titanium application.
- Set the `<module>` element in tiapp.xml, such as this:
    <modules>
	    <module version="1.0">ti.paint</module>
	</modules>
- See example

## Author

Fred Spencer <fspencer@appcelerator.com>, Appcelerator Inc.

## License

&copy; 2011 Appcelerator Inc.