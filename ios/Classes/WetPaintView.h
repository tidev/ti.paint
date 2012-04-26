#import <UIKit/UIKit.h>
#import "TiUtils.h"

@protocol WetPaintViewDelegate <NSObject>
@optional
-(void)readyToSavePaint;
@end

@interface WetPaintView : UIView {
    CFMutableDictionaryRef _touchLines;
}

@property CGFloat strokeWidth;
@property CGFloat strokeAlpha;
@property CGColorRef strokeColor;
@property bool erase;
@property (nonatomic, assign) id<WetPaintViewDelegate> delegate;

- (void)drawInContext:(CGContextRef)context andApplyErase:(bool)applyErase;

@end