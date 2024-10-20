package com.cilugame.h1.input;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.cilugame.commons.R;
import com.cilugame.h1.UnityCallbackWrapper;
import com.cilugame.h1.UnityPlayerActivity;
import com.cilugame.h1.util.Logger;
import com.demiframe.game.api.GameApi;


public class UnityEditTextDialog extends Dialog
{

  public static UnityEditTextDialog editBoxDialog;

  /**
   * The user is allowed to enter any text, including line breaks.
   */
  public static final int INPUT_MODE_ANY = 0;

  /**
   * The user is allowed to enter an e-mail address.
   */
  public static final int INPUT_MODE_EMAIL = 1;

  /**
   * The user is allowed to enter an integer value.
   */
  public static final int INPUT_MODE_NUM = 2;

  /**
   * The user is allowed to enter a phone number.
   */
  public static final int INPUT_MODE_PHONE = 3;

  /**
   * The user is allowed to enter a URL.
   */
  public static final int INPUT_MODE_URL = 4;

  /**
   * The user is allowed to enter a real number value.
   */
  public static final int INPUT_MODE_DECIMAL = 5;

  public static final int INPUT_FLAG_DEFAULT = 0;

  /**
   * Indicates that the text entered is confidential data that should be obscured whenever possible. This implies EDIT_BOX_INPUT_FLAG_SENSITIVE.
   */
  public static final int INPUT_FLAG_PASSWORD = 1;

  /**
   * Indicates that the text entered is sensitive data that the implementation must never store into a dictionary or table for use in predictive, auto-completing, or other accelerated input schemes. A credit card number is an example of sensitive data.
   */
  public static final int INPUT_FLAG_SENSITIVE = 2;


  public static final int RETURN_TYPE_DEFAULT = 0;
  public static final int RETURN_TYPE_DONE = 1;
  public static final int RETURN_TYPE_SEND = 2;
  public static final int RETURN_TYPE_SEARCH = 3;
  public static final int RETURN_TYPE_GO = 4;


  // ===========================================================
  // Fields
  // ===========================================================
  private LinearLayout parentView;
  private EditText editText;

  private String text;
  private UnityEditTextStyle editTextStyle;

  private int mInputFlagConstraints;
  private int mInputModeContraints;

  private Handler handler;

  private InputFilter inputFilter;
  private ViewTreeObserver.OnGlobalLayoutListener globalLayoutListener;


  public UnityEditTextDialog(Context context, String text, UnityEditTextStyle editTextStyle)
  {
    super(context, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);

    this.text = text;
    this.editTextStyle = editTextStyle;
  }

  @Override
  protected void onCreate(final Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    //this.getWindow().setBackgroundDrawable(new ColorDrawable(0x80000000));

    parentView = new LinearLayout(getContext());
    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    parentView.setOrientation(LinearLayout.VERTICAL);
    parentView.setGravity(Gravity.BOTTOM);
    editText = new EditText(getContext());
    parentView.addView(editText);

    setContentView(parentView, layoutParams);
    getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

    SetText(this.text);
    SetEditStyle(this.editTextStyle);


        globalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                UnityPlayerActivity.instance.getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
                int screenHeight = UnityPlayerActivity.instance.getWindow().getDecorView().getRootView().getHeight();
                int height = screenHeight - r.bottom;
                if(height > screenHeight / 3)
                {
                    Logger.Log("onGlobalLayout  SoftInput Height = " + height);
                    UnityCallbackWrapper.SendToUnity("OnSoftInputHeight", String.valueOf(height));
                    getWindow().getDecorView().getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            }
        };

        getWindow().getDecorView().getViewTreeObserver().addOnGlobalLayoutListener(globalLayoutListener);

    handler = new Handler(new Handler.Callback()
    {
      @Override
      public boolean handleMessage(Message msg)
      {
        UnityEditTextDialog.this.editText.requestFocus();
        UnityEditTextDialog.this.editText.setSelection(UnityEditTextDialog.this.editText.length());
        UnityEditTextDialog.this.OpenKeyboard();
        return false;
      }
    });

    parentView.setOnClickListener(new View.OnClickListener()
    {
      @Override
      public void onClick(View v)
      {
        UnityEditTextDialog.this.Close();
      }
    });

