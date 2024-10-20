package com.demiframe.game.api.lhsdk.proxy;

import android.app.Activity;

import com.demiframe.game.api.common.LHStatusCode;
import com.demiframe.game.api.common.LHUser;
import com.demiframe.game.api.connector.IUserListener;
import com.demiframe.game.api.connector.IUserManager;
import com.demiframe.game.api.util.LogUtil;

public class LHUserManagerProxy
        implements IUserManager {
    Activity mActivity;
    IUserListener mUserListener;

    public void guestRegist(Activity paramActivity, String paramString) {

    }

    public void login(Activity paramActivity, Object paramObject) {
        LogUtil.e("LHUserManagerProxy", "inner login");
    }

    public void loginGuest(Activity paramActivity, Object paramObject) {
    }

    //如果渠道不支持登出，ICheckSupport.IsSupportLogout需要return false
    //并且这里直接执行logout回调，返回成功
    public void logout(Activity paramActivity, Object paramObject) {
        mUserListener.onLogout(LHStatusCode.LH_LOGOUT_FAIL, "No Support Logout");
    }

    public void setUserListener(Activity paramActivity, IUserListener paramIUserListener) {
        mUserListener = paramIUserListener;
    }

    public void switchAccount(Activity paramActivity, Object paramObject) {
    }

    public void updateUserInfo(Activity paramActivity, LHUser paramLHUser) {

    }
}