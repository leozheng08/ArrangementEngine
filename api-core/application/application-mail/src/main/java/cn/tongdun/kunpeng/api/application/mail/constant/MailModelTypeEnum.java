package cn.tongdun.kunpeng.api.application.mail.constant;

import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

/**
 * @author: yuanhang
 * @date: 2020-05-28 15:57
 **/
public enum MailModelTypeEnum {

    FORMAT_OBEY_NAME_RULE("900", "格式不符合命名规则", "ILLEGAL_MAIL", -1),
    NORMAL("901", "正常邮箱", "NORMAL", 0),
    TEMPLATE("902", "临时邮箱", "DEA_MAIL", 1),
    SIMULATION("903", "相似邮箱", "SPOOFING_VULNERABLE", 2),
    SUFFIX_SIMULATION("904", "用户名后缀相似", "SPOOFING_VULNERABLE", 3),
    TO_MUCH_SYMBOL("905", "包含特殊符号过多", "ILLEGAL_MAIL", 4),
    RANDOM("906", "邮箱随机生成", "SPOOFING_VULNERABLE", 5);

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

    public static final String getDescByMappingCode(Integer mappingCode) {
        for (MailModelTypeEnum mailEnum:MailModelTypeEnum.values()) {
            if (mailEnum.mappingCode.equals(mappingCode)) {
                return mailEnum.desc;
            }
        }
        return null;
    }

    public static final String getDescEnByMappingCode(Integer mappingCode) {
        for (MailModelTypeEnum mailEnum:MailModelTypeEnum.values()) {
            if (mailEnum.mappingCode.equals(mappingCode)) {
                return mailEnum.descEn;
            }
        }
        return null;
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
