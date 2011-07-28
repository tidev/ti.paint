/**
 * Titanium Paint Module
 *
 * Appcelerator Titanium is Copyright (c) 2009-2010 by Appcelerator, Inc.
 * and licensed under the Apache Public License (version 2)
 */

#import "TiPaintPaintViewProxy.h"
#import "TiUtils.h"

@implementation TiPaintPaintViewProxy


-(void)clear:(id)args
{
	[[self view] performSelectorOnMainThread:@selector(clear:) withObject:args waitUntilDone:NO];
}

@end
