#import "HBDKeyboardInsetsView.h"

NS_ASSUME_NONNULL_BEGIN

@interface HBDKeyboardManualHandler : NSObject <HBDKeyboardHandler>

- (instancetype)initWithKeyboardInsetsView:(HBDKeyboardInsetsView *)view;

@end

NS_ASSUME_NONNULL_END
