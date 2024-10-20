package com.demiframe.game.api.huawei.proxy;

import android.app.Activity;

import com.demiframe.game.api.connector.IToolBar;
import com.huawei.gameservice.sdk.GameServiceSDK;

public class LHToolBarProxy
        implements IToolBar {
    public void hideFloatToolBar(Activity paramActivity) {
        GameServiceSDK.hideFloatWindow(paramActivity);
    }

    public void showFloatToolBar(Activity paramActivity) {
        GameServiceSDK.hideFloatWindow(paramActivity);
    }
}