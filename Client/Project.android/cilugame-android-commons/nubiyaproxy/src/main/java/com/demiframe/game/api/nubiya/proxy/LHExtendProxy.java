package com.demiframe.game.api.nubiya.proxy;

import android.app.Activity;
import android.widget.Toast;

import com.demiframe.game.api.common.LHRole;
import com.demiframe.game.api.connector.IExtend;
import com.demiframe.game.api.connector.IHandleCallback;

public class LHExtendProxy
        implements IExtend {
    public void antiAddictionQuery(final Activity paramActivity, IHandleCallback paramIHandleCallback) {
    }

    public void enterBBS(final Activity paramActivity) {
    }

    public void enterUserCenter(final Activity paramActivity) {
    }

    public void realNameRegister(final Activity paramActivity, IHandleCallback paramIHandleCallback) {
    }

    public void submitRoleData(final Activity activity, LHRole paramLHRole) {
    }

    //提前获取渠道号
    public String getSubChannelId(Activity activity){
        return "nubia";
    }

    public void GainGameCoin(Activity activity, String jsonStr){

    }

    public void ConsumeGameCoin(Activity activity, String jsonStr){

    }
}