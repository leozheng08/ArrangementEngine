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
import cn.tongdun.kunpeng.client.data.RiskRequest;
import cn.tongdun.kunpeng.api.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.api.common.util.DateUtil;
import cn.tongdun.kunpeng.api.common.util.JsonUtil;
import cn.tongdun.kunpeng.api.common.util.KunpengStringUtils;
import cn.tongdun.kunpeng.share.json.JSON;
import cn.tongdun.tdframework.core.pipeline.Step;
import com.google.common.base.CaseFormat;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

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
            add("eventType");
            add("firstIndustryType");
            add("secondIndustryType");
            add("policy_uuid");
        }
    };

    private static final Set<String> normalFields = Sets.newHashSet("int", "double", "boolean", "string", "float", "integer", "long");


    /**
     * 骆峰转下划线的字段缓存
     */
    private Map<String, String> camel2underlineCache = new ConcurrentHashMap<>(1000);
    private Map<String, String> underline2camelCache = new ConcurrentHashMap<>(1000);

    @Autowired
    private PartnerCache partnerCache;

    @Autowired
    private PolicyCache policyCache;

    @Autowired
    private FieldDefinitionCache fieldDefinitionCache;

    @Override
    public boolean invoke(AbstractFraudContext context, IRiskResponse response, RiskRequest request) {

        Partner partner = partnerCache.get(context.getPartnerCode());

        /*************根据字段的定义，将请求参数设置到上下文中 start**********************/
        //系统字段
        Collection<FieldDefinition> systemFields = fieldDefinitionCache.getSystemField(context);
        //扩展字段
        Collection<FieldDefinition> extendFields = fieldDefinitionCache.getExtendField(context);

        setFraudContext(context, request, systemFields);
        setFraudContext(context, request, extendFields);
        /*************根据字段的定义，将请求参数设置到上下文中 end**********************/


        /*************决策引擎除字段以外其他参数的获取**********************/
        //service-type=creditcloud时决策接口直接返回详情
        String serviceType = request.getServiceType();
        if (StringUtils.isBlank(serviceType)) {
            serviceType = "professional";
            context.setServiceType(serviceType);
        }

        //判断是否测试数据
        context.setTestFlag(request.isTestFlag());

        // 设置合作方的行业信息
        if(partner != null) {
            context.set("firstIndustryType", partner.getIndustryType());
            context.set("secondIndustryType", partner.getSecondIndustryType());
        }

        //调用async,如果async=true，则没有800ms规则引擎超时
        context.setAsync(request.isAsync());

        //如果客户有传事件发生时间，为客户传的为准
        Object eventOccurTime = context.getFieldValues().get("eventOccurTime");
        if(eventOccurTime != null && eventOccurTime instanceof Date){
            context.setEventOccurTime((Date)eventOccurTime);
        }

        return true;
    }



    /**
     * 使用反射获取bean对象里的所有属性字段
     *
     * @param ctx
     * @param request
     * @param fields
     */
    public void setFraudContext(AbstractFraudContext ctx, RiskRequest request, Collection<FieldDefinition> fields) {
        if (ctx != null && request != null && fields != null) {
            for (FieldDefinition fieldDefinition : fields) {
                String fieldCode = fieldDefinition.getFieldCode();
                if (excludeFieldName.contains(fieldCode)) {
                    continue;
                }

                Object requestValue = request.get(fieldCode);
                if(requestValue == null) {
                    String underlineStr = camel2underline(fieldCode);
                    requestValue = request.get(underlineStr);
                }
                if (requestValue == null) {
                    continue;
                }
                putValueByFieldDefinition(ctx.getFieldValues(),fieldDefinition,requestValue);
            }
        }
    }


    public Map<String,Object> camelJson(Map<String,Object> o) {
        Map rst = new HashMap();
        for (String key : o.keySet()) {
            Object r = o.get(key);
            Object mr = getObject(r);
            rst.put(underline2camel(key), mr);
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



    /**
     * 将合作方请求的值，根据字段定义做转换后设置到上下文
     * @param fieldDefinition
     * @param requestValue
     * @return
     */
    private void putValueByFieldDefinition(Map<String, Object> fields,FieldDefinition fieldDefinition,Object requestValue){

        String dataType = fieldDefinition.getDataType();
        String fieldCode = fieldDefinition.getFieldCode();
        try {
            if(!FieldDataType.OBJECT.name().equals(dataType)){
                Object fieldValue = null;
                if (FieldDataType.STRING.name().equals(dataType)) {
                    fieldValue = requestValue;

                } else if (FieldDataType.INT.name().equals(dataType)) {
                    if(requestValue instanceof Number){
                        fieldValue = ((Number)requestValue).intValue();
                    } else {
                        if(StringUtils.isBlank(requestValue.toString())){
                           return;
                        }
                        fieldValue = Integer.valueOf(requestValue.toString());
                    }

                } else if (FieldDataType.DOUBLE.name().equals(dataType)) {
                    if(requestValue instanceof Number){
                        fieldValue = ((Number)requestValue).doubleValue();
                    } else {
                        if(StringUtils.isBlank(requestValue.toString())){
                            return;
                        }
                        fieldValue = Double.valueOf(requestValue.toString());
                    }

                } else if (FieldDataType.DATETIME.name().equals(dataType)) {
                    if((requestValue instanceof Date)) {
                        fieldValue = requestValue;
                    } else {
                        if(StringUtils.isBlank(requestValue.toString())){
                            return;
                        }
                        fieldValue = DateUtil.parseDateTime(requestValue.toString());
                    }

                } else if (FieldDataType.BOOLEAN.name().equals(dataType)) {
                    if((requestValue instanceof Boolean)) {
                        fieldValue = requestValue;
                    } else {
                        if(StringUtils.isBlank(requestValue.toString())){
                            return;
                        }
                        fieldValue = "true".equalsIgnoreCase(requestValue.toString()) ? true : false;
                    }

                } else if (FieldDataType.ARRAY.name().equals(dataType)) {
                    if(requestValue instanceof List){
                        fieldValue = requestValue;
                    } else if(requestValue instanceof Object[]){
                        fieldValue = Arrays.asList((Object[])requestValue);
                    } else {
                        if(StringUtils.isBlank(requestValue.toString())){
                            return;
                        }
                        fieldValue = Arrays.asList(requestValue.toString().replaceAll("，", ",").split(","));
                    }

                }

                //根据所属类型（idNumber:身份证 mobile:手机号 email:邮箱）对值做处理
                fieldValue = getValueByPropertyType(fieldDefinition,fieldValue);

                if(fieldValue == null) {
                    return;
                }

                fields.put(fieldCode, fieldValue);
            } else  {
                //对象类型
                try {
                    if(StringUtils.isBlank(requestValue.toString())){
                        return;
                    }

                    // 把系统对象的名称加进来，整体组装成大JSON
                    Map fieldInfo = new HashMap();
                    Map<String,Object> fieldValueJson = JSON.parseObject(requestValue.toString(),HashMap.class);
                    fieldInfo.put(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, fieldCode), fieldValueJson);
                    // 拍平入参JSON
                    Map<String, Object> flattenedJsonInfo = JsonUtil.getFlattenedInfo(JSON.toJSONString(fieldInfo));
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




    /**
     * 把驼峰命名的字符转成下划线的方式，比如ipAddress->ip_address
     *
     * @param str
     */
    private String camel2underline(String str) {
        if(str == null){
            return null;
        }
        String result = camel2underlineCache.get(str);
        if(result != null){
            return result;
        }

        result = KunpengStringUtils.camel2underline(str);
        if(result != null){
            camel2underlineCache.put(str,result);
        }
        return result;
    }

    private String underline2camel(String str) {
        if(str == null){
            return null;
        }

        String result = underline2camelCache.get(str);
        if(result != null){
            return result;
        }

        result = KunpengStringUtils.underline2camel(str);
        if(result != null){
            underline2camelCache.put(str,result);
        }
        return result;
    }
}
