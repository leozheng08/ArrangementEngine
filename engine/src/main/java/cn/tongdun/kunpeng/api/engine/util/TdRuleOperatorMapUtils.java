package cn.tongdun.kunpeng.api.engine.util;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: liuq
 * @Date: 2020/2/14 4:33 PM
 */
public class TdRuleOperatorMapUtils {

    private static final Map<String, String> display2Engine = new HashMap<>();

    static {
        display2Engine.put("||", "|");
        display2Engine.put("&&", "&");
        display2Engine.put(">","gt");
        display2Engine.put(">=","gte");
        display2Engine.put("==","equals");
        display2Engine.put("!=","notEquals");
        display2Engine.put("<","lt");
        display2Engine.put("<=","lte");
        display2Engine.put("isdivisible","isDivisible");
        display2Engine.put("isinteger","isInteger");
        display2Engine.put("include","containString");
        display2Engine.put("prefix","startWith");
        display2Engine.put("exclude","notContainString");
        display2Engine.put("suffix","endWith");
        display2Engine.put("isnull","isEmpty");
        display2Engine.put("notnull","notEmpty");


    }

    public static String getEngineTypeFromDisplay(String display) {
        return display2Engine.get(display);
    }
}
