package com.cilugame.h1.patch;

import android.bluetooth.BluetoothClass;
import android.support.annotation.NonNull;
import android.util.Log;

import com.cilugame.android.commons.DeviceUtils;
import com.cilugame.h1.activity.GameLoaderActivity;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

public class GameSettingData {
    public enum UpdateMode
    {
        NoUpdate,
        TestUpdate,
        Update
    }
    public enum TestMode
    {
        DevTest,
        OfficialTest,
        Official,
    }
    public static  String platformName = "android";
    public static  String logTag = "GameSettingData";
    public static  String staticConfigFileName = "staticconfig.txt";
    public static  String gameSettingFileName = "GameSettingDataInAssetV2.txt";
    public String staticConfigUrl;
    public String configFileName;
    public ArrayList<String> cndUrlList = new ArrayList<String>();
    public JSONObject channelVersion = new JSONObject();
    public JSONObject centerServer = new JSONObject();
    public String urlJsonStr;
    public TestMode testMode;
    private static GameSettingData _intance;
    private JSONObject _jsonObj;
    private int _curCndUrlIdx = -1;
    public boolean isInBlackList = false;

    public static GameSettingData getInstance()
    {
        if (_intance == null) {_intance = new GameSettingData();}
        return _intance;
    }

    public  GameSettingData()
    {
        _jsonObj =  GameLoaderActivity.getAssetJsonObject(gameSettingFileName);
        SetTestMode(TestMode.Official);
    }

    public void SetTestMode(TestMode mode)
    {
        testMode = mode;
        boolean isTestVersionConfig = false;
        boolean isTestHttpRoot = false;
        if (testMode == TestMode.DevTest)
        {
            isTestVersionConfig = true;
            isTestHttpRoot = true;
        }
        else if (testMode == TestMode.OfficialTest)
        {
            isTestVersionConfig = true;
            isTestHttpRoot = false;
        }
        String httpRoot = isTestHttpRoot ? GetStringValue(_jsonObj, "testHttpRoot") : GetStringValue(_jsonObj, "httpRoot");
        staticConfigUrl = String.format("%s/%s/%s", httpRoot, GetStringValue(_jsonObj, "resdir"), platformName);
        configFileName = isTestVersionConfig ? "versionConfigTest.json" : "versionConfig.json";
    }

    String GetStringValue(JSONObject jsonObject, String key)
    {
        try
        {
            return jsonObject.getString(key);
        }
        catch (Exception e)
        {
            Log.e(logTag, "GetStringValue:" + key, e);
        }
        return "";
    }
    public void setStaticConfig(String jsonStr)
    {
        JSONObject jsonObject;
        try
        {
            jsonObject = new JSONObject(jsonStr);
        }
        catch (Exception e)
        {
            Log.e(logTag, "setupServerUrlConfig"+jsonStr, e);
            return;
        }
        urlJsonStr = jsonStr;
        String[] keys ={"masterCdnUrl", "slaveCdnUrl", "srcCdnUrl"};
        for(int i=0; i<keys.length; i++)
        {
            String key = keys[i];
            String value = GetStringValue(jsonObject, key);
            if (value != "")
            {
                String resdir = GetStringValue(_jsonObj, "resdir");
                cndUrlList.add(value + "/" + resdir + "/" + platformName);
            }
        }
        channelVersion = jsonObject.optJSONObject("channelVersion");
        centerServer = jsonObject.optJSONObject("centerServer");
    }

    public String getChannelVersion(String channelName) {
        if (channelVersion != null) {
            return channelVersion.optString(channelName);
        }
        return "";
    }

    public String getCenterServer(String centerName) {
        if (centerServer != null) {
            return centerServer.optString(centerName);
        }
        return "";
    }

    public String getOneCdnUrl()
    {
        if ( (_curCndUrlIdx + 1)  <  cndUrlList.size())
        {
            _curCndUrlIdx ++;
            String url = cndUrlList.get(_curCndUrlIdx);
            return url;
        }
        return  null;
    }

    public String getCurCndUrl()
    {
        return cndUrlList.get(_curCndUrlIdx);
    }

    public  UpdateMode GetUpdateMode()
    {
        return  UpdateMode.values()[_jsonObj.optInt("updateMode")];
    }
}
