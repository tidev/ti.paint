/**
 * Ti.Paint Module
 * Copyright (c) 2010-2013 by Appcelerator, Inc. All Rights Reserved.
 * Please see the LICENSE included with this distribution for details.
 */

#import "TiPaintPaintViewProxy.h"
#import "TiPaintPaintView.h"
#import "TiUtils.h"

@implementation TiPaintPaintViewProxy


-(void)clear:(id)args
{
	[[self view] performSelectorOnMainThread:@selector(clear:) withObject:args waitUntilDone:NO];
}

-(void)undo:(id)args
{
	[[self view] performSelectorOnMainThread:@selector(undo:) withObject:args waitUntilDone:NO];
}

-(void)redo:(id)args
{
	[[self view] performSelectorOnMainThread:@selector(redo:) withObject:args waitUntilDone:NO];
}

-(TiBlob*)toBlob:(id)args
{
	__block TiBlob* result = nil;
	TiThreadPerformOnMainThread(^{
		result = [[self view] toBlob:args];
	}, YES);
	return result;
}

// Playback methods
-(void)playbackDrawing:(id)args
{
	[[self view] performSelectorOnMainThread:@selector(playbackDrawing:) withObject:args waitUntilDone:NO];
}

-(void)pausePlayback:(id)args
{
	[[self view] performSelectorOnMainThread:@selector(pausePlayback:) withObject:args waitUntilDone:NO];
}

-(void)resumePlayback:(id)args
{
	[[self view] performSelectorOnMainThread:@selector(resumePlayback:) withObject:args waitUntilDone:NO];
}

-(void)stopPlayback:(id)args
{
	[[self view] performSelectorOnMainThread:@selector(stopPlayback:) withObject:args waitUntilDone:NO];
}

-(void)setPlaybackSpeed:(id)args
{
	[[self view] performSelectorOnMainThread:@selector(setPlaybackSpeed:) withObject:args waitUntilDone:NO];
}

-(NSNumber*)getPlaybackProgress:(id)args
{
	__block CGFloat result = 0.0;
	TiThreadPerformOnMainThread(^{
		TiPaintPaintView* paintView = (TiPaintPaintView*)[self view];
		result = [paintView getPlaybackProgress];
	}, YES);
	return [NSNumber numberWithFloat:result];
}

// Save/Load methods
-(NSArray*)getStrokesData:(id)args
{
	__block NSArray* result = nil;
	TiThreadPerformOnMainThread(^{
		TiPaintPaintView* paintView = (TiPaintPaintView*)[self view];
		result = [paintView getStrokesData];
	}, YES);
	return result;
}

-(void)loadStrokes:(id)args
{
	[[self view] performSelectorOnMainThread:@selector(loadStrokes:) withObject:args waitUntilDone:NO];
}

@end
