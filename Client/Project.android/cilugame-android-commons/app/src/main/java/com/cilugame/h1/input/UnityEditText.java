package com.cilugame.h1.input;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import com.cilugame.h1.util.Logger;

public class UnityEditText extends EditText
{
    public interface OnKeyPreImeListener
    {
        boolean onKeyPreIme(int keyCode, KeyEvent event);
    }

    public OnKeyPreImeListener onKeyPreImeListener = null;

    public void SetOnClickListener(OnKeyPreImeListener call)
    {
        onKeyPreImeListener = call;
    }

    public UnityEditText(Context context) {
        super(context);
    }

    public UnityEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public UnityEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if(onKeyPreImeListener != null)
        {
            return onKeyPreImeListener.onKeyPreIme(keyCode, event);
        }
        return super.onKeyPreIme(keyCode, event);

//        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == 1)
//        {
//            Logger.Log("键盘向下 ");
//            if(onKeyPreImeListener != null)
//            {
//                onKeyPreImeListener.onKeyPreIme(keyCode, event);
//            }
//            super.onKeyPreIme(keyCode, event);
//            return false;
//        }
//        return super.onKeyPreIme(keyCode, event);
    }
}