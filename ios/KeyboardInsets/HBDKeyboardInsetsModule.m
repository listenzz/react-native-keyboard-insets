#import "HBDKeyboardInsetsModule.h"
#import <React/RCTLog.h>
#import <React/RCTUIManager.h>

@implementation HBDKeyboardInsetsModule

RCT_EXPORT_MODULE(KeyboardInsetsModule)

@synthesize bridge;

+ (BOOL)requiresMainQueueSetup {
    return YES;
}

- (dispatch_queue_t)methodQueue {
    return dispatch_get_main_queue();
}

RCT_EXPORT_METHOD(getEdgeInsetsForView:(nonnull NSNumber *)reactTag callback:(RCTResponseSenderBlock)callback) {
    RCTUIManager* uiManager = [self.bridge moduleForClass:[RCTUIManager class]];
    UIView* view = [uiManager viewForReactTag:reactTag];
    CGRect windowFrame = [view.window convertRect:view.frame fromView:view.superview];
    
    callback(@[
        @{
            @"left":   @(MAX(0, windowFrame.origin.x)),
            @"top":    @(MAX(0, windowFrame.origin.y)),
            @"right":  @(MAX(0, CGRectGetMaxX(view.window.bounds) - CGRectGetMaxX(windowFrame))),
            @"bottom": @(MAX(0, CGRectGetMaxY(view.window.bounds) - CGRectGetMaxY(windowFrame))),
        }
    ]);
}

@end
