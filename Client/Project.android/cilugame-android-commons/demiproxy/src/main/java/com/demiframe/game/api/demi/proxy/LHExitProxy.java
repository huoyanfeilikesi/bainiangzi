package com.demiframe.game.api.demi.proxy;

import android.app.Activity;

import com.demiframe.game.api.common.IInnerExitCallback;
import com.demiframe.game.api.common.LHInnerExitDialog;
import com.demiframe.game.api.connector.IExit;
import com.demiframe.game.api.connector.IExitCallback;
import com.demiframe.game.api.connector.IExitSdk;
import com.demiframe.game.api.util.LogUtil;

public class LHExitProxy
  implements IExit, IExitSdk
{
  public void onExit(Activity paramActivity, final IExitCallback paramIExitCallback)
  {
    LHInnerExitDialog.exit(paramActivity, new IInnerExitCallback() {
      @Override
      public void onSuccess() {
        LHExitProxy.this.onExitSdk();
        paramIExitCallback.onExit(true);

      }

      @Override
      public void onCancel() {
        paramIExitCallback.onExit(false);
      }
    });
  }

  public void onExitSdk()
  {

  }
}