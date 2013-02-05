/**
 * Ti.Paint Module
 * Copyright (c) 2010-2013 by Appcelerator, Inc. All Rights Reserved.
 * Please see the LICENSE included with this distribution for details.
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
