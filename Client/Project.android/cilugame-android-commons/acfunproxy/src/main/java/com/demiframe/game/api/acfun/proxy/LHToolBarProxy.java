package com.demiframe.game.api.acfun.proxy;

import android.app.Activity;

import com.demiframe.game.api.connector.IToolBar;
import com.joygames.hostlib.JoyGamesSDK;

public class LHToolBarProxy
        implements IToolBar {
    public void hideFloatToolBar(Activity paramActivity) {
        if (JoyGamesSDK.getInstance() != null){
            JoyGamesSDK.getInstance().hideFloat();
        }
    }

    public void showFloatToolBar(Activity paramActivity) {
        if (JoyGamesSDK.getInstance() != null){
            JoyGamesSDK.getInstance().showFloat();
        }
    }
}