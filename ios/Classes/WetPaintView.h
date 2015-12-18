/**
 * WetPaintView.h
 * paint
 * 
 * Created by Dawson Toth on 4/20/12.
 * Copyright (c) 2012 Toth Solutions, LLC. All rights reserved.
 * 
 * Ti.Paint Module
 * Copyright (c) 2010-2013 by Appcelerator, Inc. All Rights Reserved.
 * Please see the LICENSE included with this distribution for details.
 */

#import <UIKit/UIKit.h>
#import "TiUtils.h"
#import "TiUIView.h"

@protocol WetPaintViewDelegate <NSObject>
@optional
-(void)readyToSavePaint;
@end

@interface WetPaintView : UIView {
    NSMutableArray *prop;
    
}
@property (nonatomic ,assign) TiProxy*proxy;
@property CGFloat strokeWidth;
@property bool strokeForce;
@property CGFloat strokeAlpha;
@property CGColorRef strokeColor;
@property bool erase;
@property CGFloat widthModifier;
@property (nonatomic, assign) id<WetPaintViewDelegate> delegate;


- (void)drawInContext:(CGContextRef)context andApplyErase:(bool)applyErase;
-(CGPoint)createPoint:(int)index;

@end