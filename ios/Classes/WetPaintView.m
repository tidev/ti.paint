/**
 * WetPaintView.m
 * paint
 * 
 * Created by Dawson Toth on 4/20/12.
 * Copyright (c) 2012 Toth Solutions, LLC. All rights reserved.
 * 
 * Ti.Paint Module
 * Copyright (c) 2010-2013 by Appcelerator, Inc. All Rights Reserved.
 * Please see the LICENSE included with this distribution for details.
 */

#import "WetPaintView.h"

@implementation WetPaintView

#pragma mark Properties

@synthesize strokeAlpha;
@synthesize strokeWidth;
@synthesize strokeColor;
@synthesize erase;
@synthesize delegate;

#pragma mark Lifecycle

- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        self.multipleTouchEnabled = YES;
        strokeWidth = 5;
        strokeAlpha = 1;
		strokeColor = CGColorRetain([[TiUtils colorValue:@"#000"] _color].CGColor);
        _touchLines = CFDictionaryCreateMutable(NULL, 0, NULL, NULL);
        self.opaque = NO;
        self.backgroundColor = [UIColor colorWithWhite:0.0 alpha:0.0];
    }
    return self;
}

- (void)dealloc
{
    CFDictionaryRemoveAllValues(_touchLines);
    _touchLines = NULL;
	CGColorRelease(strokeColor);
	[super dealloc];
}

#pragma mark Public API

- (void)drawInContext:(CGContextRef)context andApplyErase:(bool)applyErase {
    CGContextSetLineCap(context, kCGLineCapRound);
    CGContextSetLineWidth(context, strokeWidth);
    CGContextSetStrokeColorWithColor(context, strokeColor);
    if (!erase) {
        CGContextSetAlpha(context, strokeAlpha);
        CGContextSetBlendMode(context, kCGBlendModeNormal);
        CFDictionaryApplyFunction(_touchLines, drawPoints, context);
    }
    else if (applyErase) {
        CGContextSetBlendMode(context, kCGBlendModeClear);
        CFDictionaryApplyFunction(_touchLines, drawPoints, context);
    }
    else {
        CGContextSetAlpha(context, 1); // Erasing does not respect alpha, unfortunately.
        CGContextSetStrokeColorWithColor(context, [[TiUtils colorValue:@"#000"] _color].CGColor);
        CFDictionaryApplyFunction(_touchLines, drawPoints, context);
    }
}

#pragma mark Drawing

- (void)drawRect:(CGRect)rect
{
    CGContextRef context = UIGraphicsGetCurrentContext();
    [self drawInContext:context andApplyErase:NO];
    UIGraphicsEndImageContext();
}

#pragma mark Drawing Utility

static void drawPoints(const void* key, const void* value, void* ctx)
{
    CGContextRef context = ctx;
    NSMutableArray* points = (NSMutableArray*)value;
    if (points == nil || [points count] == 0) {
        return;
    }
    CGPoint first = [[points objectAtIndex:0] CGPointValue];
    CGContextBeginPath(context);
    CGContextMoveToPoint(context, first.x, first.y);
    NSValue* lastValue = nil;
    for (NSValue* value in points) {
        CGPoint point = [value CGPointValue];
        if (lastValue != nil) {
            CGPoint lastPoint = [lastValue CGPointValue];
            CGContextAddQuadCurveToPoint(context, lastPoint.x, lastPoint.y, (point.x + lastPoint.x) / 2, (point.y + lastPoint.y) / 2);
        }
        lastValue = value;
    }
    CGContextStrokePath(context);
}

#pragma mark Touch Delegate

- (void)touchesBegan:(NSSet *)touches withEvent:(UIEvent *)event 
{
    for (UITouch* touch in [touches allObjects]) {
        CGPoint point = [touch locationInView:self];
        NSValue* value = [NSValue valueWithCGPoint:point];
        NSMutableArray* points = [[NSMutableArray arrayWithObject:value] retain];
        CFDictionarySetValue(_touchLines, touch, points);
    }
	[super touchesBegan:touches withEvent:event];
}

- (void)touchesMoved:(NSSet *)touches withEvent:(UIEvent *)event 
{
	for (UITouch* touch in [touches allObjects]) {
        NSMutableArray* points = (NSMutableArray*)CFDictionaryGetValue(_touchLines, touch);
        CGPoint point = [touch locationInView:self];
        NSValue* value = [NSValue valueWithCGPoint:point];
        [points addObject:value];
    }
    [self setNeedsDisplay];
	[super touchesMoved:touches withEvent:event];
}

- (void) touchesCancelled:(NSSet *)touches withEvent:(UIEvent *)event
{
    for (UITouch* touch in [touches allObjects]) {
        CFDictionaryRemoveValue(_touchLines, touch);
    }
    [self setNeedsDisplay];
    [super touchesCancelled:touches withEvent:event];
}

- (void)touchesEnded:(NSSet *)touches withEvent:(UIEvent *)event 
{
    for (UITouch* touch in [touches allObjects]) {
        NSMutableArray* points = (NSMutableArray*)CFDictionaryGetValue(_touchLines, touch);
        CGPoint point = [touch locationInView:self];
        NSValue* value = [NSValue valueWithCGPoint:point];
        [points addObject:value];
    }
    
    if (delegate != nil && [delegate respondsToSelector:@selector(readyToSavePaint)]) {
        [delegate readyToSavePaint];
    }
    
    for (UITouch* touch in [touches allObjects]) {
        CFDictionaryRemoveValue(_touchLines, touch);
    }
    [self setNeedsDisplay];
	[super touchesEnded:touches withEvent:event];
}

@end
