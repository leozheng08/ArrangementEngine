package cn.tongdun.kunpeng.api.basedata.constant;

import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

/**
 * @author: yuanhang
 * @date: 2020-05-28 15:57
 **/
public enum MailModelTypeEnum {

    FORMAT_OBEY_NAME_RULE("900", "格式不符合命名规则", "FORMAT_OBEY_NAME_RULE", -1),
    NORMAL("901", "正常邮箱", "NORMAL", 0),
    TEMPLATE("902", "临时邮箱", "TEMPLATE", 1),
    SIMULATION("903", "相似邮箱", "SIMULATION", 2),
    SUFFIX_SIMULATION("904", "用户名后缀相似", "SUFFIX_SIMULATION", 3),
    TO_MUCH_SYMBOL("905", "包含特殊符号过多", "TO_MUCH_SYMBOL", 4),
    RANDOM("906", "邮箱随机生成", "RANDOM", 5);

    String code;
    String desc;
    String descEn;
    Integer mappingCode;

    MailModelTypeEnum(String code, String desc, String descEn, Integer mappingCode) {
        this.code = code;
        this.desc = desc;
        this.descEn = descEn;
        this.mappingCode = mappingCode;
    }

    public final String code() {
        return this.code;
    }

    public static final Map<Integer, String> mappingCode(List<String> codes) {
        Map<Integer, String> codeMapping = Maps.newHashMap();
        MailModelTypeEnum[] var0 = values();
        for (int var1 = 0; var1 < var0.length; var1++) {
            if (codes.contains(var0[var1].code())) {
                codeMapping.put(var0[var1].mappingCode, var0[var1].code);
            }
        }
        return codeMapping;
    }


}
