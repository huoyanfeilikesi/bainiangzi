package com.demiframe.game.api.ye.yeutils;

/**
 * Created by xixi on 2017/6/6.
 */
import com.demiframe.game.api.common.LHCallbackListener;
import com.demiframe.game.api.common.LHResult;
import com.demiframe.game.api.common.LHStatusCode;
import com.demiframe.game.api.common.LHUser;
import com.demiframe.game.api.util.LogUtil;

import org.json.JSONObject;

import cn.morningtec.yesdk.YeSDK;
import cn.morningtec.yesdk.controllers.callback.YeSDKCallback;

public class SdkEventReceiverImpl implements YeSDKCallback{

    @Override
    public void callback(int action, int status, String msg) {
//        LogUtil.d("YeSDK callback action:"+action);
        switch (action) {
            case YeSDKCallback.INIT:
                onInitResult(status, msg);
                break;
            case YeSDKCallback.LOGIN:
                onLoginResult(status, msg);
                break;
            case YeSDKCallback.LOGOUT:
                onLogoutResult(status, msg);
                break;
            case YeSDKCallback.PAY:
                onPayResult(status, msg);
                break;
            case YeSDKCallback.EXIT:
                onExitResult(status, msg);
                break;
            case YeSDKCallback.MORE_GAME:
                onMoreGame(status, msg);
                break;
            case YeSDKCallback.SWITCH_ACCOUNT:
                onSwitchAccoutnResult(status, msg);
                break;
            case YeSDKCallback.SHOW_USERCENTER:
                onShowUserCenterResult(status, msg);
                break;
            case YeSDKCallback.ORDER_CHECK:
                onOrderCheckResult(status, msg);
                break;
            default:
                LogUtil.e("unknown "+msg);
                break;
        }
    }

    public void onInitResult(int status, String msg){
//        LogUtil.d("onInitResult:"+msg+" status:"+status);
        LHResult lhResult = new LHResult();
        switch (status)
        {
            case YeSDKCallback.SUCCESS:
                YeSdkHelper.isInit = true;
                try{
                    String cpsName = YeSDK.getInstance().getAppInfo().getYesdkExtra();
                    int channelId = YeSDK.getInstance().getAppInfo().getYesdkChannelId();
                    JSONObject jObj = new JSONObject();
                    jObj.put("cps", cpsName);
                    jObj.put("yeChannel", channelId);
                    lhResult.setData(jObj.toString());
                }catch(Exception e) {
                    e.printStackTrace();
                }
                lhResult.setCode(LHStatusCode.LH_INIT_SUCCESS);
                break;
            case YeSDKCallback.FAILURE:
                lhResult.setCode(LHStatusCode.LH_INIT_FAIL);
                break;
            case YeSDKCallback.CANCEL:
                lhResult.setCode(LHStatusCode.LH_INIT_FAIL);
                break;
        }
        YeSdkHelper.isRequestLogin = false;
        LogUtil.d("onInitResult:"+ lhResult.getData());
        LHCallbackListener.getInstance().getInitCallback().onFinished(lhResult);
    }

