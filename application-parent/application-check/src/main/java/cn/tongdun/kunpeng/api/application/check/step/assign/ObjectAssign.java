package cn.tongdun.kunpeng.api.application.check.step.assign;

import cn.tongdun.kunpeng.api.application.check.step.AssignFieldValueStep;
import cn.tongdun.kunpeng.api.application.check.step.CamelAndUnderlineConvertUtil;
import cn.tongdun.kunpeng.common.data.IFieldDefinition;
import cn.tongdun.kunpeng.common.util.JsonUtil;
import cn.tongdun.kunpeng.share.json.JSON;
import com.google.common.base.CaseFormat;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Author: liuq
 * @Date: 2020/4/8 6:16 PM
 */
public class ObjectAssign implements Assign{

    private Logger logger = LoggerFactory.getLogger(ObjectAssign.class);

    private static final Set<String> normalFields = Sets.newHashSet("int", "double", "boolean", "string", "float", "integer", "long");


    @Override
    public void execute(Map<String, Object> fields, IFieldDefinition fieldDefinition, Object requestValue) {
        try {
            if(StringUtils.isBlank(requestValue.toString())){
                return;
            }

            // 把系统对象的名称加进来，整体组装成大JSON
            Map fieldInfo = new HashMap();
            Map<String,Object> fieldValueJson = JSON.parseObject(requestValue.toString(),HashMap.class);
            fieldInfo.put(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, fieldDefinition.getFieldCode()), fieldValueJson);
            // 拍平入参JSON
            Map<String, Object> flattenedJsonInfo = JsonUtil.getFlattenedInfo(JSON.toJSONString(fieldInfo));
            fields.putAll(flattenedJsonInfo);
            // 原始JSON也保存一份
            fields.put(fieldDefinition.getFieldCode(), camelJson(fieldValueJson));
        } catch (Exception e) {
            logger.warn("复杂入参 Invalid JSON error:" + e.getMessage());
        }
    }

    public Map<String,Object> camelJson(Map<String,Object> o) {
        Map rst = new HashMap();
        for (String key : o.keySet()) {
            Object r = o.get(key);
            Object mr = getObject(r);
            rst.put(CamelAndUnderlineConvertUtil.underline2camel(key), mr);
        }
        return rst;
    }


    private Object getObject(Object r) {
        if (isNormalField(r)) {
            return r;
        }
        if (r instanceof Map) {
            Map n = camelJson((Map) r);
            return n;
        }
        if (r instanceof List) {
            List aa = (List) r;
            for (int i = 0; i < aa.size(); i++) {
                aa.set(i, getObject(aa.get(i)));
            }
            return aa;
        }
        return r;
    }

    private static boolean isNormalField(Object o) {
        if (o == null) {
            return false;
        }
        String a = o.getClass().getSimpleName();
        if (o.getClass().isArray()) {
            a = a.substring(0, a.length() - 2);
        }
        return normalFields.contains(a.toLowerCase());
    }
}
