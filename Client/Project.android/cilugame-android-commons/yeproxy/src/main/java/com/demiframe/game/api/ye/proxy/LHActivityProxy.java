package com.demiframe.game.api.ye.proxy;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;

import com.demiframe.game.api.common.LHCallbackListener;
import com.demiframe.game.api.connector.IActivity;
import com.demiframe.game.api.connector.ICheckSupport;
import com.demiframe.game.api.connector.IInitCallback;
import com.demiframe.game.api.util.LHCheckSupport;
import com.demiframe.game.api.util.LogUtil;
import com.demiframe.game.api.ye.yeutils.SdkEventReceiverImpl;

import cn.morningtec.yesdk.YeSDK;

public class LHActivityProxy
        implements IActivity {
    public LHActivityProxy() {
        LHCheckSupport.setCheckSupport(new ICheckSupport() {
            public boolean isAntiAddictionQuery() {
                return false;
            }

            public boolean isSupportBBS() {
                return false;
            }

            public boolean isSupportLogout() {
                return true;
            }

            public boolean isSupportOfficialPlacard() {
                return true;
            }

            public boolean isSupportShowOrHideToolbar() {
                return false;
            }

            public boolean isSupportUserCenter() {
                return false;
            }
        });
    }

    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent intentData) {
        YeSDK.getInstance().onActivityResult(requestCode, resultCode, intentData);
    }

    public void onCreate(Activity activity, IInitCallback initCallback, Object obj) {
        if(initCallback == null){
            LogUtil.d("initcallback is null");
        }
        LHCallbackListener.getInstance().setInitListener(initCallback);
        LogUtil.d("LHActivityProxy", "YeSDK Init");
        YeSDK.getInstance().setActivity(activity);
        YeSDK.getInstance().onCreate(activity.getIntent().getExtras());
        YeSDK.getInstance().setYeSDKCallback(new SdkEventReceiverImpl());


        try {
            YeSDK.getInstance().init();
            YeSDK.getInstance().setDebug(false);
        }catch (Exception e) {
            e.printStackTrace();
        }
        LogUtil.d("LHActivityProxy", "YeSDK Finish");
    }

    public void afterOnCreate(Activity activity, IInitCallback listener, Object obj){

    }

    public Boolean needAfterCreate(){
        return false;
    }

    public void onDestroy(Activity activity) {
        YeSDK.getInstance().onDestroy();
    }

    public void onNewIntent(Activity activity, Intent intent) {
        YeSDK.getInstance().onNewIntent(intent);
    }

    public void onPause(Activity activity) {
        YeSDK.getInstance().onPause();
    }

    public void onRestart(Activity activity) {
        YeSDK.getInstance().onRestart();
    }

    public void onResume(Activity activity) {
        YeSDK.getInstance().onResume();
    }

    public void onStop(Activity activity) {
        YeSDK.getInstance().onStop();
    }

    public void onStart(Activity activity){
        YeSDK.getInstance().onStart();
    }

    public void onConfigurationChanged(Activity activity, Configuration newConfig){
        YeSDK.getInstance().onConfigurationChanged(newConfig);
    }
}