package cn.tongdun.kunpeng.api.engine.constant;

import org.apache.commons.lang3.StringUtils;

/**
 * 场景化使用的 事件类型
 */
public enum SceneEventTypeEnum {

    IDENTITY("Identity", "身份欺诈"),
    PROMO("Promo", "营销欺诈"),
    TRANSACTION("Transaction", "交易欺诈"),
    ;

    private String eventType;
    private String description;

    SceneEventTypeEnum(String eventType, String description) {
        this.eventType = eventType;
        this.description = description;
    }

    /**
     * 是否场景化策略
     */
    public static boolean isScenePolicy(String eventType) {
        if (StringUtils.isBlank(eventType)) {
            return false;
        }
        for (SceneEventTypeEnum eventTypeEnum : SceneEventTypeEnum.values()) {
            if (eventTypeEnum.getEventType().equals(eventType)) {
                return true;
            }
        }
        return false;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
