package cn.tongdun.kunpeng.api.common;

/**
 * @Author: liang.chen
 * @Date: 2020/2/11 下午10:48
 */
public class Constant {

    public static final String BUSINESS_DEFAULT = "default";
    public static final String BUSINESS_ANTI_FRAUD = "anti_fraud";
    public static final String BUSINESS_CREDIT = "credit";

    public static final double EARTH_RADIUS = 6378137.0;

    //合作方没填时，给的默认值
    public static final String DEFAULT_PARTNER = "default";
    public static final String DEFAULT_APP_NAME = "default";
    public static final String DEFAULT_APP_TYPE = "default";

    public class DictKey {
        public static final String DICT_EVENT_TYPE = "EventType";
        public static final String DICT_INDUSTRY_TYPE = "IndustryType";
    }


    public class Function {
        public static final String TIME_TIME_DIFFER = "time/timediffer";
        public static final String TIME_TIME_POINT_COMPARISON = "time/timePointComparison";
        public static final String TIME_EVENT_TIME_DF = "time/eventTimeDf";

        public static final String MAIL_MODEL = "mail/model";

        public static final String LOCATION_ADDRESS_MATCH = "location/addressMatch";
        public static final String LOCATION_PROXY = "location/proxy";
        public static final String LOCATION_GPS_DISTANCE = "location/gpsDistance";

        public static final String PATTERN_REGEX = "pattern/regex";
        public static final String PATTERN_FUNCTION_KIT = "pattern/functionKit";
        public static final String PATTERN_FOUR_CALCULATION = "pattern/fourCalculation";
        public static final String PATTERN_COUNT = "pattern/count";


        public static final String WATCHLIST_CUSTOM_LIST = "watchlist/customList";
        public static final String WATCHLIST_COMPLEX_CUSTOM_LIST = "watchlist/complexCustomList";

        public static final String WATCHLIST_PERSONAL_FUZZY_LIST = "watchlist/personalFuzzyMatching";
        public static final String WATCHLIST_COMPANY_FUZZY_LIST = "watchlist/companyFuzzyMatching";


        public static final String ANOMALY_DEVICE = "anomaly/device";
        public static final String ANOMALY_FP_EXCEPTION = "anomaly/fpException";
        public static final String ANOMALY_PROFILE = "anomaly/profile";
        public static final String ANOMALY_DEVICE_STATUS_ABNORMAL = "anomaly/deviceStatusAbnormal";
        public static final String VERIFICATION_CODE_ABNORMAL = "anomaly/verificationCode";


        public static final String IOS_IS_CHEAT = "ios/isCheat";
        public static final String IOS_IS_JAILBREAK = "ios/isJailBreak";
        public static final String IOS_USE_VPN = "ios/useVpn";
        public static final String IOS_USE_HTTP_PROXY = "ios/useHttpProxy";
        public static final String IOS_NOT_OFFICIAL_APP = "ios/notOfficialApp";
        public static final String IOS_DEVICE_ANOMALY = "ios/deviceAnomaly";
        public static final String IOS_FP_EXCEPTION = "ios/fpException";
        public static final String IOS_DEVICE_STATUS_ABNORMAL = "ios/deviceStatusAbnormal";


        public static final String ANDROID_EMULATOR = "android/emulator";
        public static final String ANDROID_IS_CHEAT_V2 = "android/isCheatV2";
        public static final String ANDROID_CHEAT_APP = "android/cheatApp";
        public static final String ANDROID_VPN = "android/vpn";
        public static final String ANDROID_HTTP = "android/http";
        public static final String ANDROID_NOT_OFFICIAL_APP = "android/notOfficialApp";
        public static final String ANDROID_ROOT = "android/root";
        public static final String ANDROID_DEVICE_ANOMALY = "android/deviceAnomaly";
        public static final String ANDROID_FP_EXCEPTION = "android/fpException";
        public static final String ANDROID_DEVICE_STATUS_ABNORMAL = "android/deviceStatusAbnormal";


        public static final String KEYWORD_WORDLIST = "keyword/wordList";
        public static final String KEYWORD_WORD_NEW = "keyword/wordNew";
        public static final String CONTENT_IMAGE = "content/image";
        public static final String COMNON_CUSTOM = "common/custom";

        public static final String EVIDENCE_EVIDENCE = "evidence/evidence";
        public static final String EVIDENCE_GREYLIST = "evidence/greylist";
        public static final String EVIDENCE_FUZZY = "evidence/fuzzy";
    }


    public class Flow {
        public static final String FLOW_PRE_CHECK = "flowPreCheck";
    }

    public class EncryptionField {
        public static final String POUND_SIGN = "##";
    }

}
