package com.demiframe.game.api.ye.proxy;

import android.app.Activity;

import com.demiframe.game.api.common.LHRole;
import com.demiframe.game.api.connector.IExtend;
import com.demiframe.game.api.connector.IHandleCallback;

import java.util.Date;

import cn.morningtec.yesdk.YeSDK;
import cn.morningtec.yesdk.controllers.entity.RoleInfo;

public class LHExtendProxy
        implements IExtend {
    public void antiAddictionQuery(final Activity paramActivity, IHandleCallback paramIHandleCallback) {
    }

    public void enterBBS(final Activity paramActivity) {
    }

    public void enterUserCenter(final Activity paramActivity) {
    }

    public void realNameRegister(final Activity paramActivity, IHandleCallback paramIHandleCallback) {
    }

    public void submitRoleData(final Activity activity, LHRole lhRole) {
//        Toast.makeText(activity, "扩展数据提交成功", Toast.LENGTH_SHORT).show();
        Date data = new Date();
        long time = Long.parseLong(lhRole.getRoleCTime());
        data.setTime(time);

        RoleInfo roleInfo = YeSDK.getInstance().getRoleInfo();
        roleInfo.setLevel(Integer.parseInt(lhRole.getRoleLevel()));
        roleInfo.setRoleId(lhRole.getRoledId());
        roleInfo.setRoleName(lhRole.getRoleName());
        roleInfo.setServerId(lhRole.getZoneId());
        roleInfo.setServerName(lhRole.getZoneName());
        roleInfo.setCreateTime(data);

        String extendMsg = lhRole.getExtendMsg();
//        LogUtil.d("test");
//        LogUtil.d("submitRoleData:"+extendMsg);
        if( extendMsg.equals("create")){
            YeSDK.getInstance().roleCreate();
        }else if(extendMsg.equals("change")){
            YeSDK.getInstance().roleLevelChange();
        }else if(extendMsg.equals("report")) {
            YeSDK.getInstance().roleReport();
        }
    }

    //提前获取渠道号
    public String getSubChannelId(Activity activity){
        return "yesdk";
    }

    public void GainGameCoin(Activity activity, String jsonStr){

    }

    public void ConsumeGameCoin(Activity activity, String jsonStr){

    }
}