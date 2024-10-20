//
//  SpSdkController.m
//  Unity-iPhone
//
//

#import "ShoumengSdkController.h"
#import <SM910APP/SM910APP.h>

IMPL_APP_CONTROLLER_SUBCLASS (ShoumengSdkController)

extern void UnitySendMessage(const char *, const char *, const char *);

@implementation ShoumengSdkController
{
}

//通用代码开始......

//游戏初始化完成自动调用
- (BOOL)application:(UIApplication*)application didFinishLaunchingWithOptions:(NSDictionary*)launchOptions
{
    [super application:application didFinishLaunchingWithOptions:launchOptions];
     NSLog(@"SpSdkController didFinishLaunchingWithOptions");
    
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(DoApplicationDidEnterBackground:) name:UIApplicationDidEnterBackgroundNotification object:nil]; //监听ApplicationDidEnterBackground.
    
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(DoApplicationWillEnterForeground:) name:UIApplicationWillEnterForegroundNotification object:nil]; //监听ApplicationWillEnterForeground.
    
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(DoApplicationDidBecomeActive:) name:UIApplicationDidBecomeActiveNotification object:nil]; //监听是否ApplicationDidBecomeActive.
    
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(DoApplicationWillResignActive:) name:UIApplicationWillResignActiveNotification object:nil]; //监听ApplicationWillResignActive.
    
    //这里可增加一些SDK要求加入到这个过程的处理
    [[SDK910 sharedInstance] showTipView];
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

//初始化
-(void)initsdk
{
    NSLog(@"初始化 init");
    //一般要在这里增加回调监听
        // 显示版号
    
    NSString * gameId = @"1603";
    NSString * packageId = @"16020099";
    
    NSLog(@"gameId:%@     packageId:%@",gameId,packageId);
    
    [[SDK910 sharedInstance] initSDKwithGameId:gameId PakcageId:packageId LoginCompletion:^(GenericStatus_t loginStatus, NSString *account, NSString *session) {
        
        if (loginStatus==kStatusSuccess) {
            
            NSLog(@"登陆成功:  account是:%@     sessionid是:%@",account,session);
            
            NSString *combieSession=[NSString stringWithFormat:@"%@DEMISPLITER%@",session,account];
            
            NSMutableDictionary *dictionary = [[NSMutableDictionary alloc]init];
            [dictionary setValue:combieSession forKey:@"sessionId"];
            [dictionary setValue:account forKey:@"uid"];


           if([NSJSONSerialization isValidJSONObject:dictionary]){  
               NSLog(@"it is a JSONObject!");  
           }  
           //use dataWithJSONObject fun  
             
           NSError *error = nil;  
           NSData *jsonData = [NSJSONSerialization dataWithJSONObject:dictionary options:NSJSONWritingPrettyPrinted error:&error];  
           if([jsonData length] > 0 && error == nil) {  
               NSString *jsonString = [[NSString alloc]initWithData:jsonData encoding:NSUTF8StringEncoding];  
               NSLog(@"data:%@",jsonString);  
            [self sendMessageToUnity:"login" code:0 data:jsonString];
           }       

        }else{
            NSLog(@"其它登出");
        }
    } LogoutCompletion:^{
        NSLog(@"登出");
        [self sendMessageToUnity:"logout" code:0 data:@""];
    }];
    
    // [[SDK910 sharedInstance] setAppURLScheme:@"com.vxiyu.inapppay.demo"];
    [[SDK910 sharedInstance] addNotBackUpiCloud];
    [[SDK910 sharedInstance] dissMissTipView];
    
    [self sendMessageToUnity:"init" code:0 data:@""];
}

//注销
- (void)logout
{
    NSLog(@"注销 logout");
    [[SDK910 sharedInstance] gameLogout]; // 注销
    [self sendMessageToUnity:"logout" code:0 data:@""];
}

//绑定
- (void)bindAccount
{
    NSLog(@"绑定 bindAccount");
}


