/**
 * Ti.Paint Module
 * Copyright (c) 2010-2013 by Appcelerator, Inc. All Rights Reserved.
 * Please see the LICENSE included with this distribution for details.
 */

#import "TiPaintPaintViewProxy.h"
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

@end
