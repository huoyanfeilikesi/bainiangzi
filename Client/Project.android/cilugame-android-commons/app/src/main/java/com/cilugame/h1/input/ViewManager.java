package com.cilugame.h1.input;

import android.app.Activity;

import com.cilugame.h1.util.Logger;

public class ViewManager

{
  private Activity context;
  private UnityEditTextDialog editDialog;

  public ViewManager(Activity context)
  {
    this.context = context;
  }

  public void ShowEditDialog(final String text, final UnityEditTextStyle editTextStyle)
  {
//      Logger.Log("left " + editTextStyle.left);
//      Logger.Log("top " + editTextStyle.top);
//      Logger.Log("width " + editTextStyle.width);
//      Logger.Log("height " + editTextStyle.height);
//      Logger.Log("maxLength " + editTextStyle.maxLength);
//      Logger.Log("textSize " + editTextStyle.textSize);
//      Logger.Log("textColor a:" + editTextStyle.textColorA + " r:" + editTextStyle.textColorR + " g:" + editTextStyle.textColorG + " b:" + editTextStyle.textColorB);
//      Logger.Log("inputMode " + editTextStyle.inputMode);
//      Logger.Log("inputFlag " + editTextStyle.inputFlag);
//      Logger.Log("inputReturn " + editTextStyle.inputReturn);

    if(editDialog != null && editDialog.isShowing())
    {
      return;
    }
    context.runOnUiThread(new Runnable()
    {
      @Override
      public void run()
      {
        if(editDialog == null)
        {
          editDialog = new UnityEditTextDialog(context, text, editTextStyle);
        }
        else
        {
          editDialog.SetText(text);
          editDialog.SetEditStyle(editTextStyle);
        }
        editDialog.show();
      }
    });
  }

  public void HideEditDialog()
  {
    if(editDialog == null || !editDialog.isShowing())
    {
      return;
    }
    context.runOnUiThread(new Runnable()
    {
      @Override
      public void run()
      {
        editDialog.dismiss();
      }
    });
  }

  public void SetEditText(final String text)
  {
    if(editDialog == null)
    {
      return;
    }
    context.runOnUiThread(new Runnable()
    {
      @Override
      public void run()
      {
        if(editDialog != null)
        {
          editDialog.SetText(text);
        }
      }
    });
  }

}
