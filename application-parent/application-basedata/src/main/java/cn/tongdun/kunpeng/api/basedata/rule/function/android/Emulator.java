package cn.tongdun.kunpeng.api.basedata.rule.function.android;

import cn.fraudmetrix.module.tdrule.context.ExecuteContext;
import cn.fraudmetrix.module.tdrule.function.AbstractFunction;
import cn.fraudmetrix.module.tdrule.function.FunctionDesc;
import cn.fraudmetrix.module.tdrule.function.FunctionResult;
import cn.tongdun.kunpeng.api.application.context.FraudContext;
import cn.tongdun.kunpeng.common.Constant;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class Emulator extends AbstractFunction {
    private static final Logger logger = LoggerFactory.getLogger(Emulator.class);


    @Override
    public String getName() {
        return Constant.Function.ANDROID_EMULATOR;
    }


    @Override
    public void parseFunction(FunctionDesc functionDesc) {

    }

    @Override
    public FunctionResult run(ExecuteContext executeContext) {
        FraudContext context = (FraudContext) executeContext;

        Map<String, Object> deviceInfo = context.getDeviceInfo();
        if (deviceInfo == null) {
            return new FunctionResult(false);
        }

        // 设备指纹银行基金部署改造需求,客户直接传设备信息参数(device_raw)的话,拷贝fp的代码自己处理模拟器
        if (context.getSystemFiels().containsKey("deviceRaw")) {
            int emulatorScore = isEmulator(deviceInfo);
            deviceInfo.put("emulatorProbability", emulatorScore);
            deviceInfo.put("isEmulator", emulatorScore / 200D > 0.5);
        }

        Object isAndroidEmulator = deviceInfo.get("isEmulator");
        if (Boolean.TRUE.equals(isAndroidEmulator)) {
            return new FunctionResult(true);
        }
        else {
            return new FunctionResult(false);
        }
    }


    private int isEmulator(Map<String, Object> deviceInfo) {
        Map<String, Integer> reasons = new HashMap<>();

        int score = 0;
        if (deviceInfo.get("phoneNumber") != null
                && ((String) deviceInfo.get("phoneNumber")).startsWith("1555521")) { //googleAVD
            score += 10;
            reasons.put("phonenumber", 10);
        }

        if (deviceInfo.get("voiceMail") != null
                && ((String) deviceInfo.get("voiceMail")).startsWith("+155521")) { //nox +15552175049
            score += 30;
            reasons.put("voicemail_hit", 30);
        }

        String imei = (String) deviceInfo.get("imei");
        if (imei == null) {
            score += 5;
            reasons.put("imei_null", 5);
        }
        else if ("000000000000000".equals(imei) // genymotion
                || "311006583258717".equals(imei) // googleAVD
                || "e21833235b6eef10".equals(imei) // tianitan
                || "012345678912345".equals(imei) // VirusTotal
                || "352284049577509".equals(imei) // itoolsVm
                || "133524847493227".equals(imei)) { // MEmu
            score += 10;
            reasons.put("imei_hit", 10);
        }

        String subsciberId = (String) deviceInfo.get("subscriberId");
        subsciberId = subsciberId == null ? (String) deviceInfo.get("subscriberID") : subsciberId;
        if (subsciberId == null) {
            score += 5;
            reasons.put("imsi_null", 5);
        }
        else if ("310260000000000".equals(deviceInfo.get("subscriberId")) //genymotion
                || "310260389107061".equals(deviceInfo.get("subscriberId"))) { // tiantian
            score += 10;
            reasons.put("imsi_hit", 10);
        }

        //1.0版本没有该字段
        String fmVersion = (String) deviceInfo.get("fmVersion");
        if (!"v1.1.0".equals(fmVersion) && !"v1.0.4".equals(fmVersion)) {
            if (deviceInfo.get("serialNo") == null) { // serialNo
                score += 5;
                reasons.put("serialno_null", 5);
                // } else if (deviceInfo.get("serialNo").toString().toLowerCase().equals("pf05uu7d")) {//duos
                // score += 50;
                // reasons.put("serialno_hit", 50);
            }
            else {
                String seriaNo = (String) deviceInfo.get("serialNo");
                if (seriaNo.equals("nox")) {
                    score += 50;
                    reasons.put("serialno_hit", 50);
                }
            }
        }

        String[] vmApps = new String[]{
                //amiduos
                "com.ami.duosupdater.ui",
                "com.ami.launchmetro",
                "com.ami.syncduosservices",
                //bluestacks
                "com.bluestacks.home",
                "com.bluestacks.windowsfilemanager",
                "com.bluestacks.settings",
                "com.bluestacks.bluestackslocationprovider",
                "com.bluestacks.appsettings",
                "com.bluestacks.bstfolder",
                "com.bluestacks.BstCommandProcessor",
                "com.bluestacks.s2p",
                "com.bluestacks.setup",
                //kaopu tiantian
                "com.kaopu001.tiantianserver",
                "com.kpzs.helpercenter",
                "com.kaopu001.tiantianime",
                //googleAVD
                "com.android.development_settings",
                "com.android.development",
                "com.android.customlocale2",
                "com.example.android.apis",
                //genymotion
                "com.genymotion.superuser",
                "com.genymotion.clipboardproxy",
                //
                "com.uc.xxzs.keyboard",
                "com.uc.xxzs",
                //蓝光大师
                "com.blue.huang17.agent",
                "com.blue.huang17.launcher",
                "com.blue.huang17.ime",
                // MEmu
                "com.microvirt.guide",
                "com.microvirt.market",
                "com.microvirt.memuime",
                // itoolsVm
                "cn.itools.vm.launcher",
                "cn.itools.vm.proxy",
                "cn.itools.vm.softkeyboard",
                "cn.itools.avdmarket",
                // syd
                "com.syd.IME",
                //nox
                "com.bignox.app.store.hd",
                "com.bignox.launcher",
                "com.bignox.app.phone",
                "com.bignox.app.noxservice",
                "com.android.noxpush",
                //haimawan
                "com.haimawan.push",
                "me.haima.helpcenter",
                "me.haima.androidassist",
                //windroy
                "com.windroy.launcher",
                "com.windroy.superuser",
                "com.windroy.launcher",
                "com.windroy.ime"
        };

        //installedPackages
        int installedPackagesScore = 0;
        if (deviceInfo.get("installedPackages") != null) {
            String installedPackages = ((String) deviceInfo.get("installedPackages")).toLowerCase();
            int vmScore = 0;
            for (String pkg : vmApps) {
                if (installedPackages.contains(pkg)) {
                    vmScore += 20;
                }
            }
            score += vmScore;
            installedPackagesScore = vmScore;
            reasons.put("installedapp_hit", vmScore);
        }

        //runningPacages
        if (deviceInfo.get("runningPackages") != null) {
            String runningPackages = ((String) deviceInfo.get("runningPackages")).toLowerCase();
            int singleScore = 20;
            if (installedPackagesScore > 60) {
                singleScore = 5;
            }
            int vmScore = 0;
            for (String pkg : vmApps) {
                if (runningPackages.contains(pkg)) {
                    vmScore += singleScore;
                }
            }
            score += vmScore;
            reasons.put("runningPackages_hit", vmScore);
        }

        //vmAppList
        if (deviceInfo.get("vmAppList") != null) {
            String vmAppList = (String) deviceInfo.get("vmAppList");
            if (vmAppList.length() > 2) {
                int vmScore = vmAppList.split(",").length * 20;
                if (vmScore <= 300) {
                    score += vmScore;
                    reasons.put("vmapplist_v2_hit", vmScore);
                }
            }
        }
        else if (deviceInfo.get("VMAppsList") != null) {
            String VMAppList = ((String) deviceInfo.get("VMAppsList"));
            int vmScore = VMAppList.length() >= 7 ? 40 // genymotion, gooleAVD
                    : VMAppList.length() >= 5 ? 30 :
                    VMAppList.length() >= 3 ? 20 : VMAppList.length() >= 1 ? 10 : 0; // genymotion
            if (vmScore > 0) {
                score += vmScore;
                reasons.put("vmapplist_v1_hit", vmScore);
            }
        }

        String roDevice = (String) deviceInfo.get("ro_device");
        String roName = (String) deviceInfo.get("ro_name");
        String roModel = (String) deviceInfo.get("ro_model");

        roDevice = null == roDevice ? (String) deviceInfo.get("roDevice") : roDevice;
        roName = null == roName ? (String) deviceInfo.get("roName") : roName;
        roModel = null == roModel ? (String) deviceInfo.get("roModel") : roModel;

        if (roDevice != null) {
            roDevice = roDevice.toLowerCase();
            if (roDevice.equals("nox") || roDevice.equals("vbox86p") || roDevice.equals("vbox86tp")
                    || roDevice.equals("appplayer") || roDevice.equals("droid4x")
                    || roDevice.equals("ttvm_hdragon") || roDevice.equals("native")
                    || roDevice.equals("x86") || roDevice.equals("xcplayertpvbox")
                    || roDevice.equals("pomeloappplayer") || roDevice.equals("duos")
                    || roDevice.equals("itoolsvm") || roDevice.equals("syd")
                    || roDevice.equals("virtual") || roDevice.equals("memu")
                    || roDevice.contains("vbox") || roDevice.contains("appplayer")
                    || roDevice.contains("windroy")) {
                score += 50;
                reasons.put("ro_device_hit", 50);
            }
            else if ("generic".equals(roDevice)) {
                score += 20;
                reasons.put("ro_device_hit", 20);
            }
        }
        if (roName != null) {
            roName = roName.toLowerCase();
            if (roName.equals("nox") || roName.equals("vbox86p") || roName.equals("vbox86tp")
                    || roName.equals("genymotion") || roName.equals("bluestacks")
                    || roName.equals("sdk") || roName.equals("droid4x")
                    || roName.equals("ttvm_hdragon") || roName.equals("duos_native")
                    || roName.equals("android_x86") || roName.equals("pomelo")
                    || roName.equals("xcplayertpvbox") || roName.equals("duos")
                    || roName.equals("itoolsvm") || roName.equals("memu") || roName.equals("syd")// MEmu syd
                    || roName.contains("vbox") || roName.contains("windroy")) {
                score += 50;
                reasons.put("ro_name", 50);
            }
        }
        if (roModel == null) { // genymotion
            score += 10;
            reasons.put("ro_model_null", 10);
        }
        else {
            roModel = roModel.toLowerCase();
            if (roModel.equals("duos") || roModel.equals("amiduos") || roModel.equals("memu")
                    || roModel.contains("noxw") || roModel.contains("genymotion")
                    || roModel.contains("bluestacks") || roModel.contains("droid4x")
                    || roModel.contains("xingxing") || roModel.contains("pomelo")
                    || roModel.contains("tiantian") || roModel.contains("windroy")) {
                score += 50;
                reasons.put("ro_model_hit", 50);
            }
        }

        String model = (String) deviceInfo.get("model");
        if (model != null) {
            model = model.toLowerCase();
            if ("sdk".equals(model)) {
                score += 10;
                reasons.put("model_hit", 10);
            }
            else if (model.equals("duos") || model.equals("amiduos")//duos误杀
                    || model.equals("itoolsavm") || model.equals("memu")
                    || model.contains("droid4x") || model.contains("noxw")
                    || model.contains("xingxing") || model.contains("pomelo")
                    || model.contains("tiantian") || model.contains("syd")) {
                score += 50;
                reasons.put("model_hit", 50);
            }
        }

        if (deviceInfo.get("brand") != null) {
            String brand = ((String) deviceInfo.get("brand")).toLowerCase();
            if (brand.equals("ttandroid") || brand.equals("ami")
                    || brand.equals("android-x86") || brand.equals("pomelo")
                    || brand.equals("microvirt") || brand.equals("apple")
                    || brand.equals("iphone") || brand.equals("amiduos")
                    || brand.equals("windroy")) { // duos
                score += 50;
                reasons.put("brand_hit", 50);
            }
            else if ("generic".equals(brand)) { // genymotion googleAVD
                score += 10;
                reasons.put("brand_hit", 10);
            }
        }

        if (deviceInfo.get("product") != null) {
            String product = ((String) deviceInfo.get("product")).toLowerCase();
            if (product.equals("nox") || product.equals("vbox86p")
                    || product.equals("droid4x") || product.equals("ttvm_hdragon")
                    || product.equals("duos_native") || product.equals("android_x86")
                    || product.equals("pomelo") || product.equals("duos")
                    || product.equals("itoolsavm") || product.equals("memu")
                    || product.equals("syd")) { // genymotion droid4x tiantian xingxing
                score += 50;
                reasons.put("product_hit", 50);
            }
            else if ("sdk".equals(product) || product.equals("product")) { // googleAVD
                score += 10;
                reasons.put("product_hit", 10);
            }
        }

        if (deviceInfo.get("hardware") != null) {
            String hardware = ((String) deviceInfo.get("hardware")).toLowerCase();
            if (hardware.equals("vbox86") || hardware.equals("goldfish")
                    || hardware.equals("ttvm_x86") || hardware.equals("duos")
                    || hardware.equals("android_x86") || hardware.equals("intel")
                    || hardware.equals("nox")) { // genymotion googleAVD tiantian xingxing
                score += 50;
                reasons.put("hardware_hit", 50);
            }
            else if (hardware.equals("unknown") || hardware.equals("placeholder")) {
                score += 10;
                reasons.put("hardware_hit", 10);
            }
        }

        /** linux特性文件 */
        /*
         * private final static String FILE_NAME[] = { "/system/lib/libc_malloc_debug_qemu.so", "/sys/qemu_trace",
         * "/system/bin/qemu-props", "/dev/socket/qemud", "/dev/qemu_pipe", "/dev/socket/genyd",
         * "/dev/socket/baseband_genyd", "/sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq" };
         */

        if (deviceInfo.get("isFileExist") != null) {
            String isFileExist = (String) deviceInfo.get("isFileExist");
            int isFileExistScore = 0;
            if (isFileExist.contains("0") && isFileExist.contains("7")) {
                isFileExistScore = 40 + score;
            }
            else if (isFileExist.contains("0") || isFileExist.contains("7")) {
                isFileExistScore = 20;
                if ("kltexx".equals(deviceInfo.get("product"))
                        || "kltexx".equals(deviceInfo.get("roName"))
                        || "klte".equals(deviceInfo.get("roDevice"))) {
                    isFileExistScore += score;
                }
            }
            else if (isFileExist.length() >= 5) {
                isFileExistScore = 30 + score;
            }
            score += isFileExistScore;
            reasons.put("file_hit", isFileExistScore);
        }

        //wifiList
        if (deviceInfo.get("wifiList") != null) {
            String wifiListString = (String) deviceInfo.get("wifiList");
            try {
                JSONArray wifiJsonList = JSON.parseArray(wifiListString);
                if (wifiJsonList.size() == 1) {
                    JSONObject object = wifiJsonList.getJSONObject(0);
                    if (StringUtils.equalsIgnoreCase(object.getString("wifi_ssid"), "WiredSSID")
                            || StringUtils.equalsIgnoreCase(object.getString("wifi_ssid"), "MEmuWiFi")
                            || StringUtils.equalsIgnoreCase(object.getString("wifi_ssid"), "Windroye")) {
                        score += 100;
                        reasons.put("wiredssid_wired_hit", 100);
                    }
                }
                else if (wifiJsonList.size() > 1) {
                    wifiListString = wifiListString.toLowerCase();
                    if (wifiListString.contains("duos wifi")) {//amiduos
                        score += 20;
                        reasons.put("duoswifi_hit", 20);
                    }
                    else {
                        if (score >= 50) {
                            score -= 50;
                        }
                        else {
                            score = 0;
                        }
                        reasons.put("no_wiredssid/duoswifi_hit", -50);
                    }
                }
            }
            catch (Exception e) {
                logger.error("isEmulator error", e);
            }
        }

        //cellLocation
        if (deviceInfo.get("cellLocation") != null) {
            if ("[0,0,-1]".equals(deviceInfo.get("cellLocation"))) {
                score += 10;
                reasons.put("cellLocation_hit", 10);
            }
        }

        //wifiMac
        String wifiMac = (String) deviceInfo.get("wifiMac");
        if (wifiMac != null) {
            if (StringUtils.startsWithIgnoreCase(wifiMac, "08:00:27")) {
                score += 5;
                reasons.put("wifimac_hit", 5);
            }
        }

        //devieName
        if (deviceInfo.get("deviceName") != null) {
            String deviceName = ((String) deviceInfo.get("deviceName")).toLowerCase();
            if (deviceName.equals("generic") || deviceName.equals("vbox86p")
                    || deviceName.equals("ttvm_hdragon") || deviceName.equals("duos")
                    || deviceName.equals("klte") || deviceName.equals("x86")
                    || deviceName.equals("nox") || deviceName.equals("droid4x")
                    || deviceName.equals("pomeloappplayer") || deviceName.equals("device")
                    || deviceName.equals("syd")) {
                score += 20;
                reasons.put("deviceName_hit", 20);
            }
        }

        //display
        if (deviceInfo.get("display") != null) {
            String display = ((String) deviceInfo.get("display")).toLowerCase();
            if (display.equals("generic") || display.contains("sdk-eng")
                    || display.contains("vbox86p") || display.contains("android_x86")
                    || display.contains("display")) {
                score += 20;
                reasons.put("display_hit", 20);
            }
            else if (display.contains("memu") || display.contains("ttvm_hdragon")
                    || display.contains("nox") || display.contains("xcplayertpvbox")
                    || display.contains("syd") || display.contains("droid4x")
                    || display.contains("windroy")) {
                score += 50;
                reasons.put("display_hit", 50);
            }
        }

        //wifiIp
        if (deviceInfo.get("wifiIp") != null) {
            String wifiIp = (String) deviceInfo.get("wifiIp");
            if (wifiIp.equals("10.0.3.15")) { //nox
                score += 20;
                reasons.put("wifiIp_hit", 20);
            }
        }

        //networkName
        if (deviceInfo.get("networkNames") != null) {
            String networkNames = (String) deviceInfo.get("networkNames");
            String network[] = networkNames.split(", ");
            int count = 0;
            for (String str : network) {
                if (str.startsWith("eth")) {
                    count++;
                }
            }
            if (count == 2) {
                score += 20;
                reasons.put("networkName_hit", 20);
            }
            else if (count >= 3) {
                score += 30;
                reasons.put("networkName_hit", 30);
            }
        }

        //userAgent
        if (deviceInfo.get("userAgent") != null) {
            String userAgent = ((String) deviceInfo.get("userAgent")).toLowerCase();
            if (userAgent.contains("droid4x") || userAgent.contains("noxw")
                    || userAgent.contains("tiantian") || userAgent.contains("memu")) {
                score += 50;
                reasons.put("userAgent_hit", 50);
            }
        }

        //musicHash
        if (deviceInfo.get("musicHash") != null) {
            String musicHash = ((String) deviceInfo.get("musicHash")).toLowerCase();
            if (musicHash.equals("d41d8cd98f0b24e980998ecf8427e")) {
                score += 10;
                reasons.put("musicHash_hit", 10);
            }
        }

        //cpuhardware
        if (deviceInfo.get("cpuHardware") != null) {
            String musicHash = ((String) deviceInfo.get("cpuHardware")).toLowerCase();
            if (musicHash.equals("placeholder")) {
                score += 5;
                reasons.put("cpuHardware_hit", 5);
            }
        }

        return score;
    }


}
