package com.demiframe.game.api.shunwang.proxy;

import android.app.Activity;

import com.demiframe.game.api.connector.IToolBar;
import com.shunwang.sdk.game.SWGameSDK;

public class LHToolBarProxy
        implements IToolBar {
    public void hideFloatToolBar(Activity paramActivity) {
        SWGameSDK.getInstance().hideFloatingView(paramActivity);
    }

    public void showFloatToolBar(Activity paramActivity) {
        SWGameSDK.getInstance().showFloatingView(paramActivity);
    }
}