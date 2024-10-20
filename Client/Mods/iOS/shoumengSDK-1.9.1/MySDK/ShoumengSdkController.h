//
//  SpSdkController.h
//  Unity-iPhone
//
//

#import <UIKit/UIKit.h>
#import "UnityAppController.h"

@interface ShoumengSdkController : UnityAppController<UIApplicationDelegate>
- (void)initsdk;
- (void)login;
- (void)bindAccount;
- (void)logout;
- (void)userCenter;
//支付
- (void) pay:(NSString *) json;
@end