    editText.addTextChangedListener(new TextWatcher()
    {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after)
      {

      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count)
      {
        UnityCallbackWrapper.SendToUnity("OnInputTextChanged", s.toString());
      }

      @Override
      public void afterTextChanged(Editable s)
      {

      }
    });

      OnKeyListener onKeyListener = new DialogInterface.OnKeyListener()
      {

          @Override
          public boolean onKey(DialogInterface dialogInterface, int keyCode, KeyEvent keyEvent) {
              Logger.Log("onKey " + keyCode + " " +  keyEvent.getAction());
              return  false;
          }
      };

      setOnKeyListener(onKeyListener);

    editText.setOnEditorActionListener(new OnEditorActionListener()
    {
      @Override
      public boolean onEditorAction(final TextView v, final int actionId, final KeyEvent event)
      {
                /* If user didn't set keyboard type, this callback will be invoked twice with 'KeyEvent.ACTION_DOWN' and 'KeyEvent.ACTION_UP'. */
        if(actionId == EditorInfo.IME_ACTION_DONE || (event != null && event.getAction() == KeyEvent.ACTION_DOWN))
        {
          UnityEditTextDialog.this.Close();
          UnityCallbackWrapper.SendToUnity("OnInputReturn");
          return true;
        }
        return false;
      }
    });

    this.setOnShowListener(new OnShowListener()
    {
      @Override
      public void onShow(DialogInterface dialog)
      {
        UnityCallbackWrapper.SendToUnity("OnEditDialogShow");
        handler.sendEmptyMessageDelayed(1, 50);
      }
    });

    this.setOnDismissListener(new OnDismissListener()
    {
      @Override
      public void onDismiss(DialogInterface dialog)
      {
        UnityCallbackWrapper.SendToUnity("OnEditDialogHide");
      }
    });

  }

  public void SetText(String text)
  {
    editText.setText(text);
  }

  public void SetEditStyle(UnityEditTextStyle editTextStyle)
  {
    if(editTextStyle == null)
    {
      return;
    }
    this.editTextStyle = editTextStyle;
    LinearLayout.LayoutParams editTextParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    editTextParams.leftMargin = 0;
    editTextParams.topMargin = 0;
//      editTextParams.bottomMargin = 100;
//    editTextParams.width = ;
    editTextParams.height = 80;
    editText.setLayoutParams(editTextParams);
    editText.setPadding(0, 0, 0, 0);
//    editText.setTypeface(Typeface.DEFAULT_BOLD);
    editText.setTextSize(TypedValue.COMPLEX_UNIT_PX, 50);
    editText.setGravity(editTextStyle.alignment);
      editText.setTextColor(Color.BLACK);
//      editText.setTextColor(Color.argb(editTextStyle.textColorA, editTextStyle.textColorR, editTextStyle.textColorG, editTextStyle.textColorB));
    //editText.setBackgroundColor(0);

    int oldImeOptions = editText.getImeOptions();
    editText.setImeOptions(oldImeOptions | EditorInfo.IME_FLAG_NO_FULLSCREEN);
    oldImeOptions = editText.getImeOptions();

//    switch(editTextStyle.inputMode)
//    {
//      case INPUT_MODE_ANY:
//        mInputModeContraints = InputType.TYPE_CLASS_TEXT;
//        break;
//      case INPUT_MODE_EMAIL:
//        mInputModeContraints = InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS;
//        break;
//      case INPUT_MODE_NUM:
//        mInputModeContraints = InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED;
//        break;
//      case INPUT_MODE_PHONE:
//        mInputModeContraints = InputType.TYPE_CLASS_PHONE;
//        break;
//      case INPUT_MODE_URL:
//        mInputModeContraints = InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_URI;
//        break;
//      case INPUT_MODE_DECIMAL:
//        mInputModeContraints = InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED;
//        break;
//      default:
//        break;
//    }

    editText.setInputType(mInputModeContraints | mInputFlagConstraints);

    switch(editTextStyle.inputFlag)
    {
      case INPUT_FLAG_DEFAULT:
        mInputFlagConstraints = InputType.TYPE_CLASS_TEXT;
        break;
      case INPUT_FLAG_PASSWORD:
        mInputFlagConstraints = InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD;
        break;
      case INPUT_FLAG_SENSITIVE:
        mInputFlagConstraints = InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS;
        break;
      default:
        break;
    }

    this.editText.setInputType(this.mInputFlagConstraints | this.mInputModeContraints);

//    switch(editTextStyle.inputReturn)
//    {
//      case RETURN_TYPE_DEFAULT:
//        editText.setImeOptions(oldImeOptions | EditorInfo.IME_ACTION_NONE);
//        break;
//      case RETURN_TYPE_DONE:
//        editText.setImeOptions(oldImeOptions | EditorInfo.IME_ACTION_DONE);
//        break;
//      case RETURN_TYPE_SEND:
//        editText.setImeOptions(oldImeOptions | EditorInfo.IME_ACTION_SEND);
//        break;
//      case RETURN_TYPE_SEARCH:
//        editText.setImeOptions(oldImeOptions | EditorInfo.IME_ACTION_SEARCH);
//        break;
//      case RETURN_TYPE_GO:
//        editText.setImeOptions(oldImeOptions | EditorInfo.IME_ACTION_GO);
//        break;
//      default:
//        editText.setImeOptions(oldImeOptions | EditorInfo.IME_ACTION_NONE);
//        break;
//    }

  }

  private void OpenKeyboard()
  {
    final InputMethodManager imm = (InputMethodManager)this.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
    imm.showSoftInput(this.editText, 0);
  }

  private void CloseKeyboard()
  {
    final InputMethodManager imm = (InputMethodManager)this.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
    imm.hideSoftInputFromWindow(this.editText.getWindowToken(), 0);
  }

  private void Close()
  {
//      softKeyboardStateHelper.removeSoftKeyboardStateListener(softKeyboardStateListener);
    UnityCallbackWrapper.SendToUnity("OnInputTextChanged", editText.getText().toString());
    CloseKeyboard();
    dismiss();
  }

}
