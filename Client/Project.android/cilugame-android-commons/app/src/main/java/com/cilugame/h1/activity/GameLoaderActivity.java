package com.cilugame.h1.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cilugame.android.commons.AssetsUtils;
import com.cilugame.android.commons.DeviceUtils;
import com.cilugame.android.commons.HttpUtils;
import com.cilugame.h1.UnityPlayerActivity;
import com.cilugame.h1.patch.DLLDownloadTask;
import com.cilugame.h1.patch.DLLDownloadTip;
import com.cilugame.android.commons.IOUtils;
import com.cilugame.h1.patch.FileInfo;
import com.cilugame.h1.patch.GameSettingData;
import com.cilugame.h1.util.Logger;
import com.cilugame.h1.util.Util;

import org.json.JSONObject;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GameLoaderActivity extends Activity {
    public static final String TAG = "GameLoaderActivity";
    public static final String SPLASH_PATH = "bin/Data/splash.png";
    public static final String FILE_ENCODING = "UTF-8";
    public static final String CDN_DLL_DIR = "dll";
    public static final String DLL_VERSION_FILENAME = "dllVersion.json";
    public static final String METADATA_GAME_ACTIVITY_CLASS_NAME = "gameActivityClass";
    public JSONObject localDllVersionInfo;
    public JSONObject remoteDllVersionInfo;
    public JSONObject remoteVersionConfig;
    public TextView loadingTips;
    public ProgressBar progressBar;

    private boolean forceUpdate = true;
    private DLLDownloadTask _downloadTask;
    private boolean isAlwaysShowSplash = true;//特殊处理，是否处理平滑过渡更新闪屏->游戏闪屏。（手盟有某些判断，不能做此优化）
    private static Activity _instance;

    private Handler _handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            //staticconfig.txt
            if(DLLDownloadTip.MSG_STATIC_COMFIG_DOWNLOAD == msg.what)
            {
                onLoadStaticConfigFinish((String)msg.obj);
            }
            //versionConfig.json
            else if(DLLDownloadTip.MSG_VERSION_DOWNLOAD == msg.what)
            {
                onFetchVersionConfigDown((String)msg.obj);
            }
            //dllVerion.json
            else if(DLLDownloadTip.MSG_DLLVERSION_DOWNLOAD == msg.what)
            {
                onGetRemoteDllVersion((String)msg.obj);
            }
            //更新失败
            else if(DLLDownloadTip.ERR_DLL_DOWNLOAD_FAIL == msg.what) {
                dllDownloadRetry();
            }
            //更新完成
            else if(DLLDownloadTip.MSG_DLL_DOWNLOAD_FINISH == msg.what){
                Logger.Log("程序更新成功");
                startGameActivity(true);
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        _instance = this;

        final String appPackageName = getApplicationContext().getPackageName();
        final int rLayoutId = getResources().getIdentifier("cilu_activity_game_loader", "layout", appPackageName);
        final int rGameLoaderLayoutId = getResources().getIdentifier("cilu_gameLoaderLayout", "id", appPackageName);
        final int rTxtLoadTip = getResources().getIdentifier("cilu_game_loader_txtLoadTip", "id", appPackageName);
        final int rProgressbarLoader = getResources().getIdentifier("cilu_game_loader_pbLoader", "id", appPackageName);
        setContentView(rLayoutId);

        View view = findViewById(rGameLoaderLayoutId);
        view.setBackgroundColor(Color.BLACK);

        loadingTips = (TextView) findViewById(rTxtLoadTip);
        loadingTips.setTextColor(Color.WHITE);

        progressBar = (ProgressBar) findViewById(rProgressbarLoader);
        progressBar.setVisibility(View.INVISIBLE);

        isAlwaysShowSplash = IsAlwaysShowSplash();

        //显示闪屏
        if(isAlwaysShowSplash){
            ShowSplash();
        }
        //初始化本地版本文件
        checkoutDll();
        // 检查网络设置,检查更新
        GameSettingData.UpdateMode mode = GameSettingData.getInstance().GetUpdateMode();
        if (mode  == GameSettingData.UpdateMode.NoUpdate)
        {
            startGameActivity(false);
        }
        else if(mode  == GameSettingData.UpdateMode.TestUpdate)
        {
            chooseUpdateMode();
        }
        else if(mode  == GameSettingData.UpdateMode.Update)
        {
            chekcUpdate();
        }
    }

    private void ShowSplash(){
        final String appPackageName = getApplicationContext().getPackageName();
        final int rGameLoaderLayoutId = getResources().getIdentifier("cilu_gameLoaderLayout", "id", appPackageName);

        View view = findViewById(rGameLoaderLayoutId);
        Drawable bgDrawable = null;
        try {
            bgDrawable = Drawable.createFromStream(getAssets().open(SPLASH_PATH), null);
        } catch (IOException e) {
            Log.w(TAG, "set loader background error:", e);
        }
        if (bgDrawable != null) {
            if (Build.VERSION.SDK_INT >= 16)
                view.setBackground(bgDrawable);
            else
                view.setBackgroundDrawable(bgDrawable);
        } else {
            view.setBackgroundColor(Color.BLACK);
        }

        if(loadingTips != null)
            loadingTips.setTextColor(Color.BLACK);
    }

    private boolean IsAlwaysShowSplash(){
        String value = "";
        try {
            ApplicationInfo appInfo = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            value = appInfo.metaData.get("DemiFrameChannelId").toString();
            return value.equals("demi");
        } catch (Exception e) {
            Log.d(TAG, "DemiFrameChannelId read error");
        }
        return false;
    }


    private void startGameActivity(boolean patchUpdated) {
        try {
            if (remoteDllVersionInfo != null) {
                IOUtils.writeStringToFile(getDllVersionFile(), remoteDllVersionInfo.toString(), FILE_ENCODING);
            }
            if (remoteVersionConfig != null)
            {
                String versionConfigPath = GetExternalPersistencePath() + "/versionConfig.json";
                IOUtils.writeStringToFile(new File(versionConfigPath), remoteVersionConfig.toString(), FILE_ENCODING);
            }
            JSONObject saveJsonObj = new JSONObject();
            saveJsonObj.put("cndUrls", GameSettingData.getInstance().urlJsonStr);
            if (GameSettingData.getInstance().testMode != null)
            {
                saveJsonObj.put("testMode", GameSettingData.getInstance().testMode.ordinal());
            }
            IOUtils.writeStringToFile(getUpdateConnfigSaveFile(), saveJsonObj.toString(), FILE_ENCODING);
            Log.d(TAG, "startGameActivity save remote version file success:" + getUpdateConnfigSaveFile().exists());
        } catch (Exception e) {
            Log.e(TAG, "startGameActivity save remote version file error:", e);
        }
        patchUpdated = true;
        if(patchUpdated)
        {
            Logger.Log("正在进入游戏");
            this.progressBar.setVisibility(View.INVISIBLE);
        }

        String startGameActivityName = null;
        try {
            ActivityInfo activityInfo = getPackageManager().getActivityInfo(this.getComponentName(), PackageManager.GET_ACTIVITIES | PackageManager.GET_META_DATA);
            startGameActivityName = activityInfo.metaData.getString(METADATA_GAME_ACTIVITY_CLASS_NAME);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "get metadata error: ", e);
        }

        if (startGameActivityName != null) {
            try {
                Intent oldIntent = getIntent();
                Log.i(TAG, "src intent: " + oldIntent);
                final Class<?> activityClass = Class.forName(startGameActivityName);
                Intent gameIntent = new Intent(this, activityClass);
                gameIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                gameIntent.putExtra("patchUpdated", patchUpdated);
                gameIntent.putExtras(oldIntent);

                if(!isAlwaysShowSplash){
                    GameLoaderActivity.FinishActivity();
                }
                startActivity(gameIntent);
                overridePendingTransition(0, 0);

                if(isAlwaysShowSplash){
                    GameLoaderActivity.FinishActivity();
                }
            } catch (ClassNotFoundException e) {
                Log.e(TAG, "Class not found: " + startGameActivityName, e);
            }
        } else {
            Toast.makeText(this, "无法进入游戏,请设置游戏页面类", Toast.LENGTH_LONG).show();
            Log.e(TAG, "get null game activity class name!");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public static void FinishActivity() {
        if (_instance != null) {
            _instance.finish();
            _instance = null;
        }
    }

    private void cleanupFilesFolder(){
        File filesFile = new File(GetExternalPersistencePath());
        if (filesFile.exists())
        {
            Util.deleteDir(filesFile);
        }
        Log.d(TAG, "cleanupFilesFolder"+filesFile.exists()+filesFile.getPath());
    }

    private void cleanupDllFolder() {
        File dllsFile = getDllsFile();
        if (dllsFile.exists())
        {
            Util.deleteDir(dllsFile);
        }
        Log.d(TAG, "cleanupDllFolder"+dllsFile.exists()+dllsFile.getPath());
    }

    public static JSONObject getAssetJsonObject(String path)
    {
        try
        {
            String strJson = IOUtils.toString(UnityPlayerActivity.instance.getAssets().open(path), FILE_ENCODING);
            return new JSONObject(strJson);
        }
        catch (Exception e)
        {
            Log.e(TAG, "getAssetJsonObject:"+path, e);
        }
        return null;
    }
    private File getUpdateConnfigSaveFile()
    {
        String versionConfigPath = GetExternalPersistencePath() + "/andriodUpdateInfo.json";
        return  new File(versionConfigPath);
    }

    private  File getDllVersionFile()
    {
        String dllVersionPath = GetExternalPersistencePath() + "/dllVersion.json";
        return  new File(dllVersionPath);
    }

    public String GetInternalPersistencePath() {
        return  DeviceUtils.GetInternalPersistencePathWithContext(this);
    }

    public String GetExternalPersistencePath() {
        return  DeviceUtils.GetExternalPersistencePathWithContext(this);
    }

    private boolean checkoutDll() {
        Logger.Log("检查包外DllVersion");
        File dllVersionFile = getDllVersionFile();
        if (dllVersionFile.exists()) {
            try {
                String jsonStr = IOUtils.readFileToString(dllVersionFile, FILE_ENCODING);
                localDllVersionInfo = new JSONObject(jsonStr);
            } catch (Exception e) {
                Log.e(TAG, "read local dllVersion.json error: ", e);
            }
        }
        final JSONObject assetDllVersionInfo = getAssetJsonObject(DLL_VERSION_FILENAME);
        final long localDllVersion = localDllVersionInfo == null ? 0 : localDllVersionInfo.optLong("Version");
        final long assetDllVersion = assetDllVersionInfo == null ? 0 : assetDllVersionInfo.optLong("Version");

        Log.d(TAG, String.format("checkoutDll localDllVersionInfo %d, assetDllVersion:%d",  localDllVersion ,assetDllVersion));
        //包内dll较新，清空包外dll资源
        if (localDllVersion < assetDllVersion) {
            localDllVersionInfo = assetDllVersionInfo;
            cleanupDllFolder();
        }
        return true;
    }

    //在包外Internal目录
    public File getDllsFile() {
        return  new File(GetInternalPersistencePath() + "/dlls");
    }

    private void chooseUpdateMode() {
        final String[] btns = new String[] {
                "内网_测试更新",
                "外网_测试更新",
                "外网_正式更新",
                "删除已下载补丁",
        };
        final Activity opener = this;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setSingleChoiceItems(btns, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "chooseUpdateMode onClick:"+which);
                        dialog.dismiss();
                        if (which == 3)
                        {
                            cleanupDllFolder();
                            cleanupFilesFolder();
                            opener.finish();
                            System.exit(0);
                        }
                        else
                        {
                            GameSettingData.getInstance().SetTestMode(GameSettingData.TestMode.values()[which]);
                            chekcUpdate();
                        }
                    }
                });
        Log.d(TAG, "chooseUpdateMode:");
        builder.setTitle("请选择操作");
        builder.setCancelable(false);
        builder.create().show();
    }

    private void chekcUpdate() {
        if (!HttpUtils.isNetworkAvailable(this)) {
            RetryDlg("网络异常", "当前网络不可用，请检查你的网络设置",
                "重试", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            chekcUpdate();
                        }
                    });
        } else {
            loadStaticConfig();
        }
    }

    //1.获取staticConfig.json
    private void loadStaticConfig()
    {
        Log.i(TAG, "1.获取更新地址中...");
        GameSettingData gameSetting =  GameSettingData.getInstance();
        final String url = String.format("%s/servers/%s?ver=%d", gameSetting.staticConfigUrl, gameSetting.staticConfigFileName, System.currentTimeMillis());
        Thread remoteThread = new Thread(new Runnable() {
            @Override
            public void run() {
                String strJson = HttpUtils.get(url, 10000);
                Message msg = new Message();
                msg.what = DLLDownloadTip.MSG_STATIC_COMFIG_DOWNLOAD;
                msg.obj = strJson;
                _handler.sendMessage(msg);
            }
        });
        remoteThread.start();
    }
    private void onLoadStaticConfigFinish(String jsonStr)
    {
        if(jsonStr == null || jsonStr.isEmpty()){
            RetryDlg("更新地址获取失败", "是否重试",
                    "重试", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            chekcUpdate();
                        }
                    });
            return;
        }
        File saveFile= new File(getExternalCacheDir(), GameSettingData.staticConfigFileName);
        try {
            IOUtils.writeStringToFile(saveFile, jsonStr, FILE_ENCODING);
        } catch (IOException e) {
            Log.e(TAG, "onLoadStaticConfigFinish:"+saveFile.getPath(), e);
        }
        GameSettingData.getInstance().setStaticConfig(jsonStr);
        fetchVersionConfig();
    }

    //2.获取versionConfig.json
    void fetchVersionConfig()
    {
        Log.i(TAG, "2.获取版本信息中...");
        GameSettingData gameSetting =  GameSettingData.getInstance();
        String cndUrl = gameSetting.getOneCdnUrl();
        if (cndUrl == null)
        {
            Log.e(TAG, "fetchVersionConfig url null");
            return;
        }
        final String versionConfigUrl = String.format("%s/%s?ver=%d", cndUrl, gameSetting.configFileName, System.currentTimeMillis());
        Thread remoteThread = new Thread(new Runnable() {
            @Override
            public void run() {
                String strJson = HttpUtils.get(versionConfigUrl, 10000);
                Message msg = new Message();
                msg.what = DLLDownloadTip.MSG_VERSION_DOWNLOAD;
                msg.obj = strJson;
                _handler.sendMessage(msg);
            }
        });
        remoteThread.start();
    }

    void onFetchVersionConfigDown(String strJson)
    {
        if(strJson == null || strJson.isEmpty()){
            //下载失败，换一个cdn服务器继续
            fetchVersionConfig();
            return;
        }
        try {
            remoteVersionConfig = new JSONObject(strJson);
        }
        catch (Exception e)
        {
            //解析失败，换一个cdn服务器继续
            fetchVersionConfig();
            Log.e(TAG, "onFetchVersionConfigDown:"+strJson, e);
            return;
        }
        long localDllVersion = localDllVersionInfo == null ? 0 : localDllVersionInfo.optLong("Version");
        long remoteDllVersion = remoteVersionConfig.optLong("dllVersion");
        forceUpdate = remoteVersionConfig.optBoolean("forceUpdate");

        String channelName = "";
        try {
            ApplicationInfo appInfo = _instance.getPackageManager().getApplicationInfo(_instance.getPackageName(), PackageManager.GET_META_DATA);
            channelName = appInfo.metaData.get("YESDK_CHANNEL_ID").toString();
            Log.i(TAG, "getMetaData value="+channelName);
        } catch (Exception e) {
            Log.e(TAG, "getMetaData Exception", e);
        }
        String remoteVersion = GameSettingData.getInstance().getChannelVersion(channelName);
        String localVersion = DeviceUtils.GetVersionName(_instance);
        Log.i(TAG, "版本CurApp|Remote:" + localVersion + " | " + remoteVersion );

        Boolean isDownLoadDll = localDllVersion < remoteDllVersion;
        if (localVersion.equals(remoteVersion)) {
            isDownLoadDll = false;
        }

        if (isDownLoadDll)
		{
            getRemoteDllVersion();
        }
		else
		{
            //不用更新Dll，进入游戏
            startGameActivity(false);
        }
    }

    //3.获取dllVersion.json
    void getRemoteDllVersion()
    {
        Log.i(TAG, "3.获取dll信息中...");
        long remoteDllVersion = remoteVersionConfig.optLong("dllVersion");
        final String dllCndUrl = String.format("%s/%s/dllVersion_%d.json", GameSettingData.getInstance().getCurCndUrl(), CDN_DLL_DIR, remoteDllVersion);
        Log.i(TAG, "getRemoteDllVersion:"+dllCndUrl);
        Thread remoteThread = new Thread(new Runnable() {
            @Override
            public void run() {
                String strJson = HttpUtils.get(dllCndUrl, 10000);
                Message msg = new Message();
                msg.what = DLLDownloadTip.MSG_DLLVERSION_DOWNLOAD;
                msg.obj = strJson;
                _handler.sendMessage(msg);
            }
        });
        remoteThread.start();
    }

    void onGetRemoteDllVersion(String strJson)
    {
        try
        {
            remoteDllVersionInfo = new JSONObject(strJson);
        }
        catch (Exception e)
        {
            //解析dllVersion失败，提示重试
            cdnDllVersionRetry();
            return;
        }
        final JSONObject filesJson = remoteDllVersionInfo.optJSONObject("Manifest");
        if(filesJson == null || filesJson.length() <= 0){
            Log.i(TAG, "onGetRemoteDllVersion: update dll list is empty");
            startGameActivity(false);
            return;
        }
        //显示闪屏
        if(!isAlwaysShowSplash){
            ShowSplash();
        }

        final List<FileInfo> downloadDllUrls = new ArrayList<FileInfo>();
        final Iterator<String> itKeys = filesJson.keys();
        long totalSize = 0;
        while (itKeys.hasNext()) {
            String sKey = itKeys.next();
            JSONObject remoteFileInfo = filesJson.optJSONObject(sKey);
            String fileName = remoteFileInfo.optString("dllName");
            File file = new File(getDllsFile(), fileName+".dll");
            String localMD5 = IOUtils.md5Hex(file); // localFileInfo.optString("md5");
            String remoteMD5 = remoteFileInfo.optString("MD5");
            if (!remoteMD5.equalsIgnoreCase(localMD5)) {
                String strURL = GameSettingData.getInstance().getCurCndUrl() + "/"+ CDN_DLL_DIR+ "/" + fileName + "_" + remoteMD5 + ".dll";
                FileInfo fileInfo = new FileInfo(fileName,strURL, file, remoteMD5);
                downloadDllUrls.add(fileInfo);
                int fileSize = remoteFileInfo.optInt("size");
                totalSize += fileSize;
            }
        }

        if (downloadDllUrls.isEmpty()) {
            Log.i(TAG, "onGetRemoteDllVersion: downloadDllUrls isEmpty ");
            startGameActivity(false);
            return;
        }
        Log.i(TAG, "onGetRemoteDllVersion:need download files: " + downloadDllUrls);
        checkFreeSpaces(downloadDllUrls, totalSize, true);
    }

    private void checkFreeSpaces(List<FileInfo> downloadDllUrls, long totalSize, boolean zip) {
        // 检查剩余空间大小
        float freeSpaceInBytes = IOUtils.freeBytes(Environment.getDataDirectory());
        if(freeSpaceInBytes < totalSize) {
            // 空间不足
            Log.i(TAG, "剩余空间不足:" + freeSpaceInBytes + ", " + IOUtils.byteCountToDisplaySize(freeSpaceInBytes));
            showFreeSpaceNotEnough(downloadDllUrls, totalSize, zip);
        } else {
            updateDlls(downloadDllUrls, totalSize, zip);
        }
    }

    private void showFreeSpaceNotEnough(final List<FileInfo> downloadDllUrls, final long totalSize, final boolean zip) {
        final Activity opener = this;
        RetryDlg("空间不足", "您的设备剩余空间不足, 下载文件大小: " + IOUtils.byteCountToDisplaySize(totalSize), "重试", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                checkFreeSpaces(downloadDllUrls, totalSize, zip);
            }
        });
    }

    //4.下载dlls
    protected void updateDlls(final List<FileInfo> downloadDllUrls, long totalSize, final boolean zip) {
        StringBuilder tip = new StringBuilder("需要更新" + downloadDllUrls.size() + "个文件");
        if (totalSize > 0) {
            tip.append(", 大小: " + IOUtils.byteCountToDisplaySize(totalSize));
        }
        progressBar.setMax((int) totalSize);
        progressBar.setProgress(0);
        progressBar.setVisibility(View.VISIBLE);

        //wifi环境下直接更新
        if(DeviceUtils.getNetworkType() == DeviceUtils.NETWORK_TYPE_WIFI){
            loadingTips.setText(tip);
            File dllsFile =  getDllsFile();
            if(!dllsFile.exists()) { dllsFile.mkdirs(); }
            _downloadTask = new DLLDownloadTask(this, _handler, progressBar, downloadDllUrls, getDllsFile(), zip);
            _downloadTask.execute();
            return;
        }

        tip.append("，建议WIFI环境下载");
        loadingTips.setText(tip);

        final Activity opener = this;
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(tip);
        builder.setTitle("确认下载");
        builder.setPositiveButton("好!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                File dllsFile =  getDllsFile();
                if(!dllsFile.exists()) { dllsFile.mkdirs(); }
                _downloadTask = new DLLDownloadTask(opener, _handler, progressBar, downloadDllUrls, getDllsFile(), zip);
                _downloadTask .execute();
            }
        });
        String cancelButtonName = forceUpdate ? "稍后再玩" : "暂不下载";
        builder.setNegativeButton(cancelButtonName, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (forceUpdate) {
                    opener.finish();
                } else {
                    // goto game activity!
                    startGameActivity(false);
                }
            }
        });
        builder.setOnKeyListener(new DialogInterface.OnKeyListener() {
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                // 不能用返回键取消
                return false;
            }
        });
        builder.setCancelable(false);
        builder.create().show();
    }

    private AlertDialog.Builder RetryDlg(String title, String msg, String confirm, DialogInterface.OnClickListener confirmListener)
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setCancelable(false);
        final Activity opener = this;
        builder.setPositiveButton(confirm, confirmListener);
        builder.setNegativeButton("稍后再玩", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                opener.finish();
                System.exit(0);
            }});
        builder.create().show();
        return  builder;
    }

    private void cdnDllVersionRetry(){
        final Activity opener = this;
        RetryDlg("版本检查失败", "版本文件下载失败,请检查网络或者重试",
                "重试", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        getRemoteDllVersion();
                    }});
    }

    private void dllDownloadRetry() {
        final Activity opener = this;
        RetryDlg("下载失败", "文件下载失败,请检查网络设置或者重试",
                "重试", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    if(_downloadTask != null) {
                        _downloadTask = _downloadTask.clone();
                    }
                    _downloadTask.execute();
                }});
    }
}
