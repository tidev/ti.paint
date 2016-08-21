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
#import "TiPaintPaintViewProxy.h"
#import "TiUIView.h"


@implementation WetPaintView

#pragma mark Properties

@synthesize strokeAlpha;
@synthesize strokeForce;
@synthesize strokeWidth;
@synthesize strokeColor;
@synthesize erase;
@synthesize widthModifier;
@synthesize delegate;

#pragma mark Lifecycle

- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        self.multipleTouchEnabled = YES;
        strokeWidth = 5;
        strokeAlpha = 1;
        strokeForce = true;
		strokeColor = CGColorRetain([[TiUtils colorValue:@"#000"] _color].CGColor);
        prop = [[[NSMutableArray alloc] init]retain];
        widthModifier = 1;
        self.opaque = NO;
        self.backgroundColor = [UIColor colorWithWhite:0.0 alpha:0.0];
    }
    return self;
}

- (void)dealloc
{
	CGColorRelease(strokeColor);
    RELEASE_TO_NIL(prop);
	[super dealloc];
}

#pragma mark Public API

- (void)drawInContext:(CGContextRef)context andApplyErase:(bool)applyErase {
    CGContextSetLineCap(context, kCGLineCapRound);
    //CGContextSetLineWidth(context, strokeWidth);
    CGContextSetStrokeColorWithColor(context, strokeColor);
    if (!erase) {
        CGContextSetAlpha(context, strokeAlpha);
        CGContextSetBlendMode(context, kCGBlendModeNormal);
        CGContextSetLineJoin (context, kCGLineJoinRound);
        //CFDictionaryApplyFunction(_touchLines, drawPoints, context);
        [self drawPoints:context];
    }
    else if (applyErase) {
        CGContextSetBlendMode(context, kCGBlendModeClear);
        [self drawPoints:context];
    }
    else {
        CGContextSetAlpha(context, 1); // Erasing does not respect alpha, unfortunately.
        CGContextSetStrokeColorWithColor(context, [[TiUtils colorValue:@"#000"] _color].CGColor);
        [self drawPoints:context];
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


- (void)drawPoints:(CGContextRef)context
{
    CGContextRef _context = context;
    if (prop == nil || [prop count] == 0) {
        return;
    }
    
    CGContextBeginPath(_context);
    CGPoint lastPoint = CGPointMake(0, 0);
    
    for (int i = 0; i < [prop count];i++) {
        
        if (lastPoint.x != 0 || lastPoint.y != 0) {
            
            NSMutableDictionary *tempDict = [prop objectAtIndex:i];
            CGPoint point = [self createPoint:i];
            CGContextMoveToPoint(_context,lastPoint.x, lastPoint.y);
            CGContextAddLineToPoint(_context, point.x, point.y);
            if ([TiUtils forceTouchSupported] && strokeForce) {
            strokeWidth = [TiUtils floatValue:[tempDict valueForKey:@"force"]]*widthModifier;
            }
            CGContextSetLineWidth(_context,strokeWidth);
            CGContextStrokePath(_context);
        }
        lastPoint = [self createPoint:i];
    }
    CGContextStrokePath(_context);
}

-(CGPoint)createPoint:(int)index {
    
    NSMutableDictionary *getReturnPoint = [NSMutableDictionary dictionaryWithDictionary:[prop objectAtIndex:index]];
    CGPoint retunPoint = CGPointMake([TiUtils floatValue:[getReturnPoint valueForKey:@"x"]], [TiUtils floatValue:[getReturnPoint valueForKey:@"y"]]);;
    return retunPoint;
}

#pragma mark Touch Delegate

- (void)touchesBegan:(NSSet *)touches withEvent:(UIEvent *)event 
{
        UITouch* touch = [touches anyObject];
        [prop addObject:[TiUtils touchPropertiesToDictionary:touch andPoint:[touch locationInView:self]]];

    if ([[self proxy]_hasListeners:@"touchstart"]) {
        NSDictionary *evt = [TiUtils touchPropertiesToDictionary:touch andPoint:[touch locationInView:self]];
        [[self proxy] fireEvent:@"touchstart" withObject:evt];
    }
    
	[super touchesBegan:touches withEvent:event];
}

- (void)touchesMoved:(NSSet *)touches withEvent:(UIEvent *)event 
{
	for (UITouch* touch in [touches allObjects]) {
        [prop addObject:[TiUtils touchPropertiesToDictionary:touch andPoint:[touch locationInView:self]]];
    }
    
    if ([[self proxy]_hasListeners:@"touchmove"]) {
        UITouch* touch = [touches anyObject];
        NSDictionary *evt = [TiUtils touchPropertiesToDictionary:touch andPoint:[touch locationInView:self]];
        [[self proxy] fireEvent:@"touchmove" withObject:evt];
    }

    [self setNeedsDisplay];
	[super touchesMoved:touches withEvent:event];
}

- (void) touchesCancelled:(NSSet *)touches withEvent:(UIEvent *)event
{
    if ([[self proxy]_hasListeners:@"touchcancel"]) {
        UITouch* touch = [touches anyObject];
        NSDictionary *evt = [TiUtils touchPropertiesToDictionary:touch andPoint:[touch locationInView:self]];
        [[self proxy] fireEvent:@"touchcancel" withObject:evt];
    }
    
    [prop removeAllObjects];
    [self setNeedsDisplay];
    [super touchesCancelled:touches withEvent:event];
}

- (void)touchesEnded:(NSSet *)touches withEvent:(UIEvent *)event 
{
    UITouch* touch = [touches anyObject];
    [prop addObject:[TiUtils touchPropertiesToDictionary:touch andPoint:[touch locationInView:self]]];

    if ([[self proxy]_hasListeners:@"touchend"]) {
        UITouch* touch = [touches anyObject];
        NSDictionary *evt = [TiUtils touchPropertiesToDictionary:touch andPoint:[touch locationInView:self]];
        [[self proxy] fireEvent:@"touchend" withObject:evt];
    }
    
    if (delegate != nil && [delegate respondsToSelector:@selector(readyToSavePaint)]) {
        [delegate readyToSavePaint];
    }
    [prop removeAllObjects];
    [self setNeedsDisplay];
	[super touchesEnded:touches withEvent:event];
}

@end
