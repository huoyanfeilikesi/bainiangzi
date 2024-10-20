package com.demiframe.game.api.ye.proxy;

import android.app.Activity;

import com.demiframe.game.api.common.LHCallbackListener;
import com.demiframe.game.api.common.LHUser;
import com.demiframe.game.api.connector.IUserListener;
import com.demiframe.game.api.connector.IUserManager;
import com.demiframe.game.api.util.LogUtil;

import cn.morningtec.yesdk.YeSDK;

public class LHUserManagerProxy
        implements IUserManager {
    Activity mActivity;

    public void guestRegist(Activity paramActivity, String paramString) {

    }

    public void login(Activity paramActivity, Object paramObject) {
//        if(!YeSdkHelper.isInit){
//            try {
//                LogUtil.e("LHUserManagerProxy", "yesdk reinit");
//                YeSDK.getInstance().init();
//                YeSdkHelper.isRequestLogin = true;
//            }catch (Exception e) {
//                e.printStackTrace();
//            }
//        }else{
            LogUtil.e("LHUserManagerProxy", "yesdk login");
            YeSDK.getInstance().login();
//        }


    }

    public void loginGuest(Activity paramActivity, Object paramObject) {
    }

    //如果渠道不支持登出，ICheckSupport.IsSupportLogout需要return false
    //并且这里直接执行logout回调，返回成功
    public void logout(Activity paramActivity, Object paramObject) {
        YeSDK.getInstance().logout();
    }

    public void setUserListener(Activity paramActivity, IUserListener paramIUserListener) {
        LHCallbackListener.getInstance().setUserListener(paramIUserListener);
    }

    public void switchAccount(Activity paramActivity, Object paramObject) {
        LogUtil.e("LHUserManagerProxy", "yesdk switchAccount");
        YeSDK.getInstance().switchAccount();
    }

    public void updateUserInfo(Activity paramActivity, LHUser paramLHUser) {

    }
}