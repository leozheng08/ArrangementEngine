package cn.tongdun.kunpeng.api.engine.util;

import cn.tongdun.kunpeng.api.engine.dto.RuleParamDTO;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class RuleParamUtil {

    public static String getValue(List<RuleParamDTO> paramDTOS, String key) {
        if (CollectionUtils.isEmpty(paramDTOS)) {
            return null;
        }
        for (RuleParamDTO item : paramDTOS) {
            if (StringUtils.equals(item.getName(), key)) {
                return item.getValue();
            }
        }
        return null;
    }

    public static Integer getIntegerValue(List<RuleParamDTO> all, String key) {
        String value = getValue(all, key);
        return Integer.valueOf(value);
    }
}
