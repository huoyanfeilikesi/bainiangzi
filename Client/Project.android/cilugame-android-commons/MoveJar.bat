set packPath=D:\H7Client\branches\channeltools\ApkPackTools

copy demiproxy\build\intermediates\bundles\release\classes.jar %packPath%\platforms\demi\jarlib\demiproxy.jar
copy shoumengproxy\build\intermediates\bundles\release\classes.jar %packPath%\platforms\shoumeng\jarlib\shoumengproxy.jar
copy ucproxy\build\intermediates\bundles\release\classes.jar %packPath%\platforms\uc\jarlib\ucproxy.jar
copy yijieproxy\build\intermediates\bundles\release\classes.jar %packPath%\platforms\yijie\jarlib\yijieproxy.jar
copy qihooproxy\build\intermediates\bundles\release\classes.jar %packPath%\platforms\qihoo\jarlib\qihooproxy.jar
copy huaweiproxy\build\intermediates\bundles\release\classes.jar %packPath%\platforms\huawei\jarlib\huaweiproxy.jar
copy oppoproxy\build\intermediates\bundles\release\classes.jar %packPath%\platforms\oppo\jarlib\oppoproxy.jar
copy acfunproxy\build\intermediates\bundles\release\classes.jar %packPath%\platforms\acfun\jarlib\acfunproxy.jar
copy nubiyaproxy\build\intermediates\bundles\release\classes.jar %packPath%\platforms\nubiya\jarlib\nubiyaproxy.jar
copy shunwangproxy\build\intermediates\bundles\release\classes.jar %packPath%\platforms\shunwang\jarlib\shunwangproxy.jar
copy vivoproxy\build\intermediates\bundles\release\classes.jar %packPath%\platforms\vivo\jarlib\vivoproxy.jar
copy xiaomiproxy\build\intermediates\bundles\release\classes.jar %packPath%\platforms\xiaomi\jarlib\xiaomiproxy.jar
copy demiproxy\build\intermediates\bundles\release\classes.jar %packPath%\platforms\demi\jarlib\demiproxy.jar
copy yeproxy\build\intermediates\bundles\debug\classes.jar %packPath%\platforms\ye\jarlib\yeproxy.jar

copy demiframe\build\intermediates\bundles\release\classes.jar %packPath%\Java\demiframe.jar

copy app\build\intermediates\bundles\release\classes.jar ..\..\Assets\Plugins\Android\CiluSdk\libs\com.cilugame.cilusdk.jar
copy demiframe\build\intermediates\bundles\release\classes.jar ..\..\Assets\Plugins\Android\libs\demiframe.jar

pause