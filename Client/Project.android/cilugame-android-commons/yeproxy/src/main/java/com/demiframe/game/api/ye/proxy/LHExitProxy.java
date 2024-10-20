package com.demiframe.game.api.ye.proxy;

import android.app.Activity;

import com.demiframe.game.api.common.LHCallbackListener;
import com.demiframe.game.api.connector.IExit;
import com.demiframe.game.api.connector.IExitCallback;
import com.demiframe.game.api.connector.IExitSdk;

import cn.morningtec.yesdk.YeSDK;

public class LHExitProxy
        implements IExit, IExitSdk {
    public void onExit(Activity paramActivity, IExitCallback paramIExitCallback) {
        LHCallbackListener.getInstance().setExitListener(paramIExitCallback);
        YeSDK.getInstance().exit();
    }

    public void onExitSdk() {

    }
}