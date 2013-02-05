/**
 * Ti.Paint Module
 * Copyright (c) 2010-2013 by Appcelerator, Inc. All Rights Reserved.
 * Please see the LICENSE included with this distribution for details.
 */

#import "TiPaintPaintView.h"
#import "TiUtils.h"

@implementation TiPaintPaintView

- (id)init
{
    if ((self = [super init]))
    {
        self.multipleTouchEnabled = YES;
        wetPaintView = [[WetPaintView alloc] initWithFrame:self.bounds];
        wetPaintView.delegate = self;
        [self addSubview:wetPaintView];
        [self bringSubviewToFront:wetPaintView];
    }
    return self;
}

-(BOOL)proxyHasTapListener
{
    // The TiUIView only sets multipleTouchEnabled to YES if we have a tap listener.
    // So... let's make it think that we do! (Note that we don't actually need one.)
    return YES;
}

- (void)dealloc
{
    [wetPaintView removeFromSuperview];
    RELEASE_TO_NIL(wetPaintView);
    RELEASE_TO_NIL(drawImage);
    [super dealloc];
}

- (void)frameSizeChanged:(CGRect)frame bounds:(CGRect)bounds
{
    [super frameSizeChanged:frame bounds:bounds];
    [wetPaintView setFrame:bounds];
    if (drawImage != nil)
    {
        [drawImage setFrame:bounds];
    }
    
    // MOD-348: Ensure that we get a solid box in which to draw. Otherwise, we'll end
    // up with blurry lines and visual defects.
    drawBox = CGRectMake(bounds.origin.x, bounds.origin.y,
                         ceilf(bounds.size.width), ceilf(bounds.size.height));
}

#pragma mark Utility

- (UIImageView*)imageView
{
    if (drawImage == nil)
    {
        drawImage = [[UIImageView alloc] initWithImage:nil];
        drawImage.frame = [self bounds];
        [self addSubview:drawImage];
        [self bringSubviewToFront:wetPaintView];
    }
    return drawImage;
}

#pragma mark Wet Paint View Delegate

-(void)readyToSavePaint
{
    char majorVersion = [[[UIDevice currentDevice] systemVersion] characterAtIndex:0];
    if (majorVersion == '2' || majorVersion == '3') {
        UIGraphicsBeginImageContext(drawBox.size);
    }
    else {
        UIGraphicsBeginImageContextWithOptions(drawBox.size, NO, 0.0);
    }
    CGContextRef context = UIGraphicsGetCurrentContext();
    [[self imageView].image drawInRect:CGRectMake(0, 0, drawBox.size.width, drawBox.size.height)];
    [wetPaintView drawInContext:context andApplyErase:YES];
    [self imageView].image = UIGraphicsGetImageFromCurrentImageContext();
    UIGraphicsEndImageContext();
}

#pragma mark Public APIs

- (void)setEraseMode_:(id)value
{
    wetPaintView.erase = [TiUtils boolValue:value];
}

- (void)setStrokeWidth_:(id)width
{
    wetPaintView.strokeWidth = [TiUtils floatValue:width];
}

- (void)setStrokeColor_:(id)value
{
    CGColorRelease(wetPaintView.strokeColor);
    TiColor *color = [TiUtils colorValue:value];
    wetPaintView.strokeColor = [color _color].CGColor;
    CGColorRetain(wetPaintView.strokeColor);
}

- (void)setStrokeAlpha_:(id)alpha
{
    wetPaintView.strokeAlpha = [TiUtils floatValue:alpha] / 255.0;
}

- (void)setImage_:(id)value
{
    ENSURE_UI_THREAD(setImage_, value);
    RELEASE_TO_NIL(drawImage);
    UIImage* image = value == nil ? nil : [TiUtils image:value proxy:self.proxy];
    if (image != nil) {
        drawImage = [[UIImageView alloc] initWithImage:image];
        drawImage.frame = [self bounds];
        [self addSubview:drawImage];
        [self bringSubviewToFront:wetPaintView];
        UIView *view = [self imageView];
        [drawImage.image drawInRect:CGRectMake(0, 0, view.frame.size.width, view.frame.size.height)];
    }
}

- (void)clear:(id)args
{
    if (drawImage != nil)
    {
        drawImage.image = nil;
    }
}

@end
