package cn.tongdun.kunpeng.api.engine.model.rule.util;

import cn.tongdun.kunpeng.common.data.AbstractFraudContext;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class VelocityHelper {

    private static final Logger logger = LoggerFactory.getLogger(VelocityHelper.class);

    public static List<String> getDimensionValues(AbstractFraudContext context, final String dimType) {
        Object tmpValue = getDimensionValue(context, dimType);
        String dimValue = null;
        if (tmpValue != null) {
            if (tmpValue instanceof List) {
                List<?> ks = (List<?>) tmpValue;
                List<String> dims = new ArrayList<>(ks.size());
                for (Object l : ks) {
                    if (l == null || StringUtils.isBlank(l.toString())) {
                        continue;
                    }
                    String ms = l.toString();
                    if (!dims.contains(ms)) {
                        dims.add(ms);
                    }
                }
                return dims;
            } else if (tmpValue instanceof Date) {
                dimValue = DateUtil.formatDateTime((Date) tmpValue);
            } else {
                dimValue = tmpValue.toString();
            }
        }
        if (StringUtils.isNotBlank(dimValue)) {
            return Lists.newArrayList(dimValue);
        }
        return Lists.newArrayList();
    }

    /**
     * 获取主属性维度值
     */
    public static Object getDimensionValue(AbstractFraudContext context, String dimType) {
        Object dimValue = null;
        switch (dimType) {
            case "eventOccurTime":// 事件发生时间
                Date occurTime = context.getEventOccurTime();
                if (null == occurTime) {
                    occurTime = new Date();
                }
                dimValue = DateUtil.formatDateTime(occurTime);
                break;
            default:
                dimValue = context.get(dimType);
                break;
        }
        return dimValue;
    }

}


