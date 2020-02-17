package cn.tongdun.kunpeng.common.util;

import com.alibaba.fastjson.JSONObject;

public class LogInfo extends JSONObject {

    /**
     *
     * @param function
     * @param partner
     * @param app
     * @param eventType
     * @param fieldName
     * @param errorMsg
     */
    public LogInfo(String function, String partner, String app, String eventType, String fieldName, String errorMsg) {
        super();

        //功能描述
        put("function", function);
        //合作方
        put("partner", partner);
        //应用
        put("app", app);
        //事件类型
        put("eventType", eventType);
        //字段名称
        put("fieldName", fieldName);
        //错误信息摘要
        put("errorMsg", errorMsg);
    }
}