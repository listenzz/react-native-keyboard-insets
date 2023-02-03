#import "HBDKeyboardInsetsViewManager.h"
#import "HBDKeyboardInsetsView.h"

@implementation HBDKeyboardInsetsViewManager

RCT_EXPORT_MODULE(KeyboardInsetsView)

- (UIView *)view {
    return [[HBDKeyboardInsetsView alloc] init];
}

RCT_EXPORT_VIEW_PROPERTY(mode, NSString)
RCT_EXPORT_VIEW_PROPERTY(extraHeight, CGFloat)

RCT_EXPORT_VIEW_PROPERTY(onStatusChanged, RCTDirectEventBlock)
RCT_EXPORT_VIEW_PROPERTY(onPositionChanged, RCTDirectEventBlock)

@end
