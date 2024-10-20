//
//  GuluSdkController.m
//  Unity-iPhone
//
//

#import "GuluSdkController.h"
#import "MTSDK.h"

IMPL_APP_CONTROLLER_SUBCLASS (GuluSdkController)

extern void UnitySendMessage(const char *, const char *, const char *);

@interface GuluSdkController ()<MTSDKDelegate>
@property (nonatomic,assign)BOOL flag;
@end
@implementation GuluSdkController
{
}

//通用代码开始......

//游戏初始化完成自动调用
- (BOOL)application:(UIApplication*)application didFinishLaunchingWithOptions:(NSDictionary*)launchOptions
{
    [super application:application didFinishLaunchingWithOptions:launchOptions];
     NSLog(@"GuluSdkController didFinishLaunchingWithOptions");
    //这里可增加一些SDK要求加入到这个过程的处理

    return YES;
}

//发送消息给Unity type 类型   code 代号   data 内容
- (void)sendMessageToUnity:(char *)type code:(int)code data:(NSString *)data
{
    //    NSString *message=[NSString stringWithFormat:@"{\"type\":\"%s\",\"code\":%d,\"data\":\"%@\"}",type,code,data];
    
    NSDictionary *dict = [NSDictionary dictionaryWithObjectsAndKeys:[NSString stringWithFormat:@"%s", type], @"type", [NSString stringWithFormat:@"%d", code], @"code", data, @"data", nil];
    NSData *jData = [NSJSONSerialization dataWithJSONObject:dict options:0 error:NULL];
    NSString *message = [[NSString alloc] initWithData:jData encoding:NSUTF8StringEncoding];
    
    NSLog(@"sendMessageToUnity %@",message);
    
    UnitySendMessage("PlatformAPI", "OnSdkCallback", [message cStringUsingEncoding:NSUTF8StringEncoding]);
}

//通用代码结束......

//是否审核中
-(void)shenhesdk:(bool *) shenhe
{
    NSLog(@"设置是否审核中: %@", shenhe?@"YES":@"NO");
    [MTSDK INSTANCE].isCheck = shenhe;
}

//初始化
-(void)initsdk
{
    NSLog(@"初始化 init");
    //一般要在这里增加回调监听
    [MTSDK initWidthAppId:@"gldd1017" appKey:@"53B9B38320AB4054B071A70689CBD0CA" appChannel:@"gulusdk_ios" gameVersion:@"1.0.0.0" delegate:self];
}

//登陆
- (void)login
{
    NSLog(@"登陆 login");
    [MTSDK show];
}

//注销
- (void)logout
{
    NSLog(@"注销 logout");
    [MTSDK loginOut];
}

//绑定
- (void)bindAccount
{
    NSLog(@"绑定 bindAccount");
}

//帐号管理
- (void)userCenter
{
    NSLog(@"帐号管理 userCenter");
}


/**
 *支付
 * orderSerial 订单号
 * pid 商品ID
 * name 商品名称，如XX元宝
 * price 商品价格RMB，充值金额
 * count 商品数量，一般为1
 * serverId 充值服务器ID
 */
- (void) pay:(NSString *) json
{
    NSLog(@"支付 json=%@", json);
}


#if !__has_feature(objc_arc)
-(void)dealloc
{
    [super dealloc];
}
#endif

//各种回调结束......

#pragma mark -MTSDKDelegate
- (void) onInited:(NSUInteger)code{
    int c = (unsigned int)code;
    NSLog(@"code = %u",c);
    [self sendMessageToUnity:"init" code:c data:@""];
}
- (void) onLogin:(NSString *)loginlk{
    NSLog(@"Loginlk = %@",loginlk);
    [self sendMessageToUnity:"login" code:0 data:loginlk];
}

- (void) onLoginout{
    NSLog(@"登出");
    [self sendMessageToUnity:"logout" code:0 data:@""];
}
- (void) loginCancel{
    NSLog(@"登录取消");
    [self sendMessageToUnity:"login" code:2 data:@""];
}


#if __IPHONE_OS_VERSION_MAX_ALLOWED < __IPHONE_9_0
- (NSUInteger)supportedInterfaceOrientations
#else
- (UIInterfaceOrientationMask)supportedInterfaceOrientations
#endif
{
    // 横版游戏
    return UIInterfaceOrientationMaskLandscape;
    
    // 竖版游戏
    //        return UIInterfaceOrientationMaskPortrait;
}




//必须添加代码开始....

- (UIInterfaceOrientationMask)application:(UIApplication *)application supportedInterfaceOrientationsForWindow:(UIWindow *)window
{
    return UIInterfaceOrientationMaskAll;
}

//必须添加代码结尾....

@end
