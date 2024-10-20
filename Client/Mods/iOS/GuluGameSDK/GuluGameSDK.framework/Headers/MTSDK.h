//
//  MTSDK.h
//  MTSDK
//
//  Created by  czk on 17/4/12.
//  Copyright © 2017年 czk. All rights reserved.
//

#import <Foundation/Foundation.h>
@class MTPayModel;

@protocol MTSDKDelegate <NSObject>
@required

/**
 初始化完成时回调
 
 @param code 0:初始化成功 /9999:服务器端初始化失败 /-1:客户端网络请求失败
 */
- (void)onInited:(NSUInteger)code;

/**
 登录成功完成后的回调，失败SDK会提示错误信息，不会回调
 
 @param loginlk 登录成功的lk
 */
- (void)onLogin:(NSString *)loginlk;

/**
 退出登录回调
 */
- (void)onLoginout;

@end

@interface MTSDK : NSObject

@property (nonatomic, weak  )  id<MTSDKDelegate> delegate;

/**
 是否正在审核，游戏方赋值
 */
@property (nonatomic, assign) BOOL isCheck;

/**
 获取悬浮窗的状态
 */
@property (nonatomic, assign, readonly) BOOL isFloatButtonShow;

/**
 悬浮窗是否需要显示，服务器控制
 */
@property (nonatomic, assign, readonly) BOOL isNeedFloatBtnShow;
/**
 是否是游客登录
 */
@property (nonatomic, assign, readonly) BOOL isGuestLogin;
/**
 获取登录状态
 */
@property (nonatomic, assign, readonly) BOOL isLogined;

/**
 获取单例
 */
+(instancetype)INSTANCE;

/**
 初始化SDK
 
 @param appId 从sdk服务器获得
 @param appKey 从sdk服务器获得
 @param appChannel App渠道号，appchannel 之前如果接过GuluSDK_iOS旧版本，就传之前传过的参数，例如210 或 appstore等。如果是第一次接入GuluSDK_iOS，请传空字符
 @param gameVersion 游戏版本，游戏自定义
 @param delegate SDK接口登陆回调
 */
+(void)initWidthAppId:(NSString *)appId
               appKey:(NSString *)appKey
           appChannel:(NSString *)appChannel
          gameVersion:(NSString *)gameVersion
             delegate:(id<MTSDKDelegate>)delegate;

/**
 登录
 */
+(void)show;

/**
 显示/隐藏 悬浮窗（如果SDK服务器控制浮窗不显示（isNeedFloatBtnShow 为NO）,游戏设置浮窗显示无作用）
 
 @param visable YES为显示，NO为隐藏
 */
+(void)setFloatButtonVisable:(BOOL)visable;




/**
 登出
 */
+(void)loginOut;


/**
 支付
 
 @param payModel 商品参数模型
 */
+ (void)pay:(MTPayModel *)payModel;


@end
