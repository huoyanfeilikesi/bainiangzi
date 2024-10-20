package com.demiframe.game.api.lhsdk.proxy;

import android.app.Activity;

import com.demiframe.game.api.connector.IExit;
import com.demiframe.game.api.connector.IExitCallback;
import com.demiframe.game.api.connector.IExitSdk;
import com.demiframe.game.api.util.LogUtil;

public class LHExitProxy
        implements IExit, IExitSdk {
    public void onExit(Activity paramActivity, IExitCallback paramIExitCallback) {
        if (paramIExitCallback == null)
            LogUtil.e("LHExitProxy", "退出监听回调<IExitCallback>为空");
        paramIExitCallback.onNoExiterProvide(this);
    }

    public void onExitSdk() {

    }
}