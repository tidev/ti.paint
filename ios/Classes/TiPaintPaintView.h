/**
 * Titanium Paint Module
 *
 * Appcelerator Titanium is Copyright (c) 2009-2010 by Appcelerator, Inc.
 * and licensed under the Apache Public License (version 2)
 */
#import "TiUIView.h"
#import "WetPaintView.h"

@interface TiPaintPaintView : TiUIView <WetPaintViewDelegate> {
@private
	UIImageView *drawImage;
	WetPaintView *wetPaintView;
	CGRect drawBox;
}

@end
