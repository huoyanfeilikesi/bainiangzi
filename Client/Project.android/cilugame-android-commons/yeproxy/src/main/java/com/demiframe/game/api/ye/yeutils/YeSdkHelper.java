package com.demiframe.game.api.ye.yeutils;

import cn.morningtec.yesdk.YeSDK;

/**
 * Created by CL-PC007 on 2017/8/2.
 */

public class YeSdkHelper {
    public static boolean isInit = false;
    public static boolean isRequestLogin = false;
    public static YeSdkHelper instance = null;

    public static YeSdkHelper GetInstance(){
        if(instance == null){
            instance = new YeSdkHelper();
        }
        return instance;
    }

    public int GetYesdkChannelId(){
        return YeSDK.getInstance().getAppInfo().getYesdkChannelId();
    }

    public static String GetExtraMeta() {
        return YeSDK.getInstance().getAppInfo().getYesdkExtra();
    }

    public static int GetChannelIdMeta() {
        YeSdkHelper helper = YeSdkHelper.GetInstance();
        return helper.GetYesdkChannelId();
    }
}
