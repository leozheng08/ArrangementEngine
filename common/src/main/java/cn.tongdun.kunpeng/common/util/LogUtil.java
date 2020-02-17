package cn.tongdun.kunpeng.common.util;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class LogUtil {

    private final static FastDateFormat sdf = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");

    public static Logger getDebugLogger() {
        return LoggerFactory.getLogger("debug");
    }

    public static void logUtil(Logger logger, String funcDesc, String dateStr,Object... otherInfo){
        if(StringUtils.isBlank(dateStr)){
            return;
        }
        try{
            Date t = sdf.parse(dateStr);
            if(t.getTime() > System.currentTimeMillis()){
                logInfo(logger, funcDesc, null, otherInfo);
            }
        }
        catch (Exception e){

        }
    }


    public static void logInfo(Logger logger, String funcDesc, String fieldName, Object... otherInfo) {
        logInfoWithPartner(logger, funcDesc, null, null, null, fieldName, otherInfo);
    }

    public static void logInfoWithPartner(Logger logger, String funcDesc, String partner, String app, String eventType,
                                          String fieldName, Object... otherInfo) {
        if (logger.isInfoEnabled()) {
            LogInfo logInfo = new LogInfo(funcDesc, partner, app, eventType, fieldName, null);
            logger.info(getLogData(logInfo, otherInfo));
        }
    }

    public static void logWarn(Logger logger, String funcDesc, String fieldName, String errorMsg, Object... otherInfo) {
        logWarn(logger, funcDesc, fieldName, errorMsg, null, otherInfo);
    }

    public static void logWarn(Logger logger, String funcDesc, String fieldName, String errorMsg, Throwable throwable,
                               Object... otherInfo) {
        logWarnWithPartner(logger, funcDesc, null, null, null, fieldName, errorMsg, throwable, otherInfo);
    }

    public static void logWarnWithPartner(Logger logger, String funcDesc, String partner, String app, String eventType,
                                          String fieldName, String errorMsg, Object... otherInfo) {
        logWarnWithPartner(logger, funcDesc, partner, app, eventType, fieldName, errorMsg, null, otherInfo);
    }

    public static void logWarnWithPartner(Logger logger, String funcDesc, String partner, String app, String eventType,
                                          String fieldName, String errorMsg, Throwable throwable, Object... otherInfo) {
        if (logger.isWarnEnabled()) {
            LogInfo logInfo = new LogInfo(funcDesc, partner, app, eventType, fieldName, errorMsg);
            warn(logger, getLogData(logInfo, otherInfo), throwable);
        }
    }

    public static void logError(Logger logger, String funcDesc, String fieldName, String errorMsg, Object... otherInfo) {
        logError(logger, funcDesc, fieldName, errorMsg, null, otherInfo);
    }

    public static void logError(Logger logger, String funcDesc, String fieldName, String errorMsg, Throwable throwable,
                                Object... otherInfo) {
        logErrorWithPartner(logger, funcDesc, null, null, null, fieldName, errorMsg, throwable, otherInfo);
    }

    public static void logErrorWithPartner(Logger logger, String funcDesc, String partner, String app, String eventType,
                                           String fieldName, String errorMsg, Throwable throwable, Object... otherInfo) {
        if (logger.isErrorEnabled()) {
            LogInfo logInfo = new LogInfo(funcDesc, partner, app, eventType, fieldName, errorMsg);
            error(logger, getLogData(logInfo, otherInfo), throwable);
        }
    }

    private static String getLogData(LogInfo logInfo, Object... otherInfo) {
        logInfo.putAll(buildMap(otherInfo));
        return JSON.toJSONString(logInfo);
    }

    private static void warn(Logger logger, String s, Throwable t) {
        if (t == null) {
            logger.warn(s);
        } else {
            logger.warn(s, t);
        }
    }

    private static void error(Logger logger, String s, Throwable t) {
        if (t == null) {
            logger.error(s);
        } else {
            logger.error(s, t);
        }
    }

    private static Map<String, String> buildMap(Object... args) {
        if (args == null) {
            return Collections.EMPTY_MAP;
        }
        if (args.length == 1) {
            if (args[0] instanceof Map) {
                return (Map) args[0];
            } else {
                return Collections.EMPTY_MAP;
            }
        }

        Map<String, String> result = new HashMap<>(args.length / 2 + 1);
        for (int i = 0; i < args.length; i = i + 2) {
            if (i + 1 <= args.length - 1) {
                result.put(String.valueOf(args[i]), String.valueOf(args[i + 1]));
            }
        }
        return result;
    }
}
