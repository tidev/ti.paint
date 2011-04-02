/**
 * Titanium Paint Module
 *
 * Appcelerator Titanium is Copyright (c) 2009-2010 by Appcelerator, Inc.
 * and licensed under the Apache Public License (version 2)
 */
#import "TiPaintView.h"
#import "TiUtils.h"

@implementation TiPaintView

- (id)init
{
	if (self = [super init])
	{
		strokeWidth = 5;
		strokeColor = CGColorRetain([[TiUtils colorValue:@"#009"] _color].CGColor);
	}
	return self;
}

- (void)dealloc
{
	RELEASE_TO_NIL(drawImage);
	CGColorRelease(strokeColor);
	[super dealloc];
}

- (void)frameSizeChanged:(CGRect)frame bounds:(CGRect)bounds
{
	[super frameSizeChanged:frame bounds:bounds];
	if (drawImage!=nil)
	{
		[drawImage setFrame:bounds];
	}
}

- (UIImageView*)imageView
{
	if (drawImage==nil)
	{
		drawImage = [[UIImageView alloc] initWithImage:nil];
		drawImage.frame = [self bounds];
		[self addSubview:drawImage];
	}
	return drawImage;
}

- (void)touchesBegan:(NSSet *)touches withEvent:(UIEvent *)event 
{
	[super touchesBegan:touches withEvent:event];
	
	UITouch *touch = [touches anyObject];
	lastPoint = [touch locationInView:[self imageView]];
	lastPoint.y -= 20;
}

- (void)drawAt:(CGPoint)currentPoint
{
	UIView *view = [self imageView];
	UIGraphicsBeginImageContext(view.frame.size);
	[drawImage.image drawInRect:CGRectMake(0, 0, view.frame.size.width, view.frame.size.height)];
	CGContextSetLineCap(UIGraphicsGetCurrentContext(), kCGLineCapRound);
	CGContextSetLineWidth(UIGraphicsGetCurrentContext(), strokeWidth);
	CGContextSetStrokeColorWithColor(UIGraphicsGetCurrentContext(), strokeColor);
	CGContextBeginPath(UIGraphicsGetCurrentContext());
	CGContextMoveToPoint(UIGraphicsGetCurrentContext(), lastPoint.x, lastPoint.y);
	CGContextAddLineToPoint(UIGraphicsGetCurrentContext(), currentPoint.x, currentPoint.y);
	CGContextStrokePath(UIGraphicsGetCurrentContext());
	drawImage.image = UIGraphicsGetImageFromCurrentImageContext();
	UIGraphicsEndImageContext();
	lastPoint = currentPoint;
}

- (void)touchesMoved:(NSSet *)touches withEvent:(UIEvent *)event 
{
	[super touchesMoved:touches withEvent:event];
	
	UITouch *touch = [touches anyObject];	
	CGPoint currentPoint = [touch locationInView:[self imageView]];
	currentPoint.y -= 20;
	[self drawAt:currentPoint];
}

- (void)touchesEnded:(NSSet *)touches withEvent:(UIEvent *)event 
{
	[super touchesEnded:touches withEvent:event];
	[self drawAt:lastPoint];
}

#pragma mark Public APIs

- (void)setStrokeWidth_:(id)width
{
	strokeWidth = [TiUtils floatValue:width];
}

- (void)setStrokeColor_:(id)value
{
	CGColorRelease(strokeColor);
	TiColor *color = [TiUtils colorValue:value];
	strokeColor = [color _color].CGColor;
	CGColorRetain(strokeColor);
}

- (void)clear:(id)args
{
	if (drawImage!=nil)
	{
		drawImage.image = nil;
	}
}

@end
