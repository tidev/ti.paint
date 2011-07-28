/**
 * Titanium Paint Module
 *
 * Appcelerator Titanium is Copyright (c) 2009-2010 by Appcelerator, Inc.
 * and licensed under the Apache Public License (version 2)
 */
#import "TiPaintPaintView.h"
#import "TiUtils.h"

@implementation TiPaintPaintView

- (id)init
{
	if ((self = [super init]))
	{
		strokeWidth = 5;
        strokeAlpha = 1;
		strokeColor = CGColorRetain([[TiUtils colorValue:@"#000"] _color].CGColor);
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

- (void)drawSolidLine:(CGPoint)currentPoint
{
    CGContextSetLineCap(UIGraphicsGetCurrentContext(), kCGLineCapRound);
    CGContextSetLineWidth(UIGraphicsGetCurrentContext(), strokeWidth);
    CGContextSetAlpha(UIGraphicsGetCurrentContext(), strokeAlpha);
    CGContextSetStrokeColorWithColor(UIGraphicsGetCurrentContext(), strokeColor);
    CGContextBeginPath(UIGraphicsGetCurrentContext());
    CGContextMoveToPoint(UIGraphicsGetCurrentContext(), lastPoint.x, lastPoint.y);
    CGContextAddLineToPoint(UIGraphicsGetCurrentContext(), currentPoint.x, currentPoint.y);
}

- (void)drawEraserLine:(CGPoint)currentPoint
{
    // This is an implementation of Bresenham's line algorithm
    int x0 = currentPoint.x, y0 = currentPoint.y;
    int x1 = lastPoint.x, y1 = lastPoint.y;
    int dx = abs(x0-x1), dy = abs(y0-y1);
    int sx = x0 < x1 ? 1 : -1, sy = y0 < y1 ? 1 : -1;
    int err = dx - dy, e2;
    
    while(true)
    {
        CGContextClearRect(UIGraphicsGetCurrentContext(), CGRectMake(x0, y0, strokeWidth, strokeWidth));
        if (x0 == x1 && y0 == y1)
        {
            break;
        }
        e2 = 2 * err;
        if (e2 > -dy)
        {
            err -= dy;
            x0 += sx;
        }
        if (e2 < dx)
        {
            err += dx;
            y0 += sy;
        }
    }
}

- (void)drawAt:(CGPoint)currentPoint
{
	UIView *view = [self imageView];
	UIGraphicsBeginImageContext(view.frame.size);
	[drawImage.image drawInRect:CGRectMake(0, 0, view.frame.size.width, view.frame.size.height)];
    if (erase) {
        [self drawEraserLine:currentPoint];
    }
    else {
        [self drawSolidLine:currentPoint];
    }
    CGContextStrokePath(UIGraphicsGetCurrentContext());
	drawImage.image = UIGraphicsGetImageFromCurrentImageContext();
	UIGraphicsEndImageContext();
	lastPoint = currentPoint;
}

- (void)touchesBegan:(NSSet *)touches withEvent:(UIEvent *)event 
{
	[super touchesBegan:touches withEvent:event];
	
	UITouch *touch = [touches anyObject];
	lastPoint = [touch locationInView:[self imageView]];
	lastPoint.y -= 20;
	[self drawAt:lastPoint];
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

- (void)setEraseMode_:(id)value
{
	erase = [TiUtils boolValue:value];
}

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

- (void)setStrokeAlpha_:(id)alpha
{
    strokeAlpha = [TiUtils floatValue:alpha] / 255.0;
}

- (void)clear:(id)args
{
	if (drawImage!=nil)
	{
		drawImage.image = nil;
	}
}

@end
