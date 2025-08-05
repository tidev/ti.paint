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
        _completedStrokes = [[NSMutableArray alloc] init];
        _undoStrokes = [[NSMutableArray alloc] init];
        
        // Initialize playback state
        _playbackStrokes = [[NSMutableArray alloc] init];
        _currentPlaybackIndex = 0;
        _isPlayingBack = NO;
        _isPaused = NO;
        _playbackInterval = 0.1;
        
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
    [_completedStrokes release];
    [_undoStrokes release];
    [_playbackStrokes release];
    [_playbackTimer invalidate];
	[super dealloc];
}

#pragma mark Public API

- (void)drawInContext:(CGContextRef)context andApplyErase:(bool)applyErase {
    CGContextSetLineCap(context, kCGLineCapRound);
    
    // Choose which strokes to draw based on playback state
    NSArray* strokesToDraw = _isPlayingBack ? _playbackStrokes : _completedStrokes;
    
    // Draw strokes
    for (NSDictionary* stroke in strokesToDraw) {
        NSMutableArray* points = [stroke objectForKey:@"points"];
        CGFloat width = [[stroke objectForKey:@"strokeWidth"] floatValue];
        CGFloat alpha = [[stroke objectForKey:@"strokeAlpha"] floatValue];
        CGColorRef color = (CGColorRef)[stroke objectForKey:@"strokeColor"];
        bool eraseMode = [[stroke objectForKey:@"erase"] boolValue];
        
        CGContextSetLineWidth(context, width);
        CGContextSetStrokeColorWithColor(context, color);
        
        if (!eraseMode) {
            CGContextSetAlpha(context, alpha);
            CGContextSetBlendMode(context, kCGBlendModeNormal);
        } else if (applyErase) {
            CGContextSetBlendMode(context, kCGBlendModeClear);
        } else {
            // Show erase strokes with their original color (visual feedback)
            CGContextSetAlpha(context, alpha);
            CGContextSetBlendMode(context, kCGBlendModeNormal);
        }
        
        [self drawPointsArray:points inContext:context];
    }
    
    // Draw current active strokes only if not in playback mode
    if (!_isPlayingBack) {
        CGContextSetLineWidth(context, strokeWidth);
        CGContextSetStrokeColorWithColor(context, strokeColor);
        if (!erase) {
            CGContextSetAlpha(context, strokeAlpha);
            CGContextSetBlendMode(context, kCGBlendModeNormal);
            CFDictionaryApplyFunction(_touchLines, drawPoints, context);
        } else if (applyErase) {
            CGContextSetBlendMode(context, kCGBlendModeClear);
            CFDictionaryApplyFunction(_touchLines, drawPoints, context);
        } else {
            // Show erase strokes with user's chosen color (visual feedback)
            CGContextSetAlpha(context, strokeAlpha);
            CGContextSetStrokeColorWithColor(context, strokeColor);
            CGContextSetBlendMode(context, kCGBlendModeNormal);
            CFDictionaryApplyFunction(_touchLines, drawPoints, context);
        }
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

- (void)drawPointsArray:(NSMutableArray*)points inContext:(CGContextRef)context
{
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
    // Clear redo history when new stroke starts
    [_undoStrokes removeAllObjects];
    
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
        
        // Save completed stroke to array instead of immediately baking
        NSDictionary* stroke = [NSDictionary dictionaryWithObjectsAndKeys:
                               [NSMutableArray arrayWithArray:points], @"points",
                               [NSNumber numberWithFloat:strokeWidth], @"strokeWidth",
                               [NSNumber numberWithFloat:strokeAlpha], @"strokeAlpha",
                               (id)strokeColor, @"strokeColor",
                               [NSNumber numberWithBool:erase], @"erase",
                               nil];
        [_completedStrokes addObject:stroke];
    }
    
    // NOTE: We DON'T call readyToSavePaint here anymore
    // It will be called only when we actually need to bake the image
    
    for (UITouch* touch in [touches allObjects]) {
        CFDictionaryRemoveValue(_touchLines, touch);
    }
    [self setNeedsDisplay];
	[super touchesEnded:touches withEvent:event];
}

#pragma mark Clear/Undo/Redo

- (void)clear {
    [_completedStrokes removeAllObjects];
    [_undoStrokes removeAllObjects];
    CFDictionaryRemoveAllValues(_touchLines);
    [self setNeedsDisplay];
}

- (void)undo
{
    if ([_completedStrokes count] > 0) {
        NSDictionary* lastStroke = [_completedStrokes lastObject];
        [_undoStrokes addObject:lastStroke];
        [_completedStrokes removeLastObject];
        [self setNeedsDisplay];
    }
}

- (void)redo
{
    if ([_undoStrokes count] > 0) {
        NSDictionary* lastUndoStroke = [_undoStrokes lastObject];
        [_completedStrokes addObject:lastUndoStroke];
        [_undoStrokes removeLastObject];
        [self setNeedsDisplay];
    }
}

// Method to force baking when needed
- (void)bakeStrokesToDelegate
{
    if (delegate != nil && [delegate respondsToSelector:@selector(readyToSavePaint)]) {
        [delegate readyToSavePaint];
    }
}

#pragma mark Playback Methods

- (void)playbackDrawing:(NSTimeInterval)duration
{
    if ([_completedStrokes count] == 0) return;
    
    [self stopPlayback]; // Stop any existing playback
    
    _isPlayingBack = YES;
    _isPaused = NO;
    _currentPlaybackIndex = 0;
    [_playbackStrokes removeAllObjects];
    
    _playbackInterval = duration / [_completedStrokes count];
    // Ensure reasonable maximum interval (no more than 1 second between strokes)
    if (_playbackInterval > 1.0) {
        _playbackInterval = 1.0;
    }
    
    _playbackTimer = [NSTimer scheduledTimerWithTimeInterval:_playbackInterval
                                                     repeats:YES
                                                       block:^(NSTimer *timer) {
        if (self->_currentPlaybackIndex < [self->_completedStrokes count] && self->_isPlayingBack && !self->_isPaused) {
            [self->_playbackStrokes addObject:[self->_completedStrokes objectAtIndex:self->_currentPlaybackIndex]];
            self->_currentPlaybackIndex++;
            [self setNeedsDisplay];
        } else if (self->_currentPlaybackIndex >= [self->_completedStrokes count]) {
            [self stopPlayback];
        }
    }];
}

- (void)pausePlayback
{
    _isPaused = YES;
}

- (void)resumePlayback
{
    _isPaused = NO;
}

- (void)stopPlayback
{
    [_playbackTimer invalidate];
    _playbackTimer = nil;
    _isPlayingBack = NO;
    _isPaused = NO;
    _currentPlaybackIndex = 0;
    [_playbackStrokes removeAllObjects];
    [self setNeedsDisplay];
}

- (void)setPlaybackSpeed:(CGFloat)speed
{
    if (_playbackTimer && _isPlayingBack) {
        NSTimeInterval newInterval = (_playbackInterval / speed);
        [_playbackTimer invalidate];
        
        _playbackTimer = [NSTimer scheduledTimerWithTimeInterval:newInterval
                                                         repeats:YES
                                                           block:^(NSTimer *timer) {
            if (self->_currentPlaybackIndex < [self->_completedStrokes count] && self->_isPlayingBack && !self->_isPaused) {
                [self->_playbackStrokes addObject:[self->_completedStrokes objectAtIndex:self->_currentPlaybackIndex]];
                self->_currentPlaybackIndex++;
                [self setNeedsDisplay];
            } else if (self->_currentPlaybackIndex >= [self->_completedStrokes count]) {
                [self stopPlayback];
            }
        }];
    }
}

- (CGFloat)getPlaybackProgress
{
    if ([_completedStrokes count] == 0) return 0.0;
    return (CGFloat)_currentPlaybackIndex / (CGFloat)[_completedStrokes count];
}

#pragma mark Save/Load Methods

- (NSArray*)getStrokesData
{
    NSMutableArray* strokesData = [[NSMutableArray alloc] init];
    
    for (NSDictionary* stroke in _completedStrokes) {
        NSMutableDictionary* strokeData = [[NSMutableDictionary alloc] init];
        
        // Get stroke properties
        NSArray* points = [stroke objectForKey:@"points"];
        NSNumber* width = [stroke objectForKey:@"strokeWidth"];
        NSNumber* alpha = [stroke objectForKey:@"strokeAlpha"];
        id colorObj = [stroke objectForKey:@"color"];
        NSNumber* isErase = [stroke objectForKey:@"erase"];
        
        if (points) {
            NSMutableArray* pointsArray = [[NSMutableArray alloc] init];
            for (NSValue* pointValue in points) {
                CGPoint point = [pointValue CGPointValue];
                NSDictionary* pointDict = @{
                    @"x": @(point.x),
                    @"y": @(point.y)
                };
                [pointsArray addObject:pointDict];
            }
            [strokeData setObject:pointsArray forKey:@"points"];
        }
        if (width) [strokeData setObject:width forKey:@"strokeWidth"];
        if (alpha) [strokeData setObject:alpha forKey:@"strokeAlpha"];
        [strokeData setObject:(isErase ? isErase : @(NO)) forKey:@"eraseMode"];
        
        // Convert CGColor to hex string
        if (colorObj) {
            CGColorRef color = (__bridge CGColorRef)colorObj;
            const CGFloat* components = CGColorGetComponents(color);
            size_t numComponents = CGColorGetNumberOfComponents(color);
            
            int red = 0, green = 0, blue = 0;
            if (numComponents >= 3) {
                red = (int)(components[0] * 255);
                green = (int)(components[1] * 255);
                blue = (int)(components[2] * 255);
            } else if (numComponents >= 1) {
                // Grayscale color
                red = green = blue = (int)(components[0] * 255);
            }
            
            NSString* hexColor = [NSString stringWithFormat:@"#%02x%02x%02x", red, green, blue];
            [strokeData setObject:hexColor forKey:@"strokeColor"];
        } else {
            [strokeData setObject:@"#000000" forKey:@"strokeColor"];
        }
        
        [strokesData addObject:strokeData];
    }
    
    return strokesData;
}

- (void)loadStrokes:(NSArray*)strokesArray
{
    [_completedStrokes removeAllObjects];
    [_undoStrokes removeAllObjects];
    
    for (NSDictionary* strokeData in strokesArray) {
        NSMutableDictionary* stroke = [[NSMutableDictionary alloc] init];
        
        // Get stroke data
        NSArray* points = [strokeData objectForKey:@"points"];
        NSNumber* width = [strokeData objectForKey:@"strokeWidth"];
        NSNumber* alpha = [strokeData objectForKey:@"strokeAlpha"];
        NSString* hexColor = [strokeData objectForKey:@"strokeColor"];
        NSNumber* isErase = [strokeData objectForKey:@"eraseMode"];
        
        if (points) {
            NSMutableArray* pointsArray = [[NSMutableArray alloc] init];
            for (NSDictionary* pointDict in points) {
                CGFloat x = [[pointDict objectForKey:@"x"] floatValue];
                CGFloat y = [[pointDict objectForKey:@"y"] floatValue];
                CGPoint point = CGPointMake(x, y);
                [pointsArray addObject:[NSValue valueWithCGPoint:point]];
            }
            [stroke setObject:pointsArray forKey:@"points"];
        }
        if (width) [stroke setObject:width forKey:@"strokeWidth"];
        if (alpha) [stroke setObject:alpha forKey:@"strokeAlpha"];
        if (isErase) [stroke setObject:isErase forKey:@"erase"];
        
        // Convert hex string to CGColor
        if (hexColor) {
            UIColor* uiColor = [self colorFromHexString:hexColor];
            [stroke setObject:(__bridge id)[uiColor CGColor] forKey:@"color"];
        }
        
        [_completedStrokes addObject:stroke];
    }
    
    [self setNeedsDisplay];
}

- (UIColor*)colorFromHexString:(NSString*)hexString {
    NSString* cleanString = [hexString stringByReplacingOccurrencesOfString:@"#" withString:@""];
    if ([cleanString length] == 3) {
        cleanString = [NSString stringWithFormat:@"%@%@%@%@%@%@",
                      [cleanString substringWithRange:NSMakeRange(0, 1)],[cleanString substringWithRange:NSMakeRange(0, 1)],
                      [cleanString substringWithRange:NSMakeRange(1, 1)],[cleanString substringWithRange:NSMakeRange(1, 1)],
                      [cleanString substringWithRange:NSMakeRange(2, 1)],[cleanString substringWithRange:NSMakeRange(2, 1)]];
    }
    if ([cleanString length] == 6) {
        cleanString = [cleanString stringByAppendingString:@"ff"];
    }
    
    unsigned int baseValue;
    [[NSScanner scannerWithString:cleanString] scanHexInt:&baseValue];
    
    float red = ((baseValue >> 24) & 0xFF)/255.0f;
    float green = ((baseValue >> 16) & 0xFF)/255.0f;
    float blue = ((baseValue >> 8) & 0xFF)/255.0f;
    float alpha = ((baseValue >> 0) & 0xFF)/255.0f;
    
    return [UIColor colorWithRed:red green:green blue:blue alpha:alpha];
}

@end
