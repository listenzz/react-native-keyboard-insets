#import "HBDKeyboardInsetsView.h"
#import <React/RCTLog.h>
#import <React/RCTUIManager.h>

@implementation HBDKeyboardInsetsView {
    UIView *_focusView;
    CGFloat _edgeBottom;
    
    CADisplayLink *_displayLink;
    UIView *_keyboardView;
    CGFloat _keyboardHeight;
}

- (instancetype)init {
    if (self = [super init]) {
        _mode = @"auto";
    }
    return self;
}

- (void)willMoveToWindow:(UIWindow *)newWindow {
    if (!newWindow) {
        [[NSNotificationCenter defaultCenter] removeObserver:self name:UIKeyboardWillShowNotification object:nil];
        [[NSNotificationCenter defaultCenter] removeObserver:self name:UIKeyboardDidShowNotification object:nil];
        [[NSNotificationCenter defaultCenter] removeObserver:self name:UIKeyboardWillHideNotification object:nil];
        [[NSNotificationCenter defaultCenter] removeObserver:self name:UIKeyboardDidHideNotification object:nil];
    }
}

- (void)didMoveToWindow {
    if (self.window) {
        [[NSNotificationCenter defaultCenter] addObserver:self
                                                     selector:@selector(keyboardWillShow:)
                                                         name:UIKeyboardWillShowNotification
                                                       object:nil];
        
        [[NSNotificationCenter defaultCenter] addObserver:self
                                                     selector:@selector(keyboardDidShow:)
                                                         name:UIKeyboardDidShowNotification
                                                       object:nil];
        
        [[NSNotificationCenter defaultCenter] addObserver:self
                                                     selector:@selector(keyboardWillHide:)
                                                         name:UIKeyboardWillHideNotification
                                                       object:nil];
        
        [[NSNotificationCenter defaultCenter] addObserver:self
                                                     selector:@selector(keyboardDidHide:)
                                                         name:UIKeyboardDidHideNotification
                                                       object:nil];
    }
}

- (void)keyboardWillShow:(NSNotification *)notification {
    UIView *focusView = [HBDKeyboardInsetsView findFocusView:self];
    if (![self shouldHandleKeyboardTransition:focusView]) {
        return;
    }
    
    _focusView = focusView;
    _keyboardView = [HBDKeyboardInsetsView findKeyboardView];
    
    NSDictionary *userInfo = [notification userInfo];
    CGRect keyboardRect = [[userInfo objectForKey:UIKeyboardFrameEndUserInfoKey] CGRectValue];
    CGFloat keyboardHeight = keyboardRect.size.height;
    _keyboardHeight = keyboardHeight;
    self.onKeyboardHeightChanged(@{
        @"height": @(keyboardHeight)
    });
    
    if ([self.mode isEqualToString:@"auto"] && CGAffineTransformEqualToTransform(self.transform, CGAffineTransformIdentity)) {
        CGRect windowFrame = [self.window convertRect:focusView.frame fromView:focusView.superview];
        _edgeBottom = MAX(CGRectGetMaxY(self.window.bounds) - CGRectGetMaxY(windowFrame), 0);
    }
    
    RCTLogInfo(@"[Navigation] keyboardWillShow startWatchKeyboardTransition");
    [self startWatchKeyboardTransition];
}

- (void)keyboardDidShow:(NSNotification *)notification {
    if ([self shouldHandleKeyboardTransition:_focusView]) {
        RCTLogInfo(@"[Navigation] keyboardDidShow stopWatchKeyboardTransition");
        [self stopWatchKeyboardTransition];
        [self handleKeyboardTransition:_keyboardHeight];
    }
}

- (void)keyboardWillHide:(NSNotification *)notification {
    if ([self shouldHandleKeyboardTransition:_focusView]) {
        RCTLogInfo(@"[Navigation] keyboardWillHide startWatchKeyboardTransition");
        _keyboardView = [HBDKeyboardInsetsView findKeyboardView];
        [self startWatchKeyboardTransition];
    }
}


- (void)keyboardDidHide:(NSNotification *)notification {
    if ([self shouldHandleKeyboardTransition:_focusView]) {
        RCTLogInfo(@"[Navigation] keyboardDidHide stopWatchKeyboardTransition");
        [self stopWatchKeyboardTransition];
        [self handleKeyboardTransition:0];
    }
    _focusView = nil;
    _edgeBottom = 0;
}

- (BOOL)shouldHandleKeyboardTransition:(UIView *)focusView {
    if (focusView) {
        HBDKeyboardInsetsView *closet = [HBDKeyboardInsetsView findClosetKeyboardInsetsView:focusView];
            return closet == self;
    }
    return NO;
}


- (void)startWatchKeyboardTransition {
    [self stopWatchKeyboardTransition];
    _displayLink = [CADisplayLink displayLinkWithTarget:self selector:@selector(watchKeyboardTransition)];
    _displayLink.preferredFramesPerSecond = 120;
    [_displayLink addToRunLoop:[NSRunLoop mainRunLoop] forMode:NSRunLoopCommonModes];
    
}

- (void)stopWatchKeyboardTransition {
    [_displayLink invalidate];
    _displayLink = nil;
}

- (void)watchKeyboardTransition {
    if (_keyboardView == nil) {
        return;
    }
    
    CGFloat keyboardFrameY = [_keyboardView.layer presentationLayer].frame.origin.y;
    CGFloat keyboardWindowH = _keyboardView.window.bounds.size.height;
    [self handleKeyboardTransition:(keyboardWindowH - keyboardFrameY)];
}

- (void)handleKeyboardTransition:(CGFloat)position {
    if ([self.mode isEqualToString:@"auto"]) {
        if (_focusView) {
            CGFloat translationY = -MAX(position - _edgeBottom + self.extraHeight, 0);
            self.transform = CGAffineTransformMakeTranslation(0, translationY);
        }
    } else {
        self.onInsetsChanged(@{
            @"top": @0,
            @"left": @0,
            @"right": @0,
            @"bottom": @(position)
        });
    }

}

+ (UIView *)findKeyboardView {
    NSArray<UIWindow *> *windows = UIApplication.sharedApplication.windows;
    for (UIWindow *window in windows) {
        if ([window.description hasPrefix:@"<UITextEffectsWindow"]) {
            for (UIView *subview in window.subviews) {
                if ([subview.description hasPrefix:@"<UIInputSetContainerView"]) {
                    for (UIView *hostView in subview.subviews) {
                        if ([hostView.description hasPrefix:@"<UIInputSetHostView"]) {
                            return hostView;
                        }
                    }
                    break;
                }
            }
            break;
        }
    }
    return nil;
}

+ (UIView *)findFocusView:(UIView *)view {
    if ([view isFirstResponder]) {
        return view;
    }
    
    for (UIView *child in view.subviews) {
        UIView *focus = [self findFocusView:child];
        if (focus) {
            return focus;
        }
    }
    
    return nil;
}

+ (HBDKeyboardInsetsView *)findClosetKeyboardInsetsView:(UIView *)view {
    if ([view isKindOfClass:[HBDKeyboardInsetsView class]]) {
        return (HBDKeyboardInsetsView *)view;
    }
    
    if (view.superview) {
        return [self findClosetKeyboardInsetsView:view.superview];
    }
    
    return nil;
}

@end
