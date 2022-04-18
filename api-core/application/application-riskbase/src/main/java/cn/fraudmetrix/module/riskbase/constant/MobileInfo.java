package cn.fraudmetrix.module.riskbase.constant;

import java.util.HashSet;

/**
 * Created by miao.zhang on 17/1/17.
 * 运营商号段对应关系（未全）
 * TODO 兼容过度阶段脚本，线上稳定后删除即可
 */
public enum MobileInfo {

    //移动号段
    MOBILE("MOBILE", new HashSet<String>() {{
//        add("134");
        add("135");
        add("136");
        add("137");
        add("138");
        add("139");
        add("147");
        add("150");
        add("151");
        add("152");
        add("157");
        add("158");
        add("159");
        add("178");
        add("182");
        add("183");
        add("184");
        add("187");
        add("188");
        add("1705");
    }}),

    //联通号段
    UNICOME("UNICOME", new HashSet<String>() {{
        add("130");
        add("131");
        add("132");
        add("145");
        add("155");
        add("156");
        add("171");
        add("176");
        add("185");
        add("186");
        add("1707");
        add("1708");
        add("1709");
    }}),

    //电信号段
    TELECOM("TELECOM", new HashSet<String>() {{
        add("133");
        add("153");
        add("173");
        add("177");
        add("180");
        add("181");
        add("189");
        add("1349");
        add("1700");
        add("1701");
        add("1702");
    }}),

    //未知号段
    UNKNOWN("UNKNOWN", new HashSet<String>() {{
    }});


    //运营商名称
    private String name;

    private HashSet<String> set;

    MobileInfo(String name, HashSet<String> set) {
        this.name = name;
        this.set = set;
    }

    public String getName() {
        return name;
    }

    public HashSet<String> getSet() {
        return set;
    }
}
