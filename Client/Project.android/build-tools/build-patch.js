var ByteArray = Java.type("byte[]");
var Arrays = Java.type("java.util.Arrays")
var Integer = java.lang.Integer;
var String = java.lang.String;
var StringBuilder = java.lang.StringBuilder;
var File = java.io.File;
var Files = java.nio.file.Files;
var Paths = java.nio.file.Paths;
var FileInputStream = java.io.FileInputStream;
var StandardCopyOption = java.nio.file.StandardCopyOption;

var MessageDigest = java.security.MessageDigest;
var DigestInputStream = java.security.DigestInputStream;

var messageDigest;
function md5Hex(src) {
  if(!messageDigest) {
    messageDigest = MessageDigest.getInstance("MD5");
  }
  messageDigest.reset();
    var fis = new FileInputStream(src);
    var dis = new DigestInputStream(fis, messageDigest);
    var readBufferSize = 8 * 1024;
    var buf = new ByteArray(readBufferSize);
    while (dis.read(buf, 0, readBufferSize) != -1) {
        ;
    }
    dis.close();
    fis.close();
    fis = null;
    // byte[]
    var fileDigest = messageDigest.digest();
    var checksumSb = new StringBuilder();
    for (var i = 0; i < fileDigest.length; i++) {
        var hexStr = Integer.toHexString(0x00ff & fileDigest[i]);
        if (hexStr.length() < 2) {
            checksumSb.append("0");
        }
        checksumSb.append(hexStr);
    }
    return checksumSb.toString();
}

function replaceActivityContains(content, activityName) {
  var loaderIndex = content.indexOf(activityName);
  if(loaderIndex>0) {
    var strBegin = content.substring(0,loaderIndex);
    var strEnd = content.substring(loaderIndex);
    var temp = strBegin.substring(strBegin.lastIndexOf('<activity'), loaderIndex) + 
                strEnd.substring(0, strEnd.indexOf('</activity>') + '</activity>'.length());
    return content.replace(temp, '');
  }
  return content;
}

function replaceLoaderActivity(manifestFile, manifestContent) {
  var loaderClassName = 'com.cilugame.h1.activity.GameLoaderActivity';
  var gameClassName = 'com.cilugame.h1.UnityPlayerActivity';
  var strLoaderActivity = [
  '<activity android:name="'+loaderClassName+'" android:configChanges="orientation" android:screenOrientation="sensorLandscape" android:theme="@android:style/Theme.NoTitleBar.Fullscreen" >\n',
    '<meta-data android:name="gameActivityClass" android:value="'+gameClassName+'" />\n',
  '</activity>\n',
  '<activity android:configChanges="locale|mcc|mnc|touchscreen|keyboard|keyboardHidden|navigation|orientation|screenLayout|uiMode|screenSize|smallestScreenSize|fontScale" android:label="@string/app_name" android:launchMode="standard" android:name="'+gameClassName+'" android:screenOrientation="sensorLandscape" android:windowSoftInputMode="adjustPan">\n',
  '    <meta-data android:name="unityplayer.UnityActivity" android:value="true"/>\n',
  '<intent-filter>\n',
  '    <action android:name="android.intent.action.MAIN" />\n',
  '    <category android:name="android.intent.category.LAUNCHER" />\n',
  '</intent-filter>\n',  
  '</activity>'
  ].join('');
  
  var removeActivityContent = manifestContent;
  removeActivityContent = replaceActivityContains(removeActivityContent, loaderClassName);
  removeActivityContent = replaceActivityContains(removeActivityContent, gameClassName); 
  
  var lines = removeActivityContent.split('\n');
  var newContent = '';
  for(var i=0; i<lines.length; i++) {
    var line = lines[i];
    if(line.indexOf('<application') >= 0) {
      newContent += line;
      newContent += '\n';
      newContent += strLoaderActivity;
    } else {
      newContent += line;
    }
    newContent += '\n';
  }
  Files.write(manifestFile.toPath(), Arrays.asList(newContent));
  print("renew android manifest: " + manifestFile);
}

var copyFileNames = [
  "Assembly-CSharp-firstpass.dll",
  "Assembly-CSharp.dll",
  "Assembly-UnityScript.dll",
  "H1Model.dll",
  "H1ModelSerializer.dll",
  "AppModel.dll"
];

