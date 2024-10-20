package com.demiframe.game.api.ye.proxy;

import android.app.Activity;

import com.demiframe.game.api.common.LHCallbackListener;
import com.demiframe.game.api.common.LHPayInfo;
import com.demiframe.game.api.connector.IPay;
import com.demiframe.game.api.util.LogUtil;

import org.json.JSONObject;

import cn.morningtec.yesdk.YeSDK;
import cn.morningtec.yesdk.controllers.entity.PayInfo;

public class LHPayProxy
        implements IPay {

    public void onPay(final Activity activity, final LHPayInfo lhPayInfo) {
        LHCallbackListener.getInstance().setPayListener(lhPayInfo.getPayCallback());
        LogUtil.e("LHPayProxy", "onPay");
        PayInfo payInfo = new PayInfo();
        payInfo.setGameOrderId(lhPayInfo.getOrderSerial());
        payInfo.setProductName(lhPayInfo.getProductName());
        payInfo.setProductId(lhPayInfo.getProductId());
        payInfo.setProductCount(Integer.parseInt(lhPayInfo.getProductCount()));
        payInfo.setProductDesc(lhPayInfo.getProductDes());
        payInfo.setGameNotifyUrl(lhPayInfo.getPayNotifyUrl());
        try {
            JSONObject extraJson = new JSONObject(lhPayInfo.getExtraJson());
//            payInfo.setYesdkOrderId(extraJson.getString("yesdkOrderId"));
            payInfo.setGameOrderExtend(extraJson.getString("gameOrderExtend"));
            payInfo.setGameOrderDesc(extraJson.getString("gameOrderDesc"));
            payInfo.setProductExtend(extraJson.getString("productExtend"));
        }catch (Exception e){
            e.printStackTrace();
            return;
        }
        YeSDK.getInstance().pay(payInfo);
    }
}