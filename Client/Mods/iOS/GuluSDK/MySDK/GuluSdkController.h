//
//  GuluSdkController.h
//  Unity-iPhone
//
//

#import <UIKit/UIKit.h>
#import "UnityAppController.h"

@interface GuluSdkController : UnityAppController<UIApplicationDelegate>
- (void)shenhesdk:(bool *) shenhe;
- (void)initsdk;
- (void)login;
- (void)bindAccount;
- (void)logout;
- (void)userCenter;
//支付
- (void) pay:(NSString *) json;
@end
