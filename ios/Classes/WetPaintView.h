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

#import "TiUtils.h"
#import <UIKit/UIKit.h>

@protocol WetPaintViewDelegate <NSObject>
@optional
- (void)readyToSavePaint;
@end

@interface WetPaintView : UIView {
  CFMutableDictionaryRef _touchLines;
  NSMutableArray *_completedStrokes;
  NSMutableArray *_undoStrokes;

  // Playback state
  NSTimer *_playbackTimer;
  NSMutableArray *_playbackStrokes;
  NSInteger _currentPlaybackIndex;
  BOOL _isPlayingBack;
  BOOL _isPaused;
  NSTimeInterval _playbackInterval;
}

@property CGFloat strokeWidth;
@property CGFloat strokeAlpha;
@property CGColorRef strokeColor;
@property bool erase;
@property(nonatomic, assign) id<WetPaintViewDelegate> delegate;

- (void)drawInContext:(CGContextRef)context andApplyErase:(bool)applyErase;
- (void)clear;
- (void)undo;
- (void)redo;
- (void)bakeStrokesToDelegate;

// Playback methods
- (void)playbackDrawing:(NSTimeInterval)duration;
- (void)pausePlayback;
- (void)resumePlayback;
- (void)stopPlayback;
- (void)setPlaybackSpeed:(CGFloat)speed;
- (CGFloat)getPlaybackProgress;

// Save/Load methods
- (NSArray *)getStrokesData;
- (void)loadStrokes:(NSArray *)strokesArray;

@end
