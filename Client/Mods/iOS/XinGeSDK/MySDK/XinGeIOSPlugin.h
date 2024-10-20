#import <Foundation/Foundation.h>

static NSData* mDeviceToken;
@interface XinGeIOSPlugin : NSObject

+(void)SetDeviceToken:(NSData*)token;
+(void)SenMessageToUnity:(nonnull NSString *)pMethod Message:(nonnull NSString *)pMessage;

@end