    public void onLoginResult(int status, String msg){
//        LogUtil.d("onLoginResult:"+msg+" status:"+status);
        LHResult lhResult = new LHResult();
        try {
            JSONObject jObj = new JSONObject(msg);
            LHUser lhUser = new LHUser();
            switch (status)
            {
                case YeSDKCallback.SUCCESS:
                    JSONObject user = jObj.getJSONObject("jsonObject");
                    String token = user.getString("loginUnique");
                    String uid = user.getString("platformUid");
//                    String userName = user.getString("platformUserName");
                    String cpsName = YeSDK.getInstance().getAppInfo().getYesdkExtra();
                    int channelId = YeSDK.getInstance().getAppInfo().getYesdkChannelId();
                    lhUser.setSid(token);
                    lhUser.setUid(uid);
                    //将融合渠道的子渠道id返回给客户端用于获取真实渠道名
                    lhUser.setLoginMsg(Integer.toString(channelId)+ "|" + cpsName);
//                    LogUtil.d("login data:"+uid+":"+token);
                    LHCallbackListener.getInstance().getUserListener().onLogin(LHStatusCode.LH_LOGIN_SUCCESS, lhUser);
                    break;
                case YeSDKCallback.FAILURE:
                case YeSDKCallback.CANCEL:
                    if(jObj != null){
                        String errorMsg = jObj.getString("errorMessage");
                        lhUser.setLoginMsg(errorMsg);
                    }else {
                        lhUser.setLoginMsg("unknown");
                    }
                    //返回键会调用这个接口，UC要求不能出现"登录失败"，这里返回为登录取消
                    LHCallbackListener.getInstance().getUserListener().onLogin(LHStatusCode.LH_LOGIN_CANCEL, lhUser);
                    break;
            }
        }catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    public void onLogoutResult(int status, String msg){
        switch (status)
        {
            case YeSDKCallback.SUCCESS:
                LHCallbackListener.getInstance().getUserListener().onLogout(LHStatusCode.LH_LOGOUT_SUCCESS, "onLogoutSucc");
                break;
            case YeSDKCallback.FAILURE:
            case YeSDKCallback.CANCEL:
                LHCallbackListener.getInstance().getUserListener().onLogout(LHStatusCode.LH_LOGOUT_FAIL, "onLogoutFailed");
                break;
        }
    }

    public void onPayResult(int status, String msg){
        LHResult lhResult = new LHResult();
        switch (status)
        {
            case YeSDKCallback.SUCCESS:
                lhResult.setCode(LHStatusCode.LH_PAY_SUCCESS);
                break;
            case YeSDKCallback.FAILURE:
                lhResult.setCode(LHStatusCode.LH_PAY_FAIL);
                break;
            case YeSDKCallback.CANCEL:
                lhResult.setCode(LHStatusCode.LH_PAY_CANCEL);
                break;
        }
        if (lhResult != null) {
            if (LHCallbackListener.getInstance().getPayListener() != null) {
                LHCallbackListener.getInstance().getPayListener().onFinished(lhResult);
            } else {
                LogUtil.d("onPayResult null getPayListener");
            }
        } else {
            LogUtil.d("onPayResult null lhresult");
        }
    }

    public void onExitResult(int status, String msg){
        switch (status)
        {
            case YeSDKCallback.SUCCESS:
                LHCallbackListener.getInstance().getExitListener().onExit(true);
                break;
            case YeSDKCallback.FAILURE:
            case YeSDKCallback.CANCEL:
                LHCallbackListener.getInstance().getExitListener().onExit(false);
        }
    }

    public void onMoreGame(int status, String msg){
        switch (status)
        {
            case YeSDKCallback.SUCCESS:
                LogUtil.d("获取更多游戏成功");
                break;
            case YeSDKCallback.FAILURE:
                LogUtil.d("获取更多游戏失败");
                break;
            case YeSDKCallback.CANCEL:
                LogUtil.d("取消获取更多游戏");
                break;
        }
    }

    public void onSwitchAccoutnResult(int status, String msg){
        LogUtil.d("onSwitchAccoutnResult:"+msg+" status:"+status);
        LHUser lhUser = new LHUser();
        switch (status)
        {
            case YeSDKCallback.SWITCH_ACCOUNT:
                LogUtil.d("切换账户成功");
                LHCallbackListener.getInstance().getUserListener().onSwitchAccount(LHStatusCode.LH_SWITCH_ACCOUNT_SUCCESS, lhUser);
                break;
            case YeSDKCallback.FAILURE:
                LogUtil.d("切换账户失败");
                LHCallbackListener.getInstance().getUserListener().onSwitchAccount(LHStatusCode.LH_SWITCH_ACCOUNT_FAIL, lhUser);
                break;
            case YeSDKCallback.CANCEL:
                LogUtil.d("取消切换账户");
                LHCallbackListener.getInstance().getUserListener().onSwitchAccount(LHStatusCode.LH_SWITCH_ACCOUNT_CANCEL, lhUser);
                break;
        }
    }

    public void onShowUserCenterResult(int status, String msg){
        switch (status)
        {
            case YeSDKCallback.SUCCESS:
                LogUtil.d("显示用户中心");
                break;
            case YeSDKCallback.FAILURE:
                LogUtil.d("显示用户中心失败");
                break;
            case YeSDKCallback.CANCEL:
                LogUtil.d("取消显示用户中心");
                break;
        }
    }

    public void onOrderCheckResult(int status, String msg){
        switch (status)
        {
            case YeSDKCallback.SUCCESS:
                LogUtil.d("订单查询成功");
                break;
            case YeSDKCallback.FAILURE:
                LogUtil.d("订单查询失败");
                break;
            case YeSDKCallback.CANCEL:
                LogUtil.d("订单查询状态无法确认");
                break;
        }
    }
}
