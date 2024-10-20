//
//  SDK910.h
//  SDK910
//
//  Created by SM-JS-LiZhenSheng-PC on 16/3/5.
//  Copyright © 2016年 万精游股份科技有限公司. All rights reserved.
//  Version  1.90


#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>

typedef enum {
    SMPayResultSuccess,         //充值成功
    SMPayResultFailWithDevice,  //设备不允许购买
    SMPayResultFailNOLogin,     //用户没有登录
    SMPayResultFailNOPayProduct,   //请求购买的商品有误
    SMPayResultFailNONetwork,   //网络或者服务器有问题
    SMPayResultFailNOPay,       //有可能是用户没有付款
    SMPayResultFailJailbreak,   //越狱手机不支持充值
    SMPayResultFailInPayQueue,  //该商品还在购买队列中,还没调用finishTransaction
    SMPayResultPaySubmit        //支付已经提交
} SMPayResult;

typedef enum _generic_status_type {
    kStatusInitial = 0,  // or fial
    kStatusTrying,
    kStatusFail,
    kStatusSuccess,
} GenericStatus_t;


// 回调Block类型
typedef void (^generalCompletion_t)();
typedef void (^loginSuccessCompletion_t)(GenericStatus_t loginStatus,NSString *account,NSString *session);
typedef BOOL (^generalCallback_t)();

typedef void (^smPayResult_t)(SMPayResult result,NSString *message);
typedef void (^smPayResultMessage_t)(NSString *message);

@interface SDK910 : NSObject

// 开发者需要设置的参数：
// 请通过initSDK接口来初始化 
//@property (nonatomic, strong) NSString *gameId;
//@property (nonatomic, strong) NSString *packageId;
//@property (nonatomic, strong) NSString *gameServerId;
//@property (nonatomic, strong) generalCompletion_t loginCompletion;
@property (nonatomic, copy) loginSuccessCompletion_t loginCompletion;  // 登陆完成的回调，在回掉函数里面请开发者用checkLoginStatus检查是否登陆状态，不是的话就是登陆失败了。
@property (nonatomic, copy) generalCompletion_t logoutCompletion;  // 退出的回调。
@property (nonatomic, copy) generalCallback_t userRequestToCharge;  // 用户发起充值,返回值SDK暂时不处理

//@property (nonatomic, copy) smPayResult_t smPayResult;  // 充值后回调
@property (nonatomic, copy) smPayResult_t smPayResult;  // 充值后回调
@property (nonatomic, copy) smPayResultMessage_t smPayResultMessage;  // 充值后回调的消息

// 返回SDK对象
+ (SDK910 *)sharedInstance;

/**
 * 初始化, 只要调用一次就好了
 * 建议在AppDelegate里面调用，这个要在sdk所有函数之前调用
 */

- (void)initSDKwithGameId:(NSString *)gameId PakcageId:(NSString *)packageId LoginCompletion:(loginSuccessCompletion_t)loginCompletion LogoutCompletion:(generalCompletion_t)logoutCompletion;

/**
 *   作用：设置 AppScheme
 *  @param appScheme 在info.plist添加的 URL Schemes
 */
- (void) setAppURLScheme:(NSString *)appScheme;
/**
 * 作用： 检查当前是否登陆状态
 * 返回： 是否已经登陆
 */
- (BOOL)checkLoginStatus;

/**
 *  作用：获取当前SDK版本
 */
-(NSString *)currentVersion;

/**
 *  作用：设置切换账号的时候是否关闭登录窗口
 *
 */
-(void) isCloseLoginViewWhenChangeAccount:(BOOL)isClose;

/**
 * 作用： 显示910AppSDK用户中心，浮动在屏幕提供触控
 *       //如果没登陆的话会弹出对话框让用户登陆
 */
- (void)showMemberCenter ;

/**
 *  作用:加载游戏时，显示下面的版号等信息
 *  注意:显示了一定要使用 dissMissTipView 接口 关闭界面
 */
-(void) showTipView ;

/**
 *  作用:关闭版号界面
 */
-(void) dissMissTipView ;

/**
 *  移除苹果支付监听
 */
- (void) removeTransactionObserver;

/**
 *  作用：  发起一次支付
 *  @param serverId    服务器id
 *  @param orderId     CP订单号
 *  @param amount      支付金额
 *  @param productId   商品ID
 *  @param isChecked   提审状态  1，表示提审  0，表示正常
 *  smPayResult 回调block 带有是否支付成功的参数
 */
- (void)startPayWithServerId:(NSString *)serverId OrderId:(NSString *)orderId Amount:(NSNumber *)amount  ProductId:(NSString *)productId isChecked:(int)isChecked payResult:(smPayResult_t)smPayResult;

/**
 *  作用：设置APP禁止文件同步到iCloud
 */
-(void)addNotBackUpiCloud;

- (void)applicationWillResignActive:(UIApplication *)application;

- (void)applicationDidEnterBackground:(UIApplication *)application ;

- (void)applicationWillEnterForeground:(UIApplication *)application;

- (void)applicationDidBecomeActive:(UIApplication *)application;

- (BOOL)application:(UIApplication *)application openURL:(NSURL *)url sourceApplication:(NSString *)sourceApplication annotation:(id)annotation;

-(BOOL)application:(UIApplication *)app openURL:(NSURL *)url options:(NSDictionary *)options;

/**
 *  设置屏幕横竖屏 YES为竖屏 NO为横屏，不设置为横屏
 */
-(void)setOrientationIsPortrait:(BOOL)isPortrait;

/**
 *  检查有那些接口没有接入
 */
-(NSString *) checkLinkSDKIsOK;

// ------------  1.90 修改 添加了角色id、服名字 ----------
/**
 *  创建角色，创建角色时调用
 *  roleName 角色名称
 *  roleId 角色id
 *  roleServerId 区服id
 *  roleServerName 区服名字
 */
-(void)createRoleName:(NSString *)roleName roleId:(NSString *)roleId withRoleServerId:(NSString *)roleServerId roleServerName:(NSString *)roleServerName;

/**
 *  设置角色信息，玩家进入游戏获取到角色信息后设置角色信息
 *  roleServerId 区服id
 *  roleServerName 区服名字
 *  roleId  角色id
 *  roleName 角色名称
 *  roleLevel 角色等级
 *  isCallRoleLevel 是否会在每次角色等级变化都调用 setRoleLevel
 */

-(void)setRoleServerId:(NSString *)roleServerId andRoleServerName:(NSString *)roleServerName andRoleId:(NSString *)roleId andRoleName:(NSString *)roleName andRoleLevel:(NSString *)roleLevel isCallRoleLevel:(BOOL)isCallRoleLevel;

/**
 *  设置角色游戏等级，等级变化时调用
 *  roleLevel 角色等级
 */
-(void)setRoleLevel:(NSString *)roleLevel;

/*
 *  获得游戏币，玩家获得游戏币时调用
 *  coin 获得的游戏币数量
 */
-(void)addGameCoin:(int)coin;

/*
 *  消耗游戏币，玩家消耗游戏币时调用
 *  coin 消耗的游戏币数量
 */
-(void)deleteGameCoin:(int)coin;

// ------------  1.50 添加  end ----------

// ------------  1.70 添加      ----------

/**
 *  切换账号时调用
 *  以便让SDK悬浮框隐藏
 */
-(void) gameLogout;

// ------------  1.70 添加  end ----------



@end
