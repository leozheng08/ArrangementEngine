package cn.tongdun.kunpeng.api.basedata.constant;

/**
 * @author: yuanhang
 * @date: 2020-05-28 15:57
 **/
public enum MailModelTypeEnum {

    FORMAT_OBEY_NAME_RULE("900", "格式不符合命名规则", "FORMAT_OBEY_NAME_RULE"),
    NORMAL("901", "正常邮箱", "NORMAL"),
    TEMPLATE("902", "临时邮箱", "TEMPLATE"),
    SIMULATION("903", "相似邮箱", ""),
    SUFFIX_SIMULATION("904", "用户名后缀相似", "SUFFIX_SIMULATION"),
    TO_MUCH_SYMBOL("905", "包含特殊符号过多", "TO_MUCH_SYMBOL"),
    RANDOM("906", "邮箱随机生成", "RANDOM");

    String code;
    String desc;
    String descEn;

    MailModelTypeEnum(String code, String desc, String descEn) {
        this.code = code;
        this.desc = desc;
        this.descEn = descEn;
    }

    public final String code() {
        return this.code;
    }


}
