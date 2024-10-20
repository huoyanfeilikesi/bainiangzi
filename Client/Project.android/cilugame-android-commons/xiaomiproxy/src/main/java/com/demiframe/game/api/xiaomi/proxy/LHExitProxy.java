package com.demiframe.game.api.xiaomi.proxy;

import android.app.Activity;

import com.demiframe.game.api.common.LHCallbackListener;
import com.demiframe.game.api.connector.IExit;
import com.demiframe.game.api.connector.IExitCallback;
import com.demiframe.game.api.connector.IExitSdk;
import com.demiframe.game.api.util.LogUtil;
import com.xiaomi.gamecenter.sdk.MiCommplatform;
import com.xiaomi.gamecenter.sdk.OnExitListner;

public class LHExitProxy implements IExit, IExitSdk
{
    public void onExit(Activity paramActivity, final IExitCallback paramIExitCallback)
    {
        LogUtil.d("小米退出测试onExit START");
        MiCommplatform.getInstance().miAppExit(paramActivity, new OnExitListner() {
            @Override
            public void onExit(int i)
            {
                LogUtil.d("小米退出测试onExit CALL BACK TO XIAOMI API,CODE IS " + i);
                switch(i)
                {
                    case 10001:
                        paramIExitCallback.onExit(true);
                        LogUtil.d("小米退出测试onExit，case 10001");
                        break;
                    case -1:
                        paramIExitCallback.onExit(false);
                        LogUtil.d("小米退出测试onExit，case 1");
                        break;
                    default:
                        paramIExitCallback.onExit(false);
                        LogUtil.d("小米退出测试onExit，default case " + i);
                        break;
                }
            }
        });
    }

    public void onExitSdk() {

    }
}