#import "XinGeIOSPlugin.h"
#import "XGPush.h"
#import "XGSetting.h"

NSString * const cUnityGameObjectName = @"PlatformAPI";
NSString * const cUnityMethodCallBack = @"OnSdkCallback";

extern void UnitySendMessage(const char*,const char*,const char*);

extern "C"
{
    void FormatToJson(NSString *pType,NSString *pData){
        NSDictionary *tDic = [NSDictionary dictionaryWithObjectsAndKeys:
                              pType,@"type",
                              @"0",@"code",
                              pData,@"data",nil];
        
        NSData *tData = [NSJSONSerialization dataWithJSONObject:tDic options:NSJSONWritingPrettyPrinted error:nil];
        NSString *tJson = [[NSString alloc] initWithData:tData encoding:NSUTF8StringEncoding];
        
        UnitySendMessage([cUnityGameObjectName UTF8String], [cUnityMethodCallBack UTF8String], [tJson UTF8String]);
    }
    
    void XGDebugEnable(const BOOL pLog)
    {
        [[XGSetting getInstance] enableDebug:pLog];
        FormatToJson(@"XGDebugEnable", [NSString stringWithFormat:@"%d",pLog]);
    }
    
    void XGRegisterPush(const char* pAccessID,const char* pAccessKey){
        
        NSString *tAccessID = [[NSString alloc] initWithCString:pAccessID encoding:NSASCIIStringEncoding];
        NSString *tAccessKey = [[NSString alloc] initWithCString:pAccessKey encoding:NSASCIIStringEncoding];
        
        [XGPush startApp:[tAccessID intValue] appKey:tAccessKey];
        
        FormatToJson(@"XGRegisterPush",[NSString stringWithFormat:@"accessId=%@,accessKey=%@",tAccessID,tAccessKey]);
    }
    
    void XGisUnRegisterStatus(){
        BOOL tResult = [XGPush isUnRegisterStatus];
        FormatToJson(@"XGisUnRegisterStatus", [NSString stringWithFormat:@"%d",tResult]);
    }
    
    void XGRegisterDevice(){
        if(mDeviceToken == NULL){
            FormatToJson(@"OnXGRegisterResult", @"1");
            return;
        }
        
        NSString *tDeviceToken = [XGPush registerDevice:mDeviceToken account:nil successCallback:^{
            FormatToJson(@"OnXGRegisterResult", @"0");
        } errorCallback:^{
            FormatToJson(@"OnXGRegisterResult", @"1");
        }];
    }
    
    void XGRegisterDeviceWithAccount(const char* pAccountName){
        
        if(mDeviceToken == NULL){
            FormatToJson(@"XGRegisterDeviceWithAccountError", @"1");
            return;
        }
        
        NSString *tDeviceToken = [XGPush registerDevice:mDeviceToken account:
                                  [[NSString alloc] initWithCString:pAccountName encoding:NSUTF8StringEncoding] successCallback:^{
                                      FormatToJson(@"XGRegisterWithAccountResult", @"0");
                                  } errorCallback:^{
                                      FormatToJson(@"XGRegisterWithAccountResult", @"1");
                                  }];
    }
    
    void XGUnRegisterDevice(){
        [XGPush unRegisterDevice:^{
            FormatToJson(@"XGUnRegisterDevice", @"XGUnRegisterDevice success");
        }errorCallback:^{
            FormatToJson(@"XGUnRegisterDevice", @"XGUnRegisterDevice error");
        }];
    }
    
    void XGSetTag(const char* pTag){
        [XGPush setTag:[[NSString alloc] initWithCString:pTag encoding:NSUTF8StringEncoding] successCallback:^{
            FormatToJson(@"XGSetTag", @"XGSetTag success");
        } errorCallback:^{
            FormatToJson(@"XGSetTag", @"XGSetTag error");
        }];
    }
    
    void XGDelTag(const char* pTag){
        [XGPush delTag:[[NSString alloc] initWithCString:pTag encoding:NSUTF8StringEncoding] successCallback:
         ^{
             FormatToJson(@"XGDelTag", @"XGDelTag success");
         }
         errorCallback:^{
             FormatToJson(@"XGDelTag", @"XGDelTag error");
         }];
    }
    
    void XGSetAccount(const char* pAccountName){
        [XGPush setAccount:[[NSString alloc] initWithCString:pAccountName encoding:NSUTF8StringEncoding] successCallback:^{
            FormatToJson(@"XGSetAccount", @"XGSetAccount success");
        }errorCallback: ^{
            FormatToJson(@"XGSetAccount", @"XGSetAccount error");
        }];
    }
    
    //删除帐号前要先调用一次XGRegisterDevice方法
    void XGDelAccount(){
        [XGPush delAccount:^{
            FormatToJson(@"XGDelAccount", @"XGDelAccount success");
        } errorCallback:^{
            FormatToJson(@"XGDelAccount", @"XGDelAccount error");
        }];
    }
}

@implementation XinGeIOSPlugin : NSObject

+(void)SetDeviceToken:(NSData*)token{
    mDeviceToken = token;
    FormatToJson(@"didRegisterForRemoteNotificationsWithDeviceToken", @"get token success");
}

+(void)SenMessageToUnity:(nonnull NSString *)pMethod Message:(nonnull NSString *)pMessage
{
    FormatToJson(pMethod, pMessage);
}
@end