//LocalDev    内开发
//LocalTest   内测试 
//LocalRelease   仿真正式
//LocalForever   仿真永测
//Release     正式服
//BetaTest    永测服
//Business    商务服
//Tencent    腾讯服
var profiles = {
  //Xlsj
  Xlsj_LocalDev : {
    remoteVersion : "http://nocdn.dev.h5.cilugame.com/h5/localdev/android/dlls/",
    downloadURLPrefix : "http://nocdn.dev.h5.cilugame.com/h5/localdev/android/dlls/"
  },
  Xlsj_LocalTest : {
    remoteVersion : "http://localdev84.h1.cilugame.com/h1/localtest/android/dlls/",
    downloadURLPrefix : "http://localdev84.h1.cilugame.com/h1/localtest/android/dlls/"
  },
  Xlsj_LocalRelease : {
    remoteVersion : "http://localdev84.h1.cilugame.com/h1/localrelease/android/dlls/",
    downloadURLPrefix : "http://localdev84.h1.cilugame.com/h1/localrelease/android/dlls/"
  },
  Xlsj_LocalForever : {
    remoteVersion : "http://localdev84.h1.cilugame.com/h1/localforever/android/dlls/",
    downloadURLPrefix : "http://localdev84.h1.cilugame.com/h1/localforever/android/dlls/"
  },
  Xlsj_BetaTest : {
    remoteVersion : "http://cdn.xlsj.mtiancity.com/h1/betatest/android/dlls/",
    downloadURLPrefix : "http://cdn.xlsj.mtiancity.com/h1/betatest/android/dlls/"
  },
  Xlsj_Release : {
    remoteVersion : "http://cdn.xlsj.mtiancity.com/h1/release/android/dlls/",
    downloadURLPrefix : "http://cdn.xlsj.mtiancity.com/h1/release/android/dlls/"
  },
  Xlsj_Business : {
    remoteVersion : "http://small.cilugame.com/h1/business/android/dlls/",
    downloadURLPrefix : "http://small.cilugame.com/h1/business/android/dlls/"
  },
  //Tencent
  Tencent_Release: {
    remoteVersion : "http://cdn.xlsj.mtiancity.com/h1/tencent/android/dlls/",
    downloadURLPrefix : "http://cdn.xlsj.mtiancity.com/h1/tencent/android/dlls/"
  },
  //Mhxy
  Mhxy_Release: {
    remoteVersion : "http://cdn.h1y.demigame.com/h1y/h1y_release/android/dlls/",
    downloadURLPrefix : "http://cdn.h1y.demigame.com/h1y/h1y_release/android/dlls/"
  },
  //Yhxj
  Yhxj_Release: {
    remoteVersion : "http://cdn.h1x.demigame.com/h1x/h1x_release/android/dlls/",
    downloadURLPrefix : "http://cdn.h1x.demigame.com/h1x/h1x_release/android/dlls/"
  }
}
var profileName = null;
var workDir = Paths.get("").toAbsolutePath();
var apkfileProperty = null;
var copyDllProperty = null;
var encryptDll = false;
var forceUpdate = false;
if(typeof project != "undefined") {
  profileName = project.getProperty('patchProfile');
  apkfileProperty = project.getProperty('patchApkfile');
  copyDllProperty = project.getProperty('patchCopyDllToDir');
  encryptDll = (!!project.getProperty('encrypt'));
  forceUpdate = (!!project.getProperty('forceUpdate'));
} else {
  profileName = java.lang.System.getProperty('patchProfile');
  apkfileProperty = java.lang.System.getProperty('patchApkfile');
  copyDllProperty = java.lang.System.getProperty('patchCopyDllToDir');
  encryptDll = (!!java.lang.System.getProperty('encrypt'));
  forceUpdate = (!!java.lang.System.getProperty('forceUpdate'));
}

if(!profileName || profileName.length() <= 0) {
  print("patch profile not set correctly, patchProfile=" + profileName);
  exit(1);
}
if(!profiles[profileName]) {
  print("patch profile not found, patchProfile=" + profileName);
  exit(1);
}

if(!apkfileProperty || apkfileProperty.length() <= 0 || !apkfileProperty.endsWith('.apk')) {
  print("apk file not set correctly, apkfile=" + apkfileProperty);
  exit(1);
}
var apkfilePath = Paths.get(apkfileProperty);
if(!apkfilePath.isAbsolute()) {
  apkfilePath = workDir.resolve(apkfileProperty);
}
if(!apkfilePath.toFile().exists()) {
  print("apk file not found: " + apkfilePath);
  exit(1);
}
apkfilePath = apkfilePath.toAbsolutePath();
//"/Users/baoyu/Documents/temp/test/unity/H1_0.4.36.22863BIS";
var apkDecompilePath = apkfilePath.toString().replace('\.apk',''); 
var monoLibDir = workDir.resolve("Project.cpp/mono/libs").toString();
//"/Users/baoyu/AndroidStudioProjects/MyApplication/app/src/main/assets/bin/Data/Managed";
var copyVersionDir = null; 
if(copyDllProperty) {
  var copyVersionPath = Paths.get(copyDllProperty);
  if(!copyVersionPath.isAbsolute()) {
    copyVersionPath = workDir.resolve(copyDllProperty);
  }
  if(!copyVersionPath.toFile().exists()) {
    copyVersionPath.toFile().mkdirs();
  }
  copyVersionDir = copyVersionPath.toAbsolutePath().toString();
}

