package cn.tongdun.kunpeng.api.engine.model.decisionflow;

public enum RightFieldType {

    INDEX("index","指标"), //PolicyIndex
    GAEA_INDICATRIX("gaea_indicatrix","平台指标"),//PlatformIndex
    FIELD("field","字段");

    private String name;

    private String dName;

    RightFieldType(String name, String dName){
        this.name = name;
        this.dName = dName;
    }

    public String getName() {
        return name;
    }
}
