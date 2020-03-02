package cn.tongdun.kunpeng.api.application.check.step;

import cn.tongdun.kunpeng.api.application.step.IRiskStep;
import cn.tongdun.kunpeng.api.application.step.Risk;
import cn.tongdun.kunpeng.api.engine.model.field.FieldDefinition;
import cn.tongdun.kunpeng.api.engine.model.field.FieldDefinitionCache;
import cn.tongdun.kunpeng.api.engine.model.field.FieldDataType;
import cn.tongdun.kunpeng.api.engine.model.partner.Partner;
import cn.tongdun.kunpeng.api.engine.model.partner.PartnerCache;
import cn.tongdun.kunpeng.api.engine.model.policy.PolicyCache;
import cn.tongdun.kunpeng.client.data.IRiskResponse;
import cn.tongdun.kunpeng.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.common.util.DateUtil;
import cn.tongdun.kunpeng.common.util.JsonUtil;
import cn.tongdun.kunpeng.common.util.KunpengStringUtils;
import cn.tongdun.tdframework.core.pipeline.Step;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.CaseFormat;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 设置上下文中的字段值
 * @Author: liang.chen
 * @Date: 2020/2/17 下午11:09
 */
@Component
@Step(pipeline = Risk.NAME, phase = Risk.CHECK, order = 1100)
public class AssignFieldValueStep implements IRiskStep {

    private Logger logger = LoggerFactory.getLogger(AssignFieldValueStep.class);

    private static final HashSet<String> excludeFieldName = new HashSet<String>() {

        private static final long serialVersionUID = 1L;

        {
            add("seq_id");
            add("sequenceId");
            add("appName");
            add("appType");
            add("eventType");
            add("firstIndustryType");
            add("secondIndustryType");
            add("policy_uuid");
        }
    };

    private static final Set<String> normalFields = Sets.newHashSet("int", "double", "boolean", "string", "float", "integer", "long");

    @Autowired
    private PartnerCache partnerCache;

    @Autowired
    private PolicyCache policyCache;

    @Autowired
    private FieldDefinitionCache fieldDefinitionCache;

    @Override
    public boolean invoke(AbstractFraudContext context, IRiskResponse response, Map<String, String> request) {

        Partner partner = partnerCache.get(context.getPartnerCode());

        /*************根据字段的定义，将请求参数设置到上下文中 start**********************/
        //系统字段
        List<FieldDefinition> systemFields = fieldDefinitionCache.getSystemField(context);
        //扩展字段
        List<FieldDefinition> extendFields = fieldDefinitionCache.getExtendField(context);

        setFraudContext(context, request, systemFields, extendFields);
        /*************根据字段的定义，将请求参数设置到上下文中 end**********************/


        /*************决策引擎除字段以外其他参数的获取**********************/
        //service-type=creditcloud时决策接口直接返回详情
        String serviceType = request.get("service-type");
        if (StringUtils.isBlank(serviceType)) {
            serviceType = "professional";
            context.setServiceType(serviceType);
        }


        //判断是否测试数据
        boolean testFlag = StringUtils.equalsIgnoreCase(request.get("test-flag"), "true");
        context.setTestFlag(testFlag);


        // 设置合作方的行业信息
        if(partner != null) {
            context.set("firstIndustryType", partner.getIndustryType());
            context.set("secondIndustryType", partner.getSecondIndustryType());
        }

        //调用async,如果async=true，则没有800ms规则引擎超时
        writeSyncInfoToContext(context, request);

        return true;
    }



    /**
     * 使用反射获取bean对象里的所有属性字段
     *
     * @param ctx
     * @param request
     * @param systemFields
     * @param extendFields
     */
    public void setFraudContext(AbstractFraudContext ctx, Map<String, String> request, List<FieldDefinition> systemFields,
                                       List<FieldDefinition> extendFields) {
        if (ctx != null && request != null && systemFields != null) {
            // set system fields
            for (FieldDefinition fieldDefinition : systemFields) {
                String fieldCode = fieldDefinition.getFieldCode();
                if (excludeFieldName.contains(fieldCode)) {
                    continue;
                }
                String underlineStr = KunpengStringUtils.camel2underline(fieldCode);
                String requestValue = request.get(underlineStr);
                if (StringUtils.isBlank(requestValue)) {
                    continue;
                }
                putValueByFieldDefinition(ctx.getCustomFields(),fieldDefinition,requestValue);
            }
        }

        if (ctx != null && request != null && extendFields != null) {
            // set extend fields
            for (FieldDefinition fieldDefinition : extendFields) {
                String requestValue = request.get(fieldDefinition.getFieldCode());
                if (StringUtils.isBlank(requestValue)) {
                    continue;
                }
                putValueByFieldDefinition(ctx.getCustomFields(),fieldDefinition,requestValue);
            }
        }
    }


