package cn.tongdun.kunpeng.api.engine.model.rule.function.namelist;

/**
 * Created by lvyadong on 2020/01/17.
 */
public enum MatchModeEnum {

    EQUALS("完全匹配"),
    NOT_EQUALS("不等于"),
    INCLUDE("包含"),
    EXCLUDE("不包含"),
    PREFIX("前缀"),
    SUFFIX("后缀"),
    FUZZY("模糊匹配");

    private String desc;

    MatchModeEnum(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}
