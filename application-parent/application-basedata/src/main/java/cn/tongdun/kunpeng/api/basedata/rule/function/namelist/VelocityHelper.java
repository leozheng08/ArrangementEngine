package cn.tongdun.kunpeng.api.basedata.rule.function.namelist;

import cn.fraudmetrix.module.riskbase.geoip.GeoipEntity;
import cn.tongdun.kunpeng.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.common.data.IFieldDefinition;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by lvyadong on 2020/01/17.
 */
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
                dimValue = formatDateTime((Date) tmpValue);
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
                dimValue = formatDateTime(occurTime);
                break;
            case "location":
            case "city":
                GeoipEntity geoipEntity = context.getExternalObj("geoipEntity", GeoipEntity.class);
                if (null != geoipEntity) {
                    dimValue = geoipEntity.getCity();
                }
                break;
            case "browser":
                if (null != context.getDeviceInfo()) {
                    dimValue = context.getDeviceInfo().get("browser");
                }
                break;
            default:// CUSTOM:
                if(isIdNumber(context,dimType)){//为身份证字段,转为小字
                    Object tempIdNumber = context.get(dimType);
                    if (null != tempIdNumber) {
                        if (tempIdNumber instanceof List) {
                            List<?> list = (List<?>) tempIdNumber;
                            CollectionUtils.transform(list, input -> input.toString().toUpperCase());
                        } else {
                            tempIdNumber = tempIdNumber.toString().toUpperCase();
                        }
                        dimValue = tempIdNumber;
                        break;
                    }
                }
                dimValue = context.get(dimType);
                break;
        }
        return dimValue;
    }


    //判断是否为身份证字段
    private static boolean isIdNumber(AbstractFraudContext context, String dimType){
        List<IFieldDefinition> fieldDefinitions =  context.getFieldDefinitions();
        if(fieldDefinitions == null){
            return false;
        }

        for(IFieldDefinition fieldDefinition:fieldDefinitions){
            if("idNumber".equals(fieldDefinition.getProperty())) {
                return true;
            }
        }
        return false;
    }


    private static String formatDateTime(final Date date) {
        return createDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
    }


    private static FastDateFormat createDateFormat(String pattern) {
        return FastDateFormat.getInstance(pattern);
    }
}


