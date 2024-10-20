package com.demiframe.game.api.vivo.proxy;

import android.app.Activity;

import com.demiframe.game.api.common.LHCallbackListener;
import com.demiframe.game.api.common.LHStatusCode;
import com.demiframe.game.api.common.LHUser;
import com.demiframe.game.api.connector.IUserListener;
import com.demiframe.game.api.connector.IUserManager;
import com.demiframe.game.api.util.IOUtil;
import com.demiframe.game.api.util.LogUtil;

import com.demiframe.game.api.util.SDKTools;
import com.demiframe.game.api.vivo.base.User;
import com.vivo.unionsdk.open.VivoAccountCallback;
import com.vivo.unionsdk.open.VivoUnionSDK;


public class LHUserManagerProxy implements IUserManager {
    public void guestRegist(Activity paramActivity, String paramString) {

    }

    public void login(Activity paramActivity, Object paramObject)
    {
        VivoUnionSDK.login(paramActivity);
    }

    public void loginGuest(Activity paramActivity, Object paramObject) {
    }

    public void logout(Activity paramActivity, Object paramObject){
        LogUtil.e("LHUserManagerProxy", "vivo inner logout");
        LHCallbackListener.getInstance().getUserListener().onLogout(LHStatusCode.LH_LOGOUT_FAIL,paramObject);
    }

    public void setUserListener(Activity paramActivity, IUserListener paramIUserListener)
    {
        LHCallbackListener.getInstance().setUserListener(paramIUserListener);
    }

    public void switchAccount(Activity paramActivity, Object paramObject) {
    }

    public void updateUserInfo(Activity paramActivity, LHUser paramLHUser) {

    }

    public static VivoAccountCallback accountListener = new VivoAccountCallback(){
        @Override
        public void onVivoAccountLogin(String name, String openid, String authtoken){
            LogUtil.d("---------- OnVivoAccountChangedListener onAccountLogin ----------");
            LogUtil.d("authtoken = " + authtoken);
            LogUtil.d("openid = " + openid);
            //缓存玩家信息
            User.getInstances().setOpenId(openid);
            User.getInstances().setAuthtoken(authtoken);
            User.getInstances().setName(name);

            LHUser lhUser = new LHUser();
            lhUser.setUid(openid);
            lhUser.setSid(authtoken);
            LHCallbackListener.getInstance().getUserListener().onLogin(LHStatusCode.LH_LOGIN_SUCCESS, lhUser);
        }

        @Override
        public void onVivoAccountLogout(int requestCode){
            LHUser lhUser = new LHUser();
            LogUtil.d("onVivoAccountLogout code="+requestCode);
            if(requestCode == 0)
            {
                User.getInstances().Clear();
                lhUser.setLoginMsg("登出操作成功");
                LHCallbackListener.getInstance().getUserListener().onLogin(LHStatusCode.LH_LOGOUT_SUCCESS, lhUser);
                User.getInstances().Clear();
            }
            else{
                lhUser.setLoginMsg("登出操作失败");
                LHCallbackListener.getInstance().getUserListener().onLogin(LHStatusCode.LH_LOGOUT_FAIL, lhUser);
            }
        }

        @Override
        public void onVivoAccountLoginCancel(){
            LogUtil.d("---------- OnVivoAccountChangedListener onAccountLoginCancled ----------");
            LHUser lhUser = new LHUser();
            lhUser.setLoginMsg("取消登录操作");
            LHCallbackListener.getInstance().getUserListener().onLogin(LHStatusCode.LH_LOGIN_FAIL, lhUser);
        }
    };
}