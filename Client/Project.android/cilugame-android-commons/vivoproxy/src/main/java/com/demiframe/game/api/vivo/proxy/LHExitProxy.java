package com.demiframe.game.api.vivo.proxy;

import android.app.Activity;

import com.demiframe.game.api.common.IInnerExitCallback;
import com.demiframe.game.api.common.LHInnerExitDialog;
import com.demiframe.game.api.connector.IExit;
import com.demiframe.game.api.connector.IExitCallback;
import com.demiframe.game.api.connector.IExitSdk;
import com.demiframe.game.api.util.LogUtil;
import com.vivo.unionsdk.open.VivoExitCallback;
import com.vivo.unionsdk.open.VivoUnionSDK;

public class LHExitProxy
        implements IExit, IExitSdk {
    public void onExit(Activity paramActivity, final IExitCallback paramIExitCallback) {
        VivoUnionSDK.exit(paramActivity, new VivoExitCallback(){
            @Override
            public void onExitCancel(){
                paramIExitCallback.onExit(false);
            }
            @Override
            public void onExitConfirm(){
                paramIExitCallback.onExit(true);
            }
        });
    }

    public void onExitSdk() {

    }
}