//登陆
- (void)login
{
    NSLog(@"登陆 login");
    [[SDK910 sharedInstance] showMemberCenter]; // 显示登陆界面，用户登陆成功后会回调到上面的 1
}

//帐号管理
- (void)userCenter
{
    NSLog(@"帐号管理 userCenter");
    [[SDK910 sharedInstance] showMemberCenter]; // 显示登陆界面
}


/**
 *支付
 * json 支付json
 */
- (void) pay:(NSString *) json
{
    NSLog(@"支付 json=%@", json);

   NSData *data = [json dataUsingEncoding:NSUTF8StringEncoding];  
   id jsonObject = [NSJSONSerialization JSONObjectWithData:data options:NSJSONReadingMutableContainers error:nil];
   if ([jsonObject isKindOfClass:[NSDictionary class]]) {  
       NSDictionary *jsonDictionary = (NSDictionary*)jsonObject;  

        #warning 请传商品ID与对应正确的金额
        #warning  isChecked 提审状态 1为提审状态 0为正常状态,如果能按照审核状态和正式上线状态来设置，请传相应的值。如果不能请传 0
        [[SDK910 sharedInstance] startPayWithServerId:[jsonDictionary valueForKey:@"serverId"] OrderId:[jsonDictionary valueForKey:@"appOrderId"] Amount:[jsonDictionary valueForKey:@"productPrice"] ProductId:[jsonDictionary valueForKey:@"productId"] isChecked:0 payResult:^(SMPayResult result, NSString *message) {
            if (result==SMPayResultSuccess) {
                NSLog(@"支付成功:%@",message);
                [self sendMessageToUnity:"pay" code:0 data:@""];
            }else{
                NSLog(@"支付失败:%@",message);
                [self sendMessageToUnity:"pay" code:2 data:@""];
            }
        }];
   }
}

#warning 1.30版本及以后的请添加下面两个

// 在XCode6 中如果nullable NSString *这种写法不支持，请换成 NSString *
- (BOOL)application:(UIApplication *)application openURL:(NSURL *)url sourceApplication:(nullable NSString *)sourceApplication annotation:(id)annotation {
    // ios 8 调用这里
    [[SDK910 sharedInstance] application:application openURL:url sourceApplication:sourceApplication annotation:annotation];
    return true;
}

// 在XCode6中 (NSDictionary<NSString*, id> *)options 不支持这种写法 请改成(NSDictionary *)options
- (BOOL)application:(UIApplication *)app openURL:(NSURL *)url options:(NSDictionary *)options{
    // ios 9 调用这里
    [[SDK910 sharedInstance] application:app openURL:url options:options];
    return true;
}



#warning 1.42以后请加下面4个
- (void) DoApplicationDidEnterBackground:(UIApplication *)application {
    NSLog(@"DoApplicationDidEnterBackground");
    NSLog(@"%s",__func__);
    [[SDK910 sharedInstance] applicationDidEnterBackground:application];
}

- (void) DoApplicationWillEnterForeground:(UIApplication *)application {
    NSLog(@"DoApplicationWillEnterForeground");
    NSLog(@"%s",__func__);
    [[SDK910 sharedInstance] applicationWillEnterForeground:application];
}

-(void) DoApplicationDidBecomeActive:(UIApplication *)application {
    NSLog(@"DoApplicationDidBecomeActive");
    NSLog(@"%s",__func__);
    [[SDK910 sharedInstance] applicationDidBecomeActive:application];
}

- (void) DoApplicationWillResignActive:(UIApplication *)application {
    NSLog(@"DoApplicationWillResignActive");
    NSLog(@"%s",__func__);
    [[SDK910 sharedInstance] applicationWillResignActive:application];
}

 
#if !__has_feature(objc_arc)
-(void)dealloc
{
    [super dealloc];
}
#endif

//各种回调结束......



//必须添加代码开始....

- (UIInterfaceOrientationMask)application:(UIApplication *)application supportedInterfaceOrientationsForWindow:(UIWindow *)window
{
    return UIInterfaceOrientationMaskAll;
}

//必须添加代码结尾....

@end
