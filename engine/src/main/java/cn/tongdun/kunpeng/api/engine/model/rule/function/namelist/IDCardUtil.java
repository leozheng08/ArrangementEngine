package cn.tongdun.kunpeng.api.engine.model.rule.function.namelist;

import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by lvyadong on 2020/01/17.
 */
public enum IDCardUtil {

    IDNUMBER("身份证"), PAYEEIDNUMBER("买家身份证"), CONTACTIDNUMBER("联系人身份证"), CONTACTSIDNUMBER("联系人身份证-数组类型"), CONTACT1IDNUMBER("第一联系人身份证"),
    CONTACT2IDNUMBER("第二联系人身份证"), CONTACT3IDNUMBER("第三联系人身份证"), CONTACT4IDNUMBER("第四联系人身份证"),
    CONTACT5IDNUMBER("第五联系人身份证"), DELIVERIDNUMBER("收货人身份证"), BORROWERIDNUMBER("借款人身份证"), COBORROWERIDNUMBER("共同借款人身份证"),
    COBORROWERSPOUSEIDNUMBER("共同借款人配偶身份证"), SURETYIDNUMBER("担保人身份证"), SURETYSPOUSEIDNUMBER("担保人配偶身份证"),
    SURETYCOMPANYIDNUMBER("担保企业法人身份证"), GUARANTEEIDNUMBER("被担保人身份证"),
    EXTGUARDIANIDNUMBER("EXT-被担保人身份证"), EXTSPOUSEIDNUMBER("EXT-配偶身份证"), EXTIDNUMBER("EXT-账户身份证号"),
    EXTBORROWERIDNUMBER("EXT-借款人身份证"), EXTIDCARD("EXT-身份证"), EXTUBIDNUMBER("EXT-身份证");

    private String displayName;

    IDCardUtil(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    private static Set<String> keys = new HashSet<>();

    static {
        for (IDCardUtil idCardUtil : IDCardUtil.values()) {
            keys.add(idCardUtil.name());
        }
    }

    public static boolean containIdKey(String idNumber) {
        if (StringUtils.isEmpty(idNumber)) {
            return false;
        }
        return keys.contains(idNumber.toUpperCase());
    }
}
