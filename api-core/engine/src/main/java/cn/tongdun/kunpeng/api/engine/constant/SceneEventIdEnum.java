package cn.tongdun.kunpeng.api.engine.constant;

import org.apache.commons.lang3.StringUtils;

/**
 * 场景化使用的策略标识
 */
public enum SceneEventIdEnum {

    IDENTITY_SCENE("Identity_scene", "身份欺诈"),
    PROMO_SCENE("Promo_scene", "营销欺诈"),
    TRANSACTION_SCENE("Transaction_scene", "交易欺诈"),
    ;

    private String eventId;
    private String description;

    SceneEventIdEnum(String eventId, String description) {
        this.eventId = eventId;
        this.description = description;
    }

    /**
     * 是否场景化策略
     */
    public static boolean isScenePolicy(String eventId) {
        if (StringUtils.isBlank(eventId)) {
            return false;
        }
        for (SceneEventIdEnum eventIdEnum : SceneEventIdEnum.values()) {
            if (eventIdEnum.getEventId().equals(eventId)) {
                return true;
            }
        }
        return false;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