print("work profile:" + profileName);
print("work apk:" + apkfilePath);
print("work copyDllToDir: " + copyVersionDir);
// exit(0);

var apktoolYMLName = "apktool.yml";
var androidManifestXMLName = "AndroidManifest.xml";
var appPackageName = "";
var genVersionFileName = "version.json";
var versionURL = profiles[profileName].remoteVersion + genVersionFileName;
var downloadURLPrefix = profiles[profileName].downloadURLPrefix;
var apkDLLPath = "assets/bin/Data/Managed";
var apkDecompileDir = new java.io.File(apkDecompilePath);

// get version id and app package name
var apktoolYMLFile = new File(apkDecompileDir, apktoolYMLName);
var apktoolYMLContent = new String(Files.readAllBytes(apktoolYMLFile.toPath()), "UTF-8");
var regexpVersionCode = new RegExp("versionCode:[\\s]*'(\\d+)'");
var apkVersionCode = regexpVersionCode.exec(apktoolYMLContent)[1];
print("current apk version code: " + apkVersionCode + ", from: " + apktoolYMLFile);

// package name
var androidManifestXMLFile = new File(apkDecompileDir, androidManifestXMLName);
var androidManifestXMLContent = new String(Files.readAllBytes(androidManifestXMLFile.toPath()), "UTF-8");
var regexpPackage = new RegExp("package\\s*=\\s*\"([^\"]+)\"");
appPackageName = regexpPackage.exec(androidManifestXMLContent)[1];
print("current apk package: " + appPackageName + ", from: " + androidManifestXMLFile);

replaceLoaderActivity(androidManifestXMLFile, androidManifestXMLContent);

var dllDir = new File(apkDecompileDir, apkDLLPath);
if(!dllDir.exists()) {
  print("dll dir not found: " + dllDir);
  java.lang.System.exit(1);
}
// dlls version generate
var versionInfo = {
  id : 0,
  remoteVersion : versionURL,
  downloadURLPrefix : downloadURLPrefix,
  files : {}
};

for(var i=0; i<copyFileNames.length; i++) {
  var fileName = copyFileNames[i];
  var dllFile = new File(dllDir, fileName);
  if(!dllFile.exists()) {
    print("Dll file not found: " + dllFile);
    continue;
  }
  // DLL加密处理
  if(encryptDll) {
    com.cilugame.build.tools.DLLXXTea.ciluEncryptFile(dllFile, dllFile);
  }
  
  var md5sum = md5Hex(dllFile);
  var fileInfo = { md5 : md5sum, size : dllFile.length() };
  versionInfo.files[fileName] = fileInfo;
}
versionInfo.id = Math.ceil(java.lang.System.currentTimeMillis() / 1000);

// 强制更新
if(forceUpdate) {
  versionInfo.forceUpdate = true;
}

var strVersionJson = JSON.stringify(versionInfo,null,2);
var versionFile = new File(dllDir, genVersionFileName);
var versionFilePath = Files.write(versionFile.toPath(), Arrays.asList(strVersionJson));
print("version info:" + strVersionJson);
print("generate version file: " + versionFile);

//copy mono lib
var libmonoFileName = "libmono.so";
// armv7a
var monoFromFile = new File(monoLibDir, "armv7a/" + libmonoFileName);
var monoToFile = new File(apkDecompileDir, "lib/armeabi-v7a/" + libmonoFileName);
Files.copy(monoFromFile.toPath(), monoToFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
print("copy: " + monoFromFile + " -> " + monoToFile);
// x86
monoFromFile = new File(monoLibDir, "x86/" + libmonoFileName);
monoToFile = new File(apkDecompileDir, "lib/x86/" + libmonoFileName);
Files.copy(monoFromFile.toPath(), monoToFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
print("copy: " + monoFromFile + " -> " + monoToFile);

// copy dlls to outer dir
if(copyVersionDir && copyVersionDir.length()>0) {
  for(var i=0; i<copyFileNames.length; i++) {
    var fileName = copyFileNames[i];
    var dllFile = new File(dllDir, fileName);
    if(!dllFile.exists()) {
      continue;
    }
    var toFile = new File(copyVersionDir, fileName);
    Files.copy(dllFile.toPath(), toFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
    print("copy: " + dllFile + " -> " + toFile);
  }
  var toFile = new File(copyVersionDir, genVersionFileName);
  Files.copy(versionFilePath, toFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
  print("copy: " + versionFilePath + " -> " + toFile);
}


