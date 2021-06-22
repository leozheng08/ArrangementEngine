package cn.tongdun.kunpeng.api.application.content.constant;

public enum MathOperatorEnum {

    MORE("大于", ">"),
    EQUAL("等于", "="),
    LESS("小于", "<"),
    MORE_EQUAL("大于等于", ">="),
    LESS_EQUAL("小于等于","<="),
    NOT_EQUAL("不等于","!="),
    NOT_NULL("不为空","notnull"),
    NULL("为空","isnull");

    private String desc;
    private String name;

    MathOperatorEnum(String desc, String name) {
        this.desc = desc;
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
