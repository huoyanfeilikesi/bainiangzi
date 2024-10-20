package com.demiframe.game.api.activity;

/**
 * Created by xianjian on 2017/2/28.
 */


import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout.LayoutParams;
import java.util.ArrayList;
import java.util.List;

public class SplashActivity extends Activity {
    private FrameLayout frameLayout;
    private ImageView imageView;
    private boolean inAnimation = false;
    private RelativeLayout layout;
    private List resources = new ArrayList();

    public SplashActivity() {
    }

    private Animation getAnimation() {
        AlphaAnimation var1;
        (var1 = new AlphaAnimation(0.0F, 1.0F)).setInterpolator(new DecelerateInterpolator());
        var1.setDuration(500L);
        AlphaAnimation var2;
        (var2 = new AlphaAnimation(1.0F, 0.0F)).setInterpolator(new AccelerateInterpolator());
        var2.setStartOffset(1500L);
        var2.setDuration(500L);
        AnimationSet var3;
        (var3 = new AnimationSet(false)).addAnimation(var1);
        var3.addAnimation(var2);
        return var3;
    }

    private void startAnimationFrom(final int var1) {
        if(this.resources.size() > var1) {
            if(!this.inAnimation) {
                this.inAnimation = true;
                this.imageView.setImageResource((Integer) this.resources.get(var1));
                Animation var2;
                var2 = this.getAnimation();
                var2.setAnimationListener(new Animation.AnimationListener()
                {
                    public void onAnimationStart(Animation ani){

                    }

                    public void onAnimationEnd(Animation ani){
                        SplashActivity.this.layout.setVisibility(ImageView.INVISIBLE);
                        SplashActivity.this.inAnimation = false;
                        SplashActivity.this.startAnimationFrom(var1+1);
                    }

                    public void onAnimationRepeat(Animation ani){

                    }
                });

                this.layout.startAnimation(var2);
                this.layout.setVisibility(ImageView.VISIBLE);
            }
        } else {
            this.onSplashStop();
        }
    }

    protected void onCreate(Bundle var1) {
        super.onCreate(var1);
        this.requestWindowFeature(1);
//        this.getWindow().setFlags(1024, 1024);
        int var3 = 0;

        while(true) {
            int var2 = this.getResources().getIdentifier("demi_define_splash_image_" + var3, "drawable", this.getPackageName());
            ++var3;
            if(var2 == 0) {
                this.frameLayout = new FrameLayout(this);
                this.frameLayout.setBackgroundColor(this.getBackgroundColor());
                this.layout = new RelativeLayout(this);
                this.layout.setBackgroundColor(this.getBackgroundColor());
                this.layout.setVisibility(ImageView.INVISIBLE);
                LayoutParams var4 = new LayoutParams(-1, -1);
                this.imageView = new ImageView(this);
                this.imageView.setScaleType(ScaleType.CENTER_CROP);
                LayoutParams var5 = new LayoutParams(-1, -1);
                this.layout.addView(this.imageView, var5);
                android.widget.FrameLayout.LayoutParams var6 = new android.widget.FrameLayout.LayoutParams(-1, -1);
                this.frameLayout.addView(this.layout, var6);
                this.setContentView(this.frameLayout, var4);
                return;
            }

            this.resources.add(var2);
        }
    }

    protected void onResume() {
        super.onResume();
        this.startAnimationFrom(0);
    }

    public void onSplashStop(){
        try{
            //启动主activity,打包工具替换名字
            Class mainCls = Class.forName("{DEMI_DEFINE_MAIN_ACTIVITY}");
            Intent intent = new Intent(this, mainCls);
            startActivity(intent);
            finish();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public int getBackgroundColor(){
        return Color.WHITE;
    }
}

