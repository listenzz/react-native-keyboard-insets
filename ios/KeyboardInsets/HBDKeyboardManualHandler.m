#import "HBDKeyboardManualHandler.h"

#import <React/RCTLog.h>
#import <React/RCTUIManager.h>
#import <React/RCTScrollView.h>

@interface HBDKeyboardManualHandler ()

@property (nonatomic, weak) HBDKeyboardInsetsView *view;

@end

@implementation HBDKeyboardManualHandler

- (instancetype)initWithKeyboardInsetsView:(HBDKeyboardInsetsView *)view {
    if (self = [super init]) {
        _view = view;
    }
    return self;
}

- (void)keyboardWillShow:(UIView *)focusView keyboardHeight:(CGFloat)keyboardHeight {
    self.view.onStatusChanged(@{
        @"height": @(keyboardHeight),
        @"shown": @(YES),
        @"transitioning": @(YES),
    });
}

- (void)keyboardDidShow:(UIView *)focusView keyboardHeight:(CGFloat)keyboardHeight {
    [self handleKeyboardTransition:keyboardHeight];
    self.view.onStatusChanged(@{
        @"height": @(keyboardHeight),
        @"shown": @(YES),
        @"transitioning": @(NO),
    });
}

- (void)keyboardWillHide:(UIView *)focusView keyboardHeight:(CGFloat)keyboardHeight {
    self.view.onStatusChanged(@{
        @"height": @(keyboardHeight),
        @"shown": @(NO),
        @"transitioning": @(YES),
    });
}

- (void)keyboardDidHide:(UIView *)focusView keyboardHeight:(CGFloat)keyboardHeight {
    [self handleKeyboardTransition:0];
    self.view.onStatusChanged(@{
        @"height": @(keyboardHeight),
        @"shown": @(NO),
        @"transitioning": @(NO),
    });
}

- (void)handleKeyboardTransition:(CGFloat)position {
    RCTLogInfo(@"[KeyboardInsetsView] keyboard position: %f", position);
    self.view.onPositionChanged(@{
        @"position": @(position)
    });
}

@end