    public static JSONObject camelJson(JSONObject o) {
        JSONObject rst = new JSONObject();
        for (String key : o.keySet()) {
            Object r = o.get(key);
            Object mr = getObject(r);
            rst.put(KunpengStringUtils.underline2camel(key), mr);
        }
        return rst;
    }

    private static Object getObject(Object r) {
        if (isNormalField(r)) {
            return r;
        }
        if (r instanceof JSONObject) {
            JSONObject n = camelJson((JSONObject) r);
            return n;
        }
        if (r instanceof JSONArray) {
            JSONArray aa = (JSONArray) r;
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


    /**
     * 异步调用信息写入到FraudContext,规则引擎使用
     * @param context
     * @param request
     */
    private void writeSyncInfoToContext(AbstractFraudContext context, Map<String, String> request){
        try {
            if (request.containsKey("async")) {
                context.setAsync(Boolean.parseBoolean(request.get("async")));
            }
        } catch (Exception e){
            logger.warn("writeSyncInfoToContext error",e);
        }
    }


    /**
     * 将合作方请求的值，根据字段定义做转换后设置到上下文
     * @param fieldDefinition
     * @param requestValue
     * @return
     */
    private void putValueByFieldDefinition(Map<String, Object> fields,FieldDefinition fieldDefinition,String requestValue){

        String dataType = fieldDefinition.getDataType();
        String fieldCode = fieldDefinition.getFieldCode();
        try {
            if(!FieldDataType.OBJECT.name().equals(dataType)){
                Object fieldValue = null;
                if (FieldDataType.STRING.name().equals(dataType)) {
                    fieldValue = requestValue;
                } else if (FieldDataType.INT.name().equals(dataType)) {
                    fieldValue = new Integer(requestValue);
                } else if (FieldDataType.DOUBLE.name().equals(dataType)) {
                    fieldValue = new Double(requestValue);
                } else if (FieldDataType.DATETIME.name().equals(dataType)) {
                    fieldValue = DateUtil.parseDateTime(requestValue);
                } else if (FieldDataType.BOOLEAN.name().equals(dataType)) {
                    fieldValue = "true".equalsIgnoreCase(requestValue) ? true : false;
                } else if (FieldDataType.ARRAY.name().equals(dataType)) {
                    fieldValue = Arrays.asList(requestValue.replaceAll("，", ",").split(","));
                }
                fieldValue = getValueByPropertyType(fieldDefinition,fieldValue);
                fields.put(fieldCode,fieldValue);
            } else  {
                //对象类型
                try {
                    // 把系统对象的名称加进来，整体组装成大JSON
                    JSONObject fieldInfo = new JSONObject();
                    JSONObject fieldValueJson = JSONObject.parseObject(requestValue);
                    fieldInfo.put(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, fieldCode), fieldValueJson);
                    // 拍平入参JSON
                    Map<String, Object> flattenedJsonInfo = JsonUtil.getFlattenedInfo(fieldInfo.toJSONString());
                    fields.putAll(flattenedJsonInfo);
                    // 原始JSON也保存一份
                    fields.put(fieldCode, camelJson(fieldValueJson));
                } catch (Exception e) {
                    logger.warn("复杂入参 Invalid JSON error:" + e.getMessage());
                }
            }
        } catch (Exception e) {
            logger.warn("决策引擎入参 Failed to set system field, fieldName:{},fieldValue:{},error:{}",
                    fieldCode, requestValue, e.getMessage());
        }
    }

    /**
     * 根据所属类型（idNumber:身份证 mobile:手机号 email:邮箱）对值做处理
     * @param fieldDefinition
     * @param fieldValue
     * @return
     */
    private Object getValueByPropertyType(FieldDefinition fieldDefinition,Object fieldValue){
        if(fieldValue == null){
            return fieldValue;
        }

        //所属类型idNumber:身份证 mobile:手机号 email:邮箱
        String property = fieldDefinition.getProperty();
        if(property == null){
            return fieldValue;
        }

        //身份证做小写处理
        if("idNumber".equals(property)){
            if (fieldValue instanceof List) {
                List<?> list = (List<?>) fieldValue;
                CollectionUtils.transform(list, input -> input.toString().toUpperCase());
            } else {
                fieldValue = fieldValue.toString().toUpperCase();
            }
        }

        return fieldValue;
    }

}
