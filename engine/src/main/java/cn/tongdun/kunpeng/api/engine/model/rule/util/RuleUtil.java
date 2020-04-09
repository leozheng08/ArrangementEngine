package cn.tongdun.kunpeng.api.engine.model.rule.util;

import cn.fraudmetrix.module.tdrule.spring.SpringContextHolder;
import cn.tongdun.kunpeng.api.engine.model.field.FieldDefinition;
import cn.tongdun.kunpeng.api.engine.model.field.FieldDefinitionCache;
import cn.tongdun.kunpeng.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.common.data.IFieldDefinition;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @Author: liang.chen
 * @Date: 2020/2/26 下午3:24
 */
public class RuleUtil {

    private static FieldDefinitionCache fieldDefinitionCache;

    private static FieldDefinitionCache getFieldDefinitionCache(){
        if(fieldDefinitionCache != null){
            return fieldDefinitionCache;
        }

        fieldDefinitionCache = SpringContextHolder.getBean("fieldDefinitionCache",FieldDefinitionCache.class);
        return fieldDefinitionCache;
    }

    /**
     * 根据字段的内部标识名字和事件类型获取其显示名字
     *
     * @param fieldCode 字段名
     * @param context
     * @return null fieldName 或者查询不到，否则返回查询到的显示名
     */
    public static String getDisplayName(String fieldCode, AbstractFraudContext context){
        if (StringUtils.isBlank(fieldCode)) {
            return fieldCode;
        }

        IFieldDefinition fieldDefinition=context.getFieldDefinition(fieldCode);
        if (null!=fieldDefinition){
            return fieldDefinition.getDisplayName();
        }
        return fieldCode;
    }





}
