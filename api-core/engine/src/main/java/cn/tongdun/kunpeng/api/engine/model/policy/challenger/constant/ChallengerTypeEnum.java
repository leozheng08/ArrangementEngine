package cn.tongdun.kunpeng.api.engine.model.policy.challenger.constant;

import org.apache.commons.lang3.StringUtils;

public enum ChallengerTypeEnum {

    SPLITFLOW("splitFlow"), COPY("copy");

    private String type;

    private ChallengerTypeEnum(String type){
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static boolean validate(String key) {
        for (ChallengerTypeEnum p : ChallengerTypeEnum.values()) {
            if (StringUtils.equalsIgnoreCase(p.name(), key)) {
                return true;
            }
        }
        return false;
    }
}
