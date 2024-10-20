package com.demiframe.game.api.demi.proxy;

import android.app.Activity;

import com.demiframe.game.api.common.LHUser;
import com.demiframe.game.api.connector.IUserListener;
import com.demiframe.game.api.connector.IUserManager;
import com.demiframe.game.api.util.LogUtil;

public class LHUserManagerProxy
  implements IUserManager
{
  Activity mActivity;
  IUserListener mUserListener;

  public void guestRegist(Activity paramActivity, String paramString)
  {

  }

  public void login(Activity paramActivity, Object paramObject)
  {
    LogUtil.e("LHUserManagerProxy", "inner login");
  }

  public void loginGuest(Activity paramActivity, Object paramObject)
  {
  }

  public void logout(Activity paramActivity, Object paramObject)
  {
    LogUtil.e("LHUserManagerProxy", "inner logout");
  }

  public void setUserListener(Activity paramActivity, IUserListener paramIUserListener)
  {

  }

  public void switchAccount(Activity paramActivity, Object paramObject)
  {
  }

  public void updateUserInfo(Activity paramActivity, LHUser paramLHUser)
  {

  }
}